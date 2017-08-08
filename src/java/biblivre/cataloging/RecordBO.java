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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;

import biblivre.cataloging.authorities.AuthorityRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.cataloging.search.SearchDAO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.vocabulary.VocabularyRecordBO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PagingDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.SearchMode;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.marc.MarcUtils;

public abstract class RecordBO extends AbstractBO {
	protected RecordDAO rdao;
	protected SearchDAO sdao;

	public static final int FULL = 1 << 0;
	public static final int MARC_INFO = 1 << 1;
	public static final int HOLDING_INFO = 1 << 2;
	public static final int HOLDING_LIST = 1 << 3;
	public static final int LENDING_INFO = 1 << 4;
	public static final int LENDING_LIST = 1 << 5;
	public static final int ATTACHMENTS_LIST = 1 << 6;
	
	public static final Pattern ID_PATTERN = Pattern.compile("id=(.*?)(&|$)");
	
	/**
	 * Class Factory
	 */
	public static RecordBO getInstance(String schema, RecordType recordType) {
		if (recordType == null) {
			return null;
		}

		switch(recordType) {
			case BIBLIO: return BiblioRecordBO.getInstance(schema);
			case HOLDING: return HoldingBO.getInstance(schema);
			case AUTHORITIES: return AuthorityRecordBO.getInstance(schema);
			case VOCABULARY: return VocabularyRecordBO.getInstance(schema);
			default: return null;
		}
	}

	public RecordDTO get(int id) {
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(id);
		
		return this.map(ids).get(id);
	}

	public RecordDTO get(int id, int mask) {
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(id);
		
		return this.map(ids, mask).get(id);
	}

	
	public Map<Integer, RecordDTO> map(Set<Integer> ids) {
		return this.rdao.map(ids);
	}

	public Map<Integer, RecordDTO> map(Set<Integer> ids, int mask) {
		Map<Integer, RecordDTO> map = this.rdao.map(ids);
		
		for (RecordDTO dto : map.values()) {
			this.populateDetails(dto, mask);
		}
		
		return map;
	}
	
	public List<RecordDTO> list(int offset, int limit) {
		return this.rdao.list(offset, limit);
	}

	public List<RecordDTO> listByLetter(char letter, int order) {
		return this.populateDetails(this.rdao.listByLetter(letter, order), RecordBO.MARC_INFO);
	}

	public boolean save(RecordDTO dto) {
		return this.rdao.save(dto);
	}

	public boolean update(RecordDTO dto) {
		return this.rdao.update(dto);
	}

	public boolean moveRecords(Set<Integer> ids, int modifiedBy, RecordDatabase database) {
		return this.rdao.moveRecords(ids, modifiedBy, database);
	}
	
	public boolean listContainsPrivateRecord(Set<Integer> ids) {
		return this.rdao.listContainsPrivateRecord(ids);
	}
	
