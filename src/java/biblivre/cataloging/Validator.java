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
package biblivre.cataloging;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractHandler;
import biblivre.core.AbstractValidator;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.MemoryFile;

public class Validator extends AbstractValidator {
	
	public void validateImportUpload(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		
		MemoryFile file = request.getFile("file");
		
		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (file == null || file.getSize() <= 0) {
			ex.addError("file", "cataloging.import.error.invalid_file");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
	}	
	
	public void validateImportSearch(AbstractHandler handler, ExtendedRequest request, ExtendedResponse response) {
		Integer id = request.getInteger("search_server", 0);
		String attribute = request.getString("search_attribute");
		String value = request.getString("search_query");

		ValidationException ex = new ValidationException("error.form_invalid_values");
		
		if (id == 0) {
			ex.addError("search_server", "field.error.invalid");			
		}

		if (StringUtils.isBlank(attribute)) {
			ex.addError("search_attribute", "field.error.required");
		}

		if (StringUtils.isBlank(value)) {
			ex.addError("search_query", "field.error.required");
		}

		if (ex.hasErrors()) {
			handler.setMessage(ex);
			return;
		}
	}
}
