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
package biblivre.circulation.lending;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;

public class LendingDAO extends AbstractDAO {

	public static LendingDAO getInstance(String schema) {
		return (LendingDAO) AbstractDAO.getInstance(LendingDAO.class,	schema);
	}

	public LendingDTO get(Integer id) {
		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT * FROM lendings WHERE id = ?;";
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

	public LendingDTO getCurrentLending(HoldingDTO holding) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings WHERE ");
			sql.append("holding_id = ? AND return_date IS null ORDER BY id DESC;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, holding.getId());

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

	public Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> ids) {
		Map<Integer, LendingDTO> map = new LinkedHashMap<Integer, LendingDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings WHERE ");
			sql.append("holding_id in (");
			sql.append(StringUtils.repeat("?", ", ", ids.size()));
			sql.append(") AND return_date IS null ORDER BY id DESC;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			int index = 1;
			for (Integer id : ids) {
				pst.setInt(index++, id);
			}

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				map.put(rs.getInt("holding_id"), this.populateDTO(rs));
			}			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return map;
	}

	
	public List<LendingDTO> listHistory(HoldingDTO holding) {
		List<LendingDTO> list = new ArrayList<LendingDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings WHERE holding_id = ? ");
			sql.append("AND return_date IS NOT null ORDER BY id DESC;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, holding.getId());
			
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

	public List<LendingDTO> listLendings(UserDTO user) {
		return this.list(user, false);
	}
	
	public List<LendingDTO> listHistory(UserDTO user) {
		return this.list(user, true);
	}
	
	private List<LendingDTO> list(UserDTO user, boolean history) {
		List<LendingDTO> list = new ArrayList<LendingDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings WHERE user_id = ? ");
			sql.append("AND return_date IS ");
			if (history) {
				sql.append("NOT ");
			}
			sql.append("null ORDER BY id ASC;");
			
			PreparedStatement ppst = con.prepareStatement(sql.toString());
			ppst.setInt(1, user.getId());
			
			
			ResultSet rs = ppst.executeQuery();
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

	public Integer countLendings(UserDTO user) {
		return this.count(user, false);
	}
	
	public Integer countHistory(UserDTO user) {
		return this.count(user, true);
	}
	
	private Integer count(UserDTO user, boolean history) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) AS total FROM lendings WHERE user_id = ? ");
			sql.append("AND return_date IS ");
			if (history) {
				sql.append("NOT ");
			}
			sql.append("null ORDER BY id DESC;");
			
			PreparedStatement ppst = con.prepareStatement(sql.toString());
			ppst.setInt(1, user.getId());
			
			
			ResultSet rs = ppst.executeQuery();
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

	public List<LendingDTO> listByRecordId(int recordId) {
		List<LendingDTO> list = new ArrayList<LendingDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings l ");
			sql.append("INNER JOIN biblio_holdings h ");
			sql.append("ON l.holding_id = h.id ");
			sql.append("WHERE h.record_id = ?;");

			PreparedStatement ppst = con.prepareStatement(sql.toString());
			ppst.setInt(1, recordId);
			
			ResultSet rs = ppst.executeQuery();
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
	
	public List<LendingDTO> listLendings(int offset, int limit) {
		List<LendingDTO> list = new ArrayList<LendingDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings ");
			sql.append("WHERE return_date IS null ");
			sql.append("ORDER BY return_date ASC ");
			sql.append("LIMIT ? OFFSET ?;");
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
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
	
	public Integer countLendings() {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder countSql = new StringBuilder();
			countSql.append("SELECT count(*) as total FROM lendings ");
			countSql.append("WHERE return_date IS null;");
			PreparedStatement pstCount = con.prepareStatement(countSql.toString());
			
			ResultSet rsCount = pstCount.executeQuery();
			
			int total = 0;
			if (rsCount.next()) {
				total = rsCount.getInt("total");
			}
			
			return total;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

	}
	
	public Integer getCurrentLendingsCount(UserDTO user) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) FROM lendings ");
			sql.append("WHERE user_id = ? ");
			sql.append("AND return_date IS null;");
			
			PreparedStatement ppst = con.prepareStatement(sql.toString());
			ppst.setInt(1, user.getId());
			
			ResultSet rs = ppst.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return 0;
	}

	public boolean doLend(LendingDTO lending) {
		return this.doLend(lending, null);
	}
	
	public boolean doLend(LendingDTO lending, Connection con) {
		boolean externalCall = (con == null);
		try {
			if (externalCall) {
				con = this.getConnection();
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO lendings (holding_id, user_id, previous_lending_id, ");
			sql.append("expected_return_date, created_by) VALUES (?, ?, ?, ?, ?) ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, lending.getHoldingId());
			pst.setInt(2, lending.getUserId());
			if (lending.getPreviousLendingId() != null) {
				pst.setInt(3, lending.getPreviousLendingId());
			} else {
				pst.setNull(3, Types.INTEGER);
			}
			pst.setTimestamp(4, CalendarUtils.toSqlTimestamp(lending.getExpectedReturnDate()));
			pst.setInt(5, lending.getCreatedBy());
			
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (externalCall) {
				this.closeConnection(con);
			}
		}
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO lendings (holding_id, user_id, previous_lending_id, ");
			sql.append("expected_return_date, created_by, id, created, return_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				LendingDTO lending = (LendingDTO)abstractDto;
				pst.setInt(1, lending.getHoldingId());
				pst.setInt(2, lending.getUserId());
				if (lending.getPreviousLendingId() != null) {
					pst.setInt(3, lending.getPreviousLendingId());
				} else {
					pst.setNull(3, Types.INTEGER);
				}
				if (lending.getExpectedReturnDate() != null) { 
					pst.setDate(4, CalendarUtils.toSqlDate(lending.getExpectedReturnDate()));
				} else {
					pst.setNull(4, Types.DATE);
				}
				pst.setInt(5, lending.getCreatedBy());
				pst.setInt(6, lending.getId());
				
				if (lending.getCreated() != null) {
					pst.setTimestamp(7, CalendarUtils.toSqlTimestamp(lending.getCreated()));
				} else {
					pst.setNull(7, Types.TIMESTAMP);
				}
				
				if (lending.getReturnDate() != null) {
					pst.setTimestamp(8, CalendarUtils.toSqlTimestamp(lending.getReturnDate()));
				} else {
					pst.setNull(8, Types.DATE);
				}
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
	
	public boolean doReturn(int lendingId) {
		return this.doReturn(lendingId, null);
	}
	
	public boolean doReturn(int lendingId, Connection con) {
		boolean externalCall = (con == null);
		try {
			if (externalCall) {
				con = this.getConnection();
			}

			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE lendings ");
			sql.append("SET return_date = now() ");
			sql.append("WHERE id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, lendingId);
			
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			if (externalCall) {
				this.closeConnection(con);
			}
		}
	}

	public boolean doRenew(int lendingId, Date expectedReturnDate, int createdBy) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			this.doReturn(lendingId, con);
			
			LendingDTO oldLending = this.get(lendingId);
			oldLending.setPreviousLendingId(lendingId);
			oldLending.setExpectedReturnDate(expectedReturnDate);
			oldLending.setCreatedBy(createdBy);
			this.doLend(oldLending, con);
			
			this.commit(con);

		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			closeConnection(con);
		}
		return true;
	}

	public Integer countLentHoldings(int recordId) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) FROM lendings L INNER JOIN biblio_holdings H ");
			sql.append("ON L.holding_id = H.id ");
			sql.append("WHERE H.record_id = ? AND H.availability = 'available' AND L.return_date is NULL;");

			PreparedStatement ppst = con.prepareStatement(sql.toString());
			ppst.setInt(1, recordId);
			
			ResultSet rs = ppst.executeQuery();
			while (rs.next()) {
				return rs.getInt(1);
			}
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return null;
	}
	
	public LendingDTO getLatest(int holdingSerial, int userId) {
		LendingDTO dto = null;
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lendings ");
			sql.append("WHERE holding_id = ? AND user_id = ?");
			sql.append("ORDER BY id DESC LIMIT 1;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, holdingSerial);
			pst.setInt(2, userId);

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				dto = this.populateDTO(rs);
			}
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return dto;
	}

	private LendingDTO populateDTO(ResultSet rs) throws SQLException {
		LendingDTO dto = new LendingDTO();

		dto.setId(rs.getInt("id"));
		dto.setHoldingId(rs.getInt("holding_id"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setPreviousLendingId(rs.getInt("previous_lending_id"));
		dto.setExpectedReturnDate(rs.getTimestamp("expected_return_date"));
		dto.setReturnDate(rs.getTimestamp("return_date"));

		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));

		return dto;
	}

}
