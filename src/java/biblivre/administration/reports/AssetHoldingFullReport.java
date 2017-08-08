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

import biblivre.administration.reports.dto.AssetHoldingDto;
import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.core.utils.NaturalOrderComparator;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public class AssetHoldingFullReport extends BaseBiblivreReport implements Comparator<String[]> {

	private Boolean topographic;

	public AssetHoldingFullReport(Boolean topographic) {
		this.topographic = topographic;
	}

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		return ReportsDAO.getInstance(this.getSchema()).getAssetHoldingFullReportData();
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		AssetHoldingDto dto = (AssetHoldingDto)reportData;
		String title = "";
		if (this.topographic) {
			title = this.getText("administration.reports.title.topographic");
		} else {
			title = this.getText("administration.reports.title.holdings_full");
		}
		Paragraph p1 = new Paragraph(title);
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		document.add(new Phrase("\n"));
		PdfPTable table = new PdfPTable(20);
		table.setWidthPercentage(100f);
		createHeader(table);
		PdfPCell cell;
		List<String[]> dataList = dto.getData();
		Collections.sort(dataList, this);
		for (String[] data : dataList) {
			PdfContentByte cb = getWriter().getDirectContent();

			String holdingSerial = StringUtils.leftPad(data[0], 10, "0");
			Barcode39 code39 = new Barcode39();
			code39.setExtended(true);
			code39.setCode(holdingSerial);
			code39.setStartStopText(false);

			Image image39 = code39.createImageWithBarcode(cb, null, null);
			image39.scalePercent(100f);
			cell = new PdfPCell(new Paragraph(new Phrase(new Chunk(image39, 0, 0))));
			cell.setColspan(6);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getSmallFontChunk(data[1])));
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);

			Paragraph para = new Paragraph();
			para.add(new Phrase(this.getSmallFontChunk(data[2] + "\n")));
			para.add(new Phrase(this.getSmallFontChunk(data[3] + "\n")));

			if (StringUtils.isNotBlank(data[4])) {
				para.add(new Phrase(this.getBoldChunk(this.getText("administration.reports.field.location") + ": ")));
				para.add(new Phrase(this.getSmallFontChunk(data[4] + " ")));
			}

			if (StringUtils.isNotBlank(data[5])) {
				para.add(new Phrase(this.getBoldChunk(this.getText("administration.reports.field.edition") + ": ")));
				para.add(new Phrase(this.getSmallFontChunk(data[5] + " ")));
			}

			if (StringUtils.isNotBlank(data[6])) {
				para.add(new Phrase(this.getBoldChunk(this.getText("administration.reports.field.date") + ": ")));
				para.add(new Phrase(this.getSmallFontChunk(data[6])));
			}
			
			cell = new PdfPCell(para);
			cell.setColspan(11);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_TOP);
			cell.setPaddingTop(5f);
			cell.setPaddingLeft(7f);
			cell.setPaddingBottom(4f);
			table.addCell(cell);
		}
		document.add(table);
	}

	private void createHeader(PdfPTable table) {
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("administration.reports.field.id"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(6);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("administration.reports.field.accession_number"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(3);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getBoldChunk(this.getText("administration.reports.field.holdings_count"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(11);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
	}

	@Override
	public int compare(String[] o1, String[] o2) {
		if (o1 == null) {
			return -1;
		}
		
		if (o2 == null) {
			return 1;
		}

		if (this.topographic) {
			return NaturalOrderComparator.NUMERICAL_ORDER.compare(o1[4], o2[4]);
		} else {
			return NaturalOrderComparator.NUMERICAL_ORDER.compare(o1[1], o2[1]);
		}
	}
}
