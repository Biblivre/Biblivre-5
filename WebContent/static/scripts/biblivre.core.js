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

/**
 * Translations
 * Translations will work with strings loaded from the i18n javascript file.
 * 
 * Available functions:
 *  Translations.get(key, params)
 *  Translations.date(key, date)
 *  _(key, params) => Global helper for Translations.get()
 */
var Translations = {
	translations: {},
	get: function(key, params) {
		var string = this.translations[key] || "__" + key + "__";
		var args = params || {};

		return string.replace(/\{(.+?)\}/g, function(m, i) {
			return args[m.substring(1, m.length - 1)];
		});
	}
};

var _ = function(key, params) {
	return Translations.get(key, params);
};

var _f = function(elem, param) {
	return Globalize.format(elem, param || 'n0');
};

var _d = function(date, param) {
	var myDate = new Date(date);
	
	if (isNaN(myDate)) {
		myDate = newDateFix(date);
	};
	
	var params = (param || 'd').split(' ');
	
	var arr = [];
	for (var i = 0; i < params.length; i++) {
		arr.push(Globalize.format(myDate, params[i]));
	}

	return arr.join(' ');
};

var _p = function(key, number) {
	if (number == 1) {
		return Translations.get(key + '_singular', [number]);
	} else {
		return Translations.get(key + '_plural', [number]);
	}
};


var newDateFix = function(s){
    var day, tz,
    rx=/^(\d{4}\-\d\d\-\d\d([tT ][\d:\.]*)?)([zZ]|([+\-])(\d\d):(\d\d))?$/,
    p= rx.exec(s) || [];
    if(p[1]){
        day= p[1].split(/\D/);
        for(var i= 0, L= day.length; i<L; i++){
            day[i]= parseInt(day[i], 10) || 0;
        };
        day[1]-= 1;
        day= new Date(Date.UTC.apply(Date, day));
        if(!day.getDate()) return NaN;
        if(p[5]){
            tz= (parseInt(p[5], 10)*60);
            if(p[6]) tz+= parseInt(p[6], 10);
            if(p[4]== '+') tz*= -1;
            if(tz) day.setUTCMinutes(day.getUTCMinutes()+ tz);
        }
        return day;
    }
    return NaN;
};

/**
 * Core
 * Utility functions
 * 
 * Available functions:
 * 	Core.subscribe(event, callback)
 * 	Core.trigger(event, data)
 */

var Core = {};

Core.get = function(root, path) {
	if (!root) {
		return null;
	}

	var p = path.split('.');
	var r = root;

	for (var i = 0; i < p.length; i++) {
		r = r[p[i]];
		
		if (!r) {
			return null;
		}
	}

	return r;
};

Core.subscribe = function(event, callback, scope) {
	$(document).on(event, $.proxy(callback, scope || window));
};

Core.trigger = function(event) {
	return $(document).triggerHandler(event, [].splice.call(arguments, 1, arguments.length));
};

Core.currentHistoryTrigger = {};

Core.historyTrigger = function(obj) {
	var changed = false;

	for (var param in obj) {
		if (!obj.hasOwnProperty(param)) {
			continue;
		}
		
		var value = obj[param];
		if (Core.currentHistoryTrigger[param] !== value) {
			Core.currentHistoryTrigger[param] = value;
			changed = true;
		}
	}

	if (changed) {
		$.History.go($.param(Core.currentHistoryTrigger));
	}
};

Core.historyCheckAndSet = function(trigger, param) {
	var value = Core.hs(trigger, param);
	var changed = false;
	
	if (Core.currentHistoryTrigger[param] !== value) {
		Core.currentHistoryTrigger[param] = value;
		changed = true;
	}

	return {
		value: value,
		changed: changed
	};
};

Core.submitForm = function(module, action, controller) {
	var form = document.getElementById('page_submit');

	$('#module').val(module);
	$('#action').val(action);
	if (controller) {
		$('#controller').val(controller);
	}

	
	form.submit();
};

Core.convertDateFormat = function(format) {
	var dateReplaces = [
    	['yyyy', 'Y'],
    	['yy', 'y'],
    	['MMMM', 'F'],
    	['MMM', 'M'],
    	['MM', 'm'],
    	['M', 'n'],
    	['dddd', 'l'],
    	['ddd', 'D'],
    	['dd', 'd'],
    	['d', 'j']
    ];


    for (var i = 0; i < dateReplaces.length; i++) {
    	var r = dateReplaces[i];
    	
    	format = format.replace(new RegExp('(^|[^\*])' + r[0] + '($|[^\*])', 'g'), '$1&*' + r[1] + '*&$2');
    }

    return format.replace(/(\*|&)/g, '');
};

