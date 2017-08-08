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
var CreateSearch = function(extend, params) {
	var config = $.extend({
		autoSelect: false,
		autoSearch: false,
		enableHistory: true,
		enableTabs: true,
		enablePaging: true,
		defaultTab: 'record'
	}, extend, params);
	return new Search(config);
};

var Search = function(extend) {
	$.extend(this, extend);
	this.prefix = (this.prefix || this.type) + '.';

	$(document).ready($.proxy(function() {
		this.root = $(this.root);
		this.initialize();

		Core.subscribe(this.prefix + 'clear-search', function() {
			this.selectedRecord = null;
			this.lastSearchResult = {};

			this.root.find('.search_results').empty();
			this.root.find('.paging_bar').empty();
			this.root.find('.select_bar').hide();
		}, this);

		Core.subscribe(this.prefix + 'process-results', function() {
			this.root.find('.search_results .result').fixButtonsHeight();
		}, this);

		Core.subscribe(this.prefix + 'display-search', function() {
			this.root.find('.search_results .result').fixButtonsHeight();
		}, this);

		Core.subscribe(this.prefix + 'open-record', function(e, record) {
			this.displayPagingButtons(record.id);
		}, this);

		Core.subscribe(this.prefix + 'record-selected', function(e, record) {
			this.root.find('.selected_highlight .clone').fixButtonsHeight();
		}, this);

		Core.subscribe(this.prefix + 'record-changed', function(e, record) {
			if (this.lastSearchResult && this.lastSearchResult[record.id]) {
				this.lastSearchResult[record.id] = record;
			}

			this.processResultTemplate();
		}, this);		

//		Core.subscribe(this.prefix + 'hide-search', function() {
//			this.root.find('.page_navigation :not(.back_to_search)').show();
//		});
		
		if (this.autoSearch) {
			if (!this.enableHistory || Core.qhs('query') === undefined) {
				this.search('simple', {
					disableMessage: true
				});
			}
		}
	}, this));
};

Search.prototype.lastSearchParameters = null;
Search.prototype.lastPagingParameters = null;

Search.prototype.lastSearchResult = null;
Search.prototype.lastSearchResultCounts = null;

Search.prototype.initialize = function() {
};

Search.prototype.switchToSimpleSearch = function() {
	this.root.find('.advanced_search').hide();
	this.root.find('.simple_search').show();
	
	this.isSimpleSearch = true;
	this.isAdvancedSearch = false;
	
	Core.trigger(this.prefix + 'switched-to-simple-search');
};

Search.prototype.switchToAdvancedSearch = function() {
	this.root.find('.simple_search').hide();	
	this.root.find('.advanced_search').show();
	
	this.isSimpleSearch = false;
	this.isAdvancedSearch = true;

	Core.trigger(this.prefix + 'switched-to-advanced-search');
};

Search.prototype.clearSimpleSearch = function() {
	this.clearResults();
	this.root.find('input[name="query"]').val('').change();
};

Search.prototype.clearAdvancedSearch = function() {
	console.log('Implementation needed');
};

Search.prototype.historyRead = function(trigger) {
	var search = Core.historyCheckAndSet(trigger, 'search');
	if (search.changed) {
		if (search.value == 'advanced') {
			this.switchToAdvancedSearch();
		} else {
			this.switchToSimpleSearch();
		}		
	}

	if (this.afterHistoryRead) {
		this.afterHistoryRead(trigger);
	}
};

Search.prototype.search = function(mode, params) {
	this.searched = true;
	
	Core.trigger(this.prefix + 'before-search');
	
	if (mode == 'simple') {
		this.simpleSearch(params);
	} else {
		this.advancedSearch(params);
	}

	Core.trigger(this.prefix + 'after-search');
};

Search.prototype.redoSearch = function(mode) {
	if (this.searched) {
		this.clearResults();

		setTimeout($.proxy(function() {
			this.root.find('.search_box .main_button:visible').trigger('click');
		}, this), 0);
	}
};

Search.prototype.simpleSearch = function(params) {
	console.log('Implementation needed');
};

Search.prototype.advancedSearch = function(params) {
	console.log('Implementation needed');
};

