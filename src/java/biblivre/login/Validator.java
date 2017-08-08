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
package biblivre.login;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;

public class Validator extends AbstractValidator {
	
	public void validateChangePassword(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		String currentPassword = request.getString("current_password");
		String newPassword = request.getString("new_password");
		String repeatPassword = request.getString("repeat_password");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (StringUtils.isBlank(currentPassword)) {
			ex.addError("current_password", "field.error.required");
		}

		if (StringUtils.isBlank(newPassword)) {
			ex.addError("new_password", "field.error.required");
		}

		if (StringUtils.isBlank(repeatPassword)) {
			ex.addError("repeat_password", "field.error.required");
		}
		
		if (newPassword.length() < 3) {
			ex.addError("new_password", "field.error.min_length:3");
		}

		if (!newPassword.equals(repeatPassword)) {
			ex.addError("repeat_password", "login.error.password_not_matching");
		}

		if (newPassword.equals(currentPassword)) {
			ex.addError("new_password", "login.error.same_password");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
		
		LoginBO lbo = LoginBO.getInstance(schema);
		
		int loggedId = request.getLoggedUserId();
		LoginDTO login = lbo.get(loggedId);
		if (login == null) {
			handler.setMessage(ActionResult.WARNING, "error.invalid_user");
			return;
		}

		LoginDTO checkLogin = lbo.login(login.getLogin(), currentPassword);
		if (checkLogin == null) {
			ex.addError("current_password", "login.error.invalid_password");
			handler.setMessage(ex);
			return;
		}
	}
}
