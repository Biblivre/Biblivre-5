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
import java.util.List;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.utils.CalendarUtils;

public class LendingFineBO extends AbstractBO {
	
	private LendingFineDAO dao;
	
	public static LendingFineBO getInstance(String schema) {
		LendingFineBO bo = AbstractBO.getInstance(LendingFineBO.class, schema);

		if (bo.dao == null) {
			bo.dao = LendingFineDAO.getInstance(schema);
		}

		return bo;
	}
	
	public LendingFineDTO getById(Integer fineId) {
		return this.dao.get(fineId);
	}

	public LendingFineDTO getByHistoryId(Integer lendingId) {
		return this.dao.getByLendingId(lendingId);
	}

	public List<LendingFineDTO> listLendingFines(UserDTO user) {
		return this.populateFineInfo(this.dao.list(user, false));
	}
	
	public List<LendingFineDTO> listLendingFines(UserDTO user, boolean pendingOnly) {
		return  this.populateFineInfo(this.dao.list(user, pendingOnly));
	}
	
	private List<LendingFineDTO> populateFineInfo(List<LendingFineDTO> fines) {
		for (LendingFineDTO fine : fines) {
			this.populateFineInfo(fine);
		}
		return fines;
	}

	private void populateFineInfo(LendingFineDTO fine) {
		Integer lendingId = fine.getLendingId();
		
		LendingBO lbo = LendingBO.getInstance(this.getSchema());
		HoldingBO hbo = HoldingBO.getInstance(this.getSchema());
		BiblioRecordBO rbo = BiblioRecordBO.getInstance(this.getSchema());
		
		LendingDTO lending = lbo.get(lendingId);
		HoldingDTO holding = (HoldingDTO)hbo.get(lending.getHoldingId());
		BiblioRecordDTO biblio = (BiblioRecordDTO)rbo.get(holding.getRecordId(), RecordBO.MARC_INFO);
		
		fine.setAuthor(biblio.getAuthor());
		fine.setTitle(biblio.getTitle());
	}

	public boolean update(LendingFineDTO fine) {
		return this.dao.update(fine);
	}
	
	public LendingFineDTO createFine(LendingDTO lending, Float value, boolean paid) {
		LendingFineDTO fine = new LendingFineDTO();
		fine.setUserId(lending.getUserId());
		fine.setLendingId(lending.getId());
		fine.setValue(value);
		if (paid) {
			fine.setPayment(new Date());
		}
		fine.setCreatedBy(lending.getCreatedBy());
		this.dao.insert(fine);
		return fine;
	}
	
	public Float calculateFineValue(Integer daysLate, UserDTO user) {
		if (daysLate == null || daysLate <= 0) {
			return 0.0f;
		}
		UserTypeBO utBo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO userType = utBo.get(user.getType());
		Float fineValue = userType.getFineValue();
		return daysLate * fineValue;
	}
	
	public boolean isLateReturn(LendingDTO lending) {
		return this.calculateLateDays(lending) > 0;
	}
	
	public Integer calculateLateDays(LendingDTO lending) {
		Date expectedReturnDate = lending.getExpectedReturnDate();
		return CalendarUtils.calculateDeteDifference(expectedReturnDate, new Date());
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}

}
