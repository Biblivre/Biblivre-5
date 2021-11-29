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

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import biblivre.administration.backup.BackupBO;
import biblivre.administration.backup.BackupDTO;
import biblivre.administration.indexing.IndexingBO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.Updates;
import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPointTypes;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.core.utils.NaturalOrderComparator;
import biblivre.core.utils.TextUtils;

public class Handler extends AbstractHandler {

	public void login(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		String username = request.getString("username");
		String password = request.getString("password");

		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			this.message.setText(ActionResult.WARNING, "login.access_denied");
			this.jspURL = "/jsp/index.jsp";
			return;
		}

		LoginBO loginBo = LoginBO.getInstance(schema);
		LoginDTO user = loginBo.login(username, password);

		if (user != null) {
			AuthorizationBO authBo = AuthorizationBO.getInstance(schema);
			AuthorizationPoints atps = authBo.getUserAuthorizationPoints(user);

			if ((user.getId() == 1) || schema.equals(Constants.GLOBAL_SCHEMA)) {
				atps.setAdmin(true);
			}
			
			request.setSessionAttribute(schema, "logged_user", user);
			request.setSessionAttribute(schema, "logged_user_atps", atps);

			if (atps.isAdmin()) {
				boolean warningPassword = password.toLowerCase().equals("abracadabra");
				request.setSessionAttribute(schema, "system_warning_password", warningPassword);

//				String latestVersion = Updates.checkUpdates();
//				latestVersion = StringUtils.chomp(latestVersion);
//
//				if (StringUtils.isNotBlank(latestVersion) && !latestVersion.equals(Constants.BIBLIVRE_VERSION) && NaturalOrderComparator.NUMERICAL_ORDER.compare(latestVersion, Constants.BIBLIVRE_VERSION) > 0) {
//					request.setSessionAttribute(schema, "system_warning_new_version", latestVersion);
//				}
			}

			if (!schema.equals(Constants.GLOBAL_SCHEMA)) {
				if (atps.isAllowed(AuthorizationPointTypes.ADMINISTRATION_BACKUP)) {
					boolean warningBackup = false;
	
					BackupBO bbo = BackupBO.getInstance(schema);
					BackupDTO lastBackup = bbo.getLastBackup();

					if (lastBackup == null) {
						// bbo.simpleBackup();
						warningBackup = true;
					} else {
						int diff = Days.daysBetween(new DateTime(lastBackup.getCreated()), new DateTime()).getDays();
						warningBackup = (diff >= 3);
					}
	
					request.setSessionAttribute(schema, "system_warning_backup", warningBackup);
				}
	
				if (atps.isAllowed(AuthorizationPointTypes.ADMINISTRATION_INDEXING)) {
					boolean warningReindex = IndexingBO.getInstance(schema).isIndexOutdated();
					request.setSessionAttribute(schema, "system_warning_reindex", warningReindex);
				}
			}
			
			this.message.setText(ActionResult.NORMAL, "login.welcome");
			this.jspURL = "/jsp/index.jsp";
			return; 
		} else {
			this.message.setText(ActionResult.WARNING, "login.access_denied");
			this.jspURL = "/jsp/index.jsp";
			return; 
		}
	}

	public void logout(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String language = request.getLanguage();
		HttpSession session = request.getSession();

		@SuppressWarnings("unchecked")
		Enumeration<String> attributes = session.getAttributeNames();
		while (attributes.hasMoreElements()) {
			String attribute = attributes.nextElement();

			if (attribute != null && attribute.startsWith(schema + ".")) {
				session.removeAttribute(attribute);
			}
		}

		session.setAttribute(schema + ".language", language);

		this.message.setText(ActionResult.NORMAL, "login.goodbye");
		this.jspURL = "/jsp/index.jsp";
		return;
	}
	
	public void changePassword(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedId = request.getLoggedUserId();

		LoginBO lbo = LoginBO.getInstance(schema);

		String newPassword = request.getString("new_password");
		LoginDTO login = lbo.get(loggedId);
		login.setModifiedBy(loggedId);
		login.setEncPassword(TextUtils.encodePassword(newPassword));
		lbo.update(login);

		boolean warningPassword = newPassword.equals("abracadabra") && login.getLogin().equals("admin");
		request.setSessionAttribute(schema, "system_warning_password", warningPassword);

		this.message.setText(ActionResult.SUCCESS, "login.password.success");
		this.jspURL = "/jsp/administration/password.jsp";
		return;
	}
}
