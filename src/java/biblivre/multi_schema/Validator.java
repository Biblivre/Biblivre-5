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
package biblivre.multi_schema;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.schemas.Schemas;
import biblivre.core.utils.Constants;

public class Validator extends AbstractValidator {
	

	
	public void validateCreate(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		String nameParam = request.getString("title");
		String schemaParam = request.getString("schema");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (StringUtils.isBlank(nameParam)) {
			ex.addError("name", "field.error.required");
		}

		if (StringUtils.isBlank(schemaParam)) {
			ex.addError("schema", "field.error.required");
		} else if (schemaParam.equals(Constants.SINGLE_SCHEMA)) {
			ex.addError("schema", "field.error.invalid");
		} else if (schemaParam.equals(Constants.GLOBAL_SCHEMA)) {
			ex.addError("schema", "field.error.invalid");
		} else if (!Schemas.isValidName(schemaParam)) {
			ex.addError("schema", "field.error.invalid");
		} else if (Schemas.isLoaded(schemaParam)) {
			ex.addError("schema", "field.error.invalid");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
		}		
	}
}