Core.submitFormOnEnter = function(e) {
	if (e.keyCode != 10 && e.keyCode != 13) {
		return;
	}

	e.preventDefault();
	e.stopPropagation();

	$(this).find('.main_button:not(.disabled)').trigger('click');
};

Core.getCachedElement = function(sel) {
	if (!Core._cache) {
		Core._cache = {};
	}

	var el = Core._cache[sel];
	
	if (!el) {
		Core._cache[sel] = el = $(sel);
	}
	
	return el;
};

Core.ignoreUpdate = function(el) {
	$(el).closest('.message').remove();
	
	$.ajax({
		type: 'POST',
		url: window.location.pathname,
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'administration.configurations',
			action: 'ignore_update'
		}
	});
};

Core.fixResize = function() {
	var html = Core.getCachedElement('html');
	
	// Fixing long titles
	html.removeClass('double_line_header');

	var name = Core.getCachedElement('#title h1');
	var lineHeight = parseInt(name.css('line-height'), 10);

	if (name.height() > lineHeight * 1.5) {
		html.addClass('double_line_header');
	}
	
	// Adjusting small resolutions
	var biblivreLogo = Core.getCachedElement('#logo_biblivre');
	var biblivreLogoWidth = biblivreLogo.outerWidth(true);

	var sponsorLogo = Core.getCachedElement('#logo_sponsor');
	var sponsorLogoWidth = sponsorLogo.outerWidth(true);

	var supportLogo = Core.getCachedElement('#logo_support');
	var supportLogoWidth = supportLogo.outerWidth(true);

	var title = Core.getCachedElement('#title');
	var contentWidth = title.outerWidth();
	var windowWidth = $(window).width();

	// Case 1: Window is so small that only content fits it. Remove all logos.
	if (windowWidth < contentWidth + biblivreLogoWidth) {
		if (!html.hasClass('left_side_compact')) {
			html
				.addClass('left_side_compact')
				.removeClass('left_side_fixed');
		}
	
	// Case 2: Window is small and biblivre logo would be outside of it. Stop centering content.
	} else if (Math.ceil((windowWidth - contentWidth) / 2) <= biblivreLogoWidth) {
		if (!html.hasClass('left_side_fixed')) {
			html
				.addClass('left_side_fixed')
				.removeClass('left_side_compact');
		}
	
	// Case 3: No problem with the left side of the screen
	} else {
		html
			.removeClass('left_side_fixed')
			.removeClass('left_side_compact');
	}
	
	var right = windowWidth - (contentWidth + title.offset().left);

	// Right side Case 1: 
	if (right <= sponsorLogoWidth) {
		if (!html.hasClass('right_side_compact')) {
			html
				.addClass('right_side_compact')
				.removeClass('right_side_small');
		}

	// Right side Case 2:
	} else if (right < sponsorLogoWidth + supportLogoWidth) {
		if (!html.hasClass('right_side_small')) {
			html
				.addClass('right_side_small')
				.removeClass('right_side_compact');
		}

	// Right side Case 3:			
	} else {
		html
		.removeClass('right_side_small')
		.removeClass('right_side_compact');	
	}

	var header = Core.getCachedElement('#header');
	var notifications = Core.getCachedElement('#notifications');
	
	var newHeight = $(window).height() - header.outerHeight() - notifications.outerHeight();

	var contentOuter = Core.getCachedElement('#content_outer');
	contentOuter.height(newHeight);
	
	var content = Core.getCachedElement('#content');
	var copyright = Core.getCachedElement('#copyright');
	
	var innerHeight = newHeight - copyright.outerHeight();
	content.css('min-height', innerHeight);
	
	// Fixing ScrollBars Offset
	var scrollBarWidth = Core.getScrollbarWidth();
	contentOuter.css({
		width: '100%',
		marginLeft: 0
	});

	if (!html.hasClass('left_side_fixed')) {
		contentOuter.css({
			width: contentOuter.width() - scrollBarWidth,
			marginLeft: scrollBarWidth
		});
	}
};

Core.msg = function(o, level) {
	var defaults = {
		message_level: 'normal',
		animate: true,
		sticky: false,
		translate: false
	};

	var config = defaults;
	
	if (typeof o == 'object') {
		config = $.extend({}, defaults, o);
	} else {
		config.message = o;
		config.message_level = level || 'normal';
	}

	if (!config.message) {
		if (config.message_level == 'normal') {
			Core.removeMsg();
			return;
		}

		config.message = '';
	}

	if (config.translate) {
		config.message = _(config.message);
	}

	$('#messages .message:not(.sticky)').remove(); 
	
	var message = $('<div class="message"></div>');

	message
		.addClass(config.message_level)
		.append($('<div></div>').text(config.message))
		.prependTo('#messages')
		.click(function() {
			message.fadeOut(config.animate ? 'normal' : 0, function() {
				Core.removeMsg(message);
				message = null;
			});
		})
		.hide()
		.stop()
		.fadeIn(config.animate ? 'normal' : 0);

	if (config.sticky) {
		message.addClass('sticky');
	}

	Core.fixResize();
};

