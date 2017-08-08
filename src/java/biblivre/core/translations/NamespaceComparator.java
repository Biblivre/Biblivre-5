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
package biblivre.core.translations;

import java.util.Comparator;

public class NamespaceComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		if (o1 == null) {
			return 1;
		}

		if (o2 == null) {
			return -1;
		}

		String[] names1 = o1.split("\\.");
		String[] names2 = o2.split("\\.");

		if (names1.length == names2.length) {
			return o1.compareTo(o2);
		}

		for (int i = 0; i < names1.length; i++) {
			if (i > names2.length - 1) {
				return 1;
			}

			int compares = names1[i].compareTo(names2[i]);
			if (compares != 0) {
				if (names1.length - 1 == i) {
					return -1;
				}

				if (names2.length - 1 == i) {
					return 1;
				}

				return compares;
			}
		}

		return -1;
	}

}
