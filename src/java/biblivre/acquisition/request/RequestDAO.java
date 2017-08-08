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
package biblivre.acquisition.request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.exceptions.DAOException;

public class RequestDAO extends AbstractDAO {

	public static RequestDAO getInstance(String schema) {
		return (RequestDAO) AbstractDAO.getInstance(RequestDAO.class, schema);
	}
	
	public boolean save(RequestDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO requests ( ");
			sql.append("requester, author, item_title, item_subtitle, ");
			sql.append("edition_number, publisher, info, status, quantity, created_by) ");
			sql.append("VALUES (");
			sql.append(StringUtils.repeat("?", ", ", 10));
			sql.append(");");

			PreparedStatement pstInsert = con.prepareStatement(sql.toString());
			pstInsert.setString(1, dto.getRequester());
			pstInsert.setString(2, dto.getAuthor());
			pstInsert.setString(3, dto.getTitle());
			pstInsert.setString(4, dto.getSubtitle());
			pstInsert.setString(5, dto.getEditionNumber());
			pstInsert.setString(6, dto.getPublisher());
			pstInsert.setString(7, dto.getInfo());
			pstInsert.setString(8, dto.getStatus().toString());
			pstInsert.setInt(9, dto.getQuantity());
			pstInsert.setInt(10, dto.getCreatedBy());
			
			return pstInsert.executeUpdate() > 0;

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
			sql.append("INSERT INTO requests ( ");
			sql.append("requester, author, item_title, item_subtitle, ");
			sql.append("edition_number, publisher, info, status, quantity, created_by, id) ");
			sql.append("VALUES (");
			sql.append(StringUtils.repeat("?", ", ", 11));
			sql.append(");");

			PreparedStatement pstInsert = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				RequestDTO dto = (RequestDTO) abstractDto;
				pstInsert.setString(1, dto.getRequester());
				pstInsert.setString(2, dto.getAuthor());
				pstInsert.setString(3, dto.getTitle());
				pstInsert.setString(4, dto.getSubtitle());
				pstInsert.setString(5, dto.getEditionNumber());
				pstInsert.setString(6, dto.getPublisher());
				pstInsert.setString(7, dto.getInfo());
				pstInsert.setString(8, dto.getStatus().toString());
				pstInsert.setInt(9, dto.getQuantity());
				pstInsert.setInt(10, dto.getCreatedBy());
				pstInsert.setInt(11, dto.getId());
				pstInsert.addBatch();
			}
			
			pstInsert.executeBatch();
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return true;
	}

	public RequestDTO get(int id) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM requests ");
			sql.append("WHERE id = ?;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, id);

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

	public DTOCollection<RequestDTO> search(String value, int limit, int offset) {
		DTOCollection<RequestDTO> list = new DTOCollection<RequestDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder("SELECT * FROM requests ");
			if (StringUtils.isNotBlank(value)) {
				sql.append("WHERE requester ilike ? ");
				sql.append("OR author ilike ? ");
				sql.append("OR item_title ilike ? ");
			}
			sql.append("ORDER BY id ASC LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			int i = 1;
			if (StringUtils.isNotBlank(value)) {
				pst.setString(i++, "%" + value + "%");
				pst.setString(i++, "%" + value + "%");
				pst.setString(i++, value);
			}
			pst.setInt(i++, limit);
			pst.setInt(i++, offset);
			
			StringBuilder sqlCount = new StringBuilder("SELECT count(*) as total FROM requests ");
			if (StringUtils.isNotBlank(value)) {
				sqlCount.append("WHERE requester ilike ? ");
				sqlCount.append("OR author ilike ? ");
				sqlCount.append("OR item_title ilike ? ");
			}
			
			PreparedStatement pstCount = con.prepareStatement(sqlCount.toString());			
			if (StringUtils.isNotBlank(value)) {
				pstCount.setString(1, "%" + value + "%");
				pstCount.setString(2, "%" + value + "%");
				pstCount.setString(3, value);
			}			
			
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				list.add(this.populateDto(rs));
			}
			
			ResultSet rsCount = pstCount.executeQuery();
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

	public boolean update(RequestDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE requests ");
			sql.append("SET author = ?, item_title = ?, item_subtitle = ?, edition_number = ?, ");
			sql.append("publisher = ?, info = ?, ");
			sql.append("requester = ?, quantity = ?, modified = now(), modified_by = ? ");
			sql.append("WHERE id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, dto.getAuthor());
			pst.setString(2, dto.getTitle());
			pst.setString(3, dto.getSubtitle());
			pst.setString(4, dto.getEditionNumber());
			pst.setString(5, dto.getPublisher());
			pst.setString(6, dto.getInfo());
			pst.setString(7, dto.getRequester());
			pst.setInt(8, dto.getQuantity());
			pst.setInt(9, dto.getModifiedBy());
			pst.setInt(10, dto.getId());
			return pst.executeUpdate() > 0;
	
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

	}

	public boolean updateRequestStatus(int orderId, RequestStatus status) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE requests SET status = ? ");
			sql.append("WHERE id IN (");
			sql.append("SELECT r.id FROM requests r ");
			sql.append("INNER JOIN request_quotation rq ");
			sql.append("ON rq.request_id = r.id ");
			sql.append("INNER JOIN quotation q ");
			sql.append("ON q.id = rq.quotation_id ");
			sql.append("INNER JOIN order o ");
			sql.append("ON o.quotation_id = q.id ");
			sql.append("WHERE o.id = ?); ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, status.toString());
			pst.setInt(2, orderId);
			
			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}


	public boolean delete(RequestDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM requests ");
			sql.append("WHERE id = ?; ");
			
			PreparedStatement pstInsert = con.prepareStatement(sql.toString());
			pstInsert.setInt(1, dto.getId());
			
			return pstInsert.executeUpdate() > 0;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

	}


	private RequestDTO populateDto(ResultSet rs) throws Exception {
		RequestDTO dto = new RequestDTO();
		dto.setId(rs.getInt("id"));
		dto.setRequester(rs.getString("requester"));
		dto.setAuthor(rs.getString("author"));
		dto.setTitle(rs.getString("item_title"));
		dto.setSubtitle(rs.getString("item_subtitle"));
		dto.setEditionNumber(rs.getString("edition_number"));
		dto.setPublisher(rs.getString("publisher"));
		dto.setInfo(rs.getString("info"));
		dto.setStatus(RequestStatus.fromString(rs.getString("status")));
		dto.setQuantity(rs.getInt("quantity"));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

}
