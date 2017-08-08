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
package biblivre.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;

public class LoginDAO extends AbstractDAO {

	public static LoginDAO getInstance(String schema) {
		return (LoginDAO) AbstractDAO.getInstance(LoginDAO.class, schema);
	}
	
	public LoginDTO get(Integer loginId) {
		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT id, login, employee FROM logins WHERE id = ?;";
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, loginId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				LoginDTO loginDTO = new LoginDTO();
				loginDTO.setId(rs.getInt("id"));
				loginDTO.setLogin(rs.getString("login"));
				loginDTO.setEmployee(rs.getBoolean("employee"));
				return loginDTO;
			}

			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	
	public LoginDTO login(String login, String password) {
		LoginDTO dto = null;
		Connection con = null;
		
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			
			if (this.getSchema().equals(Constants.GLOBAL_SCHEMA)) {
				sql.append("SELECT id, login, employee, login as name FROM logins ");
				sql.append("WHERE login = ? and password = ?;");
			} else {
				sql.append("SELECT L.id, L.login, L.employee, coalesce(U.name, L.login) as name FROM logins L ");
				sql.append("LEFT JOIN users U ON U.login_id = L.id ");
				sql.append("WHERE L.login = ? and L.password = ?;");
			}

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, login);
			pst.setString(2, password);
			
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				dto = this.populateDTO(rs);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return dto;
	}
	
	public boolean update(LoginDTO login) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE logins SET employee = ?, modified = now(), modified_by = ? ");
			if (StringUtils.isNotBlank(login.getEncPassword())) {
				sql.append(", password = ? ");
			}
			sql.append("WHERE id = ?;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			int index = 1;
			pst.setBoolean(index++, login.isEmployee());
			pst.setInt(index++, login.getModifiedBy());
			if (StringUtils.isNotBlank(login.getEncPassword())) {
				pst.setString(index++, login.getEncPassword());
			}
			pst.setInt(index++, login.getId());
			
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public LoginDTO getByLogin(String loginName) {
		Connection con = null;
		try {
			con = this.getConnection();
			String sqlSelectLogin = "SELECT id, login, employee FROM logins WHERE login = ?;";

			PreparedStatement pstSelectLogin = con.prepareStatement(sqlSelectLogin);
			pstSelectLogin.setString(1, loginName);
			ResultSet rs = pstSelectLogin.executeQuery();

			if (rs != null && rs.next()) {
				LoginDTO loginDTO = new LoginDTO();
				loginDTO.setId(rs.getInt("id"));
				loginDTO.setLogin(rs.getString("login"));
				loginDTO.setEmployee(rs.getBoolean("employee"));
				return loginDTO;
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return null;
	}
	
	public boolean delete(UserDTO userDTO) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			String sql = null;
			PreparedStatement pst = null;

			sql = "SELECT login_id FROM users WHERE id = ?;";
			pst = con.prepareStatement(sql);
			pst.setInt(1, userDTO.getId());
			ResultSet rs = pst.executeQuery();
			int loginId = 0;
			if (rs != null && rs.next()) {
				loginId = rs.getInt("login_id");
			}

			if (loginId != 0) {
				sql = "UPDATE users SET login_id = null WHERE id = ?;";
				pst = con.prepareStatement(sql);
				pst.setInt(1, userDTO.getId());
				pst.executeUpdate();

				sql = "DELETE FROM logins WHERE id = ?;";
				pst = con.prepareStatement(sql);
				pst.setInt(1, loginId);
				pst.executeUpdate();
			}
			
			con.commit();
			return true;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public synchronized boolean save(LoginDTO dto, UserDTO udto) {
		Connection con = null;
		int loginId = 0;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			loginId = this.getNextSerial("logins_id_seq");
			
			if (loginId != 0) {
				dto.setId(loginId);
				String sqlInsertLogin = "INSERT INTO logins (id, login, password, employee, created_by) VALUES (?, ?, ?, ?, ?);";
				PreparedStatement pstInsertLogin = con.prepareStatement(sqlInsertLogin);
				pstInsertLogin.setInt(1, dto.getId());
				pstInsertLogin.setString(2, dto.getLogin());
				pstInsertLogin.setString(3, dto.getEncPassword());
				pstInsertLogin.setBoolean(4, dto.isEmployee());
				pstInsertLogin.setInt(5, dto.getCreatedBy());
				pstInsertLogin.executeUpdate();
				
				String sqlUpdateUser = "UPDATE users SET login_id = ?, modified = now(), modified_by = ? WHERE id = ?;";
				PreparedStatement pstUpdateUser = con.prepareStatement(sqlUpdateUser);
				pstUpdateUser.setInt(1, dto.getId());
				pstUpdateUser.setInt(2, dto.getCreatedBy());
				pstUpdateUser.setInt(3, udto.getId());
				pstUpdateUser.executeUpdate();

				udto.setLoginId(dto.getId());
			}
			
			this.commit(con);
			
			return true;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		Connection con = null;
		try {
			con = this.getConnection();
			
			String sqlInsertLogin = "INSERT INTO logins (id, login, password, employee, created_by) VALUES (?, ?, ?, ?, ?);";
			PreparedStatement pstInsertLogin = con.prepareStatement(sqlInsertLogin);
			for (AbstractDTO abstractDto : dtoList) {
				LoginDTO dto = (LoginDTO) abstractDto;
				pstInsertLogin.setInt(1, dto.getId());
				pstInsertLogin.setString(2, dto.getLogin());
				pstInsertLogin.setString(3, dto.getEncPassword());
				pstInsertLogin.setBoolean(4, dto.isEmployee());
				pstInsertLogin.setInt(5, dto.getCreatedBy());
				pstInsertLogin.addBatch();
			}
			
			pstInsertLogin.executeBatch();
			
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
		return true;
	}
	
	private LoginDTO populateDTO(ResultSet rs) throws SQLException {
		LoginDTO dto = new LoginDTO();

		dto.setId(rs.getInt("id"));
		dto.setLogin(rs.getString("login"));
		dto.setEmployee(rs.getBoolean("employee"));
		dto.setName(rs.getString("name"));
		
		return dto;
	}
	
	/*
	private static final Format ISO8601_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

	public final String searchUser(String loginname) {
		try {
			final Connection con = getConnection();
			final String sql =
					" Select loginid from logins where " +
					" loginname = '" + loginname + "';";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs != null && rs.next()) {
				String s = rs.getString("loginid");
				con.close();
				return s != null ? s : "";
			}
			con.close();
			return "";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		}
	}

public int searchLoginByUserName(String username){
		Connection con = null;
		try {
			con = getConnection();
			String sql =
					" SELECT loginid " +
					" FROM users " +
					" WHERE username = ? ;";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, username.trim());
			ResultSet rs = pst.executeQuery();

			if (rs != null && rs.next()) {
				return rs.getInt("loginid");
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			closeConnection(con);
		}
		return 0;

	}

	public final synchronized void removeLogin(UserDTO userDTO) {
		Connection connection = null;
		int login_id = 0;

		try {
			connection = getDataSource().getConnection();

			final String sqlSelectLogin = "SELECT loginid FROM users WHERE userid = ?;";

			PreparedStatement pstSelectLogin = connection.prepareStatement(sqlSelectLogin);
			pstSelectLogin.setInt(1, userDTO.getUserid());

			ResultSet rs = pstSelectLogin.executeQuery();

			if (rs != null && rs.next()) {
				login_id = rs.getInt("loginid");
			}

			if (login_id != 0) {
				final String sqlUpdateUser = "UPDATE users SET loginid = null WHERE userid = ?;";

				PreparedStatement pstUpdateUser = connection.prepareStatement(sqlUpdateUser);
				pstUpdateUser.setInt(1, userDTO.getUserid());
				pstUpdateUser.executeUpdate();

				final String sqlInsertLogin = "DELETE FROM logins WHERE loginid = ?;";

				PreparedStatement pstInsertLogin = connection.prepareStatement(sqlInsertLogin);
				pstInsertLogin.setInt(1, login_id);
				pstInsertLogin.executeUpdate();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			closeConnection(connection);
		}
	}

 */
}
