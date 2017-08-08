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

Administration.backup = {};
Administration.reindex = {};
Administration.reinstall = {};

$(document).ready(function() {
	
	var div = $('#last_backups_list');
	if (div.size() > 0) {
		div.setTemplateElement('last_backups_list_template');
	}
	
	Administration.backup.list();
});

//BACKUP

Administration.backup.selectedId = null;

Administration.backup.list = function(id) {
	$('#last_backups_list').empty();

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingHolder: '#last_backups_list',
		data: {
			controller: 'json',
			module: 'administration.backup',
			action: 'list'
		},
		success: function(response) {
			$('#last_backups_list').processTemplate(response);
			if (id) {
				Administration.backup.download(id, 2000);
			}
		}
	});
};

Administration.backup.showAll = function(el) {
	var obj = $(el);
	
	obj.siblings('.hidden_backup').removeClass('hidden_backup');
	obj.remove();
};

Administration.backup.submit = function(type) {
	var schemas = [];
	
	$('#multischema :checkbox:checked[name="library"]').each(function() {
		schemas.push($(this).val());
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.backup',
			action: 'prepare',
			schemas: schemas.join(),
			type: type
		},
		success: function(response) {
			if (!response.success) {
				Administration.backup.cancel();
				Core.msg(response);
				return;
			}

			$('.system_warning_backup').remove();
			
			Administration.backup.selectedId = response.id;
			Administration.backup.progress(100);

			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				data: {
					controller: 'json',
					module: 'administration.backup',
					action: 'backup',
					id: Administration.backup.selectedId
				},
				success: function(response) {
					if (!response.success) {
						Administration.backup.cancel();
						Core.msg(response);
						return;
					}
				}
			});
		}
	});
	Administration.backup.showPopupProgress();
};

Administration.backup.showPopupProgress = function() {
	Core.showOverlay();

	$('#backup_popup')
		.appendTo('body')
		.show()
		.center();
	
	$('#backup_popup .progress').progressbar(); 
};

Administration.backup.progressTimeout = null;
Administration.backup.progressXHR = null;
Administration.backup.progress = function(delay) {
	if (Administration.backup.selectedId == null) {
		return;
	} 
	
	if (delay) {
		Administration.backup.progressTimeout = setTimeout(Administration.backup.progress, delay);
		return;
	}
	
	Administration.backup.progressXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.backup',
			action: 'progress',
			id: Administration.backup.selectedId
		},
		success: function(response) {
			if (!response.success) {
				Administration.backup.cancel();
				Core.msg(response);
				return;
			}

			$('#backup_popup .progress').progressbar(response);

			if (response.complete) {
				var id = Administration.backup.selectedId;

				Administration.backup.cancel();
				Administration.backup.list(id);

				Core.msg(_('administration.maintenance.backup.auto_download'), 'success');								
			}
		},
		complete: function() {
			Administration.backup.progress(500);
		}
	});
};

Administration.backup.download = function(id, delay) {
	if (delay) {
		$('#last_backups_list a[rel=' + id + '] .backup_never_downloaded').text(_('administration.maintenance.backup.auto_download'));

		setTimeout(function() {
			Administration.backup.download(id);	
		}, delay);

		return;
	}
	
	window.open($('#last_backups_list a[rel=' + id + ']').attr('href'));
};

Administration.backup.cancel = function(base) {
	Administration.backup.selectedId = null;

	clearTimeout(Administration.backup.progressTimeout);
	if (Administration.backup.progressXHR) {
		Administration.backup.progressXHR.abort();
		Administration.backup.progressXHR = null;
	}
	
	Core.hideOverlay();
	$('#backup_popup').hide();
};


// REINDEX

Administration.reindex.selectedType = null;
Administration.reindex.confirm = function(type) {
	Administration.reindex.selectedType = type;

	Core.showOverlay();
	Administration.reindex.showPopupButtons();

	var popup = $('#reindex_popup');
	
	popup
		.appendTo('body')
		.show()
		.center()
		.height(popup.height());
	
};

Administration.reindex.showPopupButtons = function() {
	$('#reindex_popup .progress').hide();
	$('#reindex_popup .confirm, #reindex_popup .buttons').show();
	$('#reindex_popup .close').css('visibility', 'visible');
};

Administration.reindex.showPopupProgress = function() {
	var confirm = $('#reindex_popup .confirm, #reindex_popup .buttons');
	var progress = $('#reindex_popup .progress'); 
	var close = $('#reindex_popup .close');

	progress
		.progressbar()
		.show();
	
	confirm.hide();
	
	close.css('visibility', 'hidden');
};

Administration.reindex.submit = function() {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.indexing',
			action: 'reindex',
			record_type: Administration.reindex.selectedType
		},
		success: function(response) {
			if (!response.success) {
				Administration.reindex.cancel();
				Core.msg(response);
				return;
			}
		}
	});
	
	$('.system_warning_reindex').remove();

	Administration.reindex.showPopupProgress();
	Administration.reindex.progress(1000);
};

Administration.reindex.progressTimeout = null;
Administration.reindex.progressXHR = null;
Administration.reindex.progress = function(delay) {
	if (!Administration.reindex.selectedType) {
		return;
	}

	if (delay) {
		Administration.reindex.progressTimeout = setTimeout(Administration.reindex.progress, delay);
		return;
	}
	
	Administration.reindex.progressXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.indexing',
			action: 'progress',
			record_type: Administration.reindex.selectedType
		},
		success: function(response) {
			if (!response.success) {
				Administration.reindex.cancel();
				Core.msg(response);
				return;
			}

			$('#reindex_popup .progress').progressbar(response);

			if (response.complete) {
				Administration.reindex.cancel();
				Core.msg(_('administration.maintenance.reindex.success'), 'success');
			}
		},
		complete: function() {
			Administration.reindex.progress(500);
		}
	});
};

Administration.reindex.cancel = function(base) {
	Administration.reindex.selectedType = null;

	clearTimeout(Administration.reindex.progressTimeout);
	if (Administration.reindex.progressXHR) {
		Administration.reindex.progressXHR.abort();
		Administration.reindex.progressXHR = null;
	}
	
	Core.hideOverlay();
	$('#reindex_popup').hide();
};


Administration.reinstall.confirm = function() {
	Core.popup({
		title: _('administration.maintenance.reinstall.confirm.title'),
		description: _('administration.maintenance.reinstall.confirm.description'),
		confirm: _('administration.maintenance.reinstall.confirm.question'),
		okText: _('common.yes'),
		cancelText: _('common.no'),
		okHandler: function() {
			window.location.href = window.location.pathname + '?force_setup=true';
		},
		cancelHandler: $.proxy($.noop, this)
	});
};