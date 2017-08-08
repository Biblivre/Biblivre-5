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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import biblivre.cataloging.enums.AutocompleteType;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.JavascriptCacheableList;
import biblivre.core.StaticBO;
import biblivre.core.utils.Pair;

public class Fields extends StaticBO {

	private static Logger logger = Logger.getLogger(Fields.class);

	private static HashMap<Pair<String, RecordType>, List<BriefTabFieldFormatDTO>> briefTabFieldFormats;
	private static HashMap<Pair<String, RecordType>, JavascriptCacheableList<FormTabDatafieldDTO>> formTabFields;
	private static HashMap<Pair<String, RecordType>, List<FormTabSubfieldDTO>> autocompleteSubfields;

	private Fields() {
	}

	static {
		Fields.reset();
	}

	public static void reset() {
		Fields.briefTabFieldFormats = new HashMap<Pair<String, RecordType>, List<BriefTabFieldFormatDTO>>();
		Fields.formTabFields = new HashMap<Pair<String, RecordType>, JavascriptCacheableList<FormTabDatafieldDTO>>();
		Fields.autocompleteSubfields = new HashMap<Pair<String, RecordType>, List<FormTabSubfieldDTO>>();
	}

	public static void reset(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
		Fields.briefTabFieldFormats.remove(pair);
		Fields.formTabFields.remove(pair);
		Fields.autocompleteSubfields.remove(pair);
	}

	public static List<BriefTabFieldFormatDTO> getBriefFormats(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);		
		List<BriefTabFieldFormatDTO> list = Fields.briefTabFieldFormats.get(pair);

		if (list == null) {
			list = Fields.loadBriefFormats(schema, recordType);
		}

