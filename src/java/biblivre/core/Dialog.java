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
package biblivre.core;

import biblivre.core.enums.ActionResult;

public class Dialog {
    public static void showNormal(ExtendedRequest request, String textKey) {
    	Dialog.show(request, textKey, ActionResult.NORMAL);
    }

    public static void showWarning(ExtendedRequest request, String textKey) {
    	Dialog.show(request, textKey, ActionResult.WARNING);
    }

    public static void showError(ExtendedRequest request, String textKey) {
    	Dialog.show(request, textKey, ActionResult.ERROR);
    }
    
    public static void show(ExtendedRequest request, String textKey, ActionResult level) {
        request.setAttribute("message", textKey);
        request.setAttribute("message_level", level.name().toLowerCase());
    }
}
