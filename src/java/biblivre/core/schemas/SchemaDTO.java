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
package biblivre.core.schemas;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDTO;

public class SchemaDTO extends AbstractDTO implements Comparable<SchemaDTO> {
	private static final long serialVersionUID = 1L;

	private String schema;
	private String name;
	private boolean disabled;
	
	public SchemaDTO() {
		super();
	}
	
	public SchemaDTO(String schema, String name) {
		super();
		
		this.setSchema(schema);
		this.setName(name);
		this.setDisabled(false);
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getSchema() {
		return StringUtils.defaultString(this.schema);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return StringUtils.defaultString(this.name);
	}
	
	public boolean isDisabled() {
		return this.disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || !(other instanceof SchemaDTO)) {
			return false;
		}
		
		SchemaDTO otherDto = (SchemaDTO) other;
		
		return this.schema == null ? otherDto.schema == null : this.schema.equals(otherDto.schema);
	}
	
	@Override
	public int compareTo(SchemaDTO other) {		
		return this.getSchema().compareTo(other.getSchema());
	}
}
