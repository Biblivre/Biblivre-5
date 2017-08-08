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
package biblivre.acquisition.quotation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.exceptions.DAOException;

public class QuotationDAO extends AbstractDAO {

	public static QuotationDAO getInstance(String schema) {
		return (QuotationDAO) AbstractDAO.getInstance(QuotationDAO.class, schema);
	}

	public Integer save(QuotationDTO dto) {
		
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			int quotationId = this.getNextSerial("quotations_id_seq");
			dto.setId(quotationId);
			
			StringBuilder sqlQuotations = new StringBuilder();
			sqlQuotations.append("INSERT INTO quotations (id, supplier_id, ");
			sqlQuotations.append("response_date, expiration_date, delivery_time, ");
			sqlQuotations.append("info, created_by, created) ");
			sqlQuotations.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

			PreparedStatement pstQuotations = con.prepareStatement(sqlQuotations.toString());
			pstQuotations.setInt(1, dto.getId());
			pstQuotations.setInt(2, dto.getSupplierId());
			pstQuotations.setDate(3, new java.sql.Date(dto.getResponseDate().getTime()));
			pstQuotations.setDate(4, new java.sql.Date(dto.getExpirationDate().getTime()));
			pstQuotations.setInt(5, dto.getDeliveryTime());
			pstQuotations.setString(6, dto.getInfo());
			pstQuotations.setInt(7, dto.getCreatedBy());
			pstQuotations.setDate(8, new java.sql.Date(dto.getCreated().getTime()));
			
			pstQuotations.executeUpdate();
			
			StringBuilder sqlRQuotations = new StringBuilder();
			sqlRQuotations.append("INSERT INTO request_quotation ");
			sqlRQuotations.append("(request_id, quotation_id, quotation_quantity, unit_value, ");
			sqlRQuotations.append("response_quantity) ");
			sqlRQuotations.append("VALUES (?, ?, ?, ?, ?);");

			PreparedStatement pstRQuotations = con.prepareStatement(sqlRQuotations.toString());
			
			for (RequestQuotationDTO rqdto : dto.getQuotationsList()) {
				pstRQuotations.setInt(1, rqdto.getRequestId());
				pstRQuotations.setInt(2, quotationId);
				pstRQuotations.setInt(3, rqdto.getQuantity());
				pstRQuotations.setFloat(4, rqdto.getUnitValue());
				pstRQuotations.setInt(5, rqdto.getResponseQuantity());
				pstRQuotations.addBatch();
			}

			pstRQuotations.executeBatch();
			
			con.commit();
			return quotationId;
			
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		if (dtoList == null || dtoList.isEmpty()) {
			return true;
		}
		
		AbstractDTO abstractDto = dtoList.get(0);
		
		if (abstractDto instanceof QuotationDTO) {
			return this.saveQuotationFromBiblivre3(dtoList);
		} else if (abstractDto instanceof RequestQuotationDTO) {
			return this.saveRequestQuotationFromBiblivre3(dtoList);
		} else {
			throw new IllegalArgumentException("List is not of QuotationDTO or RequestQuotationDTO objects.");
		}
		
	}
	
	private boolean saveQuotationFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			StringBuilder sqlQuotations = new StringBuilder();
			sqlQuotations.append("INSERT INTO quotations (id, supplier_id, ");
			sqlQuotations.append("response_date, expiration_date, delivery_time, ");
			sqlQuotations.append("info, created_by, created) ");
			sqlQuotations.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

			PreparedStatement pstQuotations = con.prepareStatement(sqlQuotations.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				QuotationDTO dto = (QuotationDTO) abstractDto;
				pstQuotations.setInt(1, dto.getId());
				pstQuotations.setInt(2, dto.getSupplierId());
				pstQuotations.setDate(3, new java.sql.Date(dto.getResponseDate().getTime()));
				pstQuotations.setDate(4, new java.sql.Date(dto.getExpirationDate().getTime()));
				pstQuotations.setInt(5, dto.getDeliveryTime());
				pstQuotations.setString(6, dto.getInfo());
				pstQuotations.setInt(7, dto.getCreatedBy());
				pstQuotations.setDate(8, new java.sql.Date(dto.getCreated().getTime()));
				pstQuotations.addBatch();
			}
			
