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

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;

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

		RequestBO bo = RequestBO.getInstance(schema);
		DTOCollection<RequestDTO> list = bo.search(query, limit, offset);

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

		RequestBO bo = RequestBO.getInstance(schema);
		RequestDTO dto = bo.get(id);

		try {
			this.json.put("request", dto.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer id = request.getInteger("id");		
		RequestDTO dto = this.populateDTO(request);

		RequestBO bo = RequestBO.getInstance(schema);
		
		boolean result = false;
		if (id == 0) {
			dto.setStatus(RequestStatus.PENDING);;
			dto.setCreatedBy(request.getLoggedUserId());
			result = bo.save(dto);
		} else {
			dto.setId(id);
			dto.setModifiedBy(request.getLoggedUserId());
			result = bo.update(dto);
		}
		if (result) {
			if (id == 0) {
				this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.save");
			} else {
				this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.update");
			}
		} else {
			this.setMessage(ActionResult.WARNING, "acquisition.request.error.save");
		}
		
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
		
		RequestBO bo = RequestBO.getInstance(schema);
		RequestDTO dto = new RequestDTO();
		dto.setId(id);

		if (bo.delete(dto)) {
			this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.delete");
		} else {
			this.setMessage(ActionResult.WARNING, "acquisition.request.error.delete");
		}
	}
	
	private RequestDTO populateDTO(ExtendedRequest request) {
		RequestDTO dto = new RequestDTO();
		dto.setId(request.getInteger("id"));
		dto.setRequester(request.getString("requester"));
		dto.setAuthor(request.getString("author"));
		dto.setTitle(request.getString("title"));
		dto.setSubtitle(request.getString("subtitle"));
		dto.setEditionNumber(request.getString("edition"));
		dto.setPublisher(request.getString("publisher"));
		dto.setInfo(request.getString("info"));
		dto.setStatus(RequestStatus.fromString(request.getString("status")));
		dto.setQuantity(request.getInteger("quantity"));
		return dto;
	}	
}
