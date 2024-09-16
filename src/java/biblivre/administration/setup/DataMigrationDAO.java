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
package biblivre.administration.setup;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import biblivre.acquisition.order.OrderDTO;
import biblivre.acquisition.quotation.QuotationDTO;
import biblivre.acquisition.quotation.RequestQuotationDTO;
import biblivre.acquisition.request.RequestDTO;
import biblivre.acquisition.request.RequestStatus;
import biblivre.acquisition.supplier.SupplierDTO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.administration.accesscards.AccessCardStatus;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.authorities.AuthorityRecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.cataloging.vocabulary.VocabularyRecordDTO;
import biblivre.circulation.accesscontrol.AccessControlDTO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.reservation.ReservationDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserFieldDTO;
import biblivre.circulation.user.UserFields;
import biblivre.circulation.user.UserStatus;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.MemoryFile;
import biblivre.login.LoginDTO;
import biblivre.marc.MaterialType;
import biblivre.z3950.Z3950AddressDTO;

public class DataMigrationDAO extends AbstractDAO {
	
	private String userSchema;
	private static final Map<String, String> userFieldsMap = new HashMap<String, String>(); 
	
	static {
		userFieldsMap.put("email","email");
		userFieldsMap.put("gender","sex");
		userFieldsMap.put("phone_cel","cellphone");
		userFieldsMap.put("phone_home","tel_ref_1");
		userFieldsMap.put("phone_work","tel_ref_2");
		userFieldsMap.put("phone_work_extension","extension_line");
		userFieldsMap.put("id_rg","dlicense");
		userFieldsMap.put("id_cpf","social_id_number");
		userFieldsMap.put("address","address");
		userFieldsMap.put("address_number","number");
		userFieldsMap.put("address_complement","completion");
		userFieldsMap.put("address_zip","zip_code");
		userFieldsMap.put("address_city","city");
		userFieldsMap.put("address_state","state");
		userFieldsMap.put("birthday","birthday");
		userFieldsMap.put("obs","obs");
	};

	public static DataMigrationDAO getInstance(String userSchema, String datasource) {
		DataMigrationDAO dao = (DataMigrationDAO) AbstractDAO.getInstance(DataMigrationDAO.class, "public", datasource);
		dao.userSchema = userSchema;
		return dao;
	}
	
