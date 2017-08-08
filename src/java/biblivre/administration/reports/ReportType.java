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

public enum ReportType {
	
	ACQUISITION("1", "rp01_", true),
	SUMMARY("2", "rp02_", false),
	DEWEY("3", "rp03_", false),
	HOLDING_CREATION_BY_DATE("4", "rp04_", true),
	AUTHOR_BIBLIOGRAPHY("5", "rp05_", false),
	USER("6", "rp06_", false),
	ALL_USERS("7", "rp07_", false),
	LATE_LENDINGS("8", "rp08_", false),
	SEARCHES_BY_DATE("9", "rp09_", true),
	LENDINGS_BY_DATE("10", "rp10_", true),
	RESERVATION("12", "rp12_", false),
	ASSET_HOLDING("13", "rp13_", false),
	ASSET_HOLDING_FULL("14", "rp14_", false),
	TOPOGRAPHIC_FULL("15", "rp15_", false),
	ASSET_HOLDING_BY_DATE("16", "rp16_", true),
	CUSTOM_COUNT("17", "rp17_", false);
	
	private String id;
	private String name;
	private boolean timePeriod;

	private ReportType(String id, String name, boolean timePeriod) {
		this.id = id;
		this.name = name;
		this.timePeriod = timePeriod;
	}
	
	public final String getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public boolean isTimePeriod() {
		return this.timePeriod;
	}
	
	public static ReportType getById(final String id) {
		for (ReportType type : values()) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}
	
}

