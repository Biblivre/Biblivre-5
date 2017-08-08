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
package biblivre.cataloging;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.enums.AutocompleteType;
import biblivre.core.AbstractDTO;

public class FormTabSubfieldDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;
	
	private String datafield;
	private String subfield;
	private boolean collapsed;
	private boolean repeatable;
	private AutocompleteType autocompleteType;
	private Integer sortOrder;

	public FormTabSubfieldDTO(JSONObject jsonObject) {
		super();
		this.fromJSONObject(jsonObject);
		this.setAutocompleteType(jsonObject.getString("autocomplete_type"));
	}
	
	public FormTabSubfieldDTO() {
		super();
	}
	
	public String getDatafield() {
		return this.datafield;
	}

	public void setDatafield(String datafield) {
		this.datafield = datafield;
	}

	public String getSubfield() {
		return this.subfield;
	}

	public void setSubfield(String subfield) {
		this.subfield = subfield;
	}

	public boolean isCollapsed() {
		return this.collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public boolean isRepeatable() {
		return this.repeatable;
	}

	public void setRepeatable(boolean repeatable) {
		this.repeatable = repeatable;
	}
	
	public AutocompleteType getAutocompleteType() {
		if (this.autocompleteType == null) {
			return AutocompleteType.DISABLED;
		}
		
		return this.autocompleteType;
	}

	public void setAutocompleteType(AutocompleteType autocompleteType) {
		this.autocompleteType = autocompleteType;
	}

	public void setAutocompleteType(String autocompleteType) {
		this.autocompleteType = AutocompleteType.fromString(autocompleteType);
	}
	
	public Integer getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("datafield", this.getDatafield());
			json.putOpt("subfield", this.getSubfield());
			json.putOpt("collapsed", this.isCollapsed());
			json.putOpt("repeatable", this.isRepeatable());
			json.putOpt("autocomplete_type", this.getAutocompleteType());
			json.putOpt("sortOrder", this.getSortOrder());
		} catch (JSONException e) {
		}

		return json;
	}
}
