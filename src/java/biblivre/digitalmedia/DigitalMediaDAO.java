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
package biblivre.digitalmedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.DatabaseFile;
import biblivre.core.file.MemoryFile;

public class DigitalMediaDAO extends AbstractDAO {

	public static DigitalMediaDAO getInstance(String schema) {
		return (DigitalMediaDAO) AbstractDAO.getInstance(DigitalMediaDAO.class, schema);
	}

	public final Integer save(MemoryFile file) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			PGConnection pgcon = this.getPGConnection(con);

			if (pgcon == null) {
				throw new Exception("Invalid Delegating Connection");
			}
			
			Integer serial = file.getId();
			if (serial == null) {
				serial = this.getNextSerial("digital_media_id_seq");
				file.setId(serial);
			}
			
			if (serial != 0) {
				LargeObjectManager lobj = pgcon.getLargeObjectAPI();
				long oid = lobj.createLO();

				LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
				InputStream is = file.getNewInputStream();

				byte buf[] = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = is.read(buf)) > 0) {
					obj.write(buf, 0, bytesRead);
				}

				obj.close();

				String sql = "INSERT INTO digital_media (id, name, blob, content_type, size) VALUES (?, ?, ?, ?, ?);";

				PreparedStatement pst = con.prepareStatement(sql);
				pst.setInt(1, serial);
				pst.setString(2, file.getName());
				pst.setLong(3, oid);
				pst.setString(4, file.getContentType());
				pst.setLong(5, file.getSize());

				pst.executeUpdate();
				pst.close();
				file.close();

				this.commit(con);
			} else {
				this.rollback(con);
			}
			return serial;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}


	public final long importFile(File file) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);

			PGConnection pgcon = this.getPGConnection(con);

			if (pgcon == null) {
				throw new Exception("Invalid Delegating Connection");
			}

			LargeObjectManager lobj = pgcon.getLargeObjectAPI();
			long oid = lobj.createLO();

			LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
			InputStream is = new FileInputStream(file);

			byte buf[] = new byte[4096];
			int bytesRead = 0;
			while ((bytesRead = is.read(buf)) > 0) {
				obj.write(buf, 0, bytesRead);
			}

			obj.close();
			is.close();

			this.commit(con);
			
			return oid;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public final DatabaseFile load(int id, String name) {
		Connection con = null;
		DatabaseFile file = null;

		try {
			con = this.getConnection();
			con.setAutoCommit(false);

			PGConnection pgcon = this.getPGConnection(con);

			if (pgcon == null) {
				throw new Exception("Invalid Delegating Connection");
			}
			
			LargeObjectManager lobj = pgcon.getLargeObjectAPI();

			StringBuilder sql = new StringBuilder();
			// We check both ID and FILE_NAME for security reasons, so users can't "guess"
			// id's and get the files.
			sql.append("SELECT name, blob, content_type, size, created FROM digital_media ");
			sql.append("WHERE id = ? AND name = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, id);
			pst.setString(2, name);

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				long oid = rs.getLong("blob");
				LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
				
				file = new DatabaseFile(con, obj);

				file.setName(rs.getString("name"));
				file.setContentType(rs.getString("content_type"));
				file.setLastModified(rs.getTimestamp("created").getTime());
				file.setSize(rs.getLong("size"));
			}
		} catch (Exception e) {
			this.rollback(con);
			this.closeConnection(con);

			throw new DAOException(e);
		} finally {
			// We must leave this connection open. file.close() will close it when needed.
		}

		return file;
	}
	
	public boolean delete(int id) {
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			// We check both ID and FILE_NAME for security reasons, so users can't "guess"
			// id's and get the files.
			sql.append("DELETE FROM digital_media ");
			sql.append("WHERE id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, id);

			int deleted = pst.executeUpdate();
			// Find out if we need to check how many records were deleted from DB.
			return deleted > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public List<DigitalMediaDTO> list() {
		Connection con = null;
		List<DigitalMediaDTO> list = new ArrayList<DigitalMediaDTO>();
		try {
			con = this.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT id, blob, name FROM digital_media;");
			
			while (rs.next()) {
				DigitalMediaDTO dto = new DigitalMediaDTO();
				dto.setId(rs.getInt("id"));
				dto.setBlob(rs.getLong("blob"));
				dto.setName(rs.getString("name"));
				list.add(dto);
			}
			
			return list;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
}
