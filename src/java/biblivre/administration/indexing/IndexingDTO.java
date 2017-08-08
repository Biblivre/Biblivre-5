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
package biblivre.administration.indexing;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDTO;

public class IndexingDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;
	
	private Integer recordId;
	private Integer indexingGroupId;
	private StringBuilder phrase;
	private HashMap<Integer, HashSet<String>> words;
	private int ignoreCharsCount;

	public IndexingDTO() {
		this.words = new HashMap<Integer, HashSet<String>>();
		this.phrase = new StringBuilder();
		this.ignoreCharsCount = 0;
	}
	
	public Integer getRecordId() {
		return this.recordId;
	}
	
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
		
	public Integer getIndexingGroupId() {
		return this.indexingGroupId;
	}

	public void setIndexingGroupId(Integer indexingGroupId) {
		this.indexingGroupId = indexingGroupId;
	}

	public int getIgnoreCharsCount() {
		return this.ignoreCharsCount;
	}

	public void setIgnoreCharsCount(int ignoreCharsCount) {
		this.ignoreCharsCount = ignoreCharsCount;
	}

	public String getPhrase() {
		String phrase = this.phrase.toString();

		if (StringUtils.isBlank(phrase)) {
			return null;
		}

		return phrase.trim();
	}
	
	public int getPhraseLength() {
		return this.phrase.length();
	}

	public void appendToPhrase(String phrase) {
		if (phrase == null) {
			return;
		}

		this.phrase.append(" ").append(phrase.trim());
	}

	public void addWord(String word, Integer datafieldId) {
		if (StringUtils.isNotBlank(word)) {
			if (!this.words.containsKey(datafieldId)) {
				this.words.put(datafieldId, new HashSet<String>());
			}
			
			this.words.get(datafieldId).add(word);
		}
	}
	
	public void addWords(String[] words, Integer datafieldId) {
		if (!this.words.containsKey(datafieldId)) {
			this.words.put(datafieldId, new HashSet<String>());
		}
		
		Collections.addAll(this.words.get(datafieldId), words);
	}

	public HashMap<Integer, HashSet<String>> getWords() {
		return this.words;
	}
	
	public int getCount() {
		return this.words.size();
	}
}
