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

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

public class DTOCollection<T extends AbstractDTO> extends LinkedList<T> implements IFJson  {
	private static final long serialVersionUID = 1L;
	
	private Integer id;

	private PagingDTO paging;
	
	public PagingDTO getPaging() {
		return this.paging;
	}

	public void setPaging(PagingDTO paging) {
		this.paging = paging;
	}

	public int getRecordLimit() {
		if (this.paging == null) {
			return 0;
		}
		return this.paging.getRecordLimit();
	}
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = null;
				
		if (this.getPaging() != null) {
			json = this.getPaging().toJSONObject();
		} else {
			json = new JSONObject();
		}
		
		try {
			json.putOpt("id", this.getId());
			for (T dto : this) {
				json.append("data", dto.toJSONObject());
			}
		} catch (JSONException e) {
		}

		return json;
	}
}
