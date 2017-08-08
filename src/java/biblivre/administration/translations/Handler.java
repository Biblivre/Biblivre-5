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
package biblivre.administration.translations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.core.file.MemoryFile;
import biblivre.core.translations.LanguageDTO;
import biblivre.core.translations.Languages;
import biblivre.core.translations.TranslationDTO;
import biblivre.core.translations.Translations;

public class Handler extends AbstractHandler {

	public void dump(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String dumpId = UUID.randomUUID().toString();
		String language = request.getString("language");
		
		if (StringUtils.isBlank(language)) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.invalid_language");
			return;
		}
		
		request.setSessionAttribute(schema, dumpId, language);
		
		try {
			this.json.put("uuid", dumpId);
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	
	public void downloadDump(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		String dumpId = request.getString("id");
		String language = (String)request.getSessionAttribute(schema, dumpId);

		if (StringUtils.isBlank(language)) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.invalid_language");
			return;
		}

		final DiskFile exportFile = Translations.createDumpFile(schema, language);

		this.setFile(exportFile);
		
		this.setCallback(new HttpCallback() {
			@Override
			public void success() {
				try {
					exportFile.delete();
				} catch (Exception e) {}
			}
		});
	}

	public void load(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();
		boolean loadUserCreated = StringUtils.isNotBlank(request.getString("user_created"));

		HashMap<String, String> addTranslation = new HashMap<String, String>();
		HashMap<String, String> removeTranslation = new HashMap<String, String>();
		char type = '\0';
		String key = null;
		String value = null;

		try {
			MemoryFile file = request.getFile("file");
			//TODO check file's char encoding
			//TODO validate if this a translations file
			Scanner sc = new Scanner(file.getInputStream(), "UTF-8");

			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();

				if (line.length() == 0) {
					continue;
				}
				
				line = line.replace("\\n", "\n");

				int eq = line.indexOf("=");
				char start = line.charAt(0);

				if (start == '#') {
					continue;
				}
				
				if ((eq != -1) && (start == '*' || start == '+' || start == '-')) {					
					if (key != null) {
						switch (type) {
							case '*':
								addTranslation.put(key, value);
								break;
							case '+':
								if (loadUserCreated) {
									addTranslation.put(key, value);
								}
								break;
							case '-':
								removeTranslation.put(key, value);
								break;
						}
					}
					type = start;
					key = line.substring(1, eq).trim();
					value = line.substring(eq + 1).trim();
				} else if (StringUtils.isNotBlank(line)) {
					value += "\n" + line;
				}
			}

			if (key != null) {
				switch (type) {
					case '*':
						addTranslation.put(key, value);
						break;
					case '+':
						if (loadUserCreated) {
							addTranslation.put(key, value);
						}
						break;
					case '-':
						removeTranslation.put(key, value);
						break;
				}
			}
			
			sc.close();
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.load");
			return;			
		}

		String language = addTranslation.get("language_code");

		if (StringUtils.isBlank(language)) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.no_language_code_specified");
			return;
		}

		if (!Translations.isJavaScriptLocaleAvailable(language)) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.javascript_locale_not_available");
			return;			
		}

		if (!Translations.isJavaLocaleAvailable(language)) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.java_locale_not_available");
			return;			
		}
		
		try {
			boolean success = Translations.save(schema, language, addTranslation, removeTranslation, loggedUser);
			if (success) {
				this.setMessage(ActionResult.SUCCESS, "administration.translations.success.save");
			} else {
				this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
			}
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
			return;
		}
	}
	
	public void list(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Languages.reset(schema);
		Set<LanguageDTO> languages = Languages.getLanguages(schema);
		
		HashMap<String, HashMap<String, TranslationDTO>> translations = new HashMap<String, HashMap<String, TranslationDTO>>();

		for (LanguageDTO languageDTO : languages) {
			String language = languageDTO.getLanguage();

			Translations.reset(schema, language);
			translations.put(language, Translations.get(schema, language).getAll());
		}

		try {
			this.json.put("translations", new JSONObject(translations));
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();
		String strJson = request.getString("json");

		try {
			JSONObject json = new JSONObject(strJson);
			JSONObject jsonTranslations = json.optJSONObject("translations");

			if (jsonTranslations == null) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;				
			}
		
			HashMap<String, HashMap<String, String>> newTranslations = new HashMap<String, HashMap<String, String>>();

			Iterator<String> it = jsonTranslations.keys();
			while (it.hasNext()) {
				String language = it.next();
				JSONObject jsonTranslation = jsonTranslations.optJSONObject(language);

				if (jsonTranslation == null) {
					continue;
				}

				HashMap<String, String> newTranslation = new HashMap<String, String>();

				Iterator<String> tit = jsonTranslation.keys();
				while (tit.hasNext()) {
					String key = tit.next();
					String value = jsonTranslation.optString(key);
					
					if (value != null) {
						newTranslation.put(key, value);
					}
				}

				newTranslations.put(language, newTranslation);
			}
			
			Translations.save(schema, newTranslations, null, loggedUser);
			
			this.setMessage(ActionResult.SUCCESS, "administration.translations.success.save");
			
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
			return;
		}
	}
	
	public void saveLanguageTranslations(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();
		String strJson = request.getString("translations");

		try {
			JSONObject json = new JSONObject(strJson);

			String language = json.getString("language_code");
			if (!Translations.isJavaScriptLocaleAvailable(language)) {
				this.setMessage(ActionResult.WARNING, "administration.translations.error.invalid_language");
				return;
			}

			HashMap<String, String> newTranslation = new HashMap<String, String>();

			Iterator<String> tit = json.keys();
			while (tit.hasNext()) {
				String key = tit.next();
				String value = json.optString(key);
				
				if (value != null) {
					newTranslation.put(key, value);
				}
			}

			Translations.save(schema, language, newTranslation, new HashMap<String, String>(), loggedUser);

			this.setMessage(ActionResult.SUCCESS, "administration.translations.success.save");

		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
			return;
		}
	}	
}
