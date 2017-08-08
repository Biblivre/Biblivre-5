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

import biblivre.core.AbstractBO;
import biblivre.core.file.DatabaseFile;
import biblivre.core.file.MemoryFile;

public class DigitalMediaBO extends AbstractBO {
	protected DigitalMediaDAO dao;

	public static DigitalMediaBO getInstance(String schema) {
		DigitalMediaBO bo = AbstractBO.getInstance(DigitalMediaBO.class, schema);

		if (bo.dao == null) {
			bo.dao = DigitalMediaDAO.getInstance(schema);
		}

		return bo;
	}

	public Integer save(MemoryFile file) {
		return this.dao.save(file);
	}

	public DatabaseFile load(int id, String name) {
		return this.dao.load(id, name);
	}
	
	public boolean delete(Integer fileId, String fileName) {
		return this.dao.delete(fileId);
	}
}
