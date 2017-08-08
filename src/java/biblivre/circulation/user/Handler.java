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
package biblivre.circulation.user;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.lending.LendingInfoDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.reservation.ReservationInfoDTO;
import biblivre.circulation.reservation.ReservationListDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import biblivre.digitalmedia.DigitalMediaBO;

public class Handler extends AbstractHandler {

	public void open(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id");

		UserBO bo = UserBO.getInstance(schema);
		UserDTO user = bo.get(id);
		
		if (user == null) {
			this.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
			return;
		}

		try {
			this.json.put("user", user.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}

	public void search(ExtendedRequest request, ExtendedResponse response) {
		DTOCollection<UserDTO> list = this.searchHelper(request, response, this);

		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	public void paginate(ExtendedRequest request, ExtendedResponse response) {
		this.search(request, response);
	}
	
	public DTOCollection<UserDTO> searchHelper(ExtendedRequest request, ExtendedResponse response, AbstractHandler handler) {
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");

		UserSearchDTO searchDto = new UserSearchDTO(searchParameters);

		Integer limit = request.getInteger("limit", Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
		Integer offset = request.getInteger("offset", 0);

		Integer page = request.getInteger("page", 1);
		if (page > 1) {
			offset = limit * (page - 1);
		}
		
		UserBO bo = UserBO.getInstance(schema);

		DTOCollection<UserDTO> list = bo.search(searchDto, limit, offset);

		if (list.size() == 0) {
			handler.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
		}
		
		return list;
	}
	
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id");

		UserBO bo = UserBO.getInstance(schema);
		UserDTO user = null; 
		
		if (id != 0) {
			user = bo.get(id);
			if (user == null) {
				this.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
				return;
			}
		} else {
			user = new UserDTO();
		}

		user.setName(request.getString("name"));
		user.setStatus(request.getEnum(UserStatus.class, "status", UserStatus.ACTIVE));

		user.setType(request.getInteger("type"));
		
		user.setCreatedBy(request.getLoggedUserId());
		
		List<UserFieldDTO> userFields = UserFields.getFields(schema);
		for (UserFieldDTO userField : userFields) {
			String key = userField.getKey();
			if (request.hasParameter(key)) {
				user.addField(key, request.getString(key));
			}
		}

		try {
			String photoData = request.getString("photo_data");
			if (StringUtils.isNotBlank(photoData)) {
				biblivre.digitalmedia.Handler mediaHandler = new biblivre.digitalmedia.Handler();
	
				MemoryFile file = new MemoryFile();
	
				byte[] arr = Base64.decodeBase64(photoData);
				file.setContentType("image/png");
				file.setName(user.getName() + ".png");
				file.setInputStream(new ByteArrayInputStream(arr));
				file.setSize(arr.length);
				
				String photoId = mediaHandler.uploadHelper(schema, file);
				String oldPhotoId = user.getPhotoId();
				
				if (StringUtils.isNotBlank(photoId)) {
					user.setPhotoId(photoId);
	
					if (StringUtils.isNotBlank(oldPhotoId)) {
						String decodedId = new String(new Base64().decode(oldPhotoId));
						String[] splitId = decodedId.split(":");
	
						if (splitId.length == 2 && StringUtils.isNumeric(splitId[0])) {
							// Try to remove the file from Biblivre DB
	
							DigitalMediaBO dmbo = DigitalMediaBO.getInstance(schema);
							dmbo.delete(Integer.valueOf(splitId[0]), splitId[1]);
						}
					}
				}
			}
		} catch (Exception e) {
		}
		
		if (bo.save(user)) {
			if (id == 0) {
				this.setMessage(ActionResult.SUCCESS, "circulation.users.success.save");
			} else {
				this.setMessage(ActionResult.SUCCESS, "circulation.users.success.update");
			}
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.users.error.save");
		}
		
		try {
			this.json.put("data", user.toJSONObject());
			this.json.put("full_data", true);
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}	

	public void delete(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id");

		UserBO bo = UserBO.getInstance(schema);
		UserDTO user = bo.get(id);

		String act = (user.getStatus() == UserStatus.INACTIVE) ? "delete" : "disable";
		
		boolean success = bo.delete(user);

		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.users.success." + act);
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.users.failure." + act);
		}
	}	
	
	public void loadTabData(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id");
		String tab = request.getString("tab");

		UserBO bo = UserBO.getInstance(schema);
		UserDTO user = bo.get(id);
		
		if (user == null) {
			this.setMessage(ActionResult.WARNING, "circulation.error.user_not_found");
			return;
		}
		
		DTOCollection<?> data = null;

		if (tab.equals("lendings")) {
			LendingBO lbo = LendingBO.getInstance(schema);
			DTOCollection<LendingInfoDTO> list = lbo.populateLendingInfo(lbo.listLendings(user), false);
			list.addAll(lbo.populateLendingInfo(lbo.listHistory(user), false));
			data = list;
		} else if (tab.equals("reservations")) {
			ReservationBO rbo = ReservationBO.getInstance(schema);

			List<ReservationInfoDTO> infos = rbo.listReservationInfo(user);
			ReservationListDTO reservationList = new ReservationListDTO();
			reservationList.setUser(user);
			reservationList.setId(user.getId());
			reservationList.setReservationInfoList(infos);

			DTOCollection<ReservationListDTO> list = new DTOCollection<ReservationListDTO>();
			list.add(reservationList);
			data = list;
		} else if (tab.equals("fines")) {
			LendingFineBO lfbo = LendingFineBO.getInstance(schema);

			List<LendingFineDTO> fines = lfbo.listLendingFines(user);

			DTOCollection<LendingFineDTO> list = new DTOCollection<LendingFineDTO>();
			list.addAll(fines);
			data = list;
		} else {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		try {
			this.json.put("data", data.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}

	public void block(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer userId = request.getInteger("user_id");

		UserBO userBo = UserBO.getInstance(schema);
		boolean success = userBo.updateUserStatus(userId, UserStatus.BLOCKED);
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.users.success.block");
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.users.failure.block");
		}
	}

	public void unblock(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer userId = request.getInteger("user_id");

		UserBO userBo = UserBO.getInstance(schema);
		boolean success = userBo.updateUserStatus(userId, UserStatus.ACTIVE);
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "circulation.users.success.unblock");
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.users.failure.unblock");
		}
	}
}
