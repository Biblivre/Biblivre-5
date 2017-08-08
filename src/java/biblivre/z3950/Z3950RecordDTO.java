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

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.core.AbstractDTO;

public class Z3950RecordDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;
	
	private BiblioRecordDTO record;
	private Z3950AddressDTO server;
	private transient int autogenId;
	
	public BiblioRecordDTO getRecord() {
		return this.record;
	}
	
	public void setRecord(BiblioRecordDTO record) {
		this.record = record;
	}
	
	public Z3950AddressDTO getServer() {
		return this.server;
	}
	
	public void setServer(Z3950AddressDTO server) {
		this.server = server;
	}
	
	public int getAutogenId() {
		return this.autogenId;
	}

	public void setAutogenId(int autogenId) {
		this.autogenId = autogenId;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("record", this.getRecord().toJSONObject());
			json.putOpt("server_name", this.getServer().getName());
			json.putOpt("id", this.getAutogenId());
		} catch (JSONException e) {
		}

		return json;
	}
	

}
