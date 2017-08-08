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
package biblivre.acquisition.order;

import java.util.Date;
import java.util.List;

import biblivre.acquisition.quotation.RequestQuotationDTO;
import biblivre.core.AbstractDTO;

public class OrderDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer quotationId;
	private String info;
	private String status;
	private String invoiceNumber;
	private Date receiptDate;
	private Float totalValue;
	private Integer deliveredQuantity;
	private String termsOfPayment;
	private Date deadlineDate;

	transient private Integer supplierId;
	transient private String supplierName;
	transient private Integer deliveryTime;
	transient private List<RequestQuotationDTO> quotationsList;
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQuotationId() {
		return this.quotationId;
	}

	public void setQuotationId(Integer quotationId) {
		this.quotationId = quotationId;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInvoiceNumber() {
		return this.invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getReceiptDate() {
		return this.receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public Float getTotalValue() {
		return this.totalValue;
	}

	public void setTotalValue(Float totalValue) {
		this.totalValue = totalValue;
	}

	public Integer getDeliveredQuantity() {
		return this.deliveredQuantity;
	}

	public void setDeliveredQuantity(Integer deliveredQuantity) {
		this.deliveredQuantity = deliveredQuantity;
	}

	public String getTermsOfPayment() {
		return this.termsOfPayment;
	}

	public void setTermsOfPayment(String termsOfPayment) {
		this.termsOfPayment = termsOfPayment;
	}

	public Date getDeadlineDate() {
		return this.deadlineDate;
	}

	public void setDeadlineDate(Date deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public Integer getSupplierId() {
		return this.supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getDeliveryTime() {
		return this.deliveryTime;
	}

	public void setDeliveryTime(Integer deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getSupplierName() {
		return this.supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public List<RequestQuotationDTO> getQuotationsList() {
		return this.quotationsList;
	}

	public void setQuotationsList(List<RequestQuotationDTO> quotationsList) {
		this.quotationsList = quotationsList;
	}
	
}
