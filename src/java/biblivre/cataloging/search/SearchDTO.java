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
package biblivre.cataloging.search;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.DTOCollection;
import biblivre.core.enums.SearchMode;

public class SearchDTO extends DTOCollection<RecordDTO> {
	private static final long serialVersionUID = 1L;

	private RecordType recordType;

	private SearchQueryDTO query;

	private transient Integer sort;
	private transient Integer indexingGroup;
	private transient Map<Integer, Integer> indexingGroupCount;

	public SearchDTO(RecordType recordType) {
		super();

		this.setRecordType(recordType);
	}

	public SearchMode getSearchMode() {
		if (this.getQuery() == null) {
			return SearchMode.LIST_ALL;
		}

		return this.getQuery().getSearchMode();
	}

	public RecordType getRecordType() {
		return this.recordType;
	}

	public void setRecordType(RecordType recordType) {
		this.recordType = recordType;
	}

	public SearchQueryDTO getQuery() {
		return this.query;
	}

	public void setQuery(SearchQueryDTO query) {
		this.query = query;
	}

	public Integer getIndexingGroup() {
		if (this.indexingGroup == null) {
			// This search is not over a datafield group.
			// Using 0 to represent no filtering
			return 0;
		}

		return this.indexingGroup;
	}

	public void setIndexingGroup(Integer indexingGroup) {
		this.indexingGroup = indexingGroup;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getParameters() {
		if (this.getQuery() == null) {
			return "";
		}
		
		return this.getQuery().getParameters();
	}
	
	public Map<Integer, Integer> getIndexingGroupCount() {
		return this.indexingGroupCount;
	}

	public void setIndexingGroupCount(Map<Integer, Integer> indexingGroupCount) {
		this.indexingGroupCount = indexingGroupCount;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();

		try {
			json.putOpt("record_type", this.getRecordType());
			
			JSONArray groupCount = new JSONArray();
			
			for (Integer key : this.getIndexingGroupCount().keySet()) {
				JSONObject group = new JSONObject();
				group.put("group_id", key);
				group.put("result_count", this.getIndexingGroupCount().get(key));
				
				groupCount.put(group);
			}

			json.putOpt("indexing_group_count", groupCount);
		} catch (JSONException e) {
		}

		return json;
	}
}
