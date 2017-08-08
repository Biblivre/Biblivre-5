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
package biblivre.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.utils.TextUtils;


public class UpdatesDAO extends AbstractDAO {

	public static UpdatesDAO getInstance(String schema) {
		return (UpdatesDAO) AbstractDAO.getInstance(UpdatesDAO.class, schema);
	}

	public Set<String> getInstalledVersions() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT installed_versions FROM versions;"; 

			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			Set<String> set = new TreeSet<String>();
			while (rs.next()) {
				set.add(rs.getString("installed_versions"));
			}
			return set;
		} finally {
			this.closeConnection(con);
		}
	}	

	public Connection beginUpdate() throws SQLException {
		Connection con =  this.getConnection();
		con.setAutoCommit(false);

		return con;
	}	

	public void commitUpdate(String version, Connection con) throws SQLException {
		this.commitUpdate(version, con, true);
	}
	
	
	public void commitUpdate(String version, Connection con, boolean insert) throws SQLException {
		try {
			if (insert) {
				String sql = "INSERT INTO versions (installed_versions) VALUES (?);"; 
	
				PreparedStatement pst = con.prepareStatement(sql);
				pst.setString(1, version);
				pst.executeUpdate();
			}
			
			this.commit(con);
		} finally {
			this.closeConnection(con);
		}
	}	
	
	
	public void rollbackUpdate(Connection con) {
		try {
			this.rollback(con);
		} finally {
			this.closeConnection(con);
		}
	}	
	
	public void createArrayAgg() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "CREATE AGGREGATE public.array_agg(anyelement) (SFUNC=array_append, STYPE=anyarray, INITCOND=’{}’);"; 

			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}	
	
	public void create81ArrayAgg() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "CREATE AGGREGATE public.array_agg (SFUNC = array_append, BASETYPE = anyelement, STYPE = anyarray, INITCOND = '{}');"; 

			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}

	public void fixUpdateTranslationFunction(Connection con) throws SQLException {
		String sql = "CREATE OR REPLACE FUNCTION update_translation(character varying, character varying, character varying, integer) RETURNS integer \n" +
					"    LANGUAGE plpgsql \n" +
					"    AS $_$ \n" +
					" DECLARE \n" +
					"	p_language ALIAS FOR $1; \n" +
					"	p_key ALIAS FOR $2; \n" +
					"	p_text ALIAS FOR $3; \n" +
					"	p_user ALIAS FOR $4; \n" +
					" \n" +
					"	v_schema character varying; \n" +
					"	v_current_value TEXT; \n" +
					"	v_global_value TEXT; \n" +
					"	v_user_created BOOLEAN; \n" +
					"	v_query_string character varying; \n" +
					" BEGIN \n" +
					"	v_schema = current_schema(); \n" +
					"	 \n" +
					"	IF v_schema <> 'global' THEN \n" +
					"		-- Get the global value for this key \n" +
					"		SELECT INTO v_global_value text FROM global.translations \n" +
					"		WHERE language = p_language AND key = p_key; \n" +
					" \n" +
					"		-- If the new text is the same as the global one, \n" +
					"		-- delete it from the current schema \n" +
					"		IF v_global_value = p_text THEN \n" +
					"			-- Fix for unqualified schema in functions          \n" +
					"			EXECUTE 'DELETE FROM ' || pg_catalog.quote_ident(v_schema) || '.translations WHERE language = ' || pg_catalog.quote_literal(p_language) || ' AND key = ' || pg_catalog.quote_literal(p_key); \n" +
					"			-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
					"			-- DELETE FROM translations WHERE language = p_language AND key = p_key; \n" +
					"			RETURN 1; \n" +
					"		END IF; \n" +
					"	END IF; \n" +
					" \n" +
					"	-- Get the current value for this key \n" +
					"	 \n" +
					"	-- Fix for unqualified schema in functions          \n" +
					"	EXECUTE 'SELECT text FROM ' || pg_catalog.quote_ident(v_schema) || '.translations WHERE language = ' || pg_catalog.quote_literal(p_language) || ' AND key = ' || pg_catalog.quote_literal(p_key) INTO v_current_value; \n" +
					"	-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
					"	-- SELECT INTO v_current_value text FROM translations WHERE language = p_language AND key = p_key; \n" +
					"	 \n" +
					"	-- If the new text is the same as the current one, \n" +
					"	-- return \n" +
					"	IF v_current_value = p_text THEN \n" +
					"		RETURN 2; \n" +
					"	END IF; \n" +
					" \n" +
					"	-- If the new key isn't available in the global schema, \n" +
					"	-- then this is a user_created key \n" +
					"	v_user_created = v_schema <> 'global' AND v_global_value IS NULL; \n" +
					" \n" +
					"	-- If the current value is null then there is no \n" +
					"	-- current translation for this key, then we should \n" +
					"	-- insert it \n" +
					"	IF v_current_value IS NULL THEN     \n" +
					"		EXECUTE 'INSERT INTO ' || pg_catalog.quote_ident(v_schema) || '.translations (language, key, text, created_by, modified_by, user_created) VALUES (' || pg_catalog.quote_literal(p_language) || ', ' || pg_catalog.quote_literal(p_key) || ', ' || pg_catalog.quote_literal(p_text) || ', ' || pg_catalog.quote_literal(p_user) || ', ' || pg_catalog.quote_literal(p_user) || ', ' || pg_catalog.quote_literal(v_user_created) || ');'; \n" +
					" \n" +
					"		-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
					"		--INSERT INTO translations \n" +
					"		--(language, key, text, created_by, modified_by, user_created) \n" +
					"		--VALUES \n" +
					"		--(p_language, p_key, p_text, p_user, p_user, v_user_created); \n" +
					"		 \n" +
					"		RETURN 3; \n" +
					"	ELSE \n" +
					"		EXECUTE 'UPDATE ' || pg_catalog.quote_ident(v_schema) || '.translations SET text = ' || pg_catalog.quote_literal(p_text) || ', modified = now(), modified_by = ' || pg_catalog.quote_literal(p_user) || ' WHERE language = ' || pg_catalog.quote_literal(p_language) || ' AND key = ' || pg_catalog.quote_literal(p_key); \n" +
					" \n" +
					"		-- The code below will only work with multiple schemas after Postgresql 9.3 \n" +
					"		--UPDATE translations \n" +
					"		--SET text = p_text, \n" +
					"		--modified = now(), \n" +
					"		--modified_by = p_user \n" +
					"		--WHERE language = p_language AND key = p_key; \n" +
					"		 \n" +
					"		RETURN 4; \n" +
					"	END IF; \n" +
					" END; \n" +
					" $_$; ";

		String sql2 = "ALTER FUNCTION global.update_translation(character varying, character varying, character varying, integer) OWNER TO biblivre;"; 

		Statement st = con.createStatement();
		st.execute(sql);
		st.execute(sql2);
	}

	public void fixUpdateUserFunction(Connection con) throws SQLException {
		String sql = "CREATE OR REPLACE FUNCTION update_user_value(integer, character varying, character varying, character varying) RETURNS integer \n" +
					"  LANGUAGE plpgsql \n" +
					"    AS $_$ \n" +
					" DECLARE \n" +
					"	p_id ALIAS FOR $1; \n" +
					"	p_key ALIAS FOR $2; \n" +
					"	p_value ALIAS FOR $3; \n" +
					"	p_ascii ALIAS FOR $4; \n" +
					" \n" +
					"	v_schema character varying; \n" +
					"	v_current_value TEXT; \n" +
					" BEGIN \n" +
					"	v_schema = current_schema(); \n" +
					" \n" +
					"	IF v_schema = 'global' THEN \n" +
					"		-- Can't save user fields in global schema \n" +
					"		RETURN 1; \n" +
					"	END IF; \n" +
					" \n" +
					"	-- Get the current value for this key \n" +
					"	EXECUTE 'SELECT value FROM ' || pg_catalog.quote_ident(v_schema) || '.users_values WHERE user_id = ' || pg_catalog.quote_literal(p_id) || ' AND key = ' || pg_catalog.quote_literal(p_key) INTO v_current_value; \n" +
					"	-- SELECT INTO v_current_value value FROM users_values WHERE user_id = p_id AND key = p_key; \n" +
					" \n" +
					"	-- If the new value is the same as the current one, \n" +
					"	-- return \n" +
					"	IF v_current_value = p_value THEN \n" +
					"		RETURN 2; \n" +
					"	END IF; \n" +
					" \n" +
					"	-- If the current value is null then there is no \n" +
					"	-- current value for this key, then we should \n" +
					"	-- insert it \n" +
					"	IF v_current_value IS NULL THEN \n" +
					"		-- RAISE LOG 'inserting into schema %', v_schema; \n" +
					"		EXECUTE 'INSERT INTO ' || pg_catalog.quote_ident(v_schema) || '.users_values (user_id, key, value, ascii) VALUES (' || pg_catalog.quote_literal(p_id) || ', ' || pg_catalog.quote_literal(p_key) || ', ' || pg_catalog.quote_literal(p_value) || ', ' || pg_catalog.quote_literal(p_ascii) || ');'; \n" +
					"		--INSERT INTO users_values (user_id, key, value, ascii) VALUES (p_id, p_key, p_value, p_ascii); \n" +
					"		 \n" +
					"		RETURN 3; \n" +
					"	ELSE \n" +
					"		EXECUTE 'UPDATE ' || pg_catalog.quote_ident(v_schema) || '.users_values SET value = ' || pg_catalog.quote_literal(p_value) || ', ascii = ' || pg_catalog.quote_literal(p_ascii) || ' WHERE user_id = ' || pg_catalog.quote_literal(p_id) || ' AND key = ' || pg_catalog.quote_literal(p_key); \n" +
					"		-- UPDATE users_values SET value = p_value, ascii = p_ascii WHERE user_id = p_id AND key = p_key; \n" +
					" \n" +
					"		RETURN 4; \n" +
					"	END IF; \n" +
					" END; \n" +
					"$_$; ";

		String sql2 = "ALTER FUNCTION global.update_user_value(integer, character varying, character varying, character varying) OWNER TO biblivre;"; 

		Statement st = con.createStatement();
		st.execute(sql);
		st.execute(sql2);
	}
	
	public void fixUserNameAscii(Connection con) throws SQLException {
		if (this.checkColumnExistance("users", "name_ascii")) {
			return;
		}

		Statement st = con.createStatement();
		st.executeUpdate("ALTER TABLE users ADD COLUMN name_ascii character varying;");

		st = con.createStatement();
		ResultSet rs = st.executeQuery("SELECT id, name FROM users;");

		PreparedStatement pst = con.prepareStatement("UPDATE users SET name_ascii = ? WHERE id = ?");

		boolean run = false;
		while (rs.next()) {
			run = true;
			pst.setString(1, TextUtils.removeDiacriticals(rs.getString("name")));
			pst.setInt(2, rs.getInt("id"));
			pst.addBatch();
		}

		if (run) {
			pst.executeBatch();
		}
	}

	public void fixBackupTable(Connection con) throws SQLException {
		String sql = "CREATE TABLE global.backups (id serial NOT NULL, " +
					 "created timestamp without time zone NOT NULL DEFAULT now(), " +
					 "path character varying, " +
					 "schemas character varying NOT NULL, " +
					 "type character varying NOT NULL, " +
					 "scope character varying NOT NULL, " +
					 "downloaded boolean NOT NULL DEFAULT false, " +
					 "steps integer, " +
					 "current_step integer, " +
					 "CONSTRAINT \"PK_backups\" PRIMARY KEY (id) " +
					 ") WITH (OIDS=FALSE);";

		String sql2 = "ALTER TABLE global.backups OWNER TO biblivre;"; 

		Statement st = con.createStatement();
		st.execute(sql);
		st.execute(sql2);
	}

	public void fixVersionsTable() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "CREATE TABLE versions (" + 
						 "installed_versions character varying NOT NULL, CONSTRAINT \"PK_versions\" PRIMARY KEY (installed_versions))" + 
						 "WITH (OIDS=FALSE);";
	
			String sql2 = "ALTER TABLE backups OWNER TO biblivre;"; 
	
			Statement st = con.createStatement();
			st.execute(sql);
			st.execute(sql2);
		} finally {
			this.closeConnection(con);
		}
	}

	public void fixAuthoritiesAutoComplete() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "UPDATE biblio_form_subfields SET autocomplete_type = 'authorities' WHERE subfield = 'a' AND datafield in ('100', '110', '111');";	
			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}

	
	public void fixVocabularyAutoComplete() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "UPDATE biblio_form_subfields SET autocomplete_type = 'vocabulary' WHERE subfield = 'a' AND datafield in ('600', '610', '611', '630', '650', '651');";	
			Statement st = con.createStatement();
			st.execute(sql);
		} finally {
			this.closeConnection(con);
		}
	}
	
	public void fixHoldingCreationTable(Connection con) throws SQLException {
		String sql = "UPDATE holding_creation_counter HA " +
				"SET user_name = coalesce(U.name, L.login), user_login = L.login " +
				"FROM holding_creation_counter H " +
				"INNER JOIN logins L ON L.id = H.created_by " +
				"LEFT JOIN users U on U.login_id = H.created_by " +
				"WHERE HA.created_by = H.created_by;"; 

		Statement st = con.createStatement();
		st.execute(sql);
	}
	
	public void fixCDDBiblioBriefFormat(Connection con) throws SQLException {
		String sql = "UPDATE biblio_brief_formats " +
				"SET format = '${a}_{ }${2}' " + 
				"WHERE format = '${a}_{ }_{2}';"; 
		Statement st = con.createStatement();
		st.execute(sql);
	}
	
	public void fixAuthoritiesBriefFormat(Connection con) throws SQLException {
		String sql = "UPDATE authorities_brief_formats " +
				"SET format = '${a}_{; }${b}_{; }${c}_{ - }${d}' " + 
				"WHERE datafield = '110';"; 
		Statement st = con.createStatement();
		st.execute(sql);
	}
	
	public void addIndexingGroup(Connection con, RecordType recordType, String name, String datafields, boolean sortable) throws SQLException {
		StringBuilder deleteSql = new StringBuilder();			
		deleteSql.append("DELETE FROM ").append(recordType).append("_indexing_groups WHERE translation_key = ?;");

		PreparedStatement deletePst = con.prepareStatement(deleteSql.toString());
		
		deletePst.setString(1, name);
		deletePst.execute();
		
		
		StringBuilder sql = new StringBuilder();			
		sql.append("INSERT INTO ").append(recordType).append("_indexing_groups (translation_key, datafields, sortable) VALUES (?, ?, ?);");
		
		PreparedStatement pst = con.prepareStatement(sql.toString());
		
		pst.setString(1, name);
		pst.setString(2, datafields);
		pst.setBoolean(3, sortable);
		
		pst.execute();
	}

	public void updateIndexingGroup(Connection con, RecordType recordType, String name, String datafields) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(recordType).append("_indexing_groups SET datafields = ? WHERE translation_key = ?;");
		
		PreparedStatement pst = con.prepareStatement(sql.toString());
		
		pst.setString(1, datafields);
		pst.setString(2, name);
		
		pst.execute();
	}
	
	public void addBriefFormat(Connection con, RecordType recordType, String datafield, String format, Integer sortOrder) throws SQLException {
		StringBuilder deleteSql = new StringBuilder();			
		deleteSql.append("DELETE FROM ").append(recordType).append("_brief_formats WHERE datafield = ?;");

		PreparedStatement deletePst = con.prepareStatement(deleteSql.toString());
		
		deletePst.setString(1, datafield);
		deletePst.execute();
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(recordType).append("_brief_formats (datafield, format, sort_order) VALUES (?, ?, ?);");
		PreparedStatement pst = con.prepareStatement(sql.toString());
		
		pst.setString(1, datafield);
		pst.setString(2, format);
		pst.setInt(3, sortOrder);
		
		pst.execute();
	}
	
	public void updateBriefFormat(Connection con, RecordType recordType, String datafield, String format) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(recordType).append("_brief_formats SET format = ? WHERE datafield = ?;");
		
		PreparedStatement pst = con.prepareStatement(sql.toString());
		
		pst.setString(1, format);
		pst.setString(2, datafield);
		
		pst.execute();
	}
	
	public void invalidateIndex(Connection con, RecordType recordType) throws SQLException {
		StringBuilder deleteSql = new StringBuilder();			
		deleteSql.append("DELETE FROM ").append(recordType).append("_idx_sort WHERE record_id = 0;");

		Statement deletePst = con.createStatement();			
		deletePst.execute(deleteSql.toString());
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(recordType).append("_idx_sort (record_id, indexing_group_id, phrase, ignore_chars_count) VALUES (0, 1, ?, 0);");
		PreparedStatement pst = con.prepareStatement(sql.toString());
		
		pst.setString(1, "");
		
		pst.execute();
	}
	
	public void addDatafieldSortOrderColumns(Connection con, RecordType recordType) throws SQLException {
		String tableName = recordType + "_form_datafields";
		
		if (this.checkColumnExistance(tableName, "sort_order")) {
			return;
		}
		
		StringBuilder addDatafieldColumn = new StringBuilder();			
		addDatafieldColumn.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN sort_order integer;");
		Statement addDatafieldColumnSt = con.createStatement();			
		addDatafieldColumnSt.execute(addDatafieldColumn.toString());
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(tableName).append(" SET sort_order = (CAST(datafield as INT));");
		Statement updateSt = con.createStatement();			
		updateSt.execute(updateSql.toString());
	}
	
	public void addSubfieldSortOrderColumns(Connection con, RecordType recordType) throws SQLException {
		String tableName = recordType + "_form_subfields";

		if (this.checkColumnExistance(tableName, "sort_order")) {
			return;
		}

		StringBuilder addDatafieldColumn = new StringBuilder();			
		addDatafieldColumn.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN sort_order integer;");
		Statement addDatafieldColumnSt = con.createStatement();			
		addDatafieldColumnSt.execute(addDatafieldColumn.toString());
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(tableName).append(" SET sort_order = (CAST(datafield as INT) + ASCII(subfield));");
		Statement updateSt = con.createStatement();			
		updateSt.execute(updateSql.toString());
	}	
	
	public void addBriefFormatSortOrderColumns(Connection con, RecordType recordType) throws SQLException {
		String tableName = recordType + "_brief_formats";
		
		if (this.checkColumnExistance(tableName, "sort_order")) {
			return;
		}
		
		StringBuilder addDatafieldColumn = new StringBuilder();			
		addDatafieldColumn.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN sort_order integer;");
		Statement addDatafieldColumnSt = con.createStatement();			
		addDatafieldColumnSt.execute(addDatafieldColumn.toString());
		
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("UPDATE ").append(tableName).append(" SET sort_order = (CAST(datafield as INT));");
		Statement updateSt = con.createStatement();			
		updateSt.execute(updateSql.toString());
	}

	public void updateZ3950Address(Connection con, String name, String url) throws SQLException {
		String sql = "UPDATE z3950_addresses SET url = ? WHERE name = ?;";
		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, url);
		pst.setString(2, name);
		
		pst.execute();
	}
	
	
	public void replaceBiblivreVersion(Connection con)  throws SQLException {
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'Biblivre 4', 'Biblivre 5'), modified = now() WHERE text like '%Biblivre 4%';");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'Biblivre4', 'Biblivre 5'), modified = now() WHERE text like '%Biblivre4%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'Biblivre IV', 'Biblivre V'), modified = now() WHERE text like '%Biblivre IV%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'ersão 4.0', 'ersão 5.0'), modified = now() WHERE text like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'ersión 4.0', 'ersión 5.0'), modified = now() WHERE text like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE translations SET text = replace(text, 'ersion 4.0', 'ersion 5.0'), modified = now() WHERE text like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'Biblivre IV', 'Biblivre V'), modified = now() WHERE value like '%Biblivre IV%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'ersão 4.0', 'ersão 5.0'), modified = now() WHERE value like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'ersión 4.0', 'ersión 5.0'), modified = now() WHERE value like '%ersão 4.0%'");
		con.createStatement().execute("UPDATE configurations SET value = replace(value, 'ersion 4.0', 'ersion 5.0'), modified = now() WHERE value like '%ersão 4.0%'");
	}
}