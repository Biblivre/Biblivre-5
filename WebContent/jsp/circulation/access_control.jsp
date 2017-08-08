<%@page import="biblivre.administration.accesscards.AccessCardStatus"%>
<%@page import="biblivre.circulation.user.UserStatus"%>
<%@page import="java.util.List"%>
<%@page import="biblivre.administration.usertype.UserTypeBO"%>
<%@page import="biblivre.administration.usertype.UserTypeDTO"%>
<%@ page import="biblivre.core.utils.Constants" %>
<%@ page import="biblivre.circulation.user.UserFields" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.accesscards.search.js"></script>
	
	<script type="text/javascript" src="static/scripts/biblivre.circulation.access_control.js"></script>	
	
	<script type="text/javascript">
		var CirculationSearch = CreateSearch(CirculationSearchClass, {
			type: 'circulation.accesscontrol',
			prefix: 'accesscontrol.user',
			root: '#circulation_search',
			searchAction: 'user_search',
			paginateAction: 'user_search',
			autoSelect: true,
			enableTabs: false,
			enableHistory: false
		});

		var AccessCardsSearch = CreateSearch(AccessCardsSearchClass, {
			type: 'circulation.accesscontrol',
			prefix: 'accesscontrol.card',
			root: '#access_cards',
			searchAction: 'card_search',
			paginateAction: 'card_search',
			enableTabs: false,
			enableHistory: false
		});

		Core.subscribe(CirculationSearch.prefix + 'record-selected', function(e) {
			AccessCardsSearch.root.find('input[name=query]').focus();
		});
	</script>
</layout:head>

<%
	List<UserTypeDTO> userTypes = UserTypeBO.getInstance((String) request.getAttribute("schema")).list();
%>

<layout:body>
	<div class="page_help"><i18n:text key="circulation.access_control.page_help" /></div>

	<c:set var="user_field_prefix" value="<%= Constants.TRANSLATION_USER_FIELD %>" scope="page" />

	<div id="circulation_search">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="text">
				<i18n:text key="circulation.access.user.search" />
			</div>

			<div class="clear"></div>
		</div>
		
		
		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="CirculationSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="CirculationSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="CirculationSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="record">
				{#if $T.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.user.name}<br/>{#/if}
				<label><i18n:text key="circulation.user_field.id" /></label>: {$T.user.enrollment}<br/>
				<label><i18n:text key="circulation.user_field.type" /></label>: {$T.user.type_name}<br/>
				<div class="user_status_{$T.user.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.user.status)}</div>
				{#if $T.accessCard}<label><i18n:text key="administration.accesscards.field.code" /></label>: {$T.accessCard.code}<br/>{#/if}
			</div>
		--></textarea>
	
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
					<a class="main_button arrow_right" onclick="CirculationSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
				</div>
			</div>
			<div class="clear_simple_search ico_clear mt10">
				<a href="javascript:void(0);" onclick="CirculationSearch.clearSimpleSearch();"><i18n:text key="search.common.clear_simple_search" /></a>
			</div>
		</div>
		
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="paging_bar"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.record.user.id}">
							<div class="buttons user_status_{$T.record.user.status}">
								<a class="button center" rel="open_item" onclick="CirculationSearch.openResult('{$T.record.user.id}');"><i18n:text key="search.user.select_item_button" /></a>
							</div>
							<div class="record">
								{#if $T.record.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.user.name}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.user.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.user.type_name}<br/>
								<div class="user_status_{$T.record.user.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.record.user.status)}</div>
								{#if $T.record.accessCard}<label><i18n:text key="administration.accesscards.field.code" /></label>: {$T.record.accessCard.code}<br/>{#/if}				
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
			
			<div class="paging_bar"></div>		
		</div>
	</div>
	
	<div id="access_cards">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.administration_access_cards" />
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="AccessCardsSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="AccessCardsSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="AccessCardsSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="wide_query">
					<input type="text" name="query" class="big_input" placeholder="<i18n:text key="administration.accesscards.simple_term_title" />" />
				</div>
				
				<div class="buttons">
					<a class="main_button arrow_right" onclick="AccessCardsSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
				</div>
			</div>
			<div class="clear_simple_search ico_clear mt10">
				<a href="javascript:void(0);" onclick="AccessCardsSearch.clearSimpleSearch();"><i18n:text key="search.common.clear_simple_search" /></a>
			</div>
		</div>
		
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="paging_bar"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']} {AccessControl.getOverlayClass($T.record.accessCard)}" rel="{$T.record.accessCard.id}">
							<div class="result_overlay"><div class="text">{AccessControl.getOverlayText($T.record.accessCard)}</div></div>
							{#if $T.record.accessCard.status == 'available' || $T.record.accessCard.status == 'in_use'}
								<div class="buttons">
									{#if $T.record.accessCard.status == 'available'}<a class="button center" onclick="AccessControl.bindCard('{$T.record.accessCard.id}');"><i18n:text key="circulation.accesscards.bind_card" /></a>{#/if}
									{#if $T.record.accessCard.status == 'in_use'}<a class="button center" onclick="AccessControl.unbindCard('{$T.record.accessCard.id}', '{$T.record.user.id}');"><i18n:text key="circulation.accesscards.unbind_card" /></a>{#/if}
								</div>
							{#/if}

							<div class="record">
								{#if $T.record.accessCard.code}<label><i18n:text key="administration.accesscards.field.code" /></label>: {$T.record.accessCard.code}<br/>{#/if}
								{#if $T.record.accessCard.status}<label><i18n:text key="administration.accesscards.field.status" /></label>: {_('administration.accesscards.status.' + $T.record.accessCard.status)}<br/>{#/if}
								{#if $T.record.user}
									<div class="ncspacer"></div>
									<div class="ncspacer"></div>
									<i18n:text key="circulation.access_control.card_in_use" />
									<div class="ncspacer"></div>
									<div class="ncspacer"></div>
									<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.user.name}<br/>
									<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.user.enrollment}<br/>
									<label><i18n:text key="circulation.access_control.arrival_time" /></label>: {_d($T.record.arrivalTime, 'd t')}<br/>
								{#/if}
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
			
			<div class="paging_bar"></div>		
		</div>
	</div>
</layout:body>
