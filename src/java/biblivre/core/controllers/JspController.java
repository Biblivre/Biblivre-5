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

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.Dialog;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.Message;
import biblivre.core.enums.ActionResult;

public class JspController extends Controller {

	public JspController(ExtendedRequest xRequest, ExtendedResponse xResponse) {
		super(xRequest, xResponse);
	}
	
	@Override
	protected void doReturn() throws ServletException, IOException {
		String jsp = this.handler.getJspURL();
		Message message = this.handler.getMessage();

		if (StringUtils.isBlank(jsp)) {
			jsp = "/jsp/error.jsp";
		}

		this.dispatch(jsp, message);
	}

	@Override
	protected void doAuthorizationError() throws ServletException, IOException {
		Message message = new Message(ActionResult.WARNING, "error.no_permission");

		this.dispatch("/jsp/error.jsp", message);
	}
	
	@Override
	protected void doLockedStateError() throws ServletException, IOException {
		Message message = new Message(ActionResult.WARNING, "error.biblivre_is_locked_please_wait");

		this.dispatch("/jsp/error.jsp", message);
	}
	
	@Override
	protected void doError(String error, Throwable e) throws ServletException, IOException {
		if (e != null && this.log.isDebugEnabled()) {
			this.log.error(error, e);
		} else {
			this.log.error(error);
		}
		
		Message message = new Message(ActionResult.ERROR, error, e);
		this.dispatch("/jsp/error.jsp", message);
	}
	
	@Override
	protected void doWarning(String warning, Throwable e) throws ServletException, IOException {
		if (e != null && this.log.isDebugEnabled()) {
			this.log.warn(warning, e);
		} else {
			this.log.warn(warning);
		}
		
		Message message = new Message(ActionResult.WARNING, warning, e);
		this.dispatch("/jsp/error.jsp", message);
	}
	
	private void dispatch(String jsp, Message message) throws ServletException, IOException {
    	if (StringUtils.isNotBlank(message.getText())) {
        	Dialog.show(this.xRequest, this.xRequest.getLocalizedText(message.getText()), message.getLevel());
    	}

		this.xResponse.setContentType("text/html;charset=UTF-8");
		this.xRequest.getRequestDispatcher(jsp).forward(this.xRequest, this.xResponse);
//		this.xRequest.dispatch(jsp, this.xResponse);
	}
}
