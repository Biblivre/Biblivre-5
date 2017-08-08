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
package biblivre.cataloging.holding;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.core.AbstractDTO;

public class AutomaticHoldingDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 1L;

    private Integer holdingCount;
    private Integer issueNumber;
    private Integer numberOfIssues;
    private String acquisitionDate;
    private String libraryName;
    private String acquisitionType;
    private RecordDTO biblioRecordDto;
    private RecordDatabase database; 

	public Integer getHoldingCount() {
		return this.holdingCount;
	}

	public void setHoldingCount(Integer holdingCount) {
		this.holdingCount = holdingCount;
	}

	public Integer getIssueNumber() {
		return this.issueNumber;
	}

	public void setIssueNumber(Integer issueNumber) {
		this.issueNumber = issueNumber;
	}

	public Integer getNumberOfIssues() {
		return this.numberOfIssues;
	}

	public void setNumberOfIssues(Integer numberOfIssues) {
		this.numberOfIssues = numberOfIssues;
	}

	public String getAcquisitionDate() {
		return this.acquisitionDate;
	}

	public void setAcquisitionDate(String acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	public String getLibraryName() {
		return this.libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public String getAcquisitionType() {
		return this.acquisitionType;
	}

	public void setAcquisitionType(String acquisitionType) {
		this.acquisitionType = acquisitionType;
	}

	public RecordDTO getBiblioRecordDto() {
		return this.biblioRecordDto;
	}

	public void setBiblioRecordDto(RecordDTO biblioRecordDto) {
		this.biblioRecordDto = biblioRecordDto;
	}

	public RecordDatabase getDatabase() {
		return this.database;
	}

	public void setDatabase(RecordDatabase database) {
		this.database = database;
	}
	
}
