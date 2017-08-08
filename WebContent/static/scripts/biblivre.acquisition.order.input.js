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

var OrderInput = new Input({
	initialize: function() {
		$('#biblivre_order_form_body').setTemplateElement('biblivre_order_form_body_template');
		$('#biblivre_order_form').setTemplateElement('biblivre_order_form_template');
		$('#biblivre_order_info_form').setTemplateElement('biblivre_order_info_form_template');
		
		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {			
			if (this.search.lastSearchResult && this.search.lastSearchResult[id]) {
				this.search.lastSearchResult[id].deleted = true;
				
				this.search.processResultTemplate();
			}

			this.search.closeResult();
		}, this);
	},
	clearTab: function(tab) {
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
		var root = $('#biblivre_order_form_body, #biblivre_order_form, #biblivre_order_info_form');

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
		
		root.find('input:checkbox').each(function() {
			$(this).disable();
		});
	},
	setAsEditable: function(tab) {
		var root = $('#biblivre_order_form_body, #biblivre_order_form, #biblivre_order_info_form');

		root.find('.readonly_text').remove();
		root.find('.readonly_hidden').removeClass('readonly_hidden');
		
		root.find('input:checkbox').each(function() {
			$(this).enable();
		});
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
		
		this.toggleDeliveryInputs($('input:checkbox[name="delivered"]').is(':checked'));
	},
	getNewRecord: function() {
		return {
			id: ' '
		};
	},
	getSaveRecord: function(saveAsNew) {
		var params = {};
		$('#biblivre_order_form_body :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		$('#biblivre_order_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		$('#biblivre_order_info_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		
		var delivered = $('#biblivre_order_form input[name="delivered"').is(':checked') ? 'checked' : '';
		
		
		var created = $('#biblivre_order_form_body :input[name="created"]').getIsoDate();
		var deadlineDate = $('#biblivre_order_form_body :input[name="deadline_date"]').getIsoDate();
		var receiptDate = $('#biblivre_order_form :input[name="receipt_date"]').getIsoDate();
		
		return $.extend(params, {
			oldId: this.recordIdBeingEdited || 0,
			id: (saveAsNew) ? 0 : this.recordIdBeingEdited,
			delivered: delivered,
			created: created,
			deadline_date: deadlineDate,
			receipt_date: receiptDate,
			total_value: Globalize.parseFloat($('#biblivre_order_form :input[name="total_value"]').val())
		});
	},
	toggleDeliveryInputs: function(checked) {
		$('#biblivre_order_form input:not(:checkbox)').each(function() {
			var input = $(this);
			input.attr('disabled', !checked);
		});
	},
	quotationList: [],
	listQuotations: function(supplierId) {
		this.quotationList = [];
		
		if (!supplierId) {
			var quotationSelect =  $('#quotation');
			
			quotationSelect.html("");
			quotationSelect.append($('<option>', { 
		        value: '',
		        text : _('acquisition.order.field.quotation_select')
		    }));
			this.root.find('.selected_results_area').processTemplate([]);
			$('#biblivre_order_form_body input[name="delivery_time"]').val('');
			return true;
		}
		var data = {
			controller: 'json',
			module: 'acquisition.quotation',
			action: 'list',
			supplier_id: supplierId
		};
		
		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: data,
			loadingTimedOverlay: true
		}).done($.proxy(function(response) {
			if (response.success) {
				if (!response.list || !response.list.data || response.list.data.length == 0) {
					return true;
				}

				this.quotationList = response.list.data; 
				
				var quotationSelect =  $('#quotation');
				
				quotationSelect.html("");
				quotationSelect.append($('<option>', { 
			        value: '',
			        text : _('acquisition.order.field.quotation_select')
			    }));
				$.each(response.list.data, function (i, quotation) {
					var text = '';
					
					$.each(quotation.quotationsList, function(i, item) {
						text += item.quantity + "x " + item.author + " - " + item.title + ", ";
					}); 
                    
                    if (text.length > 50) {
                        text = text.substring(0, 50) + '...';
                    }
                    if (OrderSearch.selectedRecord && OrderSearch.selectedRecord.quotationId == quotation.id) {
                        quotationSelect.append($('<option>', { 
    				        value: quotation.id,
    				        text: text,
    				        selected: "selected"
    				    }));
                        
                        quotationSelect.next('.readonly_text').text(text);
                    } else {
                        quotationSelect.append($('<option>', { 
    				        value: quotation.id,
    				        text: text
    				    }));
                    }
				});
				
				this.updateQuotationList(quotationSelect.val());
			}
		}, this));
	},
	updateQuotationList: function(quotationId) {
		var quotation = null;
		
		for (var i = 0; i < this.quotationList.length; i++) {
			if (this.quotationList[i].id == quotationId) {
				quotation = this.quotationList[i];
				break;
			}
		}
		
		var deliveryTimeInput = $('#biblivre_order_form_body input[name="delivery_time"]');
		
		if (!quotation) {
			this.root.find('.selected_results_area').processTemplate([]);
			deliveryTimeInput.val('');
			return true;
		}
		
		deliveryTimeInput.val(quotation.deliveryTime);
		
		var quotationItems = [];
		for (var i = 0; i < quotation.quotationsList.length; i++) {
			var item = quotation.quotationsList[i];
			quotationItems.push({
				id: item.id,
				name: item.author + " - " + item.title,
				quantity: item.quantity,
				value: item.unitValue
			});
		}
		
		var scroll = this.root.find('.selected_results_area ul').scrollTop();
		this.root.find('.selected_results_area').processTemplate(quotationItems);
		this.root.find('.selected_results_area ul').scrollTop(scroll);
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