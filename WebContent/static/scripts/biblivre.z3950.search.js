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
var Z3950SearchClass = $.extend(CatalogingSearchClass, {
	initialize: function() {
		var me = this;
		
		this.prefix = this.type = 'z3950';
		this.root = $('body');
		
		this.root.find('.search_results').setTemplateElement(this.root.find('.search_results_template'));

		$.History.bind(function(trigger) {
			return me.historyRead(trigger);
		});

		me.switchToSimpleSearch();
		
		
	},
	afterHistoryRead: function(trigger) {
		var query = Core.historyCheckAndSet(trigger, 'query');
		var attribute = Core.historyCheckAndSet(trigger, 'attribute');
		var server = Core.historyCheckAndSet(trigger, 'server');

		if (query.changed || attribute.changed || server.changed) {
			if (query.value !== null) {
				this.searchTerm({
					query: query.value, 
					attribute: attribute.value,
					server: server.value
				});
			}
		}			
	},
	clearResults: function(keepOrderingBar) {
		Core.trigger(this.prefix + 'clear-search');
		
		if (!keepOrderingBar) {
			Core.removeMsg();
		}
	},
	simpleSearch: function() {
		var query = this.root.find('.search_box .simple_search :input[name=query]').val();
		var attribute = this.root.find('.search_box .simple_search :input[name=attribute]').val();

		if (!query) {
			return; //TODO mensagem
		}

		var server = [];
		this.root.find('.search_box .distributed_servers :checkbox[name=server]:checked').each(function() {
			server.push($(this).val());
		});
		
		if (server.length == 0) {
			var el = this.root.find('.search_box .distributed_servers :checkbox[name=server]:first');
			el.prop('checked', true);
			server.push(el.val());
		}

		serverList = server.join(',');
		
		var searchParameters = {
			query: query,
			attribute: attribute,
			server: serverList
		};
		
		Core.historyTrigger({
			query: query, 
			attribute: attribute,
			server: serverList
		});

		this.submit(searchParameters);
	},
	afterDisplayResult: function() {
		// Empty function to override CatalogingSearch
	},
	searchTerm: function(obj) {	
		var query = $("<div />").html(obj.query).text();

		this.root.find('.search_box .simple_search :input[name=query]').val(query);
		this.root.find('.search_box .simple_search :input[name=attribute]').trigger('setvalue', obj.attribute);
		
		var server = (obj.server || '').split(',');
		this.root.find('.search_box .distributed_servers :checkbox[name=server]').each(function() {
			$(this).prop("checked", ($.inArray($(this).val(), server) != -1));
		});

		this.root.find('.search_box .main_button:visible').trigger('click');
	},
	loadRecordExtraParams: function() {
		return {
			search_id: this.lastPagingParameters.search_id
		};
	},
	getSearchResult: function(record_id) {
		var result = this.lastSearchResult[record_id] || {};
		
		return $.extend({}, result, result.record);
	}
});