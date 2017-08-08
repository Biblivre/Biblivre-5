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
package biblivre.core.controllers;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;

/**
 * Servlet Filter implementation class StatusExposingServletResponse
 */
public class ExtendedRequestResponseFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ExtendedRequest xRequest = null;
		ExtendedResponse xResponse = null;
		
		if (request instanceof ExtendedRequest) {
			// Avoid rewrapping if forwarding
			xRequest = (ExtendedRequest) request;
		} else {
			xRequest = new ExtendedRequest((HttpServletRequest) request);
		}
		
		if (response instanceof ExtendedResponse) {
			// Avoid rewrapping if forwarding
			xResponse = (ExtendedResponse) response;
		} else {
			xResponse = new ExtendedResponse((HttpServletResponse) response);
		}
		
		chain.doFilter(xRequest, xResponse);
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroy() {
	}
}