Core.formErrors = function(errors, root) {
	if (!errors) {
		return;
	}

	root = root || $(document);
	var focus = false;

	for (var i = 0; i < errors.length; i++) {
		var error = errors[i];

		for (var key in error) {
			if (!error.hasOwnProperty(key)) {
				continue;
			}
	
			var text = error[key];

			var input = $('input[name="' + key + '"]');
			if (input.length == 0) {
				input = $('select[name="' + key + '"]');
			}
			var field = input.parent();
			var extra = field.siblings('.extra');

			if (extra.size() && extra.children('.form_error_ico').size() === 0) {
				$('<div class="form_error_ico ico_exclamation"></div>').appendTo(extra);
			}

			var form_error = field.children('.form_error');
			
			if (form_error.size()) {
				form_error.text(text);
			} else {
				$('<div class="form_error"></div>').text(text).appendTo(field);
			}
			
			if (!focus) {
				focus = true;
				input.focus();
			}
		}
	}
};

Core.clearFormErrors = function(root) {
	root = root || $(document);
	
	root.find('.form_error, .form_error_ico').remove();
};

Core.removeMsg = function(message) {
	if (message) {
		message.remove();
	} else {
		$('#messages .message:not(.sticky)').remove();		
	}

	Core.fixResize();
};

Core.popup = function(settings) {
	var defaults = {
		closeable: true,
		title: '',
		closeText: _('common.close'),
		cancelText: _('common.cancel'),
		okText: _('common.ok')
	};
	
	var options = $.extend({}, defaults, settings);
	
	var div = $('<div class="popup"></div>');
	
	function submit(e) {
		if (e.keyCode == 27 && options.closeable) {
			close();

			e.preventDefault();
			e.stopPropagation();
			return;
		}
			

		if (e.keyCode != 10 && e.keyCode != 13) {
			return;
		}

		e.preventDefault();
		e.stopPropagation();

		div.find('.main_button:not(.disabled)').trigger('click');
	}

	var lastFocus = $(':focus').blur();
	
	var close = function() {
		Core.hideOverlay();
		div.remove();

		$('body').off('keydown', submit);
		lastFocus.focus();
	};

	if (options.closeable) {
		$('<div class="close"></div>')
			.text(options.closeText)
			.appendTo(div)
			.click(function() {
				close();
				
				if ($.isFunction(options.cancelHandler)) {
					options.cancelHandler();
				}
			});
	}

	var fieldset = $('<fieldset></fieldset>').appendTo(div);
	
	if (!options.closeable) {
		fieldset.css('margin-top', 20);
	}
	
	if (options.title) {
		$('<legend></legend>').html(options.title).appendTo(fieldset);
	}

	if (options.description) {
		$('<div class="description"></div>').html('<p>' + options.description + '</p>').appendTo(fieldset);
	}
	
	if (options.body) {
		$('<div class="description"></div>').append(options.body).appendTo(fieldset);
	}
	
	if (options.confirm) {
		$('<div class="confirm"></div>').html('<p>' + options.confirm + '</p>').appendTo(fieldset);
	}

	var buttons = $('<div class="buttons"></div>').appendTo(fieldset);
	
	if (options.cancelHandler) {
		$('<a class="button"></a>')
			.text(options.cancelText)
			.appendTo(buttons)
			.click(function() {
				close();
				
				if ($.isFunction(options.cancelHandler)) {
					options.cancelHandler();
				}
			});
	}
		
	if (options.okHandler) {
		$('<a class="button main_button"></a>')
			.text(options.okText)
			.appendTo(buttons)
			.click(function() {
				if ($.isFunction(options.okHandler)) {
					var res = options.okHandler();
					if (res !== false) {
						close();
					}
				} else {
					close();
				}
			});
	}
		
	Core.showOverlay();
	div.appendTo('body').show().center();

	$('body').on('keydown', submit);
	
	return div;
};

Core.fadeInOverlay = function(speed) {
	var div = $('<div class="overlay"></div>');
	
	div.appendTo('body').css({
		opacity: 0
	}).show().animate({ 
		opacity: 0.7
	}, speed);
};

Core.showOverlay = function() {
	$('<div class="overlay"></div>').appendTo('body').show();
};

Core.hideOverlay = function() {
	$('.overlay').remove();
};

Core.showLoadingTimedOverlay = function() {
	var el = $('body');
	var count = el.data('loading_overlay_count') || 0;
	count++;

	el.data('loading_overlay_count', count);

	if (count == 1) {
		var div = $('<div class="loading_overlay"></div>').appendTo(el);
		setTimeout(function() {
			div.animate({ opacity: 0.7 }, 'fast');
		}, 1000);
	}
};

