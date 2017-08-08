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
package biblivre.core.translations;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.core.StaticBO;
import biblivre.core.utils.Constants;

public class Languages extends StaticBO {
	private static Logger logger = Logger.getLogger(Languages.class);

	private static HashMap<String, Set<LanguageDTO>> languages;

	private Languages() {
	}
	
	static {
		Languages.reset();
	}
	
	public static void reset() {
		Languages.languages = new HashMap<String, Set<LanguageDTO>>();
	}
	
	public static void reset(String schema) {
		if (schema.equals(Constants.GLOBAL_SCHEMA)) {
			Languages.reset();
		} else {
			Languages.languages.remove(schema);
		}
	}
	
	public static Set<LanguageDTO> getLanguages(String schema) {
		Set<LanguageDTO> set = Languages.languages.get(schema);

		if (set == null) {
			set = Languages.loadLanguages(schema);
		}
		
		return set;
	}
	
	public static boolean isLoaded(String schema, String language) {
		if (StringUtils.isBlank(schema) || StringUtils.isBlank(language)) {
			return false;
		}
		
		Set<LanguageDTO> languages = Languages.getLanguages(schema);
		
		if (languages == null) {
			return false;
		}
		
		for (LanguageDTO dto : languages) {
			if (language.equals(dto.getLanguage())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isNotLoaded(String schema, String language) {
		return !Languages.isLoaded(schema, language);
	}
	
	public static String getDefaultLanguage(String schema) {
		Set<LanguageDTO> languages = Languages.getLanguages(schema);
		
		for (LanguageDTO dto : languages) {
			return dto.getLanguage();
		}
		
		return "";
	}
	
	public static LanguageDTO getLanguage(String schema, String language) {
		Set<LanguageDTO> languages = Languages.getLanguages(schema);
		
		for (LanguageDTO dto : languages) {
			if (language.equals(dto.getLanguage())) {
				return dto;
			}
		}
		
		return null;
	}

	
	private static synchronized Set<LanguageDTO> loadLanguages(String schema) {
		Set<LanguageDTO> set = Languages.languages.get(schema);

		// Checking again for thread safety.
		if (set != null) {
			return set;
		}
		
		Languages.logger.debug("Loading languages from " + schema);
		LanguagesDAO dao = LanguagesDAO.getInstance(schema);
		
		set = dao.list();
		Languages.languages.put(schema, set);

		return set;
	}
}
