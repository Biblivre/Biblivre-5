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
var Schemas = Schemas || {};

Schemas.createSchemaSuggestion = function(name) {
	name = name.trim().toLowerCase().replace(/\s+/g, '_').replace(/[^a-z0-9_]/g, '').substring(0, 60);
	return name;
};

Schemas.url = [window.location.protocol, '//', window.location.host, window.location.pathname].join('');

Schemas.showError = function() {
	Core.popup({
		closeable: false,
		title: _('multi_schema.manage.error'),
		description: _('multi_schema.manage.error.description'),
		okText: _('multi_schema.manage.button.show_log'),
		okHandler: function() {
			window.location.href = window.location.pathname + '?controller=log';
		}
	});
};

Schemas.create = function(button) {
	var schema = $('input[name="schema"]').val();
	var title = $('input[name="title"]').val();
	var subtitle = $('input[name="subtitle"]').val();
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingButton: button,
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'multi_schema',
			action: 'create',
			schema: schema,
			title: title,
			subtitle: subtitle
		},
		success: function(response) {
			if (!response.success) {
				Core.msg(response);			

				if (response.errors) {
					Core.formErrors(response.errors);
				}

				if (response.log) {
					Schemas.showError();			
				}				
			} else {
				var html = [
					'<div class="library">', '<a href="', schema, '/" target="_blank">', title, '</a>',
					'<div class="subtitle">', subtitle, '</div>',
					'<div><span class="address">', Schemas.url, '</span><strong>', schema, '</strong>/</div>',
					'<a href="javascript:void(0);" onclick="Schemas.toggle(\'', schema, '\', false, this);" class="enable">[', _('multi_schema.manage.enable'), ']</a> ',
					'<a href="javascript:void(0);" onclick="Schemas.deleteSchema(\'', schema, '\', this);" class="enable">[', _('common.delete'), ']</a> ',
					'<a href="javascript:void(0);" onclick="Schemas.toggle(\'', schema, '\', true, this);" class="disable">[', _('multi_schema.manage.disable'), ']</a> ',
					'</div>'
				].join('');
				
				$('#multischema').append(html);
			}
		}
	});
};

Schemas.selectedBackup = null;
Schemas.maxRestoreCount = 20;
Schemas.restore = function(metadata) {
	Schemas.selectedBackup = metadata;
	
	$('#multischema, #multischema_select_restore').hide();
	$('#multischema_partial_restore, #partial_restore_button').show();
	
	delete Schemas.selectedBackup.schemas.global;
	
	var i = 0;
	for (var schema in Schemas.selectedBackup.schemas) {
		if (Schemas.selectedBackup.schemas.hasOwnProperty(schema)) {
			i++;
		}
	}
	
	var div = $('#schemas_match').processTemplate(metadata);
	

	if (i < Schemas.maxRestoreCount) {
		$('#multischema_full_restore').show();
	} else {
		var restoreLimit = $('#multischema_restore_limit').show().find('ul');

		var select = function(min, max) {
			var libraries = $('#schemas_match .library');
			libraries.hide().find('input[value=skip]').prop('checked', true);

			libraries = libraries.filter(':lt(' + (max + 1) + ')');
			
			if (min > 0) {
				libraries = libraries.filter(':gt(' + (min - 1) + ')');
			}
			
			libraries.show().find('input[value=original]').prop('checked', true);		
		};
		
		for (var j = 0; j < i; j += Schemas.maxRestoreCount) {
			(function(j) {
				var min = j;
				var max = Math.min(j + Schemas.maxRestoreCount - 1, i - 1);
				
				var li = $('<li></li>').appendTo(restoreLimit);
				
				$('<a href="#"></a>').text(_('multi_schema.backup.display_and_select_libraries', { min: (min + 1), max: (max + 1) })).click(function(e) {
					select(min, max);
					
					e.stopPropagation();
					e.preventDefault();
				}).appendTo(li);
			})(j);
		}
 
		select(0, Schemas.maxRestoreCount - 1);
	}
	
	div.find('.address').text(function() {
		return Schemas.url;
	});
	
	div.find(':input[type="text"]').anyChange(function() {
		var el = $(this);
		var div = el.closest('.new_schema');

		div.find(':input[type="radio"]').prop('checked', true);
		
		if (Schemas.loadedSchemas[el.val()]) {
			div.find('.warn').show();
		} else {
			div.find('.warn').hide();
		}
	});
	
	$('#content_outer').stop().scrollTo(0);
};

