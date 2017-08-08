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
package biblivre.cataloging.authorities;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;

import biblivre.cataloging.RecordDAO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;

public class AuthorityRecordDAO extends RecordDAO {

	public static AuthorityRecordDAO getInstance(String schema) {
		AuthorityRecordDAO dao = (AuthorityRecordDAO) AbstractDAO.getInstance(AuthorityRecordDAO.class, schema);

		if (dao.recordType == null) {
			dao.recordType = RecordType.AUTHORITIES;
		}

		return dao;
	}

	@Override
	protected RecordDTO createRecord() {
		return new AuthorityRecordDTO();
	}

	@Override
	public boolean save(RecordDTO dto) {
		Connection con = null;

		AuthorityRecordDTO adto = (AuthorityRecordDTO) dto;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ").append(this.recordType).append("_records ");
			sql.append("(id, iso2709, material, database, created_by) ");
			sql.append("VALUES (?, ?, ?, ?, ?); ");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, adto.getId());
			pst.setString(2, adto.getIso2709());
			pst.setString(3, adto.getAuthorType());
			pst.setString(4, adto.getRecordDatabase().toString());
			pst.setInt(5, dto.getCreatedBy());

			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	@Override
	public boolean update(RecordDTO dto) {
		Connection con = null;

		AuthorityRecordDTO adto = (AuthorityRecordDTO) dto;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE ").append(this.recordType).append("_records ");
			sql.append("SET iso2709 = ?, material = ?, modified = now(), modified_by = ? ");
			sql.append("WHERE id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, adto.getIso2709());
			pst.setString(2, adto.getAuthorType());
			pst.setInt(3, adto.getModifiedBy());
			pst.setInt(4, adto.getId());

			return pst.executeUpdate() > 0;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	@Override
	protected RecordDTO populateDTO(ResultSet rs) throws SQLException, UnsupportedEncodingException {
		AuthorityRecordDTO dto = (AuthorityRecordDTO) this.createRecord();

		dto.setId(rs.getInt("id"));
		dto.setIso2709(new String(rs.getBytes("iso2709"), "UTF-8"));

		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		dto.setMaterialType(MaterialType.AUTHORITIES);
		String material = rs.getString("material");

		if (StringUtils.isBlank(material) || !"100,110,111".contains(material)) {
			material = this.fixAuthorType(dto.getIso2709());
		}

		dto.setAuthorType(material);

		dto.setRecordDatabase(rs.getString("database"));

		return dto;
	}

	private String fixAuthorType(String iso2709) {
		Record record = MarcUtils.iso2709ToRecord(iso2709);

		for (String field : new String[] { "100", "110", "111" }) {
			if (record.getVariableField(field) != null) {
				return field;
			}
		}

		return "100";
	}
}