Search.prototype._submitXHR = null;
Search.prototype.submit = function(searchParameters, extraParams) {
	searchParameters = $.extend({}, searchParameters); // Cloning to change reference
	extraParams = extraParams || {};

	Core.trigger(this.prefix + 'search-submit');

	this.clearResults();

	this.lastSearchParameters = searchParameters;
	this.lastPagingParameters = null;

	var parameters = {
		controller: 'json',
		module: this.type,
		action: this.searchAction || 'search',
		search_parameters: JSON.stringify(searchParameters)
	};

	if (this._submitXHR) {
		this._submitXHR.abort();
	}
	
	this._submitXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: parameters,
		loadingHolder: this.root.find('div.loading_indicator'),
		context: this
	}).done(function(response) {
		if (this.lastSearchParameters != searchParameters) {
			return;
		}

		Core.trigger(this.prefix + 'search-response', response);
		
		if (!extraParams.disableMessage) {
			Core.msg(response);
		}

		if (!response.success) {
			return;
		}
		
		// On success, reload the search results
		if (!response.search) {
			return;
		}

		var pagingParameters = this.createPagingParameters(parameters, response);

		this.lastPagingParameters = pagingParameters;

		this.displayResults({
			searchParameters: searchParameters,
			pagingParameters: pagingParameters,
			response: response,
			keepMessage: !extraParams.disableMessage
		});

		Core.trigger(this.prefix + 'after-search-response', response);
	});
};

Search.prototype.createPagingParameters = function(parameters, response) {
	return {
		page: response.search.page,
		search_parameters: parameters.search_parameters
	};
};

Search.prototype._lastSearchXHR = null;
Search.prototype.paginate = function(pagingParameters, callback, keepMessage) {
	pagingParameters = $.extend({}, pagingParameters); // Cloning to change reference

	Core.trigger(this.prefix + 'before-paginate');
	
	this.clearResults(true);

	this.lastPagingParameters = pagingParameters;

	var parameters = $.extend({
		controller: 'json',
		module: this.type,
		action: this.paginateAction || 'paginate'
	}, pagingParameters);

	if (this._lastSearchXHR) {
		this._lastSearchXHR.abort();
	}
	
	this._lastSearchXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: parameters,
		loadingHolder: this.root.find('div.loading_indicator'),
		context: this
	}).done(function(response) {
		if (this.lastPagingParameters != pagingParameters) {
			return;
		}
		
		Core.trigger(this.prefix + 'paginate-response', response);
		
		if (!response.success) {
			Core.msg(response);
			this.clearResults();
			return;
		}

		// On success, reload the search results
		if (!response.search) {
			return;
		}

		var searchParameters = this.lastSearchParameters;				

		this.displayResults({
			searchParameters: searchParameters,
			pagingParameters: pagingParameters,
			response: response,
			keepMessage: keepMessage
		});
		
		if ($.isFunction(callback)) {
			callback();
		}
		
		Core.trigger(this.prefix + 'after-paginate-response', response);
	});

	Core.trigger(this.prefix + 'after-paginate');
};

