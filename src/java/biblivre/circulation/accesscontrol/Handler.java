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
package biblivre.circulation.accesscontrol;

import java.util.Date;

import org.json.JSONException;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;

public class Handler extends AbstractHandler {
	
	public void userSearch(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		biblivre.circulation.user.Handler userHandler = new biblivre.circulation.user.Handler();
		DTOCollection<UserDTO> userList = userHandler.searchHelper(request, response, this);
		
		if (userList == null) {
			return;
		}
		
		DTOCollection<AccessControlDTO> list = new DTOCollection<AccessControlDTO>();
		list.setPaging(userList.getPaging());
		
		AccessControlBO bo = AccessControlBO.getInstance(schema);
		AccessCardBO abo = AccessCardBO.getInstance(schema);
		
		for (UserDTO user : userList) {
			AccessControlDTO dto = bo.getByUserId(user.getId());
			if (dto == null) {
				dto = new AccessControlDTO();
				dto.setUserId(user.getId());
			}

			dto.setId(user.getId());
			dto.setUser(user);
			
			if (dto.getAccessCardId() != null) {
				dto.setAccessCard(abo.get(dto.getAccessCardId()));
			}
			
			list.add(dto);
		}
		
		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
			return;
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void cardSearch(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		biblivre.administration.accesscards.Handler cardHandler = new biblivre.administration.accesscards.Handler();
		DTOCollection<AccessCardDTO> cardList = cardHandler.searchHelper(request, response, this);
		
		if (cardList == null) {
			return;
		}
		
		DTOCollection<AccessControlDTO> list = new DTOCollection<AccessControlDTO>();
		list.setPaging(cardList.getPaging());
		
		AccessControlBO bo = AccessControlBO.getInstance(schema);
		UserBO ubo = UserBO.getInstance(schema);
		
		for (AccessCardDTO card : cardList) {
			AccessControlDTO dto = bo.getByCardId(card.getId());
			if (dto == null) {
				dto = new AccessControlDTO();
				dto.setAccessCardId(card.getId());
			}

			dto.setId(card.getId());
			dto.setAccessCard(card);
			
			if (dto.getUserId() != null) {
				dto.setUser(ubo.get(dto.getUserId()));
			}
			
			list.add(dto);
		}


		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "administration.accesscards.error.no_card_found");
			return;
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}

	public void bind(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer cardId = request.getInteger("card_id");
		Integer userId = request.getInteger("user_id");

		AccessControlDTO dto = new AccessControlDTO();
		dto.setAccessCardId(cardId);
		dto.setUserId(userId);
		dto.setCreatedBy(request.getLoggedUserId());
		dto.setArrivalTime(new Date());
		
		AccessControlBO bo = AccessControlBO.getInstance(schema);
		
		if (bo.lendCard(dto)) {
			this.setMessage(ActionResult.SUCCESS, "circulation.accesscards.lend.success");
			try {
				dto = bo.getByCardId(cardId);
				bo.populateDetails(dto);
				dto.setId(cardId);
				this.json.put("data", dto.toJSONObject());
				this.json.put("full_data", true);
			} catch (JSONException e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;
			}
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.accesscards.lend.error");
		}
	}
	
	public void unbind(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer cardId = request.getInteger("card_id");
		Integer userId = request.getInteger("user_id");

		AccessControlDTO dto = new AccessControlDTO();
		dto.setAccessCardId(cardId);
		dto.setUserId(userId);
		dto.setModifiedBy(request.getLoggedUserId());
		dto.setDepartureTime(new Date());
		
		AccessControlBO bo = AccessControlBO.getInstance(schema);
		
		if (bo.returnCard(dto)) {
			this.setMessage(ActionResult.SUCCESS, "circulation.accesscards.return.success");
			try {
				bo.populateDetails(dto);
				dto.setId(cardId);
				this.json.put("data", dto.toJSONObject());
				this.json.put("full_data", true);
			} catch (JSONException e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;
			}
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.accesscards.return.error");
		}
	}	
}
