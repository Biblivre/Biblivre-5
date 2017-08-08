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
package biblivre.core.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import biblivre.core.utils.Pair;


public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(this.getClass());

	private List<Pair<String, String>> errorList;
	
    public ValidationException(String s) {
    	super(s);
    }
	
	public ValidationException(String s, Exception cause) {
    	super(s, cause);
		this.logger.error(cause.getMessage(), cause);  
    }

    public void addError(String key, String value) {
    	if (this.errorList == null) {
    		this.errorList = new ArrayList<Pair<String, String>>();
    	}
    	
    	this.errorList.add(new Pair<String, String>(key, value));
    }

    public List<Pair<String, String>> getErrorList() {
    	return this.errorList;
    }
    
    public boolean hasErrors() {
    	return this.errorList != null && this.errorList.size() > 0;
    }
}
