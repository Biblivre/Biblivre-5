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
var CirculationInput = CirculationInput || {};

var CirculationSearchClass = {
	enableHistory: true,
	autoSelect: false,
	historyQueryParam: 'query',
	historyFieldParam: 'field',
	defaultTab: 'form',

	initialize: function() {
		this.advancedSearchForm = this.root.find('.search_box .advanced_search');
		this.simpleSearchForm = this.root.find('.search_box .simple_search');
		
		var div = this.root.find('.search_results');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.search_results_template'));
		}

		div = this.root.find('.selected_results_area');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.selected_results_area_template'));
		}

		var simpleButton = this.root.find('.simple_search .main_button');
		var simpleQuery = this.root.find('.simple_search :input[name=query]');
		simpleQuery.data('oldVal', simpleQuery.val());

		// Look for changes in the value
		simpleQuery.bind("change propertychange keyup input paste", function(event) {
			var el = $(this);
			var val = el.val();
			if (el.data('oldVal') != val) {
				el.data('oldVal', val);
				
				simpleButton.text($.trim(val) == '' ? _('search.common.button.list_all') : _('search.common.button.search'));
			}
		});

		var advancedButton = this.root.find('.advanced_search .main_button');
		var advancedQuery = this.root.find('.advanced_search :input[name=query]');
		advancedQuery.data('oldVal', simpleQuery.val());

		// Look for changes in the value
		advancedQuery.bind("change propertychange keyup input paste", function(event) {
			var el = $(this);
			var val = el.val();
			if (el.data('oldVal') != val) {
				el.data('oldVal', val);
				
				advancedButton.text($.trim(val) == '' ? _('search.common.button.list_all') : _('search.common.button.search'));
			}
		});
		
		if (this.enableHistory) {
			this.initializeHistory();
		} else if (this.advancedSearchAsDefault) {
			this.switchToAdvancedSearch();
		} else {
			this.switchToSimpleSearch();
		}
	},
	initializeHistory: function() {
		$.History.bind($.proxy(this.historyRead, this));

		if (!Core.qhs('search')) {
			this.switchToSimpleSearch();
		};
	},
	afterHistoryRead: function(trigger) {
		var query = Core.historyCheckAndSet(trigger, this.historyQueryParam);
		var field = Core.historyCheckAndSet(trigger, this.historyFieldParam);

		if (query.changed || field.changed) {
			if (query.value !== null) {
				this.searchTerm({
					query: query.value,
					field: field.value
				});
			}
		}
	},
	clearResults: function() {
		Core.trigger(this.prefix + 'clear-search');
	},
	clearAdvancedSearch: function() {
		var form = this.advancedSearchForm;

		form.find(':input[name=query]').val('').change();
		form.find(':input.datepicker').val('');
		form.find('.combo_wrap').trigger('reset');
		form.find('.filter_checkbox :checkbox').removeAttr('checked');

		if (this.userCardSearch) {
			form.find('#users_without_user_card').attr('checked', 'checked');
		}
	},
	simpleSearch: function() {
		var query = this.simpleSearchForm.find(':input[name=query]').val();
		var field = this.simpleSearchForm.find(':input[name=field]').val();

		var searchParameters = {
			mode: 'simple',
			query: query,
			field: field
		};

		if (this.enableHistory) {
			var terms = {};
			terms[this.historyQueryParam] = query;
			terms[this.historyFieldParam] = field;

			Core.historyTrigger(terms);
		}
		
		this.submit(searchParameters);
	},
	advancedSearch: function() {
		var query = this.advancedSearchForm.find(':input[name=query]').val();
		var field = this.advancedSearchForm.find(':input[name=field]').val();

		var searchParameters = {
			mode: 'advanced',
			query: query,
			field: field,
			created_start: this.advancedSearchForm.find(':input[name="created_start"]').getIsoDate(),
			created_end: this.advancedSearchForm.find(':input[name="created_end"]').getIsoDate(),
			modified_start: this.advancedSearchForm.find(':input[name="modified_start"]').getIsoDate(),
			modified_end: this.advancedSearchForm.find(':input[name="modified_end"]').getIsoDate()
		};

		var label = this.advancedSearchForm.find(':input[name=users_with_pending_fines]').attr('checked');
		if (label) {
			searchParameters.users_with_pending_fines = true;
		}
		
		label = this.advancedSearchForm.find(':input[name=users_with_late_lendings]').attr('checked');
		if (label) {
			searchParameters.users_with_late_lendings = true;
		}

		label = this.advancedSearchForm.find(':input[name=users_who_have_login_access]').attr('checked');
		if (label) {
			searchParameters.users_who_have_login_access = true;
		}

		label = this.advancedSearchForm.find(':input[name=users_without_user_card]').attr('checked');
		if (label) {
			searchParameters.users_without_user_card = true;
		}


		label = this.advancedSearchForm.find(':input[name=inactive_users_only]').attr('checked');
		if (label) {
			searchParameters.inactive_users_only = true;
		}

		this.submit(searchParameters);		
	},
	afterDisplayResult: function(config) {
		if (this.lastSearchParameters.field) {
			var field = this.lastSearchParameters.field;
			var me = this;

			this.root.find('.search_results .result').each(function() {
				var el = $(this);
				var id = el.attr('rel');

				var user = me.lastSearchResult[id];
				
				if (!user || !user.fields || !user.fields[field]) {
					return;
				}

				var record = el.find('.record');
				record.append($('<label></label>').text(_('circulation.custom.user_field.' + field)));
				record.append(': ' + user.fields[field]);
			});

			this.root.find('.search_results .result').fixButtonsHeight();
		}
	},
	searchTerm: function(obj) {
		var query = $("<div />").html(obj.query).text();
		this.root.find('.search_box .simple_search :input[name=query]').val(query);

		if (obj.field !== undefined) {
			this.root.find('.search_box .simple_search :input[name=field]').trigger('setvalue', obj.field || '');
		}

		this.root.find('.search_box .main_button:visible').trigger('click');
	},
	loadRecord: function(record, callback) {
		this.clearAll();
		this.selectedRecord = record;
		
		if (this.enableTabs) {
			Core.changeTab(this.selectedTab || this.defaultTab, this);
		}
	},
	tabHandler: function(tab, params) {
		params = params || {};
		data = params.data || this.selectedRecord;

		try {
			if (CirculationInput.editing && tab != 'form') {
				return false;
			}
		} catch (e) {}

		if (tab != 'form' && !params.skipConvert) {
			CirculationSearch.loadTabData(tab, $.proxy(function(newData) {
				Core.changeTab(tab, this, { data: newData, keepEditing: params.keepEditing, skipConvert: true, force: true });
			}, this));

			return false;
		}

		this.selectedTab = tab;

		switch (tab) {
			case 'form':
				this.loadCirculationForm(data, params);
				break;

			case 'lendings':
				this.loadCirculationLendings(data, params);
				break;

			case 'reservations':
				this.loadCirculationReservations(data, params);
				break;
				
			case 'fines':
				this.loadCirculationFines(data, params);
				break;
		}
	},
	_loadTabDataXHR: null,
	loadTabData: function(tab, callback) {
		var user = this.selectedRecord.id;

		if (this._loadTabDataXHR) {
			this._loadTabDataXHR.abort();
		}
	
		this._loadTabDataXHR = $.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: {
				controller: 'json',
				module: this.type,
				action: 'load_tab_data',
				tab: tab,
				id: user
			},
			loadingTimedOverlay: true
		}).done(function(response) {
			if (response.success) {
				if ($.isFunction(callback)) {
					callback(response.data);
				}
			} else {
				Core.msg(response);
			}
		});
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'form':
				$('#biblivre_circulation_form').empty().data('loaded', false);
				$('#biblivre_circulation_form_body').empty();
				break;
				
			case 'lendings':
				$('#biblivre_circulation_lendings').empty().data('loaded', false);
				break;
				
			case 'reservations':
				break;	
				
			case 'fines':
				break;
		}
	},	
	clearAll: function() {
		this.clearTab('form');
		this.clearTab('lendings');
		this.clearTab('reservations');
		this.clearTab('fines');
	},	
	loadCirculationForm: function(record, params) {
		var div = $('#biblivre_circulation_form');

		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('form');
			} else {
				return;
			}
		}

		div.data('loaded', true);
		div.processTemplate(record);

		$('#biblivre_circulation_form_body').processTemplate(record);

		if (!params.keepEditing && CirculationInput) {
			CirculationInput.setAsReadOnly();
		}
	},	
	loadCirculationLendings: function(record, params) {
		var div = $('#biblivre_circulation_lendings');

		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('lendings');
			} else {
				return;
			}
		}
		
		div.data('loaded', true);
		div.processTemplate(record);
		
		
		var wrap = $('<div class="m30nfc"></div>');
		div.find('.user_lending_active_lending').wrapAll(wrap).parent().prepend($('<strong></strong>').text(_('circulation.user.active_lendings')));

		wrap = $('<div class="m30nfc"></div>');
		div.find('.user_lending_returned_lending').wrapAll(wrap).parent().prepend($('<strong></strong>').text(_('circulation.user.returned_lendings')));
	},
	loadCirculationReservations: function(record, params) {
		var div = $('#biblivre_circulation_reservations');

		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('reservations');
			} else {
				return;
			}
		}
		
		div.data('loaded', true);
		div.processTemplate(record);
	},
	loadCirculationFines: function(record, params) {
		var div = $('#biblivre_circulation_fines');

		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('fines');
			} else {
				return;
			}
		}
		
		div.data('loaded', true);
		div.processTemplate(record);
	},
	payFine: function(fineId, exempt) {
		var data = {
			controller: 'json',
			module: 'circulation.lending',
			action: 'pay_fine',
			fine_id: fineId,
			exempt: exempt
		};

		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: data,
			loadingTimedOverlay: true,
			context: this
		}).done(function(response) {
			if (response.success) {
				var fineDiv = $('.user_fines[rel="' + fineId + '"]'); 
				fineDiv.find('.fines_buttons').remove();
				fineDiv.find('.description').remove();
				var newDiv = '<label>' + _('circulation.lending.payment_date') + '</label>: '+  _d(new Date(), 'D') + '<br/>';
				fineDiv.find('.record').append(newDiv);
			}

			Core.msg(response);
		});
	},
	blockUser: function(userId) {
		var data = {
			controller: 'json',
			module: this.type,
			action: 'block',
			user_id: userId
		};

		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: data,
			loadingTimedOverlay: true,
			context: this
		}).done(function(response) {
			if (response.success) {
				if (!CirculationSearch.lastSearchResult) {
					return;
				}
				for (var key in CirculationSearch.lastSearchResult) {
					if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
						continue;
					}

					var result = CirculationSearch.lastSearchResult[key];
					if (result.id == userId) {
						result.status = 'blocked';
						CirculationSearch.processResultTemplate();
						break;
					}
				}
			}

			Core.msg(response);
		});
	},
	unblockUser: function(userId) {
		var data = {
			controller: 'json',
			module: this.type,
			action: 'unblock',
			user_id: userId
		};

		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: data,
			loadingTimedOverlay: true,
			context: this
		}).done(function(response) {
			if (response.success) {
				if (!CirculationSearch.lastSearchResult) {
					return;
				}
				for (var key in CirculationSearch.lastSearchResult) {
					if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
						continue;
					}

					var result = CirculationSearch.lastSearchResult[key];
					if (result.id == userId) {
						result.status = 'active';
						CirculationSearch.processResultTemplate();
						break;
					}
				}
			}

			Core.msg(response);
		});
	},
	_printReceiptXHR: null,
	receiptList: [],
	printReceipt: function(id) {
		if (this._printReceiptXHR) {
			this._printReceiptXHR.abort();
		}

		this._printReceiptXHR = $.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: {
				controller: 'json',
				module: 'circulation.lending',
				action: 'print_receipt',
				id_list: id || this.receiptList.join(',')
			},
			loadingTimedOverlay: true
		}).done(function(response) {
			if (response.success && response.receipt) {
				var w = window.open();
				$(w.document.body).html(response.receipt);
				w.print();
			} else {
				Core.msg(response);
			}
		});
	}
};