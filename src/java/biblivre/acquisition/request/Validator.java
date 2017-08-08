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
package biblivre.acquisition.request;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;

public class Validator extends AbstractValidator {
	
	public void validateSave(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		String requester = request.getString("requester");
		String author = request.getString("author");
		String title = request.getString("title");
		String edition = request.getString("edition");
		String publisher = request.getString("publisher");
		String quantity = request.getString("quantity");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (StringUtils.isBlank(requester)) {
			ex.addError("requester", "field.error.required");
		}
		
		if (StringUtils.isBlank(author)) {
			ex.addError("author", "field.error.required");
		}

		if (StringUtils.isBlank(title)) {
			ex.addError("title", "field.error.required");
		}

		if (StringUtils.isBlank(edition)) {
			ex.addError("edition", "field.error.required");
		}

		if (StringUtils.isBlank(publisher)) {
			ex.addError("publisher", "field.error.required");
		}

		if (StringUtils.isBlank(quantity)) {
			ex.addError("quantity", "field.error.required");
		} else if (!StringUtils.isNumeric(quantity)) {
			ex.addError("quantity", "field.error.digits_only");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
				
	}	

	public void validateOpen(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "aquisition.request.error.request_not_found");
			return;
		}
	}

	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "aquisition.request.error.request_not_found");
			return;
		}
	}
	

}
