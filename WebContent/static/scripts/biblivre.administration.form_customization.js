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
var FormCustomization = FormCustomization || {};
var CatalogingInput = CatalogingInput || {};

$(document).ready(function() {
	var firstSort = true;
	
	var datafields = $('#datafields'); 
	
	// Enable sorting
	datafields.sortable({
		handle: '.move-datafield',
		axis: 'y',
		cursor: 'move',
		distance: 10,
		placeholder: 'sortable-placeholder',
		update: FormCustomization.updateOrder,
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
		if (FormCustomization.editMode) {
			return;
		}

		var fieldset = $(this).closest('fieldset');

		FormCustomization.enterEditMode(fieldset);
	});
	
	datafields.on('click', '.cancel-datafield', function() {
		FormCustomization.exitEditMode(false);
	});
	
	datafields.on('click', '.trash-datafield', function() {
		FormCustomization.remove(this);
	});
	
	datafields.on('click', '.save-datafield', FormCustomization.saveDatafield);
	
	// Defining Template
	datafields.setTemplateElement($('#datafields_template'));
	
	//Adding behaviour to record_type select
	var recordTypeSelect = $(':input[name=record_type_field]'); 
	recordTypeSelect.on('change', function() {
		var selectedType = $(this).val();
		if (FormCustomization.formFields[selectedType]) {
			FormCustomization.selectedRecordType = selectedType; 
			FormCustomization.processTemplate();
		}
	}).trigger('change');
});

FormCustomization.formFields = {};
FormCustomization.materialTypes = [];

FormCustomization.disableSort = function() {
	$('#datafields').sortable('disable');
	$('#datafields .move-datafield').addClass('disabled');
};

FormCustomization.enableSort = function() {
	$('#datafields').sortable('enable');
	$('#datafields .move-datafield').removeClass('disabled');
};

FormCustomization.enterEditMode = function(fieldset) {
	FormCustomization.editMode = true;
	FormCustomization.disableSort();
	$('#datafields .edit-datafield, #datafields .trash-datafield').addClass('disabled');
	fieldset.addClass('editing');
	
	var editArea = fieldset.find('.edit_area');
	editArea.setTemplateElement($('#datafields_edit_template'));
	
	var tag = fieldset.attr('data-datafield');
	var datafield = FormCustomization.formFields[FormCustomization.selectedRecordType][tag];
	
	editArea.processTemplate({
		datafieldTag: tag,
		datafield: datafield 
	});

	editArea.find('input[name=indicator_1_enabled], input[name=indicator_2_enabled]').on('change', function() {
		var el = $(this);
		el.closest('tr').find(':input').not(this).attr('disabled', !el.attr('checked'));
	}).trigger('change');
	
	editArea.find('.subfield_table tbody').sortable({
		handle: '.move-subfield',
		axis: 'y',
		cursor: 'move',
		distance: 10,
		placeholder: 'sortable-placeholder'
	});
};

FormCustomization.editIndicatorAddLine = function(table, value, text) {
	var tr = $('<tr></tr>');
	$('<td></td>').append($('<input class="indicator-value" type="text" maxlength="1" />').val(value)).appendTo(tr);
	$('<td></td>')
		.append($('<input class="indicator-text" type="text" />').val(text))
		.append($('<a href="javascript:void(0);"><i class="fa fa-fw fa-times"></i></a>').click(function() {
			$(this).closest('tr').remove();
		}))
		.appendTo(tr);
	
	tr.appendTo(table.find('tbody'));
};

FormCustomization.editIndicator = function(indicator) {
	var div = $('<div/>');	
	var table = $('<table class="indicator_edit_table"><thead><tr><th>' + _('administration.form_customization.indicator.label_value') + '</th><th>' + _('administration.form_customization.indicator.label_text') + '</th></thead><tbody></tbody></table>').appendTo(div);

	$('<div class="right" style="margin-right: 14px; margin-top: 5px;"><a href="javascript:void(0);"><i class="fa fa-fw fa-plus"></i></a></div>').on('click', function(e) {
		FormCustomization.editIndicatorAddLine(table, '', '');
		e.preventDefault();
		e.stopPropagation();
	}).appendTo(div);
	
	var combo = $(indicator).siblings('select');
	var added = false;
	combo.find('option').each(function() {
		added = true;
		var option = $(this);
		FormCustomization.editIndicatorAddLine(table, option.val(), option.text());
	});
	
	if (!added) {
		FormCustomization.editIndicatorAddLine(table, '', '');
	}	
	
	var popup = Core.popup({
		body: div,
		cancelHandler: function() {
		},
		okHandler: function() {
			var ok = true;
			var list = $('<select/>');
			var unique = {};

			table.find('tbody tr').each(function() {
				var tr = $(this);
				var value = $.trim(tr.find('.indicator-value').val());
				var text = $.trim(tr.find('.indicator-text').val());
				
				if (!value.length && !text.length) {
					return;
				}
				
				if (!value.length || !text.length) {
					alert(_('administration.form_customization.indicator_edit_fill_error'));
					ok = false;
					return;
				}
				
				if (unique[value]) {
					alert(_('administration.form_customization.indicator_edit_repeated_value_error'));
					ok = false;
					return;
				}
				
				unique[value] = true;
				
				$('<option></option>').attr('value', value).text(text).appendTo(list);
			});
			
			if (ok) {
				combo.empty();
				combo.append(list.children());
			}
			
			return ok;
		}
	});
};

