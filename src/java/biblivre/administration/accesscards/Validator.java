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
package biblivre.administration.accesscards;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;

public class Validator extends AbstractValidator {
	
	public void validateSave(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		String code = request.getString("code");
		String prefix = request.getString("prefix");
		String start = request.getString("start");
		String end = request.getString("end");
		String suffix = request.getString("suffix");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		boolean single = StringUtils.isNotBlank(code);
		boolean multiple = StringUtils.isNotBlank(start) || StringUtils.isNotBlank(end) || StringUtils.isNotBlank(prefix) || StringUtils.isNotBlank(suffix);
		
		if (!single && !multiple) {
			ex.addError("code", "field.error.required");
		}
		
		if (multiple) {
			boolean numeric = true;
			
			if (StringUtils.isBlank(start)) {
				ex.addError("start", "field.error.required");
				numeric = false;
			}
			
			if (StringUtils.isBlank(end)) {
				ex.addError("end", "field.error.required");
				numeric = false;			
			}
			
			if (numeric && !StringUtils.isNumeric(start)) {
				ex.addError("start", "field.error.digits_only");
				numeric = false;			
			}
			
			if (numeric && !StringUtils.isNumeric(end)) {
				ex.addError("end", "field.error.digits_only");
				numeric = false;			
			}
			
			if (numeric) {
				
				Integer startInt = request.getInteger("start");
				Integer endInt = request.getInteger("end");
				
				if (startInt >= endInt) {
					ex.addError("start", "administration.accesscards.error.start_less_than_or_equals_end");
					ex.addError("end", "administration.accesscards.error.start_less_than_or_equals_end");
				}
			}

			
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
	}	


	
	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.accesscards.error.card_not_found");
			return;
		}
	}
	
	public void validateBlockCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.accesscards.error.card_not_found");
			return;
		}
	}
	
	public void validateUnblockCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "administration.accesscards.error.card_not_found");
			return;
		}
	}
	

}
