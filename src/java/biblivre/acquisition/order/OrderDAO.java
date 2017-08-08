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
package biblivre.acquisition.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.exceptions.DAOException;

public class OrderDAO extends AbstractDAO {

	public static OrderDAO getInstance(String schema) {
		return (OrderDAO) AbstractDAO.getInstance(OrderDAO.class, schema);
	}
	
	 public OrderDTO get(Integer orderId) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sql = " SELECT * FROM orders WHERE id = ?; ";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, orderId);
			
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

	
	public Integer save(OrderDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			int orderId = this.getNextSerial("orders_id_seq");
			
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO orders (quotation_id, created, ");
			sql.append(" created_by, info, status, invoice_number, ");
			sql.append(" receipt_date, total_value, delivered_quantity, ");
			sql.append(" terms_of_payment, deadline_date, id) ");
			sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ");
			String sqlInsert = sql.toString();
			
			PreparedStatement pst = con.prepareStatement(sqlInsert);
			pst.setInt(1, dto.getQuotationId());
			pst.setDate(2, new java.sql.Date(dto.getCreated().getTime()));
			pst.setInt(3, dto.getCreatedBy());
			pst.setString(4, dto.getInfo());
			pst.setString(5, dto.getStatus());
			pst.setString(6, dto.getInvoiceNumber());

			Date receiptDate = dto.getReceiptDate();
			if (receiptDate != null) {
				pst.setDate(7, new java.sql.Date(receiptDate.getTime()));
			} else {
				pst.setNull(7, java.sql.Types.DATE);
			}

			Float totalValue = dto.getTotalValue();
			if (totalValue != null) {
				pst.setFloat(8, totalValue);
			} else {
				pst.setNull(8, java.sql.Types.FLOAT);
			}

			Integer deliveryQuantity = dto.getDeliveredQuantity();
			if (deliveryQuantity != null) {
				pst.setInt(9, deliveryQuantity);
			} else {
				pst.setNull(9, java.sql.Types.INTEGER);
			}

			pst.setString(10, dto.getTermsOfPayment());
			pst.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
			pst.setInt(12, orderId);
			return pst.executeUpdate() > 0 ? orderId : 0;
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
			sql.append(" INSERT INTO orders (quotation_id, created, ");
			sql.append(" created_by, info, status, invoice_number, ");
			sql.append(" receipt_date, total_value, delivered_quantity, ");
			sql.append(" terms_of_payment, deadline_date, id) ");
			sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (AbstractDTO abstractDto : dtoList) {
				OrderDTO dto = (OrderDTO) abstractDto;
				pst.setInt(1, dto.getQuotationId());
				pst.setDate(2, new java.sql.Date(dto.getCreated().getTime()));
				pst.setInt(3, dto.getCreatedBy());
				pst.setString(4, dto.getInfo());
				pst.setString(5, dto.getStatus());
				pst.setString(6, dto.getInvoiceNumber());

				Date receiptDate = dto.getReceiptDate();
				if (receiptDate != null) {
					pst.setDate(7, new java.sql.Date(receiptDate.getTime()));
				} else {
					pst.setNull(7, java.sql.Types.DATE);
				}

				Float totalValue = dto.getTotalValue();
				if (totalValue != null) {
					pst.setFloat(8, totalValue);
				} else {
					pst.setNull(8, java.sql.Types.FLOAT);
				}

				Integer deliveryQuantity = dto.getDeliveredQuantity();
				if (deliveryQuantity != null) {
					pst.setInt(9, deliveryQuantity);
				} else {
					pst.setNull(9, java.sql.Types.INTEGER);
				}

				pst.setString(10, dto.getTermsOfPayment());
				pst.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
				pst.setInt(12, dto.getId());
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

	public ArrayList<OrderDTO> listBuyOrders(String status, int offset, int limit) {
		ArrayList<OrderDTO> requestList = new ArrayList<OrderDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			boolean setStatus = StringUtils.isNotBlank(status) && (status.equals("0") || status.equals("1"));
			StringBuilder sql = new StringBuilder(" SELECT * FROM orders ");
			if (setStatus) {
				sql.append(" WHERE status = ? ");
			}
			sql.append(" ORDER BY created ASC offset ? limit ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			int i = 1;
			if (setStatus) {
				pst.setString(i++, status);
			}
			pst.setInt(i++, offset);
			pst.setInt(i++, limit);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				OrderDTO dto = this.populateDto(rs);
				requestList.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return requestList;
	}

	public boolean update(OrderDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE orders ");
			sql.append(" SET quotation_id = ?, created = ?, ");
			sql.append(" created_by = ?, info = ?, status = ?, ");
			sql.append(" invoice_number = ?, receipt_date = ?, total_value = ?, ");
			sql.append(" delivered_quantity = ?, terms_of_payment = ?, deadline_date= ? ");
			sql.append(" WHERE id = ?;");
			String sqlInsert = sql.toString();

			PreparedStatement pst = con.prepareStatement(sqlInsert);
			pst.setInt(1, dto.getQuotationId());
			pst.setDate(2, new java.sql.Date(dto.getCreated().getTime()));
			pst.setInt(3, dto.getCreatedBy());
			pst.setString(4, dto.getInfo());
			pst.setString(5, dto.getStatus());
			pst.setString(6, dto.getInvoiceNumber());

			Date receiptDate = dto.getReceiptDate();
			if (receiptDate != null) {
				pst.setDate(7, new java.sql.Date(receiptDate.getTime()));
			} else {
				pst.setNull(7, java.sql.Types.DATE);
			}

			Float totalValue = dto.getTotalValue();
			if (totalValue != null) {
				pst.setFloat(8, totalValue);
			} else {
				pst.setNull(8, java.sql.Types.FLOAT);
			}

			Integer deliveryQuantity = dto.getDeliveredQuantity();
			if (deliveryQuantity != null) {
				pst.setInt(9, deliveryQuantity);
			} else {
				pst.setNull(9, java.sql.Types.INTEGER);
			}

			pst.setString(10, dto.getTermsOfPayment());
			pst.setDate(11, new java.sql.Date(dto.getDeadlineDate().getTime()));
			pst.setInt(12, dto.getId());
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public boolean delete(OrderDTO dto) {
		Connection con = null;
		try {
			con = this.getConnection();
			String sql = " DELETE FROM orders WHERE id = ?; ";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, dto.getId());
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public DTOCollection<OrderDTO> search(String value, int offset, int limit) {
		DTOCollection<OrderDTO> list = new DTOCollection<OrderDTO>();
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder(" SELECT O.id, O.info, O.status, O.invoice_number, ");
			sql.append("O.receipt_date, O.total_value, O.delivered_quantity, O.terms_of_payment, ");
			sql.append("O.deadline_date, O.created, O.created_by, O.modified, O.modified_by, ");
			sql.append("O.quotation_id  FROM orders O ");
			if (StringUtils.isNumeric(value)) {
				sql.append("WHERE O.id = ? ");
			} else if (StringUtils.isNotBlank(value)) {
				sql.append(", quotations Q, suppliers S, request_quotation RQ, requests R ");
				sql.append("WHERE O.quotation_id = Q.id ");
				sql.append("AND Q.supplier_id = S.id ");
				sql.append("AND Q.id = RQ.quotation_id ");
				sql.append("AND RQ.request_id = R.id ");
				sql.append("AND ((S.trademark ilike ?) OR (R.author ilike ?) OR (R.item_title ilike ?)) ");
			}
			sql.append("ORDER BY O.created ASC LIMIT ? OFFSET ?;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			int i = 1;
			if (StringUtils.isNumeric(value)) {
				pst.setInt(i++, Integer.valueOf(value));
			} else  if (StringUtils.isNotBlank(value)) {
				pst.setString(i++, "%" + value + "%");
				pst.setString(i++, "%" + value + "%");
				pst.setString(i++, "%" + value + "%");
			}
			pst.setInt(i++, offset);
			pst.setInt(i++, limit);
			
			StringBuilder sqlCount = new StringBuilder("SELECT count(*) as total FROM orders O ");
			if (StringUtils.isNumeric(value)) {
				sql.append("WHERE O.id = ? ");
			} else if (StringUtils.isNotBlank(value)) {
				sql.append(", quotations Q, suppliers S, request_quotation RQ, requests R ");
				sql.append("WHERE O.quotation_id = Q.id ");
				sql.append("AND Q.supplier_id = S.id ");
				sql.append("AND Q.id = RQ.quotation_id ");
				sql.append("AND RQ.request_id = R.id ");
				sql.append("AND ((S.trademark ilike ?) OR (R.author ilike ?) OR (R.item_title ilike ?));");
			}
			
			PreparedStatement pstCount = con.prepareStatement(sqlCount.toString());			
			if (StringUtils.isNumeric(value)) {
				pst.setInt(1, Integer.valueOf(value));
			} else if (StringUtils.isNotBlank(value)) {
				pst.setString(1, "%" + value + "%");
				pst.setString(2, "%" + value + "%");
				pst.setString(3, "%" + value + "%");
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

	private OrderDTO populateDto(ResultSet rs) throws Exception {
		OrderDTO dto = new OrderDTO();
		dto.setId(rs.getInt("id"));
		dto.setQuotationId(rs.getInt("quotation_id"));
		dto.setInfo(rs.getString("info"));
		dto.setStatus(rs.getString("status"));
		dto.setInvoiceNumber(rs.getString("invoice_number"));
		dto.setReceiptDate(rs.getDate("receipt_date"));
		dto.setTotalValue(rs.getFloat("total_value"));
		dto.setDeliveredQuantity(rs.getInt("delivered_quantity"));
		dto.setTermsOfPayment(rs.getString("terms_of_payment"));
		dto.setDeadlineDate(rs.getDate("deadline_date"));
		
		dto.setCreated(rs.getDate("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getDate("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

}
