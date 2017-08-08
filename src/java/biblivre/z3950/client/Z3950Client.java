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
package biblivre.z3950.client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jzkit.search.provider.iface.IRQuery;
import org.jzkit.search.provider.iface.Searchable;
import org.jzkit.search.provider.z3950.Z3950ServiceFactory;
import org.jzkit.search.util.RecordModel.ArchetypeRecordFormatSpecification;
import org.jzkit.search.util.RecordModel.iso2709;
import org.jzkit.search.util.ResultSet.IRResultSet;
import org.jzkit.search.util.ResultSet.IRResultSetStatus;
import org.jzkit.search.util.ResultSet.ReadAheadEnumeration;
import org.marc4j.marc.Record;
import org.springframework.context.ApplicationContext;

import biblivre.core.utils.Pair;
import biblivre.core.utils.TextUtils;
import biblivre.marc.MarcUtils;
import biblivre.z3950.Z3950AddressDTO;

public class Z3950Client {
	
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private ApplicationContext z3950Context;
	private Z3950ServiceFactory factory;
	private static final String QUERY_PREFIX = "@attrset bib-1 @attr 1=";
	private static final String CHARSET = "UTF-8";

	public void setZ3950Context(ApplicationContext z3950Context) {
		this.z3950Context = z3950Context;
	}

	public void setFactory(Z3950ServiceFactory factory) {
		this.factory = factory;
	}

	public Z3950ServiceFactory getFactory() {
		return this.factory;
	}

	public ApplicationContext getZ3950Context() {
		return this.z3950Context;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> search(Z3950AddressDTO address, Pair<String, String> search, int limit) {
		List<Record> listRecords = new ArrayList<Record>();

		this.factory.setHost(address.getUrl());
		this.factory.setPort(address.getPort());
		this.factory.setCharsetEncoding(CHARSET);

		this.factory.setApplicationContext(this.z3950Context);
		this.factory.setDefaultRecordSyntax("usmarc");
		this.factory.setDefaultElementSetName("F");

		this.factory.setDoCharsetNeg(true);

		this.factory.getRecordArchetypes().put("Default","usmarc::F");
		this.factory.getRecordArchetypes().put("FullDisplay","usmarc::F");
		this.factory.getRecordArchetypes().put("BriefDisplay","usmarc::B");

		final String qry = QUERY_PREFIX + search.getLeft() + " \"" + TextUtils.removeDiacriticals(search.getRight()) + "\"";

		IRQuery query = new IRQuery();
		query.collections = new Vector();
		query.collections.add(address.getCollection());
		query.query = new org.jzkit.search.util.QueryModel.PrefixString.PrefixString(qry);
		
		Searchable searchable = null;
		IRResultSet result = null;

		try {
			searchable = this.factory.newSearchable();
			searchable.setApplicationContext(this.z3950Context);
			result = searchable.evaluate(query);

			result.addObserver(new z3950Observer());
			//TODO IMPLEMENT THE OBSERVER BELOW TO CHECK FOR DIFFERENT ERROR TYPES AND RESPOND ACCORDINGLY
			// Wait without timeout until result set is complete or failure
			result.waitForStatus(IRResultSetStatus.COMPLETE | IRResultSetStatus.FAILURE, 0);
			if (result.getStatus() == IRResultSetStatus.FAILURE) {
				this.log.error("IRResultSetStatus == FAILURE");
			}
			if (result.getFragmentCount() == 0) {
				return listRecords;
			}

			
			Enumeration e = new ReadAheadEnumeration(result, new ArchetypeRecordFormatSpecification("Default"));
			int errorRecords = 0;
			int validRecords = limit;

			Record record = null;
			while (e.hasMoreElements() && validRecords > 0) {
				iso2709 o = (iso2709) e.nextElement();
				if (o == null) {
					continue;
				}
				
				record = MarcUtils.iso2709ToRecord((byte[]) o.getOriginalObject());

				if (record != null) {
					listRecords.add(record);
					validRecords--;
				} else {
					errorRecords++;
				}
			}
			
			if (errorRecords > 0) {
				this.log.warn("Total number of records that failed the conversion: " + errorRecords);
			}

		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		} finally {
			if (result != null) {
				result.close();
			}
			if (searchable != null) {
				searchable.close();
			}
		}

		return listRecords;
	}

}

class z3950Observer implements Observer {

	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println();
	}
	
	
}
