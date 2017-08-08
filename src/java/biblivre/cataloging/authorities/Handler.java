/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.cataloging.authorities;

import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.administration.reports.ReportsBO;
import biblivre.cataloging.CatalogingHandler;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.marc.MaterialType;

public class Handler extends CatalogingHandler {
	
	public Handler() {
		super(RecordType.AUTHORITIES, MaterialType.AUTHORITIES);
	}
	
	@Override
	protected RecordDTO createRecordDTO(ExtendedRequest request) {
		AuthorityRecordDTO dto = new AuthorityRecordDTO();
		return dto;
	}
	
	@Override
	protected void beforeSave(ExtendedRequest request, RecordDTO dto) {
		((AuthorityRecordDTO)dto).setAuthorType(request.getString("author_type"));
	}
	
	@Override
	protected void afterConvert(ExtendedRequest request, RecordDTO dto) {
		((AuthorityRecordDTO)dto).setAuthorType(request.getString("author_type"));
	}
	
	public void searchAuthor(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");
		String query = "";
		RecordDatabase db = RecordDatabase.MAIN;
		
		try {
			JSONObject json = new JSONObject(searchParameters);
			db = RecordDatabase.fromString(json.optString("database"));
			JSONArray searchTerms = json.optJSONArray("search_terms");
			if (searchTerms != null) {
				JSONObject searchTerm = searchTerms.optJSONObject(0);
				query = searchTerm.optString("query");
			}
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, e.getMessage());
			return;
		}

		ReportsBO bo = ReportsBO.getInstance(schema);
		//Removed pagination (limit and offset) from method to fix a bug in the Reports/Report By Author functionality.
		TreeMap<String, Set<Integer>> result = bo.searchAuthors(query, db);

		if (result.size() == 0) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}

		try {
			JSONArray data = new JSONArray();
			int id = 1;
			for (String key : result.keySet()) {
				Set<Integer> ids = result.get(key);
				JSONObject obj = new JSONObject();
				obj.put("id", id++);
				obj.put("author", key);
				obj.put("count", ids.size());
				obj.put("ids", StringUtils.join(ids, ","));
				data.put(obj);
			}
			JSONObject searchResult = new JSONObject();
			searchResult.put("data", data);
			this.json.put("search", searchResult);

		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
//	@Override
//	public void paginate(ExtendedRequest request, ExtendedResponse response) {
//		this.search(request, response);
//	}


}
