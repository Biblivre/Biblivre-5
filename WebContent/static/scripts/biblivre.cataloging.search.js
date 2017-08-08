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
var HoldingSearch = HoldingSearch || null;
var CatalogingInput = CatalogingInput || {};

var CatalogingSearchClass = {
	enableHistory: true,
	autoSelect: false,
	historyQueryParam: 'query',
	historyMaterialParam: 'material',
	historyGroupParam: 'group',

	initialize: function() {
		this.advancedSearchForm = this.root.find('.search_box .advanced_search');
		this.simpleSearchForm = this.root.find('.search_box .simple_search');

		if (CirculationSearch) {
			Core.subscribe(CirculationSearch.prefix + 'open-record', function(e, record) {
				$('#cataloging_search').hide();
			}, this);	
			
			Core.subscribe(CirculationSearch.prefix + 'record-selected', function(e, record) {
				$('#cataloging_search').show();
			}, this);	
		}

		var div = this.root.find('.search_indexing_groups');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.search_indexing_groups_template'));
		}
		
		div = this.root.find('.search_sort_by');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.search_sort_by_template'));
		}
		
		div = this.root.find('.search_results');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.search_results_template'));
		}
		
		div = this.root.find('.selected_results_area');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.selected_results_area_template'));
		}

		div = this.root.find('.biblivre_holdings');
		if (div.size() > 0) {
			div.setTemplateElement(this.root.find('.biblivre_holdings_template'));
		}

		this.addEntryAdvancedSearch();
		this.addEntryAdvancedSearch();
		
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
		var me = this;

		// Look for changes in the value
		advancedQuery.bind("change propertychange keyup input paste", function(event) {
			var el = $(this);
			var val = el.val();
			if (el.data('oldVal') != val) {
				el.data('oldVal', val);

				var empty = true;
				me.root.find('.advanced_search :input[name=query]').each(function() {
					if ($.trim($(this).val()) != '') {
						empty = false;
						return false;
					}
				});
				
				advancedButton.text(empty ? _('search.common.button.list_all') : _('search.common.button.search'));
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
		$.History.bind($.proxy(function(trigger) {
			return this.historyRead(trigger);
		}, this));

		if (!Core.qhs('search')) {
			this.switchToSimpleSearch();
		};
	},
	afterHistoryRead: function(trigger) {
		if (this.isAdvancedSearch) {
			var query = Core.historyCheckAndSet(trigger, this.historyQueryParam);
			var group = Core.historyCheckAndSet(trigger, this.historyGroupParam);

			if (query.changed || group.changed) {
				if (query.value !== null) {
					this.searchTerm({
						query: query.value, 
						group: group.value
					});
				}
			}
		} else {
			var query = Core.historyCheckAndSet(trigger, this.historyQueryParam);
			var material = Core.historyCheckAndSet(trigger, this.historyMaterialParam);
			
			if (query.changed || material.changed) {
				if (query.value !== null) {
					this.searchTerm({
						query: query.value, 
						material: material.value
					});
				}
			}			
		}
	},
	clearResults: function(keepOrderingBar) {
		Core.trigger(this.prefix + 'clear-search');
		
		if (!keepOrderingBar) {
			this.root.find('.search_ordering_bar').hide();
			this.root.find('.search_indexing_groups').empty();
			this.root.find('.search_sort_by').empty();
			
			Core.removeMsg();
		}
	},
	clearAdvancedSearch: function() {
		var form = this.advancedSearchForm;

		form.find('.search_entry:gt(0)').remove();
		form.find(':input[name=query]').val('').change();
		form.find(':input.datepicker').val('');
		form.find('.combo_wrap').trigger('reset');
		form.find(':input[name="holding_label_never_printed"]').attr('checked', 'checked');
		
		this.addEntryAdvancedSearch();
		this.addEntryAdvancedSearch();
	},
	addEntryAdvancedSearch: function() {
		var entry = this.advancedSearchForm.find('.search_entry:last');
		var clone = entry.clone(true);
		
		clone.find(':input[name=query]').val('');
		clone.find('.combo_wrap').trigger('reset');
		clone.find('.search_label').remove();

		entry.after(clone);
	},
	getCurrentDatabase: function() {
		if (CatalogingInput.getCurrentDatabase) {
			return CatalogingInput.getCurrentDatabase();
		} else {
			return 'main';
		}
	},
	createPagingParameters: function(parameters, response) {
		return {
			page: response.search.page,
			search_id: response.search.id,
			indexing_group: 0
		};
	},
	simpleSearch: function() {
		var query = this.simpleSearchForm.find(':input[name=query]').val();
		var materialType = this.simpleSearchForm.find(':input[name=material]').val();
		
		var searchParameters = $.extend({
			database: this.getCurrentDatabase(),
			material_type: materialType
		}, this.extraParams || {});

		var reserved = this.root.find('.filter_search :input[name=record_list_reserved]').attr('checked');

		if (reserved) {
			searchParameters.reserved_only = true;
		}
		
		if (query) {
			searchParameters.search_mode = 'simple';
			searchParameters.search_terms = [{
				query: query
			}];
		} else {
			searchParameters.search_mode = 'list_all';
		}

		if (this.enableHistory) {
			var terms = {};
			terms[this.historyQueryParam] = query;
			terms[this.historyMaterialParam] = materialType;

			Core.historyTrigger(terms);
		}
		
		this.submit(searchParameters);
	},
	advancedSearch: function() {
		var searchParameters = $.extend({
			database: this.getCurrentDatabase(),
			material_type: this.advancedSearchForm.find(':input[name=material]').val(),
			search_mode: 'advanced'
		}, this.extraParams || {});
				
		var searchTerms = [];

		this.advancedSearchForm.find('.search_entry').each(function() {
			var entry = $(this);
			var query = entry.find(':input[name=query]').val();
			var field = entry.find(':input[name=field]').val();
			var operator = entry.find(':input[name=operator]').val();

			if ($.trim(query)) {
				searchTerms.push({
					query: query,
					field: field,
					operator: operator || 'AND'
				});
			}
		});
		
		var createdStartDate = this.advancedSearchForm.find(':input[name="created_start"]').val();
		var createdEndDate = this.advancedSearchForm.find(':input[name="created_end"]').val();
		
		createdStartDate = Globalize.parseDate(createdStartDate, 'd');
		createdEndDate = Globalize.parseDate(createdEndDate, 'd');
		
		if (createdStartDate || createdEndDate) {
			searchTerms.push({
				field: searchParameters.holding_search ? 'holding_created' : 'created',
				operator: 'AND',
				start_date: Globalize.format(createdStartDate, 'S'),
				end_date: Globalize.format(createdEndDate, 'S')
			});
		}
		
		var modifiedStartDate = this.advancedSearchForm.find(':input[name="modified_start"]').val();
		var modifiedEndDate = this.advancedSearchForm.find(':input[name="modified_end"]').val();
		
		modifiedStartDate = Globalize.parseDate(modifiedStartDate, 'd');
		modifiedEndDate = Globalize.parseDate(modifiedEndDate, 'd');
		
		if (modifiedStartDate || modifiedEndDate) {
			searchTerms.push({
				field: searchParameters.holding_search ? 'holding_modified' : 'modified',
				operator: 'AND',
				start_date: Globalize.format(modifiedStartDate, 'S'),
				end_date: Globalize.format(modifiedEndDate, 'S')
			});
		}
		
		if (searchParameters.holding_search) {
			var label = this.advancedSearchForm.find(':input[name=holding_label_never_printed]').attr('checked');

			if (label) {
				searchTerms.push({
					query: 'true',
					field: 'holding_label_never_printed',
					operator: 'AND'
				});
			}
		}

		var reserved = this.advancedSearchForm.find(':input[name=record_list_reserved]').attr('checked');

		if (reserved) {
			searchTerms.push({
				query: 'true',
				field: 'record_list_reserved',
				operator: 'AND'
			});
		}
		
		searchParameters.search_terms = searchTerms;
		this.submit(searchParameters);
	},
	afterDisplayResult: function(config) {
		this.root.find('.search_ordering_bar').show();
		this.root.find('.search_indexing_groups').processTemplate(config.response);
		this.root.find('.search_sort_by').processTemplate(config.response).find('select').combo();
	},
	changeIndexingGroup: function(indexingGroup) {
		if (!this.lastPagingParameters) {
			return;
		}
		
		this.lastPagingParameters.indexing_group = indexingGroup;
		this.lastPagingParameters.page = 1;
		this.paginate(this.lastPagingParameters);
	},
	changeSort: function(sort) {
		if (!this.lastPagingParameters) {
			return;
		}
		
		this.lastPagingParameters.sort = sort;
		this.lastPagingParameters.page = 1;
		this.paginate(this.lastPagingParameters);
	},
	searchTerm: function(obj) {	
		var query = $('<div />').html(obj.query).text();

		if (this.isSimpleSearch) {
			this.simpleSearchForm.find(':input[name=query]').val(query).trigger('keyup');

			if (obj.material !== undefined) {
				this.simpleSearchForm.find(':input[name=material]').trigger('setvalue', obj.material || 'all');
			}
		}
		
		if (this.isAdvancedSearch) {
			this.advancedSearchForm.find('.search_entry:first :input[name=query]').val(query).trigger('keyup');

			if (obj.group !== undefined) {
				this.advancedSearchForm.find('.search_entry:first :input[name=field]').trigger('setvalue', obj.group || 0);
			}
		}

		this.root.find('.search_box .main_button:visible').trigger('click');
	},
	tabHandler: function(tab, params) {
		try {
			if (HoldingInput && HoldingInput.editing) {
				return false;
			}
		} catch (e) {}
		
		params = params || {};
		data = params.data || this.selectedRecord;

		if (CatalogingInput.editing && !params.skipConvert) {
			if (tab == 'holding') {
				return false;
			}

			CatalogingInput.convert(this.selectedTab, tab, $.proxy(function(newData) {
				Core.changeTab(tab, this, { data: newData, keepEditing: true, skipConvert: true, force: true });
			}, this));

			return false;
		}

		this.selectedTab = tab;
		
		switch (tab) {
			case 'record':
				this.loadCatalogingRecord(data, params);
				break;
				
			case 'marc':
				this.loadCatalogingMarc(data, params);
				break;
				
			case 'form':
				this.loadCatalogingForm(data, params);
				break;
				
			case 'holding':
				this.loadHoldingList(data, params);
				break;
		}

		if (HoldingSearch && tab != 'holding') {
			HoldingSearch.closeResult();
			HoldingSearch.clearResults();
		}
	},
	clearTab: function(tab) {
		if (!this.enableTabs) {
			return;
		}
		
		CatalogingInput.clearTab(tab);

		switch (tab) {
			case 'record':
				$('#biblivre_record').empty().data('loaded', false);
				$('#biblivre_record_textarea').val('');
				$('#biblivre_attachments').empty();
				break;
				
			case 'form':
				$('#biblivre_form').addClass('template').data('loaded', false);
				break;

			case 'marc':
				$('#biblivre_marc').empty().data('loaded', false);
				$('#biblivre_marc_textarea').val('');
				break;
				
			case 'holding':
				this.root.find('.biblivre_holdings').empty().data('loaded', false);
				if (HoldingSearch) {
					HoldingSearch.clearResults();
				}
				break;
		}
	},
	clearAll: function() {
		if (!this.enableTabs) {
			return;
		}
		
		this.clearTab('record');
		this.clearTab('marc');
		this.clearTab('form');
		this.clearTab('holding');
	},
	loadCatalogingRecord: function(record, params) {
		var div = $('#biblivre_record');

		if (div.data('loaded')) {
			if (!params.force) {
				return;
			}

			this.clearTab('record');
		}

		div.data('loaded', true);

		var materialType = (record.material_type || '').toLowerCase();
		this.root.find('#biblivre_record input[name=material_type]').val(materialType);
		$('#biblivre_record_textarea').val(record.marc);
		
		div.processTemplate(record);

		$('#biblivre_attachments').processTemplate(record);		
	},
	loadCatalogingMarc: function(record, params) {
		var div = $('#biblivre_marc');

		if (div.data('loaded')) {
			if (!params.force) {
				return;
			}

			this.clearTab('marc');
		}

		div.data('loaded', true);

		var materialType = (record.material_type || '').toLowerCase();
		var materialTypeDiv = this.root.find('div.biblivre_marc_body select[name=material_type]'); 
		if (materialTypeDiv.length > 0) {
			materialTypeDiv.val(materialType);
		}

		var authorType = (record.author_type || '').toLowerCase();
		var authorTypeDiv = this.root.find('div.biblivre_marc_body select[name=author_type]'); 
		if (authorTypeDiv.length > 0) {
			authorTypeDiv.val(authorType);
		}
		
		$('#biblivre_marc_textarea').val(record.marc);

		if (!params.keepEditing) {
			CatalogingInput.setAsReadOnly('marc');
		}
	},
	loadCatalogingForm: function(record, params) {
		var form = $('#biblivre_form');

		if (form.data('loaded')) {
			if (!params.force) {
				return;
			}

			this.clearTab('form');
		}
		
		form.data('loaded', true).removeClass('template');

		CatalogingInput.initializeForm(form, CatalogingInput.formFields, this.type);

		var materialType = (record.material_type || '').toLowerCase();
		var materialTypeDiv = this.root.find('div.biblivre_form_body select[name=material_type]');
		if (materialTypeDiv.length > 0) {
			materialTypeDiv.val(materialType);
			CatalogingInput.toggleMaterialType(materialType);
		}
			
		var authorType = (record.author_type || '').toLowerCase();
		var authorTypeDiv = this.root.find('div.biblivre_form_body select[name=author_type]');
		if (authorTypeDiv.length > 0) {
			authorTypeDiv.val(authorType);
			CatalogingInput.toggleAuthorType(authorType);
		}

		CatalogingInput.loadJson(form, record.json);

		if (!params.keepEditing) {
			CatalogingInput.setAsReadOnly('form');
		}
	},
	loadHoldingList: function(record, params) {
		var div = this.root.find('.biblivre_holdings');
		
		this.clearTab('holdings');
		
		div.processTemplate(record);
		
		HoldingSearch.displayLocalResults({
			data: record.holdings
		});
	},
	linkToSearch: function(text, group) {
		var terms = (text || '').split('\n');
		var database = this.getCurrentDatabase();
		
		var html = [];
		for (var i = 0; i < terms.length; i++) {
			html.push('<a href="#query=' + encodeURIComponent(terms[i]) + '&database=' + database + '&group=' + group + '&search=advanced">' + terms[i] + '</a>');
		}
		
		return html.join('; ');
	},
	openHolding: function(holding_id) {
		var holding = {};
		var node = this.root.find('.holding_list div.result[rel=' + holding_id + ']');
		
		this.clearTab('holding_form');
		this.clearTab('holding_marc');
		this.selectedHolding = holding;

		var highlight = this.createRecordHighlight(node, holding, this.root.find('.selected_holding_highlight_template'));
		var selectedHighlight = this.root.find('.selected_holding_highlight');
		
		this.root.find('.selected_record').fadeOut(300);

		$('#content_inner').height($('#content_inner').height());
		$.when(
			$('#content_outer').stop().scrollTo(0, {
				duration: 600
			})
		).then(function() {			
			$('#content_inner').height('auto');
		});

		var me = this;
		
		highlight
			.appendTo('#content_inner')
			.css({
				position: 'absolute',
				left: node.position().left,
				top: node.position().top + parseInt(node.css('margin-top'), 10)
			})
			.animate({
				top: selectedHighlight.getHiddenDimensions().position.top
			}, 600, function() {				
				highlight.css({
					position: 'relative',
					top: 0,
					left: 0
				}).appendTo(selectedHighlight);

				selectedHighlight.show();
				highlight.fixButtonsHeight();
				
				me.root.find('.record_tabs').hide();
				me.root.find('.holding_tabs').show();
				me.root.find('.selected_record').fadeIn(300);

				Core.changeTab('holding_form', me);
			});
	},
	exportSelectedRecords: function() {
		var list = [];
		
		for (var i = 0; i < this.selectedList.length; i++) {
			list.push(this.selectedList[i].id);
		}
		
		if (list.length === 0) {
			return;
		}
		
		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: {
				controller: 'json',
				module: this.type,
				action: 'export_records',
				id_list: list.join(',')
			},
			loadingTimedOverlay: true,
			context: this
		}).done(function(response) {
			if (response.success) {
				window.open(window.location.pathname + '?controller=download&module=' + this.type + '&action=download_export&id=' + response.uuid);
				this.selectedList = [];
				this.updateSelectList();
				//this.search.paginate(this.search.lastPagingParameters);
			} else {
				Core.msg(response);
			}
		});
	},
	reserve: function(id, action) {
		var _action = action || 'reserve';
		if (!CirculationSearch.selectedRecord) {
			Core.msg({
				message: _('circulation.reservation.error.select_reader_first'),
				message_level: 'warning'
			});
			return;
		}
		
		var data = {
			controller: 'json',
			module: this.type,
			action: _action,
			record_id: id,
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
				Core.trigger(this.prefix + 'reservation-created', id, response.data);
			}
			
			Core.msg(response);
		});
	},
	deleteReservation: function(reservationInfo, action) {
		var _action = action || 'delete';
		if (!reservationInfo || !reservationInfo.reservation) {
			return;
		}
		
		var data = {
			controller: 'json',
			module: this.type,
			action: _action,
			id: reservationInfo.reservation.id
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
				Core.trigger(this.prefix + 'reservation-deleted', reservationInfo.reservation.id);				
			}

			Core.msg(response);
		});
	}
};