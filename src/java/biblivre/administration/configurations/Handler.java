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
package biblivre.administration.configurations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.schemas.Schemas;
import biblivre.core.translations.Translations;

public class Handler extends AbstractHandler {

	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();
		String language = request.getLanguage();

		String configurations = request.getString("configurations", "{}");
		List<ConfigurationsDTO> configs = new ArrayList<ConfigurationsDTO>();
		
		try {
			JSONObject json = new JSONObject(configurations);

			Iterator<String> it = json.keys();
			while (it.hasNext()) {
				String key = it.next();
				String value = json.getString(key);

				if (key.equals("text.main.logged_in") || key.equals("text.main.logged_out")) {
					Translations.addSingleTranslation(schema, language, key, value, loggedUser);
				} else {
					configs.add(new ConfigurationsDTO(key, value));
				}
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}

		if (configs.size() == 0) {
			return;
		}
		
		try {
			configs = Configurations.validate(schema, configs);
		} catch (ValidationException e) {
			this.setMessage(e);
			return;
		}
		
		try {
			boolean multiSchemaBefore = Schemas.isMultipleSchemasEnabled();
			Configurations.save(schema, configs, loggedUser);
			boolean multiSchemaAfter = Schemas.isMultipleSchemasEnabled();

			this.setMessage(ActionResult.SUCCESS, "administration.configurations.save.success");
			this.json.put("reload", multiSchemaBefore != multiSchemaAfter);
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "administration.configurations.error.save");
			return;
		}
	}
	
	public void ignoreUpdate(ExtendedRequest request, ExtendedResponse response) {
		request.getSession().removeAttribute(request.getSchema() + ".system_warning_new_version");
	}
}