FormCustomization.exitEditMode = function(success) {
	FormCustomization.enableSort();
	$('#datafields .edit-datafield, #datafields .trash-datafield').removeClass('disabled');
	
	var fieldset = $('#datafields fieldset.editing').removeClass('editing').addClass(success ? 'bg-success' : 'bg-cancel');

	fieldset.find('.edit_area').empty();

	var tag = fieldset.attr('data-datafield');
	if (tag) {
		setTimeout(function() {
			fieldset.removeClass(success ? 'bg-success' : 'bg-cancel');
		}, 1500);
	} else {
		fieldset.remove();
	}
	
	FormCustomization.editMode = false;
};

FormCustomization.saveDatafield = function() {
	var fieldset = $('#datafields fieldset.editing');
	var tag = fieldset.attr('data-datafield');
	var datafield = FormCustomization.indexedDatafields[tag];
	
	var newTag = fieldset.find('[name=datafield_tag]').val() || '';
	var translations = {};
	
	if (!tag && FormCustomization.indexedDatafields[newTag]) {
		alert(_('administration.form_customization.error.existing_tag'));
		return;
	}
	
	if (newTag.trim().length != 3) {
		alert(_('administration.form_customization.error.invalid_tag'));
		return;
	}
	
	var ind1 = [];
	if (fieldset.find('[name=indicator_1_enabled]').attr('checked')) {
		fieldset.find('[name=indicator_1_values] option').each(function() {
			if (this.value === '') {
				return;
			}
			ind1.push(this.value);
			translations['marc.bibliographic.datafield.' + newTag + '.indicator.1.' + this.value] = $(this).text();
		});
		
		translations['marc.bibliographic.datafield.' + newTag + '.indicator.1'] = fieldset.find('[name=indicator_1_name]').val();
	}

	var ind2 = [];
	if (fieldset.find('[name=indicator_1_enabled]').attr('checked')) {
		fieldset.find('[name=indicator_2_values] option').each(function() {
			if (this.value === '') {
				return;
			}
			ind2.push(this.value);
			translations['marc.bibliographic.datafield.' + newTag + '.indicator.2.' + this.value] = $(this).text();
		});

		translations['marc.bibliographic.datafield.' + newTag + '.indicator.2'] = fieldset.find('[name=indicator_2_name]').val();
	}
	
	var materials = [];
	fieldset.find('.material-types input:checked').each(function() {
		materials.push(this.name);
	});
	
	var subfields = [];
	var existingSubs = {};
	var i = 0;
	var fail = false;
	fieldset.find('.subfield_row').each(function() {
		var tr = $(this);
		var subTag = tr.find('[name=subfield_tag]').val();
		
		if (!subTag) {
			return;
		}
		
		if (existingSubs[subTag]) {
			alert(_('administration.form_customization.error.existing_subfield'));
			tr.find('[name=subfield_tag]').focus();
			fail = true;
			return;
		}
		
		existingSubs[subTag] = true;

		subfields.push({
			subfield: subTag,
			datafield: newTag,
			autocomplete_type: tr.find('[name=sufield_autocomplete]').val(),
			collapsed: !!tr.find('[name=subfield_collapsed]').attr('checked'),
			repeatable: !!tr.find('[name=subfield_repeatable]').attr('checked'),
			sortOrder: i++
		});
		
		translations['marc.bibliographic.datafield.' + newTag + '.subfield.' + subTag] = tr.find('[name=subfield_name]').val();
	});

	if (fail) {
		return;
	}
	
	translations['marc.bibliographic.datafield.' + newTag] = fieldset.find('[name=datafield_name]').val();

	
	var updatedDatafield = {};
	
	updatedDatafield[datafield ? datafield.datafield : newTag] = {
		formtab: {
			datafield: newTag,
			repeatable: !!fieldset.find('[name=datafield_repeatable]').attr('checked'),
			collapsed: !!fieldset.find('[name=datafield_collapsed]').attr('checked'),
			indicator1: ind1.join(','),
			indicator2: ind2.join(','),
			materialType: materials.join(','),
			subfields: subfields,
			sortOrder: datafield ? datafield.sortOrder : 0
		},
		translations: translations
	};
	
	FormCustomization.save(updatedDatafield, function(response) {
		var t = datafield ? datafield.datafield : newTag;
		delete FormCustomization.indexedDatafields[t];

		var ftab = updatedDatafield[t].formtab;

		FormCustomization.indexedDatafields[t] = ftab;
		FormCustomization.indexedDatafields[t].indicator1 = ftab.indicator1 ? ftab.indicator1.split(',') : null;
		FormCustomization.indexedDatafields[t].indicator2 = ftab.indicator2 ? ftab.indicator2.split(',') : null;
		FormCustomization.indexedDatafields[t].materialType = ftab.materialType.split(',');

		FormCustomization.formFields[FormCustomization.selectedRecordType][t] = FormCustomization.indexedDatafields[t];
		
		fieldset.attr('data-datafield', t).children('legend').text(t + ' - ' + fieldset.find('[name=datafield_name]').val());
		
		for (var t in translations) {
			if (translations.hasOwnProperty(t)) {
				Translations.translations[t] = translations[t];
			}
		}
		
		FormCustomization.exitEditMode(response.success);
	});
};

