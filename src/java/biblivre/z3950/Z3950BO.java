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
package biblivre.z3950;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.marc4j.marc.Record;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.Configurations;
import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;
import biblivre.z3950.client.Z3950Client;

public class Z3950BO extends AbstractBO {
	
	private static ApplicationContext context;
	private Z3950DAO dao;
			
	public static Z3950BO getInstance(String schema) {
		Z3950BO bo = AbstractBO.getInstance(Z3950BO.class, schema);
		
		if (bo.dao == null) {
			bo.dao = Z3950DAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public List<Z3950RecordDTO> search(List<Z3950AddressDTO> servers, Pair<String, String> search) {
		Z3950Client z3950Client = (Z3950Client) this.getContext().getBean("z3950Client");
		List<Z3950RecordDTO> dtoList = new LinkedList<Z3950RecordDTO>();
		int limit = Configurations.getInt(this.getSchema(), Constants.CONFIG_Z3950_RESULT_LIMIT, 100);
		
		for (Z3950AddressDTO searchServer : servers) {
			try {
				List<Record> recordList = z3950Client.search(searchServer, search, limit);

				if (CollectionUtils.isNotEmpty(recordList)) {
					for (Record record : recordList) {
						Z3950RecordDTO recordDto = new Z3950RecordDTO();
						recordDto.setServer(searchServer);
						BiblioRecordDTO dto = new BiblioRecordDTO();
						dto.setRecord(record);
						recordDto.setRecord(dto);
						dtoList.add(recordDto);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return dtoList;
	}
	
	public DTOCollection<Z3950AddressDTO> search(String value, int limit, int offset) {
		return this.dao.search(value, limit, offset);
	}
	
	public List<Z3950AddressDTO> listAll() {
		return this.dao.listAll();
	}
	
	public DTOCollection<Z3950AddressDTO> listServers() {
		DTOCollection<Z3950AddressDTO> servers = new DTOCollection<Z3950AddressDTO>();

		servers.addAll(this.dao.listAll());
		return servers;
	}
	
	public boolean save(Z3950AddressDTO dto) {
		if (dto == null) {
			return false;
		}

		if (dto.getId() == 0) {
			return this.dao.insert(dto);
		} else {
			return this.dao.update(dto);
		}
	}

	public boolean delete(Z3950AddressDTO dto) {
		return this.dao.delete(dto);
	}

	public Z3950AddressDTO findById(int id) {
		List<Integer> ids = new LinkedList<Integer>();
		ids.add(id);
		
		List<Z3950AddressDTO> list = this.dao.list(ids);
		
		return (list.size() > 0) ? list.get(0) : null;
	}
	
	public List<Z3950AddressDTO> list(List<Integer> ids) {
		return this.dao.list(ids);
	}

	private ApplicationContext getContext() {
		if (Z3950BO.context == null) {
			try {
				Z3950BO.context = new ClassPathXmlApplicationContext("applicationContext.xml");
			} catch (Exception e) {
				this.logger.error(e.getMessage(), e);
			}
		}
		
		return Z3950BO.context;
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}
}
