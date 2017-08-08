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
var QuotationInput = new Input({
	initialize: function() {
		$('#biblivre_quotation_form_body').setTemplateElement('biblivre_quotation_form_body_template');
		$('#biblivre_quotation_form').setTemplateElement('biblivre_quotation_form_template');
		$('#biblivre_quotation_info_form').setTemplateElement('biblivre_quotation_info_form_template');
		
		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {			
			if (this.search.lastSearchResult && this.search.lastSearchResult[id]) {
				this.search.lastSearchResult[id].deleted = true;
				
				this.search.processResultTemplate();
			}

			this.search.closeResult();
		}, this);

		Core.subscribe(this.prefix + 'edit-record-end', function(e, id) {			
			$('input.datepicker').each(function() {
				$(this).data('Zebra_DatePicker').destroy();
			});
		}, this);		
	},
	clearTab: function(tab) {
		this.quotationList = [];
		switch (tab) {
			case 'form':
				this.setAsEditable();
				break;
		}
	},
	clearAll: function() {
		this.clearTab('form');
	},
	setAsReadOnly: function() {
		var root = $('#biblivre_quotation_form_body, #biblivre_quotation_form, #biblivre_quotation_info_form');

		root.find('input:text, textarea:not(.template)').each(function() {
			var input = $(this);

			var value = input.val();
			input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});
		
		root.find('select:not(.autocreated)').each(function() {
			var combo = $(this);
	
			var value = combo.find('option[value=' + combo.val() + ']').text();
			combo.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});

		$('#biblivre_quotation_form').hide();
		$('.xclose').hide();
	},
	setAsEditable: function(tab) {
		var root = $('#biblivre_quotation_form_body, #biblivre_quotation_form, #biblivre_quotation_info_form');

		root.find('.readonly_text').remove();
		root.find('.readonly_hidden').removeClass('readonly_hidden');
		
		$('#biblivre_quotation_form').show();
		$('.xclose').show();
	},
	editing: false,
	recordIdBeingEdited: null,
	editRecord: function(id) {
		if (this.editing) {
			return;
		}
		
		if (Core.trigger(this.prefix + 'edit-record-start', id) === false) {
			return;
		}

		this.editing = true;
		this.recordIdBeingEdited = id;
		
		var tab = this.search.selectedTab;
		if (tab != 'form') {
			tab = 'form';
		} else {
			this.setAsEditable('form');
		}
		Core.changeTab(tab, this.search, { keepEditing: true, skipConvert: true });
		
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
	},
	getNewRecord: function() {
		return {
			id: ' '
		};
	},
	setRequestQuantity: function(value) {
		if (!value) {
			$('input:text[name="quantity"]').val(value);
			return true;
		}
		var data = {
			controller: 'json',
			module: 'acquisition.request',
			action: 'open',
			id: value
		};
		
		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: data,
			loadingTimedOverlay: true
		}).done($.proxy(function(response) {
			if (response.success) {
				var request = response.request;
				$('input:text[name="quantity"]').val(request.quantity);
			}
		}, this));
	},
	
	quotationList: [],
	addQuotation: function() {
		var id = $('#biblivre_quotation_form select[name="request"]').val();
		var request = $('#biblivre_quotation_form select[name="request"] :selected').text();
		var quantity = $('#biblivre_quotation_form input[name="quantity"]').val();
		var unitValue = $('#biblivre_quotation_form input[name="unit_value"]').val();
		
		unitValue = Globalize.parseFloat(unitValue);
		
		var quotation = {
				id: id,
				name: request,
				quantity: quantity,
				value: unitValue
		};
		
		var found = false;
		for (var i = 0; i < this.quotationList.length; i++) {
			if (this.quotationList[i].id == id) {
				found = true;
				break;
			}
		}
		
		if (!found) { 			
			this.quotationList.push(quotation);
			this.updateQuotationList();
		}
	},
	removeQuotation: function(id) {
		for (var i = 0; i < this.quotationList.length; i++) {
			if (this.quotationList[i].id == id) {
				this.quotationList.splice(i, 1);
				break;
			}
		}
		
		this.updateQuotationList();
	},
	updateQuotationList: function() {
		var scroll = this.root.find('.selected_results_area ul').scrollTop();
		this.root.find('.selected_results_area').processTemplate(this.quotationList);
		this.root.find('.selected_results_area ul').scrollTop(scroll);
	},
	getSaveRecord: function(saveAsNew) {
		var params = {};
		$('#biblivre_quotation_form_body :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		$('#biblivre_quotation_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		$('#biblivre_quotation_info_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		
		var quotationDate = $('#biblivre_quotation_form_body :input[name="quotation_date"]').val();
		var responseDate = $('#biblivre_quotation_form_body :input[name="response_date"]').val();
		var expirationDate = $('#biblivre_quotation_form_body :input[name="expiration_date"]').val();
		
		quotationDate = Globalize.parseDate(quotationDate, 'd');
		responseDate = Globalize.parseDate(responseDate, 'd');
		expirationDate = Globalize.parseDate(expirationDate, 'd');
		
		return $.extend(params, {
			oldId: this.recordIdBeingEdited || 0,
			id: (saveAsNew) ? 0 : this.recordIdBeingEdited,
			supplier: $('#biblivre_quotation_form_body select[name="supplier"]').val(),
			quotation_list: JSON.stringify(this.quotationList),
			quotation_date: Globalize.format(quotationDate, 'S'),
			response_date: Globalize.format(responseDate, 'S'),
			expiration_date: Globalize.format(expirationDate, 'S'),
		});
	},
	deleteRecordTitle: function() {
		return _(this.type + '.confirm_delete_record_title.forever');
	},
	deleteRecordQuestion: function() {
		return _(this.type + '.confirm_delete_record_question.forever');
	},
	deleteRecordConfirm: function() {
		return _(this.type + '.confirm_delete_record.forever');
	},
	getDeleteRecord: function(id) {
		return {
			id: id,
		};
	},
	getOverlayClass: function(record) {
		if (record.deleted) {
			return 'overlay_error';
		}
		
		return '';
	},
	getOverlayText: function(record) {
		if (record.deleted) {
			return _('common.deleted');
		}
		
		return '';
	}
});