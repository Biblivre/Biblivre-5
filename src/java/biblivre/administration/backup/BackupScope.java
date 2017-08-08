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
package biblivre.administration.backup;

import org.apache.commons.lang3.StringUtils;

public enum BackupScope {
	SINGLE_SCHEMA, // general.multi_schema is false
	MULTI_SCHEMA, // general.multi_schema is true and backup was made from public schema
	SINGLE_SCHEMA_FROM_MULTI_SCHEMA; // general.multi_schema is true but backup was made from single schema

	public static BackupScope fromString(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		
		str = str.toLowerCase();

		for (BackupScope scope : BackupScope.values()) {
			if (str.equals(scope.toString())) {
				return scope;
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
}
