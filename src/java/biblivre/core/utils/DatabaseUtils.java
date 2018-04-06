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
package biblivre.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.core.configurations.Configurations;

public class DatabaseUtils {
	
	private static Logger logger = Logger.getLogger(DatabaseUtils.class);
	
	public static File getPgDump(String schema) {
		File pgdump = DatabaseUtils.getPgDumpFromConfiguration(schema);
		
		if (pgdump == null) {
			pgdump = DatabaseUtils.getFromFilesystem(DatabaseUtils.getPgDumpFilename());
		}
		
		return pgdump;
	}
	
	public static File getPsql(String schema) {
		File psql = DatabaseUtils.getPsqlFromConfiguration(schema);
		
		if (psql == null) {
			psql = DatabaseUtils.getFromFilesystem(DatabaseUtils.getPsqlFilename());
		}
		
		return psql;
	}

	public static File getPgDumpFromConfiguration(String schema) {
		String pgdump = Configurations.getString(schema, Constants.CONFIG_PGDUMP_PATH);

		if (StringUtils.isNotBlank(pgdump)) {
			File file = new File(pgdump);

			if (file.isDirectory()) {
				file = new File(file, DatabaseUtils.getPgDumpFilename());
			}

			return file.exists() ? file : null;
		}

		return null;
	}

	public static File getPsqlFromConfiguration(String schema) {
		String psql = Configurations.getString(schema, Constants.CONFIG_PSQL_PATH);

		if (StringUtils.isNotBlank(psql)) {
			File file = new File(psql);

			if (file.isDirectory()) {
				file = new File(file, DatabaseUtils.getPsqlFilename());
			}

			return file.exists() ? file : null;
		}

		return null;
	}
	
	private static File getFromFilesystem(String fileName) {
		String os = System.getProperty("os.name").toUpperCase();
		
		if (os.contains("WINDOWS")) {
			return DatabaseUtils.getWindows(fileName);
		} else if (os.contains("LINUX")) {
			return DatabaseUtils.getLinux(fileName);
		} else if (os.contains("MAC OS X")) {
			return DatabaseUtils.getMacOs(fileName);
		} else {
			return null;
		}
	}

	private static String getPgDumpFilename() {
		String os = System.getProperty("os.name").toUpperCase();
		
		if (os.contains("WINDOWS")) {
			return "pg_dump.exe";
		} else {
			return "pg_dump";
		}		
	}

	private static String getPsqlFilename() {
		String os = System.getProperty("os.name").toUpperCase();
		
		if (os.contains("WINDOWS")) {
			return "psql.exe";
		} else {
			return "psql";
		}		
	}
	
	private static File getMacOs(String filename) {
		String[] commands;

		commands = new String[] {
			"/bin/sh",
			"-c",
			"\"/bin/ps axwwww -o comm | grep -v grep | grep postgres$ | sed 's/postgres$//'\""
		};

		String path = DatabaseUtils.processPatternMatcher(commands, "(.*)", 1);		
		return new File(path, filename);
	}
	
	private static File getLinux(String filename) {
		ProcessBuilder pb = whichCommand(filename);
		String line = null;
		try {
			Process p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			line = reader.readLine();
		} catch (IOException e) {
			// TODO: logger
			e.printStackTrace();
		}

		if (line == null) {
			return null;
		}

		return new File(line);
	}

	private static ProcessBuilder whichCommand(String filename) {
		String[] commands = new String[] {
			"/bin/bash",
			"-c",
			"which " + filename
		};

		ProcessBuilder pb = new ProcessBuilder(commands);
		return pb;
	}
	
	private static File getWindows(String filename) {
		String[] commands;
		
		//Step 1 - Detecting current PostgreSQL service name
		commands = new String[] {
			"tasklist",
			"/nh",
			"/svc",
			"/fi",
			"imagename eq pg_ctl.exe",
			"/fo",
			"csv"
		};

		String postgresServiceName = DatabaseUtils.processPatternMatcher(commands, "([^\"]+)\"$", 1);
		if (postgresServiceName == null) {
			return null;
		}
		
		//Step 2 - Detect PostgreSQL Product Code
		String postgresProductCode = null;
		String[] regkeys = new String[] {
			"HKLM\\SOFTWARE\\PostgreSQL\\Services\\" + postgresServiceName,
			"HKLM\\SOFTWARE\\Wow6432Node\\PostgreSQL\\Services\\" + postgresServiceName
		};
		for (String regkey : regkeys) {
			postgresProductCode = getRegValue(regkey, "Product Code");
			if (postgresProductCode != null) {
				break;
			}
		}
		if (postgresProductCode == null) {
			return null;
		}
		
		//Step 3 - Detect PostgreSQL Base Directory
		String postgresBaseDirectory = null;
		regkeys = new String[] {
			"HKLM\\SOFTWARE\\PostgreSQL\\Installations\\" + postgresProductCode,
			"HKLM\\SOFTWARE\\Wow6432Node\\PostgreSQL\\Installations\\" + postgresProductCode
		};
		for (String regkey : regkeys) {
			postgresBaseDirectory = getRegValue(regkey, "Base Directory");
			if (postgresBaseDirectory != null) {
				break;
			}
		}
		if (postgresBaseDirectory == null) {
			return null;
		}
		
		File file = new File(postgresBaseDirectory + File.separator + "bin", filename);
		return file.exists() ? file : null;
	}
	
	private static String getRegValue(String dir, String key) {
		String[] commands = new String[] {
			"reg",
			"query",
			dir,
			"/V",
			key
		};
			
		return DatabaseUtils.processPatternMatcher(commands, "REG_SZ\\s+(.+)$", 1);
	}
	
	private static String processPatternMatcher(String[] commands, String regex, int group) {
		return DatabaseUtils.processPatternMatcher(commands, regex, group, null);
	}
	
	private static String processPatternMatcher(String[] commands, String regex, int group, String directory) {
		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			if (directory != null) {
				pb.directory(new File(directory));
			}
				
			pb.redirectErrorStream(true);
			Process p = pb.start();

			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line;
			
			Pattern pattern = Pattern.compile(regex);
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					return matcher.group(1);
				}
			}
		} catch (IOException e) {
			DatabaseUtils.logger.error(e.getMessage(), e);
		}

		return null;
	}
}