	public DiskFile createExportFile(Set<Integer> ids) {
		Map<Integer, RecordDTO> records = this.map(ids);
		try {
			File file = File.createTempFile("biblivre", ".mrc");
			FileOutputStream out = new FileOutputStream(file);
			OutputStreamWriter marcWriter = new OutputStreamWriter(out, "UTF-8");
			for (RecordDTO dto : records.values()) {
				marcWriter.write(dto.getIso2709());
				marcWriter.write(Constants.LINE_BREAK);
			}
			marcWriter.flush();
			marcWriter.close();
			return new DiskFile(file, "x-download");
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public boolean delete(RecordDTO dto) {
		return this.rdao.delete(dto);
	}
	
	public Integer count() {
		return this.count(null);
	}
	
	public Integer count(SearchDTO search) {
		return this.rdao.count(search);
	}
	
	public boolean search(SearchDTO search) {
		SearchMode searchMode = search.getSearchMode();

		boolean isNewSearch = (search.getId() == null);
		
		if (isNewSearch) {
			if (!this.sdao.createSearch(search)) {
				return false;
			}
		}

		switch (searchMode) {
			case SIMPLE:
				if (!this.sdao.populateSimpleSearch(search, !isNewSearch)) {
					return false;
				}

				break;

			case ADVANCED:
				if (!this.sdao.populateAdvancedSearch(search, !isNewSearch)) {
					return false;
				}

				break;

			case LIST_ALL:
				break;
		}

		return this.paginateSearch(search);
	}

	public boolean paginateSearch(SearchDTO search) {
		if (search.getQuery().isHoldingSearch()) {
			HoldingBO hbo = (HoldingBO) RecordBO.getInstance(this.getSchema(), RecordType.HOLDING);
			return hbo.paginateHoldingSearch(search);
		}

		Map<Integer, Integer> groupCount = this.rdao.countSearchResults(search);
		Integer count = groupCount.get(search.getIndexingGroup());

		if (count == null || count == 0) {
			return false;
		}

		List<RecordDTO> list = this.rdao.getSearchResults(search);

		search.getPaging().setRecordCount(count);
		search.setIndexingGroupCount(groupCount);

		for (RecordDTO rdto : list) {
			this.populateDetails(rdto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
			search.add(rdto);
		}

		return true;
	}
	
	public SearchDTO getSearch(Integer searchId) {
		SearchDTO search = this.sdao.getSearch(searchId);

		if (search != null) {
			if (search.getPaging() == null) {
				search.setPaging(new PagingDTO());
			}
			
			PagingDTO paging = search.getPaging();
			paging.setRecordsPerPage(Configurations.getPositiveInt(this.getSchema(), Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20));
			paging.setRecordLimit(Configurations.getPositiveInt(this.getSchema(), Constants.CONFIG_SEARCH_RESULT_LIMIT, 2000));
		}

		return search;
	}

	public List<RecordDTO> populateDetails(List<RecordDTO> list, int mask) {
		for (RecordDTO rdto : list) {
			this.populateDetails(rdto, mask);
		}
		
		return list;
	}
	
	public List<String> phraseAutocomplete(String datafield, String subfield, String query) {
		String[] searchTerms = TextUtils.prepareAutocomplete(query);
		
		List<String> listA = this.rdao.phraseAutocomplete(datafield, subfield, searchTerms, 10, true);
		List<String> listB = this.rdao.phraseAutocomplete(datafield, subfield, searchTerms, 5, false);
		
		listA.addAll(listB);
		
		return listA;
	}
	
	public DTOCollection<AutocompleteDTO> recordAutocomplete(String datafield, String subfield, String query) {
		String[] searchTerms = TextUtils.prepareAutocomplete(query);
		
		DTOCollection<AutocompleteDTO> listA = this.rdao.recordAutocomplete(datafield, subfield, searchTerms, 10, true);
		DTOCollection<AutocompleteDTO> listB = this.rdao.recordAutocomplete(datafield, subfield, searchTerms, 5, false);
		
		listA.addAll(listB);
		
		return listA;
	}

	public RecordDTO addAttachment(Integer recordId, String uri, String description, Integer userId) {
		RecordDTO dto = this.get(recordId);
		dto.setRecord(MarcUtils.iso2709ToRecord(dto.getIso2709()));
			
		Record record = MarcUtils.addAttachment(dto.getRecord(), uri, description);
			
		dto.setRecord(record);
		dto.setModifiedBy(userId);
			
		// Update the record in Biblivre DB
		this.update(dto);
		
		return dto;
	}

	public RecordDTO removeAttachment(Integer recordId, String uri, String description, Integer userId) {
		RecordDTO dto = this.get(recordId);
		dto.setRecord(MarcUtils.iso2709ToRecord(dto.getIso2709()));
		// Remove datafield 856 from the Marc Record
		try {
			Record record = MarcUtils.removeAttachment(dto.getRecord(), uri, description);
			dto.setRecord(record);
			dto.setModifiedBy(userId);
			
			// Update the record in Biblivre DB
			this.update(dto);
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}
		
		//Check if the file is in Biblivre's DB and try to delete it
		try {
			Matcher matcher = RecordBO.ID_PATTERN.matcher(uri);
			if (matcher.find()) {
				String encodedId = matcher.group(1);
				String fileId = "";
				String fileName = "";
				String decodedId = new String(new Base64().decode(encodedId));
				String[] splitId = decodedId.split(":");
				if (splitId.length == 2 && StringUtils.isNumeric(splitId[0])) {
					fileId = splitId[0];
					fileName = splitId[1];
				}
				
				// Try to remove the file from Biblivre DB
				DigitalMediaBO dmbo = DigitalMediaBO.getInstance(this.getSchema());
				dmbo.delete(Integer.valueOf(fileId), fileName);
			}
		} catch (Exception e) {
		}
		
		return dto;
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.rdao.saveFromBiblivre3(dtoList);
	}

	public abstract void populateDetails(RecordDTO record, int mask);
	public abstract boolean isDeleatable(HoldingDTO holding) throws ValidationException;
}
