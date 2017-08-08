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
package biblivre.core.file;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.largeobject.LargeObject;

import biblivre.core.utils.Constants;

public class DatabaseFile extends BiblivreFile {

	private Connection connection;
	private LargeObject largeObject;

	public DatabaseFile() {
	}

	public DatabaseFile(Connection connection, LargeObject largeObject) {
		this.setConnection(connection);
		this.setLargeObject(largeObject);
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public LargeObject getLargeObject() {
		return this.largeObject;
	}

	public void setLargeObject(LargeObject largeObject) {
		this.largeObject = largeObject;
	}

	@Override
	public boolean exists() {
		LargeObject input = this.getLargeObject();

		return input != null;
	}

	@Override
	public void copy(OutputStream out, long start, long size) throws IOException {
		LargeObject input = this.getLargeObject();
		
		if (input == null) {
			return;
		}

		byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
		int read;

		try {
			if (input.size() == size) {
				while ((read = input.read(buffer, 0, Constants.DEFAULT_BUFFER_SIZE)) > 0) {
					out.write(buffer, 0, read);
				}
			} else {
				input.seek((int) start);
				long toRead = size;
	
				while ((read = input.read(buffer, 0, Constants.DEFAULT_BUFFER_SIZE)) > 0) {
					if ((toRead -= read) > 0) {
						out.write(buffer, 0, read);
					} else {
						out.write(buffer, 0, (int) toRead + read);
						break;
					}
				}
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() {
		LargeObject largeObject = this.getLargeObject();
		if (largeObject != null) {
			this.setLargeObject(null);
			try {
				largeObject.close();
			} catch (SQLException ignore) {
			}
		}

		Connection con = this.getConnection();
		if (con != null) {
			this.setConnection(null);
			try {
				con.commit();
			} catch (SQLException ignore) {
			}

			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (SQLException ignore) {
			}
		}
	}
}
