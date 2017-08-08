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

import java.sql.Connection;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class SetupDAO extends AbstractDAO {
	
	public static SetupDAO getInstance(String schema) {
		return (SetupDAO) AbstractDAO.getInstance(SetupDAO.class, schema);
	}
	
	public final void fixSequence(DataMigrationPhase migrationPhase) {
		if (migrationPhase == null || migrationPhase.getBiblivre4IdColumnName() == null) {
			return;
		}
		
		this.fixSequence(
				migrationPhase.getBiblivre4SequenceName(),
				migrationPhase.getBiblivre4TableName(),
				migrationPhase.getBiblivre4IdColumnName()
				);
	}

	public final void deleteAll(DataMigrationPhase phase) {
		Connection con = null;

		try {
			con = this.getConnection();

			StringBuilder sqlDelete = new StringBuilder();
			sqlDelete.append("DELETE FROM ").append(phase.getBiblivre4TableName()).append(";");
			con.prepareStatement(sqlDelete.toString()).executeUpdate();

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

	}


}
