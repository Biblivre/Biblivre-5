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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import biblivre.core.utils.Constants;

public class DiskFile extends BiblivreFile {
	private File file;
	
	public DiskFile(File file, String contentType) {
		this.file = file;
		this.setContentType(contentType);
		if (file != null) {
			this.setName(file.getName());
			this.setSize(file.length());
			this.setLastModified(file.lastModified());
		}
	}

	@Override
	public void close() throws IOException {
	}
	
	@Override
	public boolean exists() {
		return this.file != null && this.file.exists();
	}

	@Override
	public void copy(OutputStream out, long start, long size) throws IOException {
		if (this.file == null) {
			return;
		}

		InputStream input = new FileInputStream(this.file);

		input.skip(start);

		byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];
		int read;

		while ((read = input.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		
		input.close();
	}
	
	public boolean delete() {
		return this.file != null && this.file.delete();
	}
}
