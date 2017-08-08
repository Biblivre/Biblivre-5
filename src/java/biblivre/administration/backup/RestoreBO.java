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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import biblivre.administration.setup.DataMigrationDAO;
import biblivre.administration.setup.State;
import biblivre.core.AbstractBO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.DatabaseUtils;
import biblivre.core.utils.FileIOUtils;
import biblivre.digitalmedia.DigitalMediaDAO;

public class RestoreBO extends AbstractBO {
	private BackupDAO dao;

	public static RestoreBO getInstance(String schema) {
		RestoreBO bo = AbstractBO.getInstance(RestoreBO.class, schema);

		if (bo.dao == null) {
			bo.dao = BackupDAO.getInstance(schema);
		}
		
		return bo;
	}

	public LinkedList<RestoreDTO> list() {
		LinkedList<RestoreDTO> list = new LinkedList<RestoreDTO>();
		
		BackupBO backupBO = BackupBO.getInstance(this.getSchema());

		File path = backupBO.getBackupDestination();

		if (path == null) {
			path = FileUtils.getTempDirectory();
		}
		
		if (path == null) {
			throw new ValidationException("administration.maintenance.backup.error.invalid_restore_path");
		}
		
		for (File backup : FileUtils.listFiles(path, new String[]{"b4bz", "b5bz"}, false)) {
			RestoreDTO dto = this.getRestoreDTO(backup);

			if (dto.isValid()) {
				list.add(dto);
			}
		}

		Collections.sort(list);
		
		return list;
	}
	