	public int countCatalogingRecords(RecordType recordType) {
		String tableName = "cataloging_" + recordType.toString();
		int total = 0;
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) as total FROM " + tableName + ";");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				return rs.getInt("total");
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return total;
	}

	
	public List<RecordDTO> listCatalogingRecords(RecordType recordType, int limit, int offset) {
		List<RecordDTO> list = new LinkedList<RecordDTO>();

		String tableName = "cataloging_" + recordType.toString();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM " + tableName + " ");
			sql.append("ORDER BY record_serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				RecordDTO dto = null;
				
				switch (recordType) {
					case BIBLIO:
						dto = new BiblioRecordDTO();
						break;
					case AUTHORITIES:
						dto = new AuthorityRecordDTO();
						break;
					case VOCABULARY:
						dto = new VocabularyRecordDTO();
						break;
					default:
						break;
				}

				if (dto == null) {
					continue;
				}
				
				dto.setId(rs.getInt("record_serial"));
				dto.setIso2709(new String(rs.getBytes("record"), "UTF-8"));
				dto.setCreatedBy(1);
				
				//Fix for bib4 holding reports - no creation date
				dto.setCreated(rs.getTimestamp("created"));
				dto.setModified(rs.getTimestamp("modified"));

				if (recordType == RecordType.BIBLIO) {
					Integer database = rs.getInt("database");
					dto.setRecordDatabase((database == 0) ? RecordDatabase.MAIN : RecordDatabase.WORK);

					String mt = rs.getString("material_type");
					dto.setMaterialType(this.convertMaterialType(mt));
					
				} else {
					dto.setRecordDatabase(RecordDatabase.MAIN);
					dto.setMaterialType(MaterialType.fromString(recordType.toString()));
				}
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<HoldingDTO> listCatalogingHoldings(int limit, int offset) {
		List<HoldingDTO> list = new LinkedList<HoldingDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM cataloging_holdings WHERE record_serial IN ");
			sql.append("(SELECT record_serial FROM cataloging_biblio) ");
			sql.append("ORDER BY holding_serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				HoldingDTO dto = new HoldingDTO();

				dto.setId(rs.getInt("holding_serial"));
				dto.setRecordId(rs.getInt("record_serial"));
				dto.setIso2709(new String(rs.getBytes("record"), "UTF-8"));
				//As this is a migration, we're setting the creator as 'admin' 
				dto.setCreatedBy(1);
				//Fix for bib4 holding reports - no creation date
				dto.setCreated(rs.getTimestamp("created"));
				dto.setModified(rs.getTimestamp("modified"));
				dto.setMaterialType(MaterialType.HOLDINGS);
				
				Integer recordDatabase = rs.getInt("database");
				dto.setRecordDatabase((recordDatabase == 0) ? RecordDatabase.MAIN : RecordDatabase.WORK);
				
				Integer availability = rs.getInt("availability");
				dto.setAvailability((availability == 0) ? HoldingAvailability.AVAILABLE : HoldingAvailability.UNAVAILABLE);

				dto.setAccessionNumber(rs.getString("asset_holding"));
				dto.setLocationD(rs.getString("loc_d"));		

				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<MemoryFile> listDigitalMedia(int limit, int offset) {
		List<MemoryFile> list = new LinkedList<MemoryFile>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM digital_media ");
			sql.append("ORDER BY id ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				MemoryFile dto = new MemoryFile();

				dto.setId(rs.getInt("id"));
				dto.setContentType(rs.getString("mime_type"));
				dto.setName(rs.getString("file_name"));
				byte[] file = rs.getBytes("file");
				dto.setInputStream(new ByteArrayInputStream(file));
				dto.setSize(file.length);

				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<AccessCardDTO> listAccessCards(int limit, int offset) {
		List<AccessCardDTO> list = new LinkedList<AccessCardDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM cards ");
			sql.append("ORDER BY serial_card ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				AccessCardDTO dto = new AccessCardDTO();
				
				dto.setId(rs.getInt("serial_card"));
				dto.setCode(rs.getString("card_number"));
				dto.setStatus(this.convertAccessCardStatus(rs.getInt("status")));
				dto.setCreatedBy(rs.getInt("userid"));
				dto.setCreated(rs.getTimestamp("date_time"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}


	public List<LoginDTO> listLogins(int limit, int offset) {
		List<LoginDTO> list = new LinkedList<LoginDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM logins ");
			sql.append("ORDER BY loginid ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				LoginDTO dto = new LoginDTO();

				dto.setId(rs.getInt("loginid"));
				dto.setLogin(rs.getString("loginname"));
				dto.setEmployee(Boolean.TRUE);
				dto.setEncPassword(rs.getString("encpwd"));
				
				dto.setCreatedBy(1);
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}

	public List<Z3950AddressDTO> listZ3950Servers(int limit, int offset) {
		List<Z3950AddressDTO> list = new LinkedList<Z3950AddressDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM z3950_server ");
			sql.append("ORDER BY server_id ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				Z3950AddressDTO dto = new Z3950AddressDTO();
				
				dto.setId(rs.getInt("server_id"));
				dto.setName(rs.getString("server_name").trim());
				dto.setUrl(rs.getString("server_url").trim());
				dto.setPort(rs.getInt("server_port"));
				dto.setCollection(rs.getString("server_dbname"));
				
				dto.setCreatedBy(1);
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<AccessControlDTO> listAccessControl(int limit, int offset) {
		List<AccessControlDTO> list = new LinkedList<AccessControlDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM access_control ");
			sql.append("WHERE departure_datetime IS NULL ");
			sql.append("ORDER BY serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				AccessControlDTO dto = new AccessControlDTO();
				
				dto.setId(rs.getInt("serial"));
				dto.setAccessCardId(rs.getInt("serial_card"));
				dto.setUserId(rs.getInt("serial_reader"));
				dto.setArrivalTime(rs.getTimestamp("entrance_datetime"));
				dto.setDepartureTime(rs.getTimestamp("departure_datetime"));
				
				dto.setCreatedBy(1);
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<AccessControlDTO> listAccessControlHistory(int limit, int offset) {
		List<AccessControlDTO> list = new LinkedList<AccessControlDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM access_control ");
			sql.append("WHERE departure_datetime IS NOT NULL ");
			sql.append("ORDER BY serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				AccessControlDTO dto = new AccessControlDTO();
				
				dto.setId(rs.getInt("serial"));
				dto.setAccessCardId(rs.getInt("serial_card"));
				dto.setUserId(rs.getInt("serial_reader"));
				dto.setArrivalTime(rs.getTimestamp("entrance_datetime"));
				dto.setDepartureTime(rs.getTimestamp("departure_datetime"));
				
				dto.setCreatedBy(1);
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<LendingDTO> listLendings(int limit, int offset) {
		List<LendingDTO> list = new LinkedList<LendingDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM (SELECT lending_serial as id, holding_serial, user_serial, lending_date, return_date as expected_return_date, null as return_date  FROM lending ");
			sql.append("UNION  ");
			sql.append("SELECT lending_history_serial as id, holding_serial, user_serial, lending_date, null as expected_return_date, return_date as return_date FROM lending_history ) A ");
			sql.append("ORDER BY A.lending_date, A.id ");
			sql.append("LIMIT ? OFFSET ? ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				LendingDTO dto = new LendingDTO();
				
				dto.setId(rs.getInt("id"));
				dto.setHoldingId(rs.getInt("holding_serial"));
				dto.setUserId(rs.getInt("user_serial"));
				dto.setExpectedReturnDate(rs.getDate("expected_return_date"));
				dto.setReturnDate(rs.getTimestamp("return_date"));

				dto.setCreatedBy(1);
				dto.setCreated(rs.getTimestamp("lending_date"));				
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<LendingFineDTO> listLendingFines(int limit, int offset) {
		List<LendingFineDTO> list = new LinkedList<LendingFineDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM lending_fine ");
			sql.append("ORDER BY serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				LendingFineDTO dto = new LendingFineDTO();
				dto.setCreatedBy(1);
				dto.setId(rs.getInt("serial"));
				dto.setLendingId(rs.getInt("lending_history_serial"));
				dto.setUserId(rs.getInt("user_serial"));
				dto.setValue(rs.getFloat("value"));
				dto.setPayment(rs.getDate("payment"));
				dto.setCreated(rs.getDate("payment"));				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<SupplierDTO> listAquisitionSupplier(int limit, int offset) {
		List<SupplierDTO> list = new LinkedList<SupplierDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM acquisition_supplier ");
			sql.append("ORDER BY serial_supplier ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				SupplierDTO dto = new SupplierDTO();
				dto.setCreatedBy(1);
				dto.setCreated(rs.getDate("created"));
				dto.setModified(rs.getDate("modified"));
				
				dto.setId(rs.getInt("serial_supplier"));
				dto.setTrademark(rs.getString("trade_mark_name"));
				dto.setName(rs.getString("company_name"));
				dto.setSupplierNumber(rs.getString("company_number"));
				dto.setVatRegistrationNumber(rs.getString("vat_registration_number"));
				dto.setAddress(rs.getString("address"));
				dto.setAddressNumber(rs.getString("number_address"));
				dto.setComplement(rs.getString("complement"));
				dto.setArea(rs.getString("area"));
				dto.setCity(rs.getString("city"));
				dto.setState(rs.getString("state"));
				dto.setCountry(rs.getString("country"));
				dto.setZipCode(rs.getString("zip_code"));
				dto.setTelephone1(rs.getString("telephone_1"));
				dto.setTelephone2(rs.getString("telephone_2"));
				dto.setTelephone3(rs.getString("telephone_3"));
				dto.setTelephone4(rs.getString("telephone_4"));
				dto.setContact1(rs.getString("contact_1"));
				dto.setContact2(rs.getString("contact_2"));
				dto.setContact3(rs.getString("contact_3"));
				dto.setContact4(rs.getString("contact_4"));
				dto.setInfo(rs.getString("obs"));
				dto.setUrl(rs.getString("url"));
				dto.setEmail(rs.getString("email"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<RequestDTO> listAquisitionRequisition(int limit, int offset) {
		List<RequestDTO> list = new LinkedList<RequestDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM acquisition_requisition ");
			sql.append("ORDER BY serial_requisition ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				RequestDTO dto = new RequestDTO();
				dto.setCreatedBy(1);
				dto.setCreated(rs.getDate("requisition_date"));
				
				dto.setId(rs.getInt("serial_requisition"));
				dto.setRequester(rs.getString("responsable"));
				dto.setAuthor(rs.getString("author"));
				dto.setTitle(rs.getString("item_title"));
				dto.setSubtitle(rs.getString("item_subtitle"));
				dto.setEditionNumber(rs.getString("edition_number"));
				dto.setPublisher(rs.getString("publisher"));
				dto.setInfo(rs.getString("obs"));
				String oldStatus = rs.getString("status");
				if (StringUtils.isNotBlank(oldStatus)) {
					dto.setStatus(convertRequestStatus(oldStatus));
				}
				dto.setQuantity(rs.getInt("quantity"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<QuotationDTO> listAquisitionQuotation(int limit, int offset) {
		List<QuotationDTO> list = new LinkedList<QuotationDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM acquisition_quotation ");
			sql.append("ORDER BY serial_quotation ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				QuotationDTO dto = new QuotationDTO();
				dto.setCreatedBy(1);
				dto.setCreated(rs.getDate("quotation_date"));
				
				dto.setId(rs.getInt("serial_quotation"));
				dto.setSupplierId(rs.getInt("serial_supplier"));
				dto.setResponseDate(rs.getTimestamp("response_date"));
				dto.setExpirationDate(rs.getTimestamp("expiration_date"));
				dto.setDeliveryTime(rs.getInt("delivery_time"));
				dto.setInfo(rs.getString("obs"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<RequestQuotationDTO> listAquisitionItemQuotation(int limit, int offset) {
		List<RequestQuotationDTO> list = new LinkedList<RequestQuotationDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM acquisition_item_quotation ");
			sql.append("ORDER BY serial_requisition ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				RequestQuotationDTO dto = new RequestQuotationDTO();
				dto.setCreatedBy(1);
				
				dto.setRequestId(rs.getInt("serial_requisition"));
				dto.setQuotationId(rs.getInt("serial_quotation"));
				dto.setQuantity(rs.getInt("quotation_quantity"));
				dto.setUnitValue(rs.getFloat("unit_value"));
				dto.setResponseQuantity(rs.getInt("response_quantity"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<OrderDTO> listAquisitionOrder(int limit, int offset) {
		List<OrderDTO> list = new LinkedList<OrderDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM acquisition_order ");
			sql.append("ORDER BY serial_order ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				OrderDTO dto = new OrderDTO();
				dto.setCreatedBy(1);
				dto.setCreated(rs.getDate("order_date"));
				
				dto.setId(rs.getInt("serial_order"));
				dto.setQuotationId(rs.getInt("serial_quotation"));
				dto.setInfo(rs.getString("obs"));
				dto.setDeadlineDate(rs.getTimestamp("deadline_date"));
				
				dto.setInvoiceNumber(rs.getString("invoice_number"));
				dto.setReceiptDate(rs.getTimestamp("receipt_date"));
				dto.setTotalValue(rs.getFloat("total_value"));
				dto.setDeliveredQuantity(rs.getInt("delivered_quantity"));
				dto.setTermsOfPayment(rs.getString("terms_of_payment"));
				dto.setStatus(rs.getString("status"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<ReservationDTO> listReservations(int limit, int offset) {
		List<ReservationDTO> list = new LinkedList<ReservationDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM reservation ");
			sql.append("ORDER BY reservation_serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				ReservationDTO dto = new ReservationDTO();
				dto.setCreatedBy(1);
				dto.setCreated(rs.getDate("created"));
				
				dto.setId(rs.getInt("reservation_serial"));
				dto.setRecordId(rs.getInt("record_serial"));
				dto.setUserId(rs.getInt("userid"));
				dto.setExpires(rs.getTimestamp("expires"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<UserDTO> listUsers(int limit, int offset) {
		List<UserDTO> list = new LinkedList<UserDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM users ");
			sql.append("ORDER BY userid ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				UserDTO dto = new UserDTO();
				dto.setCreatedBy(rs.getInt("whosignup"));
				dto.setId(rs.getInt("userid"));
				dto.setCreated(rs.getDate("signup_date"));
				dto.setModified(rs.getDate("alter_date"));
				
				dto.setName(rs.getString("username"));
				dto.setType(rs.getInt("user_type"));
				dto.setLoginId(rs.getInt("loginid"));
				dto.setPhotoId(rs.getString("photo_id"));
				dto.setStatus(this.convertUserStatus(rs.getString("status")));
				
				List<UserFieldDTO> userFields = UserFields.getFields(this.userSchema);
				for (UserFieldDTO userField : userFields) {
					String key = userField.getKey();
					String columnName = this.getUserFieldColumnName(key);
					if (StringUtils.isNotBlank(columnName)) {
						String columnValue = rs.getString(columnName);
						if (StringUtils.isNotBlank(columnValue)) {
							dto.addField(key, columnValue);
						}
					}
				}				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	public List<UserTypeDTO> listUsersTypes(int limit, int offset) {
		List<UserTypeDTO> list = new LinkedList<UserTypeDTO>();
		
		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM users_type ");
			sql.append("ORDER BY serial ASC ");
			sql.append("LIMIT ? OFFSET ? ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			pst.setInt(1, limit);
			pst.setInt(2, offset);
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				UserTypeDTO dto = new UserTypeDTO();
				dto.setCreatedBy(1);
				dto.setId(rs.getInt("serial"));
				
				dto.setName(rs.getString("usertype"));
				dto.setDescription(rs.getString("description"));
				dto.setLendingLimit(rs.getInt("number_max_itens"));
				dto.setLendingTimeLimit(rs.getInt("time_returned"));
				dto.setReservationLimit(dto.getLendingLimit());
				dto.setReservationTimeLimit(rs.getInt("max_reservation_days"));
				
				list.add(dto);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return list;
	}
	
	private MaterialType convertMaterialType(String mt) {
		MaterialType material;

		if ("BOOK".equals(mt)) {
			material = MaterialType.BOOK; 
		} else if ("BOOKM".equals(mt)) {
			material = MaterialType.MANUSCRIPT; 
		} else if ("BOOKP".equals(mt)) {
			material = MaterialType.PAMPHLET; 
		} else if ("BOOKT".equals(mt)) {
			material = MaterialType.THESIS; 
		} else if ("CFILES".equals(mt)) {
			material = MaterialType.COMPUTER_LEGIBLE; 
		} else if ("MAPS".equals(mt)) {
			material = MaterialType.MAP; 
		} else if ("MOVIE".equals(mt)) {
			material = MaterialType.MOVIE; 
		} else if ("MUSIC".equals(mt)) {
			material = MaterialType.SCORE; 
		} else if ("OBJ3D".equals(mt)) {
			material = MaterialType.OBJECT_3D; 
		} else if ("PHOTO".equals(mt)) {
			material = MaterialType.PHOTO; 
		} else if ("SERIAL".equals(mt)) {
			material = MaterialType.PERIODIC; 
		} else if ("SERIAR".equals(mt)) {
			material = MaterialType.ARTICLES; 
		} else if ("SOUND".equals(mt)) {
			material = MaterialType.MUSIC; 
		} else {
			material = MaterialType.BOOK;
		}
		return material;
	}

	private AccessCardStatus convertAccessCardStatus(Integer oldStatus) {
		AccessCardStatus status;
		
		switch (oldStatus) {
			case 0:
				status = AccessCardStatus.AVAILABLE; 
				break;
			case 1:
				status = AccessCardStatus.IN_USE; 
				break;
			case 2:
				status = AccessCardStatus.BLOCKED; 
				break;
			case 3:
				status = AccessCardStatus.IN_USE_AND_BLOCKED; 
				break;
			case 4:
				status = AccessCardStatus.CANCELLED; 
				break;
			default:
				status = AccessCardStatus.AVAILABLE;
				break;
		}
		return status;
	}
	
	private RequestStatus convertRequestStatus(String oldStatus) {
		RequestStatus status;
		
		if (oldStatus == null) {
			return RequestStatus.CLOSED;
		}
		
		if (oldStatus.equals("0")) {
			status = RequestStatus.PENDING; 
		} else if (oldStatus.equals("1")) {
			status = RequestStatus.CLOSED; 
		} else if (oldStatus.equals("2")) {
			status = RequestStatus.PENDING_AND_CLOSED; 
		} else {
			status = RequestStatus.CLOSED;
		}
		
		return status;
	}
	
	private UserStatus convertUserStatus(String oldStatus) {
		UserStatus status;
		
		if (oldStatus == null) {
			return UserStatus.ACTIVE;
		}

		if (oldStatus.equals("ACTIVE")) {
			status = UserStatus.ACTIVE; 
		} else if (oldStatus.equals("PENDINGS")) {
			status = UserStatus.PENDING_ISSUES; 
		} else if (oldStatus.equals("INACTIVE")) {
			status = UserStatus.INACTIVE; 
		} else if (oldStatus.equals("BLOCKED")) {
			status = UserStatus.BLOCKED; 
		} else {
			status = UserStatus.ACTIVE;
		}
		return status;
	}
	
	private String getUserFieldColumnName(String biblivre4UserFieldKey) {
		return userFieldsMap.get(biblivre4UserFieldKey);
	}
	
}
