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
package biblivre.marc;

import java.util.regex.Pattern;

public final class MarcConstants {
	public static final String DEFAULT_SPLITTER = "|";
	public static final Pattern DATAFIELD_FORMAT_PATTERN = Pattern.compile("([\\(\\)])|(.)\\{(.*?)\\}");

	public static final String ISBN = "020";
	public static final String ISSN = "022";
	public static final String ISRC = "024";

    public static final String AUTHOR_PERSONAL_NAME = "100";
    public static final String AUTHOR_CORPORATION_NAME = "110";
    public static final String AUTHOR_CONGRESS_NAME = "111";
    public static final String AUTHOR_OTHER_PERSONAL_NAMES = "400";
    public static final String AUTHOR_OTHER_CORPORATION_NAMES = "410";
    public static final String AUTHOR_OTHER_CONGRESS_NAMES = "411";
    public static final String SECONDARY_AUTHOR_PERSONAL_NAME = "700";
    public static final String SECONDARY_AUTHOR_CORPORATION_NAME = "710";
    public static final String SECONDARY_AUTHOR_CONGRESS_NAME = "711";
	
	public static final String PUBLICATION = "260";
	public static final String EDITION = "250"; 
	public static final String PUBLICATION_FUNCTIONS = "264";

    public static final String TITLE = "245";
    public static final String UNIFORM_TITLE = "240";
    public static final String COLLECTIVE_UNIFORM_TITLE = "243";
    public static final String ADDED_UNIFORM_TITLE = "730";
    public static final String ADDED_ANALYTICAL_TITLE = "740";
    public static final String SECONDARY_INPUT_SERIAL_TITLE = "830";
	
	public static final String SHELF_LOCATION = "090";
    public static final String DDCN = "082";
    public static final String CNPQ = "095";
    public static final String PATENT = "013";
	
	public static final String NETWORK_LOCATION = "852";
	public static final String ELECTRONIC_LOCATION = "856";

	public static final String SUBJECT_ADDED_ENTRY_PERSONAL_NAME = "600";
	public static final String SUBJECT_ADDED_ENTRY_CORPORATE_NAME = "610";
	public static final String SUBJECT_ADDED_ENTRY_MEETING_NAME = "611";
	public static final String SUBJECT_ADDED_ENTRY_UNIFORM_TITLE = "630";
	public static final String SUBJECT_ADDED_ENTRY_TOPICAL_TERM = "650";
	public static final String SUBJECT_ADDED_ENTRY_GEOGRAPHIC_NAME = "651";
	public static final String SUBJECT_ADDED_ENTRY_LOCAL = "699";
		
	public static final String ACCESSION_NUMBER = "949";
	
	public static final String SOURCE_ACQUISITION_NOTES= "541";
}
