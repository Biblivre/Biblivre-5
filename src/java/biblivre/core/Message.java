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

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.utils.Pair;
import biblivre.core.utils.TextUtils;

public class Message {
	private ActionResult level;
	private String text;
	private String stackTrace;
	private List<Pair<String, String>> errorList;
	
	public Message(ActionResult level, String text) {
		super();
		this.setLevel(level);
		this.setText(text);		
	}

	public Message(ActionResult level, String message, Throwable exception) {
		this(level, message);

		if (exception != null) {	
			if (exception instanceof ValidationException) {
				this.setErrorList(((ValidationException) exception).getErrorList());
			} else {
				this.setStackTrace(ExceptionUtils.getStackTrace(exception));
			}
		}
	}
	
	public Message() {
		this(ActionResult.NORMAL, "");
	}

	public Message(Throwable exception) {
		this(ActionResult.WARNING, exception.getMessage(), exception);
	}
	
	public boolean isSuccess() {
		return ActionResult.NORMAL.equals(this.getLevel()) || ActionResult.SUCCESS.equals(this.getLevel());
	}
	
	public ActionResult getLevel() {
		return this.level;
	}
	
	public void setLevel(ActionResult level) {
		this.level = level;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getStackTrace(boolean encode) {
		if (!encode || this.stackTrace == null) {
			return this.stackTrace;
		} else {
			System.out.println(this.stackTrace);
			return TextUtils.biblivreEncode(this.stackTrace);
		}
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public void setText(ActionResult level, String text) {
		this.setLevel(level);
		this.setText(text);
	}

	public List<Pair<String, String>> getErrorList() {
		return this.errorList;
	}

	public void setErrorList(List<Pair<String, String>> errorList) {
		this.errorList = errorList;
	}
}
