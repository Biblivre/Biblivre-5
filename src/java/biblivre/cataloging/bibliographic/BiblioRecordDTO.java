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
package biblivre.cataloging.bibliographic;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.lending.LendingDTO;

public class BiblioRecordDTO extends RecordDTO {
	private static final long serialVersionUID = 1L;

	private String title;
	private String author;
	private String publicationYear;
	private String isbn;
	private String issn;
	private String isrc;
	private String shelfLocation;
	private String subject;

	private Integer holdingsCount;
	private Integer holdingsAvailable;
	private Integer holdingsLent;
	private Integer holdingsReserved;
	
	private List<HoldingDTO> holdings;
	private Map<Integer, LendingDTO> lendings;
	
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublicationYear() {
		return this.publicationYear;
	}

	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public String getIssn() {
		return this.issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}
	
	public String getIsrc() {
		return this.isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}

	public String getShelfLocation() {
		return this.shelfLocation;
	}

	public void setShelfLocation(String shelfLocation) {
		this.shelfLocation = shelfLocation;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public Integer getHoldingsCount() {
		return this.holdingsCount;
	}

	public void setHoldingsCount(Integer holdingsCount) {
		this.holdingsCount = holdingsCount;
	}

	public Integer getHoldingsAvailable() {
		return this.holdingsAvailable;
	}

	public void setHoldingsAvailable(Integer holdingsAvailable) {
		this.holdingsAvailable = holdingsAvailable;
	}

	public Integer getHoldingsLent() {
		return this.holdingsLent;
	}

	public void setHoldingsLent(Integer holdingsLent) {
		this.holdingsLent = holdingsLent;
	}

	public Integer getHoldingsReserved() {
		return this.holdingsReserved;
	}

	public void setHoldingsReserved(Integer holdingsReserved) {
		this.holdingsReserved = holdingsReserved;
	}
	
	public List<HoldingDTO> getHoldings() {
		return this.holdings;
	}

	public void setHoldings(List<HoldingDTO> holdings) {
		this.holdings = holdings;
	}
	
	public Map<Integer, LendingDTO> getLendings() {
		return this.lendings;
	}

	public void setLendings(Map<Integer, LendingDTO> lendings) {
		this.lendings = lendings;
	}

	@Override
	public JSONObject toJSONObject() {
		JSONObject json = super.toJSONObject();

		try {
			json.putOpt("material_type", this.getMaterialType());
			json.putOpt("title", this.getTitle());
			json.putOpt("author", this.getAuthor());
			json.putOpt("publication_year", this.getPublicationYear());

			json.putOpt("isbn", this.getIsbn());
			json.putOpt("issn", this.getIssn());
			json.putOpt("isrc", this.getIsrc());
			json.putOpt("shelf_location", this.getShelfLocation());
			json.putOpt("subject", this.getSubject());
			
			json.putOpt("holdings_count", this.getHoldingsCount());
			json.putOpt("holdings_available", this.getHoldingsAvailable());
			json.putOpt("holdings_lent", this.getHoldingsLent());
			json.putOpt("holdings_reserved", this.getHoldingsReserved());
			
			if (this.getHoldings() != null) {
				for (HoldingDTO holding : this.getHoldings()) {
					json.append("holdings", holding.toJSONObject());
				}
			}

			if (this.getLendings() != null) {
				JSONObject lendings = new JSONObject();
				
				for (Entry<Integer, LendingDTO> entry : this.getLendings().entrySet()) {
					lendings.put(entry.getKey().toString(), entry.getValue().toJSONObject());
				}
				
				json.put("lendings", lendings);
			}
		} catch (JSONException e) {
		}

		return json;
	}
}
