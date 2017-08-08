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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;


public enum MaterialType {
	ALL('a', "m ", true),
	BOOK('a', "m ", true),
	PAMPHLET('a', "m ", true),
	MANUSCRIPT('t', "m ", true),
	THESIS('a', "m ", true),
	PERIODIC('a', "s ", true),
	ARTICLES('a', "b ", true),
	COMPUTER_LEGIBLE('m', "m ", true),
	MAP('e', "m ", true),
	PHOTO('k', "m ", true),
	MOVIE('p', "m ", true),
	SCORE('c', "m ", true),
	MUSIC('j', "m ", true),
	NONMUSICAL_SOUND('i', "m ", true),
	OBJECT_3D('r', "m ", true),
	AUTHORITIES('z', "  ", false),
	VOCABULARY('w', "  ", false),
	HOLDINGS('u', "  ", false);

	private static List<MaterialType> bibliographicMaterials = null;
	private static List<MaterialType> searchableMaterials = null;
	
	private char typeOfRecord;
	private String implDefined1;
	private boolean searchable;

	private MaterialType(char typeOfRecord, String implDef1, boolean searchable) {
		this.typeOfRecord = typeOfRecord;
		this.implDefined1 = implDef1;
		this.searchable = searchable;
	}

	public char getTypeOfRecord() {
		return this.typeOfRecord;
	}

	public String getImplDefined1() {
		return this.implDefined1;
	}

	public boolean isSearchable() {
		return this.searchable;
	}

	public static MaterialType fromString(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}

		str = str.toLowerCase();

		for (MaterialType type : MaterialType.values()) {
			if (str.equals(type.name().toLowerCase())) {
				return type;
			}
		}

		return null;
	}

	public static MaterialType fromTypeAndImplDef(char typeOfRecord, char[] implDef1) {
		String imp = String.valueOf(implDef1);
		
		for (MaterialType type : MaterialType.values()) {
			if (type.getTypeOfRecord() == typeOfRecord && type.getImplDefined1().equals(imp)) {
				return type;
			}
		}
		
		return MaterialType.BOOK;
	}
	
	public  static MaterialType fromRecord(Record record) {
		MaterialType mt = null;

		if (record != null) {
			Leader leader = record.getLeader();
			mt = MaterialType.fromTypeAndImplDef(leader.getTypeOfRecord(), leader.getImplDefined1());
		}

		return (mt != null && mt != MaterialType.ALL) ? mt : MaterialType.BOOK;
	}
	
	public static List<MaterialType> bibliographicValues() {
		if (MaterialType.bibliographicMaterials == null) {
			MaterialType.bibliographicMaterials = new LinkedList<MaterialType>();
			
			for (MaterialType material : MaterialType.values()) {
				if (material.isSearchable() && !material.equals(MaterialType.ALL)) {
					MaterialType.bibliographicMaterials.add(material);
				}
			}
		}
				
		return MaterialType.bibliographicMaterials;
	}
	
	public static List<MaterialType> searchableValues() {
		if (MaterialType.searchableMaterials == null) {
			MaterialType.searchableMaterials = new LinkedList<MaterialType>();
			
			for (MaterialType material : MaterialType.values()) {
				if (material.isSearchable()) {
					MaterialType.searchableMaterials.add(material);
				}
			}
		}
				
		return MaterialType.searchableMaterials;
	}
	
	public static String toJavascriptArray(){
		
		List<MaterialType> bibliographicValues = MaterialType.bibliographicValues();
		
		String[] names = new String[bibliographicValues.size()];
		int i = 0;
		for (MaterialType type : bibliographicValues) {
			names[i++] = type.toString();
		}
		
	    StringBuffer sb = new StringBuffer();
	    sb.append("[");
	    
	    for (int j = 0; j < names.length; j++) {
	    	
	        sb.append("\"").append(names[j]).append("\"");
	        
	        if ( (j + 1) < names.length) {
	            sb.append(",");
	        }
	        
	    }
	    sb.append("]");
	    
	    return sb.toString();
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

	public String getString() {
		return this.toString();
	}
}
