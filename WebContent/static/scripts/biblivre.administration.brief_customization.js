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
var Customization = Customization || {};
var CatalogingInput = CatalogingInput || {};

$(document).ready(function() {
	var firstSort = true;
	
	var datafields = $('#datafields'); 
	var disabledDatafields = $('#disabled_datafields'); 
	
	// Enable sorting
	datafields.sortable({
		handle: '.move-datafield',
		axis: 'y',
		cursor: 'move',
		distance: 10,
		placeholder: 'sortable-placeholder',
		update: Customization.updateOrder,
		start: function(e, ui) {
			if (firstSort) {
				datafields.sortable('refreshPositions');
				firstSort = false;
			}
		}
	});
	
	datafields.disableSelection();
	
	// Datafield Buttons
	datafields.on('click', '.edit-datafield', function(el) {
		if (Customization.editMode) {
			return;
		}

		var fieldset = $(this).closest('fieldset');
		
		Customization.enterEditMode(fieldset);
	});
	
	datafields.on('click', '.cancel-datafield', function() {
		Customization.exitEditMode(false);
	});
	
	datafields.on('click', '.save-datafield', Customization.updateFormat);
	
	datafields.on('click', '.disable-datafield', function(el) {
		if (Customization.editMode) {
			return;
		}

		var fieldset = $(this).closest('fieldset');
		
		Customization.disableDatafield(fieldset);
	});
	
	disabledDatafields.on('click', '.add-datafield', function(el) {
		var fieldset = $(this).closest('.block');
		
		Customization.enableDatafield(fieldset);
	});
	
	// Defining Template
	datafields.setTemplateElement($('#datafields_template'));
	disabledDatafields.setTemplateElement($('#disabled_datafields_template'));
	
	//Adding behaviour to record_type select
//	var recordTypeSelect = $('#record_type_select'); 
	var recordTypeSelect = $(':input[name=record_type_field]'); 
	recordTypeSelect.on('change', function() {
		var selectedType = $(this).val();
		if (Customization.formFields[selectedType]) {
			Customization.selectedRecordType = selectedType; 
			Customization.loadFormats();
		}
	}).trigger('change');
});

Customization.formFields = {};

Customization.disableSort = function() {
	$('#datafields').sortable('disable');
	$('#datafields .move-datafield').addClass('disabled');
};

Customization.enableSort = function() {
	$('#datafields').sortable('enable');
	$('#datafields .move-datafield').removeClass('disabled');
};

