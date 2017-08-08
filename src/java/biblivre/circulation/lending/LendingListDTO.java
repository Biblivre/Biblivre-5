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

import java.util.List;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;

public class LendingListDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;

	private Integer id;
	private UserDTO user;
	private HoldingDTO holding;
	private BiblioRecordDTO biblio;
	private List<LendingInfoDTO> lendingInfo;
	private List<Integer> reservedRecords;

	public List<Integer> getReservedRecords() {
		return reservedRecords;
	}

	public void setReservedRecords(List<Integer> reservedRecords) {
		this.reservedRecords = reservedRecords;
	}

	public UserDTO getUser() {
		return this.user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public List<LendingInfoDTO> getLendingInfo() {
		return this.lendingInfo;
	}

	public void setLendingInfo(List<LendingInfoDTO> lendingInfo) {
		this.lendingInfo = lendingInfo;
	}

	/*
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();
		
		try {
			json.put("id", this.getUser().getId());
		} catch (JSONException e) { }
		
		return json;
	}*/
	
}
