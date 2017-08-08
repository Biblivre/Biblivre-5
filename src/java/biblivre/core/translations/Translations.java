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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.core.StaticBO;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;

public class Translations extends StaticBO {
	private static Logger logger = Logger.getLogger(Translations.class);

	// HashMap<Pair<Schema, Language>, TranslationsMap>
	private static HashMap<Pair<String, String>, TranslationsMap> translations;
	private static Set<String> availableJavascriptLocales = new HashSet<String>(Arrays.asList(new String[] {
			"af-ZA", "af", "am-ET", "am", "ar-AE", "ar-BH",
			"ar-DZ", "ar-EG", "ar-IQ", "ar-JO", "ar-KW", "ar-LB", "ar-LY", "ar-MA", "ar-OM", "ar-QA", "ar-SA", "ar-SY", "ar-TN", "ar-YE", "ar", "arn-CL",
			"arn", "as-IN", "as", "az-Cyrl-AZ", "az-Cyrl", "az-Latn-AZ", "az-Latn", "az", "ba-RU", "ba", "be-BY", "be", "bg-BG", "bg", "bn-BD", "bn-IN", "bn",
			"bo-CN", "bo", "br-FR", "br", "bs-Cyrl-BA", "bs-Cyrl", "bs-Latn-BA", "bs-Latn", "bs", "ca-ES", "ca", "co-FR", "co", "cs-CZ", "cs", "cy-GB", "cy",
			"da-DK", "da", "de-AT", "de-CH", "de-DE", "de-LI", "de-LU", "de", "dsb-DE", "dsb", "dv-MV", "dv", "el-GR", "el", "en-029", "en-AU", "en-BZ",
			"en-CA", "en-GB", "en-IE", "en-IN", "en-JM", "en-MY", "en-NZ", "en-PH", "en-SG", "en-TT", "en-US", "en-ZA", "en-ZW", "es-AR", "es-BO", "es-CL",
			"es-CO", "es-CR", "es-DO", "es-EC", "es-ES", "es-GT", "es-HN", "es-MX", "es-NI", "es-PA", "es-PE", "es-PR", "es-PY", "es-SV", "es-US", "es-UY",
			"es-VE", "es", "et-EE", "et", "eu-ES", "eu", "fa-IR", "fa", "fi-FI", "fi", "fil-PH", "fil", "fo-FO", "fo", "fr-BE", "fr-CA", "fr-CH", "fr-FR",
			"fr-LU", "fr-MC", "fr", "fy-NL", "fy", "ga-IE", "ga", "gd-GB", "gd", "gl-ES", "gl", "gsw-FR", "gsw", "gu-IN", "gu", "ha-Latn-NG", "ha-Latn", "ha",
			"he-IL", "he", "hi-IN", "hi", "hr-BA", "hr-HR", "hr", "hsb-DE", "hsb", "hu-HU", "hu", "hy-AM", "hy", "id-ID", "id", "ig-NG", "ig", "ii-CN", "ii",
			"is-IS", "is", "it-CH", "it-IT", "it", "iu-Cans-CA", "iu-Cans", "iu-Latn-CA", "iu-Latn", "iu", "ja-JP", "ja", "ka-GE", "ka", "kk-KZ", "kk",
			"kl-GL", "kl", "km-KH", "km", "kn-IN", "kn", "ko-KR", "ko", "kok-IN", "kok", "ky-KG", "ky", "lb-LU", "lb", "lo-LA", "lo", "lt-LT", "lt", "lv-LV",
			"lv", "mi-NZ", "mi", "mk-MK", "mk", "ml-IN", "ml", "mn-Cyrl", "mn-MN", "mn-Mong-CN", "mn-Mong", "mn", "moh-CA", "moh", "mr-IN", "mr", "ms-BN",
			"ms-MY", "ms", "mt-MT", "mt", "nb-NO", "nb", "ne-NP", "ne", "nl-BE", "nl-NL", "nl", "nn-NO", "nn", "no", "nso-ZA", "nso", "oc-FR", "oc", "or-IN",
			"or", "pa-IN", "pa", "pl-PL", "pl", "prs-AF", "prs", "ps-AF", "ps", "pt-BR", "pt-PT", "pt", "qut-GT", "qut", "quz-BO", "quz-EC", "quz-PE", "quz",
			"rm-CH", "rm", "ro-RO", "ro", "ru-RU", "ru", "rw-RW", "rw", "sa-IN", "sa", "sah-RU", "sah", "se-FI", "se-NO", "se-SE", "se", "si-LK", "si",
			"sk-SK", "sk", "sl-SI", "sl", "sma-NO", "sma-SE", "sma", "smj-NO", "smj-SE", "smj", "smn-FI", "smn", "sms-FI", "sms", "sq-AL", "sq", "sr-Cyrl-BA",
			"sr-Cyrl-CS", "sr-Cyrl-ME", "sr-Cyrl-RS", "sr-Cyrl", "sr-Latn-BA", "sr-Latn-CS", "sr-Latn-ME", "sr-Latn-RS", "sr-Latn", "sr", "sv-FI", "sv-SE",
			"sv", "sw-KE", "sw", "syr-SY", "syr", "ta-IN", "ta", "te-IN", "te", "tg-Cyrl-TJ", "tg-Cyrl", "tg", "th-TH", "th", "tk-TM", "tk", "tn-ZA", "tn",
			"tr-TR", "tr", "tt-RU", "tt", "tzm-Latn-DZ", "tzm-Latn", "tzm", "ug-CN", "ug", "uk-UA", "uk", "ur-PK", "ur", "uz-Cyrl-UZ", "uz-Cyrl", "uz-Latn-UZ",
			"uz-Latn", "uz", "vi-VN", "vi", "wo-SN", "wo", "xh-ZA", "xh", "yo-NG", "yo", "zh-CHS", "zh-CHT", "zh-CN", "zh-Hans", "zh-Hant", "zh-HK", "zh-MO",
			"zh-SG", "zh-TW", "zh", "zu-ZA", "zu"
	}));

