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
package biblivre.cataloging;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.enums.ImportEncoding;
import biblivre.cataloging.enums.ImportFormat;
import biblivre.core.AbstractDTO;
import biblivre.core.IFJson;

public class ImportDTO extends AbstractDTO implements Comparable<ImportDTO>, IFJson {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private List<RecordDTO> recordList = new LinkedList<RecordDTO>();
	private List<String> foundISBN;
	private List<String> foundISSN;
	private List<String> foundISRC;

	private int success;
	private int failure;
	private int found;
	
	private ImportFormat format;
	private ImportEncoding encoding;
	
	public boolean isPerfect() {
		return this.getFound() > 0 && this.getFailure() == 0;
	}
	
	public void addRecord(RecordDTO record) {
		if (record != null) {
			this.recordList.add(record);
		}
	}
	
	public List<RecordDTO> getRecordList() {
		return this.recordList;
	}

	public int getSuccess() {
		return this.success;
	}
	
	public void setSuccess(int success) {
		this.success = success;
	}
	
	public void incrementSuccess() {
		this.success++;
	}
	
	public int getFailure() {
		return this.failure;
	}
	
	public void setFailure(int failure) {
		this.failure = failure;
	}

	public void incrementFailure() {
		this.failure++;
	}
	
	public int getFound() {
		return this.found;
	}
	
	public void setFound(int found) {
		this.found = found;
	}
	
	public void incrementFound() {
		this.found++;
	}
	
	public ImportFormat getFormat() {
		return this.format;
	}

	public void setFormat(ImportFormat format) {
		this.format = format;
	}

	public ImportEncoding getEncoding() {
		return this.encoding;
	}

	public void setEncoding(ImportEncoding encoding) {
		this.encoding = encoding;
	}
	
	public List<String> getFoundISBN() {
		return this.foundISBN;
	}

	public void setFoundISBN(List<String> foundISBN) {
		this.foundISBN = foundISBN;
	}

	public List<String> getFoundISSN() {
		return this.foundISSN;
	}

	public void setFoundISSN(List<String> foundISSN) {
		this.foundISSN = foundISSN;
	}

	public List<String> getFoundISRC() {
		return this.foundISRC;
	}

	public void setFoundISRC(List<String> foundISRC) {
		this.foundISRC = foundISRC;
	}

	@Override
	public int compareTo(ImportDTO o) {
		if (o == null) {
			return -1;
		}

		return Integer.valueOf(o.getSuccess()).compareTo(this.getSuccess());
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("success", this.getSuccess());
			json.putOpt("failure", this.getFailure());
			json.putOpt("found", this.getFound());
			json.putOpt("format", this.getFormat());
			json.putOpt("encoding", this.getEncoding());
			
			for (RecordDTO dto : this.getRecordList()) { 
				json.append("record_list", dto.toJSONObject());
			}
			
			JSONObject isbnJSON = new JSONObject();
			JSONObject issnJSON = new JSONObject();
			JSONObject isrcJSON = new JSONObject();

			if (this.getFoundISBN() != null) {
				for (String term : this.getFoundISBN()) {
					try {
						isbnJSON.putOpt(term, true);
					} catch (JSONException e) {}
				}
			}
			
			if (this.getFoundISSN() != null) {
				for (String term : this.getFoundISSN()) {
					try {
						issnJSON.putOpt(term, true);
					} catch (JSONException e) {}
				}
			}

			if (this.getFoundISRC() != null) {
				for (String term : this.getFoundISRC()) {
					try {
						isrcJSON.putOpt(term, true);
					} catch (JSONException e) {}
				}
			}

			json.putOpt("isbn_list", isbnJSON);
			json.putOpt("issn_list", issnJSON);
			json.putOpt("isrc_list", isrcJSON);
		} catch (JSONException e) {
		}

		return json;
	}
}
