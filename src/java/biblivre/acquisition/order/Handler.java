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
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.acquisition.request.RequestStatus;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;

public class Handler extends AbstractHandler {

	public void search(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");

		String query = null;
		
		try {
			JSONObject json = new JSONObject(searchParameters);
			query = json.optString("query");
		} catch (JSONException je) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		Integer limit = request.getInteger("limit", Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
		Integer offset = (request.getInteger("page", 1) - 1) * limit;

		OrderBO bo = OrderBO.getInstance(schema);
		DTOCollection<OrderDTO> list = bo.search(query, limit, offset);

		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "acquisition.request.error.no_request_found");
			return;
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void paginate(ExtendedRequest request, ExtendedResponse response) {
		this.search(request, response);
	}

	
	public void open(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id");

		OrderBO bo = OrderBO.getInstance(schema);
		OrderDTO dto = bo.get(id);

		try {
			this.json.put("request", dto);
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer id = request.getInteger("id");		
		OrderDTO dto = null;
		
		try {
			dto = this.populateDTO(request);
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		OrderBO bo = OrderBO.getInstance(schema);
		
		Integer newId = 0;
		boolean result = false;
		if (id == 0) {
			dto.setStatus(RequestStatus.PENDING.toString());
			dto.setCreatedBy(request.getLoggedUserId());
			newId = bo.save(dto);
		} else {
			dto.setId(id);
			dto.setModifiedBy(request.getLoggedUserId());
			if (dto.getReceiptDate() != null) {
				dto.setStatus(RequestStatus.CLOSED.toString());
			} else {
				dto.setStatus(RequestStatus.PENDING.toString());
			}
			result = bo.update(dto);
		}
		if (newId != 0 || result) {
			if (id == 0) {
				this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.save");
			} else {
				this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.update");
			}
		} else {
			this.setMessage(ActionResult.WARNING, "acquisition.request.error.save");
		}
		
		dto = bo.get(id == 0 ? newId : id);
		
		try {
			this.json.put("data", dto.toJSONObject());
			this.json.put("full_data", true);
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}

	}
	
	public void delete(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer id = request.getInteger("id");
		
		OrderBO bo = OrderBO.getInstance(schema);
		OrderDTO dto = new OrderDTO();
		dto.setId(id);

		if (bo.delete(dto)) {
			this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.delete");
		} else {
			this.setMessage(ActionResult.WARNING, "acquisition.request.error.delete");
		}
	}
	
	private OrderDTO populateDTO(ExtendedRequest request) throws Exception {
		OrderDTO dto = new OrderDTO();
		dto.setId(request.getInteger("id"));
		dto.setQuotationId(request.getInteger("quotation"));
		dto.setInfo(request.getString("info"));
		dto.setCreated(TextUtils.parseDate(request.getString("created")));
		dto.setDeadlineDate(TextUtils.parseDate(request.getString("deadline_date")));
		
		if (StringUtils.isNotBlank(request.getString("delivered"))) {
			dto.setInvoiceNumber(request.getString("invoice_number"));
			dto.setReceiptDate(TextUtils.parseDate(request.getString("receipt_date")));
			dto.setTotalValue(request.getFloat("total_value"));
			dto.setDeliveredQuantity(request.getInteger("delivered_quantity"));
			dto.setTermsOfPayment(request.getString("terms_of_payment"));
			dto.setStatus(RequestStatus.CLOSED.toString());
		}
		
		return dto;
	}	
}