Schemas.fullRestore = function() {
	if (!Schemas.selectedBackup) {
		return;
	}
	
	Administration.setup.biblivre4Restore(Schemas.selectedBackup.file, {
		type: 'complete'
	});
};

Schemas.partialRestore = function() {
	if (!Schemas.selectedBackup) {
		return;
	}
	
	var params = {
		type: 'partial'	
	};
	
	var schemas = [];
	
	for (var key in Schemas.selectedBackup.schemas) {
		if (!Schemas.selectedBackup.schemas.hasOwnProperty(key)) {
			continue;
		}
		
		switch ($('#schemas_match').find(':radio:checked[name="restore_library_' + key + '"]').val()) {
			case 'original':
				schemas.push(key + ':' + key);
				break;
								
			case 'new':
				var name = $('#schemas_match').find(':input[name="schema_name_' + key + '"]').val();
				schemas.push(key + ':' + name);
				break;
				
			case 'skip':
				break;
		}
	}
	
	params.schemas_map = schemas.join();

	Administration.setup.biblivre4Restore(Schemas.selectedBackup.file, params);
};

Schemas.toggle = function(schema, disable, button) {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'multi_schema',
			action: 'toggle',
			schema: schema,
			disable: disable
		},
		success: function(response) {
			if (!response.success) {
				Core.msg(response);			
			} else {
				$(button).closest('div').toggleClass('disabled', disable);
			}
		}
	});
};

Schemas.deleteSchema = function(schema, button) {
	Schemas.showConfirm('manage.drop_schema', function() {
		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			loadingTimedOverlay: true,		
			data: {
				controller: 'json',
				module: 'multi_schema',
				action: 'delete_schema',
				schema: schema
			},
			success: function(response) {
				if (!response.success) {
					Core.msg(response);			
				} else {
					$(button).closest('div').remove();
				}
			}
		});
	});
};

$(document).ready(function() {
	var schema = $('#schema_schema');

	var lastSuggestion = '';

	$('#schema_title').anyChange(function(event) {
		var val = $(this).val();
		var suggestion = Schemas.createSchemaSuggestion(val);
		if (suggestion != lastSuggestion) {
			lastSuggestion = suggestion;
			lastLocalSuggestion = suggestion;
			$('#schema_schema').val(Schemas.createSchemaSuggestion(suggestion));
		}
	});

	var lastLocalSuggestion = '';
	schema.anyChange(function(event) {
		var el = $(this);
		var val = el.val();

		var suggestion = Schemas.createSchemaSuggestion(val);
		if (suggestion != lastSuggestion || suggestion != lastLocalSuggestion) {
			lastLocalSuggestion = suggestion;
			el.val(suggestion);
		}
	});
	
	var schemaWidth = schema.width();
	var addressWidth = $('#address').text(Schemas.url).width();
	schema.width(schemaWidth - addressWidth).closest('div').css('white-space', 'nowrap');
	
	$('.address').text(function() {
		return Schemas.url + $(this).text();
	});
});


Schemas.showConfirm = function(action, callback) {
	Core.popup({
		title: _('multi_schema.' + action + '.confirm_title'),
		description: _('multi_schema.' + action + '.confirm_description'),
		confirm: _('multi_schema.' + action + '.confirm'),
		okText: _('common.yes'),
		cancelText: _('common.no'),
		okHandler: $.proxy(callback, this),
		cancelHandler: $.proxy($.noop, this)
	});
};
