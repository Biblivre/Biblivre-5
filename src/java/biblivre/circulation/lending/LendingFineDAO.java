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
import java.util.List;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.CalendarUtils;

public class LendingFineDAO extends AbstractDAO {

	public static LendingFineDAO getInstance(String schema) {
		return (LendingFineDAO) AbstractDAO.getInstance(LendingFineDAO.class, schema);
	}

	public boolean insert(LendingFineDTO fine) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO lending_fines ");
			sql.append("(user_id, lending_id, fine_value, payment_date, created_by) ");
			sql.append("VALUES (?, ?, ?, ?, ?);");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, fine.getUserId());
			pst.setInt(2, fine.getLendingId());
			pst.setFloat(3, fine.getValue());
			if (fine.getPayment() != null) {
				pst.setDate(4, CalendarUtils.toSqlDate(fine.getPayment()));
			} else {
				pst.setNull(4, Types.DATE);
			}
			pst.setInt(5, fine.getCreatedBy());
			
			return pst.executeUpdate() > 0;

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
			sql.append("INSERT INTO lending_fines ");
			sql.append("(user_id, lending_id, fine_value, payment_date, created_by, id) ");
			sql.append("VALUES (?, ?, ?, ?, ?, ?);");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				LendingFineDTO fine = (LendingFineDTO) abstractDto;
				pst.setInt(1, fine.getUserId());
				pst.setInt(2, fine.getLendingId());
				pst.setFloat(3, fine.getValue());
				if (fine.getPayment() != null) {
					pst.setDate(4, CalendarUtils.toSqlDate(fine.getPayment()));
				} else {
					pst.setNull(4, Types.DATE);
				}
				pst.setInt(5, fine.getCreatedBy());
				pst.setInt(6, fine.getId());
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
	
	public LendingFineDTO get(Integer lendingFineId) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sql = "SELECT * FROM lending_fines WHERE id = ?;";
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, lendingFineId);
			
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

	public LendingFineDTO getByLendingId(Integer lendingId) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sql = "SELECT * FROM lending_fines WHERE lending_id = ?;";
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, lendingId);
			
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

	public List<LendingFineDTO> list(UserDTO user, boolean pendingOnly) {
		List<LendingFineDTO> list = new ArrayList<LendingFineDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lending_fines WHERE user_id = ? ");
			if (pendingOnly) {
				sql.append("AND payment is null ");
			}
			sql.append("ORDER BY id DESC;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, user.getId());
			
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				list.add(populateDTO(rs));
			}
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}

	public boolean update(LendingFineDTO fine) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE lending_fines SET payment_date = now(), ");
			sql.append("fine_value = ? WHERE id = ?; ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setFloat(1, fine.getValue());
			pst.setInt(2, fine.getId());
			
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	private LendingFineDTO populateDTO(ResultSet rs) throws SQLException {
		LendingFineDTO dto = new LendingFineDTO();

		dto.setId(rs.getInt("id"));
		dto.setLendingId(rs.getInt("lending_id"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setValue(rs.getFloat("fine_value"));
		dto.setPayment(rs.getTimestamp("payment_date"));
		
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));

		return dto;
	}

}
