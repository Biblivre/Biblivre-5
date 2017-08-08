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
package biblivre.acquisition.quotation;

import biblivre.core.AbstractDTO;

public class RequestQuotationDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private int requestId;
	private int quotationId;
	private int quantity;
	private Float unitValue;
	private int responseQuantity;
	
    transient private String title;
    transient private String author;
    
	public int getRequestId() {
		return this.requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getQuotationId() {
		return this.quotationId;
	}

	public void setQuotationId(int quotationId) {
		this.quotationId = quotationId;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Float getUnitValue() {
		return this.unitValue;
	}

	public void setUnitValue(Float unitValue) {
		this.unitValue = unitValue;
	}

	public int getResponseQuantity() {
		return this.responseQuantity;
	}

	public void setResponseQuantity(int responseQuantity) {
		this.responseQuantity = responseQuantity;
	}
	
    public String getTitle() {
        return (this.title == null) ? "" : this.title.trim();
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return (this.author == null) ? "" : this.author.trim();
    }

    public void setAuthor(String author) {
        this.author = author;
    }
	
    @Override
    public String toString() {
		StringBuilder builder = new StringBuilder();
    	builder.append(this.getQuantity());
    	builder.append("x ");
    	builder.append(this.getAuthor());
    	builder.append(" - ");
    	builder.append(this.getTitle());
    	return builder.toString();
    }
}
