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

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;

public abstract class BiblivreFile implements Closeable {
	
	private Integer id;
	private String name;
	private String contentType;
	private long lastModified;
	private long size;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		if (StringUtils.isBlank(name)) {
			this.name = null;
			return;
		}

		if (StringUtils.contains(name, '\\')) {
			name = StringUtils.substringAfterLast(name, "\\");
		}
		
		if (StringUtils.contains(name, '/')) {
			name = StringUtils.substringAfterLast(name, "/");
		}
		
		this.name = name;
	}
	
	public String getContentType() {
		return StringUtils.defaultString(this.contentType, "application/octet-stream");
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public long getLastModified() {
		return this.lastModified != 0 ? this.lastModified : System.currentTimeMillis();
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public long getSize() {
		return this.size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void copy(OutputStream out) throws IOException {
		this.copy(out, 0, this.getSize());
	}

	public abstract boolean exists();
	
	public abstract void copy(OutputStream out, long start, long size) throws IOException;
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
}
