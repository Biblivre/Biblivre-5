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
package biblivre.administration.usertype;

import biblivre.core.AbstractDTO;

public class UserTypeDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String description;
	private Integer lendingLimit;
	private Integer reservationLimit;
	private Integer lendingTimeLimit;
	private Integer reservationTimeLimit;
	private Float fineValue;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getLendingLimit() {
		return this.lendingLimit;
	}

	public void setLendingLimit(Integer lendingLimit) {
		this.lendingLimit = lendingLimit;
	}

	public Integer getReservationLimit() {
		return this.reservationLimit;
	}

	public void setReservationLimit(Integer reservationLimit) {
		this.reservationLimit = reservationLimit;
	}

	public Integer getLendingTimeLimit() {
		return this.lendingTimeLimit;
	}

	public void setLendingTimeLimit(Integer lendingTimeLimit) {
		this.lendingTimeLimit = lendingTimeLimit;
	}

	public Integer getReservationTimeLimit() {
		return this.reservationTimeLimit;
	}

	public void setReservationTimeLimit(Integer reservationTimeLimit) {
		this.reservationTimeLimit = reservationTimeLimit;
	}

	public Float getFineValue() {
		return this.fineValue;
	}

	public void setFineValue(Float fineValue) {
		this.fineValue = fineValue;
	}
	
}
