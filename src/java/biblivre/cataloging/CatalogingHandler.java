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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j.marc.Record;

import biblivre.administration.indexing.IndexingGroupDTO;
import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.enums.AutocompleteType;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.PagingDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;

public abstract class CatalogingHandler extends AbstractHandler {

	protected RecordType recordType;
	protected MaterialType defaultMaterialType;
	
	protected CatalogingHandler(RecordType recordType, MaterialType defaultMaterialType) {
		this.recordType = recordType;
		this.defaultMaterialType = defaultMaterialType;
	}

	//http://localhost:8080/Biblivre5/default?controller=json&module=cataloging.bibliographic&action=search&search_parameters={search_type:%22simple%22,database:%22main%22,material_type:%22all%22,search_terms:[{query:%22machado%22}]}
	public void search(ExtendedRequest request, ExtendedResponse response) {
		SearchDTO search = this.searchHelper(request, response, this);

		if (CollectionUtils.isEmpty(search)) {
			return;
		}

		List<IndexingGroupDTO> groups = IndexingGroups.getGroups(request.getSchema(), this.recordType);
		
		try {
			this.json.put("search", search.toJSONObject());

			for (IndexingGroupDTO group : groups) {
				this.json.accumulate("indexing_groups", group.toJSONObject());
			}
		} catch(JSONException e) { }
	}

	public SearchDTO searchHelper(ExtendedRequest request, ExtendedResponse response, AbstractHandler handler) {
		String schema = request.getSchema();
		String searchParameters = request.getString("search_parameters");

		SearchQueryDTO searchQuery;

		try {
			searchQuery = new SearchQueryDTO(searchParameters);

			if (searchQuery.getDatabase() == RecordDatabase.PRIVATE) {
				this.authorize(request, "cataloging.bibliographic", "private_database_access");
			}
		} catch (ValidationException e) {
			handler.setMessage(ActionResult.WARNING, e.getMessage());
			return null;
		}

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);

		SearchDTO search = new SearchDTO(this.recordType);
		PagingDTO paging = new PagingDTO();
		search.setPaging(paging);

