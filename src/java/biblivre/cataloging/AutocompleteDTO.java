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

public class AutocompleteDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer recordId;
	private String datafield;
	private String subfield;
	private String word;
	private String phrase;
	//private AuthorityRecordDTO authority;
	//private VocabularyRecordDTO vocabulary;

	private transient RecordDTO record;
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRecordId() {
		return this.recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
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

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPhrase() {
		return this.phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	
	public RecordDTO getRecord() {
		return this.record;
	}

	public void setRecord(RecordDTO record) {
		this.record = record;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("id", this.getId());
			json.putOpt("record_id", this.getRecordId());
			json.putOpt("datafield", this.getDatafield());
			json.putOpt("subfield", this.getSubfield());
			json.putOpt("word", this.getWord());
			json.putOpt("phrase", this.getPhrase());
			
			if (this.getRecord() != null) {
				json.putOpt("record", this.getRecord().toJSONObject());
			}
		} catch (JSONException e) {
		}
		
		return json;
	}
}
