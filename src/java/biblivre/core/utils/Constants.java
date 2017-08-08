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
package biblivre.core.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {

	public static final String BIBLIVRE = "Biblivre";
	public static final String BIBLIVRE_VERSION = "5.0.5";
	public static final String UPDATE_URL = "http://update.biblivre.org.br";
	public static final String DOWNLOAD_URL = "http://update.biblivre.org.br";

	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final DateFormat DEFAULT_DATE_FORMAT_TIMEZONE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public static final String LINE_BREAK = System.getProperty("line.separator");
	public static final float MM_UNIT = 72.0f / 25.4f;

	// Configurations
	public static final String CONFIG_DEFAULT_LANGUAGE = "general.default_language";
	public static final String CONFIG_MULTI_SCHEMA = "general.multi_schema";
	public static final String CONFIG_TITLE = "general.title";
	public static final String CONFIG_SUBTITLE = "general.subtitle";
	public static final String CONFIG_UID = "general.uid";
	public static final String CONFIG_BUSINESS_DAYS = "general.business_days";
	public static final String CONFIG_CURRENCY = "general.currency";

	public static final String CONFIG_NEW_LIBRARY = "setup.new_library";

	public static final String CONFIG_ACCESSION_NUMBER_PREFIX = "cataloging.accession_number_prefix";

	public static final String CONFIG_PGDUMP_PATH = "general.pg_dump_path";
	public static final String CONFIG_PSQL_PATH = "general.psql_path";
	public static final String CONFIG_BACKUP_PATH = "general.backup_path";

	public static final String CONFIG_SEARCH_RESULTS_PER_PAGE = "search.results_per_page";
	public static final String CONFIG_SEARCH_RESULT_LIMIT = "search.result_limit";

	public static final String CONFIG_Z3950_RESULT_LIMIT = "search.distributed_search_limit";
	public static final String CONFIG_Z3950_SERVER_ACTIVE = "administration.z3950.server.active";

	public static final String CONFIG_LENDING_PRINTER_TYPE = "circulation.lending_receipt.printer.type";

	// Translations
	public static final String TRANSLATION_RECORD_TAB_FIELD_LABEL = "cataloging.tab.record.custom.field_label.";
	public static final String TRANSLATION_INDEXING_GROUP = "cataloging.custom.indexing_group.";
	public static final String TRANSLATION_USER_FIELD = "circulation.custom.user_field.";
	public static final String TRANSLATION_FORMAT_DATE = "format.date";
	public static final String TRANSLATION_FORMAT_DATETIME = "format.datetime";

	// Media server
	public static final int DEFAULT_BUFFER_SIZE = 10240; // 10 KB
	public static final long DEFAULT_EXPIRE_TIME = 9676800000L; // 16 weeks
	public static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

	public static final String GLOBAL_SCHEMA = "global";

	// The constants below should not be final
	public static String SINGLE_SCHEMA = "single";
	public static boolean REINDEXING = false;
}
