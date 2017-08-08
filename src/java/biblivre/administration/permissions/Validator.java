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

import org.apache.commons.lang3.StringUtils;

import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.login.LoginBO;

public class Validator extends AbstractValidator {
	
	public void validateSave(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		UserBO ubo = UserBO.getInstance(schema);
		
		int userId = request.getInteger("user_id");
		UserDTO udto = ubo.get(userId);
		
		String login = request.getString("new_login");
		String password = request.getString("new_password");
		String password2 = request.getString("repeat_password");

		Integer loginId = udto.getLoginId();
		boolean newLogin = (loginId == null || loginId == 0);

		LoginBO lbo = LoginBO.getInstance(schema);
		
		if (newLogin && lbo.loginExists(login)) {
			ex.addError("new_login", "login.error.login_already_exists");
		}

		if (newLogin && StringUtils.isBlank(login)) {
			ex.addError("new_login", "login.error.empty_login");
		}

		if (newLogin && StringUtils.isBlank(password)) {
			ex.addError("new_password", "login.error.empty_new_password");
		}

		if (!newLogin && !login.isEmpty()) {
			ex.addError("new_login", "login.error.user_has_login");
		}

		if (StringUtils.isNotBlank(password) && !password.equals(password2)) {
			ex.addError("repeat_password", "login.error.password_not_matching");
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
		}
				
	}	

	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("user_id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.permissions.user_not_found");
			return;
		}
	}
}