Core.hideLoadingTimedOverlay = function() {
	var el = $('body');
	var count = el.data('loading_overlay_count');
	count--;

	el.data('loading_overlay_count', count);
	if (count == 0) {
		el.children('.loading_overlay').remove();
	}			
};

Core.changeTab = function(tab, scope, params) {
	if (typeof tab == 'string') {
		tab = 'li.tab[data-tab=' + tab + ']';
	}

	var element = $(tab);
	var name = element.data('tab');

	if (scope && $.isFunction(scope.tabHandler)) {
		if (scope.tabHandler(name, params || {}) === false) {
			return false;
		}
	}

	element.closest('.tabs').find('.tab, .tab_body, .tab_extra_content').removeClass('tab_selected').filter('[data-tab~="' + name + '"]').addClass('tab_selected');

	Core.trigger((scope.prefix || '') + 'tab-changed', name);
	
	return true;
};

Core.toggleAreas = function(area, val, root) {
	root = root || $(document);
	var divs = root.find('div.' + area).hide();

	if (!val) {
		return;
	}

	divs.each(function() {
		var div = $(this);
		var dataArr = (div.attr('data') || '').split(',');

		if ($.inArray(val, dataArr) != -1) {
			div.show();
		}
	});
};

Core.pagingGenerator = function(o) {
	var config = $.extend({}, {
		pageCount: 0,
		currentPage: 1,
		recordsPerPage: 1,
		linkFunction: function() {},
		pagingHolder: null
	}, o);

	if (config.pageCount == 1 && config.pagingHolder) {
		config.pagingHolder.empty();
		return;
	}
	
	if (config.pageCount == 1 || !config.pagingHolder || !config.pagingHolder.size()) {
		return;
	}

	config.pagingHolder.empty();
	config.pagingHolder.off('click', 'a[rel]');
	config.currentPage = parseInt(config.currentPage, 10);
	config.recordsPerPage = parseInt(config.recordsPerPage, 10);

	if (config.currentPage > 1) {
		config.pagingHolder.append('<a href="javascript:void(0);" class="button paging_button paging_arrow_prev" rel="' + (config.currentPage - 1) + '"></a>');
	}

	var ellipsis = false;
	for (var i = 1; i <= config.pageCount; i++) {
		if ((i <= 3) || (i > config.pageCount - 3) || ((i > config.currentPage - 2) && (i < config.currentPage + 2))) {
			ellipsis = false;

			if (i == config.currentPage) {
				config.pagingHolder.append('<span class="paging actual_page">' + i + '</span>');
			} else {
				config.pagingHolder.append('<a href="javascript:void(0);" class="paging" rel="' + i + '">' + i + '</a>');
			}
		} else if (!ellipsis) {
			ellipsis = true;
			config.pagingHolder.append('<span class="paging">...</span>');
		}
	}

	if (config.currentPage < config.pageCount) {
		config.pagingHolder.append('<a href="javascript:void(0);" class="button paging_button paging_arrow_next" rel="' + (config.currentPage + 1) + '"></a>');		
	}

	config.pagingHolder.on('click', 'a[rel]', config.linkFunction);
};

Core.qs = function(key) {
	var qs = window.location.href.split('?')[1] || '';
	qs = qs.split('#')[0];
	
	return Core.hs(qs, key);
};

Core.qhs = function(key) {
	var qs = window.location.href.split('?')[1] || '';
	qs = qs.split('#')[1];
	
	return Core.hs(qs, key);
};

Core.hs = function(qs, key) {
	key = (key) ? key.toLowerCase() : '';

	if (key && qs) {
		var pairs = qs.split('&');
		for (var i = 0; i < pairs.length; i++) {
			var pair = pairs[i].split('=');
			if (pair[0] && pair[0].toLowerCase() == key) {
				return decodeURIComponent(pair[1].replace(/\+/g, ' '));
			}
		}
	}

	return;
};

Core.qso = function() {
	var qs = window.location.href.split('?')[1] || '';
	qs = qs.split('#')[0];

	var obj = {};
	
	var pairs = qs.split('&');
	for (var i = 0; i < pairs.length; i++) {
		var pair = pairs[i].split('=');
		if (pair[0]) {
			obj[pair[0]] = pair[1];
		}
	}

	return obj;
};


