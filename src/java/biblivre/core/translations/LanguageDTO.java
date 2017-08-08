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

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDTO;

public class LanguageDTO extends AbstractDTO implements Comparable<LanguageDTO> {
	private static final long serialVersionUID = 1L;

	private String language;
	private String name;
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return StringUtils.defaultString(this.language);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return StringUtils.defaultString(this.name);
	}

	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || !(other instanceof LanguageDTO)) {
			return false;
		}
		
		LanguageDTO otherDto = (LanguageDTO) other;
		
		return this.language == null ? otherDto.language == null : this.language.equals(otherDto.language);
	}
	
	@Override
	public int compareTo(LanguageDTO other) {
		return this.getLanguage().compareTo(other.getLanguage());
	}
}
