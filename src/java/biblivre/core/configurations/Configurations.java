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
package biblivre.core.configurations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.core.StaticBO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.schemas.Schemas;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;

public class Configurations extends StaticBO {

	private static Logger logger = Logger.getLogger(Translations.class);
	// HashMap<Schema, HashMap<Key, Value>>
	private static HashMap<String, HashMap<String, ConfigurationsDTO>> configurations;

	private Configurations() {
	}

	static {
		Configurations.reset();
	}

	public static void reset() {
		Configurations.configurations = new HashMap<String, HashMap<String, ConfigurationsDTO>>();
	}
	
	public static String getString(String schema, String key) {
		String value = Configurations.getValue(schema, key);
		
		return value;
	}

	public static String getHtml(String schema, String key) {
		String value = Configurations.getValue(schema, key);

		return StringEscapeUtils.escapeHtml4(value);
	}

	public static int getInt(String schema, String key) {
		return Configurations.getInt(schema, key, 0);
	}

	public static int getPositiveInt(String schema, String key, int def) {
		int ret = Configurations.getInt(schema, key, def);
		return ret > 0 ? ret : def;
	}

	public static int getInt(String schema, String key, int def) {
		String value = Configurations.getValue(schema, key);
		
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			Configurations.logger.warn("Configuration is not an integer: " + schema + "." + key + " = " + value);
			return def;
		}
	}

	public static float getFloat(String schema, String key) {
		String value = Configurations.getValue(schema, key);
		
		try {
			return Float.valueOf(value.replace(',', '.'));
		} catch (Exception e) {
			Configurations.logger.warn("Configuration is not a float: " + schema + "." + key + " = " + value);
			return 0.0f;
		}
	}

	public static boolean getBoolean(String schema, String key) {
		String value = Configurations.getValue(schema, key);
		
		return value.equals("true");
	}
	
	public static List<Integer> getIntArray(String schema, String key, String def) {
		String value = Configurations.getValue(schema, key);
		
		if (StringUtils.isBlank(value)) {
			value = def;
		}
		
		try {
			return Configurations.stringToIntArray(value);
		} catch (Exception e) {
			return Configurations.stringToIntArray(def);
		}
	}
	
	private static List<Integer> stringToIntArray(String string) {
		String[] array = string.split(",");
		
		List<Integer> list = new ArrayList<Integer>(array.length);
		
		for (String val : array) {
			list.add(Integer.valueOf(val));
		}
		
		return list;
	}
	
	public static List<ConfigurationsDTO> validate(String schema, List<ConfigurationsDTO> configs) throws ValidationException {
		List<ConfigurationsDTO> validConfigs = new ArrayList<ConfigurationsDTO>(configs.size());
		ValidationException e = new ValidationException("administration.configurations.error.invalid");
		boolean errors = false;

		for (ConfigurationsDTO config : configs) {
			if (config.getKey().equals(Constants.CONFIG_MULTI_SCHEMA) && config.getValue().equals("false")) {

				if (!schema.equals(Constants.GLOBAL_SCHEMA) && Schemas.isMultipleSchemasEnabled()) {
					errors = true;
					e.addError(config.getKey(), "multi_schema.configurations.error.disable_multi_schema_outside_global");
					continue;
				}

				if (Schemas.countEnabledSchemas() > 1) {
					errors = true;
					e.addError(config.getKey(), "multi_schema.configurations.error.disable_multi_schema_schema_count");
					continue;					
				}

				validConfigs.add(config);
			}

			ConfigurationsDTO currentConfig = Configurations.get(schema, config.getKey());

			if (currentConfig == null) {
				config.setType("string");
				config.setRequired(false);
			} else if (currentConfig.getValue().equals(config.getValue())) {
				continue;
			} else {
				config.setType(currentConfig.getType());
				config.setRequired(currentConfig.isRequired());
			}

			String validationError = config.validate();
			if (validationError != null) {
				errors = true;
				e.addError(config.getKey(), validationError);
			} else {
				validConfigs.add(config);
			}
		}

		if (errors) {
			throw e;
		}
		
		return validConfigs;
	}

	public static void save(String schema, List<ConfigurationsDTO> configs, int loggedUser) {
		ConfigurationsDTO multiSchemaConfig = null;
		
		for (Iterator<ConfigurationsDTO> it = configs.iterator(); it.hasNext();) {
			ConfigurationsDTO configDto = it.next();
			if (configDto.getKey().equals(Constants.CONFIG_MULTI_SCHEMA)) {
				multiSchemaConfig = configDto;
				it.remove();
				break;
			}
		}
		
		if (multiSchemaConfig != null) {
			ConfigurationsDAO globalDao = ConfigurationsDAO.getInstance(Constants.GLOBAL_SCHEMA);
			
			List<ConfigurationsDTO> multiSchemaList = new ArrayList<ConfigurationsDTO>();
			multiSchemaList.add(multiSchemaConfig);
			globalDao.save(multiSchemaList, loggedUser);
			
			HashMap<String, ConfigurationsDTO> map = Configurations.getMap(Constants.GLOBAL_SCHEMA);
			map.put(multiSchemaConfig.getKey(), multiSchemaConfig);
			
			Schemas.reset();
		}
		
		ConfigurationsDAO dao = ConfigurationsDAO.getInstance(schema);

		if (dao.save(configs, loggedUser)) {
			HashMap<String, ConfigurationsDTO> map = Configurations.getMap(schema);

			for (ConfigurationsDTO config : configs) {
				map.put(config.getKey(), config);
			}
		}
	}

	public static void save(String schema, ConfigurationsDTO config, int loggedUser) {
		List<ConfigurationsDTO> configs = new ArrayList<ConfigurationsDTO>(1);
		configs.add(config);
		
		Configurations.save(schema, configs, loggedUser);
	}

	private static String getValue(String schema, String key) {
		ConfigurationsDTO config = Configurations.get(schema, key);

		String value = "";

		if (config != null) {
			value = config.getValue();
		}

		return StringUtils.defaultString(value);
	}

	public static void setMultipleSchemasEnabled(Integer loggedUser) {
		
		ConfigurationsDTO config = Configurations.get(Constants.GLOBAL_SCHEMA,  Constants.CONFIG_MULTI_SCHEMA);
		
		config.setValue("true");
		
		Configurations.save(Constants.GLOBAL_SCHEMA, config, loggedUser);
		
		Configurations.reset();
	}
	
	private static ConfigurationsDTO get(String schema, String key) {
		HashMap<String, ConfigurationsDTO> map = Configurations.getMap(schema);

		ConfigurationsDTO config = map.get(key);
		
		if (config == null) {
			return schema.equals(Constants.GLOBAL_SCHEMA) ? null : Configurations.get(Constants.GLOBAL_SCHEMA, key);
		}
		
		return config;
	}

	private static HashMap<String, ConfigurationsDTO> getMap(String schema) {
		HashMap<String, ConfigurationsDTO> map = Configurations.configurations.get(schema);

		if (map == null) {
			map = Configurations.loadConfigurations(schema);
		}

		return map;
	}

	private static synchronized HashMap<String, ConfigurationsDTO> loadConfigurations(String schema) {
		HashMap<String, ConfigurationsDTO> map = Configurations.configurations.get(schema);

		// Checking again for thread safety.
		if (map != null) {
			return map;
		}

		Configurations.logger.debug("Loading configurations for " + schema);

		ConfigurationsDAO dao = ConfigurationsDAO.getInstance(schema);

		List<ConfigurationsDTO> configs = dao.list();
		map = new HashMap<String, ConfigurationsDTO>(configs.size());

		for (ConfigurationsDTO config : configs) {
			map.put(config.getKey(), config);
		}

		Configurations.configurations.put(schema, map);

		return map;
	}
}