FormCustomization.updateOrder = function() {
	FormCustomization.disableSort();

	var updatedOrder = {};
	
	$('#datafields > fieldset').each(function(index, element) {
		var tag = $(element).attr('data-datafield');
		var datafield = FormCustomization.indexedDatafields[tag];
		
		if (datafield.sortOrder != index) {
			datafield.sortOrder = index;
			
			updatedOrder[datafield.datafield] = {
				formtab: {
					datafield: datafield.datafield,
					collapsed: datafield.collapsed,
					repeatable: datafield.repeatable,
					indicator1: (datafield.indicator1 || []).join(','),
					indicator2: (datafield.indicator2 || []).join(','),
					materialType: (datafield.material_type || []).join(','),
					subfields: datafield.subfields,
					sortOrder: datafield.sortOrder
				},
				translations: {
				}
			};
		}
	});
	
	FormCustomization.save(updatedOrder, FormCustomization.enableSort);
};

FormCustomization.save = function(fields, callback) {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'administration.customization',
			action: 'save_form_datafields',
			fields: JSON.stringify(fields),
			record_type: FormCustomization.selectedRecordType
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

FormCustomization.insert = function(formats, callback) {
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		loadingTimedOverlay: true,		
		data: {
			controller: 'json',
			module: 'administration.customization',
			action: 'insert_form_format',
			formats: JSON.stringify(formats),
			record_type: FormCustomization.selectedRecordType
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


FormCustomization.remove = function(button) {
	if (FormCustomization.editMode) {
		return;
	}
	var fieldset = $(button).closest('fieldset');
	
	Core.popup({
		title: _('administration.form_customization.confirm_delete_datafield_title'),
		description: _('administration.form_customization.confirm_delete_datafield_description'),
		okHandler: $.proxy(function() {
			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				loadingTimedOverlay: true,		
				data: {
					controller: 'json',
					module: 'administration.customization',
					action: 'delete_form_datafield',
					record_type: FormCustomization.selectedRecordType,
					datafield: fieldset.attr('data-datafield')
				},
				success: function(response) {
					if (response.success) {
						fieldset.remove();
					}

					Core.msg(response);
				}
			});
		}, this),
		cancelHandler: $.proxy(function() {
		}, this)
	});
};

FormCustomization.removeSubfield = function(button) {
	$(button).closest('tr').remove();
};

FormCustomization.addSubfield = function(button) {
	var tbody = $(button).closest('tbody');
	var tr = $(button).closest('tr');
	var template = tbody.find('.subfield_template').clone(true).removeClass('hidden').removeClass('subfield_template').addClass('subfield_row');
	tr.before(template);
};

FormCustomization.addDatafield = function(button) {
	if (FormCustomization.editMode) {
		return;
	}
	var div = $('<fieldset class="block"><legend>---</legend><div class="buttons"><span class="cancel-datafield"><i class="fa fa-close"></i></span> <span class="save-datafield"><i class="fa fa-check"></i></span> <span class="trash-datafield"><i class="fa fa-trash-o"></i></span> <span class="edit-datafield"><i class="fa fa-pencil"></i></span> <span class="move-datafield"><i class="fa fa-bars"></i></span></div><div class="edit_area"></div></fieldset>');
	$('#datafields').prepend(div);
	div.find('.edit-datafield').trigger('click');
};

FormCustomization.processTemplate = function() {
	var enabledFields = FormCustomization.formFields[FormCustomization.selectedRecordType];

	FormCustomization.datafields = ld.sortBy(enabledFields, 'sortOrder');
	FormCustomization.indexedDatafields = ld.keyBy(FormCustomization.datafields, 'datafield');

	$('#datafields').processTemplate({
		datafields: FormCustomization.datafields
	});
};