Customization.enterEditMode = function(fieldset) {
	Customization.editMode = true;
	Customization.disableSort();
	$('#datafields .edit-datafield, #datafields .disable-datafield').addClass('disabled');
	fieldset.addClass('editing');
	
	var editArea = fieldset.find('.edit_area');
	editArea.setTemplateElement($('#datafields_edit_template'));
	
	var tag = fieldset.attr('data-datafield');
		
	editArea.processTemplate({
		datafield: tag,
		subfields: (Customization.formFields[Customization.selectedRecordType][tag] || {}).subfields,
		separators: [{
			separator: ' - ',
			code: 'space-dash-space',
		}, {
			separator: ', ',
			code: 'comma-space',
		}, {
			separator: '. ',
			code: 'dot-space',
		}, {
			separator: ': ',
			code: 'colon-space',
		}, {
			separator: '; ',
			code: 'semicolon-space',
		}],
		aggregators: [{
			aggregator: '(',
			code: 'left-parenthesis'
		}, {
			aggregator: ')',
			code: 'right-parenthesis'
		}]
	});

	var sortableIn = false;
	var sortableMargin = 0;
	var firstSort = true;
	
	var formatInput = editArea.find('.format-input');
	
	formatInput.sortable({
		'ui-floating': true,
		receive: function(e, ui) {
			sortableIn = true;
		},
		over: function(e, ui) {
			ui.helper.css('margin-left', sortableMargin);
			sortableIn = true;
		},
		out: function(e, ui) {
			if (ui.helper) {
				ui.helper.css('margin-left', 0);
			}
			sortableIn = false;
		},
		start: function(e, ui) {
			if (firstSort) {
				formatInput.sortable('refreshPositions');
				firstSort = false;
			}
		},
		beforeStop: function(e, ui) {
			if (ui.helper) {
				ui.helper.css('margin-left', 0);
				sortableMargin = 0;
			}

			if (!sortableIn) { 
				ui.item.remove(); 
			} 
		}
	});
	
	editArea.find('.subfields .subfield, .separators .separator, .aggregators .aggregator').draggable({
		connectToSortable: formatInput,
		helper: 'clone',
		cursor: 'move',
		revert: true,
		revertDuration: 0,
		start: function(e, ui) {
			sortableIn = false;
			sortableMargin = e.clientX - ui.offset.left - 6;
		}
	});
	
	var format = Customization.indexedDatafields[tag].format || {};
	var pattern = /[(\(\)]|(\$|_){(.*?)}/g;
	var match = null;
	
	while (match = pattern.exec(format)) {
		var type, text;
		
		if (match[1] == '_') {
			type = 'separator';
			text = match[2].replace(/ /g, '&nbsp;');
		} else if (match[1] == '$') {
			type = 'subfield';
			text = '$' + match[2];
		} else {
			type = 'aggregator';
			text = match[0];
		}
		
		$('<div></div>').addClass(type).html(text).appendTo(formatInput);
	}	
};

Customization.exitEditMode = function(success) {
	Customization.enableSort();
	$('#datafields .edit-datafield, #datafields .disable-datafield').removeClass('disabled');
	
	var fieldset = $('#datafields fieldset.editing').removeClass('editing').addClass(success ? 'bg-success' : 'bg-cancel');

	fieldset.find('.edit_area').empty();

	setTimeout(function() {
		fieldset.removeClass(success ? 'bg-success' : 'bg-cancel');
	}, 1500);

	Customization.editMode = false;
};

Customization.getFormat = function(fields) {
	var format = [];
	
	fields.each(function() {
		var el = $(this);
		
		el.find('.text').remove();
		
		if (el.hasClass('separator')) {
			format.push('_{');
			format.push(el.text());
			format.push('}');
		} else if (el.hasClass('subfield')) {
			format.push('${');
			format.push(el.text().substring(1));
			format.push('}');
		} else {
			format.push(el.text());
		}
	});
	
	return format.join('');
};

Customization.updateFormat = function() {
	var fieldset = $('#datafields fieldset.editing');
	var tag = fieldset.attr('data-datafield');

	var datafield = Customization.indexedDatafields[tag];
	datafield.format = Customization.getFormat(fieldset.find('.format-input').children('.separator, .subfield, .aggregator'));
	
	Customization.save([datafield], function(response) {
		Customization.exitEditMode(response.success);
	});
};

Customization.updateOrder = function() {
	Customization.disableSort();

	var updatedOrder = [];
	
	$('#datafields > fieldset').each(function(index, element) {
		var tag = $(element).attr('data-datafield');
		var datafield = Customization.indexedDatafields[tag];
		
		if (datafield.sortOrder != index) {
			datafield.sortOrder = index;
			
			updatedOrder.push({
				datafieldTag: datafield.datafieldTag,
				format: datafield.format,
				sortOrder: datafield.sortOrder
			});
		}
	});
	
	Customization.save(updatedOrder, Customization.enableSort);
};

Customization.save = function(formats, callback) {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'administration.customization',
			action: 'save_brief_formats',
			formats: JSON.stringify(formats),
			record_type: Customization.selectedRecordType
		},
		success: function(response) {
			if (!response.success) {
				Core.msg(response);
			} else {
				if ($.isFunction(callback)) {
					callback(response);
				}
			}			
		}
	});
};

Customization.insert = function(formats, callback) {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'administration.customization',
			action: 'insert_brief_format',
			formats: JSON.stringify(formats),
			record_type: Customization.selectedRecordType
		},
		success: function(response) {
			if (!response.success) {
				Core.msg(response);
			} else {
				if ($.isFunction(callback)) {
					callback(response);
				}
			}			
		}
	});
};


Customization.remove = function(formats, callback) {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'administration.customization',
			action: 'delete_brief_format',
			formats: JSON.stringify(formats),
			record_type: Customization.selectedRecordType
		},
		success: function(response) {
			if (!response.success) {
				Core.msg(response);
			} else {
				if ($.isFunction(callback)) {
					callback(response);
				}
			}			
		}
	});
};

