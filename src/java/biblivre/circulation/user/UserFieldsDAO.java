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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class UserFieldsDAO extends AbstractDAO {
	
	public static UserFieldsDAO getInstance(String schema) {
		return (UserFieldsDAO) AbstractDAO.getInstance(UserFieldsDAO.class, schema);
	}

	public List<UserFieldDTO> listFields() {
		List<UserFieldDTO> list = new ArrayList<UserFieldDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();
			String sqlDatafields = "SELECT * FROM users_fields ORDER BY \"sort_order\";";
			
			Statement pst = con.createStatement();
			ResultSet rs = pst.executeQuery(sqlDatafields);			
			while (rs.next()) {
				UserFieldDTO userField = this.populateUserFieldDTO(rs);
				//BACALHAAAAAAU
				if (userField.getKey().equalsIgnoreCase("birthday")) {
					userField.setType(UserFieldType.DATE);
				}
				list.add(userField);
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}
	
	private UserFieldDTO populateUserFieldDTO(ResultSet rs) throws SQLException {
		UserFieldDTO dto = new UserFieldDTO();

		dto.setKey(rs.getString("key"));
		dto.setType(UserFieldType.fromString(rs.getString("type")));
		dto.setRequired(rs.getBoolean("required"));
		dto.setMaxLength(rs.getInt("max_length"));
		dto.setSortOrder(rs.getInt("sort_order"));

		return dto;
	}
}
