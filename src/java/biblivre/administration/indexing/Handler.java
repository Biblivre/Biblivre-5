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

import java.util.Date;

import org.json.JSONException;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;

public class Handler extends AbstractHandler {
	
	public void reindex(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String strRecordType = request.getString("record_type", "biblio");

		RecordType recordType = RecordType.fromString(strRecordType);
		if (recordType == null) {
			this.setMessage(ActionResult.WARNING, "administration.maintenance.reindex.error.invalid_record_type");
			return;
		}

		long start = 0;
		long end = 0;

		try {
			IndexingBO bo = IndexingBO.getInstance(schema);

			start = new Date().getTime();
			bo.reindex(recordType);
			end = new Date().getTime();

			request.setSessionAttribute(schema, "system_warning_reindex", false);
		} finally {
			
		}
		
		try {
			this.json.put("success", true);
			this.json.put("time", (end - start) / 1000.0);
		} catch (JSONException e) {}
	}

	public void progress(ExtendedRequest request, ExtendedResponse response) {
		// Remember that this will only work if there is a sortable indexing_group for the recordType.
		String schema = request.getSchema();
		String strRecordType = request.getString("record_type", "biblio");

		RecordType recordType = RecordType.fromString(strRecordType);
		if (recordType == null) {
			this.setMessage(ActionResult.WARNING, "administration.maintenance.reindex.error.invalid_record_type");
			return;
		}

		IndexingBO bo = IndexingBO.getInstance(schema);

		int progress[] = bo.getReindexProgress(recordType);

		try {
			this.json.put("success", true);
			this.json.put("current", progress[0]);
			this.json.put("total", progress[1]);
			this.json.put("complete", progress[0] == progress[1]);
		} catch (JSONException e) {}
	}
}