Search.prototype.displayResults = function(o) {
	var config = $.extend({}, {
		
	}, o);

	Core.trigger(this.prefix + 'before-display-results');
	
	var search = Core.get(config, 'response.search');
	var data = Core.get(config, 'response.search.data');

	if (!search || !data) {
		return;
	}
	
	this.selectedRecord = null;
	this.lastSearchResult = {};

	for (var i = 0; i < data.length; i++) {
		var record = data[i];
		this.lastSearchResult[record.id] = record;
		this.lastSearchResult[record.id].index = i + 1;
	}

	if (data.length > 0) {
		this.root.find('div.search_print').show();
	}

	this.processResultTemplate();
	
	//Paging
	var recordsInThisPage = data.length;
	var pageCount = parseInt(search.page_count, 10);
	var currentPage = parseInt(search.page, 10);
	var recordsPerPage = parseInt(search.records_per_page, 10);
	var recordCount = parseInt(search.record_count, 10);
	var recordLimit = parseInt(search.record_limit, 10);
	var me = this;

	Core.pagingGenerator({
		pagingHolder: this.root.find('div.paging_bar'),
		pageCount: pageCount,
		currentPage: currentPage,
		recordsPerPage: recordsPerPage,
		linkFunction: function() {
			config.pagingParameters.page = $(this).attr('rel');
			me.paginate(config.pagingParameters);
		}
	});

	if (!config.keepMessage) {
		Core.removeMsg();
	}
	
	if (recordLimit && recordLimit < recordCount) {
		Core.msg({
			message_level: 'warning',
			animate: false,
			sticky: false,
			translate: false,
			message: _('search.common.search_limit', [_f(recordCount), _f(recordLimit)])
		});
	}
	
	this.lastSearchResultCounts = {
		recordsInThisPage: recordsInThisPage,
		pageCount: pageCount,
		currentPage: currentPage,
		recordsPerPage: recordsPerPage,
		recordCount: recordCount,
		recordLimit: recordLimit
	};

	this.root.find('.select_bar').show();
	
	if (this.afterDisplayResult) {
		this.afterDisplayResult(config);
	}
	
	if (this.autoSelect && data && data.length == 1) {
		if ($.isFunction(this.autoSelect)) {
			this.autoSelect(data[0]);
		} else {
			this.openResult(data[0].id, true);
		}
	}
	
	Core.trigger(this.prefix + 'display-results');
};

Search.prototype.displayLocalResults = function(o) {
	var config = $.extend({}, {
		
	}, o);

	Core.trigger(this.prefix + 'before-display-results');
	
	var data = Core.get(config, 'data');

	if (!data) {
		return;
	}
	
	this.selectedRecord = null;
	this.lastSearchResult = {};

	for (var i = 0; i < data.length; i++) {
		var record = data[i];
		this.lastSearchResult[record.id] = record;
		this.lastSearchResult[record.id].index = i + 1;
	}

	this.processResultTemplate();

	Core.trigger(this.prefix + 'display-results');
};

Search.prototype.processResultTemplate = function() {
	var results = [];

	if (!this.closed) {
		Core.trigger(this.prefix + 'before-process-results');
	}
	
	for (var id in this.lastSearchResult) {
		if (!this.lastSearchResult.hasOwnProperty(id)) {
			continue;
		}
		results.push(this.lastSearchResult[id]);
	}

	results = results.sort(function(a, b) {
		return (a.index > b.index) ? 1 : (a.index < b.index) ? -1 : 0;
	});	

	this.root.find('.search_results').processTemplate({
		data: results
	});
	
	if (!this.closed) {
		Core.trigger(this.prefix + 'process-results');
	}
};

Search.prototype.createRecordHighlight = function(result, record, templateElement) {
	templateElement = templateElement || this.root.find('.selected_highlight_template');
	
	var highlight = $('<div></div>').setTemplateElement(templateElement).processTemplate(record);
	
	highlight
		.addClass('clone')
		.attr('rel', record.id)
		.css({ width: '100%' });
	
	if (result) {
		highlight.addClass(result.hasClass('odd') ? 'odd' : 'even');
	} else {
		highlight.addClass('odd');
	}

	return highlight;
};

Search.prototype.getSearchResult = function(record_id) {
	return this.lastSearchResult[record_id];
};

Search.prototype.setSearchResult = function(record_id, record) {
	return this.lastSearchResult[record_id] = record;
};

