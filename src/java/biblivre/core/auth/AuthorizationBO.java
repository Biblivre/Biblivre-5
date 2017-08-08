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
package biblivre.core.auth;

import java.util.HashMap;

import biblivre.core.AbstractBO;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.core.utils.Constants;
import biblivre.login.LoginDTO;

public class AuthorizationBO extends AbstractBO {
	private AuthorizationDAO dao;

	public static AuthorizationBO getInstance(String schema) {
		AuthorizationBO bo = AbstractBO.getInstance(AuthorizationBO.class, schema);

		if (bo.dao == null) {
			bo.dao = AuthorizationDAO.getInstance(schema);
		}

		return bo;
	}

	public void authorize(AuthorizationPoints atps, String module, String action) {
        if (atps == null) {
            atps = AuthorizationPoints.getNotLoggedInstance(this.getSchema());
        }

        if (!atps.isAllowed(module, action)) {
        	throw new AuthorizationException();
        }
    }

    public AuthorizationPoints getUserAuthorizationPoints(LoginDTO user) {
    	HashMap<String, Boolean> permissions = null;

    	// TODO: Fixme?
		if (!this.getSchema().equals(Constants.GLOBAL_SCHEMA)) {
			permissions = this.dao.getUserPermissions(user);
		}

        return new AuthorizationPoints(this.getSchema(), true, user.isEmployee(), permissions);
    }
}
