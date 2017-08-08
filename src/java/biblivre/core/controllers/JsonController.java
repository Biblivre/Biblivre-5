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
package biblivre.core.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.Message;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Pair;

public class JsonController extends Controller { 

	public JsonController(ExtendedRequest xRequest, ExtendedResponse xResponse) {
		super(xRequest, xResponse);
	}

	@Override
	protected void doReturn() throws ServletException, IOException {
		JSONObject json = this.handler.getJson();
		Message message = this.handler.getMessage();

		this.dispatch(json, message);
	}

	@Override
	protected void doAuthorizationError() throws ServletException, IOException {
		Message message = new Message(ActionResult.WARNING, "error.no_permission");

		this.dispatch(null, message);
	}
	
	@Override
	protected void doLockedStateError() throws ServletException, IOException {
		Message message = new Message(ActionResult.WARNING, "error.biblivre_is_locked_please_wait");

		this.dispatch(null, message);
	}
	
	@Override
	protected void doError(String error, Throwable e) throws ServletException, IOException {
		if (e != null && this.log.isDebugEnabled()) {
			this.log.error(error, e);
		} else {
			this.log.error(error);
		}
		
		//e.printStackTrace();

		Message message = new Message(ActionResult.ERROR, error, e);
		this.dispatch(null, message);
	}
	
	@Override
	protected void doWarning(String warning, Throwable e) throws ServletException, IOException {
		if (e != null && this.log.isDebugEnabled()) {
			this.log.warn(warning, e);
		} else {
			this.log.warn(warning);
		}
		
		//e.printStackTrace();

		Message message = new Message(ActionResult.WARNING, warning, e);
		this.dispatch(null, message);
	}
	
	private void dispatch(JSONObject json, Message message) throws IOException {
		if (json == null) {
			json = new JSONObject();
		}
		
		if (message == null) {
			message = new Message();
		}
		
		try {
			json.putOnce("success", message.isSuccess());

			if (StringUtils.isNotBlank(message.getText())) {
				json.putOnce("message", this.xRequest.getLocalizedText(message.getText()));
				json.putOnce("message_level", message.getLevel());
			}

			if (StringUtils.isNotBlank(message.getStackTrace(false))) {
				json.putOnce("stack_trace", message.getStackTrace(false));
			}

			List<Pair<String, String>> errorList = message.getErrorList();

			if (errorList != null && !json.has("errors")) {
				for (Pair<String, String> pair : errorList) {
					JSONObject error = new JSONObject();
					error.put(pair.getLeft(), this.xRequest.getLocalizedText(pair.getRight()));
					json.append("errors", error);
				}
			}
		} catch (JSONException je) {}

		if (this.xRequest.isMultiPart()) {			
	        this.xResponse.setContentType("text/html;charset=UTF-8");			
		} else {
	        this.xResponse.setContentType("application/json;charset=UTF-8");			
		}
		
        try {
			this.xResponse.print(json.toString(2));
		} catch (JSONException e) {
			this.log.error(e.getMessage(), e);
		}
	}
}
