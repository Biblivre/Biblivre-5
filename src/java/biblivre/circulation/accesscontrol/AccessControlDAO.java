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
package biblivre.circulation.accesscontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;

public class AccessControlDAO extends AbstractDAO {

	public static AccessControlDAO getInstance(String schema) {
		return (AccessControlDAO) AbstractDAO.getInstance(AccessControlDAO.class, schema);
	}

	public boolean save(AccessControlDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO access_control ");
			sql.append("(access_card_id, user_id, created_by) ");
			sql.append("VALUES (?, ?, ?);");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, dto.getAccessCardId());
			pst.setInt(2, dto.getUserId());
			pst.setInt(3, dto.getCreatedBy());
			
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
			sql.append("INSERT INTO access_control ");
			sql.append("(access_card_id, user_id, created_by, id) ");
			sql.append("VALUES (?, ?, ?, ?);");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				AccessControlDTO dto = (AccessControlDTO) abstractDto;
				pst.setInt(1, dto.getAccessCardId());
				pst.setInt(2, dto.getUserId());
				pst.setInt(3, dto.getCreatedBy());
				pst.setInt(4, dto.getId());
				pst.addBatch();
			}
			
			return pst.executeBatch()[0] > 0;
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public boolean update(AccessControlDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE access_control ");
			sql.append("SET departure_time = now(), ");
			sql.append("modified = now(), ");
			sql.append("modified_by = ? ");
			sql.append("WHERE id = ?;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, dto.getModifiedBy());
			pst.setInt(2, dto.getId());
			
			return pst.executeUpdate() > 0;
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public AccessControlDTO getByCardId(Integer cardId) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM access_control ");
			sql.append("WHERE access_card_id = ? AND ");
			sql.append("departure_time is null;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, cardId);
			
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return this.populateDto(rs);
			}
		
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return null;
	}

	public AccessControlDTO getByUserId(Integer userId) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM access_control ");
			sql.append("WHERE user_id = ? and ");
			sql.append("departure_time is null;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, userId);
			
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				return this.populateDto(rs);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return null;
	}

	private AccessControlDTO populateDto(ResultSet rs) throws Exception {
		AccessControlDTO dto = new AccessControlDTO();
		dto.setId(rs.getInt("id"));
		dto.setAccessCardId(rs.getInt("access_card_id"));
		dto.setUserId(rs.getInt("user_id"));
		dto.setArrivalTime(rs.getTimestamp("arrival_time"));
		dto.setDepartureTime(rs.getTimestamp("departure_time"));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}
	
}
