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
var Translation = {
	uploadData: [],
	uploadPopup: null
};

$(document).ready(function() {
	Translation.uploadPopup = $('#upload_popup');
	
	Translations.displayTree();
	
	$('#translations_filter').click(function() {
		var checkbox = $(this);
		var checked = checkbox.attr('checked');
		
		if (!checked) {
			$('#translations_tree').children().show();
		} else {
			$('#translations_tree').children().hide();
			$('#translations_tree input[empty]').closest('div').show();
		}
	});
});


Translations.displayTree = function() {
	var translations = {};
	
	ld.forEach(Translations.translations, function(value, key) {
		var ns = key.split(/\./);
		var obj = translations;
		var parent = null;
		var k = null;
		var lastK = null;
		
		for (var i = 0; i < ns.length - 1; i++) {
			k = ns[i];
			
			if (typeof obj[k] == 'string') {
				obj[k] = {
					_root: obj[k]
				};
			} else if (!obj[k]) {
				obj[k] = {};
			}
			
			parent = obj;
			obj = obj[k];
			lastK = k;
		}
		
		k = ns[ns.length - 1];
		
		if (typeof obj[k] == 'string') {
			var root = obj[k];
			parent[lastK] = {
				_root: root
			};
			
			parent[lastK][k] = value;
		} else if (typeof obj[k] == 'object') {
			obj[k]['_root'] = value;
		} else {
			obj[k] = value;
		}
	});
	
	var sortKeys = function (a, b) {
		if (typeof translations[a] == 'string' && typeof translations[b] == 'string') {
			return (a > b) ? 1 : (b > a) ? -1 : 0;
		}
		
		if (typeof translations[a] == 'string') {
			return -1;
		}
		
		if (typeof translations[b] == 'string') {
			return 1;
		}

		return (a > b) ? 1 : (b > a) ? -1 : 0;
	};
	
	var keys = ld.keys(translations).sort(sortKeys);
	
	var div = $('#translations_tree').on('input', 'input', function() {
		var t = $(this);
		if (t.val() != t.data('original')) {
			t.addClass('changed');
		} else {
			t.removeClass('changed');
		}
	});
	
	var recurse = function(myParent, myTree, root) {
		return function(key) {
			if (key == '_root') {
				var d = $('<div></div>');
				$('<label></label>').text(root).appendTo(d);
				var value = Translations.translations[root];
				var input = $('<input type="text" />').attr('name', root).val(value).data('original', value).appendTo(d);
				if (value == '' || value[0] == '_' && value[1] == '_') {
					input.attr('empty', true);
				}
				d.appendTo(myTree);
				return;
			}
			
			var completeKey = (root) ? root + '.' + key : key; 
			
			var myObj = myParent[key];
			
			if (typeof myObj == 'string') {
				var d = $('<div></div>');
				$('<label></label>').text(completeKey).appendTo(d);
				var value = Translations.translations[completeKey];
				var input = $('<input type="text" />').attr('name', completeKey).val(value).data('original', value).appendTo(d);
				if (value == '' || value[0] == '_' && value[1] == '_') {
					input.attr('empty', true);
				}
				d.appendTo(myTree);
			} else {
				//var field = $('<fieldset />').append('<legend>' + completeKey + '</legend>').appendTo(myTree);
				var myKeys = ld.keys(myObj).sort(sortKeys);
				ld.forEach(myKeys, recurse(myObj, myTree, completeKey));
			}
		};
	};
	
	ld.forEach(keys, recurse(translations, div, ''));
};

Translation.dump = function(language) {
	
	params = $.extend({}, {
		controller: 'json',
		module: 'administration.translations',
		action: 'dump',
		language: language
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: params,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		if (response.success && response.uuid) {
			window.open(window.location.pathname + '?controller=download&module=administration.translations&action=download_dump&id=' + response.uuid);				
		}

		Core.msg(response);
	});

};

Translation.showPopupProgress = function() {
	Core.fadeInOverlay('fast');
	Translation.uploadPopup.appendTo('body').fadeIn('fast').center().progressbar();
};

Translation.hidePopupProgress = function() {
	Core.hideOverlay();
	Translation.uploadPopup.hide().stopContinuousProgress();	
};

Translation.advanceUploadProgress = function(current, total, percentComplete) {
	Translation.stopProcessProgress();

	Translation.uploadPopup.find('.uploading').show();
	Translation.uploadPopup.find('.processing').hide();	

	Translation.uploadPopup.find('.progress').progressbar({
		current: current,
		total: total
	});

	if (total > 0 && current == total) {
		Translation.advanceProcessProgress();
	}
};

Translation._advanceProcessProgressTimeout = null;
Translation.advanceProcessProgress = function() {
	Translation.uploadPopup.find('.uploading').hide();
	Translation.uploadPopup.find('.processing').show();

	Translation.uploadPopup.continuousProgress();
};

Translation.stopProcessProgress = function() {
	clearTimeout(Translation._advanceProcessProgressTimeout);	
	Translation.uploadPopup.stopContinuousProgress();
};

Translation.upload = function(button) {
	Core.clearFormErrors();

	$('#page_submit').ajaxSubmit({
		beforeSerialize: function($form, options) { 
			$('#controller').val('json');
			$('#module').val('administration.translations');
			$('#action').val('load');
		},
		beforeSubmit: function() {
			Translation.showPopupProgress();
			Translation._advanceProcessProgressTimeout = setTimeout(Translation.advanceProcessProgress, 500);
		},
		dataType: 'json',
		forceSync: true,
		complete: function() { 
			$('#controller').val('jsp');
			Translation.hidePopupProgress();
		},
		success: function(response) {
			if (response.success) {
				Core.msg(response);
//				location.reload();
				return;
			}
			
			if (response.errors) {
				Core.formErrors(response.errors);
			} else {
				Core.msg(response);
			}
		},
		error: function() {
			Core.msg({
				message_level: 'warning',
				message: _('cataloging.import.error.file_upload_error')
			});
		},
		uploadProgress: function(event, current, total, percentComplete) {
			Translation.advanceUploadProgress(current, total, percentComplete);
		}
	}); 
};

Translation.selectLanguage = function(language) {
	var languageCombo = $('#language_selection select');
	languageCombo.val(language).trigger('change');
};

Translation.save = function(button) {
	var tree = $('#translations_tree');

	var translations = {};
	tree.find('input').each(function() {
		var input = $(this);
		translations[input.attr('name')] = input.val();
	});
	
	params = $.extend({}, {
		controller: 'json',
		module: 'administration.translations',
		action: 'save_language_translations',
		translations: JSON.stringify(translations)
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: params,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		Core.msg(response);
	});
};