			pstQuotations.executeBatch();
			
			con.commit();
			
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return true;
	}
	
	private boolean saveRequestQuotationFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			StringBuilder sqlRQuotations = new StringBuilder();
			sqlRQuotations.append("INSERT INTO request_quotation ");
			sqlRQuotations.append("(request_id, quotation_id, quotation_quantity, unit_value, ");
			sqlRQuotations.append("response_quantity) ");
			sqlRQuotations.append("VALUES (?, ?, ?, ?, ?);");

			PreparedStatement pstRQuotations = con.prepareStatement(sqlRQuotations.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				RequestQuotationDTO rqdto = (RequestQuotationDTO) abstractDto;
				pstRQuotations.setInt(1, rqdto.getRequestId());
				pstRQuotations.setInt(2, rqdto.getQuotationId());
				pstRQuotations.setInt(3, rqdto.getQuantity());
				pstRQuotations.setFloat(4, rqdto.getUnitValue());
				pstRQuotations.setInt(5, rqdto.getResponseQuantity());
				pstRQuotations.addBatch();
			}
			
			pstRQuotations.executeBatch();
			
			con.commit();
			
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return true;
	}


	public boolean update(QuotationDTO dto) {
		
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
						
			StringBuilder sqlQuotations = new StringBuilder();
			sqlQuotations.append("UPDATE quotations SET supplier_id = ?, ");
			sqlQuotations.append("response_date = ?, expiration_date = ?, delivery_time = ?, ");
			sqlQuotations.append("info = ?, modified_by = ?, modified = now() ");
			sqlQuotations.append("WHERE id = ?;");

			PreparedStatement pstQuotations = con.prepareStatement(sqlQuotations.toString());
			pstQuotations.setInt(1, dto.getSupplierId());
			pstQuotations.setDate(2, new java.sql.Date(dto.getResponseDate().getTime()));
			pstQuotations.setDate(3, new java.sql.Date(dto.getExpirationDate().getTime()));
			pstQuotations.setInt(4, dto.getDeliveryTime());
			pstQuotations.setString(5, dto.getInfo());
			pstQuotations.setInt(6, dto.getModifiedBy());
			pstQuotations.setInt(7, dto.getId());
			
			pstQuotations.executeUpdate();
			
			StringBuilder sqlItemQuotations = new StringBuilder();
			sqlItemQuotations.append("DELETE FROM request_quotation ");
			sqlItemQuotations.append("WHERE quotation_id = ?;");
			PreparedStatement pstItemQuotations = con.prepareStatement(sqlItemQuotations.toString());
			pstItemQuotations.setInt(1, dto.getId());
			pstItemQuotations.executeUpdate();
			
			StringBuilder sqlRQuotations = new StringBuilder();
			sqlRQuotations.append("INSERT INTO request_quotation ");
			sqlRQuotations.append("(request_id, quotation_id, quotation_quantity, unit_value, ");
			sqlRQuotations.append("response_quantity) ");
			sqlRQuotations.append("VALUES (?, ?, ?, ?, ?);");

			PreparedStatement pstRQuotations = con.prepareStatement(sqlRQuotations.toString());
			
			for (RequestQuotationDTO rqdto : dto.getQuotationsList()) {
				pstRQuotations.setInt(1, rqdto.getRequestId());
				pstRQuotations.setInt(2, dto.getId());
				pstRQuotations.setInt(3, rqdto.getQuantity());
				pstRQuotations.setFloat(4, rqdto.getUnitValue());
				pstRQuotations.setInt(5, rqdto.getResponseQuantity());
				pstRQuotations.addBatch();
			}

			pstRQuotations.executeBatch();
			
			con.commit();
			
			return true;
			
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		
	}
	
	public QuotationDTO get(int id) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM quotations ");
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
	
	public List<RequestQuotationDTO> listRequestQuotation(int quotationId) {
		List<RequestQuotationDTO> list = new ArrayList<RequestQuotationDTO>();
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM request_quotation ");
			sql.append("WHERE quotation_id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, quotationId);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				list.add(this.populateRequestQuotationDto(rs));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public boolean delete(QuotationDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM quotations ");
			sql.append("WHERE id = ?;");

			PreparedStatement pstInsert = con.prepareStatement(sql.toString());
			pstInsert.setInt(1, dto.getId());

			return pstInsert.executeUpdate() > 0;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public DTOCollection<QuotationDTO> search(String value, int limit, int offset) {
		DTOCollection<QuotationDTO> list = new DTOCollection<QuotationDTO>();

		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder("SELECT * FROM quotations q ");
			if (StringUtils.isNumeric(value)) {
				sql.append("WHERE id = ? ");
			} else {
				sql.append("INNER JOIN suppliers s ON q.supplier_id = s.id ");
				sql.append("WHERE trademark ilike ? ");
			}
			sql.append("ORDER BY q.id ASC LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			int i = 1;
			if (StringUtils.isNumeric(value)) {
				pst.setInt(i++, Integer.valueOf(value));
			} else {
				pst.setString(i++, "%" + value + "%");
			}
			pst.setInt(i++, limit);
			pst.setInt(i++, offset);
			
			StringBuilder sqlCount = new StringBuilder("SELECT count(*) as total FROM quotations q ");
			if (StringUtils.isNumeric(value)) {
				sql.append("WHERE id = ? ");
			} else {
				sql.append("INNER JOIN suppliers s ON q.supplier_id = s.id ");
				sql.append("WHERE trademark ilike ? ");
			}
			
			PreparedStatement pstCount = con.prepareStatement(sqlCount.toString());			
			if (StringUtils.isNumeric(value)) {
				pst.setInt(1, Integer.valueOf(value));
			} else {
				pst.setString(1, "%" + value + "%");
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
	
	public DTOCollection<QuotationDTO> list(Integer supplierId) {
		DTOCollection<QuotationDTO> list = new DTOCollection<QuotationDTO>();

		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder("SELECT * FROM quotations ");
			//Add 1 day to expiration_date, else it'll check for expiration_date at midnight (00:00H)
			//and it won't find quotations for the same day
			//sql.append("WHERE supplier_id = ? AND expiration_date + interval '1 day' >= now(); ");
			sql.append("WHERE supplier_id = ? AND expiration_date >= now()::date; ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, supplierId);
			
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				list.add(this.populateDto(rs));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}

	private QuotationDTO populateDto(ResultSet rs) throws Exception {
		QuotationDTO dto = new QuotationDTO();
		
		dto.setId(rs.getInt("id"));
		dto.setSupplierId(rs.getInt("supplier_id"));
		dto.setResponseDate(rs.getDate("response_date"));
		dto.setExpirationDate(rs.getDate("expiration_date"));
		dto.setDeliveryTime(rs.getInt("delivery_time"));
		dto.setInfo(rs.getString("info"));
		
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

	private RequestQuotationDTO populateRequestQuotationDto(ResultSet rs) throws Exception {
		RequestQuotationDTO dto = new RequestQuotationDTO();
		
		dto.setRequestId(rs.getInt("request_id"));
		dto.setQuotationId(rs.getInt("quotation_id"));
		dto.setQuantity(rs.getInt("quotation_quantity"));
		dto.setUnitValue(rs.getFloat("unit_value"));
		dto.setResponseQuantity(rs.getInt("response_quantity"));
		
		return dto;
	}
}
