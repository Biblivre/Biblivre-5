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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;

public class LoginDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private int id;
	private String login;
	private String encPassword;
	private boolean employee;
	
	private transient String plainPassword;
	private transient String name;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFirstName() {
		String name = StringUtils.defaultString(this.getName()).trim();

		String[] split = name.split(" ");
		return split[0];
	}
	
	public String getLogin() {
		return this.login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getEncPassword() {
		return this.encPassword;
	}
	
	public void setEncPassword(String encPassword) {
		this.encPassword = encPassword;
	}
	
	public String getPlainPassword() {
		return this.plainPassword;
	}
	
	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public boolean isEmployee() {
		return this.employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();

		try {
			json.putOpt("employee", (this.isEmployee()) ? "true" : "false");
		} catch (JSONException e) {
		}

		return json;
	}

}
