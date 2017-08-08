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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.SearchOperator;
import biblivre.core.AbstractDTO;
import biblivre.core.enums.SearchMode;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MaterialType;

public class SearchQueryDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private SearchMode searchMode;
	private RecordDatabase database;
	private MaterialType materialType;
	private List<SearchTermDTO> terms;
	private String parameters;
	private Boolean holdingSearch;
	private Boolean reservedOnly;

	public SearchQueryDTO(String jsonString) throws ValidationException {
		this.parameters = jsonString;
		this.terms = new ArrayList<SearchTermDTO>();
		
		try {
			this.fromJson(jsonString);
		} catch (Exception e) {
			throw new ValidationException("cataloging.error.invalid_search_parameters");			
		}

		if (this.getDatabase() == null) {
			throw new ValidationException("cataloging.error.invalid_database");
		}
	}
	
	public SearchQueryDTO(RecordDatabase database) {
		this.setDatabase(database);
		
		if (this.getDatabase() == null) {
			throw new ValidationException("cataloging.error.invalid_database");
		}
	}

	private void fromJson(String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);

		this.setSearchMode(SearchMode.fromString(json.optString("search_mode")));
		this.setDatabase(RecordDatabase.fromString(json.optString("database")));
		this.setMaterialType(MaterialType.fromString(json.optString("material_type")));
		this.setHoldingSearch(json.optBoolean("holding_search"));
		this.setReservedOnly(json.optBoolean("reserved_only"));

		JSONArray searchTerms = json.optJSONArray("search_terms");

		if (searchTerms == null || searchTerms.length() == 0) {
			// If no search terms were specified, force a "list all" search
			this.setSearchMode(SearchMode.LIST_ALL);
			return;
		}

		for (int i = 0, imax = searchTerms.length(); i < imax ; i++) {
			JSONObject searchTerm = searchTerms.optJSONObject(i);

			if (searchTerm == null) {
				continue;
			}
			
			String query = searchTerm.optString("query");
			String field = searchTerm.optString("field");
			String operator = searchTerm.optString("operator");
			String startDate = searchTerm.optString("start_date");
			String endDate = searchTerm.optString("end_date");

			String f = StringUtils.defaultString(field);
			if (f.equals("created") || f.equals("modified") || f.equals("holding_created") || f.equals("holding_modified")) {
				query = "date";
			}
			
			String sanitizedQuery = TextUtils.preparePhrase(query);

			Pattern pattern = Pattern.compile("\\s*(\"[^\"]+\"|[^\\s\"]+)");
			Matcher matcher = pattern.matcher(sanitizedQuery);

			Set<String> validTerms = new HashSet<String>();
			while (matcher.find()) {
				String group = matcher.group(1);
				String[] terms;
				
				if (group.charAt(0) == '"' && group.indexOf(' ') != -1) {
					//Multiple terms grouped by quotes
					terms = new String[]{group};
				} else {
					//Single term
					if (f.equals("holding_accession_number")) {
						terms = new String[]{group};
					} else {
						terms = TextUtils.prepareWords(group);
					}
				}

				for (String term : terms) {
					if ((term.length() > 1) || NumberUtils.isDigits(term)) {
						validTerms.add(term);
					}
				}
			}

			if (validTerms.size() == 0) {
				continue;
			}

			SearchTermDTO dto = new SearchTermDTO();
			dto.setField(field);
			if (i != 0) {
				dto.setOperator(SearchOperator.fromString(operator));
			}
			dto.setStartDate(startDate);
			dto.setEndDate(endDate);
			dto.addTerms(validTerms);

			this.addTerm(dto);
		}

		if (this.getTerms().size() == 0) {
			throw new ValidationException("cataloging.error.no_valid_terms");
		}
	}
	
	public String getParameters() {
		return this.parameters;
	}

	public SearchMode getSearchMode() {
		return this.searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}

	public RecordDatabase getDatabase() {
		return this.database;
	}

	public void setDatabase(RecordDatabase database) {
		this.database = database;
	}

	public MaterialType getMaterialType() {
		if (this.materialType == null) {
			return MaterialType.ALL;
		}

		return this.materialType;
	}

	public void setMaterialType(MaterialType materialType) {
		this.materialType = materialType;
	}

	public Boolean isHoldingSearch() {
		return this.holdingSearch != null ? this.holdingSearch : false;
	}

	public void setHoldingSearch(Boolean holdingSearch) {
		if (holdingSearch == null) {
			holdingSearch = Boolean.FALSE;
		}

		this.holdingSearch = holdingSearch;
	}
	
	public Boolean isReservedOnly() {
		return this.reservedOnly != null ? this.reservedOnly : false;
	}

	public void setReservedOnly(Boolean reservedOnly) {
		if (reservedOnly == null) {
			reservedOnly = Boolean.FALSE;
		}
		
		this.reservedOnly = reservedOnly;
	}

	public List<SearchTermDTO> getTerms() {
		return this.terms;
	}
	
	public Set<String> getSimpleTerms() {
		if (this.terms == null || this.terms.size() == 0) {
			return new HashSet<String>();
		}
		
		return this.terms.get(0).getTerms();
	}
	
	public void addTerm(SearchTermDTO dto) {
		if (dto == null) {
			return;
		}

		this.terms.add(dto);
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("search_mode", this.getSearchMode());
			json.putOpt("database", this.getDatabase());
			json.putOpt("material_type", this.getMaterialType());	
			json.putOpt("holding_search", this.isHoldingSearch());	
			json.putOpt("terms", this.getTerms());
		} catch (JSONException e) {
		}

		return json;
	}
}
