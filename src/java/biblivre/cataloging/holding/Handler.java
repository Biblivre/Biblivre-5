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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.marc4j.marc.Record;

import biblivre.cataloging.CatalogingHandler;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;

public class Handler extends CatalogingHandler {
	
	public Handler() {
		super(RecordType.HOLDING, MaterialType.HOLDINGS);
	}
	
	@Override
	protected RecordDTO createRecordDTO(ExtendedRequest request) {
		return new HoldingDTO();
	}

	@Override
	public void search(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", false);
		} catch (JSONException e) {}
	}
	
	@Override
	public void paginate(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", false);
		} catch (JSONException e) {}
	}
	
	@Override
	public void itemCount(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", false);
		} catch (JSONException e) {}
	}

	@Override
	public void moveRecords(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", false);
		} catch (JSONException e) {}
	}
	
	@Override
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
		
		Record record = MarcUtils.iso2709ToRecord(dto.getIso2709());
		dto.setRecord(record);

		// Marc tab
		dto.setMarc(MarcUtils.recordToMarc(record));

		// Form tab
		dto.setJson(MarcUtils.recordToJson(record));

		MarcDataReader marcDataReader = new MarcDataReader(dto.getRecord());
		String holdingLocation = marcDataReader.getShelfLocation();

		if (StringUtils.isBlank(holdingLocation)) {
			RecordBO parentBO = RecordBO.getInstance(schema, RecordType.BIBLIO);
			RecordDTO parent = parentBO.get(((HoldingDTO) dto).getRecordId()); 
			
			marcDataReader = new MarcDataReader(MarcUtils.iso2709ToRecord(parent.getIso2709()));
			holdingLocation = marcDataReader.getShelfLocation();
		}
		
		((HoldingDTO) dto).setShelfLocation(holdingLocation);
	
		try {
			this.json.put("data", dto.toJSONObject());
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}

	public void list(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int recordId = request.getInteger("record_id");

		HoldingBO bo = HoldingBO.getInstance(schema);
		DTOCollection<HoldingDTO> list = bo.list(recordId);

		try {
			this.json.put("list", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}
	}
	
	@Override
	public void delete(ExtendedRequest request, ExtendedResponse response) {
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

		boolean success = bo.delete(dto);
		
		if (success) {
			this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.delete");
		} else {
			this.setMessage(ActionResult.WARNING, "cataloging.record.error.delete");
		}
		
	}

	
	@Override
	protected void beforeSave(ExtendedRequest request, RecordDTO dto) {
		if (dto == null || !(dto instanceof HoldingDTO)) {
			throw new ValidationException("cataloging.error.invalid_data");
		}

		int recordId = request.getInteger("record_id");
		HoldingAvailability availability = request.getEnum(HoldingAvailability.class, "availability", HoldingAvailability.AVAILABLE);

		HoldingDTO hdto = (HoldingDTO)dto;
		hdto.setRecordId(recordId);
		hdto.setAvailability(availability);
	}

	public void createAutomaticHolding(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		Integer recordId = request.getInteger("record_id", null);
		
		if (recordId == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}

		RecordBO rbo = RecordBO.getInstance(schema, RecordType.BIBLIO);
		RecordDTO rdto = rbo.get(recordId);
		
		if (rdto == null) {
			this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
			return;
		}

		AutomaticHoldingDTO autoDto = this.createAutomaticHoldingDto(request);
		autoDto.setBiblioRecordDto(rdto);
		
        HoldingBO hbo = HoldingBO.getInstance(schema);
        
        if (autoDto.getHoldingCount() <= 0) {
        	this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
        	return;
        }

        if (hbo.createAutomaticHolding(autoDto)) {
        	this.list(request, response);
        } else {
        	this.setMessage(ActionResult.WARNING, "cataloging.record.error.save");
        }
		
	}
	
	private AutomaticHoldingDTO createAutomaticHoldingDto(ExtendedRequest request) {
		AutomaticHoldingDTO dto = new AutomaticHoldingDTO();
		
		dto.setHoldingCount(request.getInteger("holding_count", 1));
		dto.setIssueNumber(request.getInteger("holding_volume_number", 0));
		dto.setNumberOfIssues(request.getInteger("holding_volume_count", 1));
		dto.setLibraryName(request.getString("holding_library"));
		dto.setAcquisitionType(request.getString("holding_acquisition_type"));
		dto.setAcquisitionDate(request.getString("holding_acquisition_date"));
		dto.setDatabase(request.getEnum(RecordDatabase.class, "database"));
		dto.setCreatedBy(request.getLoggedUserId());
		
		return dto;
	}
}