	private Translations() {
	}
	
	static {
		Translations.reset();
	}
	
	public static void reset() {
		Translations.translations = new HashMap<Pair<String, String>, TranslationsMap>();
	}

	public static void reset(String schema, String language) {
		Pair<String, String> pair = new Pair<String, String>(schema, language);
		Translations.translations.remove(pair);
	}
	
	public static TranslationsMap get(String schema, String language) {
		Pair<String, String> pair = new Pair<String, String>(schema, language);
		TranslationsMap map = Translations.translations.get(pair);

		if (map == null) {
			map = Translations.loadLanguage(schema, language);
		}

		return map;
	}

	public static boolean save(String schema, String language, HashMap<String, String> translation, HashMap<String, String> removeTranslation, int loggedUser) {
		HashMap<String, HashMap<String, String>> translations = new HashMap<String, HashMap<String, String>>();
		translations.put(language, translation);

		HashMap<String, HashMap<String, String>> removeTranslations = null;
		
		if (removeTranslation != null) {
			removeTranslations = new HashMap<String, HashMap<String, String>>();
			removeTranslations.put(language, removeTranslation);
		}
		
		return Translations.save(schema, translations, removeTranslations, loggedUser);
	}
	
	public static boolean save(String schema, HashMap<String, HashMap<String, String>> translations, HashMap<String, HashMap<String, String>> removeTranslations, int loggedUser) {
		TranslationsDAO.getInstance(schema).save(translations, removeTranslations, loggedUser);
		
		for (String language : translations.keySet()) {
			Translations.reset(schema, language);
		}
		
		Languages.reset(schema);
		
		return true;
	}

	public static boolean addSingleTranslation(String schema, String language, String key, String text, int loggedUser) {
		HashMap<String, String> translation = new HashMap<String, String>();
		translation.put(key, text);

		HashMap<String, HashMap<String, String>> translations = new HashMap<String, HashMap<String, String>>();
		translations.put(language, translation);

		boolean success = TranslationsDAO.getInstance(schema).save(translations, loggedUser);

		Translations.reset(schema, language);
		
		return success;
	}

