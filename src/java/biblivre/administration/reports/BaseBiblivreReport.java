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

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public abstract class BaseBiblivreReport extends PdfPageEventHelper implements IBiblivreReport {

	protected final Logger logger = Logger.getLogger(this.getClass().getName());
	protected final Float headerBorderWidth = 0.8f;
	protected final Float smallFontSize = 8f;
	protected final Float reportFontSize = 10f;
	protected final Float pageNumberFontSize = 8f;
	protected final Color headerBgColor = new Color(239, 239, 239);
	protected final Font smallFont = FontFactory.getFont("Arial", this.smallFontSize, Font.NORMAL, Color.BLACK);
	protected final Font textFont = FontFactory.getFont("Arial", this.reportFontSize, Font.NORMAL, Color.BLACK);
	protected final Font boldFont = FontFactory.getFont("Arial", this.smallFontSize, Font.BOLD, Color.BLACK);
	protected final Font headerFont = FontFactory.getFont("Arial", this.reportFontSize, Font.BOLD, Color.BLACK);
	protected final Font footerFont = FontFactory.getFont(FontFactory.COURIER, this.pageNumberFontSize, Font.BOLD, Color.BLACK);
	protected TranslationsMap i18n;
	private PdfWriter writer;
	private Date generationDate;
	protected DateFormat dateFormat;
	
	protected String schema; 

	@Override
	public DiskFile generateReport(ReportsDTO dto) {
		this.generationDate = new Date();
		this.dateFormat = new SimpleDateFormat(this.getText("format.datetime"));
		BaseReportDto reportData = getReportData(dto);
		String fileName = this.getFileName(dto);
		return generateReportFile(reportData, fileName);
	}

	protected abstract BaseReportDto getReportData(ReportsDTO dto);

	protected DiskFile generateReportFile(BaseReportDto reportData, String fileName) {
		Document document = new Document(PageSize.A4);
		DiskFile report = null;
		try {
			File file = File.createTempFile(fileName, ".pdf");
			FileOutputStream out = new FileOutputStream(file);
			this.writer = PdfWriter.getInstance(document, out);
			this.writer.setPageEvent(this);
			this.writer.setFullCompression();
			document.open();
			generateReportBody(document, reportData);
			this.writer.flush();
			document.close();
			report = new DiskFile(file, "application/pdf");
			report.setName(fileName);
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return null;
		} 
		return report;
	}

	private String getFileName(ReportsDTO dto) {
		String reportName = dto.getType().getName();
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH) + 1;
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		return reportName + day + month + hour + minute + "_" + second;
	}

	protected abstract void generateReportBody(Document document, BaseReportDto reportData) throws Exception;

	@Override
	public final void setI18n(TranslationsMap i18n) {
		this.i18n = i18n;
	}

	protected final String getText(String key) {
		String[] params = key.split(":");
		
		if (params.length == 1) {
			return this.i18n.getText(key);
		}
		
		String text = this.i18n.getText(params[0]);
		for (int i = 1; i < params.length; i++) {
			String replacement = params[i];
			text = text.replace("{" + (i - 1) + "}", replacement);
		}
		return text;
	}


	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			Rectangle page = document.getPageSize();
			
			PdfPTable head = new PdfPTable(1);
			PdfPCell cell = new PdfPCell(new Paragraph(this.getText("administration.reports.biblivre_report_header")));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(Rectangle.BOTTOM);
			head.addCell(cell);
			head.setTotalWidth( (page.getWidth() / 2) - document.leftMargin());
			head.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());

			PdfPTable date = new PdfPTable(1);
			PdfPCell dateCell = new PdfPCell(new Paragraph(this.dateFormat.format(this.generationDate)));
			dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			dateCell.setVerticalAlignment(Element.ALIGN_CENTER);
			dateCell.setBorder(Rectangle.BOTTOM);
			date.addCell(dateCell);
			date.setTotalWidth( (page.getWidth() / 2) - document.rightMargin());
			date.writeSelectedRows(0, -1, (page.getWidth() / 2), page.getHeight() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());


			PdfPTable foot = new PdfPTable(1);
			Chunk pageNumber = new Chunk(String.valueOf(document.getPageNumber()));
			pageNumber.setFont(this.footerFont);
			cell = new PdfPCell(new Paragraph(pageNumber));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			cell.setBorder(Rectangle.TOP);
			foot.addCell(cell);
			foot.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
			foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
		}
		catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	protected Chunk getNormalChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(this.textFont);
		return chunk;
	}

	protected Chunk getBoldChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(this.boldFont);
		return chunk;
	}

	protected Chunk getSmallFontChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(this.smallFont);
		return chunk;
	}

	protected Chunk getHeaderChunk(String text) {
		Chunk chunk = new Chunk(StringUtils.defaultIfEmpty(text, ""));
		chunk.setFont(this.headerFont);
		return chunk;
	}

	protected PdfWriter getWriter() {
		return this.writer;
	}

	public String getSchema() {
		return this.schema;
	}

	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}
}
