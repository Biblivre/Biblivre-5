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
package biblivre.view;

import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.translations.TranslationsMap;

public class LayoutUtils {

	private TranslationsMap translationsMap;

	public LayoutUtils(TranslationsMap translationsMap) {
		this.translationsMap = translationsMap;
	}

	public String menuItem(AuthorizationPoints atps, String module, String action) {
		boolean allowed = (atps == null || atps.isAllowed("menu", action));

		if (allowed) {
			return String.format("<li class=\"submenu_%1$s\" data-action=\"%2$s\"><a href=\"?action=%2$s\">%3$s</a></li>", module, action,
					this.translationsMap.getText("menu." + action));
		} else {
			return String.format("<li class=\"disabled\" data-action=\"%s\">%s</li>", action, this.translationsMap.getText("menu." + action));
		}
	}

	public String menuLevel(AuthorizationPoints atps, String module, String... actions) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("<li class=\"menu_%1$s\" data-module=\"%1$s\">", module));
		sb.append(this.translationsMap.getText("menu." + module));

		sb.append("<ul class=\"submenu\">");
		for (String action : actions) {
			sb.append(this.menuItem(atps, module, action));
		}
		sb.append("</ul>");

		sb.append("</li>");

		return sb.toString();
	}

	public String menuHelp(AuthorizationPoints atps) {
		StringBuilder sb = new StringBuilder();

		sb.append("<li class=\"menu_help\" data-module=\"help\">");
		sb.append(this.translationsMap.getText("menu.help"));

		sb.append("<ul class=\"submenu\">");
		sb.append(this.menuItem(atps, "help", "help_about_biblivre"));
		sb.append("<li class=\"submenu_help\"><a href=\"http://www.biblivre.org.br/forum/viewforum.php?f=30\" target=\"_blank\">")
				.append(this.translationsMap.getText("menu.help_faq")).append("</a></li>");
		sb.append("<li class=\"submenu_help\"><a href=\"static/Manual_Biblivre_5.0.0.pdf\" target=\"_blank\">").append(this.translationsMap.getText("menu.help_manual"))
				.append("</a></li>");

		sb.append("</ul>");
		sb.append("</li>");
		sb.append("<li>&#160;</li>");

		return sb.toString();
	}

	public String menuLogout() {
		StringBuilder sb = new StringBuilder();

		sb.append("<li data-module=\"logout\" class=\"logout\">");
		sb.append("<button onclick=\"Core.submitForm('login', 'logout', 'jsp');\" type=\"button\">").append(this.translationsMap.getText("label.logout")).append("</button>");
		sb.append("</li>");

		return sb.toString();
	}

	public String menuLogin() {
		StringBuilder sb = new StringBuilder();

		sb.append("<li data-module=\"login\" class=\"login\">");
		sb.append("<button onclick=\"Core.submitForm('login', 'login', 'jsp');\">").append(this.translationsMap.getText("label.login")).append("</button>");
		sb.append("</li>");

		sb.append("<li class=\"inputs\">&#160;");
		sb.append(String.format("<input type=\"text\" name=\"username\" placeholder=\"%s\">", this.translationsMap.getText("label.username")));
		sb.append(String.format("<input type=\"password\" name=\"password\" placeholder=\"%s\">", this.translationsMap.getText("label.password")));
		sb.append("</li>");

		return sb.toString();
	}
}
