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

import org.json.JSONObject;

import biblivre.core.AbstractDTO;

public class BriefTabFieldFormatDTO extends AbstractDTO implements Comparable<BriefTabFieldFormatDTO> {
	private static final long serialVersionUID = 1L;

	private String datafieldTag;
	private String format;
	private Integer sortOrder;
	
	public BriefTabFieldFormatDTO() {
	}
	
	public BriefTabFieldFormatDTO(JSONObject jsonObject) {
		this.fromJSONObject(jsonObject);
	}
	
	public BriefTabFieldFormatDTO(String datafieldTag, String format) {
		this.setDatafieldTag(datafieldTag);
		this.setFormat(format);
	}
	
	public String getDatafieldTag() {
		return this.datafieldTag;
	}
	
	public void setDatafieldTag(String datafieldTag) {
		this.datafieldTag = datafieldTag;
	}

	public String getFormat() {
		return this.format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();
		
		json.put("datafieldTag", this.getDatafieldTag());
		
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
