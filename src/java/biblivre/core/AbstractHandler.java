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
package biblivre.core;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.BiblivreFile;

public abstract class AbstractHandler {

	protected JSONObject json;
	protected String jspURL;
	protected Message message;
	protected BiblivreFile file;
	protected int returnCode;
	protected HttpCallback callback;
	
	public class HttpCallback {
		public void success() {};
	}
	
	public AbstractHandler() {
		this.setJson(new JSONObject());
		this.setJspURL("");
		this.setMessage(new Message());
		this.setFile(null);
		this.setReturnCode(0);
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public JSONObject getJson() {
		return this.json;
	}

	public void setJspURL(String jspURL) {
		this.jspURL = jspURL;
	}

	public String getJspURL() {
		return this.jspURL;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setMessage(ActionResult level) {
		this.message = new Message(level, "");
	}

	public void setMessage(ActionResult level, String message) {
		this.message = new Message(level, message);
	}

	public void setMessage(Throwable exception) {
		this.message = new Message(exception);
	}
	
	public Message getMessage() {
		return this.message;
	}

	public boolean hasErrors() {
		Message message = this.getMessage();
		boolean hasErrorMessage = StringUtils.isNotBlank(message.getText());

		if (!hasErrorMessage) {
			return false;
		}
		
		ActionResult level = message.getLevel();
		switch (level) {
			case WARNING:
			case ERROR:
				return true;
			default:
				return false;
		}
	}
	
	public BiblivreFile getFile() {
		return this.file;
	}

	public void setFile(BiblivreFile file) {
		this.file = file;
	}

	public int getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public HttpCallback getCallback() {
		return this.callback;
	}

	public void setCallback(HttpCallback callback) {
		this.callback = callback;
	}
	
	protected void authorize(ExtendedRequest request, String module, String action) {
		AuthorizationPoints authPoints = (AuthorizationPoints) request.getSessionAttribute(request.getSchema(), "logged_user_atps");
		if (authPoints == null) {
			authPoints = AuthorizationPoints.getNotLoggedInstance(request.getSchema());
		}

		AuthorizationBO abo = AuthorizationBO.getInstance(request.getSchema());
		abo.authorize(authPoints, module, action);
	}
}
