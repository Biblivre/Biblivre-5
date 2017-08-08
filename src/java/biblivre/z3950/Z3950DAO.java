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
package biblivre.z3950;

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
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;

public class Z3950DAO extends AbstractDAO {

	public static Z3950DAO getInstance(String schema) {
		return (Z3950DAO) AbstractDAO.getInstance(Z3950DAO.class, schema);
	}

	public List<Z3950AddressDTO> listAll() {
		return this.list(null);
	}

	public DTOCollection<Z3950AddressDTO> search(String value, int limit, int offset) {
		DTOCollection<Z3950AddressDTO> list = new DTOCollection<Z3950AddressDTO>();

		if (value != null) {
			value = TextUtils.removeDiacriticals(value);
		}
		
		if (limit == 0) {
			limit = Configurations.getInt(this.getSchema(), Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 25);
		}

		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM z3950_addresses ");

			StringBuilder countSql = new StringBuilder();
			countSql.append("SELECT count(*) as total FROM z3950_addresses ");

			if (StringUtils.isNotBlank(value)) {
				sql.append("WHERE name ilike ? ");
				countSql.append("WHERE name ilike ? ");
			}

			sql.append("ORDER BY name ASC ");
			sql.append("LIMIT ? OFFSET ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			PreparedStatement pstCount = con.prepareStatement(countSql.toString());
			int psIndex = 1;

			if (StringUtils.isNotBlank(value)) {
				pst.setString(psIndex, "%" + value + "%");
				pstCount.setString(psIndex, "%" + value + "%");
				psIndex++;
			}

			pst.setInt(psIndex++, limit);
			pst.setInt(psIndex++, offset);

			ResultSet rs = pst.executeQuery();
			ResultSet rsCount = pstCount.executeQuery();

			while (rs.next()) {
				Z3950AddressDTO server = this.populateDTO(rs);
				list.add(server);
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

	
	public List<Z3950AddressDTO> list(List<Integer> ids) {
		List<Z3950AddressDTO> list = new LinkedList<Z3950AddressDTO>();

		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM z3950_addresses ");
			
			if (ids != null && ids.size() > 0) {
				sql.append("WHERE id in (");
				sql.append(StringUtils.repeat("?", ", ", ids.size()));
				sql.append(") ");
			}
			
			sql.append("ORDER BY name ASC;");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			if (ids != null) {
				int index = 1;

				for (Integer id : ids) {
					pst.setInt(index++, id);
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

	public boolean insert(Z3950AddressDTO dto) {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "INSERT INTO z3950_addresses (name, url, port, collection) VALUES (?, ?, ?, ?);";
			
			PreparedStatement pst = con.prepareStatement(sql, Statement	.RETURN_GENERATED_KEYS);

			pst.setString(1, dto.getName());
			pst.setString(2, dto.getUrl());
			pst.setInt(3, dto.getPort());
			pst.setString(4, dto.getCollection());

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
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sql = "INSERT INTO z3950_addresses (name, url, port, collection) VALUES (?, ?, ?, ?);";
			
			PreparedStatement pst = con.prepareStatement(sql);
			
			for (AbstractDTO abstractDto : dtoList) {
				Z3950AddressDTO dto = (Z3950AddressDTO) abstractDto;
				pst.setString(1, dto.getName());
				pst.setString(2, dto.getUrl());
				pst.setInt(3, dto.getPort());
				pst.setString(4, dto.getCollection());
//				pst.setInt(5, dto.getId());
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

	public boolean update(Z3950AddressDTO dto) {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "UPDATE z3950_addresses SET name = ?, url = ?, port = ?, collection = ? WHERE id = ?;";

			PreparedStatement pst = con.prepareStatement(sql);

			pst.setString(1, dto.getName());
			pst.setString(2, dto.getUrl());
			pst.setInt(3, dto.getPort());
			pst.setString(4, dto.getCollection());
			pst.setInt(5, dto.getId());

			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public boolean delete(Z3950AddressDTO dto) {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "DELETE FROM z3950_addresses WHERE id = ?;";
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, dto.getId());

			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	private Z3950AddressDTO populateDTO(ResultSet rs) throws SQLException {
		Z3950AddressDTO dto = new Z3950AddressDTO();
		
		dto.setId(rs.getInt("id"));
		dto.setName(rs.getString("name").trim());
		dto.setUrl(rs.getString("url").trim());
		dto.setPort(rs.getInt("port"));
		dto.setCollection(rs.getString("collection"));
		
		return dto;
	}
}
