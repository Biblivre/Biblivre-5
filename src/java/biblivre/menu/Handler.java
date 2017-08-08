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
package biblivre.menu;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import biblivre.acquisition.request.RequestBO;
import biblivre.acquisition.request.RequestDTO;
import biblivre.acquisition.supplier.SupplierBO;
import biblivre.acquisition.supplier.SupplierDTO;
import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.z3950.Z3950AddressDTO;
import biblivre.z3950.Z3950BO;

public class Handler extends AbstractHandler {

	public void ping(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", true);
		} catch (JSONException e) {
		}
	}

	public void i18n(ExtendedRequest request, ExtendedResponse response) throws IOException {
		if (request.getString("from_translations").equals("true")) {
			this.jspURL = "/jsp/administration/translations.jsp";
		} else {
			this.jspURL = "/jsp/index.jsp";
		}
		return; 
	}

	public void listBibliographic(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		RecordType type = RecordType.BIBLIO;
		
		String letter = request.getString("letter");
		Integer order = request.getInteger("order",IndexingGroups.getDefaultSortableGroupId(schema, type));
		
		if (StringUtils.isBlank(letter)) {
			letter = "a";
		}
		
		RecordBO rbo = RecordBO.getInstance(schema, type);
		List<RecordDTO> records = rbo.listByLetter(letter.charAt(0), order);

		request.setAttribute("records", records);
		
		this.jspURL = "/jsp/list/bibliographic.jsp";
		return; 
	}
	
	public void listAuthorities(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/list/authorities.jsp";
		return; 
	}
	
	public void listVocabulary(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/list/vocabulary.jsp";
		return; 
	}
	
	public void searchBibliographic(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/search/bibliographic.jsp";
		return; 
	}
	
	public void searchAuthorities(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/search/authorities.jsp";
		return; 
	}
	
	public void searchVocabulary(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/search/vocabulary.jsp";
		return; 
	}
	
	public void searchZ3950(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Z3950BO bo = Z3950BO.getInstance(schema);
		
		List<Z3950AddressDTO> servers = bo.listAll();

		request.setAttribute("servers", servers);
		
		this.jspURL = "/jsp/search/z3950.jsp";
		return; 
	}

	public void catalogingBibliographic(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/cataloging/bibliographic.jsp";
		return; 
	}
	
	public void catalogingAuthorities(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/cataloging/authorities.jsp";
		return; 
	}

	public void catalogingVocabulary(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/cataloging/vocabulary.jsp";
		return; 
	}

	public void catalogingImport(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Z3950BO bo = Z3950BO.getInstance(schema);
		
		List<Z3950AddressDTO> servers = bo.listAll();

		request.setAttribute("servers", servers);
		
		this.jspURL = "/jsp/cataloging/import.jsp";
		return; 
	}

	public void catalogingLabels(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/cataloging/labels.jsp";
		return; 
	}
	
	public void circulationUser(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/circulation/user.jsp";
		return; 
	}

	public void circulationLending(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/circulation/lending.jsp";
		return; 
	}

	public void circulationReservation(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/circulation/reservation.jsp";
		return; 
	}
	
	public void circulationUserReservation(ExtendedRequest request, ExtendedResponse response) {
		Integer loggedUser = request.getLoggedUserId();
		if (loggedUser == null || loggedUser == 0) {
			this.jspURL = "/jsp/index.jsp";
		} else {
			UserDTO user = UserBO.getInstance(request.getSchema()).getUserByLoginId(loggedUser);
			if (user != null) {
				request.setAttribute("RESERVATION_USER_ID", user.getId());
			} else {
				request.setAttribute("RESERVATION_USER_ID", 0);
			}
			
			this.jspURL = "/jsp/circulation/user_reservation.jsp";
		}
		return;
	}
	
	public void circulationAccess(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/circulation/access_control.jsp";
		return; 
	}
	
	public void circulationUserCards(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/circulation/user_cards.jsp";
		return; 
	}
	
	public void acquisitionSupplier(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/acquisition/suppliers.jsp";
		return; 
	}
	
	public void acquisitionRequest(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/acquisition/requests.jsp";
		return; 
	}
	
	public void acquisitionQuotation(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		SupplierBO bo = SupplierBO.getInstance(schema);
		List<SupplierDTO> suppliers = bo.list();
		request.setAttribute("suppliers", suppliers);
		
		RequestBO rbo = RequestBO.getInstance(schema);
		List<RequestDTO> requests = rbo.list();
		request.setAttribute("requests", requests);
		
		this.jspURL = "/jsp/acquisition/quotations.jsp";
		return; 
	}
	
	public void acquisitionOrder(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		SupplierBO bo = SupplierBO.getInstance(schema);
		List<SupplierDTO> suppliers = bo.list();
		request.setAttribute("suppliers", suppliers);
		
		this.jspURL = "/jsp/acquisition/orders.jsp";
		return; 
	}
	
	public void administrationPassword(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/password.jsp";
		return;
	}

	public void administrationMaintenance(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/maintenance.jsp";
		return;
	}
	
	public void administrationPermissions(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/permissions.jsp";
		return;
	}

	public void administrationConfigurations(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/configurations.jsp";
		return;
	}

	public void administrationZ3950Servers(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/z3950_servers.jsp";
		return;
	}
	
	public void administrationReports(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/reports.jsp";
		return;
	}
	
	public void administrationUserTypes(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/user_types.jsp";
		return;
	}
	
	public void administrationAccessCards(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/access_cards.jsp";
		return;
	}
	
	public void administrationDatamigration(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/migration.jsp";
		return;
	}
	
	public void administrationTranslations(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/translations.jsp";
		return;
	}

	public void administrationBriefCustomization(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/brief_customization.jsp";
		return;
	}

	public void administrationFormCustomization(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/administration/form_customization.jsp";
		return;
	}
	
	public void multiSchemaTranslations(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/multi_schema/translations.jsp";
		return;
	}

	public void multiSchemaBackup(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/multi_schema/backup.jsp";
		return;
	}

	public void multiSchemaConfigurations(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/multi_schema/configurations.jsp";
		return;
	}

	public void multiSchemaManage(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/multi_schema/manage.jsp";
		return;
	}

	public void helpAboutBiblivre(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/help/about_biblivre.jsp";
		return;
	}
	
	public void setup(ExtendedRequest request, ExtendedResponse response) {
		this.jspURL = "/jsp/setup.jsp";
		return;
	}
	
	public void test(ExtendedRequest request, ExtendedResponse response) {
//		this.jspURL = "/jsp/record_tab_config.jsp";
		this.jspURL = "/jsp/upload.jsp";
		return; 
	}
}
