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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.HoldingAvailability;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.labels.LabelDTO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.LabelPrintDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class HoldingBO extends RecordBO {

	private HoldingDAO dao;
	
	public static HoldingBO getInstance(String schema) {
		HoldingBO bo = AbstractBO.getInstance(HoldingBO.class, schema);

		if (bo.dao == null) {
			bo.dao = HoldingDAO.getInstance(schema);
		}

		return bo;
	}

	@Override
	public Map<Integer, RecordDTO> map(Set<Integer> ids) {
		return this.dao.map(ids);
	}

	@Override
	public Map<Integer, RecordDTO> map(Set<Integer> ids, int mask) {
		Map<Integer, RecordDTO> map = this.dao.map(ids);
		
		for (RecordDTO dto : map.values()) {
			this.populateDetails(dto, mask);
		}
		
		return map;
	}

	@Override
	public List<RecordDTO> list(int offset, int limit) {
		return this.dao.list(offset, limit);
	}

	@Override
	public Integer count() {
		return this.count(0);
	}
	
	public Integer count(int recordId) {
		return this.dao.count(recordId, false);
	}
	
	public Integer countAvailableHoldings(int recordId) {
		return this.dao.count(recordId, true);
	}
	
	public void markAsPrinted(Set<Integer> ids) {
		this.dao.markAsPrinted(ids);
	}
	
	public String getNextAccessionNumber() {
		String prefix = Configurations.getString(this.getSchema(), Constants.CONFIG_ACCESSION_NUMBER_PREFIX);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		
		String accessionPrefix = prefix + "." + year + ".";
		return accessionPrefix + this.dao.getNextAccessionNumber(accessionPrefix);
	}
	
	public boolean isAccessionNumberAvailable(String accessionNumber, int holdingSerial) {
		return this.dao.isAccessionNumberAvailable(accessionNumber, holdingSerial);
	}

	public boolean isAccessionNumberAvailable(String accessionNumber) {
		return this.isAccessionNumberAvailable(accessionNumber, 0);
	}

	public DTOCollection<HoldingDTO> list(int recordId) {
		return this.dao.list(recordId);
	}
	
	public HoldingDTO getByAccessionNumber(String accessionNumber) {
		return this.dao.getByAccessionNumber(accessionNumber);
	}
	
	public DTOCollection<HoldingDTO> search(String query, RecordDatabase database, boolean lentOnly, int offset, int limit) {
		DTOCollection<HoldingDTO> searchResults = this.dao.search(query, database, lentOnly, offset, limit); 
		for (HoldingDTO holding : searchResults) {
			MarcDataReader reader = new MarcDataReader(holding.getRecord());
			holding.setShelfLocation(reader.getShelfLocation());
		}
		return searchResults;
	}
	
	@Override
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}
	
	@Override
	public boolean save(RecordDTO dto) {
		HoldingDTO hdto = (HoldingDTO) dto;
		Record record = hdto.getRecord();
		MarcDataReader marcReader = new MarcDataReader(record);

		String accessionNumber = marcReader.getAccessionNumber();
		String holdingLocation = marcReader.getHoldingLocation();

		if (StringUtils.isBlank(accessionNumber)) {
			accessionNumber = this.getNextAccessionNumber();
			MarcUtils.setAccessionNumber(record, accessionNumber);

		} else if (!this.isAccessionNumberAvailable(accessionNumber)) {
			throw new ValidationException("cataloging.holding.error.accession_number_unavailable");
		}

		Integer id = this.dao.getNextSerial("biblio_holdings_id_seq");

		MarcUtils.setCF001(record, id);
		MarcUtils.setCF004(record, hdto.getRecordId());
		MarcUtils.setCF005(record);

		// Availability and RecordId are already populated at this point.
		hdto.setId(id);
		hdto.setAccessionNumber(accessionNumber);
		hdto.setLocationD(holdingLocation);

		String iso2709 = MarcUtils.recordToIso2709(record);
		dto.setIso2709(iso2709);

		boolean success = this.dao.save(dto);
		
		if (success) {
			//UPDATE holding_creation_counter
			try {
				UserDTO udto = UserBO.getInstance(this.getSchema()).getUserByLoginId(dto.getCreatedBy());
				LoginDTO ldto = LoginBO.getInstance(this.getSchema()).get(dto.getCreatedBy());
				this.dao.updateHoldingCreationCounter(udto, ldto);
			} catch (Exception e) {
				this.logger.error(e);
			}
		}
		
		return success;
	}	
	
	@Override
	public boolean update(RecordDTO dto) {
		HoldingDTO hdto = (HoldingDTO) dto;
		Record record = hdto.getRecord();
		MarcDataReader marcReader = new MarcDataReader(record);

		String accessionNumber = marcReader.getAccessionNumber();
		String holdingLocation = marcReader.getHoldingLocation();

		if (StringUtils.isBlank(accessionNumber)) {
			accessionNumber = this.getNextAccessionNumber();
			MarcUtils.setAccessionNumber(record, accessionNumber);

		} else if (!this.isAccessionNumberAvailable(accessionNumber, hdto.getId())) {
			throw new ValidationException("cataloging.holding.error.accession_number_unavailable");
		}

		MarcUtils.setCF004(record, hdto.getRecordId());
		MarcUtils.setCF005(record);

		// Id, Availability and RecordId are already populated at this point.
		hdto.setAccessionNumber(accessionNumber);
		hdto.setLocationD(holdingLocation);

		String iso2709 = MarcUtils.recordToIso2709(record);
		dto.setIso2709(iso2709);

		return this.dao.update(dto);
	}

	@Override
	public boolean delete(RecordDTO dto) {

		//TODO AVISAR O USER, SE O USER CONFIRMAR DELEÇÃO, DELETE CASCADE
//		HoldingBO holdingBo = new HoldingBO();
//		LendingBO lendingBo = new LendingBO();
//		List<HoldingDTO> holdings = holdingBo.list(record);
//		for (HoldingDTO holding : holdings) {
//			if (lendingBo.isLent(holding) || lendingBo.wasLent(holding)) {
//				throw new RuntimeException("MESSAGE_DELETE_BIBLIO_ERROR");
//			}
//		}
		return this.dao.delete(dto);
	}

	// If a holding was ever lent, the user shouldn't delete it. The correct way
	// is setting it's availability to false. If the user wants to delete it anyway,
	// he must use the force delete function.
	@Override
	public boolean isDeleatable(HoldingDTO holding) throws ValidationException {
		LendingBO lbo = LendingBO.getInstance(this.getSchema());

		if (lbo.isLent(holding) || lbo.wasEverLent(holding)) {
			throw new ValidationException("cataloging.holding.error.shouldnt_delete_because_holding_is_or_was_lent");
		}
		
		return true;
	}

	@Override
	public void populateDetails(RecordDTO rdto, int mask) {
		
		if ((mask & RecordBO.MARC_INFO) != 0) {
			MarcDataReader reader = new MarcDataReader(rdto.getRecord());
			((HoldingDTO)rdto).setShelfLocation(reader.getShelfLocation());
		}
		return;
	}
	
	public boolean paginateHoldingSearch(SearchDTO search) {
		Map<Integer, Integer> groupCount = this.dao.countSearchResults(search);
		Integer count = groupCount.get(search.getIndexingGroup());

		if (count == null || count == 0) {
			return false;
		}

		List<RecordDTO> list = this.dao.getSearchResults(search);

		search.getPaging().setRecordCount(count);
		search.setIndexingGroupCount(groupCount);

		for (RecordDTO rdto : list) {
			this.populateDetails(rdto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
			search.add(rdto);
		}

		return true;
	}
	
	public DiskFile printLabelsToPDF(List<LabelDTO> labels, LabelPrintDTO printDTO) {
		Document document = new Document();

		try {
			File file = File.createTempFile("biblivre_label_", ".pdf");

			FileOutputStream fos = new FileOutputStream(file);
			PdfWriter writer = PdfWriter.getInstance(document, fos);

			document.setPageSize(PageSize.A4);
			float verticalMargin = (297.0f - (printDTO.getHeight() * printDTO.getRows())) / 2;

			document.setMargins(7.15f * Constants.MM_UNIT, 7.15f * Constants.MM_UNIT, verticalMargin * Constants.MM_UNIT, verticalMargin * Constants.MM_UNIT);
			document.open();

			PdfPTable table = new PdfPTable(printDTO.getColumns());
			table.setWidthPercentage(100f);
			PdfPCell cell;

			int i = 0;
			for (i = 0; i < printDTO.getOffset(); i++) {
				cell = new PdfPCell();
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setFixedHeight(printDTO.getHeight() * Constants.MM_UNIT);
				table.addCell(cell);
			}

			for (LabelDTO ldto : labels) {
				PdfContentByte cb = writer.getDirectContent();

				String holdingSerial = String.valueOf(ldto.getId());
				while (holdingSerial.length() < 10) {
					holdingSerial = "0" + holdingSerial;
				}
				Barcode39 code39 = new Barcode39();
				code39.setExtended(true);
				code39.setCode(holdingSerial);
				code39.setStartStopText(false);

				Image image39 = code39.createImageWithBarcode(cb, null, null);
				if (printDTO.getHeight() > 30.0f) {
					image39.scalePercent(110f);
				} else {
					image39.scalePercent(90f);
				}

				Paragraph para = new Paragraph();
				Phrase p1 = new Phrase(StringUtils.left(ldto.getAuthor(), 28) + "\n");
				Phrase p2 = new Phrase(StringUtils.left(ldto.getTitle(), 28) + "\n\n");
				Phrase p3 = new Phrase(new Chunk(image39, 0, 0));
				para.add(p1);
				para.add(p2);
				para.add(p3);

				cell = new PdfPCell(para);
				i++;
				cell.setNoWrap(true);
				cell.setFixedHeight(printDTO.getHeight() * Constants.MM_UNIT);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);

				Paragraph para2 = new Paragraph();
				Phrase p5 = new Phrase(ldto.getLocationA() + "\n");
				Phrase p6 = new Phrase(ldto.getLocationB() + "\n");
				Phrase p7 = new Phrase(ldto.getLocationC() + "\n");
				Phrase p8 = new Phrase(ldto.getLocationD() + "\n");
				Phrase p4 = new Phrase(ldto.getAccessionNumber() + "\n");
				para2.add(p5);
				para2.add(p6);
				para2.add(p7);
				para2.add(p8);
				para2.add(p4);

				cell = new PdfPCell(para2);
				i++;
				cell.setFixedHeight(printDTO.getHeight() * Constants.MM_UNIT);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);
			}
			
			if ((i % printDTO.getColumns()) != 0) {
				while ((i % printDTO.getColumns()) != 0) {
					i++;
					cell = new PdfPCell();
					cell.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell);
				}
			}

			document.add(table);
			writer.flush();
			document.close();
			fos.close();

			return new DiskFile(file, "application/pdf");
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean createAutomaticHolding(AutomaticHoldingDTO autoDto) {

		int holdingCount = autoDto.getHoldingCount();
		int volumeNumber = autoDto.getIssueNumber();
		int volumeCount = autoDto.getNumberOfIssues();
		String library = autoDto.getLibraryName();
		String acquisitionType = autoDto.getAcquisitionType();
		String acquisitionDate = autoDto.getAcquisitionDate();

		String[] notes = new String[3];
		notes[0] = 'a' + library;
		notes[1] = 'c' + acquisitionType;
		notes[2] = 'd' + acquisitionDate;

		boolean success = true;

		RecordDTO biblioDto = autoDto.getBiblioRecordDto();
		if (biblioDto.getRecord() == null) {
			biblioDto.setRecord(MarcUtils.iso2709ToRecord(biblioDto.getIso2709()));
		}
		MarcDataReader mdr = new MarcDataReader(biblioDto.getRecord());

		String biblioLocationA = mdr.getLocation();
		biblioLocationA = StringUtils.defaultString(biblioLocationA);
		String biblioLocationB = mdr.getLocationB();
		biblioLocationB = StringUtils.defaultString(biblioLocationB);
		String biblioLocationC = mdr.getLocationC();
		biblioLocationC = StringUtils.defaultString(biblioLocationC);

		for (int j = 0; j < volumeCount; j++) {
			for (int i = 0; i < holdingCount; i++) {

				String[] location = new String[4];

				location[0] = "a" + biblioLocationA;
				location[1] = "b" + biblioLocationB;
				location[2] = "c";

				if (StringUtils.isNotBlank(biblioLocationC)) {
					location[2] += biblioLocationC;
				} else {
					if (volumeNumber != 0) {
						location[2] += "v." + volumeNumber;
					} else if (volumeCount > 1) {
						location[2] += "v." + (j + 1);
					}
				}

				location[3] = 'd' + "ex." + (i + 1);

				Record holding = this.createHoldingMarcRecord(location, notes);
				MarcUtils.setAccessionNumber(holding, this.getNextAccessionNumber());

				success &= this.save(this.createHoldingDto(holding, autoDto));
			}
		}

		return success;

	}

	private Record createHoldingMarcRecord(String[] location, String[] notes) {
		MarcFactory factory = MarcFactory.newInstance();
		Record record = factory.newRecord();

		DataField df = factory.newDataField("090", '_', '_');
		record.addVariableField(df);
		for (int i = 0; i < 4; i++) {
			if (StringUtils.isNotBlank(location[i])) {
				final char code = location[i].charAt(0);
				Subfield subfield = factory.newSubfield(code, location[i].substring(1));
				df.addSubfield(subfield);
			}
		}

		DataField df1 = factory.newDataField("541", '_', '_');
		record.addVariableField(df1);
		for (int i = 0; i < 3; i++) {
			if (StringUtils.isNotBlank(notes[i])) {
				final char code = notes[i].charAt(0);
				Subfield subfield = factory.newSubfield(code, notes[i].substring(1));
				df1.addSubfield(subfield);
			}
		}

		record.setLeader(MarcUtils.createBasicLeader(MaterialType.HOLDINGS, RecordStatus.NEW));

		return record;
	}

	private HoldingDTO createHoldingDto(Record record, AutomaticHoldingDTO autoDto) {
		HoldingDTO dto = new HoldingDTO();

		dto.setRecord(record);
		dto.setAvailability(HoldingAvailability.AVAILABLE);
		dto.setRecordDatabase(autoDto.getDatabase());
		dto.setRecordId(autoDto.getBiblioRecordDto().getId());
		dto.setCreatedBy(autoDto.getCreatedBy());

		return dto;
	}


	
	@Override
	public Integer count(SearchDTO search) {
		throw new RuntimeException("error.invalid_method_call");
	}
	
	@Override
	public List<RecordDTO> listByLetter(char letter, int order) {
		throw new RuntimeException("error.invalid_method_call");
	}

	@Override
	public boolean moveRecords(Set<Integer> ids, int modifiedBy, RecordDatabase database) {
		throw new RuntimeException("error.invalid_method_call");
	}	

	@Override
	public boolean search(SearchDTO search) {
		throw new RuntimeException("error.invalid_method_call");
	}

	@Override
	public boolean paginateSearch(SearchDTO search) {
		throw new RuntimeException("error.invalid_method_call");
	}

	@Override
	public SearchDTO getSearch(Integer searchId) {
		throw new RuntimeException("error.invalid_method_call");
	}
}
