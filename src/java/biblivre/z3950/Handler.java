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
package biblivre.z3950;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j.marc.Record;

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.Fields;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.PagingDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;

public class Handler extends AbstractHandler {

	public void search(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");

		String servers = null;
		String attribute = null;
		String query = null;
		
		try {
			JSONObject json = new JSONObject(searchParameters);
			servers = json.optString("server");
			attribute = json.optString("attribute");
			query = json.optString("query");
		} catch (JSONException je) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}
		
		Z3950BO bo = Z3950BO.getInstance(schema);
		
		String[] serverIds = servers.split(",");
		List<Integer> ids = new LinkedList<Integer>();
		for (String serverId : serverIds) {
			try {
				ids.add(Integer.parseInt(serverId.trim()));
			} catch (Exception e) {
			}
		}

		if (ids.isEmpty()) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}
		
		List<Z3950AddressDTO> serverList = bo.list(ids);
		Pair<String, String> search = new Pair<String, String>(attribute, query);
		List<Z3950RecordDTO> results = bo.search(serverList, search);
		
		if (results.isEmpty()) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}
		
		Integer searchId = (Integer) request.getSessionAttribute(schema, "z3950_search.last_id");
		if (searchId == null) {
			searchId = 1;
		} else {
			searchId++;
		}
		
		request.setSessionAttribute(schema, "z3950_search." + searchId, results);
		request.setSessionAttribute(schema, "z3950_search.last_id", searchId);
		
		DTOCollection<Z3950RecordDTO> collection = this.paginateResults(schema, results, 1);
		collection.setId(searchId);
		
		try {
			this.json.putOpt("search", collection.toJSONObject());
		} catch(JSONException e) { }
	}
	
	@SuppressWarnings("unchecked")
	public void paginate(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		String searchId = request.getString("search_id");
		if (StringUtils.isBlank(searchId)) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}
		Integer page = request.getInteger("page", 1);
		
		String uuid = "z3950_search." + searchId;
		List<Z3950RecordDTO> results = (List<Z3950RecordDTO>)request.getSessionAttribute(schema, uuid);
		if (results == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}
		DTOCollection<Z3950RecordDTO> collection = this.paginateResults(schema, results, page);
		try {
			this.json.putOpt("search", collection.toJSONObject());
		} catch(JSONException e) { 
		}
	}
	
	@SuppressWarnings("unchecked")
	public void open(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer index = request.getInteger("id");
		String searchId = request.getString("search_id");
		if (StringUtils.isBlank(searchId)) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}		
		String uuid = "z3950_search." + searchId;
		List<Z3950RecordDTO> results = (List<Z3950RecordDTO>)request.getSessionAttribute(schema, uuid);
		if (results == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}
		Z3950RecordDTO dto = results.get(index);
		
		RecordBO bo = RecordBO.getInstance(schema, RecordType.BIBLIO);
		
		if (dto == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}
		
		RecordDTO recordDTO = dto.getRecord();
		Record record = recordDTO.getRecord();
		MarcDataReader marcDataReader = new MarcDataReader(record);
		
		bo.populateDetails(recordDTO, RecordBO.MARC_INFO);
		recordDTO.setId(index);
		recordDTO.setMaterialType(MaterialType.fromRecord(record));

		// Record tab
		List<BriefTabFieldFormatDTO> formats = Fields.getBriefFormats(schema, RecordType.BIBLIO);
		List<BriefTabFieldDTO> fields = marcDataReader.getFieldList(formats);

		recordDTO.setFields(fields);

		// Marc tab
		recordDTO.setMarc(MarcUtils.recordToMarc(record));

		// Form tab
		recordDTO.setJson(MarcUtils.recordToJson(record));
	
		// Attachments
		recordDTO.setAttachments(marcDataReader.getAttachments());
		
		try {
			this.json.put("data", recordDTO.toJSONObject());
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
		
	}
	
	private DTOCollection<Z3950RecordDTO> paginateResults(String schema, List<Z3950RecordDTO> results, int page) {
		Integer recordsPerPage = Configurations.getPositiveInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20);
		Integer start = (page - 1) * recordsPerPage;
		PagingDTO paging = new PagingDTO(results.size(), recordsPerPage, start);
		DTOCollection<Z3950RecordDTO> collection = new DTOCollection<Z3950RecordDTO>();

		List<Z3950RecordDTO> sublist = results.subList(start, Math.min(start + recordsPerPage, results.size()));
		BiblioRecordBO brbo = BiblioRecordBO.getInstance(schema);
		
		int autoGeneratedId = start;
		for (Z3950RecordDTO z3950 : sublist) {
			z3950.setAutogenId(autoGeneratedId++);

			brbo.populateDetails(z3950.getRecord(), RecordBO.MARC_INFO);
		}
		
		collection.addAll(sublist);
		collection.setPaging(paging);
		return collection;
	}
	
}
