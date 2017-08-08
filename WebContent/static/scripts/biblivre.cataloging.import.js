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
var Import = {
	currentPage: 1,
	recordsPerPage: 25,
	uploadData: [],
	extraData: [],
	resultTempHolder: $('<div></div>'),
	uploadPopup: null,
	importPopup: null,
	searchResults: null
};

Import.changeSourceSelection = function(obj) {
	$('#step_1 .selection_box .title input[type=radio]').removeAttr('checked');
	$(obj).find('.title input[type=radio]').attr('checked', 'checked');
};

$(document).ready(function() {
	$('#step_1 .selection_box').click(function() {
		Import.changeSourceSelection(this);
	});
	
	$('#step_1 .selection_box .title input[type=radio]').click(function(event) {
		event.stopPropagation();
	});
	
	$('select[name="search_attribute"]').combo({
		expand: true
	});

	$('select[name="search_server"]').combo({
		expand: true
	});

	Import.resultTempHolder.setTemplateElement($('.search_result_template'));
	Import.uploadPopup = $('#upload_popup');
	Import.importPopup = $('#import_popup');
	Import.searchResults = $('#search_results');
	
//	$('.record input[type=checkbox]').change(function() {
//		if (this.checked) {
//			$(this).closest('.record').addClass('selected');
//		} else {
//			$(this).closest('.record').removeClass('selected');
//		}
//	});
});

Import.showPopupProgress = function() {
	Core.fadeInOverlay('fast');
	Import.uploadPopup.appendTo('body').fadeIn('fast').center().progressbar();
};

Import.hidePopupProgress = function() {
	Core.hideOverlay();
	Import.uploadPopup.hide().stopContinuousProgress();	
};

Import.advanceUploadProgress = function(current, total, percentComplete) {
	Import.stopProcessProgress();

	Import.uploadPopup.find('.uploading').show();
	Import.uploadPopup.find('.processing').hide();	

	Import.uploadPopup.find('.progress').progressbar({
		current: current,
		total: total
	});

	if (total > 0 && current == total) {
		Import.advanceProcessProgress();
	}
};

Import._advanceProcessProgressTimeout = null;
Import.advanceProcessProgress = function() {
	Import.uploadPopup.find('.uploading').hide();
	Import.uploadPopup.find('.processing').show();

	Import.uploadPopup.continuousProgress();
};

Import.stopProcessProgress = function() {
	clearTimeout(Import._advanceProcessProgressTimeout);	
	Import.uploadPopup.stopContinuousProgress();
};

Import.upload = function(button) {
	Core.clearFormErrors();

	$('#page_submit').ajaxSubmit({
		beforeSerialize: function($form, options) { 
			$('#controller').val('json');
			$('#module').val('cataloging');
			$('#action').val('import_upload');
		},
		beforeSubmit: function() {
			Import.showPopupProgress();
			Import._advanceProcessProgressTimeout = setTimeout(Import.advanceProcessProgress, 500);
		},
		dataType: 'json',
		forceSync: true,
		complete: function() { 
			$('#controller').val('jsp');
			Import.hidePopupProgress();
		},
		success: function(response) {
			if (response.success) {
				Import.processUploadData(response.data);
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
			Import.advanceUploadProgress(current, total, percentComplete);
		}
	}); 
};

Import.search = function(button) {
	Core.clearFormErrors();

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'cataloging',
			action: 'import_search',
			search_query: $('input[name=search_query]').val(),
			search_attribute: $('select[name=search_attribute]').val(),
			search_server: $('select[name=search_server]').val()
		},
		loadingTimedOverlay: true,
		success: function(response) {
			if (response.success) {
				Import.processUploadData(response.data);
				return;
			}
			
			if (response.errors) {
				Core.formErrors(response.errors);
			} else {
				Core.msg(response);
			}
		}
	}); 
};

Import.processUploadData = function(data) {
	if (!data || !data.record_list || data.record_list.length === 0) {
		Core.msg({
			message_level: 'warning',
			message: _('cataloging.import.error.no_records_found')
		});
		
		return;
	}

	Core.msg({
		message_level: 'normal',
		message: _p('cataloging.import.records_found', _f(data.found))
	});

	Import.uploadData = [];
	var lists = {
		isbn: data.isbn_list,
		issn: data.issn_list,
		isrc: data.isrc_list,
	};

	for (var i = 0; i < data.record_list.length; i++) {
		Import.updateRecordData(i, data.record_list[i], lists);
	}

	$('#step_1').hide();

	$('.page_title .step').text(_('common.step') + ' 2');
	$('.page_title .subtext').text(_('cataloging.import.step_2_title'));

	$('#step_2').fadeIn();
	Import.paginate(1);
};

Import.updateRecordData = function(i, record, lists) {
	var data = Import.uploadData[i];

	if (!data) {
		var material = record.material_type;
		if (material != 'authorities' && material != 'vocabulary') {
			material = 'biblio';
		}
		
		data = Import.uploadData[i] = {
			record: record,
			index: i,
			material: material
		};
	}
	
	if (record) {
		data.record = record;		
	}
	
	if (lists) {
		data.isbn = (record.isbn && lists.isbn[record.isbn.toLowerCase()]) || lists.isbn === true;
		data.issn = (record.issn && lists.issn[record.issn.toLowerCase()]) || lists.issn === true;
		data.isrc = (record.isrc && lists.isrc[record.isrc.toLowerCase()]) || lists.isrc === true;
	}
	
	if (data.material == 'ignore') {
		data.overlay = 'overlay_normal';
		data.overlay_text = _('cataloging.import.record_will_be_ignored');
	}
	
	if (data.imported) {
		data.overlay = 'overlay_success';
		data.overlay_text = _('cataloging.import.record_imported_successfully');
	} else if (data.isbn || data.issn || data.isrc) {
		data.material = 'ignore';
		data.overlay = 'overlay_warning';

		if (data.isbn) {
			data.overlay_text = _('cataloging.import.isbn_already_in_database');
		} else if (data.issn) {
			data.overlay_text = _('cataloging.import.issn_already_in_database');			
		} else if (data.isrc) {
			data.overlay_text = _('cataloging.import.isrc_already_in_database');
		}
	}
	
	return data;
};