Core.scrollbarWidth;
Core.getScrollbarWidth = function() {
	if (Core.scrollbarWidth === undefined) {
		if ($.browser.msie) {
			var textareaA = $('<textarea cols="10" rows="2"></textarea>').css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body');
			var textareaB = $('<textarea cols="10" rows="2" style="overflow: hidden;"></textarea>').css({ position: 'absolute', top: -1000, left: -1000 }).appendTo('body');

			Core.scrollbarWidth = textareaA.width() - textareaB.width();
			textareaA.remove();
			textareaB.remove();
		} else {
			var divA = $('<div></div>').css({ width: 100, height: 100, overflow: 'auto', position: 'absolute', top: -1000, left: -1000 }).prependTo('body');
			var divB = $('<div></div>').css({ width: '100%', height: 200 }).appendTo(divA);

			Core.scrollbarWidth = 100 - divB.width();
			divA.remove();
		}
	}

	return Core.scrollbarWidth;
};

// Keep session from expiring
Core.keepSession = function() {
	$.ajax({
		type: 'POST',
		url: window.location.pathname,
		dataType: 'json',
		data: {
			controller: 'json',
			module: 'menu',
			action: 'ping'
		},
		complete: function() {
			setTimeout(Core.keepSession, 300000);
		}
	});
};
setTimeout(Core.keepSession, 30000);

Core.fixNavigationHeight = function() {
	var title = $('.page_title');
	var navigation = $('.page_navigation');

	var navHeight = navigation.getHiddenDimensions().outerHeight;
	var titleHeight = title.getHiddenDimensions().outerHeight;

	if (titleHeight > navHeight) {
		var padding = (titleHeight - navHeight) / 2;
		navigation.css({
			paddingTop: padding,
			paddingBottom: padding
		});
	}
};

/**
 * Menu
 */
var Menu = {};
Menu.hide = function() {
	Core.getCachedElement('#menu .submenu, #slider').fadeOut('fast');
};

Menu.initialize = function() {
	var action = Core.qs('action');
	var slider = Core.getCachedElement('#slider');
	var menu = Core.getCachedElement('#menu');
	
	$('#menu > ul > li').mouseenter(function(e, hide) {
		var item = $(this);
		var module = item.data('module');

		if (!module) {
			menu.trigger('mouseleave');
			return;
		}

		Core.getCachedElement('#menu .submenu').hide();

		slider.removeClass().addClass('slider_' + module);

		if (!hide) {
			item.children('.submenu').show();
		}

		var width = item.outerWidth(true);
		var left = item.position().left;

		slider.stop().show().css({
			opacity: 1,
			width: width,
			left: left
		});
	});
	
	var subitem = $('#menu li[data-action="' + action + '"]:first');
	var module = subitem.parents('li').data('module');	
	Menu.module = module || 'start';

	menu.mouseleave(function() {
		if (action == '' || !subitem.size()) {
			Menu.hide();
		} else if (subitem.size()) {
			subitem.parents('li').trigger('mouseenter', true);
		}
	}).trigger('mouseleave').unselectable();

	if ($.browser.msie) {
		menu.each(function() {
			var el = $(this);
			el.width(el.outerWidth());
		});
	}
	
	// Let's populate our dynamic breadcrumb
	var breadcrumb = '<span>Biblivre</span>';

	if (module) {
		breadcrumb = '<span>' + (subitem.parents('li').html() || '').replace(/\r?\n|<.*$|^\s*/gm, '') + '</span> &gt; ' + subitem.text();
	}

	Core.getCachedElement('#breadcrumb').prepend(breadcrumb);

	if (Menu.module) {
		Core.getCachedElement('#notifications').addClass('notifications_' + Menu.module);
	}
};

var PageHelp = {};
PageHelp.initialize = function() {
	$('<a class="xclose">&times;</a>').prependTo('.page_help').click(function() {
		PageHelp.close();
	});
	
	$('.page_help').addClass('page_help_' + Menu.module).after('<div class="clear"></div>');
	
	var help = $.jStorage.get(Core.qs('action') + '_help');

	if (help === false) {
		PageHelp.close(true);
	}
};

PageHelp.close = function(instant) {
	var pageHelp = $('.page_help');

	pageHelp.addClass('page_help_closed').animate({
		width: 0,
		height: 0,
		marginTop: -20
	}, instant ? 0 : 'normal', function() {
		pageHelp.hide();
		$('#page_help_icon').show();
	});
	
	if (!instant) {
		$.jStorage.set(Core.qs('action') + '_help', false);
	}
};

PageHelp.show = function() {
	var pageHelp = $('.page_help').css({
		width: 'auto',
		height: 'auto'
	}).show().removeClass('page_help_closed');

	$('#page_help_icon').hide();
	
	var w = pageHelp.width();
	var h = pageHelp.height();

	pageHelp
	.addClass('page_help_closed')
	.css({
		width: 0,
		height: 0
	}).animate({
		width: w,
		height: h,
		marginTop: 0
	}, function() {
		pageHelp.removeClass('page_help_closed');
	});
	
	$.jStorage.set(Core.qs('action') + '_help', true);
};

var Header = {};

