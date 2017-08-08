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

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.RecordDTO;

public class AuthorityRecordDTO extends RecordDTO {
	private static final long serialVersionUID = 1L;

	private String authorName;
	private String authorOtherName;
	private String authorType;
	
	public String getAuthorName() {
		return this.authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorType() {
		return this.authorType;
	}

	public void setAuthorType(String authorType) {
		this.authorType = authorType;
	}

	public String getAuthorOtherName() {
		return this.authorOtherName;
	}

	public void setAuthorOtherName(String authorOtherName) {
		this.authorOtherName = authorOtherName;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();

		try {
			json.putOpt("material_type", this.getMaterialType());
			json.putOpt("name", this.getAuthorName());
			json.putOpt("other_name", this.getAuthorOtherName());
			json.putOpt("author_type", this.getAuthorType());
		} catch (JSONException e) {
		}

		return json;
	}
}
