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

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDTO;
import biblivre.core.utils.FileIOUtils;

public class ConfigurationsDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private String key;
	private String value;
	private String type;
	private boolean required;
	
	public ConfigurationsDTO() {
		super();
	}

	public ConfigurationsDTO(String key, String value) {
		super();
		
		this.setKey(key);
		this.setValue(value);
	}

	public String validate() {
		String value = this.getValue();
		String type = this.getType();

		if (this.isRequired() && StringUtils.isBlank(value)) {
			return "administration.configurations.error.value_is_required";
		}

		if (type.equals("integer") && !StringUtils.isNumeric(value)) {
			return "administration.configurations.error.value_must_be_numeric";
		}

		if (type.equals("float")) {
			try {
				//TODO Internacionalizar
				this.setValue(String.valueOf(Float.valueOf(value)));
			} catch (Exception e) {
				return "administration.configurations.error.value_must_be_numeric";
			}
		}

		if (type.equals("boolean") && !(value.equals("true") || value.equals("false"))) {
			return "administration.configurations.error.value_must_be_boolean";
		}
		
		if (type.equals("file") && StringUtils.isNotBlank(value)) {
			try {
				File file = new File(value);
				if (!file.exists()) {
					return "administration.configurations.error.file_not_found";
				}
			} catch (Exception e) {
				return "administration.configurations.error.file_not_found";
			}
		}
		
		if (type.equals("writable_path") && StringUtils.isNotBlank(value)) {
			if (!FileIOUtils.isWritablePath(value)) {
				return "administration.configurations.error.invalid_writable_path";
			}
		}

		return null;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return StringUtils.defaultString(this.value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return StringUtils.defaultString(this.type, "string");
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
