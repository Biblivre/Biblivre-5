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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j.MarcException;
import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Leader;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

public class MarcUtils {

	private static Logger logger = Logger.getLogger(MarcUtils.class);
	private static Format CF001_FORMAT = new DecimalFormat("0000000");
	private static Format CF008_FORMAT = new SimpleDateFormat("yyMMdd");
	private static Format COMPACT_ISO = new SimpleDateFormat("yyyyMMddHHmmss.SSS");

	public static String recordToIso2709(Record record) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		MarcWriter writer = new MarcStreamWriter(os, "UTF-8");
		writer.write(record);
		writer.close();

		try {
			return os.toString("UTF-8");
		} catch (UnsupportedEncodingException uee) {
			MarcUtils.logger.error(uee.getMessage(), uee);
			return os.toString();
		}
	}

	public static Record iso2709ToRecord(String iso2709) {
		Record record = null;
		
		try {
			record = MarcUtils.iso2709ToRecord(iso2709.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException uee) {
		}
		
		return record;
	}
	
	public static Record iso2709ToRecord(byte[] iso2709) {
		Record record = null;

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(iso2709);
			MarcReader reader = new MarcPermissiveStreamReader(is, true, true);

			if (reader.hasNext()) {
				record = reader.next();
			}

		} catch (MarcException me) {
			if (MarcUtils.logger.isDebugEnabled()) {
				MarcUtils.logger.error(me.getMessage(), me);
			}
		}

		return record;
	}

	public static String marcToIso2709(String marc, MaterialType materialType, RecordStatus status) {
		Record record = MarcUtils.marcToRecord(marc, materialType, status);
		return MarcUtils.recordToIso2709(record);
	}

	public static String iso2709ToMarc(String iso2709) {
		Record record = MarcUtils.iso2709ToRecord(iso2709);
		return MarcUtils.recordToMarc(record);
	}

	public static Record marcToRecord(String marc, MaterialType materialType, RecordStatus status) {
		String splitter = MarcUtils.detectSplitter(marc);
		String unescaped = StringEscapeUtils.unescapeHtml4(marc);
		Scanner scanner = null;

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(unescaped.getBytes("UTF-8"));
			scanner = new Scanner(is, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			MarcUtils.logger.error(uee.getMessage(), uee);
			scanner = new Scanner(unescaped);
		}

		List<String> text = new ArrayList<String>();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();

			if (line.length() > 3) {
				text.add(line);
			}
		}

		scanner.close();

		String tags[] = new String[text.size()];
		String values[] = new String[text.size()];

		for (int i = 0; i < text.size(); i++) {
			String line = text.get(i).trim();
			if (line.toUpperCase().startsWith("LEADER")) {
				tags[i] = line.substring(0, 6).toUpperCase();
				values[i] = line.substring(7);
			} else {
				tags[i] = line.substring(0, 3).toUpperCase();
				values[i] = line.substring(4);
			}
		}

		Leader leader = MarcUtils.createLeader(tags, values, materialType, status);
		MarcFactory factory = MarcFactory.newInstance();
		Record record = factory.newRecord(leader);
		MarcUtils.setControlFields(record, tags, values);
		MarcUtils.setDataFields(record, tags, values, splitter);
		return record;
	} 

	@SuppressWarnings("unchecked")
	public static String recordToMarc(Record record) {
		if (record == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("000 ");
		sb.append(record.getLeader().marshal());
		sb.append('\n');

		List<ControlField> controlFields = record.getControlFields();
		for (ControlField field : controlFields) {
			sb.append(field.toString());
			sb.append('\n');
		}

		List<DataField> dataFields = record.getDataFields();		
		for (DataField field : dataFields) {
			sb.append(field.getTag());
			sb.append(' ');

			char ind1 = field.getIndicator1();
			char ind2 = field.getIndicator2();

			sb.append(ind1 == ' ' ? '_' : ind1);
			sb.append(ind2 == ' ' ? '_' : ind2);

			List<Subfield> subfieldList = field.getSubfields();
			for (Subfield subfield : subfieldList) {
				sb.append(MarcConstants.DEFAULT_SPLITTER);
				sb.append(subfield.getCode());
				sb.append(subfield.getData());
			}

			sb.append('\n');
		}

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static JSONObject recordToJson(Record record) {
		JSONObject json = new JSONObject();

		if (record == null) {
			return json;
		}

		try {
			json.putOpt("000", record.getLeader().marshal());

			ArrayList<ControlField> controlFields = (ArrayList<ControlField>) record.getControlFields();

			for (ControlField cf : controlFields) {
				json.putOpt(cf.getTag(), cf.getData());
			}

			ArrayList<DataField> dataFields = (ArrayList<DataField>) record.getDataFields();
			for (DataField df : dataFields) {
				JSONObject datafieldJson = new JSONObject();

				datafieldJson.putOpt("ind1", df.getIndicator1());
				datafieldJson.putOpt("ind2", df.getIndicator2());

				ArrayList<Subfield> subFields = (ArrayList<Subfield>) df.getSubfields();

				for (Subfield sf : subFields) {
					datafieldJson.append(String.valueOf(sf.getCode()), sf.getData());
				}

				json.append(df.getTag(), datafieldJson);
			}
		} catch (JSONException je) {
		}

		return json;
	}

	public static Record jsonToRecord(JSONObject json, MaterialType materialType, RecordStatus status) {
		if (json == null) {
			return null;
		}

		Record record = null;

		try {
			String strLeader = null;
			if (json.has("000")) {
				strLeader = json.getString("000");
			}

			MarcFactory factory = MarcFactory.newInstance();

			Leader leader = MarcUtils.createLeader(strLeader, materialType, status);
			record = factory.newRecord(leader);

			Iterator<String> dataFieldsIterator = json.sortedKeys();
			while (dataFieldsIterator.hasNext()) {
				String dataFieldTag = dataFieldsIterator.next();

				try {
					Integer dataFieldIntTag = Integer.valueOf(dataFieldTag);

					if (dataFieldIntTag == 0) {
						continue;

					} else if (dataFieldIntTag < 10) {
						ControlField cf = factory.newControlField(dataFieldTag, json.getString(dataFieldTag));
						record.addVariableField(cf);

					} else {
						JSONArray subFieldsArray = json.getJSONArray(dataFieldTag);

						for (int i = 0; i < subFieldsArray.length(); i++) {
							JSONObject subFieldJson = subFieldsArray.getJSONObject(i);

							DataField df = factory.newDataField();
							df.setTag(dataFieldTag);
							df.setIndicator1(' ');
							df.setIndicator2(' ');

							Iterator<String> dfIterator = subFieldJson.sortedKeys();
							while (dfIterator.hasNext()) {
								String subFieldTag = dfIterator.next();

								if (subFieldTag.equals("ind1")) {
									df.setIndicator1(subFieldJson.getString(subFieldTag).charAt(0));
								} else if (subFieldTag.equals("ind2")) {
									df.setIndicator2(subFieldJson.getString(subFieldTag).charAt(0));
								} else {
									JSONArray subFieldDataArray = subFieldJson.getJSONArray(subFieldTag);

									for (int j = 0; j < subFieldDataArray.length(); j++) {
										String subfieldData = subFieldDataArray.getString(j);

										Subfield sf = factory.newSubfield(subFieldTag.charAt(0), subfieldData);
										df.addSubfield(sf);
									}
								}
							}

							record.addVariableField(df);
						}
					}
				} catch (NumberFormatException nfe) {
					MarcUtils.logger.error(nfe.getMessage(), nfe);
				}
			}
		} catch (JSONException je) {
			MarcUtils.logger.error(je.getMessage(), je);
		}

		return record;
	}

	public static String detectSplitter(String marc) {
		// Try to detect the first split.
		if (!StringUtils.isBlank(marc)) {
			String[] lines = marc.split("\n");
			for (String line : lines) {
				line = line.trim();
				if (line.length() > 7) {
					char separator = line.charAt(6);
					if (separator == '|' || separator == '$') {
						return String.valueOf(separator);
					}
				}
			}
		}

		return MarcConstants.DEFAULT_SPLITTER;
	}

	private static Leader createLeader(String[] tags, String values[], MaterialType materialType, RecordStatus status) {
		Leader leader = null;
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals("000") || tags[i].equals("LDR") || tags[i].equals("LEADER")) {
				leader = MarcUtils.createLeader(values[i], materialType, status);
				break;
			}
		}

		if (leader == null) {
			leader = MarcUtils.createBasicLeader(materialType, status);
		}

		return leader;
	}

	private static Leader createLeader(String pLeader, MaterialType materialType, RecordStatus status) {
		Leader leader = MarcFactory.newInstance().newLeader();

		if (pLeader != null && pLeader.length() == 24) {
			leader.setRecordStatus(status.getCode());

			if (materialType != null && !materialType.equals(MaterialType.ALL)) {
				leader.setTypeOfRecord(materialType.getTypeOfRecord());
				leader.setImplDefined1(materialType.getImplDefined1().toCharArray());
			} else {
				leader.setTypeOfRecord(pLeader.charAt(6));
				char $07 = pLeader.charAt(7);
				char $08 = (pLeader.charAt(8)) == 'a' ? 'a' : ' ';
				char[] implDef1 = {$07, $08};
				leader.setImplDefined1(implDef1);
			}

			char $09 = (pLeader.charAt(9)) == 'a' ? 'a' : ' ';
			leader.setCharCodingScheme($09);
			leader.setIndicatorCount(2);
			leader.setSubfieldCodeLength(2);
			leader.setImplDefined2(pLeader.substring(17, 20).toCharArray());
			leader.setEntryMap((pLeader.substring(20)).toCharArray());
		} else {
			leader = MarcUtils.createBasicLeader(materialType, status);
		}

		return leader;
	}

	public static Leader createBasicLeader(MaterialType materialType, RecordStatus status) {
		if (materialType == null) {
			materialType = MaterialType.ALL;
		}

		Leader leader = MarcFactory.newInstance().newLeader();
		leader.setRecordStatus(status.getCode());
		leader.setTypeOfRecord(materialType.getTypeOfRecord());
		leader.setImplDefined1(materialType.getImplDefined1().toCharArray());
		leader.setCharCodingScheme('a');
		leader.setIndicatorCount(2);
		leader.setSubfieldCodeLength(2);

		if (materialType.equals(MaterialType.AUTHORITIES)) {
			leader.setImplDefined2("n  ".toCharArray());
		} else if (materialType.equals(MaterialType.HOLDINGS)) {
			leader.setImplDefined2("un ".toCharArray());
		} else if (materialType.equals(MaterialType.VOCABULARY)) {
			leader.setImplDefined2("o  ".toCharArray());
		} else {//BIBLIO
			leader.setImplDefined2(" a ".toCharArray());
		}

		leader.setEntryMap("4500".toCharArray());
		return leader;
	}

	private static void setControlFields(Record record, String[] tags, String[] values) {
		MarcFactory factory = MarcFactory.newInstance();
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			String value = values[i];

			if (StringUtils.isNumeric(tag)) {
				int iTag = Integer.parseInt(tag);

				if (iTag > 0 && iTag < 10) {
					ControlField controlField = factory.newControlField(tag, value);
					record.addVariableField(controlField);
				}
			}
		}
	}

	private static void setDataFields(Record record, String[] tags, String[] values, String splitter) {
		if (splitter == null) {
			splitter = MarcConstants.DEFAULT_SPLITTER;
		}
		splitter = "\\" + splitter;

		MarcFactory factory = MarcFactory.newInstance();
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			String value = values[i];        	

			if (StringUtils.isNumeric(tag)) {
				int iTag = Integer.parseInt(tags[i]);

				if (iTag >= 10) {
					char ind1 = value.charAt(0) != '_' ? value.charAt(0) : ' ';
					char ind2 = value.charAt(1) != '_' ? value.charAt(1) : ' ';
					DataField dataField = factory.newDataField(tag, ind1, ind2);
					record.addVariableField(dataField);

					String[] subfs = value.substring(2).trim().split(splitter);
					for (String data : subfs) {
						if (StringUtils.isNotBlank(data)) {
							Subfield subfield = factory.newSubfield(data.charAt(0), data.substring(1).trim());
							dataField.addSubfield(subfield);
						}
					}
				}
			}
		}
	}

	public static Record addAttachment(Record record, String uri, String description) {
		MarcFactory factory = MarcFactory.newInstance();

		DataField field = factory.newDataField(MarcConstants.ELECTRONIC_LOCATION, ' ', ' ');

		Subfield subfieldD = factory.newSubfield('d', uri.replaceAll("[^\\/]*$", ""));
		field.addSubfield(subfieldD);

		Subfield subfieldF = factory.newSubfield('f', uri.replaceAll(".*\\/", ""));
		field.addSubfield(subfieldF);

		Subfield subfieldU = factory.newSubfield('u', uri);
		field.addSubfield(subfieldU);

		Subfield subfieldY = factory.newSubfield('y', description);
		field.addSubfield(subfieldY);

		record.addVariableField(field);
		return record;
	}
	
	public static Record removeAttachment(Record record, String uri, String description) throws Exception {
		VariableField dataFieldToRemove = null;
		
		MarcDataReader marcReader = new MarcDataReader(record);
		
		for (DataField df : marcReader.getDataFields(MarcConstants.ELECTRONIC_LOCATION)) {
			String sfName = marcReader.getFirstSubfieldData(df, 'y');
			String sfUri = marcReader.getFirstSubfieldData(df, 'u');

			if (StringUtils.isBlank(sfName)) {
				sfName = sfUri;
			}
			
			if (description.equals(sfName) && uri.equals(sfUri)) {
				dataFieldToRemove = df;
				break;
			}
		}
		
		if (dataFieldToRemove != null) {
			record.removeVariableField(dataFieldToRemove);
		}
		
		return record;
	}

	public static Record setAccessionNumber(Record holding, String accessionNumber) {
		MarcFactory factory = MarcFactory.newInstance();

		DataField field = (DataField) holding.getVariableField(MarcConstants.ACCESSION_NUMBER);
		if (field == null) {
			field = factory.newDataField(MarcConstants.ACCESSION_NUMBER, ' ', ' ');
			holding.addVariableField(field);
		}

		Subfield subfield = field.getSubfield('a');
		if (subfield == null) {
			subfield = factory.newSubfield('a');
			field.addSubfield(subfield);
		}

		subfield.setData(accessionNumber);
		return holding;
	}

	public static Record setCF001(Record record, Integer controlNumber) {
		MarcFactory factory = MarcFactory.newInstance();
		ControlField field = factory.newControlField("001");
		field.setData(MarcUtils.CF001_FORMAT.format(controlNumber));
		record.addVariableField(field);
		return record;
	}

	public static Record setCF004(Record holding, Integer recordId) {
		MarcFactory factory = MarcFactory.newInstance();
		ControlField field = (ControlField) holding.getVariableField("004");

		if (field == null) {
			field = factory.newControlField("004");
			holding.addVariableField(field);
		}
		
		field.setData(recordId.toString());
		return holding;
	}
	
	public static Record setCF005(Record record) {
		return MarcUtils.setCF005(record, new Date());
	}

	public static Record setCF005(Record record, Date date) {
		MarcFactory factory = MarcFactory.newInstance();
		ControlField field = (ControlField) record.getVariableField("005");

		if (field == null) {
			field = factory.newControlField("005");
			record.addVariableField(field);
		}

		field.setData(MarcUtils.COMPACT_ISO.format(date));
		return record;
	}

	public static Record setCF008(Record record) {
		MarcFactory factory = MarcFactory.newInstance();
		ControlField field = (ControlField) record.getVariableField("008");
		if (field == null) {
			//Following the specs, this field should be constructed only
			//if it doesn't already exist.  Otherwise, keep what has
			//come with the freemarc string.
			field = factory.newControlField("008");
			StringBuilder data = new StringBuilder();
			//From 01 to 06
			data.append(CF008_FORMAT.format(new Date()));
			//From 07 to 40
			data.append("s||||     bl|||||||||||||||||por|u");
			field.setData(data.toString());
			record.addVariableField(field);
		}
		return record;
	}
}
