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
package biblivre.administration.customization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.Fields;
import biblivre.cataloging.FormTabDatafieldDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.translations.Translations;

public class Handler extends AbstractHandler {

	public void saveBriefFormats(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();

		RecordType recordType = request.getEnum(RecordType.class, "record_type", RecordType.BIBLIO);

		String briefFormats = request.getString("formats", "{}");
		List<BriefTabFieldFormatDTO> list = new ArrayList<BriefTabFieldFormatDTO>();
		
		try {
			JSONArray json = new JSONArray(briefFormats);

			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);
				list.add(new BriefTabFieldFormatDTO(jsonObject));
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}

		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
		
		boolean success = Fields.updateBriefFormats(schema, recordType, list, loggedUser);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.save");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
		}
	}
	
	public void insertBriefFormat(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();

		RecordType recordType = request.getEnum(RecordType.class, "record_type", RecordType.BIBLIO);
		
		String briefFormats = request.getString("formats", "{}");
		BriefTabFieldFormatDTO dto = null;
		
		try {
			JSONArray json = new JSONArray(briefFormats);

			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);
				dto = new BriefTabFieldFormatDTO(jsonObject);
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}

		if (dto == null) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
		
		boolean success = Fields.insertBriefFormat(schema, recordType, dto, loggedUser);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.save");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
		}
	}
	
	public void deleteBriefFormat(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		RecordType recordType = request.getEnum(RecordType.class, "record_type", RecordType.BIBLIO);
		
		String briefFormats = request.getString("formats", "{}");
		BriefTabFieldFormatDTO dto = null;
		
		try {
			JSONArray json = new JSONArray(briefFormats);

			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);
				dto = new BriefTabFieldFormatDTO(jsonObject);
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}

		if (dto == null) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
		
		boolean success = Fields.deleteBriefFormat(schema, recordType, dto.getDatafieldTag());
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.save");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
		}
	}
	
	public void saveFormDatafields(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();
		
		RecordType recordType = request.getEnum(RecordType.class, "record_type", RecordType.BIBLIO);
		
		String fields = request.getString("fields", "{}");
		HashMap<String, FormTabDatafieldDTO> map = new HashMap<String, FormTabDatafieldDTO>();
		HashMap<String, String> translations = new HashMap<String, String>();
		
		try {
			JSONObject json = new JSONObject(fields);
			Iterator<String> it = json.keys();
			
			while (it.hasNext()) {
				String datafield = it.next();
				
				if (StringUtils.isBlank(datafield) || datafield.trim().length() != 3 || !StringUtils.isNumeric(datafield)) {
					this.setMessage(ActionResult.WARNING, "administration.form_customization.error.invalid_tag");
					return;
				}
				
				datafield = datafield.trim();
				
				JSONObject jsonObject = json.getJSONObject(datafield).getJSONObject("formtab");
				map.put(datafield, new FormTabDatafieldDTO(jsonObject));
				
				JSONObject translationsObject = json.getJSONObject(datafield).getJSONObject("translations");
				
				for (String key : translationsObject.keySet()) {
					translations.put(key, translationsObject.getString(key));
				}
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
		
		if (map.size() == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
		
		boolean success = Fields.updateFormTabDatafield(schema, recordType, map, loggedUser);
		success = success && Translations.save(schema, request.getLanguage(), translations, null, loggedUser);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.save");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
		}
	}
	
	public void deleteFormDatafield(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		RecordType recordType = request.getEnum(RecordType.class, "record_type", RecordType.BIBLIO);
		String datafieldTag = request.getString("datafield");
				
		boolean success = Fields.deleteFormTabDatafield(schema, recordType, datafieldTag);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.delete");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.delete");
		}
	}
	
}
