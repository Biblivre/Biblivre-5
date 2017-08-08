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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.DeweyReportDto;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class DeweyReport extends BaseBiblivreReport implements Comparator<String[]> {

	private Integer index = 0;

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getDeweyReportData(dto.getDatabase(), dto.getDatafield(), dto.getDigits());
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		DeweyReportDto dto = (DeweyReportDto)reportData;
		Paragraph p1 = new Paragraph(this.getText("administration.reports.title.dewey"));
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		document.add(new Phrase("\n\n"));
		PdfPTable table = new PdfPTable(6);
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		createHeader(table);
		PdfPCell cell;
		int totalRecords = 0;
		int totalHoldings = 0;
		List<String[]> dataList = dto.getData();
		Collections.sort(dataList, this);
		for (String[] data : dataList) {
			if (StringUtils.isBlank(data[0])) {
				data[0] = this.getText("administration.reports.field.unclassified");
			}
			totalRecords += Integer.parseInt(data[1]);
			totalHoldings += Integer.parseInt(data[2]);
		}
		if (totalRecords > 0) {
			dataList.add(new String[]{
				this.getText("administration.reports.field.total"),
				String.valueOf(totalRecords),
				String.valueOf(totalHoldings)
			});
		}

		for (String[] data : dataList) {
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[0])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[1])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(data[2])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		document.add(table);
	}


	private void createHeader(PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.dewey"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.number_of_titles"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.number_of_holdings"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}

	@Override
	public int compare(String[] o1, String[] o2) {
		if (o1 == null || o1[this.index] == null) {
			return -1;
		}
		
		if (o2 == null || o2[this.index] == null) {
			return 1;
		}

		return o1[this.index].compareTo(o2[this.index]);
	}
}
