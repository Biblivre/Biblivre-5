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
package biblivre.administration.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.HoldingCreationByDateReportDto;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class HoldingCreationByDatetReport extends BaseBiblivreReport {

	private static Map<String, Integer> userTotal;

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		ReportsDAO dao = ReportsDAO.getInstance(this.getSchema());
		String initialDate = this.dateFormat.format(dto.getInitialDate());
		String finalDate = this.dateFormat.format(dto.getFinalDate());
		return dao.getHoldingCreationByDateReportData(initialDate, finalDate);
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		HoldingCreationByDateReportDto dto = (HoldingCreationByDateReportDto)reportData;
		userTotal = new HashMap<String, Integer>();
		Paragraph p1 = new Paragraph(this.getText("administration.reports.title.holdings_creation_by_date"));
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		document.add(new Phrase("\n"));
		String dateSpan = this.getText("administration.reports.field.date_from") + " " + dto.getInitialDate() + " " + this.getText("administration.reports.field.date_to")+ " " + dto.getFinalDate();
		Paragraph p2 = new Paragraph(this.getHeaderChunk(dateSpan));
		p2.setAlignment(Element.ALIGN_LEFT);
		document.add(p2);
		document.add(new Phrase("\n"));

		if (dto.getData() != null) {
			PdfPTable table = createTable(dto.getData());
			document.add(table);
			document.add(new Phrase("\n"));
		}

		if (userTotal.size() > 0) {
			Paragraph p3 = new Paragraph(this.getHeaderChunk(this.getText("administration.reports.title.user_creation_count")));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			PdfPTable table = new PdfPTable(2);
			PdfPCell cell;
			cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_name"))));
			cell.setBackgroundColor(this.headerBgColor);
			cell.setBorderWidth(this.headerBorderWidth);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.total"))));
			cell.setBackgroundColor(this.headerBgColor);
			cell.setBorderWidth(this.headerBorderWidth);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			for (String name : userTotal.keySet()) {
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(name)));
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(userTotal.get(name)))));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
			}
			document.add(table);
			document.add(new Phrase("\n"));
		}

		//Database totals table
		Paragraph p3 = new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.database_count")));
		p3.setAlignment(Element.ALIGN_CENTER);
		document.add(p3);
		document.add(new Phrase("\n"));
		PdfPTable table = new PdfPTable(3);
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.database_count"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.biblio"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.holdings_count"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.database_main"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		String biblio = dto.getTotalBiblioMain();
		biblio = biblio != null ? biblio : "";
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(biblio)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		String tombos = dto.getTotalHoldingMain();
		tombos = tombos != null ? tombos : "";
		PdfPCell cell2 = new PdfPCell(new Paragraph(this.getNormalChunk(tombos)));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell2);

		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.database_work"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		biblio = dto.getTotalBiblioWork();
		biblio = biblio != null ? biblio : "";
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(biblio)));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		tombos = dto.getTotalHoldingWork();
		tombos = tombos != null ? tombos : "";
		cell2 = new PdfPCell(new Paragraph(this.getNormalChunk(tombos)));
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell2);

		document.add(table);

	}

	private PdfPTable createTable(List<String[]> dataList) {
		PdfPTable table = new PdfPTable(4);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		createHeader(table);
		PdfPCell cell;
		for (String[] data : dataList) {
			String name = data[1];
			int total = Integer.valueOf(data[2]);
			if (userTotal.containsKey(name)) {
				userTotal.put(name, userTotal.get(name) + total);
			} else {
				userTotal.put(name, total);
			}
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[0])));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(name)));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(total))));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		return table;
	}

	private void createHeader(PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.date"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_name"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.total"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}

}
