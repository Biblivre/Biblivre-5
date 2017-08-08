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
package biblivre.marc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import biblivre.cataloging.BriefTabFieldDTO;
import biblivre.cataloging.BriefTabFieldFormatDTO;
import biblivre.cataloging.RecordAttachmentDTO;
import biblivre.core.utils.TextUtils;

public class MarcDataReader {
	private Record record;
	private HashMap<String, List<DataField>> cache;
	
	public MarcDataReader(Record record) {
		this.record = record;
		this.cache = this.readDataFieldMap();
	}
	
	public Record getRecord() {
		return this.record;
	}

	public HashMap<String, List<DataField>> getCache() {
		return this.cache;
	}
	
	public List<BriefTabFieldDTO> getFieldList(List<BriefTabFieldFormatDTO> dataFieldFormats) {
		List<BriefTabFieldDTO> list = new LinkedList<BriefTabFieldDTO>();

		for (BriefTabFieldFormatDTO dto : dataFieldFormats) {
			String tag = dto.getDatafieldTag();
			List<DataField> fields = this.getDataFields(tag);

			String formattedField = this.formatDataField(dto.getFormat(), fields);

			if (StringUtils.isNotBlank(formattedField)) {
				list.add(new BriefTabFieldDTO(dto.getDatafieldTag(), formattedField));
			}
		}

		return list;
	}

	public String getFieldValue(boolean listAll, BriefTabFieldFormatDTO ... dataFieldFormats) {
		return this.getFieldValue(listAll, "\n", dataFieldFormats);
	}

	public String getFieldValue(String separator, BriefTabFieldFormatDTO ... dataFieldFormats) {
		return this.getFieldValue(true, separator, dataFieldFormats);
	}
	
	public String getFieldValue(boolean listAll, String separator, BriefTabFieldFormatDTO ... dataFieldFormats) {
		List<String> formattedFields = new LinkedList<String>();

		for (BriefTabFieldFormatDTO dto : dataFieldFormats) {
			String tag = dto.getDatafieldTag();
			List<DataField> fields = this.getDataFields(tag);

			for (DataField datafield : fields) {
				String formattedField = this.formatDataField(dto.getFormat(), datafield);

				if (StringUtils.isNotBlank(formattedField)) {
					if (!listAll) {
						return formattedField;
					}

					formattedFields.add(formattedField);
				}
			}
		}
		
		return formattedFields.size() > 0 ? StringUtils.join(formattedFields, separator) : null;
	}
	
	public List<RecordAttachmentDTO> getAttachments() {
		List<DataField> fields = this.getDataFields(MarcConstants.ELECTRONIC_LOCATION);
		List<RecordAttachmentDTO> attachments = new LinkedList<RecordAttachmentDTO>();

		String file = null;
		String name = null;
		String path = null;
		String uri = null;

		for (DataField field : fields) {
			file = this.getFirstSubfieldData(field, 'f');
			name = this.getFirstSubfieldData(field, 'y');
			path = this.getFirstSubfieldData(field, 'd');
			uri = this.getFirstSubfieldData(field, 'u');

			if (StringUtils.isBlank(file)) {
				file = name;
			}

			if (StringUtils.isBlank(file)) {
				file = uri;
			}

			if (StringUtils.isBlank(file)) {
				continue;
			}

			if (StringUtils.isBlank(name)) {
				name = file;
			}

			RecordAttachmentDTO attachment = new RecordAttachmentDTO();

			attachment.setPath(path);
			attachment.setFile(file);
			attachment.setName(name);
			attachment.setUri(uri);

			attachments.add(attachment);
		}

		return attachments;
	}
	
