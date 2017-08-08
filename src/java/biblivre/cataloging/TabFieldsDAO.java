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
package biblivre.cataloging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class TabFieldsDAO extends AbstractDAO {
	
	public static TabFieldsDAO getInstance(String schema) {
		return (TabFieldsDAO) AbstractDAO.getInstance(TabFieldsDAO.class, schema);
	}

	public List<BriefTabFieldFormatDTO> listBriefFormats(RecordType recordType) {
		List<BriefTabFieldFormatDTO> list = new LinkedList<BriefTabFieldFormatDTO>();

		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT * FROM " + recordType + "_brief_formats ORDER BY sort_order, datafield;";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				try {
					list.add(this.populateFormatsDTO(rs));
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}
	
	public boolean insertBriefFormat(BriefTabFieldFormatDTO dto, RecordType recordType, int loggedUser) {

		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO " + recordType + "_brief_formats ");
			sql.append(" (datafield, format, sort_order, created_by) ");
			sql.append(" VALUES (?, ?, ?, ?); ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, dto.getDatafieldTag());
			pst.setString(2, dto.getFormat());
			pst.setInt(3, dto.getSortOrder());
			pst.setInt(4, dto.getCreatedBy());
			pst.executeUpdate();
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return true;
	}
	
	public boolean updateBriefFormats(List<BriefTabFieldFormatDTO> briefFormats, RecordType recordType, int loggedUser) {

		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE " + recordType + "_brief_formats ");
			sql.append(" SET sort_order = ?, ");
			sql.append(" format = ?, ");
			sql.append(" modified = now(), ");
			sql.append(" modified_by = ? ");
			sql.append(" WHERE datafield = ?; ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			for (BriefTabFieldFormatDTO dto : briefFormats) {
				pst.setInt(1, dto.getSortOrder());
				pst.setString(2, dto.getFormat());
				pst.setInt(3, loggedUser);
				pst.setString(4, dto.getDatafieldTag());
				
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
	
	public boolean deleteBriefFormat(String datafield, RecordType recordType) {

		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE FROM " + recordType + "_brief_formats ");
			sql.append(" WHERE datafield = ?; ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, datafield);
			
			pst.executeUpdate();
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return true;
	}
	
	public boolean deleteFormTabDatafield(String datafield, RecordType recordType) {

		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			StringBuilder subfieldSql = new StringBuilder();
			subfieldSql.append(" DELETE FROM " + recordType + "_form_subfields ");
			subfieldSql.append(" WHERE datafield = ?; ");
			
			PreparedStatement subfieldPst = con.prepareStatement(subfieldSql.toString());
			subfieldPst.setString(1, datafield);
			
			subfieldPst.executeUpdate();

			StringBuilder datafieldSql = new StringBuilder();
			datafieldSql.append(" DELETE FROM " + recordType + "_form_datafields ");
			datafieldSql.append(" WHERE datafield = ?; ");
			
			PreparedStatement datafieldPst = con.prepareStatement(datafieldSql.toString());
			datafieldPst.setString(1, datafield);
			
			datafieldPst.executeUpdate();

			con.commit();
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return true;
	}

	public List<FormTabDatafieldDTO> listFields(RecordType recordType) {
		List<FormTabDatafieldDTO> list = new LinkedList<FormTabDatafieldDTO>();
		HashMap<String, FormTabDatafieldDTO> hash = new HashMap<String, FormTabDatafieldDTO>();

		
		Connection con = null;
		try {
			con = this.getConnection();
			String sqlDatafields = "SELECT * FROM " + recordType + "_form_datafields ORDER BY sort_order, datafield;;";
			
			Statement stDatafields = con.createStatement();
			ResultSet rsDatafields = stDatafields.executeQuery(sqlDatafields);			
			while (rsDatafields.next()) {
				try {
					FormTabDatafieldDTO datafield = this.populateDatafieldDTO(rsDatafields);
					
					hash.put(datafield.getDatafield(), datafield);
					list.add(datafield);
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}

			String sqlSubfields = "SELECT * FROM " + recordType + "_form_subfields ORDER BY sort_order, datafield, subfield;";
			Statement stSubfields = con.createStatement();
			ResultSet rsSubfields = stSubfields.executeQuery(sqlSubfields);

			while (rsSubfields.next()) {
				try {
					FormTabSubfieldDTO subfield = this.populateSubfieldDTO(rsSubfields);
					
					FormTabDatafieldDTO datafield = hash.get(subfield.getDatafield());
					
					if (datafield != null) {
						datafield.addSubfield(subfield);
					}
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}
	
	private BriefTabFieldFormatDTO populateFormatsDTO(ResultSet rs) throws SQLException {
		BriefTabFieldFormatDTO dto = new BriefTabFieldFormatDTO();

		dto.setDatafieldTag(rs.getString("datafield"));
		dto.setFormat(rs.getString("format"));
		dto.setSortOrder(rs.getInt("sort_order"));

		return dto;
	}

	private FormTabDatafieldDTO populateDatafieldDTO(ResultSet rs) throws SQLException {
		FormTabDatafieldDTO dto = new FormTabDatafieldDTO();

		dto.setDatafield(rs.getString("datafield"));
		dto.setCollapsed(rs.getBoolean("collapsed"));
		dto.setRepeatable(rs.getBoolean("repeatable"));
		dto.setIndicator1(rs.getString("indicator_1"));
		dto.setIndicator2(rs.getString("indicator_2"));
		dto.setMaterialType(rs.getString("material_type"));
		dto.setSortOrder(rs.getInt("sort_order"));

		return dto;
	}

	private FormTabSubfieldDTO populateSubfieldDTO(ResultSet rs) throws SQLException {
		FormTabSubfieldDTO dto = new FormTabSubfieldDTO();

		dto.setDatafield(rs.getString("datafield"));
		dto.setSubfield(rs.getString("subfield"));
		dto.setCollapsed(rs.getBoolean("collapsed"));
		dto.setRepeatable(rs.getBoolean("repeatable"));
		dto.setAutocompleteType(rs.getString("autocomplete_type"));
		dto.setSortOrder(rs.getInt("sort_order"));

		return dto;
	}

	public boolean updateFormTabDatafield(HashMap<String, FormTabDatafieldDTO> formDatafields, RecordType recordType, int loggedUser) {
		Connection con = null;
		try {
			con = this.getConnection();

			boolean delete = false;
			boolean insert = false;
			
			StringBuilder sqlDelete = new StringBuilder();
			sqlDelete.append("DELETE FROM " + recordType + "_form_subfields ");
			sqlDelete.append("WHERE datafield = ?; ");

			PreparedStatement pstDelete = con.prepareStatement(sqlDelete.toString());

			StringBuilder sqlInsert = new StringBuilder();
			sqlInsert.append("INSERT INTO " + recordType + "_form_subfields ");
			sqlInsert.append("(datafield, subfield, collapsed, repeatable, created, created_by, modified, modified_by, autocomplete_type, sort_order) ");
			sqlInsert.append(" VALUES (?, ?, ?, ?, now(), ?, now(), ?, ?, ?); ");

			PreparedStatement pstInsert = con.prepareStatement(sqlInsert.toString());
			
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE " + recordType + "_form_datafields ");
			sql.append("SET sort_order = ?, ");
			sql.append("datafield = ?, ");
			sql.append("collapsed = ?, ");
			sql.append("repeatable = ?, ");
			sql.append("indicator_1 = ?, ");
			sql.append("indicator_2 = ?, ");
			sql.append("material_type = ?, ");
			sql.append("modified = now(), ");
			sql.append("modified_by = ? ");
			sql.append("WHERE datafield = ?; ");
			
			StringBuilder sqlDatafieldInsert = new StringBuilder();
			sqlDatafieldInsert.append("INSERT INTO " + recordType + "_form_datafields (sort_order, datafield, collapsed, repeatable, indicator_1, indicator_2, material_type, created_by, modified_by) ");
			sqlDatafieldInsert.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?); ");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			PreparedStatement pstDatafieldInsert = con.prepareStatement(sqlDatafieldInsert.toString());
			
			
			for (Entry<String, FormTabDatafieldDTO> entry : formDatafields.entrySet()) {
				String key = entry.getKey();
				FormTabDatafieldDTO dto = entry.getValue();
				
				if (dto.getSubfields() != null && dto.getSubfields().size() > 0) {
					delete = true;
					
					pstDelete.setString(1, key);
					pstDelete.addBatch();
					
					for (FormTabSubfieldDTO sub : dto.getSubfields()) {
						insert = true;
						
						pstInsert.setString(1, sub.getDatafield());
						pstInsert.setString(2, sub.getSubfield());
						pstInsert.setBoolean(3, sub.isCollapsed());
						pstInsert.setBoolean(4, sub.isRepeatable());
						pstInsert.setInt(5, loggedUser);
						pstInsert.setInt(6, loggedUser);
						pstInsert.setString(7, sub.getAutocompleteType().toString());
						pstInsert.setInt(8, sub.getSortOrder());
						
						pstInsert.addBatch();
					}
				}
								
				pst.setInt(1, dto.getSortOrder());
				pst.setString(2, dto.getDatafield());
				pst.setBoolean(3, dto.isCollapsed());
				pst.setBoolean(4, dto.isRepeatable());
				pst.setString(5, dto.getIndicator1());
				pst.setString(6, dto.getIndicator2());
				pst.setString(7, dto.getMaterialType());
				pst.setInt(8, loggedUser);
				pst.setString(9, dto.getDatafield());
				
				pst.addBatch();
				
				pstDatafieldInsert.setInt(1, dto.getSortOrder());
				pstDatafieldInsert.setString(2, dto.getDatafield());
				pstDatafieldInsert.setBoolean(3, dto.isCollapsed());
				pstDatafieldInsert.setBoolean(4, dto.isRepeatable());
				pstDatafieldInsert.setString(5, dto.getIndicator1());
				pstDatafieldInsert.setString(6, dto.getIndicator2());
				pstDatafieldInsert.setString(7, dto.getMaterialType());
				pstDatafieldInsert.setInt(8, loggedUser);
				pstDatafieldInsert.setInt(9, loggedUser);
				
				try {
					pstDatafieldInsert.execute();
				} catch (Exception e) {
				}
			}
			
			con.setAutoCommit(false);
			
			if (delete) {
				pstDelete.executeBatch();
			}
			
			pst.executeBatch();

			if (insert) {
				pstInsert.executeBatch();
			}
			
			this.commit(con);
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return true;
	}
}
