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

import org.apache.commons.lang3.StringUtils;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.CustomCountDto;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class CustomCountReport extends BaseBiblivreReport implements Comparator<String[]> {

	private Integer index;
	private String marcField;
	private String datafield;
	private String subfield;

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		
		this.marcField = dto.getMarcField();
		if (StringUtils.isNotBlank(this.marcField)) {
			this.datafield = this.marcField.split("\\_")[0];
			this.subfield = this.marcField.split("\\_")[1];
		}
		
		String order = "1";
		if (StringUtils.isNotBlank(dto.getCountOrder())) {
			order = dto.getCountOrder();
		}
		if (order.equals("2")) {
			this.index = 1; //field count
		} else {
			this.index = 0; //marc field
		}
		return ReportsBO.getInstance(this.getSchema()).getCustomCountData(dto);
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		CustomCountDto dto = (CustomCountDto)reportData;
		StringBuilder text = new StringBuilder();
		text.append(this.getText("administration.reports.title.custom_count") + ":\n");
		text.append(this.datafield).append(" (");
		text.append(this.getText("marc.bibliographic.datafield." + this.datafield));
		text.append(")\n$").append(this.subfield);
		text.append(" (");
		text.append(this.getText("marc.bibliographic.datafield." + this.datafield + ".subfield." + this.subfield));
		text.append(")");
		Paragraph p2 = new Paragraph(text.toString());
		p2.setAlignment(Element.ALIGN_CENTER);
		document.add(p2);
		document.add(new Phrase("\n"));
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100f);
		createHeader(table, this.getText("marc.bibliographic.datafield." + this.datafield + ".subfield." + this.subfield));
		Collections.sort(dto.getData(), this);
		PdfPCell cell;
		int total = 0;
		for (String[] data : dto.getData()) {
			cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[0])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			if (data[1] != null && StringUtils.isNumeric(data[1])) {
				total += Integer.valueOf(data[1]);
			}
			cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[1])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		
		if (total != 0) {
			cell = new PdfPCell(new Paragraph(this.getBoldChunk("Total")));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getBoldChunk(String.valueOf(total))));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		
		document.add(table);
	}

	private void createHeader(PdfPTable table, String title) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(title)));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("administration.reports.field.total"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}


	@Override
	public int compare(String[] o1, String[] o2) {
		if (o1 == null) {
			return 0;
		}
		
		if (o2 == null) {
			return 0;
		}
		
		if (o1[this.index] == null && o2[this.index] == null) {
			return 0;
		}
		
		switch (this.index) {
		case 0:
			return o1[this.index].compareTo(o2[this.index]);
		case 1:
			return (Integer.valueOf(o2[this.index]).compareTo(Integer.valueOf(o1[this.index])));
		default:
			return o1[this.index].compareTo(o2[this.index]);
		}
			
		
	}

}
