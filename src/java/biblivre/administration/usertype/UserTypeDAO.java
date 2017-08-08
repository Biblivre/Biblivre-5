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
package biblivre.administration.usertype;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;

public class UserTypeDAO extends AbstractDAO {
	public static UserTypeDAO getInstance(String schema) {
		return (UserTypeDAO) AbstractDAO.getInstance(UserTypeDAO.class, schema);
	}
	
	public UserTypeDTO get(int id) {		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM users_types ");
			sql.append("WHERE id = ? ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return this.populateDTO(rs);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
        return null;
	}

	public DTOCollection<UserTypeDTO> search(String value, int limit, int offset) {
		DTOCollection<UserTypeDTO> list = new DTOCollection<UserTypeDTO>();
		
		if (value != null) {
			value = TextUtils.removeDiacriticals(value);
		}
		
		if (limit == 0) {
			limit = Configurations.getInt(this.getSchema(), Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 25);
		}
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM users_types ");

			StringBuilder countSql = new StringBuilder();
			countSql.append("SELECT count(*) as total FROM users_types ");

			if (StringUtils.isNotBlank(value)) {
				sql.append("WHERE name ilike ? ");
				countSql.append("WHERE name ilike ? ");
			}
			
			sql.append("ORDER BY name ASC ");
			sql.append("LIMIT ? OFFSET ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			PreparedStatement pstCount = con.prepareStatement(countSql.toString());
			int psIndex = 1;

			if (StringUtils.isNotBlank(value)) {
				pst.setString(psIndex, "%" + value + "%");
				pstCount.setString(psIndex, "%" + value + "%");
				psIndex++;
			}

			pst.setInt(psIndex++, limit);
			pst.setInt(psIndex++, offset);

			ResultSet rs = pst.executeQuery();
			ResultSet rsCount = pstCount.executeQuery();

			while (rs.next()) {
				UserTypeDTO userType = this.populateDTO(rs);
				list.add(userType);
			}

			if (rsCount.next()) {
				int total = rsCount.getInt("total");

				PagingDTO paging = new PagingDTO(total, limit, offset);
				list.setPaging(paging);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
        return list;
		
	}
	
	public List<UserTypeDTO> list() {
		List<UserTypeDTO> list = new LinkedList<UserTypeDTO>();

		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT * FROM users_types ORDER BY name, id;";

			Statement st = con.createStatement();

			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(this.populateDTO(rs));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}

    public boolean save(UserTypeDTO dto) {
        Connection con = null;
        try {
            con = this.getConnection();
            StringBuilder sql = new StringBuilder();
            
            PreparedStatement pst = null;
			Boolean newType = dto.getId() == null || dto.getId() == 0;
			if (newType) {
				dto.setId(this.getNextSerial("users_id_seq"));
	            sql.append("INSERT INTO users_types (name, description, lending_limit, reservation_limit, ");
	            sql.append("lending_time_limit, reservation_time_limit, fine_value, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?); ");
	            pst = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			} else {
				sql.append("UPDATE users_types SET modified = now(), ");
				sql.append("name = ?, description = ?, lending_limit = ?, reservation_limit = ?, ");
				sql.append("lending_time_limit = ?, reservation_time_limit = ?, fine_value = ?, modified_by = ? ");
				sql.append("WHERE id = ?;");
				pst = con.prepareStatement(sql.toString());
			}
			
            pst.setString(1, dto.getName());
            pst.setString(2, dto.getDescription());
            pst.setInt(3, dto.getLendingLimit());
            pst.setInt(4, dto.getReservationLimit());
            pst.setInt(5, dto.getLendingTimeLimit());
            pst.setInt(6, dto.getReservationTimeLimit());
            pst.setFloat(7, dto.getFineValue());
            if (newType) {
            	pst.setInt(8, dto.getCreatedBy());
            } else {
            	pst.setInt(8, dto.getModifiedBy());
            	pst.setInt(9, dto.getId());
            }
            
            pst.executeUpdate();
            
            if (newType) {
				ResultSet keys = pst.getGeneratedKeys();
				if (keys.next()) {
					dto.setId(keys.getInt(1));
				}
            }
            
			return true;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeConnection(con);
        }
    }
    
    public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
        Connection con = null;
        try {
            con = this.getConnection();
            
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO users_types (name, description, lending_limit, reservation_limit, ");
            sql.append("lending_time_limit, reservation_time_limit, fine_value, created_by, id) VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?); ");
            
            PreparedStatement pst = con.prepareStatement(sql.toString());
			
            for (AbstractDTO abstractDto : dtoList) {
				UserTypeDTO dto = (UserTypeDTO) abstractDto;
	            pst.setString(1, dto.getName());
	            pst.setString(2, dto.getDescription());
	            pst.setInt(3, dto.getLendingLimit());
	            pst.setInt(4, dto.getReservationLimit());
	            pst.setInt(5, dto.getLendingTimeLimit());
	            pst.setInt(6, dto.getReservationTimeLimit());
            	pst.setInt(7, dto.getCreatedBy());
            	pst.setInt(8, dto.getId());
            	pst.addBatch();
            }
            
            pst.executeBatch();
            
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeConnection(con);
        }
		return true;
	}

    public boolean delete(int id) {
        Connection con = null;
        try {
            con = this.getConnection();

            String sql = "DELETE FROM users_types WHERE id = ?;";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            closeConnection(con);
        }
    }

	
	private UserTypeDTO populateDTO(ResultSet rs) throws SQLException {
		UserTypeDTO dto = new UserTypeDTO();

		dto.setId(rs.getInt("id"));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		
		dto.setName(rs.getString("name"));
		dto.setDescription(rs.getString("description"));
		dto.setLendingLimit(rs.getInt("lending_limit"));
		dto.setReservationLimit(rs.getInt("reservation_limit"));
		dto.setLendingTimeLimit(rs.getInt("lending_time_limit"));
		dto.setReservationTimeLimit(rs.getInt("reservation_time_limit"));
		dto.setFineValue(rs.getFloat("fine_value"));

		return dto;
	}
}
