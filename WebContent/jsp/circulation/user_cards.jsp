<%@page import="biblivre.circulation.user.UserFields"%>
<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.administration.usertype.UserTypeBO"%>
<%@page import="biblivre.administration.usertype.UserTypeDTO"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.circulation.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.user_cards.js"></script>		

	<script type="text/javascript" src="static/scripts/<%= UserFields.getFields((String) request.getAttribute("schema")).getCacheFileName() %>"></script>
	
	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">

	<script type="text/javascript">
		var CirculationSearch = CreateSearch(CirculationSearchClass, {
			type: 'circulation.user',
			root: '#circulation_user',
			autoSelect: false,
			enableTabs: false,
			enableHistory: false,
			advancedSearchAsDefault: true,
			userCardSearch: true
		});

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
				direction: false,
				offset: [-19, -7],
				readonly_element: false
			});
		});
	</script>
</layout:head>

<%
	List<UserTypeDTO> userTypes = UserTypeBO.getInstance((String) request.getAttribute("schema")).list();
%>
<c:set var="user_field_prefix" value="<%= Constants.TRANSLATION_USER_FIELD %>" scope="page" />

<layout:body>
	<div id="circulation_user">
		<div class="page_help"><i18n:text key="circulation.user_cards.page_help" /></div>
		
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text contains_subtext">
				<i18n:text key="search.common.simple_search" />
				<div class="subtext"><i18n:text key="search.common.switch_to" /> <a href="javascript:void(0);" onclick="CirculationSearch.switchToAdvancedSearch();"><i18n:text key="search.common.advanced_search" /></a></div>
			</div>
	
			<div class="advanced_search text contains_subtext">
				<i18n:text key="search.common.advanced_search" />
				<div class="subtext"><i18n:text key="search.common.switch_to" /> <a href="javascript:void(0);" onclick="CirculationSearch.switchToSimpleSearch();"><i18n:text key="search.common.simple_search" /></a></div>
			</div>

			<div class="clear"></div>
		</div>

		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>

				<div class="buttons">
					<label class="search_label"><i18n:text key="search.user.field" /></label>
					<select name="field" class="combo">
						<option value=""><i18n:text key="search.user.name_or_id" /></option>
						<c:forEach var="field" items="<%= UserFields.getSearchableFields((String) request.getAttribute(\"schema\")) %>" >
							<option value="${field.key}"><i18n:text key="${user_field_prefix}${field.key}" /></option>
						</c:forEach>
					</select>
					<a class="main_button arrow_right" onclick="CirculationSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>
			
			<div class="advanced_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="filter_search">
					<div class="fleft filter_field">
						<label class="search_label"><i18n:text key="search.user.field" /></label>
						<select name="field" class="combo combo_expand">
							<option value=""><i18n:text key="search.user.name_or_id" /></option>
							<c:forEach var="field" items="<%= UserFields.getSearchableFields((String) request.getAttribute(\"schema\")) %>" >
								<option value="${field.key}"><i18n:text key="${user_field_prefix}${field.key}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.registered_between" /></label>
						<input type="text" name="created_start" class="small_input datepicker" />-<input type="text" name="created_end" class="small_input datepicker" />
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.modified_between" /></label>
						<input type="text" name="modified_start" class="small_input datepicker" />-<input type="text" name="modified_end" class="small_input datepicker" />
					</div>
					<div class="clear"></div>
					<div class="filter_checkbox">
						<input type="checkbox" name="users_who_have_login_access" id="users_who_have_login_access" value="true">
						<label class="search_label" for="users_who_have_login_access"><i18n:text key="circulation.user.users_who_have_login_access" /></label>
					</div>
					<div class="filter_checkbox">
						<input type="checkbox" name="users_without_user_card" id="users_without_user_card" value="true" checked="checked">
						<label class="search_label" for="users_without_user_card"><i18n:text key="circulation.user.users_without_user_card" /></label>
					</div>
				</div>

				<div class="fleft clear_search ico_clear">
					<a href="javascript:void(0);" onclick="CirculationSearch.clearAdvancedSearch();"><i18n:text key="search.common.clear_search" /></a>
				</div>

				<div class="buttons">
					<a class="main_button arrow_right" onclick="CirculationSearch.search('advanced');"><i18n:text key="search.common.button.list_all" /></a>
					<div class="clear"></div>
				</div>
			</div>			
		</div>
		
		<div class="selected_results_area"></div>
		<textarea class="selected_results_area_template template"><!--
			{#if $T.length > 0}
				<fieldset class="block">
					<legend>{_p('circulation.user_cards.selected_records', $T.length)}</legend>
					<ul>
						{#foreach $T as record}
							<li rel="{$T.record.id}">{$T.record.enrollment} - {$T.record.name} <a class="xclose" onclick="CirculationSearch.unselectRecord({$T.record.id});">&times;</a></li>
						{#/for}
					</ul>
					<div class="buttons">
						<a class="main_button center" onclick="CirculationLabels.printLabels();"><i18n:text key="circulation.user_cards.button.print_user_cards" /></a>
					</div>
				</fieldset>
			{/#if}
		--></textarea>
	
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="select_bar">
				<a class="button center" onclick="CirculationSearch.selectPageResults();"><i18n:text key="circulation.user_cards.button.select_page" /></a> 
			</div>
		
			<div class="paging_bar"></div>

			<div class="clear"></div>
			
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.record.id}">
							<div class="buttons user_status_{$T.record.status}">
								<a class="button center" rel="select_item" onclick="CirculationSearch.selectRecord('{$T.record.id}');"><i18n:text key="circulation.user_cards.button.select_item" /></a>
							</div>
							<div class="record">
								{#if $T.record.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.name}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.type_name}<br/>
								<div class="user_status_{$T.record.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.record.status)}</div>
	
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>						
								<label><i18n:text key="common.created" /></label>: {_d($T.record.created, 'd t')} {#if $T.record.modified && $T.record.modified != $T.record.created}<br/><label><i18n:text key="common.modified" /></label>: {_d($T.record.modified, 'd t')}{#/if}
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
			
			<div class="paging_bar"></div>		
		</div>
	</div>
	
	<div id="label_select_popup" class="popup">
		<div class="close" onclick="CirculationLabels.hidePopup();"><i18n:text key="common.close" /></div>

		<fieldset>
			<legend><i18n:text key="circulation.user_cards.popup.title" /></legend>
			<div class="content"><select id="label_format_select"></select></div>
		</fieldset>
		
		<fieldset>
			<legend><i18n:text key="circulation.user_cards.popup.description" /></legend>
			<div class="content">
				<table id="label_table"><tbody></tbody></table>
			</div>
		</fieldset>
	</div>
</layout:body>
