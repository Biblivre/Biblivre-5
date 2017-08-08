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
package biblivre.administration.permissions;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.TextUtils;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;

public class Handler extends AbstractHandler {

	public void search(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		biblivre.circulation.user.Handler userHandler = new biblivre.circulation.user.Handler();
		DTOCollection<UserDTO> userList = userHandler.searchHelper(request, response, this);

		if (userList == null || userList.size() == 0) {
			this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
			return;
		}

		DTOCollection<PermissionDTO> list = new DTOCollection<PermissionDTO>();
		list.setPaging(userList.getPaging());

		for (UserDTO user : userList) {
			list.add(this.populatePermission(schema, user));
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	
	public void open(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int userId = request.getInteger("user_id");
		if (userId == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		UserBO ubo = UserBO.getInstance(schema);
		LoginBO lbo = LoginBO.getInstance(schema);
		
		UserDTO udto = ubo.get(userId);
		if (udto == null) {
			this.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		int loginId = udto.getLoginId();

		PermissionDTO dto = new PermissionDTO();
		dto.setUser(udto);

		if (loginId > 0) {
			dto.setLogin(lbo.get(loginId));
			PermissionBO bo = PermissionBO.getInstance(schema);
			List<String> list = bo.getByLoginId(loginId);
			if (list != null) {
				dto.setPermissions(list);
			}
		}
		try {
			this.json.put("permission", dto.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int userId = request.getInteger("user_id");
		if (userId == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		UserBO ubo = UserBO.getInstance(schema);
		UserDTO udto = ubo.get(userId);

		if (udto == null) {
			this.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		String login = request.getString("new_login");
		String password = request.getString("new_password");
		boolean employee = request.getBoolean("employee", false);

		Integer loginId = udto.getLoginId();
		boolean newLogin = (loginId == null || loginId == 0);

		PermissionBO pbo = PermissionBO.getInstance(schema);
		LoginBO lbo = LoginBO.getInstance(schema);
		
		LoginDTO ldto = new LoginDTO();
		ldto.setLogin(login);
		ldto.setEmployee(employee);
		
		if (StringUtils.isNotBlank(password)) {
			ldto.setEncPassword(TextUtils.encodePassword(password));
		}
		
		boolean result = true;
		
		if (newLogin) {
			ldto.setCreatedBy(request.getLoggedUserId());
			result = lbo.save(ldto, udto);
		} else {
			ldto.setId(udto.getLoginId());
			ldto.setModifiedBy(request.getLoggedUserId());
			result = lbo.update(ldto);
		}

		String[] permissions = request.getParameterValues("permissions[]");
		
		if (permissions != null) {
			result &= pbo.save(udto.getLoginId(), Arrays.asList(permissions));
		}
		 
		if (result) {
			if (newLogin) {
				this.setMessage(ActionResult.SUCCESS, "administration.permission.success.create_login");
			} else if (!password.isEmpty()) {
				this.setMessage(ActionResult.SUCCESS, "administration.permission.success.password_saved");
			} else {
				this.setMessage(ActionResult.SUCCESS, "administration.permission.success.permissions_saved");
			}
		} else {
			this.setMessage(ActionResult.WARNING, "administration.permission.error.create_login");
		}
		
		
		PermissionDTO dto = this.populatePermission(schema, udto);
		
		try {
			this.json.put("data", dto.toJSONObject());
			this.json.put("full_data", true);
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	public void delete(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int userId = request.getInteger("user_id");
		if (userId == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		UserBO ubo = UserBO.getInstance(schema);
		UserDTO udto = ubo.get(userId);

		if (udto == null) {
			this.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		//WE DELETE THE LOGIN RECORD, AND THE PERMISSIONS WILL ALSO BE DELETED
		//BY THE LOGIN_BO
		LoginBO lbo = LoginBO.getInstance(schema);

		if (lbo.delete(udto)) {
			this.setMessage(ActionResult.SUCCESS, "administration.permission.success.delete");
		} else {
			this.setMessage(ActionResult.WARNING, "administration.permission.error.delete");
		}
	}
	
	private PermissionDTO populatePermission(String schema, UserDTO user) {
		PermissionBO pbo = PermissionBO.getInstance(schema);
		LoginBO lbo = LoginBO.getInstance(schema);
		
		PermissionDTO dto = new PermissionDTO();
		dto.setUser(user);
		
		if (user.getLoginId() == null || user.getLoginId() == 0) {
			return dto;
		}
		
		LoginDTO ldto = lbo.get(user.getLoginId());
		
		if (ldto == null) {
			return dto;
		}
		
		dto.setLogin(ldto);
		
		List<String> permissions = pbo.getByLoginId(user.getLoginId());
		
		if (permissions == null) {
			return dto;
		}
		
		dto.setPermissions(permissions);
		
		return dto;
		
	}
	
}
