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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractDTO;
import biblivre.core.utils.Pair;

/**
 * Class that defines the format used to index records by a group of datafields.
 * E.g.: Authors will be indexed by datafields 100, 110, 111, etc.
 */
public class IndexingGroupDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String translationKey;

	/**
	 * Format: DATAFIELD1_SUBFIELD1_SUBFIELD2_SUBFIELD3,DATAFIELD2_SUBFIELD1_SUBFIELD2_SUBFIELD3
	 * E.g.: 100_a_b_c,110_a_b_c,111_a_b_c
	 */
	private String datafields;
	private boolean sortable;
	private boolean defaultSort;
	
	/**
	 * List of DataFields and their respective SubFields
	 * E.g.: [{
	 *   "100": ['a', 'b', 'c']
	 * }, {
	 *   "110": ['a', 'b', 'c']
	 * }, {
	 *   "111": ['a', 'b', 'c']
	 * }]
	 */
	private transient List<Pair<String, List<Character>>> _array;
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTranslationKey() {
		return this.translationKey;
	}

	public void setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
	}

	public boolean isDefaultSort() {
		return this.defaultSort;
	}

	public void setDefaultSort(boolean defaultSort) {
		this.defaultSort = defaultSort;
	}

	public String getDatafields() {
		return this.datafields;
	}

	public void setDatafields(String datafields) {
		this._array = null;
		this.datafields = datafields;
	}
	
	public boolean isSortable() {
		return this.sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public List<Pair<String, List<Character>>> getDatafieldsArray() {
		if (this._array == null) {
			List<Pair<String, List<Character>>> list = new ArrayList<Pair<String, List<Character>>>();
			
			if (this.datafields != null) {
				String[] fields = this.datafields.split(",");
				for (String field : fields) {
					String[] line = field.trim().split("_");
	
					String datafield = line[0];
					List<Character> subfields = new ArrayList<Character>(line.length - 1);

					for (int i = 1; i < line.length; i++) {
						subfields.add(line[i].charAt(0));
					}

					list.add(new Pair<String, List<Character>>(datafield, subfields));
				}
			}

			this._array = list;
		}
		
		return this._array;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("id", this.getId());
			json.putOpt("translation_key", this.getTranslationKey());
			json.putOpt("datafields", this.getDatafields());
			json.putOpt("sortable", this.isSortable());
			json.putOpt("default_sort", this.isDefaultSort());
		} catch (JSONException e) {
		}

		return json;
	}
}
