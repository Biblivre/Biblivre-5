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

import java.util.ArrayList;
import java.util.List;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.BibliographyReportDto;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class BibliographyReport extends BaseBiblivreReport {

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		String ids = dto.getRecordIds();
		String[] idArray = ids.split(",");
		List<Integer> idList = new ArrayList<Integer>();
		for (int i = 0; i < idArray.length; i++) {
			try {
				idList.add(Integer.valueOf(idArray[i].trim()));
			} catch (Exception e) {}
		}
		return ReportsDAO.getInstance(this.getSchema()).getBibliographyReportData(dto.getAuthorName(), idList.toArray(new Integer[]{}));
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		BibliographyReportDto dto = (BibliographyReportDto)reportData;
		Paragraph p1 = new Paragraph(this.getText("administration.reports.title.bibliography"));
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		document.add(new Phrase("\n"));
		Paragraph p2 = new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.author") + ":  " + dto.getAuthorName()));
		p2.setAlignment(Element.ALIGN_LEFT);
		document.add(p2);
		document.add(new Phrase("\n"));
		if (dto.getData() != null) {
			PdfPTable table = new PdfPTable(8);
			table.setHorizontalAlignment(Element.ALIGN_CENTER);
			createHeader(table);
			PdfPCell cell;
			for (String[] data : dto.getData()) {
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[0])));
				cell.setColspan(3);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[1])));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[2])));
				cell.setColspan(2);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[3])));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[4])));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
			}
			document.add(table);
			document.add(new Phrase("\n"));
		}
	}

	private void createHeader(PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.title"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(3);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.edition"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.editor"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.year"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.place"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}
	
}
