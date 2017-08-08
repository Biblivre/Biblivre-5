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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ExtendedResponse extends HttpServletResponseWrapper {
	
	private int httpStatus = SC_OK;
	
	public ExtendedResponse(HttpServletResponse response) {
		super(response);
	}
	
	public void print(Object text) throws IOException {
		this.getWriter().print(text);
	}
	
	@Override
	public void sendError(int sc) throws IOException {
		super.sendError(sc);
		this.httpStatus = sc;
	}
	
	@Override
	public void sendError(int sc, String msg) throws IOException {
		super.sendError(sc, msg);
		this.httpStatus = sc;
	}
	
	@Override
	public void sendRedirect(String location) throws IOException {
		this.httpStatus = SC_MOVED_TEMPORARILY;
	    super.sendRedirect(location);
	}

    @Override
    public void setStatus(int sc) {
    	super.setStatus(sc);
    	this.httpStatus = sc;
    }

    @Override
    public void setStatus(int sc, String msg) {
    	super.setStatus(sc, msg);
    	this.httpStatus = sc;
    }
	
	public int getStatus() {
		return this.httpStatus;
 	}
}
