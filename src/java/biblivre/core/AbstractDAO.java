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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.postgresql.PGConnection;

import biblivre.core.exceptions.DAOException;
import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;

public abstract class AbstractDAO {
	private static Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

	protected Logger logger = Logger.getLogger(this.getClass());
	private String schema;
	private String dataSourceName;

	private static HashMap<Pair<Class<? extends AbstractDAO>, String>, AbstractDAO> instances = new HashMap<Pair<Class<? extends AbstractDAO>, String>, AbstractDAO>();

	protected AbstractDAO() {
	}

	protected static AbstractDAO getInstance(Class<? extends AbstractDAO> cls, String schema) {
		return AbstractDAO.getInstance(cls, schema, "biblivre4");
	}
	
	protected static AbstractDAO getInstance(Class<? extends AbstractDAO> cls, String schema, String dataSourceName) {
		Pair<Class<? extends AbstractDAO>, String> pair = new Pair<Class<? extends AbstractDAO>, String>(cls, schema + ":" + dataSourceName);
		AbstractDAO instance = AbstractDAO.instances.get(pair);

		if (instance == null) {
			if (!AbstractDAO.class.isAssignableFrom(cls)) {
				throw new IllegalArgumentException("DAO: getInstance: Class " + cls.getName() + " is not a subclass of DAO.");
			}

			try {
				instance = cls.newInstance();
				instance.setSchema(schema);
				instance.setDataSourceName(dataSourceName);

				AbstractDAO.instances.put(pair, instance);
			} catch(Exception ex) {}
		}

		return instance;
	}

