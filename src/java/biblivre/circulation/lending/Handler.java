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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.PagingDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;

public class Handler extends AbstractHandler {

	public void search(ExtendedRequest request, ExtendedResponse response) {
		
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");
		String query = null;
		Boolean lentOnly = false;
		
		try {
			JSONObject json = new JSONObject(searchParameters);
			query = json.optString("query");
			lentOnly = json.optBoolean("holding_list_lendings");
		} catch (JSONException je) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		HoldingBO hbo = HoldingBO.getInstance(schema);

		Integer limit = request.getInteger("limit", Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
		Integer offset = (request.getInteger("page", 1) - 1) * limit;

		DTOCollection<HoldingDTO> holdingList = hbo.search(query, RecordDatabase.MAIN, lentOnly, offset, limit);
				
		if (CollectionUtils.isEmpty(holdingList)) {
			this.setMessage(ActionResult.WARNING, "circulation.lending.no_holding_found");
			return;
		}

		LendingBO lbo = LendingBO.getInstance(schema);
		DTOCollection<LendingInfoDTO> lendingInfo = lbo.populateLendingInfoByHolding(holdingList);

		try {
			this.json.put("search", lendingInfo.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	public void userSearch(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		biblivre.circulation.user.Handler userHandler = new biblivre.circulation.user.Handler();
		DTOCollection<UserDTO> userList = userHandler.searchHelper(request, response, this);

		if (userList == null || userList.size() == 0) {
			this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
			return;
		}

		DTOCollection<LendingListDTO> list = new DTOCollection<LendingListDTO>();
		list.setPaging(userList.getPaging());

		for (UserDTO user : userList) {
			list.add(this.populateLendingList(schema, user, false));
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	private LendingListDTO populateLendingList(String schema, UserDTO user, boolean history) {
		LendingBO lbo = LendingBO.getInstance(schema);
		HoldingBO hbo = HoldingBO.getInstance(schema);
		BiblioRecordBO rbo = BiblioRecordBO.getInstance(schema);
		LendingFineBO lfbo = LendingFineBO.getInstance(schema);
		ReservationBO rsvBo = ReservationBO.getInstance(schema);
		
		LendingListDTO lendingList = new LendingListDTO();
		
		lendingList.setUser(user);
		lendingList.setId(user.getId());
		
		List<LendingDTO> lendings = lbo.listUserLendings(user);
		if (history) {
			lendings.addAll(lbo.listHistory(user));
		}
		
		List<LendingInfoDTO> infos = new LinkedList<LendingInfoDTO>();

		for (LendingDTO lending : lendings) {
			HoldingDTO holding = (HoldingDTO) hbo.get(lending.getHoldingId(), RecordBO.MARC_INFO);
			
			BiblioRecordDTO biblio = (BiblioRecordDTO) rbo.get(holding.getRecordId(), RecordBO.MARC_INFO);
			
			LendingInfoDTO info = new LendingInfoDTO();
			
			info.setLending(lending);
			info.setHolding(holding);
			info.setBiblio(biblio);
			
			infos.add(info);
			
			//CHECK FOR LENDING FINES
			Integer daysLate = lfbo.calculateLateDays(lending);
			if (daysLate > 0) {
				UserTypeBO utbo = UserTypeBO.getInstance(schema);
				UserTypeDTO userType = utbo.get(user.getType());
				Float dailyFine = userType.getFineValue();
				
				lending.setDaysLate(daysLate);
				lending.setDailyFine(dailyFine);
				lending.setEstimatedFine(dailyFine * daysLate);
			}
		}
		lendingList.setLendingInfo(infos);
		
		lendingList.setReservedRecords(rsvBo.listReservedRecordIds(user));
		
		return lendingList;
		
	}
	
	public void lend(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer holdingId = request.getInteger("holding_id");
		Integer userId = request.getInteger("user_id");
		
		HoldingBO holdingBo = HoldingBO.getInstance(schema);
		HoldingDTO holding = (HoldingDTO)holdingBo.get(holdingId, RecordBO.MARC_INFO);
		
		UserBO userBo = UserBO.getInstance(schema);
		UserDTO user = userBo.get(userId);
		
		LendingBO lendingBo = LendingBO.getInstance(schema);
		boolean success = lendingBo.doLend(holding, user, request.getLoggedUserId());
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.lending.lend_success");
			
			BiblioRecordBO rbo = BiblioRecordBO.getInstance(schema);
			BiblioRecordDTO biblio = (BiblioRecordDTO) rbo.get(holding.getRecordId(), RecordBO.MARC_INFO);
			LendingDTO lending = lendingBo.getCurrentLending(holding);
			LendingInfoDTO info = new LendingInfoDTO();
			info.setLending(lending);
			info.setHolding(holding);
			info.setBiblio(biblio);
			info.setUser(user);
			
			try {
				this.json.put("data", info.toJSONObject());
				this.json.put("full_data", true);
			} catch (JSONException e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;
			}

		} else {
			this.setMessage(ActionResult.WARNING, "circulation.lending.lend_failure");
		}

	}

	public void renewLending(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer lendingId = request.getInteger("id");
		
		LendingBO lendingBo = LendingBO.getInstance(schema);
		LendingDTO lending = lendingBo.get(lendingId);
		
		Integer holdingId = lending.getHoldingId();
		Integer userId = lending.getUserId();
		
		lending.setCreatedBy(request.getLoggedUserId());
		boolean success = lendingBo.doRenew(lending);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.lending.renew_success");
			
			HoldingBO holdingBo = HoldingBO.getInstance(schema);
			HoldingDTO holding = (HoldingDTO)holdingBo.get(holdingId, RecordBO.MARC_INFO);
			BiblioRecordBO rbo = BiblioRecordBO.getInstance(schema);
			BiblioRecordDTO biblio = (BiblioRecordDTO) rbo.get(holding.getRecordId(), RecordBO.MARC_INFO);
			UserBO userBo = UserBO.getInstance(schema);
			UserDTO user = userBo.get(userId);
			
			LendingDTO newLending = lendingBo.getCurrentLending(holding);

			LendingInfoDTO info = new LendingInfoDTO();
			info.setLending(newLending);
			info.setHolding(holding);
			info.setBiblio(biblio);
			info.setUser(user);
			
			try {
				this.json.put("data", info.toJSONObject());
				this.json.put("full_data", true);
			} catch (JSONException e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;
			}
		
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.lending.renew_failure");
		}
		
	}
	
	public void returnLending(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer lendingId = request.getInteger("id");
		Float fineValue = request.getFloat("fine");
		boolean paid = request.getBoolean("paid");
		
		LendingBO lendingBo = LendingBO.getInstance(schema);
		LendingDTO lending = lendingBo.get(lendingId);
		boolean success = lendingBo.doReturn(lending, fineValue, paid);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.lending.return_success");
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.lending.return_failure");
		}
	}	

	public void payFine(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer fineId = request.getInteger("fine_id");
		Boolean exempt = request.getBoolean("exempt", false);

		LendingFineBO lendingFineBo = LendingFineBO.getInstance(schema);
		LendingFineDTO dto = lendingFineBo.getById(Integer.valueOf(fineId));
		if (exempt) {
			dto.setValue(0f);
		}
		
		boolean success = lendingFineBo.update(dto);
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.lending.fine.success_pay_fine");
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.lending.fine.failure_pay_fine");
		}
	}
	
	public void listAll(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		LendingBO bo = LendingBO.getInstance(schema);
		
		Integer limit = request.getInteger("limit", Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
		Integer offset = request.getInteger("offset", 0);

		DTOCollection<LendingInfoDTO> list = new DTOCollection<LendingInfoDTO>();
		
		DTOCollection<LendingInfoDTO> lendingInfoList = bo.listLendings(offset, limit);
		
		list.addAll(lendingInfoList);
		
		PagingDTO paging = new PagingDTO(bo.countLendings(), limit, offset);
		list.setPaging(paging);
		
		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "circulation.lending.no_lending_found");
			return;
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}

	public void printReceipt(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String idList = request.getString("id_list");

		String[] idArray = idList.split(",");
		List<Integer> ids = new ArrayList<Integer>();
		try {
			for (int i = 0; i < idArray.length; i++) {
				ids.add(Integer.valueOf(idArray[i]));
			}			
		} catch(Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
		}
		
		LendingBO bo = LendingBO.getInstance(schema);
		String receipt = bo.generateReceipt(ids, request.getTranslationsMap());
		
		try {
			this.json.put("receipt", receipt);
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
}
