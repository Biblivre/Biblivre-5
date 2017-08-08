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
package biblivre.administration.reports.dto;

import java.util.List;

import biblivre.circulation.user.UserDTO;

public class UserReportDto extends BaseReportDto {

	private UserDTO user;
	private List<String[]> lendings;
	private List<String[]> lateLendings;
	private List<String[]> returnedLendings;

	public List<String[]> getLateLendings() {
		return this.lateLendings;
	}

	public void setLateLendings(List<String[]> lateLendings) {
		this.lateLendings = lateLendings;
	}

	public List<String[]> getLendings() {
		return this.lendings;
	}

	public void setLendings(List<String[]> lendings) {
		this.lendings = lendings;
	}

	public List<String[]> getReturnedLendings() {
		return this.returnedLendings;
	}

	public void setReturnedLendings(List<String[]> returnedLendings) {
		this.returnedLendings = returnedLendings;
	}

	public UserDTO getUser() {
		return this.user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

}
