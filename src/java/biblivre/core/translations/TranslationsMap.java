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
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import biblivre.core.IFCacheableJavascript;
import biblivre.core.JavascriptCache;
import biblivre.core.utils.Constants;

public class TranslationsMap extends HashMap<String, TranslationDTO> implements IFCacheableJavascript {
	private Logger logger = Logger.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	private String schema;
	private String language;
	private JavascriptCache cache;

	public TranslationsMap(String schema, String language) {
		this(schema, language, 16);
	}

	public TranslationsMap(String schema, String language, int initialSize) {
		super(initialSize);
		
		this.setSchema(schema);
		this.setLanguage(language);
	}
	
	public String getText(Object key) {
		if (StringUtils.isBlank(key.toString())) {
			return "";
		}

		TranslationDTO dto = this.get(key);
		String value = null;
		
		if (dto != null) {
			value = dto.getText();
		}

		if (StringUtils.isEmpty(value)) {
			if (!this.getSchema().equals(Constants.GLOBAL_SCHEMA)) {
				value = Translations.get(Constants.GLOBAL_SCHEMA, this.getLanguage()).getText(key);
			} else {
				this.logger.warn("Translation key not found: " + this.schema + "." + this.language + "." + key);
				value = "__" + key + "__";
			}
		}

		return value;
	}

	public String getHtml(Object key) {
		String value = this.getText(key);

		return StringEscapeUtils.escapeHtml4(value);
	}

	public String getSchema() {
		return StringUtils.defaultString(this.schema, Constants.GLOBAL_SCHEMA);
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getLanguage() {
		return StringUtils.defaultString(this.language);
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public HashMap<String, TranslationDTO> getAll() {
		HashMap<String, TranslationDTO> translations = new HashMap<String, TranslationDTO>();
		
		if (!this.getSchema().equals(Constants.GLOBAL_SCHEMA)) { 
			translations.putAll(Translations.get(Constants.GLOBAL_SCHEMA, this.getLanguage()));
		}

		for (Entry<String, TranslationDTO> e : this.entrySet()) {
			String key = e.getKey();
			TranslationDTO dto = e.getValue();

			if (!StringUtils.isEmpty(dto.getText()) || !translations.containsKey(key)) {
				translations.put(key, dto);
			}
		}
		
		return translations;
	}
	
	@Override
	public String toJavascriptString() {
		return "Translations.translations = " + this.toString() + ";";
	}

	@Override
	public String getCacheFileNamePrefix() {
		return this.getSchema() + "." + this.getLanguage();
	}
	
	@Override
	public String getCacheFileNameSuffix() {
		return ".i18n.js";
	}

	@Override
	public File getCacheFile() {
		if (this.cache == null) {
			this.cache = new JavascriptCache(this);
		}

		return this.cache.getCacheFile();
	}
	
	@Override
	public String getCacheFileName() {
		if (this.cache == null) {
			this.cache = new JavascriptCache(this);
		}
				
		return this.cache.getFileName();
	}
	
	@Override
	public void invalidateCache() {
		this.cache = null;
	}
	
	@Override
	public String toString() {
		return new JSONObject(this.getAll()).toString();
	}
}