	public boolean testDatabaseConnection() {
		Connection con = null;

		try {
			con = this.getConnection();
			Statement st = con.createStatement();
			st.executeQuery("select version();");
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			this.closeConnection(con);
		}
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSchema() {
		return StringUtils.defaultString(this.schema, Constants.GLOBAL_SCHEMA);
	}
	
	public String getDataSourceName() {
		return this.dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public static Map<String, DataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	public static void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
		AbstractDAO.dataSourceMap = dataSourceMap;
	}

	public boolean isGlobalSchema() {
		return this.getSchema().equals(Constants.GLOBAL_SCHEMA);
	}

	protected final Connection getConnection() throws SQLException {
		Connection con = this.getDataSource().getConnection();
		
		/*
		DatabaseMetaData dbmd = con.getMetaData(); 
		System.out.println("=====  Driver info ====="); 
		System.out.println("DriverName: " + dbmd.getDriverName() ); 
		System.out.println("DriverVersion: " + dbmd.getDriverVersion() ); 
		System.out.println("DriverMajorVersion: " + dbmd.getDriverMajorVersion()); 
		*/
		
		if (this.getSchema() != null) {
			con.createStatement().execute("SET search_path = '" + this.getSchema() + "', public, pg_catalog;");
		}

		return con;
	}

	private final DataSource getDataSource() {
		DataSource ds = dataSourceMap.get(this.getDataSourceName());
		if (ds == null) {
			try {
				InitialContext cxt = new InitialContext();

				ds = (DataSource) cxt.lookup("java:comp/env/jdbc/" + this.getDataSourceName());
			} catch (Exception e) {
				throw new DAOException(e);
			}
		}

		if (ds == null) {
			this.logger.error("[DAO.Constructor] Data Source not found.");
			throw new RuntimeException("Data Source not found!");
		} else {
			dataSourceMap.put(this.getDataSourceName(), ds);
		}

		return ds;
	}
	
	protected final void closeConnection(Connection con) {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	protected final void rollback(Connection con) {
		try {
			if (con != null) {
				con.rollback();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	protected final void commit(Connection con) {
		try {
			if (con != null) {
				con.commit();
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public final Integer getNextSerial(String sequence) {
		Integer serial = 0;
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT nextval('" + sequence + "') FROM " + sequence;
			PreparedStatement pst = con.prepareStatement(sql);
			
			ResultSet rs = pst.executeQuery();

			if ((rs != null) && rs.next()) {
				serial = rs.getInt(1);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

		return serial;
	}
	
	public final void fixSequence(String sequence, String tableName) {
		this.fixSequence(sequence, tableName, "id");
	}
	
	public final void fixSequence(String sequence, String tableName, String tableIdColumnName) {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT setval('" + sequence + "', coalesce((SELECT max(" + tableIdColumnName + ") + 1 FROM " + tableName + "), 1), false);";
			PreparedStatement pst = con.prepareStatement(sql);
			
			pst.executeQuery();
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			this.closeConnection(con);
		}

	}

	public final boolean checkFunctionExistance(String functionName) throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT count(*) as count FROM pg_catalog.pg_proc WHERE proname = ?;"; 

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, functionName);
			
			ResultSet rs = pst.executeQuery();
			
			if (rs.next()) {
				int count = rs.getInt("count");
				
				return count > 0;
			}
			
			return false;
		} finally {
			this.closeConnection(con);
		}
	}
	
	public final boolean checkColumnExistance(String tableName, String columnName) throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT count(*) as count FROM information_schema.columns WHERE table_schema = ? and table_name = ? and column_name = ?;"; 

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, this.getSchema());
			pst.setString(2, tableName);
			pst.setString(3, columnName);
			
			ResultSet rs = pst.executeQuery();
			return rs.next() && rs.getInt("count") == 1; 
		} finally {
			this.closeConnection(con);
		}
	}

	
	public final boolean checkTableExistance(String tableName) throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT count(*) as count FROM information_schema.tables WHERE table_schema = ? and table_name = ?;"; 

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, this.getSchema());
			pst.setString(2, tableName);
			
			ResultSet rs = pst.executeQuery();
			return rs.next() && rs.getInt("count") == 1; 
		} finally {
			this.closeConnection(con);
		}
	}
	
	
	public final String getPostgreSQLVersion() throws SQLException {
		Connection con = null;

		try {
			con = this.getConnection();

			String sql = "SELECT version() as version;"; 

			Statement st = con.createStatement();
			
			ResultSet rs = st.executeQuery(sql);
			
			if (rs.next()) {
				return rs.getString("version");
			}
			
			return ""; 
		} finally {
			this.closeConnection(con);
		}
	}
	
	protected final boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
	    ResultSetMetaData metadata = rs.getMetaData();
	    int columns = metadata.getColumnCount();
	    for (int x = 1; x <= columns; x++) {
	        if (columnName.equals(metadata.getColumnName(x))) {
	            return true;
	        }
	    }
	    return false;
	}
	
	protected PGConnection getPGConnection(Connection con) {
		PGConnection pgcon = null;

		try {
			Class<?> D = Class.forName("org.apache.tomcat.dbcp.dbcp.DelegatingConnection");
			Constructor<?> c = D.getConstructor(Connection.class);
			Object o = c.newInstance(con);
			Method m = D.getMethod("getInnermostDelegate");

			pgcon =	 (PGConnection) m.invoke(o);
		} catch (Exception e) {
			this.logger.info("Skipping org.apache.tomcat.dbcp.dbcp.DelegatingConnection");
		}

		if (pgcon == null) {
			try {
				Class<?> D = Class.forName("org.apache.commons.dbcp.DelegatingConnection");
				Constructor<?> c = D.getConstructor(Connection.class);
				Object o = c.newInstance(con);
				Method m = D.getMethod("getInnermostDelegate");

				pgcon =	 (PGConnection) m.invoke(o);
			} catch (Exception e) {
				this.logger.info("org.apache.commons.dbcp.DelegatingConnection");
				e.printStackTrace();
			}
		}
		
		if (pgcon == null) {
			try {
				pgcon = (PGConnection) con.unwrap(PGConnection.class);
			} catch (Exception e) {
				this.logger.info("getInnermostDelegate Unwrap");
				e.printStackTrace();
			}
		}

		return pgcon;
	}
	
}