Search.prototype.openResult = function(record_id, instant) {
	var record = this.getSearchResult(record_id);
	var node = this.root.find('.search_results div.result[rel=' + record_id + ']');

	var me = this;
	this.loadRecord(record, function(response) {
		me.clearAll();
		me.selectedRecord = response.data;
		me.selectedRecord.index = record.index;

		if (me.enableTabs) {
			Core.changeTab(me.selectedTab || me.defaultTab, me);
		}

		Core.trigger(me.prefix + 'after-load-record', response.data);
	});

	Core.trigger(this.prefix + 'open-record', record, instant);
	
	this.selectedRecord = record;

	if (this.enablePaging) {
		var currentCount = (this.lastSearchResultCounts.currentPage - 1) * this.lastSearchResultCounts.recordsPerPage + record.index;
		var totalCount = this.lastSearchResultCounts.recordCount;
		var limitCount = this.lastSearchResultCounts.recordLimit;
		
		if (limitCount && totalCount > limitCount) {
			totalCount = limitCount;
		}

		this.root.find('.search_count').text(_('search.common.search_count', { current: _f(currentCount), total: _f(totalCount) }));
	}
		
	var highlight = this.createRecordHighlight(node, record);
	var selectedHighlight = this.root.find('.selected_highlight');
	var me = this;
	
	var delay = (instant) ? 0 : 600;
	
	if (!this.closed) {
		var startAt = node.position().top + parseInt(node.css('margin-top'), 10);
		var slideTo = selectedHighlight.getHiddenDimensions().position.top;

		if (startAt == slideTo) {
			delay = 0;
			instant = true;
		}
		
		highlight
			.appendTo('#content_inner')
			.css({
				position: 'absolute',
				left: node.position().left,
				top: startAt
			})
			.animate({
				top: slideTo
			}, delay, function() {
				highlight.css({
					position: 'relative',
					top: 0,
					left: 0
				}).appendTo(selectedHighlight);

				me.root.find('.selected_highlight, .selected_record').show();

				Core.trigger(me.prefix + 'record-selected', record);
			});
	} else {
		selectedHighlight.children('.clone:first').remove();

		highlight.css({ position: 'relative' }).appendTo(selectedHighlight);

		Core.trigger(me.prefix + 'record-selected', record);
	}

	this.toggleSearch(true, instant);
};

Search.prototype.reloadResult = function() {
	if (!this.selectedRecord || this.selectedRecord.id === undefined) {
		return;
	}
	
	$('#content_inner').height($('#content_inner').height());
	this.root.find('.search_results div.result[rel=' + this.selectedRecord.id + '] div.buttons a[rel=open_item]').trigger('click');

	setTimeout(function() {
		$('#content_inner').height('auto');
	}, 2000);
};

Search.prototype.nextResult = function() {
	if (!this.selectedRecord || this.selectedRecord.id === undefined) {
		return;
	}

	Core.trigger(this.prefix + 'next-result');
	
	var next = this.root.find('.search_results div.result[rel=' + this.selectedRecord.id + ']').nextAll('.result:first');
	var me = this;
	
	if (next.size()) {
		next.find('div.buttons a[rel=open_item]').trigger('click');
	} else if (this.lastPagingParameters.page != this.lastSearchResultCounts.pageCount) {
		this.lastPagingParameters.page++;
		this.paginate(this.lastPagingParameters, function() {
			me.root.find('.search_results div.buttons a[rel=open_item]:first').click();
		});
	}
};

Search.prototype.previousResult = function() {
	if (!this.selectedRecord || this.selectedRecord.id === undefined) {
		return;
	}

	Core.trigger(this.prefix + 'previous-result');
	
	var prev = this.root.find('.search_results div.result[rel=' + this.selectedRecord.id + ']').prevAll('.result:first');
	var me = this;
	
	if (prev.size()) {
		prev.find('div.buttons a[rel=open_item]').trigger('click');
	} else if (this.lastPagingParameters.page > 1) {
		this.lastPagingParameters.page--;
		this.paginate(this.lastPagingParameters, function() {
			me.root.find('.search_results div.buttons a[rel=open_item]:last').click();
		});
	}
};

Search.prototype.closeResult = function(closeSearch) {
	if (!this.closed) {
		return;
	}

	Core.trigger(this.prefix + 'close-result');
	
	this.root.find('.selected_highlight').empty().hide();
	this.root.find('.selected_record').hide();
	
	this.toggleSearch(closeSearch || false);
};

