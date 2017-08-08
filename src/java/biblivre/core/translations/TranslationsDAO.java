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
package biblivre.core.translations;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class TranslationsDAO extends AbstractDAO {

	public static TranslationsDAO getInstance(String schema) {
		return (TranslationsDAO) AbstractDAO.getInstance(TranslationsDAO.class, schema);
	}

	public List<TranslationDTO> list() {
		return this.list(null);
	}

	public List<TranslationDTO> list(String language) {
		List<TranslationDTO> list = new ArrayList<TranslationDTO>();

		Connection con = null;
		try {
			con = this.getConnection();

			StringBuilder sql = new StringBuilder();

			sql.append("SELECT K.language, K.key, coalesce(T.text, '') as text, T.created, T.created_by, T.modified, T.modified_by, T.user_created FROM ( ");
			sql.append("SELECT DISTINCT B.language, A.key FROM (SELECT DISTINCT key FROM translations UNION SELECT DISTINCT key FROM global.translations) A ");
			sql.append("CROSS JOIN (SELECT DISTINCT language FROM translations UNION SELECT DISTINCT language FROM global.translations) B ");
			sql.append(") K LEFT JOIN translations T ON T.key = K.key AND T.language = K.language ");
			
			if (StringUtils.isNotBlank(language)) {
				sql.append("WHERE K.language = ? ");
			}

			sql.append("ORDER BY K.language, K.key;");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			if (StringUtils.isNotBlank(language)) {
				pst.setString(1, language);
			}
			
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				try {
					list.add(this.populateDTO(rs));
				} catch (Exception e) {
					this.logger.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}

	public boolean save(HashMap<String, HashMap<String, String>> translations, int loggedUser) {
 		return this.save(translations, null, loggedUser);
	}
	
	public boolean save(HashMap<String, HashMap<String, String>> translations, HashMap<String, HashMap<String, String>> removeTranslations, int loggedUser) {
		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);

			if (translations != null) {
				CallableStatement function = con.prepareCall("{ call global.update_translation(?, ?, ?, ?) }");
			
				for (String language : translations.keySet()) {
					Map<String, String> translation = translations.get(language);
					
					for (String key : translation.keySet()) {
						function.setString(1, language);
						function.setString(2, key);
						function.setString(3, translation.get(key));
						function.setInt(4, loggedUser);
						function.addBatch();
					}
				}
				
				function.executeBatch();
				function.close();
			}

			if (removeTranslations != null) {
				String sql = "DELETE FROM translations WHERE language = ? AND key = ?; ";
				PreparedStatement pst = con.prepareStatement(sql);
	
				for (String language : removeTranslations.keySet()) {
					Map<String, String> translation = removeTranslations.get(language);
					
					for (String key : translation.keySet()) {
						pst.setString(1, language);
						pst.setString(2, key);
						pst.addBatch();
					}
				}
				
				pst.executeBatch();
			}
			
			this.commit(con);
			return true;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
    private TranslationDTO populateDTO(ResultSet rs) throws SQLException {
    	TranslationDTO dto = new TranslationDTO();

        dto.setLanguage(rs.getString("language"));
        dto.setKey(rs.getString("key"));
        dto.setText(rs.getString("text"));

        dto.setCreated(rs.getTimestamp("created"));
        dto.setCreatedBy(rs.getInt("created_by"));
        dto.setModified(rs.getTimestamp("modified"));
        dto.setModifiedBy(rs.getInt("modified_by"));
        
        dto.setUserCreated(rs.getBoolean("user_created"));

        return dto;
    }
}
