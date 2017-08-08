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
package biblivre.administration.setup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;

import biblivre.administration.backup.BackupBO;
import biblivre.administration.backup.BackupScope;
import biblivre.administration.backup.BackupType;
import biblivre.administration.backup.RestoreBO;
import biblivre.administration.backup.RestoreDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.StaticBO;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.MemoryFile;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.schemas.Schemas;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;

public class Handler extends AbstractHandler {

	public void cleanInstall(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		
		boolean isNewLibrary = Configurations.getBoolean(schema, Constants.CONFIG_NEW_LIBRARY);
		boolean success = true;
		
		if (!isNewLibrary) {
			SchemaDTO dto = new SchemaDTO();
			dto.setName(Constants.BIBLIVRE);
			dto.setSchema(schema);
			dto.setCreatedBy(request.getLoggedUserId());

			State.start();
			State.writeLog(request.getLocalizedText("multi_schema.manage.log_header"));
			
			File template = new File(request.getSession().getServletContext().getRealPath("/"), "biblivre_template_4.0.0.sql");			
			success = Schemas.createSchema(dto, template, false);
			
			if (success) {
				State.finish();
			} else {			
				State.cancel();
			}
		}
		
		if (success) {
			ConfigurationsDTO dto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
			Configurations.save(schema, dto, 0);
		}

		try {
			this.json.put("success", success);
		} catch (JSONException e) {}
	}
	
	// http://localhost:8080/Biblivre5/?controller=json&module=administration.backup&action=list_restores
	public void listRestores(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		RestoreBO bo = RestoreBO.getInstance(schema);

		LinkedList<RestoreDTO> list = bo.list();
		
		try {
			this.json.put("success", true);

			for (RestoreDTO dto : list) {
				this.json.append("restores", dto.toJSONObject());
			}
		} catch (JSONException e) {}
	}

	public void uploadBiblivre4(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Boolean mediaUpload = request.getBoolean("media_upload", false);
		
		BackupBO bo = BackupBO.getInstance(schema);
		MemoryFile file = request.getFile(mediaUpload ? "biblivre4backupmedia" : "biblivre4backup");
		
		String extension = file.getName().endsWith("b4bz") ? "b4bz" : "b5bz";
		
		File path = bo.getBackupDestination();
		String uuid = UUID.randomUUID().toString() + "." + extension;
		File backup = new File(path, uuid);
		
		boolean success = true;
		RestoreDTO dto = null;

		try {
			file.copy(new FileOutputStream(backup));

			RestoreBO rbo = RestoreBO.getInstance(schema);
			dto = rbo.getRestoreDTO(backup.getName());
		} catch (Exception e) {
			success = false;
		}
		try {
			this.json.put("success", success);
			this.json.put("file", uuid);
			
			if (success && dto != null) {
				this.json.put("metadata", dto.toJSONObject());
			}
		} catch (JSONException e) {}
	}
	
	public void uploadBiblivre3(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		boolean success = false;		
		try {
			State.start();
			State.writeLog(request.getLocalizedText("administration.setup.biblivre3restore.log_header"));

			MemoryFile file = request.getFile("biblivre3backup");
			File gzip = new File(FileIOUtils.createTempDir(), file.getName());
			OutputStream os = new FileOutputStream(gzip);
			
			file.copy(os);

			os.close();
			
			RestoreBO bo = RestoreBO.getInstance(schema);
			success = bo.restoreBiblivre3(gzip);
			
			if (success) {
				State.finish();
			} else {
				State.cancel();
			}
		} catch (ValidationException e) {
			this.setMessage(e);
			State.writeLog(request.getLocalizedText(e.getMessage()));
			
			if (e.getCause() != null) {
				State.writeLog(ExceptionUtils.getStackTrace(e.getCause()));
			}
			
			State.cancel();
		} catch (Exception e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();
		}

		try {
			this.json.put("success", success);
		} catch (JSONException e) {}
	}
	
	private Map<String, String> breakString(String string) {
		Map<String, String> ret = new HashMap<String, String>();

		if (StringUtils.isBlank(string)) {
			return ret;
		}

		for (String s : string.split(",")) {
			if (StringUtils.isBlank(s)) {
				continue;
			}

			String[] split = s.split(":");

			if (split.length != 2) {
				continue;
			}

			ret.put(split[0], split[1]);
		}
		
		return ret;
	}

