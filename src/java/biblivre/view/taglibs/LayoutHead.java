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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;

import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;

public class LayoutHead extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	private String schema;
	private TranslationsMap translationsMap;
	
	private String getCurrentURI() {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		return request.getRequestURI();
	}
	
	public boolean isSchemaSelection() {
		return this.getSchema().equals(Constants.GLOBAL_SCHEMA);
	}
	
	private String getSchema() {
		return StringUtils.defaultString(this.schema);
	}

	private TranslationsMap getTranslationsMap() {
		return this.translationsMap;
	}

	private void init() {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		this.schema = (String) request.getAttribute("schema");
		this.translationsMap = (TranslationsMap) request.getAttribute("translationsMap");
	}
	
	@Override
	public int doStartTag() throws JspException {
		this.init();	
		
		JspWriter out = this.pageContext.getOut();
		String schema = this.getSchema();
		
		String message = (String) this.pageContext.getRequest().getAttribute("message");
		String messageLevel = (String) this.pageContext.getRequest().getAttribute("message_level");
		
		try {
			//out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			//out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			out.println("<!doctype html>");
			out.println("<html class=\"noscript\">");
			out.println("<!-- " + this.getCurrentURI() + " -->");
			out.println("<head>");
			out.println("	<meta charset=\"utf-8\">");
			out.println("	<meta name=\"google\" content=\"notranslate\" />");
			out.println("	<title>" + Configurations.getString(schema, Constants.CONFIG_TITLE) + "</title>");

			out.println("	<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"static/images/favicon.ico\" />");
			out.println("	<link rel=\"stylesheet\" type=\"text/css\" href=\"static/styles/biblivre.core.css\" />");
			out.println("	<link rel=\"stylesheet\" type=\"text/css\" href=\"static/styles/biblivre.print.css\" />");
			out.println("	<link rel=\"stylesheet\" type=\"text/css\" href=\"static/styles/jquery-ui.css\" />");
			out.println("	<link rel=\"stylesheet\" type=\"text/css\" href=\"static/styles/font-awesome.min.css\" />");

			out.println("	<script type=\"text/javascript\" src=\"static/scripts/json.js\"></script>");
			out.println("	<script type=\"text/javascript\" src=\"static/scripts/jquery.js\"></script>");
			out.println("	<script type=\"text/javascript\" src=\"static/scripts/jquery-ui.js\"></script>");
			out.println("	<script type=\"text/javascript\" src=\"static/scripts/jquery.extras.js\"></script>");
			out.println("	<script type=\"text/javascript\" src=\"static/scripts/lodash.js\"></script>");

			out.println("	<script type=\"text/javascript\" src=\"static/scripts/globalize.js\"></script>");
			out.println("	<script type=\"text/javascript\" src=\"static/scripts/cultures/globalize.culture." + this.getTranslationsMap().getText("language_code") + ".js\"></script>");
			out.println("	<script type=\"text/javascript\" >Globalize.culture('" + this.getTranslationsMap().getText("language_code") + "'); </script>");
			out.println("	<script type=\"text/javascript\" >Globalize.culture().numberFormat.currency.symbol = '" + Configurations.getString(schema, Constants.CONFIG_CURRENCY) + "'; </script>");

			out.println("	<script type=\"text/javascript\" src=\"static/scripts/biblivre.core.js\"></script>");
			out.println("	<script type=\"text/javascript\" src=\"static/scripts/" + this.getTranslationsMap().getCacheFileName() + "\"></script>");
			
			//TODO Check layout on IE6
			out.println("	<!--[if lte IE 6]><script src=\"static/scripts/ie6/warning.js\"></script><script>window.onload = function(){ e('static/scripts/ie6/') }</script><![endif]-->");
			//out.println("	<!--[if lte IE 9]><script src=\"static/scripts/IE9.js\"></script><![endif]-->");
			
//			out.println("<link rel=\"stylesheet/less\" href=\"extra/less/biblivre.less\"><script src=\"extra/js/vendor/modernizr.js\"></script><script>var less = {env: \"development\",	poll: 1000};</script><script src=\"extra/js/vendor/less.js\"></script>");


			ExtendedResponse response = (ExtendedResponse) this.pageContext.getResponse();
			boolean translateError = false;
			if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
				message = "error.file_not_found";
				messageLevel = "error";
				translateError = true;
			}
			
			if (StringUtils.isNotBlank(message)) {
				out.println("<script type=\"text/javascript\">");
				out.println("	$(document).ready(function() {");
				out.println("		Core.msg({");
				out.println("			message: '" + message + "',");
				out.println("			message_level: '" + messageLevel + "',");
				out.println("			animate: true,");
				
				if (translateError) {
					out.println("			translate: true,");
				}

				out.println("			sticky: true");
				out.println("		});");
				out.println("	});");
				out.println("</script>");
			}
		} catch (Exception e) {
		}

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {
		JspWriter out = this.pageContext.getOut();

		try {
			out.println("</head>");
		} catch (Exception e) {
		}

		return Tag.EVAL_PAGE;
	}

	@Override
	public int doAfterBody() throws JspException {
		try {
			// This code is generated for tags whose bodyContent is "JSP"
			BodyContent bodyCont = getBodyContent();
			JspWriter out = bodyCont.getEnclosingWriter();
			
			this.writeTagBodyContent(out, bodyCont);
		} catch (Exception ex) {
			this.handleBodyContentException(ex);
		}

		return Tag.EVAL_PAGE;
	}
	
	private void writeTagBodyContent(JspWriter out, BodyContent bodyContent) throws IOException {
		bodyContent.writeOut(out);
		bodyContent.clearBody();
	}

	private void handleBodyContentException(Exception ex) throws JspException {
		// Since the doAfterBody method is guarded, place exception handing code here.
		throw new JspException("Error in Head tag", ex);
	}
}
