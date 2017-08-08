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
package biblivre.administration.reports;

import org.json.JSONException;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.circulation.lending.LendingListDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.TextUtils;

public class Handler extends AbstractHandler {

	public void userSearch(ExtendedRequest request, ExtendedResponse response) {

		biblivre.circulation.user.Handler userHandler = new biblivre.circulation.user.Handler();
		DTOCollection<UserDTO> userList = userHandler.searchHelper(request,
				response, this);

		if (userList == null || userList.size() == 0) {
			this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
			return;
		}

		DTOCollection<LendingListDTO> list = new DTOCollection<LendingListDTO>();
		list.setPaging(userList.getPaging());

		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}

	public void generate(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		ReportsBO bo = ReportsBO.getInstance(schema);

		ReportsDTO dto = null;
		try {
			dto = this.populateDto(request);
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		DiskFile report = bo.generateReport(dto, request.getTranslationsMap());
		
		if (report != null) {
			request.setSessionAttribute(schema, report.getName(), report);
			this.setMessage(ActionResult.SUCCESS, "administration.reports.success.generate");
		} else {
			this.setMessage(ActionResult.WARNING, "administration.reports.error.generate");
		}
		
		try {
			if (report != null) {
				this.json.put("file_name", report.getName());
			}
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	//http://localhost:8080/Biblivre5/?controller=download&module=cataloging.export&action=download_report&file_name={export_id}
	public void downloadReport(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String report_name = request.getString("file_name");
				
		final DiskFile report = (DiskFile)request.getSessionAttribute(schema, report_name);

		this.setFile(report);
		
		this.setCallback(new HttpCallback() {
			@Override
			public void success() {
				try {
					report.delete();
				} catch (Exception e) {}
			}
		});
	}

	private ReportsDTO populateDto(ExtendedRequest request) throws Exception {
		ReportsDTO dto = new ReportsDTO();

		String reportId = request.getString("report");
		ReportType type = ReportType.getById(reportId);
		dto.setType(type);

		if (type.isTimePeriod()) {
			dto.setInitialDate(TextUtils.parseDate(request.getString("start")));
			dto.setFinalDate(TextUtils.parseDate(request.getString("end")));
		}

		dto.setDatabase(RecordDatabase.fromString(request.getString("database")));
		dto.setOrder(request.getString("order"));
		dto.setCountOrder(request.getString("count_order"));
		dto.setSearchId(request.getInteger("search_id"));
		dto.setMarcField(request.getString("marc_field"));
		dto.setUserId(request.getString("user_id"));
		dto.setRecordIds(request.getString("recordIds"));
		dto.setAuthorName(request.getString("authorName"));
		dto.setDatafield(request.getString("datafield"));
		dto.setDigits(request.getInteger("digits"));

		return dto;
	}

}
