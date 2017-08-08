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


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.marc4j.marc.Record;

import biblivre.administration.indexing.IndexingBO;
import biblivre.cataloging.authorities.AuthorityRecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.ImportEncoding;
import biblivre.cataloging.enums.ImportFormat;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.vocabulary.VocabularyRecordDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import biblivre.z3950.Z3950AddressDTO;
import biblivre.z3950.Z3950BO;
import biblivre.z3950.Z3950RecordDTO;

public class Handler extends AbstractHandler {
	
	public void importUpload(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		MemoryFile file = request.getFile("file");
		
		ImportFormat format = request.getEnum(ImportFormat.class, "format", ImportFormat.AUTO_DETECT);
		ImportEncoding encoding = request.getEnum(ImportEncoding.class, "encoding", ImportEncoding.AUTO_DETECT);

		ImportBO bo = ImportBO.getInstance(schema);

		ImportDTO list = bo.loadFromFile(file, format, encoding);

		if (list != null) {
			List<String> isbnList = new LinkedList<String>();
			List<String> issnList = new LinkedList<String>();
			List<String> isrcList = new LinkedList<String>();

			for (RecordDTO dto : list.getRecordList()) {
				if (dto instanceof BiblioRecordDTO) {
					BiblioRecordDTO rdto = (BiblioRecordDTO) dto;
					
					if (StringUtils.isNotBlank(rdto.getIsbn())) {
						isbnList.add(rdto.getIsbn());
					} else if (StringUtils.isNotBlank(rdto.getIssn())) {
						issnList.add(rdto.getIssn());
					} else if (StringUtils.isNotBlank(rdto.getIsrc())) {
						isrcList.add(rdto.getIsrc());
					}
				}
				//TODO: Completar para autoridades e vocabulário
			}

			IndexingBO ibo = IndexingBO.getInstance(schema);
	
			if (isbnList.size() > 0) {
				list.setFoundISBN(ibo.searchExactTerms(RecordType.BIBLIO, 5, isbnList));
			}
	
			if (issnList.size() > 0) {
				list.setFoundISSN(ibo.searchExactTerms(RecordType.BIBLIO, 6, issnList));
			}
			
			if (isrcList.size() > 0) {
				list.setFoundISRC(ibo.searchExactTerms(RecordType.BIBLIO, 7, isrcList));
			}
		}

		try {
			if (list == null) {
				this.setMessage(ActionResult.WARNING, "cataloging.import.error.invalid_file");
			} else if (list.getSuccess() == 0) {
				this.setMessage(ActionResult.WARNING, "cataloging.import.error.no_record_found");
			} else {
				this.json.putOpt("data", list.toJSONObject());
			}
		} catch(JSONException e) { }
	}

