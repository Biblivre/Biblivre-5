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
package biblivre.administration.usertype;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
		
		String url = request.getString("description");
		if (StringUtils.isBlank(url)) {
			ex.addError("description", "field.error.required");
		}

		String lendingLimit = request.getString("lending_limit");
		if (StringUtils.isBlank(url)) {
			ex.addError("lending_limit", "field.error.required");
		} else if (!StringUtils.isNumeric(lendingLimit)) {
			ex.addError("lending_limit", "field.error.digits_only");
		}
		
		String reservationLimit = request.getString("reservation_limit");
		if (StringUtils.isBlank(url)) {
			ex.addError("reservation_limit", "field.error.required");
		} else if (!StringUtils.isNumeric(reservationLimit)) {
			ex.addError("reservation_limit", "field.error.digits_only");
		}
		
		String lendingTimeLimit = request.getString("lending_time_limit");
		if (StringUtils.isBlank(url)) {
			ex.addError("lending_time_limit", "field.error.required");
		} else if (!StringUtils.isNumeric(lendingTimeLimit)) {
			ex.addError("lending_time_limit", "field.error.digits_only");
		}
		
		String reservationTimeLimit = request.getString("reservation_time_limit");
		if (StringUtils.isBlank(url)) {
			ex.addError("reservation_time_limit", "field.error.required");
		} else if (!StringUtils.isNumeric(reservationTimeLimit)) {
			ex.addError("reservation_time_limit", "field.error.digits_only");
		}
		
		String fineValue = request.getString("fine_value");
		if (StringUtils.isBlank(url)) {
			ex.addError("fine_value", "field.error.required");
		} else if (!NumberUtils.isNumber(fineValue)) {
			ex.addError("fine_value", "field.error.digits_only");
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
