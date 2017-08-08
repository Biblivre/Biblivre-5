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
package biblivre.circulation.user;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;

public class UserDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private Integer type;
	private String photoId;
	private UserStatus status;
	private Integer loginId;
	private Boolean userCardPrinted;
	
	private HashMap<String, String> fields;
	
	transient private Integer currentLendings;
	transient private String usertypeName;
	
	public UserDTO() {
		this.fields = new HashMap<String, String>();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getEnrollment() {
		return StringUtils.leftPad(String.valueOf(this.getId()), 5, "0");
	}

	public int getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPhotoId() {
		return this.photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public UserStatus getStatus() {
		return this.status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public void setStatus(String status) {
		this.status = UserStatus.fromString(status);
	}

	public Integer getLoginId() {
		return this.loginId;
	}

	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public HashMap<String, String> getFields() {
		return this.fields;
	}

	public void setFields(HashMap<String, String> fields) {
		this.fields = fields;
	}
	
	public void addField(String key, String value) {
		this.fields.put(key, value);
	}
	
	public Integer getCurrentLendings() {
		return this.currentLendings;
	}

	public void setCurrentLendings(Integer currentLendings) {
		this.currentLendings = currentLendings;
	}
	
	public Boolean getUserCardPrinted() {
		return this.userCardPrinted == null ? Boolean.FALSE : this.userCardPrinted;
	}

	public void setUserCardPrinted(Boolean userCardPrinted) {
		this.userCardPrinted = userCardPrinted;
	}

	public String getUsertypeName() {
		return this.usertypeName;
	}

	public void setUsertypeName(String usertypeName) {
		this.usertypeName = usertypeName;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			
			json.putOpt("id", this.getId());
			json.putOpt("enrollment", this.getEnrollment());
			json.putOpt("name", this.getName());
			json.putOpt("loginId", this.getLoginId());
			json.putOpt("status", this.getStatus());
			json.putOpt("type", this.getType());
			json.putOpt("type_name", this.getUsertypeName());
			json.putOpt("photo_id", this.getPhotoId());
			json.putOpt("fields", this.getFields());
			json.putOpt("current_lendings", this.getCurrentLendings());

			json.putOpt("created", this.getCreated());
			json.putOpt("modified", this.getModified());
		} catch (JSONException e) {
		}
		
		return json;
	}
	
}
