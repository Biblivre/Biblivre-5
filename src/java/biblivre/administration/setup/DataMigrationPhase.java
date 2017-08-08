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
package biblivre.administration.setup;

import org.apache.commons.lang3.StringUtils;

public enum DataMigrationPhase {
	
	DIGITAL_MEDIA("digital_media", "digital_media", "digital_media_id_seq", "id"), 
	CATALOGING_BIBLIOGRAPHIC("cataloging_biblio", "biblio_records", "biblio_records_id_seq", "id"),
	CATALOGING_HOLDINGS("cataloging_holdings", "biblio_holdings", "biblio_holdings_id_seq", "id"),
	CATALOGING_AUTHORITIES("cataloging_authorities", "authorities_records", "authorities_records_id_seq", "id"),
	CATALOGING_VOCABULARY("cataloging_vocabulary", "vocabulary_records", "vocabulary_records_id_seq", "id"),
	ACCESS_CARDS("cards", "access_cards", "access_cards_id_seq", "id"),
	LOGINS("logins", "logins", "logins_id_seq", "id"),
	USER_TYPES("users_type", "users_types", "users_types_id_seq", "id"),
	USERS("users", "users", "users_id_seq", "id"),
	ACQUISITION_SUPPLIER("acquisition_supplier", "suppliers", "supplier_id_seq", "id"), 
	ACQUISITION_REQUISITION("acquisition_requisition", "requests", "request_id_seq", "id"), 
	ACQUISITION_QUOTATION("acquisition_quotation", "quotations", "quotations_id_seq", "id"), 
	ACQUISITION_ITEM_QUOTATION("acquisition_item_quotation", "request_quotation", "quotations_id_seq", null), 
	ACQUISITION_ORDER("acquisition_order", "orders", "orders_id_seq", "id"), 
	Z3950_SERVERS("z3950_server", "z3950_addresses", "z3950_addresses_id_seq", "id"),
	ACCESS_CONTROL("access_control", "access_control", "access_control_id_seq", "id"),
	ACCESS_CONTROL_HISTORY("access_control", "access_control", "access_control_id_seq", "id"),
	LENDINGS("lending", "lendings", "lendings_id_seq", "id"), 
	LENDINGS_HISTORY("lending_history", "lendings", "lendings_id_seq", "id"), 
	LENDING_FINE("lending_fine", "lending_fines", "lending_fines_id_seq", "id"), 
	RESERVATIONS("reservation", "reservations", "reservations_id_seq", "id");
	
	private String biblivre3TableName;
	private String biblivre4TableName;
	private String biblivre4SequenceName;
	private String biblivre4IdColumnName;
	
	private DataMigrationPhase(String biblivre3TableName, String biblivre4TableName, String biblivre4SequenceName, String biblivre4IdColumnName) {
		this.biblivre3TableName = biblivre3TableName;
		this.biblivre4TableName = biblivre4TableName;
		this.biblivre4SequenceName = biblivre4SequenceName;
		this.biblivre4IdColumnName = biblivre4IdColumnName;
	}

	public static DataMigrationPhase fromString(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}

		str = str.toLowerCase();

		for (DataMigrationPhase phase : DataMigrationPhase.values()) {
			if (str.equals(phase.name().toLowerCase())) {
				return phase;
			}
		}

		return null;
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	public String getString() {
		return this.toString();
	}

	public String getBiblivre3TableName() {
		return this.biblivre3TableName;
	}

	public String getBiblivre4TableName() {
		return this.biblivre4TableName;
	}

	public String getBiblivre4SequenceName() {
		return this.biblivre4SequenceName;
	}
	
	public String getBiblivre4IdColumnName() {
		return this.biblivre4IdColumnName;
	}
	
}
