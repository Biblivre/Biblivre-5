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
package biblivre.circulation.user_cards;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.json.JSONException;

import biblivre.circulation.user.UserBO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.LabelPrintDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;

public class Handler extends AbstractHandler {

	public void createPdf(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String printId = UUID.randomUUID().toString();

		LabelPrintDTO print = new LabelPrintDTO();
		String idList = request.getString("id_list");

		String[] idArray = idList.split(",");
		Set<Integer> ids = new TreeSet<Integer>();
		try {
			for (int i = 0; i < idArray.length; i++) {
				ids.add(Integer.valueOf(idArray[i]));
			}			
		} catch(Exception e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
		}
		
		print.setIds(ids);
		print.setOffset(request.getInteger("offset"));
		print.setWidth(request.getFloat("width"));
		print.setHeight(request.getFloat("height"));
		print.setColumns(request.getInteger("columns"));
		print.setRows(request.getInteger("rows"));
		
		request.setSessionAttribute(schema, printId, print);
		
		try {
			this.json.put("uuid", printId);
		} catch(JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}

	public void downloadPdf(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String printId = request.getString("id");
		
		LabelPrintDTO dto = (LabelPrintDTO) request.getSessionAttribute(schema, printId);

		UserBO ubo = UserBO.getInstance(schema);

		final DiskFile exportFile = ubo.printUserCardsToPDF(dto, request.getTranslationsMap());
		ubo.markAsPrinted(dto.getIds());

		this.setFile(exportFile);

		this.setCallback(new HttpCallback() {
			@Override
			public void success() {
				try {
					exportFile.delete();
				} catch (Exception e) {}
			}
		});
	}
}
