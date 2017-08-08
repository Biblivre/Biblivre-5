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
package biblivre.cataloging.labels;

import biblivre.core.AbstractDTO;

public class LabelDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

    private int id;
    private Integer recordId;
    private String accessionNumber;
    private String author;
    private String title;
    private String locationA;
    private String locationB;
    private String locationC;
    private String locationD;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getRecordId() {
		return this.recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public String getAccessionNumber() {
		return this.accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocationA() {
		return this.locationA;
	}

	public void setLocationA(String locationA) {
		this.locationA = locationA;
	}

	public String getLocationB() {
		return this.locationB;
	}

	public void setLocationB(String locationB) {
		this.locationB = locationB;
	}

	public String getLocationC() {
		return this.locationC;
	}

	public void setLocationC(String locationC) {
		this.locationC = locationC;
	}

	public String getLocationD() {
		return this.locationD;
	}

	public void setLocationD(String locationD) {
		this.locationD = locationD;
	}
}
