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
package biblivre.circulation.reservation;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;

public class ReservationInfoDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;

	private BiblioRecordDTO biblio;
	private ReservationDTO reservation;
	private UserDTO user;
	
	public UserDTO getUser() {
		return this.user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public ReservationDTO getReservation() {
		return this.reservation;
	}

	public void setReservation(ReservationDTO reservation) {
		this.reservation = reservation;
	}

	public BiblioRecordDTO getBiblio() {
		return this.biblio;
	}

	public void setBiblio(BiblioRecordDTO biblio) {
		this.biblio = biblio;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