	public static Locale toLocale(String locale) {
		if (locale == null) {
			return null;
		}
		
		locale = locale.replaceAll("-", "_");
		
		try {
			return LocaleUtils.toLocale(locale);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	public static boolean isJavaScriptLocaleAvailable(String language) {
		if (language == null) {
			return false;
		}
		
		if (Translations.availableJavascriptLocales.contains(language)) {
			return true;
		}
		
		return Translations.availableJavascriptLocales.contains(language.split("-")[0]);
	}

	public static boolean isJavaLocaleAvailable(String language) {
		Locale locale = Translations.toLocale(language);

		if (locale == null) {
			return false;
		}

		if (LocaleUtils.isAvailableLocale(locale)) {
			return true;
		}
		
		locale = Translations.toLocale(language.split("-")[0]);
		
		return LocaleUtils.isAvailableLocale(locale);
	}
	
	public static DiskFile createDumpFile(String schema, String language) {
		Translations.reset(schema, language);
		HashMap<String, TranslationDTO> translations = Translations.get(schema, language).getAll();
		
		List<String> list = new ArrayList<String>(translations.keySet());
		Collections.sort(list, new NamespaceComparator());

		try {
			
			File file = File.createTempFile("biblivre_translations_" + language + "_", ".txt");
			FileWriter out = new FileWriter(file);

			String lastNamespace = null;
			String namespace = null;
			int namespaceIndex;

			out.write("##################################################################\n");
			out.write("#               ARQUIVO DE TRADUÇÕES DO BIBLIVRE V               #\n");
			out.write("#                  BIBLIVRE V TRANSLATIONS FILE                  #\n");
			out.write("##################################################################\n");
			out.write("#                                                                #\n");			
			out.write("#                     DIRETRIZES / GUIDELINES                    #\n");
			out.write("#                                                                #\n");
			out.write("##################################################################\n");
			out.write("#                                                                #\n");
			out.write("# 1. Este arquivo deve ser salvo com o encoding UTF-8            #\n");
			out.write("# 2. Traduza apenas o que estiver depois do símbolo de igual     #\n");
			out.write("# 3. Não altere ou traduza termos que estiverem dentro de chaves #\n");
			out.write("# 4. É possível usar quebra de linha nas  traduções. Os símbolos #\n");
			out.write("#    * ou + no começo da linha definirão que ali começa um novo  #\n");
			out.write("#    termo                                                       #\n");
			out.write("# 5. A chave *language_code deve ser preenchida com o código do  #\n");
			out.write("#    idioma no formato RFC 3066 como descrito em                 #\n");
			out.write("#    http://www.i18nguy.com/unicode/language-identifiers.html    #\n");
			out.write("#                                                                #\n");
			out.write("##################################################################\n");
			out.write("\n");
			out.write("\n");
			
			for (String key : list) {
				namespaceIndex = key.lastIndexOf('.');

				if (namespaceIndex != -1) {
					namespace = key.substring(0, namespaceIndex);
				} else {
					namespace = "";
				}

				if (lastNamespace != null && !lastNamespace.equals(namespace)) {
					out.write(Constants.LINE_BREAK);
				}

				lastNamespace = namespace;

				TranslationDTO dto = translations.get(key);
				
				out.write(dto.isUserCreated() ? "+" : "*");
				out.write(key);
				out.write(" = ");
				out.write(dto.getText());
				out.write(Constants.LINE_BREAK);
			}

			out.flush();
			out.close();
			return new DiskFile(file, "x-download");
		} catch (Exception e) {
			Translations.logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	private static synchronized TranslationsMap loadLanguage(String schema, String language) {
		Pair<String, String> pair = new Pair<String, String>(schema, language);
		TranslationsMap map = Translations.translations.get(pair);

		// Checking again for thread safety.
		if (map != null) {
			return map;
		}

		if (Translations.logger.isDebugEnabled()) {
			Translations.logger.debug("Loading language " + schema + "." + language);
		}

		TranslationsDAO dao = TranslationsDAO.getInstance(schema);

		if (StringUtils.isNotBlank(language)) {
			List<TranslationDTO> list = dao.list(language);
			map = new TranslationsMap(schema, language, list.size());

			for (TranslationDTO dto : list) {
				map.put(dto.getKey(), dto);
			}

			Translations.translations.put(pair, map);
		} else {
			map = new TranslationsMap(schema, language, 1);
		}

		return map;
	}
	
}
