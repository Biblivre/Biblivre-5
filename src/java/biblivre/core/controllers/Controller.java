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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationBO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;


public abstract class Controller {
	
	protected final Logger log = Logger.getLogger(this.getClass());
	protected ExtendedRequest xRequest;
	protected ExtendedResponse xResponse;
	protected AbstractHandler handler;
	protected boolean headerOnly;
	protected Class<?> handlerClass;

	public Controller(ExtendedRequest xRequest, ExtendedResponse xResponse) {
		this.xRequest = xRequest;
		this.xResponse = xResponse;
		this.headerOnly = false;
	}
	
	protected void processRequest() throws ServletException, IOException {
		String schema = null;
		String module = null;
		String action = null;
		
		this.xRequest.setCharacterEncoding("UTF-8");
		this.xResponse.setCharacterEncoding("UTF-8");
		
		try {
			schema = this.xRequest.getSchema();
			module = this.xRequest.getString("module", (String) this.xRequest.getAttribute("module"));
			action = this.xRequest.getString("action", (String) this.xRequest.getAttribute("action"));
			
			// In case of invalid pack and method, send user to index page
			if (StringUtils.isBlank(module) || StringUtils.isBlank(action)) {
				this.doError("error.void");
				return;
			}

			if (State.LOCKED.get() && !action.equals("progress")) {
				this.doLockedStateError();
				return;	
			}

			boolean isSetup = (module.equals("administration.setup") || (module.equals("menu") && action.equals("setup")));
			
			if (isSetup && (Configurations.getBoolean(schema, Constants.CONFIG_NEW_LIBRARY) || action.equals("progress"))) {
				// authorize
			} else {	
				AuthorizationPoints authPoints = (AuthorizationPoints) this.xRequest.getSessionAttribute(schema, "logged_user_atps");
				if (authPoints == null) {
					authPoints = AuthorizationPoints.getNotLoggedInstance(schema);
				}
	
				AuthorizationBO abo = AuthorizationBO.getInstance(schema);
				abo.authorize(authPoints, module, action);
			}			
		} catch (AuthorizationException e) {
			// Exception thrown in abo.authorize
			this.doAuthorizationError();
			return;	
		}
		
		try {
			this.handlerClass = Class.forName("biblivre." + module + ".Handler");
			this.handler = (AbstractHandler) this.handlerClass.newInstance();
			
			Class<?> validatorClass = Class.forName("biblivre." + module + ".Validator");
			String validationMethodName = "validate_" + action;
			Method validationMethod = validatorClass.getDeclaredMethod(TextUtils.camelCase(validationMethodName), AbstractHandler.class, ExtendedRequest.class, ExtendedResponse.class);
			
			AbstractValidator validator = (AbstractValidator) validatorClass.newInstance();
			validationMethod.invoke(validator, this.handler, this.xRequest, this.xResponse);
			if (!validator.checkValidation(this.handler)) {
				this.doReturn();
				return;
			}
		} catch (AuthorizationException e) {
			this.doAuthorizationError();
		} catch (ClassNotFoundException cnfe) {
			//No Validator found, do nothing.
		} catch (NoSuchMethodException nsme) {
			//No Method found in the Validator, so do nothing.
		} catch (InvocationTargetException e) {
			// Exception thrown in method.invoke
			Throwable handlerException = e.getTargetException();
			if (handlerException instanceof AuthorizationException) {
				this.doAuthorizationError();
			} else {
				this.doError("error.runtime_error", handlerException);
			}
			this.log.error(e.getMessage(), e);
			return;
		} catch (Exception e) {
			// ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, etc.
			this.doError("error.invalid_handler", e);
			return;
		}
		
		try {
			Method method;
			
			try {
				method = this.handlerClass.getDeclaredMethod(TextUtils.camelCase(action), ExtendedRequest.class, ExtendedResponse.class);
			} catch (NoSuchMethodException e) {
				method = this.handlerClass.getSuperclass().getDeclaredMethod(TextUtils.camelCase(action), ExtendedRequest.class, ExtendedResponse.class);
			}

			method.invoke(this.handler, this.xRequest, this.xResponse);
		} catch (InvocationTargetException e) {
			// Exception thrown in method.invoke
			Throwable handlerException = e.getTargetException();

			if (handlerException instanceof AuthorizationException) {
				this.doAuthorizationError();
			} else 	if (handlerException instanceof ValidationException) {
				this.doWarning(handlerException.getMessage(), handlerException);
			} else {
				this.doError("error.runtime_error", handlerException);
			}
			this.log.error(e.getMessage(), e);
			return;
			
		} catch (Exception e) {
			// ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, etc.
			this.doError("error.invalid_handler", e);
			this.log.error(e.getMessage(), e);
			return;
		}

		this.doReturn();
	}
	
	public boolean isHeaderOnly() {
		return this.headerOnly;
	}

	public void setHeaderOnly(boolean headerOnly) {
		this.headerOnly = headerOnly;
	}

	protected abstract void doReturn() throws ServletException, IOException;
	protected abstract void doAuthorizationError() throws ServletException, IOException;
	protected abstract void doLockedStateError() throws ServletException, IOException;
	protected abstract void doError(String error, Throwable e) throws ServletException, IOException;
	protected abstract void doWarning(String warning, Throwable e) throws ServletException, IOException;

	protected void doError(String error) throws ServletException, IOException {
		this.doError(error, null);
	}
}
