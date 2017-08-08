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
package biblivre.administration.z3950;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;

public class Validator extends AbstractValidator {
	
	public void validateSave(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		String name = request.getString("name");
		if (StringUtils.isBlank(name)) {
			ex.addError("name", "field.error.required");
		}
		
		String url = request.getString("url");
		if (StringUtils.isBlank(url)) {
			ex.addError("url", "field.error.required");
		}

		String port = request.getString("port");
		if (StringUtils.isBlank(url)) {
			ex.addError("port", "field.error.required");
		} else if (!StringUtils.isNumeric(port)) {
			ex.addError("port", "field.error.digits_only");
		}

		String collection = request.getString("collection");
		if (StringUtils.isBlank(collection)) {
			ex.addError("collection", "field.error.required");
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
		}

	}
	
	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		Integer id = request.getInteger("id");
		if (id == null || id == 0) {
			ex.addError("id", "TODO: ADD ERROR MESSAGE");
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
		}
	}
	

}
