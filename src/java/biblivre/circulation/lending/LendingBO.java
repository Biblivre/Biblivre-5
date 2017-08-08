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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserStatus;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.PrinterType;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.CalendarUtils;
import biblivre.core.utils.Constants;

public class LendingBO extends AbstractBO {
	
	private LendingDAO dao;
	
	public static LendingBO getInstance(String schema) {
		LendingBO bo = AbstractBO.getInstance(LendingBO.class, schema);

		if (bo.dao == null) {
			bo.dao = LendingDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public LendingDTO get(Integer lendingId) {
		return this.dao.get(lendingId);
	}

	public boolean isLent(HoldingDTO holding) {
		return this.getCurrentLending(holding) != null;
	}

	public boolean wasEverLent(HoldingDTO holding) {
		List<LendingDTO> history = this.dao.listHistory(holding);
		return history.size() > 0;
	}

	public LendingDTO getCurrentLending(HoldingDTO holding) {
		return this.dao.getCurrentLending(holding);
	}
	
	public Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> ids) {
		return this.dao.getCurrentLendingMap(ids);
	}


	public void checkLending(HoldingDTO holding, UserDTO user) {

		//User cannot be blocked
		if (UserStatus.BLOCKED.equals(user.getStatus()) || UserStatus.INACTIVE.equals(user.getStatus())) {
			throw new ValidationException("cataloging.lending.error.blocked_user");
		}

		//The material must be available
		if (!HoldingAvailability.AVAILABLE.equals(holding.getAvailability())) {
			throw new ValidationException("cataloging.lending.error.holding_unavailable");
		}

		//The material can't be already lent
		if (this.isLent(holding)) {
			throw new ValidationException("cataloging.lending.error.holding_is_lent");
		}

		//The lending limit (total number of materials that can be lent to a 
		//specific user) must be preserved
		if (!this.checkUserLendLimit(user, false)) {
			throw new ValidationException("cataloging.lending.error.limit_exceeded");
		}

	}

	public void checkRenew(HoldingDTO holding, UserDTO user) {
		//User cannot be blocked
		if (UserStatus.BLOCKED.equals(user.getStatus()) || UserStatus.INACTIVE.equals(user.getStatus())) {
			throw new ValidationException("cataloging.lending.error.blocked_user");
		}

		//The material must be available
		if (!HoldingAvailability.AVAILABLE.equals(holding.getAvailability())) {
			throw new ValidationException("cataloging.lending.error.holding_unavailable");
		}

		//The lending limit (total number of materials that can be lent to a 
		//specific user) must be preserved
		if (!this.checkUserLendLimit(user, true)) {
			throw new ValidationException("cataloging.lending.error.limit_exceeded");
		}
	}

	public boolean checkUserLendLimit(UserDTO user, boolean renew) {
		UserTypeBO userTypeBo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO type = userTypeBo.get(user.getType());
		Integer lendingLimit = (type != null) ? type.getLendingLimit() : 1;
		Integer count = this.dao.getCurrentLendingsCount(user);

		return renew ? (count <= lendingLimit) : (count < lendingLimit);
	}

	public Integer getCurrentLendingsCount(UserDTO user) {
		return this.dao.getCurrentLendingsCount(user);
	}

	public boolean doLend(HoldingDTO holding, UserDTO user, int createdBy) {
		this.checkLending(holding, user);

		LendingDTO lending = new LendingDTO();
		lending.setHoldingId(holding.getId());
		lending.setUserId(user.getId());

		UserTypeBO userTypeBo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO type = userTypeBo.get(user.getType());

		Date today = new Date();
		int days = (type != null) ? type.getLendingTimeLimit() : 7;
		Date expectedReturnDate = CalendarUtils.calculateExpectedReturnDate(this.getSchema(), today, days);
		lending.setExpectedReturnDate(expectedReturnDate);
		
		if (this.dao.doLend(lending)) {
			ReservationBO rbo = ReservationBO.getInstance(this.getSchema());
			rbo.delete(user.getId(), holding.getRecordId());
			return true;
		} else {
			return false;
		}
	}

	public boolean doReturn(LendingDTO lending, Float fineValue, boolean paid) {
		this.dao.doReturn(lending.getId());

		if (fineValue > 0) {
			LendingFineBO fineBo = LendingFineBO.getInstance(this.getSchema());
			fineBo.createFine(lending, fineValue, paid);
		}

		return true;
	}

	public boolean doRenew(LendingDTO lending) {
		UserBO userBo = UserBO.getInstance(this.getSchema());
		UserDTO userDto = userBo.get(lending.getUserId());
		if (userDto == null) {
			throw new ValidationException("cataloging.lending.error.user_not_found");
		}

		HoldingBO holdingBo = HoldingBO.getInstance(this.getSchema());
		HoldingDTO holding = (HoldingDTO)holdingBo.get(lending.getHoldingId());
		this.checkRenew(holding, userDto);
		
		UserTypeBO userTypeBO = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO type = userTypeBO.get(userDto.getType());

		Date today = new Date();
		int days = (type != null) ? type.getLendingTimeLimit() : 7;
		Date expectedReturnDate = CalendarUtils.calculateExpectedReturnDate(this.getSchema(), today, days);
		lending.setExpectedReturnDate(expectedReturnDate);

		return this.dao.doRenew(lending.getId(), lending.getExpectedReturnDate(), lending.getCreatedBy());
	}

	public List<LendingDTO> listHistory(UserDTO user) {
		List<LendingDTO> list = this.dao.listHistory(user);
		DTOCollection<LendingDTO> collection = new DTOCollection<LendingDTO>();
		collection.addAll(list);
		return collection;
	}
	
	public DTOCollection<LendingDTO> listLendings(UserDTO user) {
		List<LendingDTO> list = this.dao.listLendings(user);
		DTOCollection<LendingDTO> collection = new DTOCollection<LendingDTO>();
		collection.addAll(list);
		return collection;
	}

	public List<LendingDTO> listUserLendings(UserDTO user) {
		return this.dao.listLendings(user);
	}

	
	public DTOCollection<LendingInfoDTO> listLendings(int offset, int limit) {
		List<LendingDTO> list = this.dao.listLendings(offset, limit);
		return this.populateLendingInfo(list);
	}

	public Integer countHistory(UserDTO user) {
		return this.dao.countHistory(user);
	}
	
	public Integer countLendings(UserDTO user) {
		return this.dao.countLendings(user);
	}
	
	public DTOCollection<LendingInfoDTO> populateLendingInfoByHolding(DTOCollection<HoldingDTO> holdingList) {
		String schema = this.getSchema();

		Set<Integer> users = new HashSet<Integer>();
		Set<Integer> records = new HashSet<Integer>();
		Set<Integer> holdings = new HashSet<Integer>();

		for (HoldingDTO holding : holdingList) {
			holdings.add(holding.getId());

			Integer recordId = holding.getRecordId();
			if (recordId != null) {
				records.add(recordId);
			}
		}		

		UserBO ubo = UserBO.getInstance(schema);
		BiblioRecordBO bbo = BiblioRecordBO.getInstance(schema);
		
		Map<Integer, LendingDTO> lendingsMap = this.getCurrentLendingMap(holdings);
		for (Entry<Integer, LendingDTO> entry : lendingsMap.entrySet()) {
			Integer userid = entry.getValue().getUserId();
			if (userid != null) {
				users.add(userid);
			}
		}
		
		Map<Integer, RecordDTO> recordsMap = new HashMap<Integer, RecordDTO>();
		if (!records.isEmpty()) {
			recordsMap = bbo.map(records, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
		}

		Map<Integer, UserDTO> usersMap = new HashMap<Integer, UserDTO>();
		if (!users.isEmpty()) {
			usersMap = ubo.map(users);
		}

		// Join data
		DTOCollection<LendingInfoDTO> collection = new DTOCollection<LendingInfoDTO>();
		collection.setPaging(holdingList.getPaging());

		LendingFineBO lfbo = LendingFineBO.getInstance(schema);

		for (HoldingDTO holding : holdingList) {
			LendingInfoDTO info = new LendingInfoDTO();

			Integer holdingId = holding.getId();
			Integer recordId = holding.getRecordId();
			
			info.setHolding(holding);
			
			if (recordId != null) {
				info.setBiblio((BiblioRecordDTO)recordsMap.get(recordId));
			}
			
			LendingDTO lending = lendingsMap.get(holdingId);
			if (lending != null) {
				info.setLending(lending);
				
				if (lending.getUserId() != null) {
					UserDTO user = usersMap.get(lending.getUserId());
					UserTypeBO utbo = UserTypeBO.getInstance(schema);
					UserTypeDTO userType = utbo.get(user.getType());
					user.setUsertypeName(userType.getName());
					info.setUser(user);
					
					Integer daysLate = lfbo.calculateLateDays(lending);
					if (daysLate > 0) {
						Float dailyFine = userType.getFineValue();
						lending.setDaysLate(daysLate);
						lending.setDailyFine(dailyFine);
						lending.setEstimatedFine(dailyFine * daysLate);
					}

				}
			}
			
			collection.add(info);
		}

		return collection;
	}


	public DTOCollection<LendingInfoDTO> populateLendingInfo(List<LendingDTO> list) {
		return this.populateLendingInfo(list, true);
	}
	
	public DTOCollection<LendingInfoDTO> populateLendingInfo(List<LendingDTO> list, boolean populateUser) {
		String schema = this.getSchema();
		
		DTOCollection<LendingInfoDTO> collection = new DTOCollection<LendingInfoDTO>();
		
		Set<Integer> userIds = new HashSet<Integer>();
		Set<Integer> holdingIds = new HashSet<Integer>();
		Set<Integer> recordIds = new HashSet<Integer>();

		for (LendingDTO lending : list) {
			if (populateUser && lending.getUserId() != null) {
				userIds.add(lending.getUserId());
			}
			
			if (lending.getHoldingId() != null) {
				holdingIds.add(lending.getHoldingId());
			}
		}

		UserBO userBo = UserBO.getInstance(schema);
		HoldingBO holdingBo = HoldingBO.getInstance(schema);
		BiblioRecordBO biblioBo = BiblioRecordBO.getInstance(schema);
		
		Map<Integer, UserDTO> users = new HashMap<Integer, UserDTO>();
		if (!userIds.isEmpty()) {
			users = userBo.map(userIds);
		}
		
		Map<Integer, RecordDTO> holdings = new HashMap<Integer, RecordDTO>();
		if (!holdingIds.isEmpty()) {
			holdings = holdingBo.map(holdingIds);
		}
		
		for (RecordDTO holding : holdings.values()) {
			recordIds.add(((HoldingDTO)holding).getRecordId());
		}

		Map<Integer, RecordDTO> records = new HashMap<Integer, RecordDTO>();
		if (!recordIds.isEmpty()) {
			records = biblioBo.map(recordIds, RecordBO.MARC_INFO);
		}
		
		for (LendingDTO lending : list) {
			UserDTO user = users.get(lending.getUserId());
			HoldingDTO holding = (HoldingDTO)holdings.get(lending.getHoldingId());
			BiblioRecordDTO record = (holding != null) ? (BiblioRecordDTO)records.get(holding.getRecordId()) : null;

			LendingInfoDTO info = new LendingInfoDTO();
			info.setLending(lending);
			info.setUser(user);
			info.setHolding(holding);
			info.setBiblio(record);

			collection.add(info);
		}

		
		return collection;
	}

	public Integer countLentHoldings(int recordId) {
		return this.dao.countLentHoldings(recordId);
	}

	public LendingDTO getLatest(int holdingSerial, int userId) {
		return this.dao.getLatest(holdingSerial, userId);
	}
	
	public Integer countLendings() {
		return this.dao.countLendings();
	}
	
	public List<LendingDTO> listLendings(List<Integer> lendingsIds) {
		List<LendingDTO> lendingList = new ArrayList<LendingDTO>();
		
		for (Integer id : lendingsIds) {
			lendingList.add(this.get(id));
		}
		
		return lendingList;
	}

	public String generateReceipt(List<Integer> lendingsIds, TranslationsMap i18n) {
		PrinterType printerType = PrinterType.fromString(Configurations.getString(this.getSchema(), Constants.CONFIG_LENDING_PRINTER_TYPE));
		int columns = 24;
		
		if (printerType != null) {
			switch (printerType) {
			case PRINTER_40_COLUMNS:
				columns = 40;
				break;
			case PRINTER_80_COLUMNS:
				columns = 80;
				break;
			case PRINTER_COMMON:
				columns = 0;
				break;
			case PRINTER_24_COLUMNS:
			default:
				break;
			}
		}
		
		if (columns == 0) {
			return this.generateTableReceipt(lendingsIds, i18n);
		} else {
			return this.generateTxtReceipt(lendingsIds, i18n, columns);
		}
	}
	
	private String generateTxtReceipt(List<Integer> lendingsIds, TranslationsMap i18n, int columns) {
		DateFormat receiptDateFormat = new SimpleDateFormat(i18n.getText("format.datetime"));
		
		List<LendingDTO> lendings = this.listLendings(lendingsIds);
		if (lendings == null || lendings.isEmpty()) {
			return "";
		}
		List<LendingInfoDTO> lendingInfo = this.populateLendingInfo(lendings);
		if (lendingInfo == null || lendingInfo.isEmpty()) {
			return "";
		}
		
		StringBuilder receipt = new StringBuilder();
		receipt.append("<pre>\n");
		receipt.append(StringUtils.repeat('*', columns)).append("\n");
		receipt.append("*").append(StringUtils.repeat(' ', columns - 2)).append("*\n");
		
		String libraryName = Configurations.getString(this.getSchema(), "general.title");
		
		receipt.append("* ").append(StringUtils.center(libraryName, columns - 4)).append(" *\n");
		receipt.append("*").append(StringUtils.repeat(' ', columns - 2)).append("*\n");
		receipt.append(StringUtils.repeat('*', columns)).append("\n");
		receipt.append(StringUtils.center(receiptDateFormat.format(new Date()), columns)).append("\n");
		receipt.append("\n");
		
		if (lendingInfo.size() > 0) {
			
			UserDTO user = lendingInfo.get(0).getUser();
			
			String nameLabel = i18n.getText("circulation.user_field.name");
			String userName = StringEscapeUtils.escapeHtml4(user.getName());
			String idLabel = i18n.getText("circulation.user_field.id");
			String enrollment = user.getEnrollment();
			
			receipt.append(nameLabel).append(":");
			if (nameLabel.length() + userName.length() + 2 > columns) {
				receipt.append("\n");
				receipt.append("   ").append(StringUtils.abbreviate(userName, columns - 3)).append("\n");
			} else {
				receipt.append(" ").append(StringUtils.abbreviate(userName, columns - nameLabel.length() -3)).append("\n");
			}
			
			receipt.append(idLabel).append(":");
			if (idLabel.length() + enrollment.length() + 2 > columns) {
				receipt.append("\n");
				receipt.append("   ").append(StringUtils.abbreviate(enrollment, columns - 3)).append("\n");
			} else {
				receipt.append(" ").append(StringUtils.abbreviate(enrollment, columns - idLabel.length() - 3)).append("\n");
			}
			receipt.append("\n");
			
			List<LendingInfoDTO> currentLendings = new ArrayList<LendingInfoDTO>();
			List<LendingInfoDTO> currentRenews = new ArrayList<LendingInfoDTO>();
			List<LendingInfoDTO> currentReturns = new ArrayList<LendingInfoDTO>();
			
			for (LendingInfoDTO info : lendingInfo) {
				LendingDTO lendingDto = info.getLending();
				if (lendingDto.getReturnDate() != null) {
					currentReturns.add(info);
					continue;
				} else if (lendingDto.getPreviousLendingId() != null && lendingDto.getPreviousLendingId() > 0) {
					currentRenews.add(info);
					continue;
				} else {
					currentLendings.add(info);
				}
			}
			
			String authorLabel = i18n.getText("circulation.lending.receipt.author");
			String titleLabel = i18n.getText("circulation.lending.receipt.title");
			String biblioLabel = i18n.getText("circulation.lending.receipt.holding_id");
			String holdingLabel = i18n.getText("circulation.lending.receipt.accession_number");
			String expectedDateLabel = i18n.getText("circulation.lending.receipt.expected_return_date");
			String returnDateLabel = i18n.getText("circulation.lending.receipt.return_date");
			String lendingDateLabel = i18n.getText("circulation.lending.receipt.lending_date");

			if (!currentLendings.isEmpty()) {
			
				String header = "**" + i18n.getText("circulation.lending.receipt.lendings") + "**";
				receipt.append(StringUtils.center(header, columns)).append("\n");
				receipt.append("\n");
				
				for (LendingInfoDTO info : currentLendings) {
					receipt.append(StringUtils.repeat('*', columns / 2)).append("\n");
					receipt.append(authorLabel).append(":\n");
					String author = info.getBiblio().getAuthor();
					author = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(author, columns - 3));
					receipt.append("   ").append(author).append("\n");
					receipt.append(titleLabel).append(":\n");
					String title = info.getBiblio().getTitle();
					title = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(title, columns - 3));
					receipt.append("   ").append(title).append("\n");
					receipt.append(biblioLabel).append(":\n");
					receipt.append("   ").append(info.getHolding().getId()).append("\n");
					receipt.append(holdingLabel).append(":\n");
					String accessionNumber = info.getHolding().getAccessionNumber();
					accessionNumber = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(accessionNumber, columns - 3));
					receipt.append("   ").append(accessionNumber).append("\n");
					receipt.append(lendingDateLabel).append(":\n");
					Date lendingDate = info.getLending().getCreated();
					receipt.append("   ").append(receiptDateFormat.format(lendingDate)).append("\n");
					receipt.append(expectedDateLabel).append(":\n");
					Date expectedReturnDate = info.getLending().getExpectedReturnDate();
					receipt.append("   ").append(receiptDateFormat.format(expectedReturnDate)).append("\n");
					receipt.append(StringUtils.repeat('*', columns / 2)).append("\n");
					receipt.append("\n");
				}
				
			}
			
			if (!currentRenews.isEmpty()) {
				
				String header = "**" + i18n.getText("circulation.lending.receipt.renews") + "**";
				receipt.append(StringUtils.center(header, columns)).append("\n");
				receipt.append("\n");
				
				for (LendingInfoDTO info : currentRenews) {
					receipt.append(StringUtils.repeat('*', columns / 2)).append("\n");
					receipt.append(authorLabel).append(":\n");
					String author = info.getBiblio().getAuthor();
					author = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(author, columns - 3));
					receipt.append("   ").append(author).append("\n");
					receipt.append(titleLabel).append(":\n");
					String title = info.getBiblio().getTitle();
					title = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(title, columns - 3));
					receipt.append("   ").append(title).append("\n");
					receipt.append(biblioLabel).append(":\n");
					receipt.append("   ").append(info.getHolding().getId()).append("\n");
					receipt.append(holdingLabel).append(":\n");
					String accessionNumber = info.getHolding().getAccessionNumber();
					accessionNumber = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(accessionNumber, columns - 3));
					receipt.append("   ").append(accessionNumber).append("\n");
					receipt.append(lendingDateLabel).append(":\n");
					Date lendingDate = info.getLending().getCreated();
					receipt.append("   ").append(receiptDateFormat.format(lendingDate)).append("\n");
					receipt.append(expectedDateLabel).append(":\n");
					Date expectedReturnDate = info.getLending().getExpectedReturnDate();
					receipt.append("   ").append(receiptDateFormat.format(expectedReturnDate)).append("\n");
					receipt.append(StringUtils.repeat('*', columns / 2)).append("\n");
					receipt.append("\n");
				}
				
			}
			
			if (!currentReturns.isEmpty()) {
				
				String header = "**" + i18n.getText("circulation.lending.receipt.returns") + "**";
				receipt.append(StringUtils.center(header, columns)).append("\n");
				receipt.append("\n");
				
				for (LendingInfoDTO info : currentReturns) {
					receipt.append(StringUtils.repeat('*', columns / 2)).append("\n");
					receipt.append(authorLabel).append(":\n");
					String author = info.getBiblio().getAuthor();
					author = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(author, columns - 3));
					receipt.append("   ").append(author).append("\n");
					receipt.append(titleLabel).append(":\n");
					String title = info.getBiblio().getTitle();
					title = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(title, columns - 3));
					receipt.append("   ").append(title).append("\n");
					receipt.append(biblioLabel).append(":\n");
					receipt.append("   ").append(info.getHolding().getId()).append("\n");
					receipt.append(holdingLabel).append(":\n");
					String accessionNumber = info.getHolding().getAccessionNumber();
					accessionNumber = StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(accessionNumber, columns - 3));
					receipt.append("   ").append(accessionNumber).append("\n");
					receipt.append(lendingDateLabel).append(":\n");
					Date lendingDate = info.getLending().getCreated();
					receipt.append("   ").append(receiptDateFormat.format(lendingDate)).append("\n");
					receipt.append(returnDateLabel).append(":\n");
					Date returnDate = info.getLending().getReturnDate();
					receipt.append("   ").append(receiptDateFormat.format(returnDate)).append("\n");
					receipt.append(StringUtils.repeat('*', columns / 2)).append("\n");
					receipt.append("\n");
				}
				
			}
		}
		receipt.append("\n");
		receipt.append(StringUtils.repeat('*', columns)).append("\n");
		receipt.append("</pre>\n");
		
		return receipt.toString();
	}
	
	private String generateTableReceipt(List<Integer> lendingsIds, TranslationsMap i18n) {
		DateFormat receiptDateFormat = new SimpleDateFormat(i18n.getText("format.datetime"));
		
		List<LendingDTO> lendings = this.listLendings(lendingsIds);
		if (lendings == null || lendings.isEmpty()) {
			return "";
		}
		List<LendingInfoDTO> lendingInfo = this.populateLendingInfo(lendings);
		if (lendingInfo == null || lendingInfo.isEmpty()) {
			return "";
		}
		
		StringBuilder receipt = new StringBuilder();
		receipt.append("<html>");
		receipt.append("<table style=\"border: 1px solid; padding: 10px; font-family: HelveticaNeue-Light, 'Helvetica Neue Light', 'Helvetica Neue', Helvetica, Arial, 'Lucida Grande', sans-serif; font-size: 14px; font-style: normal; font-variant: normal; font-weight: normal;\">");
		
		String libraryName = Configurations.getString(this.getSchema(), "general.title");
		String now = receiptDateFormat.format(new Date());
		receipt.append("<tr><td colspan=\"2\" style=\"text-align: center;\">");
		receipt.append(libraryName).append(" - ").append(now);
		receipt.append("</td></tr>");
		receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
		
		if (lendingInfo.size() > 0) {
			
			UserDTO user = lendingInfo.get(0).getUser();
			
			String nameLabel = i18n.getText("circulation.user_field.name");
			String userName = StringEscapeUtils.escapeHtml4(user.getName());
			
			receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
			receipt.append(nameLabel).append(":");
			receipt.append("</td><td style=\"text-align: left\">");
			receipt.append(userName);
			receipt.append("</td></tr>");
			
			String idLabel = i18n.getText("circulation.user_field.id");
			String enrollment = user.getEnrollment();
			
			receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
			receipt.append(idLabel).append(":");
			receipt.append("</td><td style=\"text-align: left\">");
			receipt.append(enrollment);
			receipt.append("</td></tr>");
			
			receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
			
			List<LendingInfoDTO> currentLendings = new ArrayList<LendingInfoDTO>();
			List<LendingInfoDTO> currentRenews = new ArrayList<LendingInfoDTO>();
			List<LendingInfoDTO> currentReturns = new ArrayList<LendingInfoDTO>();
			
			for (LendingInfoDTO info : lendingInfo) {
				LendingDTO lendingDto = info.getLending();
				if (lendingDto.getReturnDate() != null) {
					currentReturns.add(info);
					continue;
				} else if (lendingDto.getPreviousLendingId() != null && lendingDto.getPreviousLendingId() > 0) {
					currentRenews.add(info);
					continue;
				} else {
					currentLendings.add(info);
				}
			}
			
			String authorLabel = i18n.getText("circulation.lending.receipt.author");
			String titleLabel = i18n.getText("circulation.lending.receipt.title");
			String biblioLabel = i18n.getText("circulation.lending.receipt.holding_id");
			String holdingLabel = i18n.getText("circulation.lending.receipt.accession_number");
			String expectedDateLabel = i18n.getText("circulation.lending.receipt.expected_return_date");
			String returnDateLabel = i18n.getText("circulation.lending.receipt.return_date");
			String lendingDateLabel = i18n.getText("circulation.lending.receipt.lending_date");

			if (!currentLendings.isEmpty()) {
			
				String header = i18n.getText("circulation.lending.receipt.lendings");
				receipt.append("<tr><td colspan=\"2\" style=\"text-align: center;\">");
				receipt.append(header);
				receipt.append("</td></tr>");
				receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
				receipt.append("<tr><td>&nbsp;</td></tr>");
				
				for (LendingInfoDTO info : currentLendings) {
					
					String author = info.getBiblio().getAuthor();
					author = StringEscapeUtils.escapeHtml4(author);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(authorLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(author);
					receipt.append("</td></tr>");
					
					String title = info.getBiblio().getTitle();
					title = StringEscapeUtils.escapeHtml4(title);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(titleLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(title);
					receipt.append("</td></tr>");
					
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(biblioLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(info.getHolding().getId());
					receipt.append("</td></tr>");
					
					String accessionNumber = info.getHolding().getAccessionNumber();
					accessionNumber = StringEscapeUtils.escapeHtml4(accessionNumber);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(holdingLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(accessionNumber);
					receipt.append("</td></tr>");
					
					Date lendingDate = info.getLending().getCreated();
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(lendingDateLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(receiptDateFormat.format(lendingDate));
					receipt.append("</td></tr>");
					
					Date expectedReturnDate = info.getLending().getExpectedReturnDate();
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(expectedDateLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(receiptDateFormat.format(expectedReturnDate));
					receipt.append("</td></tr>");
					
					receipt.append("<tr><td>&nbsp;</td></tr>");
					
				}
				
				receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
				
			}
			
			if (!currentRenews.isEmpty()) {
				
				String header = i18n.getText("circulation.lending.receipt.renews");
				receipt.append("<tr><td colspan=\"2\" style=\"text-align: center;\">");
				receipt.append(header);
				receipt.append("</td></tr>");
				receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
				receipt.append("<tr><td>&nbsp;</td></tr>");
				
				for (LendingInfoDTO info : currentRenews) {
					
					String author = info.getBiblio().getAuthor();
					author = StringEscapeUtils.escapeHtml4(author);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(authorLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(author);
					receipt.append("</td></tr>");
					
					String title = info.getBiblio().getTitle();
					title = StringEscapeUtils.escapeHtml4(title);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(titleLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(title);
					receipt.append("</td></tr>");
					
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(biblioLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(info.getHolding().getId());
					receipt.append("</td></tr>");
					
					String accessionNumber = info.getHolding().getAccessionNumber();
					accessionNumber = StringEscapeUtils.escapeHtml4(accessionNumber);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(holdingLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(accessionNumber);
					receipt.append("</td></tr>");
					
					Date lendingDate = info.getLending().getCreated();
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(lendingDateLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(receiptDateFormat.format(lendingDate));
					receipt.append("</td></tr>");
					
					Date expectedReturnDate = info.getLending().getExpectedReturnDate();
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(expectedDateLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(receiptDateFormat.format(expectedReturnDate));
					receipt.append("</td></tr>");
					receipt.append("<tr><td>&nbsp;</td></tr>");
					
				}
				
				receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
				
			}
			
			if (!currentReturns.isEmpty()) {
				
				String header = i18n.getText("circulation.lending.receipt.returns");
				receipt.append("<tr><td colspan=\"2\" style=\"text-align: center;\">");
				receipt.append(header);
				receipt.append("</td></tr>");
				receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
				receipt.append("<tr><td>&nbsp;</td></tr>");
				
				for (LendingInfoDTO info : currentReturns) {
					
					String author = info.getBiblio().getAuthor();
					author = StringEscapeUtils.escapeHtml4(author);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(authorLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(author);
					receipt.append("</td></tr>");
					
					String title = info.getBiblio().getTitle();
					title = StringEscapeUtils.escapeHtml4(title);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(titleLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(title);
					receipt.append("</td></tr>");
					
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(biblioLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(info.getHolding().getId());
					receipt.append("</td></tr>");
					
					String accessionNumber = info.getHolding().getAccessionNumber();
					accessionNumber = StringEscapeUtils.escapeHtml4(accessionNumber);
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(holdingLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(accessionNumber);
					receipt.append("</td></tr>");
					
					Date lendingDate = info.getLending().getCreated();
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(lendingDateLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(receiptDateFormat.format(lendingDate));
					receipt.append("</td></tr>");
					

					Date returnDate = info.getLending().getReturnDate();
					receipt.append("<tr><td style=\"width: 40%; text-align: right;\">");
					receipt.append(returnDateLabel).append(":");
					receipt.append("</td><td style=\"text-align: left\">");
					receipt.append(receiptDateFormat.format(returnDate));
					receipt.append("</td></tr>");
					receipt.append("<tr><td>&nbsp;</td></tr>");
					
				}
				
				receipt.append("<tr><td colspan=\"2\"><hr /></td></tr>");
				
			}
		}
		receipt.append("</table></html>");
		
		return receipt.toString();
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}


//	public List<LendingInfoDTO> listByRecordSerial(Integer recordSerial) {
//		List<LendingInfoDTO> result = new ArrayList<LendingInfoDTO>();
//		List<LendingDTO> lendings = new LendingDAO().listByRecordSerial(recordSerial);
//		for (LendingDTO lending : lendings) {
//			result.add(new LendingInfoDTO(lending));
//		}
//		return result;
//	}

}
