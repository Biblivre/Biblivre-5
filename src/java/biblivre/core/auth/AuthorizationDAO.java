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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.login.LoginDTO;

public class AuthorizationDAO extends AbstractDAO {

	public static AuthorizationDAO getInstance(String schema) {
		return (AuthorizationDAO) AbstractDAO.getInstance(AuthorizationDAO.class, schema);
	}

	public HashMap<String, Boolean> getUserPermissions(LoginDTO user) {
		Connection con = null;
		HashMap<String, Boolean> hash = new HashMap<String, Boolean>();

		try {
			con = this.getConnection();
			String sql = "SELECT permission FROM permissions WHERE login_id = ?;";
			
			PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, user.getId());
            
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
                hash.put(rs.getString("permission"), Boolean.TRUE);
            }
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return hash;
	}
}