		return list;
	}
	
	public static boolean insertBriefFormat(String schema, RecordType recordType, BriefTabFieldFormatDTO dto, int loggedUser) {

		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);
		boolean result = dao.insertBriefFormat(dto, recordType, loggedUser);

		if (result) {
			Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
			Fields.briefTabFieldFormats.remove(pair);
		}

		return result;
	}
	
	public static boolean updateBriefFormats(String schema, RecordType recordType, List<BriefTabFieldFormatDTO> briefFormats, int loggedUser) {

		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);
		boolean result = dao.updateBriefFormats(briefFormats, recordType, loggedUser);

		if (result) {
			Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
			Fields.briefTabFieldFormats.remove(pair);
		}

		return result;
	}
	
	public static boolean updateFormTabDatafield(String schema, RecordType recordType, HashMap<String, FormTabDatafieldDTO> formDatafields, int loggedUser) {
		
		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);
		boolean result = dao.updateFormTabDatafield(formDatafields, recordType, loggedUser);

		if (result) {
			Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
			Fields.formTabFields.remove(pair);
		}
		
		return result;
	}
	
	public static boolean deleteBriefFormat(String schema, RecordType recordType, String datafield) {

		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);
		boolean result = dao.deleteBriefFormat(datafield, recordType);

		if (result) {
			Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
			Fields.briefTabFieldFormats.remove(pair);
		}

		return result;
	}
	
	public static boolean deleteFormTabDatafield(String schema, RecordType recordType, String datafield) {

		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);
		boolean result = dao.deleteFormTabDatafield(datafield, recordType);

		if (result) {
			Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
			Fields.formTabFields.remove(pair);
		}

		return result;
	}

	public static JavascriptCacheableList<FormTabDatafieldDTO> getFormFields(String schema, String type) {
		RecordType recordType = RecordType.fromString(type);
		if (recordType == null) {
			recordType = RecordType.BIBLIO;
		}
		return Fields.getFormFields(schema, recordType);
	}
	
	public static JavascriptCacheableList<FormTabDatafieldDTO> getFormFields(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);		
		JavascriptCacheableList<FormTabDatafieldDTO> list = Fields.formTabFields.get(pair);

		if (list == null) {
			list = Fields.loadFormFields(schema, recordType);
		}

		return list;
	}
	
	public static List<FormTabSubfieldDTO> getAutocompleteSubFields(String schema, String type) {
		RecordType recordType = RecordType.fromString(type);
		if (recordType == null) {
			recordType = RecordType.BIBLIO;
		}
		return Fields.getAutocompleteSubFields(schema, recordType);
	}
	
	public static List<FormTabSubfieldDTO> getAutocompleteSubFields(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);		
		List<FormTabSubfieldDTO> list = Fields.autocompleteSubfields.get(pair);

		if (list == null) {
			list = Fields.loadAutocompleteSubFields(schema, recordType);
		}

		return list;
	}
	
	private static synchronized List<BriefTabFieldFormatDTO> loadBriefFormats(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
		List<BriefTabFieldFormatDTO> list = Fields.briefTabFieldFormats.get(pair);

		// Checking again for thread safety.
		if (list != null) {
			return list;
		}

		if (Fields.logger.isDebugEnabled()) {
			Fields.logger.debug("Loading brief formats from " + schema + "." + recordType);
		}

		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);

		list = dao.listBriefFormats(recordType);
		Fields.briefTabFieldFormats.put(pair, list);

		return list;
	}
	
	private static synchronized JavascriptCacheableList<FormTabDatafieldDTO> loadFormFields(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
		JavascriptCacheableList<FormTabDatafieldDTO> list = Fields.formTabFields.get(pair);

		// Checking again for thread safety.
		if (list != null) {
			return list;
		}

		if (Fields.logger.isDebugEnabled()) {
			Fields.logger.debug("Loading form fields from " + schema + "." + recordType);
		}

		TabFieldsDAO dao = TabFieldsDAO.getInstance(schema);

		List<FormTabDatafieldDTO> fields = dao.listFields(recordType);
		if (recordType == RecordType.HOLDING) {
			list = new JavascriptCacheableList<FormTabDatafieldDTO>("CatalogingInput.holdingFields", schema + ".cataloging." + recordType.toString(), ".form.js");			
		} else {
			list = new JavascriptCacheableList<FormTabDatafieldDTO>("CatalogingInput.formFields", schema + ".cataloging." + recordType.toString(), ".form.js");
		}
		list.addAll(fields);

		Fields.formTabFields.put(pair, list);

		return list;
	}
	
	private static synchronized List<FormTabSubfieldDTO> loadAutocompleteSubFields(String schema, RecordType recordType) {
		Pair<String, RecordType> pair = new Pair<String, RecordType>(schema, recordType);
		List<FormTabSubfieldDTO> list = Fields.autocompleteSubfields.get(pair);

		// Checking again for thread safety.
		if (list != null) {
			return list;
		}

		if (Fields.logger.isDebugEnabled()) {
			Fields.logger.debug("Loading autocomplete subfields from " + schema + "." + recordType);
		}
		
		JavascriptCacheableList<FormTabDatafieldDTO> fields = Fields.getFormFields(schema, recordType);
		list = new LinkedList<FormTabSubfieldDTO>();

		if (fields == null) {
			return list;
		}
		
		for (FormTabDatafieldDTO datafield : fields) {
			for (FormTabSubfieldDTO subfield : datafield.getSubfields()) {
				AutocompleteType type = subfield.getAutocompleteType();

				if ((type == AutocompleteType.PREVIOUS_VALUES) || (type == AutocompleteType.FIXED_TABLE_WITH_PREVIOUS_VALUES)) {
					list.add(subfield);
				} else if (recordType == RecordType.AUTHORITIES) {
					if ("100,110,111".contains(datafield.getDatafield())) {
						if (subfield.getSubfield().equals("a")) {
							list.add(subfield);
						}
					}
				} else if (recordType == RecordType.VOCABULARY) {
					if ("150".contains(datafield.getDatafield())) {
						if (subfield.getSubfield().equals("a")) {
							list.add(subfield);
						}
					}
				}

			}
		}
		
		Fields.autocompleteSubfields.put(pair, list);

		return list;
	}
}
