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

import java.util.List;

import biblivre.acquisition.quotation.QuotationBO;
import biblivre.acquisition.quotation.QuotationDTO;
import biblivre.acquisition.quotation.RequestQuotationDTO;
import biblivre.acquisition.request.RequestBO;
import biblivre.acquisition.request.RequestDTO;
import biblivre.acquisition.supplier.SupplierBO;
import biblivre.acquisition.supplier.SupplierDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public class OrderBO extends AbstractBO {
	private OrderDAO dao;

	public static OrderBO getInstance(String schema) {
		OrderBO bo = AbstractBO.getInstance(OrderBO.class, schema);

		if (bo.dao == null) {
			bo.dao = OrderDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public OrderDTO get(Integer id) {
		OrderDTO dto = this.dao.get(id);
		
		QuotationBO qbo = QuotationBO.getInstance(this.getSchema());
		SupplierBO sbo = SupplierBO.getInstance(this.getSchema());
		RequestBO rbo = RequestBO.getInstance(this.getSchema());
		this.populateDTO(dto, qbo, sbo, rbo);
		
		return dto;
	}
	
	public Integer save(OrderDTO dto) {
		return this.dao.save(dto);
	}

	public boolean update(OrderDTO dto) {
		return this.dao.update(dto);
	}
	
	public boolean delete(OrderDTO dto) {
		return this.dao.delete(dto);
	}
	
	public DTOCollection<OrderDTO> list() {
		return this.search(null, Integer.MAX_VALUE, 0);
	}

	public DTOCollection<OrderDTO> search(String value, int limit, int offset) {
		DTOCollection<OrderDTO> list = this.dao.search(value, limit, offset);
		
		QuotationBO qbo = QuotationBO.getInstance(this.getSchema());
		SupplierBO sbo = SupplierBO.getInstance(this.getSchema());
		RequestBO rbo = RequestBO.getInstance(this.getSchema());
		for (OrderDTO dto : list) {
			this.populateDTO(dto, qbo, sbo, rbo);
		}
		
		return list;
	}
	
	private void populateDTO(OrderDTO dto, QuotationBO qbo, SupplierBO sbo, RequestBO rbo) {
		QuotationDTO qdto = qbo.get(dto.getQuotationId());
		SupplierDTO sdto = sbo.get(qdto.getSupplierId());
		
		List<RequestQuotationDTO> rqList = qbo.listRequestQuotation(qdto.getId());
		for (RequestQuotationDTO rqdto : rqList) {
			RequestDTO request = rbo.get(rqdto.getRequestId());
			rqdto.setAuthor(request.getAuthor());
			rqdto.setTitle(request.getTitle());
		}
		
		dto.setQuotationsList(rqList);
		dto.setSupplierId(qdto.getSupplierId());
		dto.setSupplierName(sdto.getTrademark());
		dto.setDeliveryTime(qdto.getDeliveryTime());
		
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}

}