	public String getAuthor(boolean listAll) {
		return this.getFieldValue(listAll,
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_PERSONAL_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CORPORATION_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CONGRESS_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_PERSONAL_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_CORPORATION_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_AUTHOR_CONGRESS_NAME, "${a}")
		);
	}
	
	public String getAuthorName(boolean listAll) {
		return this.getFieldValue(listAll,
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_PERSONAL_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CORPORATION_NAME, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_CONGRESS_NAME, "${a}")
		);
	}
	
	public String getAuthorOtherName(boolean listAll) {
		return this.getFieldValue(listAll,
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_PERSONAL_NAMES, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CORPORATION_NAMES, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.AUTHOR_OTHER_CONGRESS_NAMES, "${a}")
		);
	}

	public String getTitle(boolean listAll) {
		return this.getFieldValue(listAll,
				new BriefTabFieldFormatDTO(MarcConstants.TITLE, "${a}_{: }${b}"),
				new BriefTabFieldFormatDTO(MarcConstants.COLLECTIVE_UNIFORM_TITLE, "${a}_{ }${f}"),
				new BriefTabFieldFormatDTO(MarcConstants.UNIFORM_TITLE, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.ADDED_UNIFORM_TITLE, "${a}"),
				new BriefTabFieldFormatDTO(MarcConstants.ADDED_ANALYTICAL_TITLE, "${a}_{ }${n}_{ }${p}"),
				new BriefTabFieldFormatDTO(MarcConstants.SECONDARY_INPUT_SERIAL_TITLE, "${a}_{ }${v}")
		);
	}
	
	public String getIsbn() {
		return this.getFieldValue(" ",
				new BriefTabFieldFormatDTO(MarcConstants.ISBN, "${a}")
		);
	}

	public String getIssn() {
		return this.getFieldValue(" ",
				new BriefTabFieldFormatDTO(MarcConstants.ISSN, "${a}")
		);
	}

	public String getIsrc() {
		return this.getFieldValue(" ",
				new BriefTabFieldFormatDTO(MarcConstants.ISRC, "${a}")
		);
	}

	public String getPublicationYear() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION, "${c}"),
				new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION_FUNCTIONS, "${c}")
		);
	}

	public String getShelfLocation() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${a}_{ }${b}_{ }${c}")
		);
	}

	public String getHoldingLocation() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${d}")
		);
	}
	
	public String getLocation() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${a}")
		);
	}
	
	public String getLocationB() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${b}")
		);
	}
	
	public String getLocationC() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${c}")
		);
	}
	
	public String getLocationD() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SHELF_LOCATION, "${d}")
		);
	}
	
	public String getDDCN() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.DDCN, "${a}")
		);
	}
	
	public String getSourceAcquisitionDate() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.SOURCE_ACQUISITION_NOTES, "${d}")
		);
	}

	public String getSubject(boolean listAll) {
		return this.getFieldValue(listAll,
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_PERSONAL_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_CORPORATE_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_MEETING_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_UNIFORM_TITLE, "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_TOPICAL_TERM, "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_GEOGRAPHIC_NAME, "${a}_{ - }${x}_{ - }${y}_{ - }${z}"),
				new BriefTabFieldFormatDTO(MarcConstants.SUBJECT_ADDED_ENTRY_LOCAL, "${a}_{ - }${x}_{ - }${y}_{ - }${z}")
		);
	}
	
	public String getEditor() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.PUBLICATION, "${b}")
		);
	}
	
	public String getEdition() {
		return this.getFieldValue(false,
				new BriefTabFieldFormatDTO(MarcConstants.EDITION, "${a}")
		);
	}

    public String getAccessionNumber() {
		return this.getFieldValue(false, new BriefTabFieldFormatDTO(MarcConstants.ACCESSION_NUMBER, "${a}"));
    }
	
	public ControlField getControlField(String tag) {
		Record record = this.getRecord();

		if (record == null || StringUtils.isBlank(tag)) {
			return null;
		}

		for (Object obj : record.getControlFields()) {
			ControlField field = (ControlField) obj;

			if (field.getTag().equals(tag)) {
				return field;
			}
		}

		return null;
	}

	public DataField getFirstDataField(String tag) {
		List<DataField> list = this.getDataFields(tag);
		
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	public List<DataField> getDataFields(String tag) {
		Record record = this.getRecord();

		if (record == null || StringUtils.isBlank(tag)) {
			return new LinkedList<DataField>();
		}

		List<DataField> list = this.getCache().get(tag);
		
		if (list != null) {
			return list;
		}
		
		return new LinkedList<DataField>();
	}


	public Subfield getFirstSubfield(String tag, char subfield) {
		List<DataField> dataFields = this.getDataFields(tag);
		
		for (DataField field : dataFields) {
			Subfield sf = field.getSubfield(subfield);

			if (sf != null) {
				return sf;
			}
		}

		return null;
	}

	public String getFirstSubfieldData(String tag, char subfield) {
		Subfield sf = this.getFirstSubfield(tag, subfield);

		return sf != null ? sf.getData() : "";
	}
	
	public String getFirstSubfieldData(DataField datafield, char subfield) {
		// Get a single subfield value
		if (datafield == null) {
			return "";
		}

		Subfield sf = datafield.getSubfield(subfield);

		return sf != null ? sf.getData() : "";
	}
	
	private HashMap<String, List<DataField>> readDataFieldMap() {
		HashMap<String, List<DataField>> hash = new HashMap<String, List<DataField>>();
		Record record = this.getRecord();

		if (record == null) {
			return hash;
		}

		DataField field = null;
		String tag = null;
		List<DataField> fieldList = null;

		for (Object obj : record.getDataFields()) {
			field = (DataField) obj;
			tag = field.getTag();

			fieldList = hash.get(tag);

			if (fieldList == null) {
				fieldList = new LinkedList<DataField>();
				hash.put(tag, fieldList);
			}

			fieldList.add(field);
		}

		return hash;
	}
	
	private String formatDataField(String format, DataField ... datafields) {
		List<DataField> dataFieldList = Arrays.asList(datafields);
		
		return this.formatDataField(format, dataFieldList);
	}
	
	@SuppressWarnings("unchecked")
	private String formatDataField(String format, List<DataField> dataFieldList) {
		if (dataFieldList == null || dataFieldList.isEmpty()) {
			return "";
		}

		Matcher matcher = MarcConstants.DATAFIELD_FORMAT_PATTERN.matcher(format);

		StringBuilder result = new StringBuilder();
		StringBuilder values;

		String specialGroup;
		String element;
		String content;
		String value;
		String subfieldSeparator;

		List<Subfield> subfields;

		for (DataField dataField : dataFieldList) {
			if (dataField == null) {
				continue;
			}
			
			String lastSeparator = null;
			String lastValue = null;
			boolean newSeparator = false;
			boolean endsWithSeparator = false;
			boolean shouldAddStartParenthesis = false;

			while (matcher.find()) {
				specialGroup = matcher.group(1);

				if (specialGroup == null) {
					element = matcher.group(2);
					content = matcher.group(3);
				} else {
					element = specialGroup;
					content = "";
				}

				if (element.equals("$")) {
					subfields = dataField.getSubfields(content.charAt(0));

					subfieldSeparator = (content.length() == 1) ? ", " : content.substring(1);
					endsWithSeparator = false;

					values = new StringBuilder();
					for (Subfield s : subfields) {
						value = s.getData();

						if (StringUtils.isNotBlank(value)) {
							value = value.trim();

							values.append(value);

							if (TextUtils.endsInValidCharacter(value)) {
								values.append(subfieldSeparator);
								endsWithSeparator = true;
							} else {
								values.append(" ");
								endsWithSeparator = false;
							}
						}
					}
					value = values.toString();

					if (endsWithSeparator) {
						value = value.substring(0, value.length() - subfieldSeparator.length());
					} else {
						value = value.trim();
					}

					if (StringUtils.isNotBlank(value)) {
						if (newSeparator) {
							newSeparator = false;
							
							if (StringUtils.isNotBlank(lastValue)) {
								if (TextUtils.endsInValidCharacter(lastValue)) {
									result.append(lastSeparator);
								} else {
									result.append(" ");
								}
							}
						}

						lastValue = value.trim();
						
						if (shouldAddStartParenthesis) {
							shouldAddStartParenthesis = false;
							result.append("(");
						}

						result.append(lastValue);
					}
					
				} else if (element.equals("_")) {
					lastSeparator = content;
					newSeparator = true;
					
				} else if (element.equals("(")) {
					shouldAddStartParenthesis = true;
					
				} else if (element.equals(")")) {
					if (result.toString().endsWith("(")) {
						result.deleteCharAt(result.length() - 1);
					} else if (!shouldAddStartParenthesis) {
						result.append(")");
						shouldAddStartParenthesis = false;
					}
				}
			}

			matcher.reset();
			result.append("\n");
		}

		return StringUtils.chomp(result.toString());
	}
}
