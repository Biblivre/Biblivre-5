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
package biblivre.administration.accesscards;

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
import biblivre.core.exceptions.DAOException;

public class AccessCardDAO extends AbstractDAO {

	public static AccessCardDAO getInstance(String schema) {
		return (AccessCardDAO) AbstractDAO.getInstance(AccessCardDAO.class, schema);
	}

	public AccessCardDTO get(String code) {
		List<String> codes = new LinkedList<String>();
		codes.add(code);

		List<AccessCardDTO> list = this.get(codes, null);
		if (list.size() == 0) {
			return null;
		}
		
		return list.get(0);
	}
		
	public List<AccessCardDTO> get(List<String> codes, List<AccessCardStatus> status) {
		List<AccessCardDTO> list = new LinkedList<AccessCardDTO>();

		boolean hasCodes = (codes != null && codes.size() > 0);
		boolean hasStatus = (status != null && status.size() > 0);
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM access_cards WHERE 1 = 1 ");
			
			if (hasCodes) {
				sql.append("and code in (");
				sql.append(StringUtils.repeat("?", ", ", codes.size()));
				sql.append(");");
			}
			
			if (hasStatus) {
				sql.append("and status in (");
				sql.append(StringUtils.repeat("?", ", ", status.size()));
				sql.append(");");
			}

			PreparedStatement pst = con.prepareStatement(sql.toString());
			int index = 1;
			if (hasCodes) {
				for (String code : codes) {
					pst.setString(index++, code);
				}
			}
			
			if (hasStatus) {
				for (AccessCardStatus stat : status) {
					pst.setString(index++, stat.toString());
				}
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
	
	public AccessCardDTO get(int id) {
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM access_cards WHERE id = ?; ");
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
	
	public DTOCollection<AccessCardDTO> search(String code, AccessCardStatus status, int limit, int offset) {
		DTOCollection<AccessCardDTO> list = new DTOCollection<AccessCardDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			StringBuilder sqlCount = new StringBuilder();
			
			sql.append("SELECT * FROM access_cards WHERE ");
			sqlCount.append("SELECT count(*) as total FROM access_cards WHERE ");
			
			if (status == null) {
				sql.append("status <> ? ");
				sqlCount.append("status <> ? ");
			} else {
				sql.append("status = ? ");
				sqlCount.append("status = ? ");
			}
			
			if (StringUtils.isNotBlank(code)) {
				sql.append("AND code ILIKE ? ");
				sqlCount.append("AND code ILIKE ? ");
			}
			sql.append("ORDER BY id ASC LIMIT ? OFFSET ?;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			PreparedStatement pstCount = con.prepareStatement(sqlCount.toString());
			
			int idx = 1;
			
			if (status == null) {
				pst.setString(idx, AccessCardStatus.CANCELLED.toString());
				pstCount.setString(idx++, AccessCardStatus.CANCELLED.toString());
			} else {
				pst.setString(idx,status.toString());
				pstCount.setString(idx++, status.toString());
			}
			
			if (StringUtils.isNotBlank(code)) {
				pst.setString(idx,"%" + code + "%");
				pstCount.setString(idx++, "%" + code + "%");
			}
			
			pst.setInt(idx++, limit);
			pst.setInt(idx++, offset);
			
			ResultSet rs = pst.executeQuery();
			ResultSet rsCount = pstCount.executeQuery();

			while (rs.next()) {
				list.add(this.populateDTO(rs));
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

	public boolean save(AccessCardDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO access_cards(code, status, created_by) ");
			sql.append("VALUES (?, ?, ?);");
			PreparedStatement pst = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, dto.getCode());
			pst.setString(2, dto.getStatus().toString());
			pst.setInt(3, dto.getCreatedBy());
			
			pst.executeUpdate();
			
			ResultSet keys = pst.getGeneratedKeys();
			if (keys.next()) {
				dto.setId(keys.getInt(1));
			}
			
			return true;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean save(LinkedList<AccessCardDTO> cardList) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO access_cards(code, status, created_by) ");
			sql.append("VALUES (?, ?, ?);");
			
			PreparedStatement pst = con.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			
			for (AccessCardDTO card : cardList) {
				pst.setString(1, card.getCode());
				pst.setString(2, card.getStatus().toString());
				pst.setInt(3, card.getCreatedBy());
				pst.addBatch();
			}
			
			pst.executeBatch();
			
			ResultSet keys = pst.getGeneratedKeys();
			if (keys.next()) {
				AccessCardDTO dto = cardList.get(0);
				dto.setId(keys.getInt(1));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return true;
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO access_cards(code, status, created_by, id) ");
			sql.append("VALUES (?, ?, ?, ?);");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				AccessCardDTO dto = (AccessCardDTO) abstractDto;
				pst.setString(1, dto.getCode());
				pst.setString(2, dto.getStatus().toString());
				pst.setInt(3, dto.getCreatedBy());
				pst.setInt(4, dto.getId());
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
	
	
	public boolean update(AccessCardDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE access_cards SET status = ?, ");
			sql.append("modified = now(), modified_by = ? ");
			sql.append("WHERE id = ?;");
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, dto.getStatus().toString());
			pst.setInt(2, dto.getModifiedBy());
			pst.setInt(3, dto.getId());
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public boolean delete(int id) {
		Connection con = null;
		try {
			con = this.getConnection();
			String sql = " DELETE FROM access_cards WHERE id = ?;";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	private AccessCardDTO populateDTO(ResultSet rs) throws SQLException {
		AccessCardDTO dto = new AccessCardDTO();
		dto.setId(rs.getInt("id"));
		dto.setCode(rs.getString("code"));
		dto.setStatus(AccessCardStatus.fromString(rs.getString("status")));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}
	

//	public AccessCardDTO get(String code) {
//		Connection con = null;
//		try {
//			con = getDataSource().getConnection();
//			String sql = "SELECT C.*, A.entrance_datetime, U.userid as userserial, U.username FROM cards C " +
//						 "LEFT JOIN access_control A ON A.serial_card = C.serial_card and A.departure_datetime is null " +
//						 "LEFT JOIN users U ON U.userid = A.serial_reader " +
//						 "WHERE C.card_number = ? AND C.status <> '" + AccessCardStatus.CANCELLED.ordinal() + "';";
//
//			PreparedStatement pst = con.prepareStatement(sql);
//			pst.setString(1, cardNumber);
//			ResultSet rs = pst.executeQuery();
//			if (rs.next()) {
//				AccessCardDTO dto = new AccessCardDTO();
//				dto.setSerialCard(rs.getInt("serial_card"));
//				dto.setCardNumber(rs.getString("card_number"));
//				dto.setStatus(AccessCardStatus.values()[rs.getInt("status")]);
//				dto.setUserid(rs.getInt("userid"));
//				dto.setDateTime(rs.getTimestamp("date_time"));
//				dto.setEntranceDatetime(rs.getTimestamp("entrance_datetime"));
//				dto.setUserSerial(rs.getInt("userserial"));
//				dto.setUserName(rs.getString("username"));
//				return dto;
//			}
//		} catch (Exception e) {
//			log.error(e);
//			throw new ExceptionUser("Exception at AdminDAO.listCards");
//		} finally {
//			closeConnection(con);
//		}
//		return null;
//	}
	
}