Header.animateLogos = function() {
	$('#logo_support div:last').fadeOut(2000, function() {
		var logo = $(this);
		logo.parent().prepend(this);
		logo.show();
	});
};

Header.setTime = function() {
	$('#clock').text(_d(new Date(), 't'));
};

(function($) {
	$.fn.anyChange = function(handler) {
		this.bind("change propertychange keyup input paste", handler);
	};
	
	$.fn.bindFirst = function(name, fn) {
		this.bind(name, fn);

		var handlers = this.data('events')[name.split('.')[0]];
		var handler = handlers.pop();
	    handlers.splice(0, 0, handler);
	};

	$.fn.getIsoDate = function() {
		var val = Globalize.parseDate(this.val(), 'd') || '';
		return Globalize.format(val, 'S');
	};
	
	var disabledFunction = function(e) {
		return false;
	};
	
	$.fn.disable = function(o) {
		return this.each(function() {
			var el = $(this);

			if (el.hasClass('disabled')) {
				return;
			}
			el.addClass('disabled');
			
			var onclick = this.onclick;
			if (onclick) {
				el.data('onclick', onclick);

				this.onclick = null;
			};
			var events = el.data('events');
			if (events && events.click) {
				el.data('onclick_events', events.click);
				delete events.click;
			}

			el.bindFirst('click', disabledFunction);
		});
	};

	$.fn.enable = function(o) {
		return this.each(function() {
			var el = $(this);

			if (!el.hasClass('disabled')) {
				return;
			}
			el.removeClass('disabled');

			var onclick = el.data('onclick');
			if (onclick) {
				this.onclick = onclick;
			};

			var events = el.data('onclick_events');
			if (events) {
				el.data('events').click = events;
			}

			el.off('click', disabledFunction);
		});
	};
	
	$.fn.fadeOutToHidden = function() {
		return this.animate({ opacity: 0 }, 600, function() {
			$(this).css('visibility', 'hidden');
		});
	};

	$.fn.fadeInFromHidden = function() {
		return this.css('visibility', 'visible').animate({ opacity: 1 }, 600);
	};

	$.fn.combo = function(o) {
		// TODO: Keyboard access
		return this.each(function() {
			var defaults = {
				inputClass: 'combo_input',
				wrapClass: 'combo_wrap',
				textClass: 'combo_text',
				arrowClass: 'combo_arrow',
				hoverClass: 'hover',
				activeClass: 'active',
				listClass: 'combo_list',
				rowClass: 'combo_row',
				autoSize: false,
				alignRight: false,
				expand: false
			};
		
			var config = $.extend({}, defaults, o);
			
			var combo = $(this).wrap('<div></div>').hide();
			config.autoSize = config.autoSize || combo.hasClass('combo_auto_size');
			config.alignRight = config.alignRight || combo.hasClass('combo_align_right');
			config.expand = config.expand || combo.hasClass('combo_expand');
			config.hideEmptyValue = config.hideEmptyValue || combo.hasClass('combo_hide_empty_value');

			var wrap = combo.parent();
			
			var text = $('<div></div>').addClass(config.textClass).appendTo(wrap);
			var arrow = $('<div></div>').addClass(config.arrowClass).appendTo(wrap);		
			$('<div style="clear: both;"></div>').appendTo(wrap);

			var currentText = combo.find('option[value="' + combo.val() + '"]').text();			
			text.text(currentText);
	
			var list = $('<div></div>').appendTo(wrap);
			
			var id = combo.attr('id');
			combo.removeAttr('id');

			var closeCombo = function(e) {
				e.data.trigger('click');
			};

			wrap
				.attr('id', id)
				.addClass(config.wrapClass)
				.addClass(combo.attr('class'))
				.hover(function() {
					var $wrap = $(this);

					if (!$wrap.hasClass('disabled')) {
						$wrap.addClass(config.hoverClass);
					}
				}, function() {
					var $wrap = $(this);

					$wrap.removeClass(config.hoverClass);
				}).click(function(e) {
					var $wrap = $(this);

					//if ($wrap.hasClass('disabled')) {
					//	return false;
					//} else
					if ($wrap.hasClass(config.activeClass)) {
						$wrap.removeClass(config.activeClass);
						$('body').off('click', closeCombo);
					} else {
						$wrap.addClass(config.activeClass);

						var $list = $wrap.find('.' + config.listClass);
	
						var listWidth = $wrap.outerWidth() - (parseInt($list.css('border-left-width'), 10) + parseInt($list.css('border-right-width'), 10));
						var listTop = $wrap.height() + parseInt($wrap.css('padding-top'), 10);

						$list.width('auto');

						if (config.autoSize && listWidth < $list.width()) {
							$wrap.width($list.width() - (parseInt($wrap.css('padding-left'), 10) || 0 + parseInt($wrap.css('padding-right'), 10) || 0));
						}

						$list.css({
							top: listTop,
							width: Math.max(listWidth, $list.width())
						});

						setTimeout(function() {
							$('body').on('click', $wrap, closeCombo);
						}, 0);
					}
				}).on('reset', function() {
					$(this).find('.' + config.rowClass + ':first').trigger('click', true);
				}).on('setvalue', function(e, value) {
					$(this).find('.' + config.rowClass).each(function() {
						var el = $(this);

						if (el.data('value') == value) {
							el.trigger('click', true);
							return false;
						}
					});
				});

			
			if (config.expand) {
				var extraWidth = wrap.getHiddenDimensions().outerWidth - wrap.getHiddenDimensions().width;
				wrap.width(wrap.parent().getHiddenDimensions().width - extraWidth);
			}
			
			if (config.autoSize) {
				wrap.width(text.getHiddenDimensions().outerWidth + arrow.getHiddenDimensions().outerWidth + 1);
			} else {
				text.width(wrap.getHiddenDimensions().width - arrow.getHiddenDimensions().outerWidth);
			}
			
			combo.children('option').each(function() {
				var option = $(this);
				
				if (config.hideEmptyValue && option.val() == '') {
					return;
				}
				
				$('<div></div>')
					.addClass(config.rowClass)
					.data('value', option.val())
					.data('text', option.text())
					.text(option.text())
					.appendTo(list)
					.hoverClass()
					.click(function(e, suppress) {
						var el = $(this);
						var $wrap = el.parents('.' + config.wrapClass);
						var $text = $wrap.find('.' + config.textClass);
						var $arrow = $wrap.find('.' + config.arrowClass);
						var $combo = $wrap.find('select');
						
						$text.text(el.data('text'));
						$combo.val(el.data('value'));
						$combo.trigger('change');

						$wrap.trigger('mouseout');
					
						if (config.autoSize) {
							$wrap.width($text.outerWidth() + $arrow.width() + 1);
						}
						
						if (suppress) {
							e.stopPropagation();
							e.preventDefault();
						}
					});
			});

			var items = list.children();

			var height = items.getHiddenDimensions(true).outerHeight * items.size();

			list.addClass(config.listClass);

			var css = {
				height: height
			};
			
			if (config.alignRight) {
				css.right = - (parseInt(wrap.css('margin-left'), 10) + parseInt(wrap.css('border-left-width'), 10));
			} else {
				css.left = - (parseInt(wrap.css('margin-left'), 10) + parseInt(wrap.css('border-left-width'), 10));
			}
			
			list.css(css);
		});
	};

	// Add a class when objects are hovered
	$.fn.hoverClass = function(cls) {
		if (!cls) {
			cls = 'hover';
		}

		return this.hover(function() {
			var el = $(this);

			if (!el.hasClass('disabled')) {
				el.addClass(cls);
			}
		}, function() {
			$(this).removeClass(cls);
		});
	};

	// Add a class when objects are clicked
	$.fn.clickClass = function(cls) {
		if (!cls) {
			cls = 'clicked';
		}

		return this.bind('mousedown', function() {
			var el = $(this);

			if (!el.hasClass('disabled')) {
				el.addClass(cls);
			}
		}).bind('mouseup', function() {
			$(this).removeClass(cls);
		});
	};
	
	$.fn.toggleValue = function() {
		var el = $(this);
		el.attr('checked', !el.attr('checked'));
	};

	$.fn.unselectable = function() {
		return this.each(function() {
			this.onselectstart = function() { return false; };
			this.unselectable = 'on';
			this.style.MozUserSelect = 'none';
		});
	};
	
	$.fn.center = function() {
		return this.css({
			'position': 'fixed',
			'left': '50%',
			'top': '50%'
		}).css({
			'margin-left': -this.outerWidth() / 2 + 'px',
			'margin-top': -this.outerHeight() / 2 + 'px'
		});
	};
	
	$.fn.progressbar = function(o) {
		if (!o) {
			this.find('.progress_text').text(_('common.wait'));
			this.find('.progress_bar_inner').width('0%');
			return this;
		}
		
		var progress = 100;
		
		var current = parseInt(o.current, 10) || 0;
		var total = parseInt(o.total, 10) || 0;
		var secondary = parseInt(o.secondary_current, 10) || 0;

		if (total == 0) {
			this.find('.progress_text').text(_('common.calculating'));
			this.find('.progress_bar_inner').stop().width(0);
			return this;
		}

		if (current < total) {
			progress = 100 * current / total; 
		}

		secondary = (secondary) ? '[' + secondary + '] ' : '';
		
		this.find('.progress_text').text(secondary + _f(current) + ' / ' + _f(total) + ' (' + progress.toFixed(1) + '%)');
		this.find('.progress_bar_inner').stop().width(progress + '%');

		return this;
	};
	
	$.fn.continuousProgress = function() {
		this.find('.progress_text').text(_('common.wait'));
		this.find('.progress_bar_inner').width(0).animate({ width: '100%' }, 2000, 'linear', $.proxy($.fn.continuousProgress, this));

		return this;
	};
	
	$.fn.stopContinuousProgress = function() {
		this.find('.progress_bar_inner').stop();
		return this;
	};
	
	$.fn.loadingButton = function(o) {
		return this.disable().addClass('loading');
	};
	
	$.fn.loading = function(o) {
		var config = $.extend({}, o, {
			showText: true,
			showImage: true
		});

		var div = $('<div class="loading_indicator"></div>');
		var text = config.loadingText || _('common.loading');

		if (config.showImage) {
			div.append('<img src="static/images/ajax-loader.gif" width="16" height="16" alt="' + text + '"/> ');
		}

		if (config.showText) {
			div.append(text);
		}
		
		return this.append(div);
	};
	
	$.fn.removeLoadingButton = function() {
		return this.enable().removeClass('loading');
	};

	$.fn.removeLoading = function() {
		this.find('.loading_indicator').remove();
		
		return this;
	};

	
	$.fn.fixButtonsHeight = function() {
		return this.each(function() {
			var result = $(this);
			var record = result.children('.record');
			var buttons = result.children('.buttons');

			var recordHeight = record.height('auto').outerHeight();
			var buttonsHeight = buttons.height('auto').outerHeight();
	
			if (recordHeight < buttonsHeight) {
				record.height(buttonsHeight - parseInt(record.css('padding-top'), 10) - parseInt(record.css('padding-bottom'), 10));
			} else if (buttonsHeight < recordHeight) {
				buttons.height(10000);
				recordHeight = record.height('auto').outerHeight();
				
				buttons.height(recordHeight - parseInt(buttons.css('padding-top'), 10) - parseInt(buttons.css('padding-bottom'), 10));
			}
		});
	};
		
	
	$.ajaxSetup({
		beforeSend: function(xhr, settings) {
			xhr.loadingHolder = settings.loadingHolder;
			xhr.loadingText = settings.loadingText;

			xhr.loadingButton = settings.loadingButton;
			xhr.loadingElement = settings.loadingElement;

			xhr.loadingTimedOverlay = settings.loadingTimedOverlay;

			if (xhr.loadingHolder) {
				$(xhr.loadingHolder).removeLoading().loading({
					loadingText: xhr.loadingText
				});
			}

			if (xhr.loadingButton) {
				$(xhr.loadingButton).loadingButton();
			}
			
			if (xhr.loadingElement) {
				var el = $(xhr.loadingElement);
				var count = el.data('loading_count') || 0;
				count++;

				el.addClass('loading').data('loading_count', count);
			}

			if (xhr.loadingTimedOverlay) {
				Core.showLoadingTimedOverlay();
			}
		},
		complete: function(xhr) {
			if (xhr.loadingHolder) {
				$(xhr.loadingHolder).removeLoading();
			}

			if (xhr.loadingButton) {
				$(xhr.loadingButton).removeLoadingButton();
			}

			if (xhr.loadingElement) {
				var el = $(xhr.loadingElement);
				var count = el.data('loading_count');
				count--;

				el.data('loading_count', count);
				if (count == 0) {
					el.removeClass('loading');
				}
			}

			if (xhr.loadingTimedOverlay) {
				Core.hideLoadingTimedOverlay();
			}
		}
	});
})(jQuery);


$(document).ready(function() {
	$('html').removeClass('noscript');

	$(window).resize(Core.fixResize);

	// Setting default form submit action
	$('#page_submit').attr('action', window.location.pathname);

	// It's good to have all images with their titles the same as their alts
	$('img[alt]').attr('title', function() {
		return $(this).attr('alt');
	});

	// Starting textareas and inputs placeholders (for browsers that doesn't support native @placeholder)
	$('input, textarea').placeholder();

	// Focus the .auto_focus input
	setTimeout(function() {
		$('.auto_focus:input:first').focus();
	}, 50);

	$('.submit_on_enter').keypress(Core.submitFormOnEnter);

	$('select.combo').combo();

	$('#header').unselectable();

	Menu.initialize();
	PageHelp.initialize();

	//Core.setTime();
	Core.fixResize();
	Core.fixNavigationHeight();
	
	setInterval(Header.animateLogos, 6000);

	Header.setTime();
	setInterval(Header.setTime, 1000);
	
	var msg = Core.qs('msg');
	var level = Core.qs('level') || 'normal';
	if (msg) {
		Core.msg({
			message: msg,
			message_level: level
		});
	}
});
