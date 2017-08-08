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
package biblivre.acquisition.order;

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
		
		String quotationId = request.getString("quotation");
		
		String orderDate = request.getString("created");
		String deadlineDate = request.getString("deadline_date");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (StringUtils.isBlank(quotationId)) {
			ex.addError("quotation_id", "field.error.required");
		} else if (!StringUtils.isNumeric(quotationId)) {
			ex.addError("quotation_id", "field.error.digits_only");
		}

		if (StringUtils.isBlank(orderDate)) {
			ex.addError("created", "field.error.required");
		} else {
			try {
				TextUtils.parseDate(orderDate);
			} catch (Exception e) {
				ex.addError("created", "field.error.invalid");				
			}
		}
		
		if (StringUtils.isBlank(deadlineDate)) {
			ex.addError("deadline_date", "field.error.required");
		} else {
			try {
				TextUtils.parseDate(deadlineDate);
			} catch (Exception e) {
				ex.addError("deadline_date", "field.error.invalid");				
			}
		}
		
		String delivered = request.getString("delivered");
		
		if (StringUtils.isNotBlank(delivered)) {
			String invoiceNumber = request.getString("invoice_number");
			String receiptDate = request.getString("receipt_date");
			Float totalValue = request.getFloat("total_value", null);
			String deliveredQuantity = request.getString("delivered_quantity");
			String termsOfPayment = request.getString("terms_of_payment");
			
			if (StringUtils.isBlank(invoiceNumber)) {
				ex.addError("invoice_number", "field.error.required");
			} 
			
			if (StringUtils.isBlank(receiptDate)) {
				ex.addError("receipt_date", "field.error.required");
			} else {
				try {
					TextUtils.parseDate(receiptDate);
				} catch (Exception e) {
					ex.addError("receipt_date", "field.error.invalid");				
				}
			}
			
			if (totalValue == null || totalValue == 0.00) {
				ex.addError("total_value", "field.error.invalid");
			}
			
			if (StringUtils.isBlank(deliveredQuantity)) {
				ex.addError("delivered_quantity", "field.error.required");
			} else if (!StringUtils.isNumeric(deliveredQuantity)) {
				ex.addError("delivered_quantity", "field.error.digits_only");
			}
			
			if (StringUtils.isBlank(termsOfPayment)) {
				ex.addError("terms_of_payment", "field.error.required");
			} 

		}
		
		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
				
	}	

	public void validateOpen(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "acquisition.order.error.order_not_found");
			return;
		}
	}

	public void validateDelete(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("id");
		if (id == 0) {
			handler.setMessage(ActionResult.WARNING, "acquisition.order.error.order_not_found");
			return;
		}
	}
	

}
