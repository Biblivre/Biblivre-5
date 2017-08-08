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
package biblivre.view.taglibs;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;

public class TranslationsDate extends SimpleTagSupport {

	private Date date = new Date();
	private Boolean time = false;

	@Override
	public void doTag() throws JspException {
		final JspWriter out = getJspContext().getOut();

		try {
			ServletRequest request = ((PageContext) getJspContext()).getRequest();
			TranslationsMap map = (TranslationsMap) request.getAttribute("translationsMap");

			String pattern;
			
			if (this.time) {
				pattern = map.getText(Constants.TRANSLATION_FORMAT_DATETIME);
			} else {
				pattern = map.getText(Constants.TRANSLATION_FORMAT_DATE);
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			
			out.print(formatter.format(this.date));
		} catch (Exception e)  {
			throw new JspException(e.getMessage());
		}
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTime(Boolean time) {
		this.time = time;
	}
}
