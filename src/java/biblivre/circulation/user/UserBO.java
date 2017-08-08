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
package biblivre.circulation.user;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biblivre.administration.usertype.UserTypeBO;
import biblivre.administration.usertype.UserTypeDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.LabelPrintDTO;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;

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

public class UserBO extends AbstractBO {

	private UserDAO dao;

	public static UserBO getInstance(String schema) {
		UserBO bo = AbstractBO.getInstance(UserBO.class, schema);

		if (bo.dao == null) {
			bo.dao = UserDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public DTOCollection<UserDTO> search(UserSearchDTO dto, int limit, int offset) {
		DTOCollection<UserDTO> list = this.dao.search(dto, limit, offset);
		
		UserTypeBO utbo = UserTypeBO.getInstance(this.getSchema());
		Map<Integer, UserTypeDTO> map = utbo.map();
		
		for (UserDTO udto : list) {
			UserTypeDTO utdto = map.get(udto.getType());
			
			if (utdto != null) {
				udto.setUsertypeName(utdto.getName());
			}
		}
		
		return list;
	}
	
	public UserDTO get(int id) {
		Set<Integer> ids = new HashSet<Integer>();
		ids.add(id);
		
		return this.map(ids).get(id);
	}
	
	public Map<Integer, UserDTO> map(Set<Integer> ids) {
		Map<Integer, UserDTO> map = this.dao.map(ids);
		
		UserTypeBO utbo = UserTypeBO.getInstance(this.getSchema());
		Map<Integer, UserTypeDTO> typeMap = utbo.map();
		
		for (UserDTO user : map.values()) {
			user.setUsertypeName(typeMap.get(user.getType()).getName());
		}
		
		return map;
	}
	
	public UserDTO getUserByLoginId(Integer loginId) {
		Integer userId = this.dao.getUserIdByLoginId(loginId);
		if (userId == null) {
			return null;
		}
		return this.get(userId);
	}
	
	public boolean save(UserDTO user) {
		return this.dao.save(user);
	}
	
	public boolean updateUserStatus(Integer userId, UserStatus status) {
		return this.dao.updateUserStatus(userId, status);
	}
	
	public boolean delete(UserDTO user) {
		return this.dao.delete(user);
	}
	
	public DiskFile printUserCardsToPDF(LabelPrintDTO dto, TranslationsMap i18n) {
		Document document = new Document();
		
		try {
			File file = File.createTempFile("biblivre_user_cards_", ".pdf");

			FileOutputStream fos = new FileOutputStream(file);
			PdfWriter writer = PdfWriter.getInstance(document, fos);

			document.setPageSize(PageSize.A4);
			document.setMargins(
					7.15f * Constants.MM_UNIT,
					7.15f * Constants.MM_UNIT,
					9.09f * Constants.MM_UNIT,
					9.09f * Constants.MM_UNIT
					);
			document.open();
			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100f);
			PdfPCell cell;
			int i = 0;
			int offset = dto.getOffset();
			//Fill the empty cell till the startOffset cell
			for (i = 0; i < offset; i++) {
				cell = new PdfPCell();
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setFixedHeight(46.47f * Constants.MM_UNIT);
				table.addCell(cell);
			}
			Map<Integer, UserDTO> userMap = this.map(dto.getIds());
			UserTypeBO utbo = UserTypeBO.getInstance(this.getSchema());
			for (UserDTO user : userMap.values()) {
				String userId = String.valueOf(user.getId());
				PdfContentByte cb = writer.getDirectContent();
				Barcode39 code39 = new Barcode39();
				code39.setExtended(true);
				while (userId.length() < 10) {
					userId = "0" + userId;
				}
				code39.setCode(userId);
				code39.setStartStopText(false);
				Image image39 = code39.createImageWithBarcode(cb, null, null);
				image39.scalePercent(110f);
				Paragraph para = new Paragraph();
				String userName = user.getName();
				userName = userName.length() >= 30 ? userName.substring(0, 30) : userName;
				Phrase p1 = new Phrase(userName + "\n");
				Phrase p2 = new Phrase(new Chunk(image39, 0, 0));
				Phrase p3 = new Phrase(this.getText(i18n, "circulation.user_field.id") + ": " + user.getEnrollment() + "\n");
				UserTypeDTO usdto = utbo.get(user.getType());
				Phrase p4 = new Phrase(this.getText(i18n, "circulation.user_field.short_type") + ": " + usdto.getDescription() + "\n\n");
				para.add(p1);
				para.add(p3);
				para.add(p4);
				para.add(p2);
				cell = new PdfPCell(para);
				i++;
				cell.setFixedHeight(46.47f * Constants.MM_UNIT);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell);
			}
			
			if ((i % 3) != 0) {
				while ((i % 3) != 0) {
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
	
	
	private String getText(TranslationsMap i18n, String key) {
		String[] params = key.split(":::");

		if (params.length == 1) {
			return i18n.getText(key);
		}

		String text = i18n.getText(params[0]);
		for (int i = 1; i < params.length; i++) {
			String replacement = params[i];
			text = text.replace("{" + (i - 1) + "}", replacement);
		}
		return text;
	}
	
	public void markAsPrinted(Set<Integer> ids) {
		this.dao.markAsPrinted(ids);
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}
}
