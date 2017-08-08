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

import java.util.Date;

import biblivre.core.AbstractDTO;

public class LendingDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer holdingId;
	private Integer userId;
	private Integer previousLendingId;
	private Date expectedReturnDate;
	private Date returnDate;
	
	private transient Float dailyFine;
	private transient Integer daysLate;
	private transient Float estimatedFine;
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHoldingId() {
		return this.holdingId;
	}

	public void setHoldingId(Integer holdingId) {
		this.holdingId = holdingId;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPreviousLendingId() {
		return this.previousLendingId;
	}

	public void setPreviousLendingId(Integer previousLendingId) {
		this.previousLendingId = previousLendingId;
	}

	public Date getExpectedReturnDate() {
		return this.expectedReturnDate;
	}

	public void setExpectedReturnDate(Date expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}

	public Date getReturnDate() {
		return this.returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public Float getEstimatedFine() {
		return this.estimatedFine;
	}

	public void setEstimatedFine(Float estimatedFine) {
		this.estimatedFine = estimatedFine;
	}

	public Float getDailyFine() {
		return this.dailyFine;
	}

	public void setDailyFine(Float dailyFine) {
		this.dailyFine = dailyFine;
	}

	public Integer getDaysLate() {
		return this.daysLate;
	}

	public void setDaysLate(Integer daysLate) {
		this.daysLate = daysLate;
	}
}
