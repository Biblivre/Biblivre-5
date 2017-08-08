<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.request.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.request.input.js"></script>
	<script type="text/javascript">
		var RequestSearch = CreateSearch(RequestSearchClass, {
			type: 'acquisition.request',
			root: '#requests',
			enableTabs: true,
			enableHistory: false
		});	
	
		RequestInput.type = 'acquisition.request';
		RequestInput.root = '#requests';
		RequestInput.search = RequestSearch;
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="acquisition.request.page_help" /></div>
	
	<div id="requests">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.acquisition_request" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="RequestInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="RequestSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="RequestSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="RequestSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="RequestInput.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="RequestInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="RequestInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="RequestInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="RequestInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="RequestInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="RequestInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>
			<div class="record">
				{#if $T.author}<label><i18n:text key="acquisition.request.field.author" /></label>: {$T.author}<br/>{#/if}
				{#if $T.title}<label><i18n:text key="acquisition.request.field.title" /></label>: {$T.title}<br/>{#/if}
				{#if $T.requester}<label><i18n:text key="acquisition.request.field.requester" /></label>: {$T.requester}<br/>{#/if}
				{#if $T.id}<label><i18n:text key="acquisition.request.field.id" /></label>: {$T.id}<br/>{#/if}
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form"><i18n:text key="common.form" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_request_form_body"></div>
					<textarea id="biblivre_request_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.request.field.requester" /></div>
							<div class="value"><input type="text" name="requester" value="{$T.requester}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.request.field.quantity" /></div>
							<div class="value"><input type="text" name="quantity" value="{$T.quantity}"></div>
							<div class="clear"></div>	
						</div>
					--></textarea>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_request_form"></div>
					<textarea id="biblivre_request_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.request.fieldset.title_info" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.request.field.author" /></div>
									<div class="value"><input type="text" name="author" value="{$T.author}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.request.field.title" /></div>
									<div class="value"><input type="text" name="title" value="{$T.title}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.request.field.subtitle" /></div>
									<div class="value"><input type="text" name="subtitle" value="{$T.subtitle}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.request.field.edition" /></div>
									<div class="value"><input type="text" name="edition" value="{$T.editionNumber}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.request.field.publisher" /></div>
									<div class="value"><input type="text" name="publisher" value="{$T.publisher}"></div>
									<div class="clear"></div>
								</div>
							</div>
						</fieldset>
					--></textarea>
				</div>		
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_request_info_form"></div>
					<textarea id="biblivre_request_info_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.request.field.info" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.request.field.info" /></div>
									<div class="value"><input type="text" name="info" value="{$T.info}"></div>
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
				<div class="wide_query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="RequestSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
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
						<div class="result {#cycle values=['odd', 'even']} {RequestInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{RequestInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="RequestSearch.openResult('{$T.record.id}');"><i18n:text key="common.open" /></a>
								<a class="danger_button center delete" onclick="RequestInput.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.author}<label><i18n:text key="acquisition.request.field.author" /></label>: {$T.record.author}<br/>{#/if}
								{#if $T.record.title}<label><i18n:text key="acquisition.request.field.title" /></label>: {$T.record.title}<br/>{#/if}
								{#if $T.record.requester}<label><i18n:text key="acquisition.request.field.requester" /></label>: {$T.record.requester}<br/>{#/if}
								{#if $T.record.id}<label><i18n:text key="acquisition.request.field.id" /></label>: {$T.record.id}<br/>{#/if}
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

