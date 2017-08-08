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
package biblivre.multi_schema;

import java.io.File;

import org.json.JSONException;

import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.schemas.Schemas;
import biblivre.core.utils.Constants;

public class Handler extends AbstractHandler {

	public void create(ExtendedRequest request, ExtendedResponse response) {
		
		String titleParam = request.getString("title");
		String subtitleParam = request.getString("subtitle");
		String schemaParam = request.getString("schema");

		SchemaDTO dto = new SchemaDTO();
		dto.setName(titleParam);
		dto.setSchema(schemaParam);
		dto.setCreatedBy(request.getLoggedUserId());
		
		State.start();
		State.writeLog(request.getLocalizedText("multi_schema.manage.log_header"));

		File template = new File(request.getSession().getServletContext().getRealPath("/"), "biblivre_template_4.0.0.sql");

		boolean success = Schemas.createSchema(dto, template, true);
		if (success) {
			State.finish();
			
			Configurations.save(schemaParam, new ConfigurationsDTO(Constants.CONFIG_TITLE, titleParam), request.getLoggedUserId());
			Configurations.save(schemaParam, new ConfigurationsDTO(Constants.CONFIG_SUBTITLE, subtitleParam), request.getLoggedUserId());
			this.setMessage(ActionResult.SUCCESS, "multi_schema.manage.success.create");			
		} else {			
			State.cancel();

			this.setMessage(ActionResult.WARNING, "multi_schema.manage.error.create");
		}
		
		try {
			this.json.put("success", success);

			if (success) {
				this.json.put("data", dto.toJSONObject());
				this.json.put("full_data", true);
			} else {
				this.json.put("log", true);
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	

	public void toggle(ExtendedRequest request, ExtendedResponse response) {
		String schemaParam = request.getString("schema");
		boolean disable = request.getBoolean("disable", false);	

		SchemaDTO dto = Schemas.getSchema(schemaParam);
		
		if (dto == null) {
			this.setMessage(ActionResult.WARNING, "multi_schema.manage.error.toggle");
			return;
		}

		if (disable) {			
			if (Schemas.countEnabledSchemas() <= 1) {
				this.setMessage(ActionResult.WARNING, "multi_schema.manage.error.cant_disable_last_library");
				return;
			}
		}
		
		boolean success = (disable) ? Schemas.disable(dto) : Schemas.enable(dto);
		try {
			this.json.put("success", success);
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	public void deleteSchema(ExtendedRequest request, ExtendedResponse response) {
		
		String schemaParam = request.getString("schema");

		SchemaDTO dto = Schemas.getSchema(schemaParam);
		boolean success = Schemas.deleteSchema(dto);
		
		try {
			this.json.put("success", success);
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
}
