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
package biblivre.cataloging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;

import biblivre.cataloging.authorities.AuthorityRecordBO;
import biblivre.cataloging.authorities.AuthorityRecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.ImportEncoding;
import biblivre.cataloging.enums.ImportFormat;
import biblivre.cataloging.vocabulary.VocabularyRecordBO;
import biblivre.cataloging.vocabulary.VocabularyRecordDTO;
import biblivre.core.AbstractBO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MarcFileReader;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.z3950.Z3950RecordDTO;

public class ImportBO extends AbstractBO {
	
	protected ImportDAO dao;
	
	public static ImportBO getInstance(String schema) {
		ImportBO bo = AbstractBO.getInstance(ImportBO.class, schema);
		
		if (bo.dao == null) {
			bo.dao = ImportDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public ImportDTO loadFromFile(MemoryFile file, ImportFormat format, ImportEncoding enc) {
		ImportDTO dto = null;
		String encoding = null;
				
		switch (enc) {
			case AUTO_DETECT:
				try {
					encoding = TextUtils.detectCharset(file.getNewInputStream());
				} catch (IOException e) {
				}
				break;

			case UTF8:
				encoding = "UTF-8";
				break;

			case MARC8:
				encoding = "ISO-8859-1";
				break;
		}

		if (encoding == null) {
			encoding = "UTF-8";
		}
		
		String streamEncoding = (enc == ImportEncoding.AUTO_DETECT)  ? "BESTGUESS" : encoding;

		try {
			switch (format) {
				case AUTO_DETECT:
					MarcReader reader = null;
					List<ImportDTO> list = new ArrayList<ImportDTO>();
					
					try {
						reader = new MarcPermissiveStreamReader(file.getNewInputStream(), true, true, streamEncoding);	
						dto = this.readFromMarcReader(reader);
						dto.setFormat(ImportFormat.ISO2709);
					} catch (Exception e) {
					}

					if (dto.isPerfect()) {
						break;
					} else {
						list.add(dto);
					}
					
					try {
						reader = new MarcXmlReader(file.getNewInputStream());
						dto = this.readFromMarcReader(reader);
						dto.setFormat(ImportFormat.XML);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (dto.isPerfect()) {
						break;
					} else {
						list.add(dto);
					}
					
					try {
						reader = new MarcFileReader(file.getNewInputStream(), encoding);
						dto = this.readFromMarcReader(reader);
						dto.setFormat(ImportFormat.MARC);
					} catch (Exception e) {
					}

					if (dto.isPerfect()) {
						break;
					} else {
						list.add(dto);
					}
					
					Collections.sort(list);
					dto = list.get(0);
					
					break;
					
				case ISO2709:
					MarcReader isoReader = new MarcPermissiveStreamReader(file.getNewInputStream(), true, true, streamEncoding);
					dto = this.readFromMarcReader(isoReader);
					dto.setFormat(ImportFormat.ISO2709);
					
					break;

				case XML:
					MarcReader xmlReader = new MarcXmlReader(file.getNewInputStream());
					dto = this.readFromMarcReader(xmlReader);
					dto.setFormat(ImportFormat.XML);
					break;

				case MARC:
					MarcReader marcReader = new MarcFileReader(file.getNewInputStream(), encoding);
					dto = this.readFromMarcReader(marcReader);
					dto.setFormat(ImportFormat.MARC);
					break;

				default:
					break;
			}
		} catch (Exception e) {
			this.logger.debug("Error reading file", e);
			throw new ValidationException(e.getMessage());
		}
		
		if (dto != null) {
			dto.setEncoding(enc);
		}

		return dto;
	}
	
	/**
	 * @param reader
	 * @return
	 */
	private ImportDTO readFromMarcReader(MarcReader reader) {
		ImportDTO dto = new ImportDTO();

		while (reader.hasNext()) {
			dto.incrementFound();

			try {
				RecordDTO rdto = this.dtoFromRecord(reader.next());

				if (rdto != null) {
					dto.addRecord(rdto);
					dto.incrementSuccess();
				} else {
					dto.incrementFailure();					
				}
			} catch (Exception e) {
				dto.incrementFailure();
			}
		}

		return dto;
	}
	
	public ImportDTO readFromZ3950Results(List<Z3950RecordDTO> recordList) {
		ImportDTO dto = new ImportDTO();
		BiblioRecordBO bbo = BiblioRecordBO.getInstance(this.getSchema());
		for (Z3950RecordDTO z3950Dto : recordList) {
			dto.incrementFound();
			try {
				BiblioRecordDTO brdto = z3950Dto.getRecord();

				if (brdto != null) {
					bbo.populateDetails(brdto, RecordBO.MARC_INFO);
					brdto.setMarc(MarcUtils.recordToMarc(brdto.getRecord()));

					dto.addRecord(brdto);
					dto.incrementSuccess();
				}
			} catch (Exception e) {
				dto.incrementFailure();
			}
		}

		return dto;
	}
	
	public RecordDTO dtoFromRecord(Record record) {
		String schema = this.getSchema();		
		RecordDTO rdto = null;
		RecordBO rbo = null;

		switch (MaterialType.fromRecord(record)) {
			case HOLDINGS:
				break;
			case VOCABULARY:
				rdto = new VocabularyRecordDTO();
				rbo = VocabularyRecordBO.getInstance(schema);
				break;
			case AUTHORITIES:
				rdto = new AuthorityRecordDTO();
				rbo = AuthorityRecordBO.getInstance(schema);
				break;
			default:
				rdto = new BiblioRecordDTO();
				rbo = BiblioRecordBO.getInstance(schema);
				break;
		}
		
		if (rdto != null && rbo != null) {
			rdto.setRecord(record);
			rbo.populateDetails(rdto, RecordBO.MARC_INFO);
	
			rdto.setMarc(MarcUtils.recordToMarc(record));
		}
		
		return rdto;
	}
}
