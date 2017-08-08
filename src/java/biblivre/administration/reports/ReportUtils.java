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

import org.apache.commons.lang3.StringUtils;

public final class ReportUtils {

    public static String formatDeweyString(final String dewey, final int digits) {
        if (StringUtils.isBlank(dewey)) {
            return "";
        }

        if (digits == -1) {
            return dewey;
        }
        
        int i = digits;
        
        StringBuilder format = new StringBuilder();
        for (char c : dewey.toCharArray()) {
            if (i == 0) {
                break;
            }

            if (Character.isDigit(c)) {
                i--;
            }

            format.append(c);
        }
        
        if (digits < 3) {
            while (format.length() < 3) {
                format.append("0");
            }
        }
        
        return format.toString();
    }

}