	public boolean restore(RestoreDTO dto, RestoreDTO partial) {
		if (!dto.isValid() || dto.getBackup() == null || !dto.getBackup().exists()) {
			throw new ValidationException("administration.maintenance.backup.error.corrupted_backup_file");
		}
		
		File tmpDir = null;
		try {
			tmpDir = FileIOUtils.unzip(dto.getBackup());
		} catch (Exception e) {
			throw new ValidationException("administration.maintenance.backup.error.couldnt_unzip_backup", e);
		}
		
		String extension = (dto.getBackup().getPath().endsWith("b5bz")) ? "b5b" : "b4b";

		if (partial != null) {
			File partialTmpDir = null;
			try {
				partialTmpDir = FileIOUtils.unzip(partial.getBackup());

				for (File partialFile : partialTmpDir.listFiles()) {
					if (partialFile.getName().equals("backup.meta")) {
						FileUtils.deleteQuietly(partialFile);
						
					} else if (partialFile.isDirectory()) {

						FileUtils.moveDirectoryToDirectory(partialFile, tmpDir, true);
					} else {

						FileUtils.moveFileToDirectory(partialFile, tmpDir, true);
					}
				}

				FileUtils.deleteQuietly(partialTmpDir);
			} catch (Exception e) {
				throw new ValidationException("administration.maintenance.backup.error.couldnt_unzip_backup", e);
			}
		}

		
		// Counting restore steps
		long steps = 0;

		for (String schema : dto.getRestoreSchemas().keySet()) {
			steps += FileIOUtils.countLines(new File(tmpDir, schema + ".schema." + extension));
			steps += FileIOUtils.countLines(new File(tmpDir, schema + ".data." + extension));

			if (!schema.equals(Constants.GLOBAL_SCHEMA)) {
				steps += FileIOUtils.countLines(new File(tmpDir, schema + ".media." + extension));
			}			
		}

		State.setSteps(steps);
		State.writeLog("Restoring " + dto.getRestoreSchemas().size() + " schemas for a total of " + steps + " SQL lines");
		
		try {
			return this.restoreBackup(dto, tmpDir);
		} catch (ValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new ValidationException("administration.maintenance.backup.error.couldnt_restore_backup", e);
		} finally {
			FileUtils.deleteQuietly(tmpDir);			
		}
	}
	
	
	public boolean restoreBiblivre3(File file) {
		if (file == null || !file.exists()) {
			throw new ValidationException("administration.maintenance.backup.error.corrupted_backup_file");
		}

		File sql = null;
		try {
			sql = FileIOUtils.ungzipBackup(file);
		} catch (Exception e) {
			throw new ValidationException("administration.maintenance.backup.error.couldnt_unzip_backup", e);
		}

		try {
			boolean success = (this.recreateBiblivre3RestoreDatabase(false) || this.recreateBiblivre3RestoreDatabase(true));
			if (!success) {
				return false;
			}
			
			try {
				DataMigrationDAO dao = DataMigrationDAO.getInstance(this.getSchema(), " biblivre4_b3b_restore");
				for (int i = 0; i < 1000; i++) {
					dao.testDatabaseConnection();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return this.restoreBackupBiblivre3(sql);
		} catch (Exception e) {
			throw new ValidationException("administration.maintenance.backup.error.couldnt_restore_backup", e);
		} finally {
			FileUtils.deleteQuietly(file);
			FileUtils.deleteQuietly(sql);			
		}
	}

	public synchronized boolean restoreBackup(RestoreDTO dto, File path) {
		Map<String, String> restoreSchemas = dto.getRestoreSchemas();
		if (restoreSchemas == null || restoreSchemas.size() == 0) {
			throw new ValidationException("administration.maintenance.backup.error.no_schema_selected");
		}

		String extension = (dto.getBackup().getPath().endsWith("b5bz")) ? "b5b" : "b4b";
		
		long date = new Date().getTime();
		Set<String> databaseSchemas = this.dao.listDatabaseSchemas();

		Map<String, String> deleteSchemas = new HashMap<String, String>();
		Map<String, String> preRenameSchemas = new HashMap<String, String>();
		Map<String, String> postRenameSchemas = new HashMap<String, String>();
		Map<String, String> restoreRenamedSchemas = new HashMap<String, String>();

		// Para cada schema sendo restaurado
		for (String originalSchemaName : restoreSchemas.keySet()) {
			// Verificamos o nome de destino do schema. Lembrando que o usuário pode escolher para qual schema o backup será restaurado.
			// Isso significa que o originalSchemaName (nome do schema no backup) pode ser diferente do finalSchemaName (nome do schem depois de restaurado).
			String finalSchemaName = restoreSchemas.get(originalSchemaName);

			if (databaseSchemas.contains(finalSchemaName)) {
				String aux = "_" + finalSchemaName + "_" + date;
				preRenameSchemas.put(finalSchemaName, aux);
				deleteSchemas.put(finalSchemaName, aux);
			}

			if (!originalSchemaName.equals(finalSchemaName)) {
				postRenameSchemas.put(originalSchemaName, finalSchemaName);
			}
		}

		for (String originalSchemaName : restoreSchemas.keySet()) {	
			// Verifica se o schema está atrapalhando alguma importação. Neste caso, renomeia temporariamente.
			// Se o schema estiver na lista de schemas a serem deletados, não se preocupar em renomear e restaurar
			if (!deleteSchemas.containsKey(originalSchemaName) && databaseSchemas.contains(originalSchemaName)) {
				String aux = "_" + originalSchemaName + "_" + date;
				preRenameSchemas.put(originalSchemaName, aux);
				restoreRenamedSchemas.put(aux, originalSchemaName);
			}
		}
		
		if (dto.isPurgeAll()) {
			for (String remainingSchema : databaseSchemas) {
				if (remainingSchema.equals("public")) {
					continue;
				}
				
				if (remainingSchema.equals(Constants.GLOBAL_SCHEMA)) {
					continue;
				}
				
				if (!deleteSchemas.containsKey(remainingSchema)) {
					deleteSchemas.put(remainingSchema, remainingSchema);
				}
			}
		}
		
		File psql = DatabaseUtils.getPsql(this.getSchema());

		if (psql == null) {
			throw new ValidationException("administration.maintenance.backup.error.psql_not_found");
		}

		String[] commands = new String[] {
			psql.getAbsolutePath(),		// 0
			"--single-transaction",		// 1
			"--host",					// 2
			"localhost",				// 3
			"--port",					// 4
			"5432",						// 5
			"-v",						// 6
			"ON_ERROR_STOP=1",			// 7
			"--file",					// 8
			"-",						// 9
		};

		ProcessBuilder pb = new ProcessBuilder(commands);

		pb.environment().put("PGDATABASE", "biblivre4");
		pb.environment().put("PGUSER", "biblivre");
		pb.environment().put("PGPASSWORD", "abracadabra");

		pb.redirectErrorStream(true);

		try {
			State.writeLog("Starting psql");

			Process p = pb.start();

			InputStreamReader isr = new InputStreamReader(p.getInputStream(), "UTF-8");
			final BufferedReader br = new BufferedReader(isr);

			OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					String outputLine;
					try {
						while ((outputLine = br.readLine()) != null) {
							State.writeLog(outputLine);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			t.start();

			// Preprocessing renames
			for (String originalSchemaName : preRenameSchemas.keySet()) {
				String finalSchemaName = preRenameSchemas.get(originalSchemaName);

				State.writeLog("Renaming schema " + originalSchemaName + " to " + finalSchemaName);
				bw.write("ALTER SCHEMA \"" + originalSchemaName + "\" RENAME TO \"" + finalSchemaName + "\";\n");
			}
			bw.flush();

			if (restoreSchemas.containsKey(Constants.GLOBAL_SCHEMA)) {
				State.writeLog("Processing schema for '" + Constants.GLOBAL_SCHEMA + "'");
				this.processRestore(new File(path, Constants.GLOBAL_SCHEMA + ".schema." + extension), bw);
				
				State.writeLog("Processing data for '" + Constants.GLOBAL_SCHEMA + "'");				
				this.processRestore(new File(path, Constants.GLOBAL_SCHEMA + ".data." + extension), bw);
				bw.flush();
			}

			for (String schema : restoreSchemas.keySet()) {
				if (schema.equals(Constants.GLOBAL_SCHEMA)) {
					continue;
				}

				// Restoring database schema (creating tables and indexes)
				State.writeLog("Processing schema for '" + schema + "'");
				this.processRestore(new File(path, schema + ".schema." + extension), bw);

				// Restoring database data
				State.writeLog("Processing data for '" + schema + "'");				
				this.processRestore(new File(path, schema + ".data." + extension), bw);

				// Restoring digital media files
				State.writeLog("Processing media for '" + schema + "'");
				this.processMediaRestore(new File(path, schema + ".media." + extension), bw, schema);
				this.processMediaRestoreFolder(new File(path, schema), bw);

				bw.flush();
			}

			// Postprocessing renames
			for (String renamedSchemaName : postRenameSchemas.keySet()) {
				String originalSchemaName = postRenameSchemas.get(renamedSchemaName);

				State.writeLog("Renaming schema " + renamedSchemaName + " to " + originalSchemaName);
				bw.write("ALTER SCHEMA \"" + renamedSchemaName + "\" RENAME TO \"" + originalSchemaName + "\";\n");
			}
			bw.flush();

			// Postprocessing renames
			for (String renamedSchemaName : restoreRenamedSchemas.keySet()) {
				String originalSchemaName = restoreRenamedSchemas.get(renamedSchemaName);

				State.writeLog("Renaming schema " + renamedSchemaName + " to " + originalSchemaName);
				bw.write("ALTER SCHEMA \"" + renamedSchemaName + "\" RENAME TO \"" + originalSchemaName + "\";\n");
			}
			bw.flush();

			
			// Postprocessing deletes
			for (String originalSchemaName : deleteSchemas.keySet()) {
				String aux = deleteSchemas.get(originalSchemaName);
				State.writeLog("Droping schema " + aux);
				
				if (!originalSchemaName.equals(Constants.GLOBAL_SCHEMA)) {
					bw.write("DELETE FROM \"" + aux + "\".digital_media;\n");
				}
				
				bw.write("DROP SCHEMA \"" + aux + "\" CASCADE;\n");

				if (!originalSchemaName.equals(Constants.GLOBAL_SCHEMA)) {
					bw.write("DELETE FROM \"" + Constants.GLOBAL_SCHEMA + "\".schemas WHERE \"schema\" = '" + originalSchemaName + "';\n");
				}
			}
			bw.flush();
			
			for (String originalSchemaName : restoreSchemas.keySet()) {
				String finalSchemaName = restoreSchemas.get(originalSchemaName);

				if (!finalSchemaName.equals(Constants.GLOBAL_SCHEMA)) {
					String schemaTitle = finalSchemaName;
					
					try {
						schemaTitle = dto.getSchemas().get(originalSchemaName).getLeft();
						schemaTitle = schemaTitle.replaceAll("'", "''").replaceAll("\\", "\\\\");
					} catch (Exception e) {
					}

					bw.write("DELETE FROM \"" + Constants.GLOBAL_SCHEMA + "\".schemas WHERE \"schema\" = '" + finalSchemaName + "';\n");
					bw.write("INSERT INTO \"" + Constants.GLOBAL_SCHEMA + "\".schemas (schema, name) VALUES ('" + finalSchemaName + "', E'" + schemaTitle + "');\n");
				}
			}
			
			bw.write("DELETE FROM \"" + Constants.GLOBAL_SCHEMA + "\".schemas WHERE \"schema\" not in (SELECT schema_name FROM information_schema.schemata);\n");
			
			bw.write("ANALYZE;\n");
			bw.close();
			
			p.waitFor();

			return p.exitValue() == 0;
		} catch (IOException e) {
			this.logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage(), e);
		}

		return false;
	}
	
	public synchronized boolean recreateBiblivre3RestoreDatabase(boolean tryPGSQL92) {		
		File psql = DatabaseUtils.getPsql(this.getSchema());

		if (psql == null) {
			throw new ValidationException("administration.maintenance.backup.error.psql_not_found");
		}

		String[] commands = new String[] {
			psql.getAbsolutePath(),		// 0
			"--host",					// 1
			"localhost",				// 2
			"--port",					// 3
			"5432",						// 4
			"-v",						// 6
			"ON_ERROR_STOP=1",			// 7
			"--file",					// 8
			"-",						// 9
		};

		ProcessBuilder pb = new ProcessBuilder(commands);

		pb.environment().put("PGUSER", "biblivre");
		pb.environment().put("PGPASSWORD", "abracadabra");
		pb.environment().put("PGDATABASE", "biblivre4");

		pb.redirectErrorStream(true);

		try {
			Process p = pb.start();

			InputStreamReader isr = new InputStreamReader(p.getInputStream(), "UTF-8");
			final BufferedReader br = new BufferedReader(isr);

			OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					String outputLine;
					try {
						while ((outputLine = br.readLine()) != null) {
							State.writeLog(outputLine);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			t.start();

			if (tryPGSQL92) {
				bw.write("SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'biblivre4_b3b_restore' AND pid <> pg_backend_pid();\n");
			} else {
				bw.write("SELECT pg_terminate_backend(pg_stat_activity.procpid) FROM pg_stat_activity WHERE pg_stat_activity.datname = 'biblivre4_b3b_restore' AND procpid <> pg_backend_pid();\n");
			}

			bw.flush();
			bw.write("DROP DATABASE IF EXISTS biblivre4_b3b_restore;\n");
			bw.flush();
			bw.write("CREATE DATABASE biblivre4_b3b_restore WITH OWNER = biblivre ENCODING = 'UTF8';\n");
			bw.flush();

			bw.close();

			p.waitFor();
			
			return p.exitValue() == 0;
		} catch (IOException e) {
			this.logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	public synchronized boolean restoreBackupBiblivre3(File sql) {		
		File psql = DatabaseUtils.getPsql(this.getSchema());

		if (psql == null) {
			throw new ValidationException("administration.maintenance.backup.error.psql_not_found");
		}

		String[] commands = new String[] {
			psql.getAbsolutePath(),		// 0
			"--single-transaction",		// 1
			"--host",					// 2
			"localhost",				// 3
			"--port",					// 4
			"5432",						// 5
			"-v",						// 6
			"ON_ERROR_STOP=1",			// 7
			"--file",					// 8
			"-",						// 9
		};

		ProcessBuilder pb = new ProcessBuilder(commands);

		pb.environment().put("PGUSER", "biblivre");
		pb.environment().put("PGPASSWORD", "abracadabra");
		pb.environment().put("PGDATABASE", "biblivre4");

		pb.redirectErrorStream(true);

		try {
			Process p = pb.start();

			InputStreamReader isr = new InputStreamReader(p.getInputStream(), "UTF-8");
			final BufferedReader br = new BufferedReader(isr);

			OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					String outputLine;
					try {
						while ((outputLine = br.readLine()) != null) {
							State.writeLog(outputLine);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			t.start();
			
			BufferedReader sqlBr = new BufferedReader(new InputStreamReader(new FileInputStream(sql), "UTF-8"));
			
			String inputLine;
			boolean validBackup = false;
			
			while ((inputLine = sqlBr.readLine()) != null) {
				if (inputLine.trim().startsWith("\\connect biblivre")) {
					validBackup = true;
					break;
				}
			}
			
			if (!validBackup) {
				sqlBr.close();
				State.writeLog("Never reached \\connect biblivre;");
				throw new ValidationException("administration.maintenance.backup.error.corrupted_backup_file");
			}

			bw.write("\\connect biblivre4_b3b_restore\n");
			bw.flush();
			
			while ((inputLine = sqlBr.readLine()) != null) {
				if (inputLine.trim().startsWith("CREATE PROCEDURAL LANGUAGE")) {
					continue;
				}
				
				if (inputLine.trim().startsWith("ALTER PROCEDURAL LANGUAGE")) {
					continue;
				}
				
				bw.write(inputLine);
				
				if (inputLine.trim().startsWith("SET search_path =")) {
					break;
				}
			}

			bw.flush();
			
			char[] buf = new char[1024 * 8];
			int len;
			while ((len = sqlBr.read(buf)) > 0) {
				bw.write(buf, 0, len);
				bw.flush();
			}

			sqlBr.close();

			//bw.write("ANALYZE;\n");
			bw.close();
			
			p.waitFor();

			return p.exitValue() == 0;
		} catch (IOException e) {
			this.logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			this.logger.error(e.getMessage(), e);
		}

		return false;
	}
	
	
	public RestoreDTO getRestoreDTO(String filename) {
		BackupBO backupBO = BackupBO.getInstance(this.getSchema());
		
		File path = backupBO.getBackupDestination();

		if (path == null) {
			path = FileUtils.getTempDirectory();
		}

		File backup = new File(path, filename);
		if (!backup.exists()) {
			throw new ValidationException("administration.maintenance.backup.error.backup_file_not_found");
		}
		
		RestoreDTO dto = this.getRestoreDTO(backup);
		if (dto == null || !dto.isValid()) {
			throw new ValidationException("administration.maintenance.backup.error.corrupted_backup_file");
		}

		return dto;
	}	
	
	private RestoreDTO getRestoreDTO(File file) {
		ZipFile zip = null;
		InputStream content = null;
		RestoreDTO dto = null;
		
		try {
			zip = new ZipFile(file);
			ZipArchiveEntry metadata = zip.getEntry("backup.meta");
	
			if (metadata == null || !zip.canReadEntryData(metadata)) {
				return null;
			}
			
			StringWriter writer = new StringWriter();
			content = zip.getInputStream(metadata);
			IOUtils.copy(content, writer, "UTF-8");
 
			JSONObject json = new JSONObject(writer.toString());
			dto = new RestoreDTO(json);
		} catch (Exception e) {
			dto = new RestoreDTO();
			dto.setValid(false);
		} finally {
			if (dto != null) {
				dto.setBackup(file);
			}

			ZipFile.closeQuietly(zip);
			IOUtils.closeQuietly(content);
		}
		
		return dto;
	}
	
	private void processRestore(File restore, BufferedWriter bw) throws IOException {
		if (restore == null) {
			this.logger.info("===== Skipping File 'null' =====");
			return;
		}

		if (!restore.exists()) {
			this.logger.info("===== Skipping File '" + restore.getName() + "' =====");
			return;
		}

		this.logger.info("===== Restoring File '" + restore.getName() + "' =====");

		BufferedReader sqlBr = new BufferedReader(new InputStreamReader(new FileInputStream(restore), "UTF-8"));

		char[] buf = new char[1024 * 8];
		int len;
		while ((len = sqlBr.read(buf)) > 0) {

			for (int i = 0; i < len; i++) {
				if (buf[i] == '\n') {
					State.incrementCurrentStep();
				}
			}
			
			bw.write(buf, 0, len);
			bw.flush();
		}

		sqlBr.close();
	}
	
	private void processMediaRestoreFolder(File path, BufferedWriter bw) throws IOException {
		if (path == null) {
			this.logger.info("===== Skipping File 'null' =====");
			return;
		}

		if (!path.exists() || !path.isDirectory()) {
			this.logger.info("===== Skipping File '" + path.getName() + "' =====");
			return;
		}

		Pattern filePattern = Pattern.compile("^(\\d+)_(.*)$");
		DigitalMediaDAO dao = DigitalMediaDAO.getInstance(Constants.GLOBAL_SCHEMA);

		for (File file : path.listFiles()) {
			Matcher fileMatcher = filePattern.matcher(file.getName());

			if (fileMatcher.find()) {
				String mediaId = fileMatcher.group(1);

				long oid = dao.importFile(file);
				
				bw.write("UPDATE digital_media SET blob = '" + oid + "' WHERE id = '" + mediaId + "';\n");
			}
		}
	}

	private void processMediaRestore(File restore, BufferedWriter bw, String schema) throws IOException {
		if (restore == null) {
			this.logger.info("===== Skipping File 'null' =====");
			return;
		}

		if (!restore.exists()) {
			this.logger.info("===== Skipping File '" + restore.getName() + "' =====");
			return;
		}

		this.logger.info("===== Restoring File '" + restore.getName() + "' =====");

		Scanner sc = new Scanner(restore, "UTF-8");
		String inputLine;

		// Since PostgreSQL uses global OIDs for LargeObjects, we can't simply
		// restore a digital_media backup. To prevent oid conflicts, we will create
		// a new oid, replacing the old one.

		HashMap<String, Long> oidMap = new HashMap<String, Long>();
		Pattern loCreatePattern = Pattern.compile("lo_create\\('(.*?)'\\)");
		Pattern loOpenPattern = Pattern.compile("(.*lo_open\\(')(.*?)(',.*)");

		while (sc.hasNextLine()) {
			inputLine = sc.nextLine();
			State.incrementCurrentStep();

			if (inputLine.startsWith("SELECT pg_catalog.lo_create")) {
				// New OID detected

				Matcher loCreateMatcher = loCreatePattern.matcher(inputLine);
				if (loCreateMatcher.find()) {
					String currentOid = loCreateMatcher.group(1);
					Long newOid = this.dao.createOID();

					this.logger.info("Creating new OID (old: " + currentOid + ", new: " + newOid + ")");

					oidMap.put(currentOid, this.dao.createOID());
				}

			} else if (inputLine.startsWith("SELECT pg_catalog.lo_open")) {
				// Opening the Large Object for writing purposes

				Matcher loOpenMatcher = loOpenPattern.matcher(inputLine);
				if (loOpenMatcher.find()) {
					String newLine = loOpenMatcher.replaceFirst("$1" + oidMap.get(loOpenMatcher.group(2)) + "$3");

					bw.write(newLine);
					bw.write("\n");
				}
			} else if (inputLine.startsWith("ALTER LARGE OBJECT")) {
				// Large objects are already created with the correct owner
			} else if (inputLine.startsWith("BEGIN;") || inputLine.startsWith("COMMIT;")) {
				// Ignore internal transactions (we are already using --single-transaction)
			} else {
				if (inputLine.startsWith("COPY")) {
					this.logger.info(inputLine);
				}

				bw.write(inputLine);
				bw.write("\n");
			}

			bw.flush();
		}
		
		bw.write("SET search_path = \"" + schema + "\", pg_catalog;\n");

		for (String oid : oidMap.keySet()) {
			Long newOid = oidMap.get(oid);

			bw.write("UPDATE digital_media SET blob = '" + newOid + "' WHERE blob = '" + oid + "';\n");
		}

		bw.flush();
		sc.close();
	}
}