		paging.setRecordsPerPage(Configurations.getPositiveInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20));
		paging.setRecordLimit(Configurations.getPositiveInt(schema, Constants.CONFIG_SEARCH_RESULT_LIMIT, 2000));
		paging.setPage(1);

		search.setQuery(searchQuery);
		search.setSort(IndexingGroups.getDefaultSortableGroupId(schema, this.recordType));

		bo.search(search);

		paging.endTimer();

		if (search.size() == 0) {
			handler.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
		}
		
		return search;
	}
	
	//http://localhost:8080/Biblivre5/default?controller=json&module=cataloging.bibliographic&action=paginate&search_id=248&page=20&indexing_group=0
	public void paginate(ExtendedRequest request, ExtendedResponse response) {
		SearchDTO search = this.paginateHelper(request, response, this);

		List<IndexingGroupDTO> groups = IndexingGroups.getGroups(request.getSchema(), this.recordType);
		
		if (CollectionUtils.isEmpty(search)) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return;
		}
		
		try {
			this.json.put("search", search.toJSONObject());

			for (IndexingGroupDTO group : groups) {
				this.json.accumulate("indexing_groups", group.toJSONObject());
			}
		} catch(JSONException e) { }
	}

	public SearchDTO paginateHelper(ExtendedRequest request, ExtendedResponse response, AbstractHandler handler) {
		String schema = request.getSchema();
		Integer searchId = request.getInteger("search_id", null);
		Integer indexingGroup = request.getInteger("indexing_group", 0);
		Integer sort = request.getInteger("sort", IndexingGroups.getDefaultSortableGroupId(schema, this.recordType));
		Integer page = request.getInteger("page", 1);
		
		RecordBO bo = RecordBO.getInstance(schema, this.recordType);

		SearchDTO search = bo.getSearch(searchId);

		if (search == null) {
			handler.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
			return null;			
		}

		if (search.getQuery().getDatabase() == RecordDatabase.PRIVATE) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}
		
		search.getPaging().setPage(page);
		search.setSort(sort);
		search.setIndexingGroup(indexingGroup);

		bo.paginateSearch(search);

		if (search.size() == 0) {
			bo.search(search);
		}

		if (search.size() == 0) {
			handler.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
		}
		
		return search;
	}
	
	public void open(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id", null);
		
		if (id == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		RecordDTO dto = bo.get(id);
		
		if (dto == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}
		
		if (dto.getRecordDatabase() == RecordDatabase.PRIVATE) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}		
		
		
		Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
		MarcDataReader marcDataReader = new MarcDataReader(record);

		bo.populateDetails(dto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO | RecordBO.HOLDING_LIST | RecordBO.LENDING_INFO);

		// Record tab
		List<BriefTabFieldFormatDTO> formats = Fields.getBriefFormats(schema, this.recordType);
		List<BriefTabFieldDTO> fields = marcDataReader.getFieldList(formats);

		dto.setFields(fields);

		// Marc tab
		dto.setMarc(MarcUtils.recordToMarc(record));

		// Form tab
		dto.setJson(MarcUtils.recordToJson(record));
	
		// Attachments
		dto.setAttachments(marcDataReader.getAttachments());
		
		try {
			this.json.put("data", dto.toJSONObject());
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		Integer id = request.getInteger("id");
		RecordConvertion from = request.getEnum(RecordConvertion.class, "from");
		MaterialType materialType = request.getEnum(MaterialType.class, "material_type", this.defaultMaterialType);
		RecordDatabase database = request.getEnum(RecordDatabase.class, "database");
		String data = request.getString("data");

		if (database == RecordDatabase.PRIVATE) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}		
		
		RecordStatus status = (id == 0) ? RecordStatus.NEW : RecordStatus.CORRECTED;

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);

		RecordDTO dto = (id == 0) ? this.createRecordDTO(request) : bo.get(id);

		if (dto == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}

		dto.setMaterialType(materialType);
		dto.setRecordDatabase(database);

		try {
			switch (from) {
				case MARC:
				case RECORD:
				case HOLDING_MARC:
					dto.setRecord(MarcUtils.marcToRecord(data, materialType, status));	
					break;
				case FORM:
				case HOLDING_FORM:
					dto.setRecord(MarcUtils.jsonToRecord(new JSONObject(data), materialType, status));
					break;
			}
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;			
		}
		
		this.beforeSave(request, dto);

		boolean success = false;
		
		if (id == 0) {
			dto.setCreatedBy(request.getLoggedUserId());
			success = bo.save(dto);
		} else {
			dto.setModifiedBy(request.getLoggedUserId());
			success = bo.update(dto);
		}
		
		if (success) {
			if (id == 0) {
				this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.save");
			} else {
				this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.update");
			}
			
			try {
				this.json.put("data", dto.toJSONObject());
			} catch (Exception e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
			}
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
		}
	}
	
	protected void beforeSave(ExtendedRequest request, RecordDTO dto) {
	}

	protected void afterConvert(ExtendedRequest request, RecordDTO dto) {
	}

	public void delete(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer id = request.getInteger("id", null);
		RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");
		
		if (id == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		RecordDTO dto = bo.get(id);
		
		if (dto == null || dto.getRecordDatabase() != recordDatabase) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}

		if (recordDatabase == null) {
			ValidationException ex = new ValidationException("cataloging.error.invalid_database");
			this.setMessage(ex);
			return;
		}
		
		if (recordDatabase == RecordDatabase.PRIVATE) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}		
		
		dto.setModifiedBy(request.getLoggedUserId());
		boolean success = false;
		if (recordDatabase == RecordDatabase.TRASH) {
			success = bo.delete(dto);
		} else {
			Set<Integer> ids = new TreeSet<Integer>();
			ids.add(dto.getId());
			success = bo.moveRecords(ids, request.getLoggedUserId(), RecordDatabase.TRASH);
		}		
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.delete");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.delete");
		}
		
	}
	
	public void convert(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		String data = request.getString("data");
		RecordConvertion from = request.getEnum(RecordConvertion.class, "from");
		RecordConvertion to = request.getEnum(RecordConvertion.class, "to");
		MaterialType materialType = request.getEnum(MaterialType.class, "material_type", this.defaultMaterialType);
		Integer id = request.getInteger("id");
		
		RecordStatus status = (id == 0) ? RecordStatus.NEW : RecordStatus.CORRECTED;
		
		RecordDTO dto = this.createRecordDTO(request);
		dto.setMaterialType(materialType);
		
		Record record = null;
		try {
			switch (from) {
				case MARC:
				case RECORD:
				case HOLDING_MARC:
					record = MarcUtils.marcToRecord(data, dto.getMaterialType(), status);	
					break;
				case FORM:
				case HOLDING_FORM:
					record = MarcUtils.jsonToRecord(new JSONObject(data), dto.getMaterialType(), status);
					break;
			}
			if (record != null) {
				MarcUtils.recordToIso2709(record);	
			}
			switch (to) {
				case MARC:
				case HOLDING_MARC:
					dto.setMarc(MarcUtils.recordToMarc(record));
					break;
				case FORM:
				case HOLDING_FORM:
					dto.setJson(MarcUtils.recordToJson(record));
					break;
				case RECORD:
					MarcDataReader marcDataReader = new MarcDataReader(record);
					List<BriefTabFieldFormatDTO> formats = Fields.getBriefFormats(schema, this.recordType);
					List<BriefTabFieldDTO> fields = marcDataReader.getFieldList(formats);

					dto.setFields(fields);
					dto.setAttachments(marcDataReader.getAttachments());
					dto.setMarc(MarcUtils.recordToMarc(record));
					break;
			}
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;			
		}

		this.afterConvert(request, dto);
		
		try {
			this.json.put("data", dto.toJSONObject());
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void itemCount(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");

		if (recordDatabase == null) {
			ValidationException ex = new ValidationException("cataloging.error.invalid_database");
			this.setMessage(ex);
			return;
		}
				
		if (recordDatabase == RecordDatabase.PRIVATE) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}		

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		SearchQueryDTO query = new SearchQueryDTO(recordDatabase);
		SearchDTO dto = new SearchDTO(this.recordType);
		dto.setQuery(query);
		
		int count = bo.count(dto);
		
		try {
			this.json.put("count", count);
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void moveRecords(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		String[] idList = request.getString("id_list").split(",");
		RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		
		if (recordDatabase == null) {
			ValidationException ex = new ValidationException("cataloging.error.invalid_database");
			this.setMessage(ex);
			return;
		}

		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = 0; i < idList.length; i++) {
			ids.add(Integer.valueOf(idList[i]));
		}

		if (recordDatabase == RecordDatabase.PRIVATE || bo.listContainsPrivateRecord(ids)) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}
		
		if (bo.moveRecords(ids, request.getLoggedUserId(), recordDatabase)) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.move");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.move");
		}
	}
	
	public void exportRecords(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String exportId = UUID.randomUUID().toString();
		String idList = request.getString("id_list");
		request.setSessionAttribute(schema, exportId, idList);
		
		try {
			this.json.put("uuid", exportId);
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	//http://localhost:8080/Biblivre5/?controller=download&module=cataloging.export&action=download_export&id={export_id}
	public void downloadExport(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String exportId = request.getString("id");
		String idList = (String)request.getSessionAttribute(schema, exportId);
		
		String[] idArray = idList.split(",");
		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = 0; i < idArray.length; i++) {
			ids.add(Integer.valueOf(idArray[i]));
		}

		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		
		if (bo.listContainsPrivateRecord(ids)) {
			this.authorize(request, "cataloging.bibliographic", "private_database_access");
		}
		
		final DiskFile exportFile = bo.createExportFile(ids);

		this.setFile(exportFile);
		
		this.setCallback(new HttpCallback() {
			@Override
			public void success() {
				try {
					exportFile.delete();
				} catch (Exception e) {}
			}
		});
	}

	public void autocomplete(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String query = request.getString("q");
		String datafield = request.getString("datafield", "000");
		String subfield = request.getString("subfield", "a");
		AutocompleteType type = request.getEnum(AutocompleteType.class, "type", AutocompleteType.DISABLED);

		if (StringUtils.isBlank(query)) {
			return;
		}

		switch (type) {
			case FIXED_TABLE:
			case FIXED_TABLE_WITH_PREVIOUS_VALUES:
			case PREVIOUS_VALUES: {
				RecordBO bo = RecordBO.getInstance(schema, this.recordType);

				List<String> list = bo.phraseAutocomplete(datafield, subfield, query);
				
				try {
					for (String term : list) {
						this.json.append("data", term);
					}
				} catch (JSONException e) {
					
				}
				
				break;

			}
			case BIBLIO:
			case AUTHORITIES:{
				RecordBO bo = RecordBO.getInstance(schema, RecordType.fromString(type.toString()));

				DTOCollection<AutocompleteDTO> list = bo.recordAutocomplete(datafield, subfield, query);
				
				try {
					this.json.putOpt("data", list.toJSONObject());
				} catch (JSONException e) {
					
				}
				
				break;
			}
			
			case VOCABULARY: {
				RecordBO bo = RecordBO.getInstance(schema, RecordType.fromString(type.toString()));

//				List<String> list = bo.phraseAutocomplete("150", "a", query);
//				
//				try {
//					for (String term : list) {
//						this.json.append("data", term);
//					}
//				} catch (JSONException e) {
//					
//				}	
				
				DTOCollection<AutocompleteDTO> list = bo.recordAutocomplete("150", "a", query);
				
				try {
					this.json.putOpt("data", list.toJSONObject());
				} catch (JSONException e) {
					
				}
			}
			
			default:
		}
	}

	public void addAttachment(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer recordId = request.getInteger("id");
		String uri = request.getString("uri");
		String description = request.getString("description");
		Integer userId = request.getLoggedUserId();
		
		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		RecordDTO dto = bo.addAttachment(recordId, uri, description, userId);
		
		MarcDataReader marcDataReader = new MarcDataReader(dto.getRecord());
		dto.setAttachments(marcDataReader.getAttachments());
	}
	
	public void removeAttachment(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		Integer recordId = request.getInteger("id");
		String uri = request.getString("uri");
		String description = request.getString("description");
		Integer userId = request.getLoggedUserId();
		
		RecordBO bo = RecordBO.getInstance(schema, this.recordType);
		bo.removeAttachment(recordId, uri, description, userId);
	}
	
	public void listBriefFormats(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		// Record tab
		List<BriefTabFieldFormatDTO> formats = Fields.getBriefFormats(schema, this.recordType);
		DTOCollection<BriefTabFieldFormatDTO> list = new DTOCollection<BriefTabFieldFormatDTO>();
		list.addAll(formats);
		try {
			this.json.put("data", list.toJSONObject());
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	protected abstract RecordDTO createRecordDTO(ExtendedRequest request);
	
}