	private boolean checkForPartialBackup(RestoreDTO dto, ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		String mediaFileBackup = request.getString("mediaFileBackup");
		Boolean skip = request.getBoolean("skip", false);

		try {
			RestoreBO bo = RestoreBO.getInstance(schema);			

			switch (dto.getType()) {
				case FULL: {
					// Full backup User may restore it
					return true;
				}
				
				case DIGITAL_MEDIA_ONLY: {
					State.writeLog(request.getLocalizedText("administration.setup.biblivre4restore.error.digital_media_only_selected"));
					this.json.put("success", false);
					return false;
				}
				
				case EXCLUDE_DIGITAL_MEDIA: {
					if (skip) {
						return true;
					}

					if (StringUtils.isNotBlank(mediaFileBackup)) {
						RestoreDTO partialDto = bo.getRestoreDTO(mediaFileBackup);
						
						if (partialDto.getType() == BackupType.DIGITAL_MEDIA_ONLY) {
							return true;						
						}
						
						
						State.writeLog(request.getLocalizedText("administration.setup.biblivre4restore.error.digital_media_only_should_be_selected"));
						this.json.put("success", false);
						return false;
					} 
					
					this.json.put("success", true);
					this.json.put("ask_for_media_backup", true);						
					return false;
				}
			}			
		} catch (Exception e) {
			State.writeLog(ExceptionUtils.getStackTrace(e));
		}

		return false;
	}

	
	// http://localhost:8080/Biblivre5/?controller=json&module=administration.backup&action=restore&filename=Biblivre Backup 2012-09-15 22h56m22s Full.b5bz
	public void restore(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String filename = request.getString("filename");
		String mediaFileBackup = request.getString("mediaFileBackup");
		String selectedBackupSchema = request.getString("selected_schema");
		String schemasMap = request.getString("schemas_map");
		String type = request.getString("type", "partial");

		boolean success = false;
		try {
			State.start();
			State.writeLog(request.getLocalizedText("administration.setup.biblivre4restore.log_header"));

			BackupBO bbo = BackupBO.getInstance(schema);
			BackupScope restoreScope = bbo.getBackupScope();

			RestoreBO bo = RestoreBO.getInstance(schema);
			RestoreDTO dto = bo.getRestoreDTO(filename);

			if (!this.checkForPartialBackup(dto, request, response)) {
				State.cancel();	
				return;
			}
			
			BackupScope backupScope = dto.getBackupScope();

			State.writeLog(backupScope.toString() + " => " + restoreScope.toString());

			Map<String, String> restoreSchemas = new HashMap<String, String>();

			if (restoreScope == BackupScope.SINGLE_SCHEMA) {
				// Se o backup possui schema global e multi bibliotecas não está habilitado, restaura o global também
				
				if (dto.getSchemas().containsKey(Constants.GLOBAL_SCHEMA)) {
					restoreSchemas.put(Constants.GLOBAL_SCHEMA, Constants.GLOBAL_SCHEMA);
				}
			}

			if (restoreScope == BackupScope.SINGLE_SCHEMA || restoreScope == BackupScope.SINGLE_SCHEMA_FROM_MULTI_SCHEMA) {
				// Schema de destino é o schema atual e só um schema de origem pode ter sido selecionado
				if (StringUtils.isNotBlank(selectedBackupSchema)) {
					// Se o usuário selecionou, usa o selecionado
					restoreSchemas.put(selectedBackupSchema, schema);
				} else {
					// Se o usuário não selecionou, use o que encontrar. Geralmente só vai ter um, já que origem MULTI SCHEMA requer seleção acima

					for (String s : dto.getSchemas().keySet()) {
						if (!s.equals(Constants.GLOBAL_SCHEMA)) {
							restoreSchemas.put(s, schema);
							break;
						}
					}
				}
			} else {
				// O destino é multi bibliotecas. Usar o que o usuário selecionou na tela.
				if (type.equals("complete")) {
					// Marcar para excluir todos os schemas
					dto.setPurgeAll(true);

					for (String s : dto.getSchemas().keySet()) {
						restoreSchemas.put(s, s);
					}
				} else {
					restoreSchemas.putAll(this.breakString(schemasMap));
				}
			}

			// Validando se schemas de origem existem no backup e se esquemas de destino são válidos
			Set<String> uniqueCheck = new TreeSet<String>();
			if (restoreSchemas.size() == 1 && restoreSchemas.containsKey(Constants.GLOBAL_SCHEMA)) {
				throw new ValidationException("administration.maintenance.backup.error.no_schema_selected");
			}

			for (Entry<String, String> entry : restoreSchemas.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				
				if (StringUtils.isBlank(key) || !dto.getSchemas().containsKey(key)) {
					throw new ValidationException("administration.maintenance.backup.error.invalid_origin_schema");
				}

				if (StringUtils.isBlank(value) || !Schemas.isValidName(value)) {
					throw new ValidationException("administration.maintenance.backup.error.invalid_destination_schema");	
				}

				if (key.equals(Constants.GLOBAL_SCHEMA) && !key.equals(value)) {
					throw new ValidationException("administration.maintenance.backup.error.invalid_destination_schema");
				}

				uniqueCheck.add(entry.getValue());
			}

			if (uniqueCheck.size() != restoreSchemas.size()) {
				throw new ValidationException("administration.maintenance.backup.error.duplicated_destination_schema");
			}
			
			dto.setRestoreScope(restoreScope);
			dto.setRestoreSchemas(restoreSchemas);

			RestoreDTO partialDTO = null;
			
			if (StringUtils.isNotBlank(mediaFileBackup)) {
				partialDTO = bo.getRestoreDTO(mediaFileBackup);
			}
			
			success = bo.restore(dto, partialDTO);

			if (success) {
				ConfigurationsDTO cdto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
				Configurations.save(schema, cdto, 0);
				
				State.finish();

				StaticBO.resetCache();
			} else {
				State.cancel();
			}
		} catch (ValidationException e) {
			this.setMessage(e);
			State.writeLog(request.getLocalizedText(e.getMessage()));
			
			if (e.getCause() != null) {
				State.writeLog(ExceptionUtils.getStackTrace(e.getCause()));
			}
			
			State.cancel();
		} catch (Exception e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();
		} catch (Throwable e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();			
		}
		
		try {
			this.json.put("success", success);
		} catch (JSONException e) {}
	}


