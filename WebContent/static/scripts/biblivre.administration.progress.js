/**
 *  Este arquivo é parte do Biblivre5.
 *  
 *  Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 *  Licença, ou (caso queira) qualquer versão posterior.
 *  
 *  Este programa é distribuído na esperança de que possa ser  útil, 
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *  
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 * 
 */
var Administration = Administration || {};

Administration.progress = {};

Administration.progress.showPopupProgress = function() {
	Core.showOverlay();

	$('#progress_popup')
		.appendTo('body')
		.show()
		.center();
		
	$('#progress_popup .progress').progressbar(); 
};

Administration.progress.progressTimeout = null;
Administration.progress.progressXHR = null;
Administration.progress.progress = function(delay) {
	if (delay) {
		Administration.progress.progressTimeout = setTimeout(Administration.progress.progress, delay);
		return;
	}
	
	Administration.progress.progressXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.setup',
			action: 'progress'
		},
		success: function(response) {
			if (!response.success) {
				return;
			}

			$('#progress_popup .progress').progressbar(response);

			if (response.complete) {
				Administration.progress.cancel();
				if (Administration.progress.autoReload) {
					window.location.reload();
				}
			}
		},
		complete: function() {
			Administration.progress.progress(500);
		}
	});
};

Administration.progress.cancel = function() {
	clearTimeout(Administration.progress.progressTimeout);
	if (Administration.progress.progressXHR) {
		Administration.progress.progressXHR.abort();
		Administration.progress.progressXHR = null;
	}
	
	Core.hideOverlay();
	$('#progress_popup').hide();
};


var Upload = Upload || {};

Upload.showUploadPopup = function() {
	Core.showOverlay();

	$('#upload_popup')
		.appendTo('body')
		.show()
		.center();
		
	$('#upload_popup .progress').progressbar().continuousProgress();
};

Upload.stopUploadProgress = function() {
	$('#upload_popup .progress').stopContinuousProgress();
};

Upload.advanceUploadProgress = function(current, total, percentComplete) {
	Upload.stopUploadProgress();
	
	$('#upload_popup .progress').progressbar({
		current: current,
		total: total
	});
};

Upload.cancel = function() {
	Core.hideOverlay();
	$('#upload_popup').hide();
};

Upload.continuousProgress = function() {
	$('#upload_popup .progress').progressbar().continuousProgress();
};