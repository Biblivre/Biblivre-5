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
var Reports = Reports || {};
var CatalogingSearch = CatalogingSearch || {};

$(document).ready(function() {
	var global = Globalize.culture().calendars.standard;
	
	$('input.datepicker').Zebra_DatePicker({
		days: global.days.names,
		days_abbr: global.days.namesAbbr,
		months: global.months.names,
		months_abbr: global.months.namesAbbr,
		format: Core.convertDateFormat(global.patterns.d),
		show_select_today: _('common.today'),
		lang_clear_date: _('common.clear'),
		readonly_element: false
	});
	
	$('#dateDiv, #buttonDiv, #orderDiv, #userDiv, #authorDiv, #databaseSelection, #deweyDiv, #catalogingDiv, #fieldCountDiv').hide();
	
});

Reports.toggleDivs = function(reportId) {
	$('#dateDiv, #buttonDiv, #orderDiv, #userDiv, #authorDiv, #databaseSelection, #deweyDiv, #catalogingDiv, #fieldCountDiv').hide();
	
	if (reportId !== '') {
		$('#buttonDiv').show();
	} else {
		$('#buttonDiv').hide();
	}

	if (reportId === "1" || reportId === "4" || reportId === "9" || reportId === "10" || reportId === "16") {
		$('#dateDiv').show();
	}

	if (reportId === "2") {
		$('#orderDiv').show();
		$('#databaseSelection').show();
	}

	if (reportId === "3") {
		$('#databaseSelection').show();
		$('#deweyDiv').show();
	}

	if (reportId === "6") {
		$('#userDiv').show();
		$('#buttonDiv').hide();
	}

	if (reportId === "5") {
		CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'cataloging.authorities',
			root: '#authorDiv',
			searchAction: 'search_author',
			enableTabs: false,
			enableHistory: false
		});
	
		CatalogingInput.type = 'cataloging.authorities';
		CatalogingInput.root = '#authorDiv';
	
		CatalogingInput.search = CatalogingSearch;
		CatalogingInput.defaultMaterialType = 'authorities';
		
		$('#databaseSelection').show();
		$('#authorDiv').show();
		$('#buttonDiv').hide();
	}
	
	if (reportId === "17") {
		CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'cataloging.bibliographic',
			root: '#catalogingDiv',
			enableTabs: false,
			enableHistory: false
		});
	
		CatalogingInput.type = 'cataloging.bibliographic';
		CatalogingInput.root = '#catalogingDiv';
	
		CatalogingInput.search = CatalogingSearch;
		CatalogingInput.defaultMaterialType = 'book';
		
		Reports.createMarcFieldsDropdown(CatalogingInput.formFields);
		
		$('#databaseSelection').show();
		$('#catalogingDiv').show();
		$('#fieldCountDiv').show();
		$('#buttonDiv').hide();
		
	}
};

Reports.createMarcFieldsDropdown = function(datafields) {
	var translationPrefix = 'marc.bibliographic.datafield.';
	
	var comboDiv = $('#marc_field_combo');
	var select = $('<select name="marc_field"></select>');
	var defaultOption = $('<option></option>')
		.attr('value', '')
		.attr('selected', 'selected')
		.text(_('administration.reports.select.option.select_marc_field'));
	select.append(defaultOption);
		
	for (var i = 0; i < datafields.length; i++) {
		var datafield = datafields[i];
		var optgroup = $('<optgroup/>');
		
		optgroup.attr('label', datafield.datafield + ' - ' + _(translationPrefix + datafield.datafield));
		
		for (var sub = 0; sub < datafield.subfields.length; sub++) {
			var subfield = datafield.subfields[sub];
			
			var option = $('<option></option>');
			option.attr('value', datafield.datafield + '_' + subfield.subfield);
			option.text(datafield.datafield + ' $' + subfield.subfield + ' - ' + _(translationPrefix + datafield.datafield + '.subfield.' + subfield.subfield));
			
			optgroup.append(option);
		}

		select.append(optgroup);
	}
	
	comboDiv.append(select);
	
	select.change(function() {
		$(this).val() ? $('#buttonDiv').show() : $('#buttonDiv').hide();
	});
};

Reports.generateReport = function() {
	
	var params = {};
	
	var divs = $('#reportSelection, #dateDiv, #buttonDiv, #orderDiv, #userDiv, #authorDiv, #databaseSelection, #deweyDiv, #fieldCountDiv').find(':input:not(".template")');
	
	divs.each(function() {
		var input = $(this);
		params[input.attr('name')] = input.val();
	});
	
	var startDate = $('#dateDiv :input[name="start"]').val();
	var endDate = $('#dateDiv :input[name="end"]').val();
	
	startDate = Globalize.parseDate(startDate, 'd');
	endDate = Globalize.parseDate(endDate, 'd');
	
	if (CatalogingSearch && CatalogingSearch.lastPagingParameters) {
		params["search_id"] = CatalogingSearch.lastPagingParameters.search_id;
	}
	
	params = $.extend(params, {
		controller: 'json',
		module: 'administration.reports',
		action: 'generate',
		start:  Globalize.format(startDate, 'S'),
		end:  Globalize.format(endDate, 'S')
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: params,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		if (response.success) {
			Core.msg(response);
			if (response.file_name) {
				window.open(window.location.pathname + '?controller=download&module=administration.reports&action=download_report&file_name=' + response.file_name);				
			}
		} else {
			Core.msg(response);
		}
	});
	
		
};

Reports.generateAuthorReport = function(authorRecord) {
	
	var params = {};
	
	var divs = $('#reportSelection, #databaseSelection').find(':input');
	
	divs.each(function() {
		var input = $(this);
		params[input.attr('name')] = input.val();
	});
	
	params = $.extend(params, {
		controller: 'json',
		module: 'administration.reports',
		action: 'generate',
		authorName: authorRecord.author,
		recordIds: authorRecord.ids
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: params,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		if (response.success) {
			Core.msg(response);
			if (response.file_name) {
				window.open(window.location.pathname + '?controller=download&module=administration.reports&action=download_report&file_name=' + response.file_name);				
			}
		} else {
			Core.msg(response);
		}
	});
	
		
};

Reports.generateUserReport = function(userId) {
	
	var params = {};
	
	var divs = $('#reportSelection, #databaseSelection').find(':input');
	
	divs.each(function() {
		var input = $(this);
		params[input.attr('name')] = input.val();
	});
	
	params = $.extend(params, {
		controller: 'json',
		module: 'administration.reports',
		action: 'generate',
		user_id: userId
	});
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: params,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		if (response.success) {
			Core.msg(response);
			if (response.file_name) {
				window.open(window.location.pathname + '?controller=download&module=administration.reports&action=download_report&file_name=' + response.file_name);				
			}
		} else {
			Core.msg(response);
		}
	});
	
		
};
