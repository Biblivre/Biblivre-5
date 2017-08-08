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
package biblivre.administration.reports;

import java.util.Calendar;
import java.util.Date;

import biblivre.cataloging.enums.RecordDatabase;

public class ReportsDTO {

	private ReportType type;
	private Date initialDate;
	private Date finalDate;
	private RecordDatabase database;
	private String order;
	private String userId;
	private String authorName;
	private String recordIds;
	private String datafield;
	private Integer digits;
	private String marcField;
	private String countOrder;
	private Integer searchId;

	public String getAuthorName() {
		return this.authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public RecordDatabase getDatabase() {
		return this.database;
	}

	public void setDatabase(RecordDatabase database) {
		this.database = database;
	}

	public Date getFinalDate() {
		return this.finalDate;
	}

	public void setFinalDate(Date finalDate) {
		if (finalDate != null) {
			Calendar c = Calendar.getInstance(); 
			c.setTime(finalDate); 
			c.add(Calendar.DATE, 1);
			finalDate = c.getTime();
		}
		this.finalDate = finalDate;
	}

	public Date getInitialDate() {
		return this.initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public String getOrder() {
		return this.order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public ReportType getType() {
		return this.type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	public String getRecordIds() {
		return this.recordIds;
	}

	public void setRecordIds(String recordIds) {
		this.recordIds = recordIds;
	}

	public String getDatafield() {
		return this.datafield;
	}

	public void setDatafield(String datafield) {
		this.datafield = datafield;
	}

	public Integer getDigits() {
		return this.digits;
	}

	public void setDigits(Integer digits) {
		this.digits = digits;
	}

	public String getMarcField() {
		return this.marcField;
	}

	public void setMarcField(String marcField) {
		this.marcField = marcField;
	}

	public String getCountOrder() {
		return this.countOrder;
	}

	public void setCountOrder(String countOrder) {
		this.countOrder = countOrder;
	}

	public Integer getSearchId() {
		return this.searchId;
	}

	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
	}
	
}