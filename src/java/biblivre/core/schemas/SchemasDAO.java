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
package biblivre.core.schemas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import org.postgresql.core.BaseConnection;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class SchemasDAO extends AbstractDAO {

	public static SchemasDAO getInstance(String schema) {
		return (SchemasDAO) AbstractDAO.getInstance(SchemasDAO.class, schema);
	}
	
	public Set<SchemaDTO> list() {
		Set<SchemaDTO> set = new TreeSet<SchemaDTO>();

		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT * FROM schemas;";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				try {
					set.add(this.populateDTO(rs));
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return set;
	}
	
	public boolean insert(SchemaDTO dto) {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "INSERT INTO schemas (schema, name) VALUES (?, ?);";
			
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, dto.getSchema());
			pst.setString(2, dto.getName());

			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean delete(SchemaDTO dto) {
		Connection con = null;

		try {
			con = this.getConnection();
			con.setAutoCommit(false);

			String sql = "DELETE FROM schemas WHERE schema = ? AND name = ?;";
			
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, dto.getSchema());
			pst.setString(2, dto.getName());

			boolean success = pst.executeUpdate() > 0;

			if (success) {
				String escaped = ((BaseConnection) this.getPGConnection(con)).escapeString(dto.getSchema());
				String dropSql = "DROP SCHEMA \"" + escaped + "\" CASCADE;";
				Statement dropSt = con.createStatement();
	
				dropSt.executeUpdate(dropSql);
			}
			
			this.commit(con);
			
			return success;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean save(SchemaDTO dto) {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "UPDATE schemas SET name = ?, disabled = ? WHERE schema = ?;";
			
			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, dto.getName());
			pst.setBoolean(2, dto.isDisabled());
			pst.setString(3, dto.getSchema());
			
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

    private SchemaDTO populateDTO(ResultSet rs) throws SQLException {
    	SchemaDTO dto = new SchemaDTO();

        dto.setSchema(rs.getString("schema"));
        dto.setName(rs.getString("name"));
        dto.setDisabled(rs.getBoolean("disabled"));

        return dto;
    }
}