	public void importSearch(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("search_server");
		String attribute = request.getString("search_attribute");
		String value = request.getString("search_query");

		Z3950BO bo = Z3950BO.getInstance(schema);
		Z3950AddressDTO server = bo.findById(id);
		
		List<Z3950AddressDTO> serverList = new LinkedList<Z3950AddressDTO>();
		serverList.add(server);
		Pair<String, String> search = new Pair<String, String>(attribute, value);
		
		List<Z3950RecordDTO> recordList = bo.search(serverList, search);
		
		ImportBO importBo = ImportBO.getInstance(schema);
		ImportDTO list = importBo.readFromZ3950Results(recordList);

		if (list != null) {
			List<String> isbnList = new LinkedList<String>();
			List<String> issnList = new LinkedList<String>();
			List<String> isrcList = new LinkedList<String>();

			for (RecordDTO dto : list.getRecordList()) {
				if (dto instanceof BiblioRecordDTO) {
					BiblioRecordDTO rdto = (BiblioRecordDTO) dto;
					
					if (StringUtils.isNotBlank(rdto.getIsbn())) {
						isbnList.add(rdto.getIsbn());
					} else if (StringUtils.isNotBlank(rdto.getIssn())) {
						issnList.add(rdto.getIssn());
					} else if (StringUtils.isNotBlank(rdto.getIsrc())) {
						isrcList.add(rdto.getIsrc());
					}
				}
			}

			IndexingBO ibo = IndexingBO.getInstance(schema);
	
			if (isbnList.size() > 0) {
				list.setFoundISBN(ibo.searchExactTerms(RecordType.BIBLIO, 5, isbnList));
			}
	
			if (issnList.size() > 0) {
				list.setFoundISSN(ibo.searchExactTerms(RecordType.BIBLIO, 6, issnList));
			}
			
			if (isrcList.size() > 0) {
				list.setFoundISRC(ibo.searchExactTerms(RecordType.BIBLIO, 7, isrcList));
			}
		}

		try {
			if (list == null) {
				this.setMessage(ActionResult.WARNING, "cataloging.import.error.invalid_file");
			} else if (list.getSuccess() == 0) {
				this.setMessage(ActionResult.WARNING, "cataloging.import.error.no_record_found");
			} else {
				this.json.putOpt("data", list.toJSONObject());
			}
		} catch(JSONException e) { }
	}

	
	public void parseMarc(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String marc = request.getString("marc");
				
		ImportBO bo = ImportBO.getInstance(schema);
		IndexingBO ibo = IndexingBO.getInstance(schema);
		RecordDTO dto = null;
		try {
			Record record = MarcUtils.marcToRecord(marc, null, RecordStatus.NEW);
			dto = bo.dtoFromRecord(record);
			BiblioRecordDTO rdto = ((BiblioRecordDTO)dto);
			
			if (StringUtils.isNotBlank(rdto.getIsbn())) {
				List<String> search = ibo.searchExactTerm(RecordType.BIBLIO, 5, rdto.getIsbn());
				this.json.putOpt("isbn", !search.isEmpty());
			} else if (StringUtils.isNotBlank(rdto.getIssn())) {
				List<String> search = ibo.searchExactTerm(RecordType.BIBLIO, 6, rdto.getIssn());
				this.json.putOpt("issn", !search.isEmpty());
			} else if (StringUtils.isNotBlank(rdto.getIsrc())) {
				List<String> search = ibo.searchExactTerm(RecordType.BIBLIO, 7, rdto.getIsrc());
				this.json.putOpt("isrc", !search.isEmpty());
			}
			
		} catch (Exception e) {		
		}
		
		try {
			if (dto == null) {
				this.setMessage(ActionResult.WARNING, "cataloging.import.error.invalid_marc");
			} else {
				this.json.putOpt("data", dto.toJSONObject());
			}
		} catch(JSONException e) { }
	}

	
	public void saveImport(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		int start = request.getInteger("start", 1);
		int end = request.getInteger("end", Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
		Set<Integer> successIds = new HashSet<Integer>();
		Set<Integer> failedIds = new HashSet<Integer>(); 
		
		for (int i = start; i <= end; i++) {
			String marc = request.getString("marc_" + i);
			RecordType recordType = request.getEnum(RecordType.class, "record_type_" + i);
			
			if (recordType == null) { 
				continue;
			}
			
			RecordBO bo = RecordBO.getInstance(schema, recordType);
			
			RecordDTO dto = null;
			
			switch(recordType) {
				case BIBLIO: dto = new BiblioRecordDTO(); break;
				case AUTHORITIES: dto = new AuthorityRecordDTO(); break;
				case VOCABULARY: dto = new VocabularyRecordDTO(); break;
				default: dto = new RecordDTO();
			}
			
			Record record = null;			
			try {
				record = MarcUtils.marcToRecord(marc, null, RecordStatus.NEW);
			} catch (Exception e) {
				failedIds.add(i);
				this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
				continue;			
			}
			
			if (record == null) {
				continue;
			}
			
			dto.setRecord(record);
			dto.setMaterialType(MaterialType.fromRecord(record));
			dto.setRecordDatabase(RecordDatabase.WORK);
			dto.setCreatedBy(request.getLoggedUserId());
			
			if (bo.save(dto)) {
				successIds.add(i);
			} else {
				failedIds.add(i);
			}
		}
		
		if (!successIds.isEmpty()) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.import.save.success");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.import.save.failed");
		}
		
		try {
			for (Integer id : successIds) {
				this.json.append("saved", id);
			}
			for (Integer id : failedIds) {
				this.json.append("failed", id);
			}
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
		
	}
}
