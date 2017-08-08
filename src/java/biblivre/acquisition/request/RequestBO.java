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
package biblivre.acquisition.request;

import java.util.List;

import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public class RequestBO extends AbstractBO {
	private RequestDAO dao;

	public static RequestBO getInstance(String schema) {
		RequestBO bo = AbstractBO.getInstance(RequestBO.class, schema);

		if (bo.dao == null) {
			bo.dao = RequestDAO.getInstance(schema);
		}
		
		return bo;
	}
	
    public RequestDTO get(Integer id) {
        return this.dao.get(id);
    }
    
    public boolean save(RequestDTO dto) {
        return this.dao.save(dto);
    }

    public boolean update(RequestDTO dto) {
        return this.dao.update(dto);
    }
    
    public boolean delete(RequestDTO dto) {
        return this.dao.delete(dto);
    }
	
    public DTOCollection<RequestDTO> list() {
        return this.search(null, Integer.MAX_VALUE, 0);
    }

    public DTOCollection<RequestDTO> search(String value, int limit, int offset) {
    	return this.dao.search(value, limit, offset);
    }
    
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}

}
