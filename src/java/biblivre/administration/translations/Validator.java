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
package biblivre.administration.translations;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.MemoryFile;

public class Validator extends AbstractValidator {
	
	public void validateDump(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		String language = request.getString("language");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (StringUtils.isBlank(language)) {
			ex.addError("language", "administration.translations.error.invalid_language");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
	}
	
	public void validateLoad(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		MemoryFile file = request.getFile("file");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (file == null || file.getSize() <= 0) {
			ex.addError("file", "administration.translations.error.invalid_file");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
	}	
	

}
