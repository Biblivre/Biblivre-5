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

public enum AuthorizationPointTypes {
	
	LOGIN(AuthorizationSchemaScope.ANY, AuthorizationUserScope.ANY, AuthorizationPointGroups.LOGIN, true),
	LOGIN_CHANGE_PASSWORD(AuthorizationSchemaScope.ANY, AuthorizationUserScope.ANY, AuthorizationPointGroups.LOGIN, false, true),

	MENU_SEARCH(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.ANY, AuthorizationPointGroups.MENU, true),
	MENU_HELP(AuthorizationSchemaScope.ANY, AuthorizationUserScope.ANY, AuthorizationPointGroups.MENU, true),
	MENU_OTHER(AuthorizationSchemaScope.ANY, AuthorizationUserScope.ANY, AuthorizationPointGroups.MENU, true),

	CATALOGING_BIBLIOGRAPHIC_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.ANY, AuthorizationPointGroups.CATALOGING, true),
	CATALOGING_BIBLIOGRAPHIC_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_BIBLIOGRAPHIC_MOVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_BIBLIOGRAPHIC_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_BIBLIOGRAPHIC_PRIVATE_DATABASE_ACCESS(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),

	CATALOGING_AUTHORITIES_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.ANY, AuthorizationPointGroups.CATALOGING, true),
	CATALOGING_AUTHORITIES_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_AUTHORITIES_MOVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_AUTHORITIES_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),

	CATALOGING_VOCABULARY_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.ANY, AuthorizationPointGroups.CATALOGING, true),
	CATALOGING_VOCABULARY_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_VOCABULARY_MOVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),
	CATALOGING_VOCABULARY_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),

	CATALOGING_PRINT_LABELS(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CATALOGING),

	CIRCULATION_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_LENDING_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_LENDING_LEND(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_LENDING_RETURN(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_RESERVATION_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_RESERVATION_RESERVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_ACCESS_CONTROL_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_ACCESS_CONTROL_BIND(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),
	CIRCULATION_PRINT_USER_CARDS(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.CIRCULATION),

	CIRCULATION_USER_RESERVATION(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.READER, AuthorizationPointGroups.CIRCULATION),
	
	ACQUISITION_SUPPLIER_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_SUPPLIER_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_SUPPLIER_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_REQUEST_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_REQUEST_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_REQUEST_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_QUOTATION_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_QUOTATION_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_QUOTATION_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_ORDER_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_ORDER_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),
	ACQUISITION_ORDER_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ACQUISITION),

	ADMINISTRATION_BACKUP(AuthorizationSchemaScope.ANY, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_RESTORE(AuthorizationSchemaScope.ANY, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_CONFIGURATIONS(AuthorizationSchemaScope.ANY, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_TRANSLATIONS(AuthorizationSchemaScope.ANY, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_CUSTOMIZATION(AuthorizationSchemaScope.ANY, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),

	ADMINISTRATION_PERMISSIONS(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_INDEXING(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_USERTYPE_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_USERTYPE_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_USERTYPE_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_Z3950_SEARCH(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_Z3950_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_Z3950_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_REPORTS(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_ACCESSCARDS_LIST(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_ACCESSCARDS_SAVE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	ADMINISTRATION_ACCESSCARDS_DELETE(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),
	
	ADMINISTRATION_MULTI_SCHEMA(AuthorizationSchemaScope.GLOBAL_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.ADMIN),

	DIGITALMEDIA_UPLOAD(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.EMPLOYEE, AuthorizationPointGroups.DIGITALMEDIA),
	DIGITALMEDIA_DOWNLOAD(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.ANY, AuthorizationPointGroups.DIGITALMEDIA, true),

	Z3950_SEARCH(AuthorizationSchemaScope.SINGLE_SCHEMA, AuthorizationUserScope.ANY, AuthorizationPointGroups.SEARCH, true);

	private AuthorizationPointGroups group;
	private AuthorizationSchemaScope schemaScope;
	private AuthorizationUserScope userScope;
	private boolean _public;
	private boolean _publicForLoggedUsers;

	private AuthorizationPointTypes(AuthorizationSchemaScope schemaScope, AuthorizationUserScope userScope, AuthorizationPointGroups group) {
		this(schemaScope, userScope, group, false, false);
	}

	private AuthorizationPointTypes(AuthorizationSchemaScope schemaScope, AuthorizationUserScope userScope, AuthorizationPointGroups group, boolean _public) {
		this(schemaScope, userScope, group, _public, false);
	}

	private AuthorizationPointTypes(AuthorizationSchemaScope schemaScope, AuthorizationUserScope userScope, AuthorizationPointGroups group, boolean _public, boolean _publicForLoggedUsers) {
		this.schemaScope = schemaScope;
		this.userScope = userScope;
		this.group = group;
		this._public = _public;
		this._publicForLoggedUsers = _publicForLoggedUsers;
	}
	
	public AuthorizationPointGroups getGroup() {
		return this.group;
	}

	public boolean isPublic() {
		return this._public;
	}

	public boolean isPublicForLoggedUsers() {
		return this._publicForLoggedUsers;
	}

	public AuthorizationSchemaScope getSchemaScope() {
		return this.schemaScope;
	}

	public AuthorizationUserScope getUserScope() {
		return this.userScope;
	}
}
