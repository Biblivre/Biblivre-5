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

import biblivre.core.AbstractDTO;

public class BriefTabFieldDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private String datafieldTag;
	private String value;

	public BriefTabFieldDTO(String datafieldTag, String value) {
		this.setDatafieldTag(datafieldTag);
		this.setValue(value);
	}

	public String getDatafieldTag() {
		return this.datafieldTag;
	}

	public void setDatafieldTag(String datafieldTag) {
		this.datafieldTag = datafieldTag;
	}
	
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("datafield", this.getDatafieldTag());
			json.putOpt("value", this.getValue());
		} catch (JSONException e) {
		}

		return json;
	}
}