	public void importBiblivre3(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String origin = request.getString("origin", "biblivre3");

		String[] groups = request.getParameterValues("groups[]");
		List<DataMigrationPhaseGroup> phaseGroups = new ArrayList<DataMigrationPhaseGroup>();

		if (groups != null) {
			for (String group : groups) {
				phaseGroups.add(DataMigrationPhaseGroup.fromString(group));
			}
		}

		if (phaseGroups.size() == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		List<DataMigrationPhase> selectedPhases = new ArrayList<DataMigrationPhase>();
		for (DataMigrationPhaseGroup group : phaseGroups) {
			selectedPhases.addAll(group.getPhases());
		}
		
		boolean success = false;

		try {
			State.start();
			State.writeLog(request.getLocalizedText("administration.setup.biblivre3import.log_header"));
			
			success = DataMigrationBO.getInstance(schema, origin).migrate(selectedPhases);

			if (success) {
				ConfigurationsDTO cdto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
				Configurations.save(schema, cdto, 0);

				StaticBO.resetCache();

				State.finish();
			} else {
				State.cancel();
			}
		} catch (ValidationException e) {
			this.setMessage(e);
			State.writeLog(request.getLocalizedText(e.getMessage()));
			State.cancel();
		} catch (Exception e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();
		}
		
		try {
			this.json.put("success", success);
		} catch (JSONException e) {}		
	}
	
	public void progress(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", true);
			this.json.put("current", State.getCurrentStep());
			this.json.put("total", State.getSteps());
			this.json.put("secondary_current", State.getCurrentSecondaryStep());
			this.json.put("complete", !State.LOCKED.get());
		} catch (JSONException e) {}
	}
}
