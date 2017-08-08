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
package biblivre.core.auth;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;

import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;


public class AuthorizationPoints implements Serializable {
	private static final long serialVersionUID = 1L;

	private static AuthorizationPoints notLoggedMultiSchemaInstance;
	private static AuthorizationPoints notLoggedSingleSchemaInstance;
	
	private HashMap<Pair<String, String>, Boolean> points;
	private HashMap<String, Boolean> permissions;
	private boolean admin;
	private boolean logged;

	private boolean employee;
	private String schema;

	public static AuthorizationPoints getNotLoggedInstance(String schema) {
		if (schema.equals(Constants.GLOBAL_SCHEMA)) {
			if (AuthorizationPoints.notLoggedMultiSchemaInstance == null) {
				AuthorizationPoints.notLoggedMultiSchemaInstance = new AuthorizationPoints(Constants.GLOBAL_SCHEMA, false, false, null);
			}

			return AuthorizationPoints.notLoggedMultiSchemaInstance;
		}
					
		if (AuthorizationPoints.notLoggedSingleSchemaInstance == null) {
			AuthorizationPoints.notLoggedSingleSchemaInstance = new AuthorizationPoints("*", false, false, null);
		}

		return AuthorizationPoints.notLoggedSingleSchemaInstance;
	}

	/**
	 * @param schema
	 * @param logged
	 * @param employee
	 * @param permissions
	 */
	public AuthorizationPoints(String schema, boolean logged, boolean employee, HashMap<String, Boolean> permissions) {
		this.schema = schema;
		this.admin = false;
		this.employee = employee;
		this.permissions = permissions;
		this.logged = logged;

		if (this.permissions == null) {
			this.permissions = new HashMap<String, Boolean>();
		}

		this.points = new HashMap<Pair<String, String>, Boolean>();

		this.addAuthPoint("login", "login", AuthorizationPointTypes.LOGIN);
		this.addAuthPoint("login", "logout", AuthorizationPointTypes.LOGIN);
		this.addAuthPoint("login", "change_password", AuthorizationPointTypes.LOGIN_CHANGE_PASSWORD);

		this.addAuthPoint("menu", "list_bibliographic", AuthorizationPointTypes.MENU_SEARCH);
		// TODO (SEO)
//		this.addAuthPoint("menu", "list_authorities", AuthorizationPointTypes.MENU_SEARCH);
//		this.addAuthPoint("menu", "list_vocabulary", AuthorizationPointTypes.MENU_SEARCH);
		
		this.addAuthPoint("menu", "search_bibliographic", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("menu", "search_authorities", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("menu", "search_vocabulary", AuthorizationPointTypes.MENU_SEARCH);
		this.addAuthPoint("menu", "search_z3950", AuthorizationPointTypes.MENU_SEARCH);

		this.addAuthPoint("menu", "cataloging_bibliographic", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_DELETE);
		this.addAuthPoint("menu", "cataloging_authorities", AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_DELETE);
		this.addAuthPoint("menu", "cataloging_vocabulary", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);
		this.addAuthPoint("menu", "cataloging_import", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("menu", "cataloging_labels", AuthorizationPointTypes.CATALOGING_PRINT_LABELS);
		
		this.addAuthPoint("menu", "circulation_user", AuthorizationPointTypes.CIRCULATION_LIST, AuthorizationPointTypes.CIRCULATION_SAVE, AuthorizationPointTypes.CIRCULATION_DELETE);
		this.addAuthPoint("menu", "circulation_lending", AuthorizationPointTypes.CIRCULATION_LENDING_LIST, AuthorizationPointTypes.CIRCULATION_LENDING_LEND, AuthorizationPointTypes.CIRCULATION_LENDING_RETURN);
		this.addAuthPoint("menu", "circulation_reservation", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST, AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		this.addAuthPoint("menu", "circulation_access", AuthorizationPointTypes.CIRCULATION_LIST);
		this.addAuthPoint("menu", "circulation_user_cards", AuthorizationPointTypes.CIRCULATION_PRINT_USER_CARDS);
		
		this.addAuthPoint("menu", "circulation_user_reservation", AuthorizationPointTypes.CIRCULATION_USER_RESERVATION);

		this.addAuthPoint("menu", "acquisition_order", AuthorizationPointTypes.ACQUISITION_ORDER_LIST, AuthorizationPointTypes.ACQUISITION_ORDER_SAVE, AuthorizationPointTypes.ACQUISITION_ORDER_DELETE);
		this.addAuthPoint("menu", "acquisition_quotation", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST, AuthorizationPointTypes.ACQUISITION_QUOTATION_SAVE, AuthorizationPointTypes.ACQUISITION_QUOTATION_DELETE);
		this.addAuthPoint("menu", "acquisition_request", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST, AuthorizationPointTypes.ACQUISITION_REQUEST_SAVE, AuthorizationPointTypes.ACQUISITION_REQUEST_DELETE);
		this.addAuthPoint("menu", "acquisition_supplier", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST, AuthorizationPointTypes.ACQUISITION_SUPPLIER_SAVE, AuthorizationPointTypes.ACQUISITION_SUPPLIER_DELETE);
		
		this.addAuthPoint("menu", "administration_password", AuthorizationPointTypes.LOGIN_CHANGE_PASSWORD);
		this.addAuthPoint("menu", "administration_maintenance", AuthorizationPointTypes.ADMINISTRATION_INDEXING);
		this.addAuthPoint("menu", "administration_user_types", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_LIST, AuthorizationPointTypes.ADMINISTRATION_USERTYPE_SAVE, AuthorizationPointTypes.ADMINISTRATION_USERTYPE_DELETE);
		this.addAuthPoint("menu", "administration_access_cards", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_LIST, AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_SAVE, AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_DELETE);
		this.addAuthPoint("menu", "administration_configurations", AuthorizationPointTypes.ADMINISTRATION_CONFIGURATIONS);
		this.addAuthPoint("menu", "administration_permissions", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("menu", "administration_z3950_servers", AuthorizationPointTypes.ADMINISTRATION_Z3950_SEARCH, AuthorizationPointTypes.ADMINISTRATION_Z3950_SAVE, AuthorizationPointTypes.ADMINISTRATION_Z3950_DELETE);
		this.addAuthPoint("menu", "administration_reports", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("menu", "administration_translations", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("menu", "administration_brief_customization", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("menu", "administration_form_customization", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);

		this.addAuthPoint("menu", "multi_schema_manage", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);
		this.addAuthPoint("menu", "multi_schema_configurations", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);
		this.addAuthPoint("menu", "multi_schema_translations", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);
		this.addAuthPoint("menu", "multi_schema_backup", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);
		
		this.addAuthPoint("menu", "help_about_biblivre", AuthorizationPointTypes.MENU_HELP);
		this.addAuthPoint("menu", "ping", AuthorizationPointTypes.MENU_OTHER);
		this.addAuthPoint("menu", "i18n", AuthorizationPointTypes.MENU_OTHER);
		this.addAuthPoint("menu", "test", AuthorizationPointTypes.MENU_OTHER);
		this.addAuthPoint("menu", "setup", AuthorizationPointTypes.ADMINISTRATION_RESTORE);

		this.addAuthPoint("cataloging.bibliographic", "search", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "paginate", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "open", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "item_count", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "autocomplete", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "convert", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "save", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "delete", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_DELETE);
		this.addAuthPoint("cataloging.bibliographic", "move_records", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_MOVE);
		this.addAuthPoint("cataloging.bibliographic", "export_records", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_MOVE);
		this.addAuthPoint("cataloging.bibliographic", "download_export", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.bibliographic", "add_attachment", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "remove_attachment", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.bibliographic", "list_brief_formats", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);

		this.addAuthPoint("cataloging.bibliographic", "private_database_access", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_PRIVATE_DATABASE_ACCESS);

		this.addAuthPoint("cataloging.holding", "list", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.holding", "open", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_LIST);
		this.addAuthPoint("cataloging.holding", "convert", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.holding", "save", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		this.addAuthPoint("cataloging.holding", "delete", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_DELETE);
		this.addAuthPoint("cataloging.holding", "create_automatic_holding", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE);
		
		this.addAuthPoint("cataloging", "import_upload", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging", "save_import", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging", "parse_marc", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging", "import_search", AuthorizationPointTypes.CATALOGING_BIBLIOGRAPHIC_SAVE, AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);

		this.addAuthPoint("cataloging.authorities", "search", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "paginate", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "open", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "item_count", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "autocomplete", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "convert", AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE);
		this.addAuthPoint("cataloging.authorities", "save", AuthorizationPointTypes.CATALOGING_AUTHORITIES_SAVE);
		this.addAuthPoint("cataloging.authorities", "delete", AuthorizationPointTypes.CATALOGING_AUTHORITIES_DELETE);
		this.addAuthPoint("cataloging.authorities", "move_records", AuthorizationPointTypes.CATALOGING_AUTHORITIES_MOVE);
		this.addAuthPoint("cataloging.authorities", "export_records", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "download_export", AuthorizationPointTypes.CATALOGING_AUTHORITIES_LIST);
		this.addAuthPoint("cataloging.authorities", "search_author", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("cataloging.authorities", "list_brief_formats", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);

		
		this.addAuthPoint("cataloging.vocabulary", "search", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "paginate", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "open", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "item_count", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "autocomplete", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "convert", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging.vocabulary", "save", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
		this.addAuthPoint("cataloging.vocabulary", "delete", AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);
		this.addAuthPoint("cataloging.vocabulary", "move_records", AuthorizationPointTypes.CATALOGING_VOCABULARY_MOVE);
		this.addAuthPoint("cataloging.vocabulary", "export_records", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "download_export", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
		this.addAuthPoint("cataloging.vocabulary", "list_brief_formats", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);

		
		this.addAuthPoint("cataloging.labels", "create_pdf", AuthorizationPointTypes.CATALOGING_PRINT_LABELS);
		this.addAuthPoint("cataloging.labels", "download_pdf", AuthorizationPointTypes.CATALOGING_PRINT_LABELS);

		this.addAuthPoint("circulation.user", "search", AuthorizationPointTypes.CIRCULATION_LIST);
		this.addAuthPoint("circulation.user", "paginate", AuthorizationPointTypes.CIRCULATION_LIST);
		this.addAuthPoint("circulation.user", "save", AuthorizationPointTypes.CIRCULATION_SAVE);
		this.addAuthPoint("circulation.user", "delete", AuthorizationPointTypes.CIRCULATION_DELETE);
		this.addAuthPoint("circulation.user", "load_tab_data", AuthorizationPointTypes.CIRCULATION_LENDING_LIST, AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.user", "block", AuthorizationPointTypes.CIRCULATION_SAVE);
		this.addAuthPoint("circulation.user", "unblock", AuthorizationPointTypes.CIRCULATION_SAVE);
		
		this.addAuthPoint("circulation.user_cards", "create_pdf", AuthorizationPointTypes.CIRCULATION_PRINT_USER_CARDS);
		this.addAuthPoint("circulation.user_cards", "download_pdf", AuthorizationPointTypes.CIRCULATION_PRINT_USER_CARDS);

		this.addAuthPoint("circulation.lending", "search", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("circulation.lending", "user_search", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("circulation.lending", "list", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
		this.addAuthPoint("circulation.lending", "lend", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("circulation.lending", "renew_lending", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
		this.addAuthPoint("circulation.lending", "return_lending", AuthorizationPointTypes.CIRCULATION_LENDING_RETURN);
		this.addAuthPoint("circulation.lending", "print_receipt", AuthorizationPointTypes.CIRCULATION_LENDING_LEND, AuthorizationPointTypes.CIRCULATION_LENDING_RETURN);
		this.addAuthPoint("circulation.lending", "pay_fine", AuthorizationPointTypes.CIRCULATION_SAVE);

		this.addAuthPoint("circulation.reservation", "search", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.reservation", "paginate", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.reservation", "user_search", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
		this.addAuthPoint("circulation.reservation", "reserve", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		this.addAuthPoint("circulation.reservation", "delete", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
		
		this.addAuthPoint("circulation.reservation", "self_open", AuthorizationPointTypes.CIRCULATION_USER_RESERVATION);
		this.addAuthPoint("circulation.reservation", "self_search", AuthorizationPointTypes.CIRCULATION_USER_RESERVATION);
		this.addAuthPoint("circulation.reservation", "self_reserve", AuthorizationPointTypes.CIRCULATION_USER_RESERVATION);
		this.addAuthPoint("circulation.reservation", "self_delete", AuthorizationPointTypes.CIRCULATION_USER_RESERVATION);
		
		this.addAuthPoint("circulation.accesscontrol", "card_search", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_LIST);
		this.addAuthPoint("circulation.accesscontrol", "user_search", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_LIST);
		this.addAuthPoint("circulation.accesscontrol", "bind", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_BIND);
		this.addAuthPoint("circulation.accesscontrol", "unbind", AuthorizationPointTypes.CIRCULATION_ACCESS_CONTROL_BIND);

		this.addAuthPoint("acquisition.supplier", "search", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
		this.addAuthPoint("acquisition.supplier", "paginate", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
		this.addAuthPoint("acquisition.supplier", "save", AuthorizationPointTypes.ACQUISITION_SUPPLIER_SAVE);
		this.addAuthPoint("acquisition.supplier", "delete", AuthorizationPointTypes.ACQUISITION_SUPPLIER_DELETE);
		
		this.addAuthPoint("acquisition.request", "search", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("acquisition.request", "paginate", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("acquisition.request", "open", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
		this.addAuthPoint("acquisition.request", "save", AuthorizationPointTypes.ACQUISITION_REQUEST_SAVE);
		this.addAuthPoint("acquisition.request", "delete", AuthorizationPointTypes.ACQUISITION_REQUEST_DELETE);

		this.addAuthPoint("acquisition.quotation", "search", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("acquisition.quotation", "list", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("acquisition.quotation", "paginate", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
		this.addAuthPoint("acquisition.quotation", "save", AuthorizationPointTypes.ACQUISITION_QUOTATION_SAVE);
		this.addAuthPoint("acquisition.quotation", "delete", AuthorizationPointTypes.ACQUISITION_QUOTATION_DELETE);

		this.addAuthPoint("acquisition.order", "search", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("acquisition.order", "paginate", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
		this.addAuthPoint("acquisition.order", "save", AuthorizationPointTypes.ACQUISITION_ORDER_SAVE);
		this.addAuthPoint("acquisition.order", "delete", AuthorizationPointTypes.ACQUISITION_ORDER_DELETE);
		
		this.addAuthPoint("administration.configurations", "save", AuthorizationPointTypes.ADMINISTRATION_CONFIGURATIONS);
		this.addAuthPoint("administration.configurations", "ignore_update", AuthorizationPointTypes.MENU_OTHER);		

		this.addAuthPoint("administration.indexing", "reindex", AuthorizationPointTypes.ADMINISTRATION_INDEXING);
		this.addAuthPoint("administration.indexing", "progress", AuthorizationPointTypes.ADMINISTRATION_INDEXING);

		this.addAuthPoint("administration.translations", "dump", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "download_dump", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "load", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "save", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "save_language_translations", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);
		this.addAuthPoint("administration.translations", "list", AuthorizationPointTypes.ADMINISTRATION_TRANSLATIONS);

		this.addAuthPoint("administration.backup", "list", AuthorizationPointTypes.ADMINISTRATION_BACKUP);
		this.addAuthPoint("administration.backup", "prepare", AuthorizationPointTypes.ADMINISTRATION_BACKUP);
		this.addAuthPoint("administration.backup", "backup", AuthorizationPointTypes.ADMINISTRATION_BACKUP);
		this.addAuthPoint("administration.backup", "download", AuthorizationPointTypes.ADMINISTRATION_BACKUP);		
		this.addAuthPoint("administration.backup", "progress", AuthorizationPointTypes.ADMINISTRATION_BACKUP);		

		this.addAuthPoint("administration.usertype", "search", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_LIST);
		this.addAuthPoint("administration.usertype", "paginate", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_LIST);
		this.addAuthPoint("administration.usertype", "save", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_SAVE);
		this.addAuthPoint("administration.usertype", "delete", AuthorizationPointTypes.ADMINISTRATION_USERTYPE_DELETE);
		
		this.addAuthPoint("administration.accesscards", "search", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_LIST);
		this.addAuthPoint("administration.accesscards", "paginate", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_LIST);
		this.addAuthPoint("administration.accesscards", "save", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_SAVE);
		this.addAuthPoint("administration.accesscards", "change_status", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_SAVE);
		this.addAuthPoint("administration.accesscards", "delete", AuthorizationPointTypes.ADMINISTRATION_ACCESSCARDS_DELETE);
		
		this.addAuthPoint("administration.permissions", "search", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("administration.permissions", "open", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("administration.permissions", "save", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		this.addAuthPoint("administration.permissions", "delete", AuthorizationPointTypes.ADMINISTRATION_PERMISSIONS);
		
		this.addAuthPoint("administration.z3950", "search", AuthorizationPointTypes.ADMINISTRATION_Z3950_SEARCH);
		this.addAuthPoint("administration.z3950", "paginate", AuthorizationPointTypes.ADMINISTRATION_Z3950_SEARCH);
		this.addAuthPoint("administration.z3950", "save", AuthorizationPointTypes.ADMINISTRATION_Z3950_SAVE);
		this.addAuthPoint("administration.z3950", "delete", AuthorizationPointTypes.ADMINISTRATION_Z3950_DELETE);
		
		this.addAuthPoint("administration.reports", "user_search", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("administration.reports", "author_search", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("administration.reports", "generate", AuthorizationPointTypes.ADMINISTRATION_REPORTS);
		this.addAuthPoint("administration.reports", "download_report", AuthorizationPointTypes.ADMINISTRATION_REPORTS);

		this.addAuthPoint("multi_schema", "create", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);
		this.addAuthPoint("multi_schema", "toggle", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);
		this.addAuthPoint("multi_schema", "delete_schema", AuthorizationPointTypes.ADMINISTRATION_MULTI_SCHEMA);

		this.addAuthPoint("administration.setup", "clean_install", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.setup", "list_restores", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.setup", "upload_biblivre4", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.setup", "upload_biblivre3", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.setup", "restore", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		this.addAuthPoint("administration.setup", "import_biblivre3", AuthorizationPointTypes.ADMINISTRATION_RESTORE);
		
		this.addAuthPoint("administration.customization", "save_brief_formats", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);		
		this.addAuthPoint("administration.customization", "insert_brief_format", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);		
		this.addAuthPoint("administration.customization", "delete_brief_format", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);		
		
		this.addAuthPoint("administration.customization", "save_form_datafields", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);		
		this.addAuthPoint("administration.customization", "insert_form_datafield", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);		
		this.addAuthPoint("administration.customization", "delete_form_datafield", AuthorizationPointTypes.ADMINISTRATION_CUSTOMIZATION);
		
		this.addAuthPoint("digitalmedia", "upload", AuthorizationPointTypes.DIGITALMEDIA_UPLOAD);
		this.addAuthPoint("digitalmedia", "download", AuthorizationPointTypes.DIGITALMEDIA_DOWNLOAD);

		this.addAuthPoint("z3950", "search", AuthorizationPointTypes.Z3950_SEARCH);
		this.addAuthPoint("z3950", "paginate", AuthorizationPointTypes.Z3950_SEARCH);
		this.addAuthPoint("z3950", "open", AuthorizationPointTypes.Z3950_SEARCH);
	}

	private void addAuthPoint(String module, String action, AuthorizationPointTypes ... types) {
		Pair<String, String> pair = new Pair<String, String>(module, action);

		boolean matchesThisSchema = false;

		for (AuthorizationPointTypes type : types) {
			if (type.getSchemaScope() == AuthorizationSchemaScope.ANY) {
				matchesThisSchema = true;
				break;
			}
			
			if (this.schema.equals(Constants.GLOBAL_SCHEMA) && type.getSchemaScope() == AuthorizationSchemaScope.GLOBAL_SCHEMA) {
				matchesThisSchema = true;
				break;
			}
	
			if (!this.schema.equals(Constants.GLOBAL_SCHEMA) && type.getSchemaScope() == AuthorizationSchemaScope.SINGLE_SCHEMA) {
				matchesThisSchema = true;
				break;
			}
		}
		
		if (!matchesThisSchema) {
			return;
		}
		
		boolean matchesUserAffiliation = false;
		
		for (AuthorizationPointTypes type : types) {
			if (type.getUserScope() == AuthorizationUserScope.ANY) {
				matchesUserAffiliation = true;
				break;
			}
			
			if (this.employee && type.getUserScope() == AuthorizationUserScope.EMPLOYEE) {
				matchesUserAffiliation = true;
				break;
			}
			if (!this.employee && type.getUserScope() == AuthorizationUserScope.READER) {
				matchesUserAffiliation = true;
				break;
			}
		}
		
		if (!matchesUserAffiliation) {
			return;
		}
		
		boolean allowed = false;

		for (AuthorizationPointTypes type : types) {
			allowed = type.isPublic() || (type.isPublicForLoggedUsers() && this.isLogged()) || this.permissions.containsKey(type.name());

			if (allowed) {
				break;
			}
		}

		this.points.put(pair, allowed);
	}

	public boolean isAllowed(String module, String action) {
		Pair<String, String> pair = new Pair<String, String>(module, action);
		Boolean allowed = this.points.get(pair);

		if (allowed == null) {
			Logger.getLogger(this.getClass()).error("Action not found: " + pair);
			return false;
		}

		return (this.admin || allowed);
	}

	public boolean isAllowed(AuthorizationPointTypes type) {
		return this.admin || this.permissions.containsKey(type.name());
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public boolean isLogged() {
		return this.logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public boolean isEmployee() {
		return this.employee;
	}

	public void setEmployee(boolean employee) {
		this.employee = employee;
	}

	public String getSchema() {
		return this.schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
}
