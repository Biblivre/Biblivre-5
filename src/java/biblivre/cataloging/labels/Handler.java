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
package biblivre.cataloging.labels;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.marc4j.marc.Record;

import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.holding.HoldingDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.LabelPrintDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MarcUtils;

public class Handler extends AbstractHandler {

	public void createPdf(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String printId = UUID.randomUUID().toString();
		
		LabelPrintDTO print = new LabelPrintDTO();
		String idList = request.getString("id_list");

		String[] idArray = idList.split(",");
		Set<Integer> ids = new TreeSet<Integer>();
		try {
			for (int i = 0; i < idArray.length; i++) {
				ids.add(Integer.valueOf(idArray[i]));
			}			
		} catch(Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
		}
		
		print.setIds(ids);
		print.setOffset(request.getInteger("offset"));
		print.setWidth(request.getFloat("width"));
		print.setHeight(request.getFloat("height"));
		print.setColumns(request.getInteger("columns"));
		print.setRows(request.getInteger("rows"));
		
		request.setSessionAttribute(schema, printId, print);
		
		try {
			this.json.put("uuid", printId);
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void downloadPdf(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String printId = request.getString("id");
		LabelPrintDTO dto = (LabelPrintDTO) request.getSessionAttribute(schema, printId);
		
		HoldingBO hbo = HoldingBO.getInstance(schema);
		Map<Integer, RecordDTO> hdto = hbo.map(dto.getIds(), RecordBO.MARC_INFO);
		
		BiblioRecordBO biblioBo = BiblioRecordBO.getInstance(schema);
		
		List<LabelDTO> labels = new LinkedList<LabelDTO>();
		for (RecordDTO rdto : hdto.values()) {
			HoldingDTO holding = (HoldingDTO)rdto;
			
			LabelDTO label = new LabelDTO();
			
			label.setId(rdto.getId());
			label.setAccessionNumber(holding.getAccessionNumber());
			
			BiblioRecordDTO biblio = (BiblioRecordDTO) biblioBo.get(holding.getRecordId());
			Record biblioRecord = MarcUtils.iso2709ToRecord(biblio.getIso2709());
			MarcDataReader dataReader = new MarcDataReader(biblioRecord);
			
			label.setAuthor(StringUtils.defaultString(dataReader.getAuthorName(false)));
			label.setTitle(StringUtils.defaultString(dataReader.getTitle(false)));
			
			Record holdingRecord = MarcUtils.iso2709ToRecord(holding.getIso2709());
			dataReader = new MarcDataReader(holdingRecord);
			label.setLocationA(StringUtils.defaultString(dataReader.getLocation()));
			label.setLocationB(StringUtils.defaultString(dataReader.getLocationB()));
			label.setLocationC(StringUtils.defaultString(dataReader.getLocationC()));
			label.setLocationD(StringUtils.defaultString(dataReader.getLocationD()));
			
			labels.add(label);
		}
		
		final DiskFile exportFile = hbo.printLabelsToPDF(labels, dto);
		hbo.markAsPrinted(dto.getIds());

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
	
	public void printText(ExtendedRequest request, ExtendedResponse response) {
		//TODO Implementar e Melhorar
		//TODO RENAME RECORD_FILE_TXT => printText 
	}	
}
