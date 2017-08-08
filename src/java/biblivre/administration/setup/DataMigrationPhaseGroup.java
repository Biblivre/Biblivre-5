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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum DataMigrationPhaseGroup {
	
	DIGITAL_MEDIA(DataMigrationPhase.DIGITAL_MEDIA),
	CATALOGING_BIBLIOGRAPHIC(DataMigrationPhase.CATALOGING_BIBLIOGRAPHIC, DataMigrationPhase.CATALOGING_HOLDINGS),
	CATALOGING_AUTHORITIES(DataMigrationPhase.CATALOGING_AUTHORITIES),
	CATALOGING_VOCABULARY(DataMigrationPhase.CATALOGING_VOCABULARY),
	USERS(DataMigrationPhase.USER_TYPES, DataMigrationPhase.LOGINS, DataMigrationPhase.USERS),
	ACQUISITION(DataMigrationPhase.ACQUISITION_SUPPLIER, DataMigrationPhase.ACQUISITION_REQUISITION, DataMigrationPhase.ACQUISITION_QUOTATION, DataMigrationPhase.ACQUISITION_ITEM_QUOTATION, DataMigrationPhase.ACQUISITION_ORDER),
	Z3950_SERVERS(DataMigrationPhase.Z3950_SERVERS),
	ACCESS_CONTROL(DataMigrationPhase.ACCESS_CARDS, DataMigrationPhase.ACCESS_CONTROL, DataMigrationPhase.ACCESS_CONTROL_HISTORY),
	LENDINGS(DataMigrationPhase.LENDINGS, DataMigrationPhase.LENDINGS_HISTORY, DataMigrationPhase.LENDING_FINE), 
	RESERVATIONS(DataMigrationPhase.RESERVATIONS);
	
	private List<DataMigrationPhase> phases;
	
	private DataMigrationPhaseGroup(DataMigrationPhase... phases) {
		this.phases = Arrays.asList(phases);
	}
	
	
	public static DataMigrationPhaseGroup fromString(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}

		str = str.toLowerCase();

		for (DataMigrationPhaseGroup group : DataMigrationPhaseGroup.values()) {
			if (str.equals(group.name().toLowerCase())) {
				return group;
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
	
	public List<DataMigrationPhase> getPhases() {
		return this.phases;
	}

}
