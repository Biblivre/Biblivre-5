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
package biblivre.circulation.accesscontrol;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;

public class Validator extends AbstractValidator {
	
	public void validateSelectCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		String code = request.getString("code");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (StringUtils.isBlank(code)) {
			ex.addError("code", "field.error.required");
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
				
	}	

	public void validateLendCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		Integer cardId = request.getInteger("card_id");
		Integer userId = request.getInteger("user_id");

		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (cardId == 0) {
			ex.addError("card_id", "field.error.required");
		}
		
		if (userId == 0) {
			ex.addError("user_id", "field.error.required");
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
				
	}	
	
	public void validateReturnCard(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		Integer cardId = request.getInteger("card_id");
		Integer userId = request.getInteger("user_id");

		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (cardId == 0) {
			ex.addError("card_id", "field.error.required");
		}
		
		if (userId == 0) {
			ex.addError("user_id", "field.error.required");
		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
		
	}
	
}
