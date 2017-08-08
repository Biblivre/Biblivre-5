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
package biblivre.administration.indexing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.cataloging.AutocompleteDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.TextUtils;

public class IndexingDAO extends AbstractDAO {
	public static IndexingDAO getInstance(String schema) {
		return (IndexingDAO) AbstractDAO.getInstance(IndexingDAO.class, schema);
	}

	public Integer countIndexed(RecordType recordType) {
		Connection con = null;
		try {
			con = this.getConnection();
			String sql = "SELECT count(DISTINCT record_id) as total FROM " + recordType + "_idx_sort";

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				return rs.getInt("total");
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return 0;
	}

	public void clearIndexes(RecordType recordType) {
		Connection con = null;
		try {
			con = this.getConnection();

			String sql = "TRUNCATE TABLE " + recordType + "_idx_fields";
			String sql2 = "TRUNCATE TABLE " + recordType + "_idx_sort";
			String sql3 = "DELETE FROM " + recordType + "_idx_autocomplete WHERE record_id is not null";

			Statement st = con.createStatement();
			st.execute(sql);
			st.execute(sql2);
			st.execute(sql3);
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public void startIndexing() {
		
	}

	public void insertIndexes(RecordType recordType, List<IndexingDTO> indexes) {
		int total = 0;
		for (IndexingDTO index : indexes) {
			total += index.getCount();
		}

		if (total == 0) {
			return;
		}

		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ").append(recordType).append("_idx_fields ");
			sql.append("(record_id, indexing_group_id, word, datafield) VALUES (?, ?, ?, ?);");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			for (IndexingDTO index : indexes) {
				final int recordId = index.getRecordId();
				final int groupId = index.getIndexingGroupId();

				HashMap<Integer, HashSet<String>> wordsGroups = index.getWords();
				for (Integer key : wordsGroups.keySet()) {
					HashSet<String> words = wordsGroups.get(key);

					for (String word : words) {
						pst.setInt(1, recordId);
						pst.setInt(2, groupId);
						pst.setString(3, word);
						pst.setInt(4, key);
						pst.addBatch();
					}
				}
			}

			pst.executeBatch();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}

	public void insertSortIndexes(RecordType recordType, List<IndexingDTO> sortIndexes) {
		int total = sortIndexes.size();

		if (total == 0) {
			return;
		}

		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ").append(recordType).append("_idx_sort ");
			sql.append("(record_id, indexing_group_id, phrase, ignore_chars_count) VALUES (?, ?, ?, ?);");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			for (IndexingDTO sortIndex : sortIndexes) {
				pst.setInt(1, sortIndex.getRecordId());
				pst.setInt(2, sortIndex.getIndexingGroupId());
				pst.setString(3, sortIndex.getPhrase());
				pst.setInt(4, sortIndex.getIgnoreCharsCount());
				pst.addBatch();
			}

			pst.executeBatch();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public void insertAutocompleteIndexes(RecordType recordType, List<AutocompleteDTO> autocompleteIndexes) {
		if (autocompleteIndexes.size() == 0) {
			return;
		}
		
		boolean batched = false;

		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO ").append(recordType).append("_idx_autocomplete ");
			sql.append("(datafield, subfield, word, phrase, record_id) VALUES (?, ?, ?, ?, ?);");

			PreparedStatement pst = con.prepareStatement(sql.toString());

			for (AutocompleteDTO index : autocompleteIndexes) {
				final Integer recordId = index.getRecordId();
				final String datafield = index.getDatafield();
				final String subfield = index.getSubfield();
				final String phrase = index.getPhrase();
				
				for (String word : TextUtils.prepareAutocomplete(phrase)) {
					if (StringUtils.isBlank(word) || word.length() < 2) {
						continue;
					}
					
					pst.setString(1, datafield);
					pst.setString(2, subfield);
					pst.setString(3, word);
					pst.setString(4, phrase);
					pst.setInt(5, recordId);

					pst.addBatch();
					batched = true;
				}
			}

			if (batched) {
				pst.executeBatch();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public void reindexAutocompleteFixedTable(RecordType recordType, String datafield, String subfield, List<String> phrases) {
		boolean batched = false;

		Connection con = null;
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			
			StringBuilder sql = new StringBuilder();
			
			sql.append("DELETE FROM ").append(recordType).append("_idx_autocomplete ");
			sql.append("WHERE datafield = ? and subfield = ? and record_id is null;");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setString(1, datafield);
			pst.setString(2, subfield);

			pst.executeUpdate();
			
			sql = new StringBuilder();
			sql.append("INSERT INTO ").append(recordType).append("_idx_autocomplete ");
			sql.append("(datafield, subfield, word, phrase, record_id) VALUES (?, ?, ?, ?, null);");

			pst = con.prepareStatement(sql.toString());

			for (String phrase : phrases) {
				for (String word : TextUtils.prepareAutocomplete(phrase)) {
					if (StringUtils.isBlank(word) || word.length() < 2) {
						continue;
					}
					
					pst.setString(1, datafield);
					pst.setString(2, subfield);
					pst.setString(3, word);
					pst.setString(4, phrase);

					pst.addBatch();
					batched = true;
				}
			}

			if (batched) {
				pst.executeBatch();
			}
			
			con.commit();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public boolean deleteIndexes(RecordType recordType, RecordDTO dto) {
		Connection con = null;
		
		try {
			con = this.getConnection();
			con.setAutoCommit(false);
			
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM ").append(recordType).append("_idx_fields ");
			sql.append("WHERE record_id = ?;");

			PreparedStatement pst = con.prepareStatement(sql.toString());
			pst.setInt(1, dto.getId());
			pst.executeUpdate();

			sql = new StringBuilder();
			sql.append("DELETE FROM ").append(recordType).append("_idx_sort ");
			sql.append("WHERE record_id = ?;");

			pst = con.prepareStatement(sql.toString());
			pst.setInt(1, dto.getId());
			pst.executeUpdate();

			sql = new StringBuilder();
			sql.append("DELETE FROM ").append(recordType).append("_idx_autocomplete ");
			sql.append("WHERE record_id = ?;");

			pst = con.prepareStatement(sql.toString());
			pst.setInt(1, dto.getId());
			pst.executeUpdate();
			
			this.commit(con);
			return true;
		} catch (Exception e) {
			this.rollback(con);
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}	
	
	public void reindexDatabase(RecordType recordType) {
		Connection con = null;
		try {
			con = this.getConnection();

			Statement st = con.createStatement();
			
			st.execute("REINDEX TABLE " + recordType + "_idx_fields");
			st.execute("REINDEX TABLE " + recordType + "_idx_sort");
			st.execute("ANALYZE " + recordType + "_idx_fields");
			st.execute("ANALYZE " + recordType + "_idx_sort");
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public List<String> searchExactTerms(RecordType recordType, int indexingGroupId, List<String> terms) {
		List<String> list = new LinkedList<String>();
		
		Connection con = null;
		try {
			con = this.getConnection();
			StringBuilder sql = new StringBuilder();
			
			sql.append("SELECT phrase FROM ").append(recordType).append("_idx_sort WHERE indexing_group_id = ? AND phrase in (");
			sql.append(StringUtils.repeat("?", ", ", terms.size()));
			sql.append(");");
			
			PreparedStatement pst = con.prepareStatement(sql.toString());
			
			int index = 1;
			pst.setInt(index++, indexingGroupId);
			
			for (String term : terms) {
				pst.setString(index++, term.toLowerCase());
			}
			
			ResultSet rs = pst.executeQuery();
 
			while (rs.next()) {
				list.add(rs.getString("phrase"));
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return list;
	}
}