Search.prototype.closed = false;
Search.prototype.toggleSearch = function(close, instant) {
	if (this.closed == close) {
		return;
	}

	var duration = 300;
	if (close === undefined) {
		close = !this.closed;
	}

	if (close) {				
		$.when(
			this.root.find('.page_title, .search_box, .search_results_area, .selected_results_area').fadeOut(instant ? 0 : duration)
		).then($.proxy(function() {
			this.root.find('.page_navigation').fadeIn(duration);
		}, this));

		// Fix content height while scrolling;
		$('#content_inner').height($('#content_inner').height());
		$.when(
			$('#content_outer').stop().scrollTo(0, {
				duration: instant ? duration : duration * 2
			})
		).then(function() {			
			$('#content_inner').height('auto');
		});

		Core.trigger(this.prefix + 'hide-search', instant);
	} else {
		this.root.find('.page_navigation').hide();
		this.root.find('.page_title, .search_box, .search_results_area, .selected_results_area').fadeIn(duration);

		Core.trigger(this.prefix + 'display-search', instant);
	}

	this.closed = close;
};

Search.prototype._loadRecordXHR = null;
Search.prototype.loadRecordExtraParams = function() {
	return {};
};

Search.prototype.loadRecord = function(record, callback) {
	if (!record || record.id === undefined) {
		return;
	}

	var parameters = $.extend({
		controller: 'json',
		module: this.type,
		action: this.openAction || 'open',
		id: record.id
	}, this.loadRecordExtraParams());
	
	if (this._loadRecordXHR) {
		this._loadRecordXHR.abort();
	}
	
//	this.clearAll();

	this._loadRecordXHR = $.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: parameters,
		context: this,
		loadingTimedOverlay: true
	}).done(function(response) {
		if (response.success) {
			if ($.isFunction(callback)) {
				callback(response);
			}
		} else {
			Core.msg(response);
		}
	});
};

Search.prototype.tabHandler = function(tab, record) {
	this.selectedTab = tab;
	console.log('Implementation needed');
};

Search.prototype.clearResults = function() {
	console.log('Implementation needed');
};

Search.prototype.displayPagingButtons = function(id) {
	var recordFound = (this.lastSearchResult && this.lastSearchResult[id]);

	this.root.find('.page_navigation :not(.back_to_search)')[recordFound ? 'show' : 'hide']();
};

/*
Search.prototype.processResult = function(response, record, tab) {
	var expectedSerial = record.id;
	if (!this.selectedRecord || this.selectedRecord.id != expectedSerial) {
		return;
	}

	this.selectedRecordData = response.data;
	this.clearAll();
};
*/

Search.prototype.clearAll = function() {
	console.log('Implementation needed');
};

Search.prototype.selectedList = [];

Search.prototype.selectPageResults = function() {
	var me = this;
	this.root.find('.search_results .result').each(function() {
		me.selectRecord($(this).attr('rel'), true);
	});
	this.updateSelectList();
};

Search.prototype.selectRecord = function(id, dontUpdate) {
	var result = this.root.find('.search_results .result[rel="' + id + '"]');
	
	var record = this.getSearchResult(id);
	if (record) {
		var found = false;
		for (var i = 0; i < this.selectedList.length; i++) {
			if (this.selectedList[i].id == id) {
				found = true;
				break;
			}
		}
		
		if (!found) { 			
			this.selectedList.push(record);
			
			if (!dontUpdate) {
				this.updateSelectList();
			}
		}
	}
	
	var button = result.find('a[rel=select_item]');
	if (!button.hasClass('disabled')) {
		var buttons = result.find('.buttons');
		var width = buttons.width();			
		buttons.width(width);
		
		var oldText = button.html();
		button.addClass('disabled').html(_('common.added_to_list'));
		
		setTimeout(function() {
			button.removeClass('disabled').html(oldText);
			buttons.width('auto');
		}, 2000);
		buttons.width(width);
	}
};

Search.prototype.unselectRecord = function(id) {
	for (var i = 0; i < this.selectedList.length; i++) {
		if (this.selectedList[i].id == id) {
			this.selectedList.splice(i, 1);
			break;
		}
	}
	
	this.updateSelectList();
};

Search.prototype.updateSelectList = function() {
	var scroll = this.root.find('.selected_results_area ul').scrollTop();
	this.root.find('.selected_results_area').processTemplate(this.selectedList);
	this.root.find('.selected_results_area ul').scrollTop(scroll);
	this.root.find('.selected_results_area select').combo();
};