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
package biblivre.cataloging.vocabulary;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.RecordDTO;

public class VocabularyRecordDTO extends RecordDTO {
	private static final long serialVersionUID = 1L;

	//transient fields
	private transient String termTE;
	private transient String termUP;
	private transient String termTG;
	private transient String termVTTA;
	
	public String getTermTE() {
		return this.termTE;
	}

	public void setTermTE(String termTE) {
		this.termTE = termTE;
	}

	public String getTermUP() {
		return this.termUP;
	}

	public void setTermUP(String termUP) {
		this.termUP = termUP;
	}

	public String getTermTG() {
		return this.termTG;
	}

	public void setTermTG(String termTG) {
		this.termTG = termTG;
	}

	public String getTermVTTA() {
		return this.termVTTA;
	}

	public void setTermVTTA(String termVTTA) {
		this.termVTTA = termVTTA;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();

		try {
			json.putOpt("material_type", this.getMaterialType());
			json.putOpt("term_te", this.getTermTE());
			json.putOpt("term_up", this.getTermUP());
			json.putOpt("term_tg", this.getTermTG());
			json.putOpt("term_vt_ta", this.getTermVTTA());
		} catch (JSONException e) {
		}

		return json;
	}
}
