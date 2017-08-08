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
var CirculationSearch = CirculationSearch || null;
var CatalogingSearch = CatalogingSearch || null;
var CatalogingInput = CatalogingInput || null;

var HoldingSearchClass = {
	enableHistory: false,
	autoSelect: false,
	historyQueryParam: 'query',
	historyFieldParam: 'field',

	initialize: function() {
		this.root.find('.search_results').setTemplateElement(this.root.find('.search_results_template'));

		if (CirculationSearch) {
			Core.subscribe(CirculationSearch.prefix + 'open-record', function(e, record) {
				$('#holding_search').hide();
			}, this);	
			
			Core.subscribe(CirculationSearch.prefix + 'record-selected', function(e, record) {
				$('#holding_search').show();
			}, this);	
		}

		if (CatalogingSearch) {
			Core.subscribe(CatalogingSearch.prefix + 'record-selected', function(e) {
				$('#holding_search').show();
				this.toggleSearch(false, true);
			}, this);	

			Core.subscribe(CatalogingSearch.prefix + 'display-search', function(e) {
				$('#holding_search').hide();
				this.closeResult(true);

				if (CatalogingSearch.selectedTab == 'holding') {
					CatalogingSearch.selectedTab = 'record';
				}
			}, this);

			Core.subscribe(CatalogingSearch.prefix + 'next-result', function(e) {
				this.closeResult();
			}, this);
			
			Core.subscribe(CatalogingSearch.prefix + 'previous-result', function(e) {
				this.closeResult();
			}, this);
			
			
			if (HoldingInput) {
				Core.subscribe(HoldingInput.prefix + 'edit-record-start', function() {
					$('#database_selection_combo, #new_record_button, #new_holding_button').disable();
				}, this);

				Core.subscribe(HoldingInput.prefix + 'edit-record-end', function() {
					$('#database_selection_combo, #new_record_button, #new_holding_button').enable();
				}, this);
				
			}
			
		}
		
		if (CatalogingInput) {
			Core.subscribe(CatalogingInput.prefix + 'record-new', function(e) {
				this.closeResult();
			}, this);
			
			if (HoldingInput) {
				Core.subscribe(HoldingInput.prefix + 'edit-record-start', function() {
					CatalogingInput.root.find('.page_navigation .button').disable();
					CatalogingInput.root.find('.selected_highlight').find('.button, .danger_button').disable();
				}, this);

				Core.subscribe(HoldingInput.prefix + 'edit-record-end', function() {
					CatalogingInput.root.find('.page_navigation .button').enable();
					CatalogingInput.root.find('.selected_highlight').find('.button, .danger_button').enable();
				}, this);
			}
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
		
		if (this.enableHistory) {
			this.initializeHistory();
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
	clearResults: function(keepOrderingBar) {
		Core.trigger(this.prefix + 'clear-search');
	},
	tabHandler: function(tab, params) {
		params = params || {};
		data = params.data || this.selectedRecord;

		var oldTab = this.selectedTab;

		this.selectedTab = tab;
		if (HoldingInput.editing && !params.skipConvert) {

			HoldingInput.convert(oldTab, tab, $.proxy(function(newData) {
				Core.changeTab(tab, this, { data: newData, keepEditing: true, skipConvert: true, force: true });
			}, this));

			return false;
		}
		
		switch (tab) {
			case 'holding_marc':
				this.loadHoldingMarc(data, params);
				break;
				
			case 'holding_form':
				this.loadHoldingForm(data, params);
				break;
		}
	},
	clearTab: function(tab) {
		if (!this.enableTabs) {
			return;
		}
		
		HoldingInput.clearTab(tab);

		switch (tab) {
			case 'holding_form':
				$('#biblivre_holding_form').addClass('template').data('loaded', false);
				break;

			case 'holding_marc':
				$('#biblivre_holding_marc').empty().data('loaded', false);
				$('#biblivre_holding_marc_textarea').val('');
				break;
		}
	},
	clearAll: function() {
		if (!this.enableTabs) {
			return;
		}
		
		this.clearTab('holding_form');
		this.clearTab('holding_marc');
	},
	loadHoldingMarc: function(record, params) {
		var div = $('#biblivre_holding_marc');

		if (div.data('loaded')) {
			if (!params.force) {
				return;
			}

			this.clearTab('holding_marc');
		}

		div.data('loaded', true);

		var availability = (record.availability || '').toLowerCase();
		this.root.find('div.biblivre_holding_marc_body select[name=holding_availability]').val(availability);
		$('#biblivre_holding_marc_textarea').val(record.marc);

		if (!params.keepEditing) {
			HoldingInput.setAsReadOnly('holding_marc');
		}
	},
	loadHoldingForm: function(record, params) {
		var form = $('#biblivre_holding_form');

		if (form.data('loaded')) {
			if (!params.force) {
				return;
			}

			this.clearTab('holding_form');
		}

		form.data('loaded', true).removeClass('template');

		HoldingInput.initializeForm(form, CatalogingInput.holdingFields, this.type);

		var availability = (record.availability || '').toLowerCase();
		this.root.find('div.biblivre_holding_form_body select[name=holding_availability]').val(availability);

		HoldingInput.loadJson(form, record.json);

		if (!params.keepEditing) {
			HoldingInput.setAsReadOnly('holding_form');
		}
	},
	simpleSearch: function() {
		var query = this.root.find('.search_box .simple_search :input[name=query]').val();
		var holding_list_lendings = this.root.find('.search_box :input[name=holding_list_lendings]').attr('checked');
		
		var searchParameters = {
			query: query
		};

		if (holding_list_lendings) {
			searchParameters.holding_list_lendings = true;
		}
		
		this.submit(searchParameters);
	},
	lend: function(id) {
		Core.msg({
			message: _('circulation.lending.error.select_reader_first'),
			message_level: 'warning'
		});

		
		var data = {
			controller: 'json',
			module: this.type,
			action: 'lend',
			holding_id: id,
			user_id: CirculationSearch.selectedRecord.id
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
				Core.trigger(this.prefix + 'holding-lent', id, response.data);
			}
			
			Core.msg(response);
		});
	},
	returnLending: function(lendingInfo) {
		if (!lendingInfo || !lendingInfo.lending) {
			return;
		}
		
		if (lendingInfo.lending.daysLate > 0) {
			this.selectedLendingInfo = lendingInfo;
			
			Core.showOverlay();

			var popup = $('#fine_popup')
				.appendTo('body')
				.show()
				.center();

			popup.find('.days_late').text(lendingInfo.lending.daysLate);
			popup.find('.daily_fine').text(_f(lendingInfo.lending.dailyFine || 0, 'n2'));
			popup.find('input[name="fine_value"]').val(_f(lendingInfo.lending.estimatedFine || 0, 'n2'));
		} else {
			this.submitReturnLending(lendingInfo, 0.0, true);
		}
	},
	renewLending: function(lendingInfo) {
		var data = {
			controller: 'json',
			module: this.type,
			action: 'renew_lending',
			id: lendingInfo.lending.id
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
				Core.trigger(this.prefix + 'holding-renewed', lendingInfo.lending.id, response.data);				
			}

			Core.msg(response);
		});
	},
	submitReturnLending: function(lendingInfo, fine, paid) {
		var data = {
			controller: 'json',
			module: this.type,
			action: 'return_lending',
			id: lendingInfo.lending.id,
			fine: fine,
			paid: paid
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
				Core.trigger(this.prefix + 'holding-returned', lendingInfo.lending.id);				
				this.closeFinePopup();
			}

			Core.msg(response);
		});
	},
	closeFinePopup: function() {
		this.selectedLendingInfo = null;

		Core.hideOverlay();
		$('#fine_popup').hide();
	},
	applyFine: function() {
		var fine = Globalize.parseFloat($('#fine_popup input[name="fine_value"]').val());
		this.submitReturnLending(this.selectedLendingInfo, fine, false);
	},
	payFine: function() {
		var fine = Globalize.parseFloat($('#fine_popup input[name="fine_value"]').val());
		this.submitReturnLending(this.selectedLendingInfo, fine, true);
	},
	dismissFine: function() {
		this.submitReturnLending(this.selectedLendingInfo, 0.0, true);
	}
};