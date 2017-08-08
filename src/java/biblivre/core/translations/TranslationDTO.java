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

import biblivre.core.AbstractDTO;

public class TranslationDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private String language;
	private String key;
	private String text;
	private boolean userEdited;
	private boolean userCreated;
	
	public String getLanguage() {
		return this.language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public boolean isUserEdited() {
		return this.userEdited;
	}
	
	public void setUserEdited(boolean userEdited) {
		this.userEdited = userEdited;
	}
	
	public boolean isUserCreated() {
		return this.userCreated;
	}
	
	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}

	@Override
	public String toString() {
		return this.getText();
	}
}