Import.createResult = function(data) {
	Import.updateRecordData(data.index);

	var result = Import.resultTempHolder.processTemplate({
		data: data,
		record: data.record
	}).find('.result');

	var material = data.material;
	
	result.find('input:radio').change(function() {
		data.material = $(this).val();
		
		if (data.material == 'ignore') {
			result.addClass('overlay_normal');
			result.find('.result_overlay .text').text(_('cataloging.import.record_will_be_ignored'));
		}
	}).filter('[value="' + material + '"]').prop('checked', true);
	
	return result;
};

Import.paginate = function(currentPage) {
	Import.currentPage = currentPage;
	
	Core.pagingGenerator({
		pagingHolder: $('div.paging_bar'),
		pageCount: Math.ceil(Import.uploadData.length / Import.recordsPerPage),
		currentPage: Import.currentPage,
		recordsPerPage: Import.recordsPerPage,
		linkFunction: function() {
			Import.paginate($(this).attr('rel'));
		}
	});

	Import.searchResults.empty();
	
	var start = (Import.currentPage - 1) * Import.recordsPerPage;
	var end = Math.min(start + Import.recordsPerPage, Import.uploadData.length);
	
	for (var i = start; i < end; i++) {
		Import.createResult(Import.uploadData[i]).appendTo(Import.searchResults).fixButtonsHeight();
	}
};

Import.marcEditIndex = null;
Import.marcEdit = function(index) {
	Core.fadeInOverlay('fast');
	$('#marc_popup').appendTo('body').fadeIn('fast').center();
	$('#marc_popup_textarea').val(Import.uploadData[index].record.marc);
	Import.marcEditIndex = index;
};

Import.hideMarcEdit = function() {
	Core.hideOverlay();
	$('#marc_popup').hide().stopContinuousProgress();	
};

Import.marcChange = function() {
	var marc = $('#marc_popup_textarea').val();
	Import.hideMarcEdit();

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'cataloging',
			action: 'parse_marc',
			marc: marc
		},
		loadingTimedOverlay: true,
		success: function(response) {
			if (response.success) {
				var data = Import.updateRecordData(Import.marcEditIndex, response.data, {
					isbn: response.isbn || {},
					issn: response.issn || {},
					isrc: response.isrc || {}
				});
	
				var result = Import.createResult(data);				
				var oldResult = Import.searchResults.find('.result[data-index="' + data.index + '"]');

				result.replaceAll(oldResult).fixButtonsHeight();
			} else {
				Core.msg(response);
			}
		}
	});
};

Import.importCurrentPage = function() {
	var start = Import.searchResults.find('.result:first').data('index');
	var end = Import.searchResults.find('.result:last').data('index');
	
	Import.import(start, end);
};

Import.importAll = function() {
	Import.import(0, Import.uploadData.length - 1);
};

Import.successImports;
Import.import = function(start, end, page) {
	var data = {};
	
	if (page === undefined) {
		page = 0;
		Import.successImports = 0;
		Import.showPopupImportProgress();
	}

	Import.advanceImportProgress(Import.recordsPerPage * page, end - start + 1);
	
	var realStart = start + (Import.recordsPerPage * page);
	var realEnd = Math.min(end, realStart + Import.recordsPerPage - 1);
	
	for (var i = realStart; i <= realEnd; i++) {
		data['marc_' + i] = Import.uploadData[i].record.marc;
		data['record_type_' + i] = Import.uploadData[i].material;
	}
	
	data = $.extend(data, {
		controller: 'json',
		module: 'cataloging',
		action: 'save_import',
		start: realStart,
		end: realEnd
	});

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: data,
//		loadingTimedOverlay: true,
		success: function(response) {			
			if (response.success) {
				for (var i = 0; i < response.saved.length; i++) {
					var index = response.saved[i];

					Import.uploadData[index].imported = true;

					var result = Import.searchResults.find('.result[data-index="' + index + '"]');
					result.addClass('overlay_success');
					result.find('.result_overlay .text').text(_('cataloging.import.record_imported_successfully'));

					Import.successImports++;
				}
			}
			
			if (realEnd == end) {
				Core.msg(response);	
			}
		},
		complete: function() {
			if (realEnd == end) {
				Import.hidePopupImportProgress();
			} else {
				Import.import(start, end, page + 1);
			}
		}
	});
};

Import.showPopupImportProgress = function() {
	Core.fadeInOverlay('fast');
	Import.importPopup.appendTo('body').fadeIn('fast').center().progressbar();
};

Import.hidePopupImportProgress = function() {
	Core.hideOverlay();
	Import.importPopup.hide();	
};

Import.advanceImportProgress = function(current, total) {
	Import.importPopup.find('.progress').progressbar({
		current: current,
		total: total
	});
};
