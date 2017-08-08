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
package biblivre.circulation.reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;

public class ReservationDAO extends AbstractDAO {

	public static ReservationDAO getInstance(String schema) {
		return (ReservationDAO) AbstractDAO.getInstance(ReservationDAO.class,	schema);
	}

	public ReservationDTO get(Integer id) {
		Connection con = null;
		try {
			con = this.getConnection();

			String sql = "SELECT * FROM reservations WHERE id = ?;";

			PreparedStatement ppst = con.prepareStatement(sql);
			ppst.setInt(1, id);

			ResultSet rs = ppst.executeQuery();
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

	public List<ReservationDTO> list() {
		return this.list(null, null);
	}
	
	public List<ReservationDTO> list(UserDTO user, RecordDTO record) {
		List<ReservationDTO> list = new ArrayList<ReservationDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT R.* FROM reservations R INNER JOIN biblio_idx_sort S ");
			sql.append("ON S.record_id = R.record_id WHERE R.expires > localtimestamp ");
			sql.append("AND S.indexing_group_id = ? ");

			if (user != null) {
				sql.append("AND R.user_id = ? ");
			}

			if (record != null) {
				sql.append("AND R.record_id = ? ");
			}
			
			sql.append("ORDER BY S.phrase ASC;");
			
		
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			int index = 1;
			pst.setInt(index++, IndexingGroups.getDefaultSortableGroupId(this.getSchema(), RecordType.BIBLIO));
			
			if (user != null) {
				pst.setInt(index++, user.getId());
			}

			if (record != null) {
				pst.setInt(index++, record.getId());
			}


			ResultSet rs = pst.executeQuery();
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

	public int count() {
		return this.count(null, null);
	}
	
	public int count(UserDTO user, RecordDTO record) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT count(*) as total FROM reservations WHERE expires > localtimestamp ");

			if (user != null) {
				sql.append("AND user_id = ? ");
			}

			if (record != null) {
				sql.append("AND record_id = ? ");
			}
			
		
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			int index = 1;
	
			if (user != null) {
				pst.setInt(index++, user.getId());
			}

			if (record != null) {
				pst.setInt(index++, record.getId());
			}

			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getInt("total");
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		
		return 0;
	}
	
	public boolean deleteExpired() {
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sql = "DELETE FROM reservations WHERE expires < localtimestamp;";

			Statement st = con.createStatement();

			return st.executeUpdate(sql) > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public boolean delete(Integer id) {
		if (id == null) {
			return false;
		}
		
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sql = "DELETE FROM reservations WHERE id = ?;";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);

			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean delete(Integer userId, Integer recordId) {
		if (userId == null || recordId == null) {
			return false;
		}
		
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM reservations WHERE id IN ");
			sql.append("(SELECT id FROM reservations WHERE user_id = ? AND record_id = ? AND expires > localtimestamp ");
			sql.append("ORDER BY expires ASC LIMIT 1);"); // Users can reserve more than one copy of each record

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, userId);
			pst.setInt(2, recordId);

			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public int insert(ReservationDTO dto) {
		Connection con = null;		
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO reservations (record_id, user_id, expires, created_by) ");
			sql.append("VALUES (?, ?, ?, ?) ");

			PreparedStatement pst = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			pst.setInt(1, dto.getRecordId());
			pst.setInt(2, dto.getUserId());
			pst.setTimestamp(3, CalendarUtils.toSqlTimestamp(dto.getExpires()));
			pst.setInt(4, dto.getCreatedBy());
			
			pst.executeUpdate();
			
			ResultSet keys = pst.getGeneratedKeys();
			if (keys.next()) {
				dto.setId(keys.getInt(1));
			}
			
			return dto.getId();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;		
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO reservations (record_id, user_id, expires, created_by, id) ");
			sql.append("VALUES (?, ?, ?, ?, ?) ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				ReservationDTO dto = (ReservationDTO) abstractDto;
				pst.setInt(1, dto.getRecordId());
				pst.setInt(2, dto.getUserId());
				pst.setTimestamp(3, CalendarUtils.toSqlTimestamp(dto.getExpires()));
				pst.setInt(4, dto.getCreatedBy());
				pst.setInt(5, dto.getId());
				pst.addBatch();
			}
			
			pst.executeBatch();

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return true;
	}

	private ReservationDTO populateDTO(ResultSet rs) throws SQLException {
		ReservationDTO dto = new ReservationDTO();

		dto.setId(rs.getInt("id"));
		dto.setRecordId(rs.getInt("record_id"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setExpires(rs.getTimestamp("expires"));

		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));

		return dto;
	}

	public Map<Integer, List<ReservationDTO>> getReservationsMap(Set<Integer> recordIds) {
		Map<Integer, List<ReservationDTO>> map = new LinkedHashMap<Integer, List<ReservationDTO>>();
		
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM reservations WHERE ");
			sql.append("record_id in (");
			sql.append(StringUtils.repeat("?", ", ", recordIds.size()));
			sql.append(") AND expires > localtimestamp ORDER BY created ASC;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			int index = 1;
			for (Integer id : recordIds) {
				pst.setInt(index++, id);
			}

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				Integer recordId = rs.getInt("record_id");
				List<ReservationDTO> reservations = map.get(recordId);
				if (reservations == null) {
					reservations = new LinkedList<ReservationDTO>();
					map.put(recordId, reservations);
				}
				reservations.add(this.populateDTO(rs));
			}			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return map;
	}

}
