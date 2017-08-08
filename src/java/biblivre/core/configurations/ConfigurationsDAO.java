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
package biblivre.core.configurations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class ConfigurationsDAO extends AbstractDAO {

	public static ConfigurationsDAO getInstance(String schema) {
		return (ConfigurationsDAO) AbstractDAO.getInstance(ConfigurationsDAO.class, schema);
	}
	
	public List<ConfigurationsDTO> list() {
		List<ConfigurationsDTO> list = new ArrayList<ConfigurationsDTO>();

		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT * FROM configurations;";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				try {
					list.add(this.populateDTO(rs));
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}

	public boolean save(List<ConfigurationsDTO> configs, int loggedUser) {
		Connection con = null;
		try {
			con = this.getConnection();
			String update = "UPDATE configurations SET value = ?, modified = now(), modified_by = ? WHERE key = ?;";
			String insert = "INSERT INTO configurations (key, value, type, required, modified_by) VALUES (?, ?, ?, ?, ?);";

			con.setAutoCommit(false);

			PreparedStatement updatePst = con.prepareStatement(update);
			PreparedStatement insertPst = con.prepareStatement(insert);

			for (ConfigurationsDTO config : configs) {
				updatePst.clearParameters();
				
				updatePst.setString(1, config.getValue());
				updatePst.setInt(2, loggedUser);
				updatePst.setString(3, config.getKey());
				
				if (updatePst.executeUpdate() == 0) {
					insertPst.clearParameters();
					
					insertPst.setString(1, config.getKey());
					insertPst.setString(2, config.getValue());
					insertPst.setString(3, config.getType());
					insertPst.setBoolean(4, config.isRequired());
					insertPst.setInt(5, loggedUser);
					
					if (insertPst.executeUpdate() == 0) {
						con.rollback();
						return false;
					}
				}
			}

			this.commit(con);
			return true;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	private ConfigurationsDTO populateDTO(ResultSet rs) throws SQLException {
		ConfigurationsDTO dto = new ConfigurationsDTO();

		dto.setKey(rs.getString("key"));
		dto.setValue(rs.getString("value"));
		dto.setType(rs.getString("type"));
		dto.setRequired(rs.getBoolean("required"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));

		return dto;
	}
}
