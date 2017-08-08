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
var AccessCardsInput = AccessCardsInput || {};

var AccessCardsSearchClass = {
	enableHistory: true,
	historyQueryParam: 'query',
	historyStatusParam: 'status',

	initialize: function() {
		this.root.find('.search_results').setTemplateElement(this.root.find('.search_results_template'));

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
	afterHistoryRead: function(trigger) {
		var query = Core.historyCheckAndSet(trigger, this.historyQueryParam);
		var status = Core.historyCheckAndSet(trigger, this.historyStatusParam);

		if (query.changed || status.changed) {
			if (query.value !== null && status.value !== null) {
				this.searchTerm({
					query: query.value,
					status: status.value
				});
			}
		}
	},
	clearResults: function() {
		Core.trigger(this.prefix + 'clear-search');
	},
	simpleSearch: function(extraParams) {
		var query = this.root.find('.search_box .simple_search :input[name=query]').val();
		var status = this.root.find('.search_box .simple_search :input[name=status]').val();

		var searchParameters = {
			mode: 'simple',
			query: query,
			status: status
		};
		
		if (this.enableHistory) {
			var terms = {};
			terms[this.historyQueryParam] = query;
			terms[this.historyStatusParam] = status;

			Core.historyTrigger(terms);
		}
		
		this.submit(searchParameters, extraParams);
	},
	afterDisplayResult: function(config) {
		this.root.find('.search_results .result').fixButtonsHeight();
	},
	searchTerm: function(obj) {
		var query = $("<div />").html(obj.query).text();
		this.root.find('.search_box .simple_search :input[name=query]').val(query);
		this.root.find('.search_box .simple_search :input[name=status]').trigger('setvalue', obj.status || 'all');
		
		this.root.find('.search_box .main_button:visible').trigger('click');
	},
	loadRecord: function(record, callback) {
		this.clearAll();
		this.selectedRecord = record;

		if (this.enableTabs) {
			Core.changeTab(this.selectedTab || 'form', this);
		}
	},
	tabHandler: function(tab, params) {
		params = params || {};
		data = params.data || this.selectedRecord;

		this.selectedTab = tab;
		switch (tab) {
			case 'form':
				this.loadAccessCardForm(data, params);
				break;
		}
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'form':
				$('#biblivre_accesscards_form_body').empty().data('loaded', false);
				$('#biblivre_accesscards_single_form_body').empty().data('loaded', false);
				$('#biblivre_accesscards_multiple_form_body').empty().data('loaded', false);
				break;
		}
	},	
	clearAll: function() {
		this.clearTab('form');
	},	
	loadAccessCardForm: function(record, params) {
		var div = $('#biblivre_accesscards_form_body');
		
		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('form');
			} else {
				return;
			}
		}
		
		div.processTemplate(record);
		$('#biblivre_accesscards_single_form_body').processTemplate(record);
		$('#biblivre_accesscards_multiple_form_body').processTemplate(record);
		
		if (!params.keepEditing && AccessCardsInput) {
			AccessCardsInput.setAsReadOnly();
		}
	}
};