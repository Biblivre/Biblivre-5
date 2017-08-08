<%@page import="biblivre.administration.accesscards.AccessCardStatus"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.accesscards.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.accesscards.input.js"></script>
	<script type="text/javascript">
		var AccessCardsSearch = CreateSearch(AccessCardsSearchClass, {
			type: 'administration.accesscards',
			root: '#access_cards',
			autoSelect: false,
			autoSearch: true,
			enableTabs: false,
			enableHistory: false
		});
	
		AccessCardsInput.type = 'administration.accesscards';
		AccessCardsInput.root = '#access_cards';
		AccessCardsInput.search = AccessCardsSearch;
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="administration.accesscards.page_help" /></div>
	
	<div id="access_cards">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.administration_access_cards" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="AccessCardsInput.newRecord();"><i18n:text key="administration.accesscards.add_cards" /></a>
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
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="danger_button center delete" onclick="AccessCardsInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>			
				<div class="new">
					<a class="main_button center" onclick="AccessCardsInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="AccessCardsInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>			
			<div class="record">
				<label><i18n:text key="administration.accesscards.field.code" /></label>: {$T.code}<br/>
				<label><i18n:text key="administration.accesscards.field.status" /></label>: {#if $T.status}{_('administration.accesscards.status.' + $T.status)}{#/if}<br/>
				{#if $T.created}<label><i18n:text key="common.created" /></label>: {_d($T.created, 'd t')}<br/>{#/if}
				{#if $T.modified}<label><i18n:text key="common.modified" /></label>: {_d($T.modified, 'd t')}<br/>{#/if}
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form"><i18n:text key="cataloging.tabs.form" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_accesscards_form_body"></div>
					<textarea id="biblivre_accesscards_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="administration.accesscards.field.status" /></div>
							<div class="value">
								<select name="status">
									<c:forEach var="status" items="<%= AccessCardStatus.values() %>" >
										<option value="${status.string}" {#if $T.status == '${status.string}'}selected{#/if}><i18n:text key="administration.accesscards.status.${status.string}" /></option>
									</c:forEach>					
								</select>						
							</div>
							<div class="clear"></div>	
						</div>
					--></textarea>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_accesscards_single_form_body"></div>
					<textarea id="biblivre_accesscards_single_form_body_template" class="template"><!-- 
						<fieldset>
							<legend>{_('administration.accesscards.add_one_card')}</legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="administration.accesscards.field.code" /></div>
									<div class="value"><input type="text" name="code" maxlength="10" value="{$T.code}"></div>
									<div class="clear"></div>	
								</div>
							</div>
						</fieldset>
					--></textarea>				
				</div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_accesscards_multiple_form_body"></div>
					<textarea id="biblivre_accesscards_multiple_form_body_template" class="template"><!-- 
						<fieldset>
							<legend>{_('administration.accesscards.add_multiple_cards')}</legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="administration.accesscards.prefix" /></div>
									<div class="value"><input type="text" id="prefix" name="prefix" maxlength="10" value="{$T.prefix}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="administration.accesscards.suffix" /></div>
									<div class="value"><input type="text" id="suffix" name="suffix" maxlength="10" value="{$T.suffix}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="administration.accesscards.start_number" /></div>
									<div class="value"><input type="text" id="start" name="start" maxlength="10" value="{$T.start_number}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="administration.accesscards.end_number" /></div>
									<div class="value"><input type="text" id="end" name="end" maxlength="10" value="{$T.end_number}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="administration.accesscards.preview" /></div>
									<div class="value"><input type="text" id="preview" name="preview" maxlength="10" value="{$T.preview}" disabled="disabled"></div>
									<div class="clear"></div>	
								</div>
							</div>
						</fieldset>
					--></textarea>				
				</div>
			</div>
		</div>
		
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="administration.accesscards.simple_term_title" />" />
				</div>
				<div class="buttons">
					<label class="search_label"><i18n:text key="administration.accesscards.field.status" /></label>
					<select name="status" class="combo">
						<option value="any"><i18n:text key="administration.accesscards.status.any" /></option>
						<c:forEach var="status" items="<%= AccessCardStatus.values() %>" >
							<option value="${status.string}"><i18n:text key="administration.accesscards.status.${status.string}" /></option>
						</c:forEach>
					</select>
					<a class="main_button arrow_right" onclick="AccessCardsSearch.search('simple');"><i18n:text key="search.common.button.filter" /></a>
				</div>
			</div>
		</div>
		
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="paging_bar"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']} {AccessCardsInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{AccessCardsInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								{#if $T.record.status == '<%= AccessCardStatus.AVAILABLE %>' || $T.record.status == '<%= AccessCardStatus.IN_USE %>'}
									<a class="button center" onclick="AccessCardsInput.changeStatus('{$T.record.id}', '{$T.record.status == '<%= AccessCardStatus.AVAILABLE %>' ? '<%= AccessCardStatus.BLOCKED %>' : '<%= AccessCardStatus.IN_USE_AND_BLOCKED %>'}');"><i18n:text key="common.block" /></a>
								{#/if}

								{#if $T.record.status == '<%= AccessCardStatus.BLOCKED %>' || $T.record.status == '<%= AccessCardStatus.IN_USE_AND_BLOCKED %>'}
									<a class="button center" onclick="AccessCardsInput.changeStatus('{$T.record.id}', '{$T.record.status == '<%= AccessCardStatus.BLOCKED %>' ? '<%= AccessCardStatus.AVAILABLE %>' : '<%= AccessCardStatus.IN_USE %>'}');"><i18n:text key="common.unblock" /></a>
								{#/if}
								
								{#if $T.record.status == '<%= AccessCardStatus.CANCELLED %>'}
									<a class="button center" onclick="AccessCardsInput.changeStatus('{$T.record.id}', '<%= AccessCardStatus.AVAILABLE %>', '{$T.record.status}');"><i18n:text key="common.uncancel" /></a>
									<a class="danger_button center delete" onclick="AccessCardsInput.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
								{#else}				
									<a class="danger_button center delete" onclick="AccessCardsInput.changeStatus('{$T.record.id}', '<%= AccessCardStatus.CANCELLED %>');"><i18n:text key="common.cancel" /></a>
								{#/if}
							</div>
							<div class="record">
								{#if $T.record.code}<label><i18n:text key="administration.accesscards.field.code" /></label>: {$T.record.code}<br/>{#/if}
								{#if $T.record.status}<label><i18n:text key="administration.accesscards.field.status" /></label>: {_('administration.accesscards.status.' + $T.record.status)}<br/>{#/if}
								{#if $T.record.created}<label><i18n:text key="common.created" /></label>: {_d($T.record.created, 'd t')}<br/>{#/if}
								{#if $T.record.modified}<label><i18n:text key="common.modified" /></label>: {_d($T.record.modified, 'd t')}<br/>{#/if}
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

