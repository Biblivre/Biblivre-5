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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import biblivre.administration.reports.dto.BaseReportDto;
import biblivre.administration.reports.dto.UserReportDto;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingInfoDTO;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserFieldDTO;
import biblivre.circulation.user.UserFields;
import biblivre.core.JavascriptCacheableList;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;


public class UserReport extends BaseBiblivreReport {
	
	public static final DateFormat dd_MM_yyyy = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected BaseReportDto getReportData(ReportsDTO dto) {
		UserReportDto urdto = new UserReportDto();
		UserBO ubo = UserBO.getInstance(this.getSchema());
		Integer userId = Integer.valueOf(dto.getUserId());
		
		UserDTO user = ubo.get(userId);
		urdto.setUser(user);

		LendingBO lbo = LendingBO.getInstance(this.getSchema());
		LendingFineBO lfbo = LendingFineBO.getInstance(this.getSchema());
		
		List<LendingDTO> history = lbo.listHistory(user);
		
		List<LendingInfoDTO> historyInfo = lbo.populateLendingInfo(history);
		List<String[]> returnedLendings = new ArrayList<String[]>();
		for (LendingInfoDTO lidto : historyInfo) {
			String[] data = new String[3];
			data[0] = dd_MM_yyyy.format(lidto.getLending().getCreated());
			data[1] = lidto.getBiblio().getTitle();
			data[2] = lidto.getBiblio().getAuthor();
			returnedLendings.add(data);
		}
		urdto.setReturnedLendings(returnedLendings);

		List<String[]> currentLendings = new ArrayList<String[]>();
		List<String[]> lateLendings = new ArrayList<String[]>();
		
		List<LendingDTO> currentLendingsList = lbo.listUserLendings(user);
		List<LendingInfoDTO> currentLendingsInfo = lbo.populateLendingInfo(currentLendingsList);
		for (LendingInfoDTO lidto : currentLendingsInfo) {
			String[] data = new String[3];
			data[0] = dd_MM_yyyy.format(lidto.getLending().getCreated());
			data[1] = lidto.getBiblio().getTitle();
			data[2] = lidto.getBiblio().getAuthor();
			if (lfbo.isLateReturn(lidto.getLending())) {
				lateLendings.add(data);
			} else {
				currentLendings.add(data);
			}
		}
		urdto.setLendings(currentLendings);
		urdto.setLateLendings(lateLendings);

		return urdto;
	}

	@Override
	protected void generateReportBody(Document document, BaseReportDto reportData) throws Exception {
		UserReportDto dto = (UserReportDto)reportData;
		Paragraph p1 = new Paragraph(this.getText("administration.reports.title.user"));
		p1.setAlignment(Element.ALIGN_CENTER);
		document.add(p1);
		document.add(new Phrase("\n"));

		PdfPTable dataTable = createUserDataTable(dto.getUser());
		document.add(dataTable);

		Paragraph p2 = new Paragraph(this.getText("administration.reports.field.user_data"));
		p2.setAlignment(Element.ALIGN_CENTER);
		document.add(p2);
		document.add(new Phrase("\n"));
		document.add(createDateTable(dto.getUser()));

		Paragraph p3 = null;
		if (dto.getLendings() != null && dto.getLendings().size() > 0) {
			document.add(new Phrase("\n"));
			p3 = new Paragraph(this.getText("administration.reports.field.user_lendings"));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			document.add(createLendingsTable(dto.getLendings()));
		}

		if (dto.getLateLendings() != null && dto.getLateLendings().size() > 0) {
			document.add(new Phrase("\n"));
			p3 = new Paragraph(this.getText("administration.reports.field.user_late_lendings"));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			document.add(createLendingsTable(dto.getLateLendings()));
		}

		if (dto.getReturnedLendings() != null && dto.getReturnedLendings().size() > 0) {
			document.add(new Phrase("\n"));
			p3 = new Paragraph(this.getText("administration.reports.field.user_returned_lendings"));
			p3.setAlignment(Element.ALIGN_CENTER);
			document.add(p3);
			document.add(new Phrase("\n"));
			document.add(createLendingsTable(dto.getReturnedLendings()));
		}
	}


	private PdfPTable createLendingsTable(List<String[]> lendings) {
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);

		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.date"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.title"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.author"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setColspan(2);
		cell.setBorderWidth(this.headerBorderWidth);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		for (String[] lending : lendings) {
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[0])));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[1])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(lending[2])));
			cell.setColspan(2);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		return table;
	}

	private PdfPTable createUserDataTable(UserDTO user) {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		PdfPCell cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_name"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getName())));
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_id"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(String.valueOf(user.getId()))));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_signup"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		Date signupDate = user.getCreated();
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(signupDate != null ? this.dateFormat.format(signupDate) : "")));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_type"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		UserTypeBO utbo = UserTypeBO.getInstance(this.getSchema());
		UserTypeDTO usdto = utbo.get(user.getType());
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(usdto.getDescription())));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("administration.reports.field.user_status"))));
		cell.setBackgroundColor(this.headerBgColor);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);
		cell = new PdfPCell(new Paragraph(this.getNormalChunk(this.getText("administration.reports.field.user_status." + user.getStatus().toString()))));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(cell);

		return table;
	}

	private PdfPTable createDateTable(UserDTO user) {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100f);
		PdfPCell cell;
		JavascriptCacheableList<UserFieldDTO> fields = UserFields.getFields(this.getSchema());

		for (UserFieldDTO field : fields) {
			String fieldKey = field.getKey();
			cell = new PdfPCell(new Paragraph(this.getHeaderChunk(this.getText("circulation.custom.user_field." + fieldKey))));
			cell.setBackgroundColor(this.headerBgColor);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(this.getNormalChunk(user.getFields().get(fieldKey))));
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}		

		return table;
	}
	
}
