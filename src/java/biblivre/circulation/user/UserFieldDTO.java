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
package biblivre.circulation.user;

import biblivre.core.AbstractDTO;

public class UserFieldDTO extends AbstractDTO implements Comparable<UserFieldDTO> {
	private static final long serialVersionUID = 1L;

	private String key;
	private UserFieldType type;
	private boolean required;
	private int maxLength;
	private int sortOrder;

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(int order) {
		this.sortOrder = order;
	}

	public UserFieldType getType() {
		return this.type;
	}

	public void setType(UserFieldType type) {
		this.type = type;
	}

	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public int compareTo(UserFieldDTO other) {
		if (other == null) {
			return -1;
		}

		if (this.getSortOrder() != other.getSortOrder()) {
			return other.getSortOrder() - this.getSortOrder();
		} else	if (this.getKey() != null && other.getKey() != null) {
			return this.getKey().compareTo(other.getKey());
		}

		return 0;
	}
}
