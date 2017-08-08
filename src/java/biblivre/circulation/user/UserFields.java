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
package biblivre.circulation.user;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import biblivre.core.JavascriptCacheableList;
import biblivre.core.StaticBO;

public class UserFields extends StaticBO {

	private static Logger logger = Logger.getLogger(UserFields.class);

	private static HashMap<String, JavascriptCacheableList<UserFieldDTO>> fields;	 // FormTab

	private UserFields() {
	}

	static {
		UserFields.reset();
	}

	public static void reset() {
		UserFields.fields = new HashMap<String, JavascriptCacheableList<UserFieldDTO>>();
	}

	public static void reset(String schema) {
		UserFields.fields.remove(schema);
	}

	public static JavascriptCacheableList<UserFieldDTO> getFields(String schema) {
		JavascriptCacheableList<UserFieldDTO> list = UserFields.fields.get(schema);

		if (list == null) {
			list = UserFields.loadFields(schema);
		}

		return list;
	}
	
	public static List<UserFieldDTO> getSearchableFields(String schema) {
		JavascriptCacheableList<UserFieldDTO> list = UserFields.getFields(schema);

		List<UserFieldDTO> searcheableList = new LinkedList<UserFieldDTO>();
		for (UserFieldDTO dto : list) {
			
			switch (dto.getType()) {
				case STRING:
				case TEXT:
				case NUMBER: searcheableList.add(dto); break;
				case DATE:
				case DATETIME:
				case BOOLEAN:
				case LIST:
				default: break;
			}
			
		}
		
		return searcheableList;
	}
	
	
	private static synchronized JavascriptCacheableList<UserFieldDTO> loadFields(String schema) {
		JavascriptCacheableList<UserFieldDTO> list = UserFields.fields.get(schema);

		// Checking again for thread safety.
		if (list != null) {
			return list;
		}

		if (UserFields.logger.isDebugEnabled()) {
			UserFields.logger.debug("Loading user fields from " + schema + ".");
		}

		UserFieldsDAO dao = UserFieldsDAO.getInstance(schema);

		
		List<UserFieldDTO> fields = dao.listFields();
		list = new JavascriptCacheableList<UserFieldDTO>("CirculationInput.userFields", schema + ".circulation", ".user_fields.js");
		list.addAll(fields);

		UserFields.fields.put(schema, list);

		return list;
	}
}
