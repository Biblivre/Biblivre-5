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
var Configurations = Configurations || {};

$(document).ready(function() {
	var businessDays = $('#business_days');
	var businessSelected = BusinessValues.split(',');
	var businessText = [];

	for (var i = 0; i < 7; i++) {
		var checked = false;
		for (var j = 0; j < businessSelected.length; j++) {
			if (businessSelected[j] == i + 1) {
				checked = true;
				break;
			}
		}
		
		var text = Globalize.culture().calendars.standard.days.names[i];

		var input = $('<input type="checkbox" name="' + Configurations.businessDays + '" class="finput" value="' + (i + 1) + '" id="bd_' + (i + 1) + '" >');
		if (checked) {
			input.attr('checked', 'checked');
			businessText.push(text);
		}
		input.appendTo(businessDays);
		$('<label for="bd_' + (i + 1) + '"></label>').text(' ' + text).appendTo(businessDays);
		$('<br>').appendTo(businessDays);
	}
	
	$('#business_days_current').text(businessText.join(', '));
});

Configurations.save = function(button) {
	var result = {};
	
	$('.biblivre_form :input:not(:checkbox)').each(function() {
		var el = $(this);
		result[el.attr('name')] = el.val();
	});

	$('.biblivre_form #business_days :checkbox:checked').each(function() {
		var el = $(this);
		var name = el.attr('name');
		var val = el.val();

		if (result[name]) {
			result[name] += ',' + val;
		} else {
			result[name] = val;
		}
	});

	var z3950Checkbox = $('#z3950_server_active');
	if (z3950Checkbox.size()) {
		result[z3950Checkbox.attr('name')] = z3950Checkbox.is(':checked');
	}

	var multiSchema = $('#multi_schema_active');
	var multiSchemaChecked = false;

	if (multiSchema.size()) {
		multiSchemaChecked = multiSchema.is(':checked');
		result[multiSchema.attr('name')] = multiSchemaChecked;
	}
	
	Core.clearFormErrors();
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.configurations',
			action: 'save',
			configurations: JSON.stringify(result)
		},
		loadingButton: button,
		loadingTimedOverlay: true
	}).done($.proxy(function(response) {
		if (!response.success) {
			Core.msg(response);
			Core.formErrors(response.errors);
		} else if (response.reload) {
			window.location.href = window.location.pathname;
		} else {
			var obj = Core.qso();
			obj.msg = response.message;
			obj.level = response.message_level;

			 window.location.href = window.location.pathname + '?' + $.param(obj);
		}
	}, this));

};