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

import java.util.List;

import biblivre.administration.permissions.PermissionBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.utils.TextUtils;

public class LoginBO extends AbstractBO {
	private LoginDAO dao;

	public static LoginBO getInstance(String schema) {
		LoginBO bo = AbstractBO.getInstance(LoginBO.class, schema);

		if (bo.dao == null) {
			bo.dao = LoginDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public final LoginDTO login(String login, String password) {
		String encodedPassword = TextUtils.encodePassword(password);

		return this.dao.login(login, encodedPassword);
	}
	
	public boolean update(LoginDTO login) {
		return this.dao.update(login);
	}
	
	public boolean delete(UserDTO user) {
		String schema = this.getSchema();
		PermissionBO pbo = PermissionBO.getInstance(schema);
		
		pbo.delete(user);
		return this.dao.delete(user);
	}	
	
	public LoginDTO get(Integer loginId) {
		return this.dao.get(loginId);
	}
	
	public boolean loginExists(String login) {
		return this.get(login) != null;
	}
	
	public LoginDTO get(String login) {
		return this.dao.getByLogin(login);
	}
	
	public boolean save(LoginDTO dto, UserDTO udto) {
		return this.dao.save(dto, udto);
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}

}
