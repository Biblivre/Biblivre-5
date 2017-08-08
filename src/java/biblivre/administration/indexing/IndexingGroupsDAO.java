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
package biblivre.administration.indexing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class IndexingGroupsDAO extends AbstractDAO {
	
	public static IndexingGroupsDAO getInstance(String schema) {
		return (IndexingGroupsDAO) AbstractDAO.getInstance(IndexingGroupsDAO.class, schema);
	}

	public List<IndexingGroupDTO> list(RecordType recordType) {
		List<IndexingGroupDTO> list = new ArrayList<IndexingGroupDTO>();

		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT * FROM " + recordType + "_indexing_groups ORDER BY id;";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				try {
					list.add(this.populateDTO(rs));
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
	
	private IndexingGroupDTO populateDTO(ResultSet rs) throws SQLException {
		IndexingGroupDTO dto = new IndexingGroupDTO();

		dto.setId(rs.getInt("id"));
		dto.setTranslationKey(rs.getString("translation_key"));
		dto.setDatafields(rs.getString("datafields"));
		dto.setSortable(rs.getBoolean("sortable"));
		dto.setDefaultSort(rs.getBoolean("default_sort"));
		
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));

		return dto;
	}
}
