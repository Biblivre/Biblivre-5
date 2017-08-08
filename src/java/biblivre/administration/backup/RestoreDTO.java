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
package biblivre.administration.backup;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;
import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;

public class RestoreDTO extends AbstractDTO implements Comparable<RestoreDTO> {
	private static final long serialVersionUID = 1L;
	
	private Map<String, Pair<String, String>> schemas;
	private BackupType type;
	private BackupScope backupScope;
	private File backup;
	private boolean valid;
	private boolean purgeAll;

	private transient BackupScope restoreScope;	
	private transient Map<String, String> restoreSchemas;

	public RestoreDTO() {
	}
	
	public RestoreDTO(BackupDTO dto) {
		this.setSchemas(dto.getSchemas());
		this.setType(dto.getType());
		this.setBackupScope(dto.getBackupScope());
		this.setCreated(dto.getCreated());
		this.setBackup(dto.getBackup());
	}

	public RestoreDTO (JSONObject json) throws Exception {
		this.setSchemas(json.getString("schemas"));
		this.setType(BackupType.fromString(json.getString("type")));
		this.setBackupScope(BackupScope.fromString(json.getString("backup_scope")));
		this.setValid(true);

		String created = json.getString("created");
		
		try {
			if (created != null) {
				this.setCreated(Constants.DEFAULT_DATE_FORMAT_TIMEZONE.parse(created));
			}
		} catch (ParseException e) {
			try {
				this.setCreated(Constants.DEFAULT_DATE_FORMAT.parse(created));
			} catch (ParseException e2) {
				this.setCreated(new Date(0));
				this.setValid(false);
			}
		}
	}
	
	public Map<String, Pair<String, String>> getSchemas() {
		return this.schemas;
	}

	public void setSchemas(Map<String, Pair<String, String>> schemas) {
		this.schemas = schemas;
	}
	
	public void setSchemas(String schemas) {
		Map<String, Pair<String, String>> map = new HashMap<String, Pair<String, String>>();

		try {
			JSONObject json = new JSONObject(schemas);
		
			Iterator<String> iterator = json.keys();
	
			while (iterator.hasNext()) {
				String key = iterator.next();
				map.put(key, Pair.<String, String>fromJSONObject(json.getJSONObject(key)));
			}

		} catch (JSONException e) {
		}

		this.schemas = map;
	}

	public BackupType getType() {
		return this.type;
	}

	public void setType(BackupType type) {
		this.type = type;
	}

	public BackupScope getBackupScope() {
		return this.backupScope;
	}

	public void setBackupScope(BackupScope backupScope) {
		this.backupScope = backupScope;
	}

	public BackupScope getRestoreScope() {
		return this.restoreScope;
	}

	public void setRestoreScope(BackupScope restoreScope) {
		this.restoreScope = restoreScope;
	}	

	public File getBackup() {
		return this.backup;
	}

	public void setBackup(File backup) {
		this.backup = backup;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public boolean isPurgeAll() {
		return this.purgeAll;
	}

	public void setPurgeAll(boolean purgeAll) {
		this.purgeAll = purgeAll;
	}
	
	public Map<String, String> getRestoreSchemas() {
		return this.restoreSchemas;
	}

	public void setRestoreSchemas(Map<String, String> restoreSchemas) {
		this.restoreSchemas = restoreSchemas;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("schemas", this.getSchemas());
			json.putOpt("type", this.getType());
			json.putOpt("backup_scope", this.getBackupScope());
			
			if (this.getCreated() != null) {
				json.putOpt("created", Constants.DEFAULT_DATE_FORMAT_TIMEZONE.format(this.getCreated()));
			}
			
			if (this.getBackup() != null) {
				json.putOpt("file", this.getBackup().getName());
			}

			json.putOpt("valid", this.isValid());
		} catch (JSONException e) {
		}

		return json;
	}
	
	@Override
	public int compareTo(RestoreDTO other) {
		if (other == null) {
			return -1;
		}
		
		if (this.getCreated() != null && other.getCreated() != null) {
			return this.getCreated().compareTo(other.getCreated()) * -1; // Order Desc
		}

		if (this.getBackup() != null && other.getBackup() != null) {
			return this.getBackup().getName().compareTo(other.getBackup().getName());
		}
		
		return 0;
	}
}
