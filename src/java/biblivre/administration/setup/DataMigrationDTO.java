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
package biblivre.administration.setup;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import biblivre.core.AbstractDTO;

public class DataMigrationDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private volatile String dataSourceName;

	private volatile DataMigrationPhase currentPhase;
	private volatile Integer index;
	private volatile Integer total;
	private volatile HashMap<DataMigrationPhase, Boolean> phases;
	private volatile Set<DataMigrationPhase> completedPhases;

	public DataMigrationDTO() {
		this.phases = new HashMap<DataMigrationPhase, Boolean>();
		this.completedPhases = new TreeSet<DataMigrationPhase>(); 
	}
	
	public String getDataSourceName() {
		return this.dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public DataMigrationPhase getCurrentPhase() {
		return this.currentPhase;
	}

	public void setCurrentPhase(DataMigrationPhase currentPhase) {
		this.currentPhase = currentPhase;
	}

	public Integer getIndex() {
		return this.index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getTotal() {
		return this.total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
	
	public void enablePhase(String phase) {
		this.enablePhase(DataMigrationPhase.fromString(phase));
	}

	public void enablePhase(DataMigrationPhase phase) {
		if (phase != null) {
			this.phases.put(phase, true);
		}
	}
	
	public void disablePhase(String phase) {
		this.disablePhase(DataMigrationPhase.fromString(phase));	
	}

	public void disablePhase(DataMigrationPhase phase) {
		if (phase != null) {
			this.phases.put(phase, false);
		}
	}
	
	public boolean isPhaseEnabled(DataMigrationPhase phase) {
		if (!this.phases.containsKey(phase)) {
			return false;
		}
		
		return this.phases.get(phase);
	}

	public void completePhase(DataMigrationPhase phase) {
		this.completedPhases.add(phase);
	}
	
	public Set<DataMigrationPhase> getCompletedPhases() {
		return this.completedPhases;
	}
}
