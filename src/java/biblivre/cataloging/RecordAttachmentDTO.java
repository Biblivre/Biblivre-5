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

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;

public class RecordAttachmentDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;
	
	private String path;
	private String file;
	private String name;
	private String uri;
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFile() {
		return this.file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUri() {
		return this.uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		
		try {
			json.putOpt("path", this.getPath());
			json.putOpt("file", this.getFile());
			json.putOpt("name", this.getName());
			json.putOpt("uri", this.getUri());
		} catch (JSONException e) {
		}
        
		return json;
	}
}
