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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;
import biblivre.core.utils.Pair;

public class BackupDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Map<String, Pair<String, String>> schemas;
	private BackupType type;
	private BackupScope backupScope;
	private File backup;

	private Integer steps;
	private Integer currentStep;
	private boolean downloaded;

	public BackupDTO(Map<String, Pair<String, String>> schemas, BackupType type, BackupScope backupScope) {
		this.setSchemas(schemas);
		this.setType(type);
		this.setBackupScope(backupScope);
	}

	public BackupDTO(String schemas, BackupType type, BackupScope backupScope) {
		this.setSchemas(schemas);
		this.setType(type);
		this.setBackupScope(backupScope);
	}
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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
	
	public String getSchemasString() {
		return new JSONObject(this.schemas).toString();
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
	
	public File getBackup() {
		return this.backup;
	}

	public void setBackup(File backup) {
		this.backup = backup;
	}

	public boolean isDownloaded() {
		return this.downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public Integer getSteps() {
		return this.steps;
	}

	public void setSteps(Integer steps) {
		this.steps = steps;
	}

	public Integer getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(Integer currentStep) {
		this.currentStep = currentStep;
	}
	
	public void increaseCurrentStep() {
		this.currentStep++;
	}
	
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("id", this.getId());
			json.putOpt("schemas", this.getSchemas());
			json.putOpt("type", this.getType());
			json.putOpt("backup_scope", this.getBackupScope());			
			json.putOpt("created", this.getCreated());

			json.putOpt("steps", this.getSteps());
			json.putOpt("current_step", this.getCurrentStep());

			json.putOpt("downloaded", this.isDownloaded());
			
			boolean exists = (this.getBackup() != null && this.getBackup().exists());
			json.putOpt("exists", exists);
		} catch (JSONException e) {
		}

		return json;
	}
}
