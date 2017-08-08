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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserStatus;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.CalendarUtils;

public class ReservationBO extends AbstractBO {
	
	private ReservationDAO dao;
	
	public static ReservationBO getInstance(String schema) {
		ReservationBO bo = AbstractBO.getInstance(ReservationBO.class, schema);

		if (bo.dao == null) {
			bo.dao = ReservationDAO.getInstance(schema);
		}

		return bo;
	}
	
	public boolean deleteExpired() {
		return this.dao.deleteExpired();
	}

	public ReservationDTO get(Integer id) {
		return this.dao.get(id);
	}

	public List<ReservationDTO> get(RecordDTO record) {
		return this.dao.list(null, record);
	}

	public int countReserved(RecordDTO record) {
		return this.dao.count(null, record);
	}

	public int countReserved(UserDTO user) {
		return this.dao.count(user, null);
	}
	
	public List<Integer> listReservedRecordIds(UserDTO user) {
		List<Integer> reservedRecords = new ArrayList<Integer>();
		List<ReservationDTO> list = this.dao.list(user, null);
		
		for (ReservationDTO dto : list) {
			reservedRecords.add(dto.getRecordId());
		}
		
		return reservedRecords;
	}

	public List<ReservationDTO> list(UserDTO user) {
		List<ReservationDTO> list = this.dao.list(user, null);
		BiblioRecordBO bo = BiblioRecordBO.getInstance(this.getSchema());
		
		for (ReservationDTO dto : list) {
			BiblioRecordDTO record = (BiblioRecordDTO) bo.get(dto.getRecordId(), RecordBO.MARC_INFO);

			dto.setTitle(record.getTitle());
			dto.setAuthor(record.getAuthor());
		}
		
		return list;
	}
	
	public List<ReservationInfoDTO> listReservationInfo(UserDTO user) {
		List<ReservationDTO> list = this.dao.list(user, null);
		List<ReservationInfoDTO> result = new LinkedList<ReservationInfoDTO>();
		BiblioRecordBO bo = BiblioRecordBO.getInstance(this.getSchema());
		
		for (ReservationDTO dto : list) {
			ReservationInfoDTO info = new ReservationInfoDTO();
			info.setReservation(dto);

			BiblioRecordDTO record = (BiblioRecordDTO) bo.get(dto.getRecordId(), RecordBO.MARC_INFO);
			info.setBiblio(record);

			result.add(info);
		}
		
		return result;
	}
	
	public List<ReservationInfoDTO> list() {
		List<ReservationDTO> list = this.dao.list();
		List<ReservationInfoDTO> result = new LinkedList<ReservationInfoDTO>();

		BiblioRecordBO bo = BiblioRecordBO.getInstance(this.getSchema());
		UserBO ubo = UserBO.getInstance(this.getSchema());
		
		for (ReservationDTO dto : list) {
			ReservationInfoDTO info = new ReservationInfoDTO();

			BiblioRecordDTO record = (BiblioRecordDTO) bo.get(dto.getRecordId(), RecordBO.MARC_INFO);
			info.setBiblio(record);

			dto.setTitle(record.getTitle());
			dto.setAuthor(record.getAuthor());
			info.setReservation(dto);

			UserDTO user = ubo.get(dto.getUserId()); 
			info.setUser(user);
		}
		
		return result;
	}

	public boolean delete(Integer id) {
		return this.dao.delete(id);
	}
	
	public boolean delete(Integer userId, Integer recordId) {
		return this.dao.delete(userId, recordId);
	}
	
	public void checkReservation(RecordDTO record, UserDTO user) {

		//User cannot be blocked
		if (UserStatus.BLOCKED.equals(user.getStatus()) || UserStatus.INACTIVE.equals(user.getStatus())) {
			throw new ValidationException("cataloging.reservation.error.blocked_user");
		}

		//The lending limit (total number of materials that can be lent to a 
		//specific user) must be preserved
		if (!this.checkUserReservationLimit(user)) {
			throw new ValidationException("cataloging.reservation.error.limit_exceeded");
		}
	}

	public boolean checkUserReservationLimit(UserDTO user) {
		UserTypeBO userTypeBo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO type = userTypeBo.get(user.getType());

		Integer limit = (type != null) ? type.getReservationLimit() : 1;
		Integer count = this.dao.count(user, null);

		return count < limit;
	}

	public int reserve(RecordDTO record, UserDTO user, int createdBy) {
		this.checkReservation(record, user);

		ReservationDTO reservation = new ReservationDTO();
		reservation.setRecordId(record.getId());
		reservation.setUserId(user.getId());

		UserTypeBO userTypeBo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO type = userTypeBo.get(user.getType());

		Date today = new Date();
		int days = (type != null) ? type.getReservationTimeLimit() : 7;
		Date expires = CalendarUtils.calculateExpectedReturnDate(this.getSchema(), today, days);

		reservation.setExpires(expires);
		
		int reservationId = this.dao.insert(reservation);
		
		return reservationId;
	}
	
	public SearchDTO populateReservationInfoByBiblio(SearchDTO search) {
		String schema = this.getSchema();

		Set<Integer> users = new HashSet<Integer>();
		Set<Integer> records = new HashSet<Integer>();

		for (RecordDTO record : search) {
			records.add(record.getId());
		}		

		UserBO ubo = UserBO.getInstance(schema);
		
		Map<Integer, List<ReservationDTO>> reservationsMap = this.getReservationsMap(records);
		for (Entry<Integer, List<ReservationDTO>> entry : reservationsMap.entrySet()) {
			for (ReservationDTO dto : entry.getValue()) {
				Integer userid = dto.getUserId();
				if (userid != null) {
					users.add(userid);
				}
			}
		}

		Map<Integer, UserDTO> usersMap = new HashMap<Integer, UserDTO>();
		if (!users.isEmpty()) {
			usersMap = ubo.map(users);
		}
		
		// Join data
		for (RecordDTO record : search) {
			List<ReservationDTO> reservations = reservationsMap.get(record.getId());
			
			if (reservations == null) {
				continue;
			}

			DTOCollection<ReservationInfoDTO> infoList = new DTOCollection<ReservationInfoDTO>();
			for (ReservationDTO reservation : reservations) {
				ReservationInfoDTO info = new ReservationInfoDTO();

				info.setReservation(reservation);
				if (reservation.getUserId() != null) {
					info.setUser(usersMap.get(reservation.getUserId()));
				}
				
				infoList.add(info);
			}
			record.addExtraData("reservationInfo", infoList);
		}

		return search;
	}
	
	public Map<Integer, List<ReservationDTO>> getReservationsMap(Set<Integer> recordIds) {
		return this.dao.getReservationsMap(recordIds);
	}
	
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}
}
