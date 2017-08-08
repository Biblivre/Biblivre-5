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
package biblivre.acquisition.quotation;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.TextUtils;

public class Validator extends AbstractValidator {
	
	public void validateSave(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		Integer supplierId = request.getInteger("supplier");
		Integer requestId = request.getInteger("request");
		String quotationList = request.getString("quotation_list");
		
		String quotationDate = request.getString("quotation_date");
		String responseDate = request.getString("response_date");
		String expirationDate = request.getString("expiration_date");
		String deliveryTime = request.getString("delivery_time");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (supplierId == 0) {
			ex.addError("supplier", "field.error.required");
		}
		
		if (StringUtils.isBlank(quotationList)) {
			if (requestId == 0) {
				ex.addError("request", "field.error.required");
			} else {
				ex.addError("quotation_list", "field.error.required");
			}
		}
		
		if (StringUtils.isBlank(quotationDate)) {
			ex.addError("quotation_date", "field.error.required");
		} else {
			try {
				TextUtils.parseDate(quotationDate);
			} catch (Exception e) {
				ex.addError("quotation_date", "field.error.invalid");				
			}
		}
		
		if (StringUtils.isBlank(responseDate)) {
			ex.addError("response_date", "field.error.required");
		} else {
			try {
				TextUtils.parseDate(responseDate);
			} catch (Exception e) {
				ex.addError("response_date", "field.error.invalid");				
			}
		}
		
		if (StringUtils.isBlank(expirationDate)) {
			ex.addError("expiration_date", "field.error.required");
		} else {
			try {
				TextUtils.parseDate(expirationDate);
			} catch (Exception e) {
				ex.addError("expiration_date", "field.error.invalid");				
			}
		}
		
		if (StringUtils.isBlank(deliveryTime)) {
			ex.addError("delivery_time", "field.error.required");
		} else if (!StringUtils.isNumeric(deliveryTime)) {
			ex.addError("delivery_time", "field.error.digits_only");
		}
		
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
				
	}	

	public void validateOpen(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "aquisition.quotation.error.quotation_not_found");
			return;
		}
	}

	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "aquisition.quotation.error.quotation_not_found");
			return;
		}
	}
	

}