Customization.enableDatafield = function(fieldset) {
	var tag = fieldset.attr('data-datafield');

	Customization.disableSort();
	var buttons = fieldset.find('.edit-datafield, .disable-datafield').addClass('disabled');
	
	var datafield = Customization.indexedDatafieldsData[tag];
	var newDatafield;
	
	if (datafield) {
		newDatafield = datafield;
		newDatafield.datafieldTag = tag;
		newDatafield.enabled = true;
	} else {
		datafield = Customization.indexedDisabledDatafields[tag];
		
		newDatafield = {
			datafieldTag: tag,
			format: '${a}',
			sortOrder: Customization.datafieldsData.length
		};

		Customization.datafieldsData.push(newDatafield);
		Customization.indexedDatafieldsData[newDatafield.datafieldTag] = newDatafield;
	}
	
	Customization.insert([newDatafield], function(response) {
		Customization.enableSort();
		buttons.removeClass('disabled');

		if (response.success) {
			Customization.computeDatafields();
			Customization.computeDisabledDatafields();
			
			
			var f = $('#datafields fieldset[data-datafield='+tag+']').addClass('bg-success');

			Customization.enterEditMode(f);
			
			setTimeout(function() {
				f.removeClass('bg-success');
			}, 1500);
		}
	});	
};

Customization.disableDatafield = function(fieldset) {
	var tag = fieldset.attr('data-datafield');

	Core.popup({
		title: _('administration.brief_customization.confirm_disable_datafield_title'),
		description: _('administration.brief_customization.confirm_disable_datafield_question', tag),
		confirm: _('administration.brief_customization.confirm_disable_datafield_confirm'),
		okText: _('common.yes'),
		cancelText: _('common.no'),
		okHandler: $.proxy(function() {
			var datafield = Customization.indexedDatafields[tag];
			datafield.enabled = false;
			
			Customization.disableSort();
			var buttons = fieldset.find('.edit-datafield, .disable-datafield').addClass('disabled');

			Customization.remove([datafield], function(response) {
				Customization.enableSort();
				buttons.removeClass('disabled');

				if (response.success) {
					fieldset.remove();
					Customization.computeDatafields();
					Customization.computeDisabledDatafields();
					
					var f = $('#disabled_datafields div[data-datafield='+tag+']').addClass('bg-success');

					setTimeout(function() {
						f.removeClass('bg-success');
					}, 1500);

				}
			});
		}, this),
		cancelHandler: $.proxy($.noop, this)
	});
};

// TODO: abort
Customization._loadingFormats = null;
Customization.loadFormats = function() {
	if (Customization._loadingFormats) {
		Customization._loadingFormats.abort();
		Customization._loadingFormats = null;
	}

	Customization._loadingFormats = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'cataloging.' + Customization.selectedRecordType,
			action: 'list_brief_formats'
		},
		success: function(response) {
			if (!response.success) {
				Core.msg(response);
			} else {
				Customization.populateDatafields(response.data || {});
			}
		}
	});
};

Customization.computeDatafields = function() {
	var enabledFields = ld.filter(Customization.datafieldsData, function(el) {
		return Customization.formFields[Customization.selectedRecordType][el.datafieldTag] && (el.enabled !== false);
	});

	Customization.datafields = ld.sortBy(enabledFields, 'sortOrder');
	Customization.indexedDatafields = ld.keyBy(Customization.datafields, 'datafieldTag');

	$('#datafields').processTemplate({
		datafields: Customization.datafields
	});
};

Customization.computeDisabledDatafields = function() {
	var disabledFields = ld.filter(Customization.formFields[Customization.selectedRecordType], function(el) {
		var df = Customization.indexedDatafields[el.datafield];
		return !df || df.enabled === false;
	});

	Customization.disabledDatafields = ld.sortBy(disabledFields, 'sortOrder');
	Customization.indexedDisabledDatafields = ld.keyBy(Customization.disabledDatafields, 'datafieldTag');

	$('#disabled_datafields').processTemplate({
		disabled_datafields: Customization.disabledDatafields
	});
};

Customization.datafieldsData = null;
Customization.populateDatafields = function(datafields) {
	Customization.datafieldsData = datafields.data;
	Customization.indexedDatafieldsData = ld.keyBy(Customization.datafieldsData, 'datafieldTag');
	
	Customization.computeDatafields();
	Customization.computeDisabledDatafields();
};