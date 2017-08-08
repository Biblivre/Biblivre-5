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
package biblivre.circulation.lending;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;

public class LendingInfoDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;

	private BiblioRecordDTO biblio;
	private HoldingDTO holding;
	private UserDTO user;
	private LendingDTO lending;
	private LendingFineDTO lendingFine;

	public LendingDTO getLending() {
		return this.lending;
	}

	public void setLending(LendingDTO lending) {
		this.lending = lending;
	}

	public LendingFineDTO getLendingFine() {
		return this.lendingFine;
	}

	public void setLendingFine(LendingFineDTO lendingFine) {
		this.lendingFine = lendingFine;
	}

	public UserDTO getUser() {
		return this.user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public HoldingDTO getHolding() {
		return this.holding;
	}

	public void setHolding(HoldingDTO holding) {
		this.holding = holding;
	}

	public BiblioRecordDTO getBiblio() {
		return this.biblio;
	}

	public void setBiblio(BiblioRecordDTO biblio) {
		this.biblio = biblio;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();
		
		try {
			json.put("id", this.getHolding().getId());
		} catch (JSONException e) { }
		
		return json;
	}
}
