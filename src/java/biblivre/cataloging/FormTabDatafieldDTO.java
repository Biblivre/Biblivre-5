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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;

public class FormTabDatafieldDTO extends AbstractDTO implements Comparable<BriefTabFieldFormatDTO> {

	private static final long serialVersionUID = 1L;

	private String datafield;
	private boolean collapsed;
	private boolean repeatable;
	private String indicator1;
	private String indicator2;
	private String materialType;
	private List<FormTabSubfieldDTO> subfields;
	private Integer sortOrder;

	
	public FormTabDatafieldDTO(JSONObject jsonObject) {
		this.fromJSONObject(jsonObject);
		this.subfields = new LinkedList<FormTabSubfieldDTO>();
		
		if (jsonObject.has("subfields")) {
			JSONArray array = jsonObject.getJSONArray("subfields");
			for (int i = 0; i < array.length(); i++) {
				JSONObject subfield = array.getJSONObject(i);
				
				this.subfields.add(new FormTabSubfieldDTO(subfield));
			}
		}
	}

	public FormTabDatafieldDTO() {
		this.subfields = new LinkedList<FormTabSubfieldDTO>();
	}
	
	public String getDatafield() {
		return this.datafield;
	}

	public void setDatafield(String datafield) {
		this.datafield = datafield;
	}

	public List<FormTabSubfieldDTO> getSubfields() {
		return this.subfields;
	}

	public void addSubfield(FormTabSubfieldDTO subfield) {
		this.subfields.add(subfield);
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

	public String getIndicator1() {
		return this.indicator1;
	}

	public String[] getIndicator1Values() {
		return StringUtils.split(this.getIndicator1(), ",");
	}
	
	public void setIndicator1(String indicator1) {
		this.indicator1 = indicator1;
	}

	public String getIndicator2() {
		return this.indicator2;
	}

	public String[] getIndicator2Values() {
		return StringUtils.split(this.getIndicator2(), ",");
	}

	public void setIndicator2(String indicator2) {
		this.indicator2 = indicator2;
	}

	public String getMaterialType() {
		return this.materialType;
	}

	public String[] getMaterialTypeValues() {
		return StringUtils.split(this.getMaterialType(), ",");
	}
	
	public void setMaterialType(String materialType) {
		this.materialType = materialType;
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
			json.putOpt("collapsed", this.isCollapsed());
			json.putOpt("repeatable", this.isRepeatable());
			json.putOpt("material_type", this.getMaterialType());
			json.putOpt("sortOrder", this.getSortOrder());
			
			if (StringUtils.isNotBlank(this.getIndicator1())) {
				json.putOpt("indicator1", new JSONArray(this.getIndicator1Values()));
			}

			if (StringUtils.isNotBlank(this.getIndicator2())) {
				json.putOpt("indicator2", new JSONArray(this.getIndicator2Values()));
			}

			if (StringUtils.isNotBlank(this.getMaterialType())) {
				json.putOpt("material_type", new JSONArray(this.getMaterialTypeValues()));
			}

			for (FormTabSubfieldDTO subfield : this.getSubfields()) {
				json.append("subfields", subfield.toJSONObject());
			}
		} catch (JSONException e) {
		}

		return json;
	}
	
	@Override
	public int compareTo(BriefTabFieldFormatDTO other) {
		if (other == null) {
			return -1;
		}

		if (this.getSortOrder() != null && other.getSortOrder() != null) {
			return this.getSortOrder().compareTo(other.getSortOrder());
		}

		return 0;
	}
}
