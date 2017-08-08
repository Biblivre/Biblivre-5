<%@page import="biblivre.circulation.user.UserFields"%>
<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@page import="biblivre.cataloging.Fields"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="biblivre.cataloging.enums.RecordDatabase"%>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.circulation.css" />	
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.cataloging.css" />
	
	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">
	
	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.search.js"></script>	
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.input.js"></script>
	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "biblio").getCacheFileName() %>"></script>
	
	<script type="text/javascript" src="static/scripts/biblivre.circulation.search.js"></script>
	
	<script type="text/javascript" src="static/scripts/biblivre.administration.reports.js"></script>
	
	<script type="text/javascript">
		var CirculationSearch = CreateSearch(CirculationSearchClass, {
			type: 'circulation.user',
			root: '#userDiv',
			enableTabs: false,
			enableHistory: false
		});
	
	</script>
	
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="administration.reports.page_help" /></div>
	
	<fieldset id="biblivre_reports_form" class="block reports">
		<legend><i18n:text key="administration.reports.title" /></legend>
		
		<div id="reportSelection" class="reports center">
			<div class="title"><i18n:text key="administration.reports.select_report" /></div>
			<div class="ncspacer"></div>
			<div class="ncspacer"></div>
			<select name="report" id="reportSelect" onchange="Reports.toggleDivs(this.value)" >
			
				<option value=""><i18n:text key="administration.reports.select.option.default"/></option>
				
				<optgroup label="<i18n:text key="administration.reports.select.group.acquisition"/>">
					<option value="1"><i18n:text key="administration.reports.select.option.acquisition"/></option>
				</optgroup>
				
				<optgroup label="<i18n:text key="administration.reports.select.group.cataloging"/>">
					<option value="2"><i18n:text key="administration.reports.select.option.summary"/></option>
					<option value="3"><i18n:text key="administration.reports.select.option.dewey"/></option>
					<option value="4"><i18n:text key="administration.reports.select.option.records"/></option>
					<option value="5"><i18n:text key="administration.reports.select.option.bibliography"/></option>
					<option value="13"><i18n:text key="administration.reports.select.option.accession_number"/></option>
					<option value="14"><i18n:text key="administration.reports.select.option.accession_number.full"/></option>
					<option value="15"><i18n:text key="administration.reports.select.option.topographic"/></option>
					<option value="16"><i18n:text key="administration.reports.select.option.holdings"/></option>
				</optgroup>
				
				<optgroup label="<i18n:text key="administration.reports.select.group.circulation"/>">
					<option value="6"><i18n:text key="administration.reports.select.option.user"/></option>
					<option value="7"><i18n:text key="administration.reports.select.option.all_users"/></option>
					<option value="8"><i18n:text key="administration.reports.select.option.late_lendings"/></option>
					<option value="12"><i18n:text key="administration.reports.select.option.reservations"/></option>
					<option value="9"><i18n:text key="administration.reports.select.option.searches"/></option>
					<option value="10"><i18n:text key="administration.reports.select.option.lendings"/></option>
				</optgroup>
				
				<optgroup label="<i18n:text key="administration.reports.select.group.custom"/>">
					<option value="17"><i18n:text key="administration.reports.select.option.custom_count"/></option>
				</optgroup>
			</select>
		</div>
	</fieldset>
	
	<fieldset id="dateDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.dates" /></legend>
		
		<div class="biblivre_form_body reports">
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.start_date" /></div>
				<div class="value"><input type="text" name="start" class="datepicker"></div>
				<div class="clear"></div>	
			</div>
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.start_date" /></div>
				<div class="value"><input type="text" name="end" class="datepicker"></div>
				<div class="clear"></div>	
			</div>
		</div>
	</fieldset>
	
	<fieldset id="databaseSelection" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.database" /></legend>
		
		<div class="biblivre_form_body reports">
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.database" /></div>
				<div class="value" id="database_selection_combo">
					<select name="database">
						<option value="<%=RecordDatabase.MAIN%>"><i18n:text key="administration.reports.option.database.main"/></option>
						<option value="<%=RecordDatabase.WORK%>"><i18n:text key="administration.reports.option.database.work"/></option>
					</select>
				</div>
				<div class="clear"></div>	
			</div>
		</div>
	</fieldset>
	
	<fieldset id="orderDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.order" /></legend>
		
		<div class="biblivre_form_body reports">
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.order" /></div>
				<div class="value">
					<select name="order">
						<option value="1"><i18n:text key="administration.reports.option.classification"/></option>
						<option value="2"><i18n:text key="administration.reports.option.title"/></option>
						<option value="3"><i18n:text key="administration.reports.option.author"/></option>
					</select>
				</div>
				<div class="clear"></div>	
			</div>
		</div>
	</fieldset>
	
	<fieldset id="deweyDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.dewey" /></legend>
		
		<div class="biblivre_form_body reports">
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.datafield" /></div>
				<div class="value">
					<select name="datafield">
						<option value="082">082 |a (<i18n:text key="administration.reports.option.dewey" />)</option>
						<option value="090">090 |a (<i18n:text key="administration.reports.option.location"/>)</option>
					</select>
				</div>
				<div class="clear"></div>	
			</div>
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.digits" /></div>
				<div class="value">
					<select name="digits">
						<option value="-1"><i18n:text key="administration.reports.option.all_digits"/></option>
						<option value="1">1 (<i18n:text key="administration.reports.label.example"/> 500)</option>
						<option value="2">2 (<i18n:text key="administration.reports.label.example"/> 560)</option>
						<option value="3">3 (<i18n:text key="administration.reports.label.example"/> 561)</option>
						<option value="4" selected="selected">4 (<i18n:text key="administration.reports.label.example"/> 561.1)</option>
						<option value="5">5 (<i18n:text key="administration.reports.label.example"/> 561.11)</option>
						<option value="6">6 (<i18n:text key="administration.reports.label.example"/> 561.117)</option>
					</select>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</fieldset>
	
	<fieldset id="authorDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.author" /></legend>
		
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"></textarea>
		
		<div class="selected_record tabs">
			<div class="tabs_body">
				<div class="tab_body" data-tab="record">
					<div id="biblivre_record"></div>
					<textarea id="biblivre_record_template" class="template"></textarea>
				</div>
				<div class="tab_body" data-tab="form">
					<div class="biblivre_form_body"></div>
				</div>
				<div class="tab_body" data-tab="marc">
					<div class="biblivre_marc_body"></div>
				</div>
				<div class="tabs_extra_content">
					<div class="tab_extra_content biblivre_form" data-tab="form">
						<div id="biblivre_form"></div>
					</div>
					<div class="tab_extra_content biblivre_marc" data-tab="marc">
						<fieldset>
							<div id="biblivre_marc"></div>
							<textarea id="biblivre_marc_template" class="template"></textarea>
						</fieldset>
					</div>				
				</div>
			</div>
		</div>
	
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="small_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="CatalogingSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>
		</div>
		
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>

			<div class="paging_bar"></div>
			<div class="clear"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result" rel="{$T.record.id}">
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="Reports.generateAuthorReport({#var $T.record});"><i18n:text key="administration.reports.button.generate_report" /></a>
							</div>
							<div class="record_author">
								{#if $T.record.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.author}<br/>{#/if}
								{#if $T.record.count}<label><i18n:text key="administration.reports.label.author_count" /></label>: {$T.record.count}<br/>{#/if}
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
	
			<div class="paging_bar"></div>
		</div>
	</fieldset>
	
	<fieldset id="fieldCountDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.field_count" /></legend>
		<div class="description"><i18n:text key="administration.reports.field_count.description" /></div>
		
		<div class="biblivre_form_body reports">
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.marc_field" /></div>
				<div class="value" id="marc_field_combo"></div>
				<div class="clear"></div>	
			</div>
			
			<div class="field">
				<div class="label"><i18n:text key="administration.reports.field.order" /></div>
				<div class="value">
					<select name="count_order">
						<option value="1"><i18n:text key="administration.reports.select.option.marc_field"/></option>
						<option value="2"><i18n:text key="administration.reports.select.option.field_count"/></option>
					</select>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</fieldset>
	
	
	<fieldset id="catalogingDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.cataloging" /></legend>
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"></textarea>
		
		<div class="selected_record tabs">
			<div class="tabs_body">
				<div class="tab_body" data-tab="record">
					<div id="biblivre_record"></div>
					<textarea id="biblivre_record_template" class="template"></textarea>
				</div>
				<div class="tab_body" data-tab="form">
					<div class="biblivre_form_body"></div>
				</div>
				<div class="tab_body" data-tab="marc">
					<div class="biblivre_marc_body"></div>
				</div>
				<div class="tabs_extra_content">
					<div class="tab_extra_content biblivre_form" data-tab="form">
						<div id="biblivre_form"></div>
					</div>
					<div class="tab_extra_content biblivre_marc" data-tab="marc">
						<fieldset>
							<div id="biblivre_marc"></div>
							<textarea id="biblivre_marc_template" class="template"></textarea>
						</fieldset>
					</div>				
				</div>
			</div>
		</div>
	
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="small_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="CatalogingSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>
		</div>
		
		<div class="search_results_area">
			<div class="search_ordering_bar" style="padding: 0px 10px;">
	
				<div class="search_indexing_groups"></div>
				<textarea class="search_indexing_groups_template template"><!--
					{#foreach $T.search.indexing_group_count as group_count}
						{#foreach $T.indexing_groups as group}
							{#if $T.group_count.group_id == $T.group.id}
								{#if $T.group.id == CatalogingSearch.lastPagingParameters.indexing_group}
									<div class="group selected">
										<span class="name">{_('cataloging.bibliographic.indexing_groups.' + ($T.group.id ? $T.group.translation_key : 'total'))}</span>
										<span class="value">({_f($T.group_count.result_count)})</span>
									</div>
								{#else}
									<div class="group">
										<a href="javascript:void(0);" onclick="CatalogingSearch.changeIndexingGroup('{$T.group.id}');">{_('cataloging.bibliographic.indexing_groups.' + ($T.group.id ? $T.group.translation_key : 'total'))}</a>
										<span class="value">({_f($T.group_count.result_count)})</span>
									</div>
								{#/if}
							{#/if}						
						{#/for}
						{#if !$T.group_count$last} <div class="hspacer">|</div> {#/if}
					{#/for}
				--></textarea>
	
				<div class="search_sort_by"></div>
				<textarea class="search_sort_by_template template"><!--
					<i18n:text key="search.common.sort_by" />:
					<select class="combo search_sort_combo combo_auto_size combo_align_right" onchange="CatalogingSearch.changeSort(this.value);">
						{#foreach $T.indexing_groups as group}
							{#if $T.group.sortable}
								<option value="{$T.group.id}"{#if ((CatalogingSearch.lastPagingParameters.sort == $T.group.id) || (!CatalogingSearch.lastPagingParameters.sort && $T.group.default_sort))} selected="selected" {#/if}>{_('cataloging.bibliographic.indexing_groups.' + $T.group.translation_key)}</option>
							{#/if}
						{#/for}
					</select>
				--></textarea>			
				
				<div class="clear"></div>
			</div>
		
			<div class="search_loading_indicator loading_indicator"></div>

			<div class="paging_bar"></div>
			<div class="clear"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result" rel="{$T.record.id}">
							<div class="record_cataloging">
								{#if $T.record.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.record.title}<br/>{#/if}
								{#if $T.record.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.author}<br/>{#/if}
								{#if $T.record.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.record.publication_year}<br/>{#/if}
								{#if $T.record.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.record.shelf_location}<br/>{#/if}
								{#if $T.record.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.record.isbn}<br/>{#/if}
								{#if $T.record.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.record.issn}<br/>{#/if}
								{#if $T.record.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.record.isrc}<br/>{#/if}
	
								{#if $T.record.subject}
									<label><i18n:text key="search.bibliographic.subject" /></label>: {$T.record.subject}<br/>
								{#/if}
							</div>
							<div class="clear"></div>
						</div>
				--></textarea>
			</div>
	
			<div class="paging_bar"></div>
		</div>
	</fieldset>
	
	
	<fieldset id="userDiv" class="block reports">
		<legend><i18n:text key="administration.reports.fieldset.user" /></legend>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"></textarea>
	
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="small_input auto_focus" placeholder="<i18n:text key="administration.reports.user.search" />"/>
					<input type="hidden" name="field" value=""/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="CirculationSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
				</div>
			</div>
		</div>
		
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="paging_bar"></div>
			<div class="clear"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result" rel="{$T.record.id}">
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="Reports.generateUserReport('{$T.record.id}');"><i18n:text key="administration.reports.button.generate_report" /></a>
							</div>
							<div class="record_user">
								{#if $T.record.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.name}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.type_name}<br/>
								<div class="user_status_{$T.record.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.record.status)}</div>
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
			
			<div class="paging_bar"></div>	
		</div>
	</fieldset>
	
	<fieldset id="buttonDiv" class="block reports">
		<div class="biblivre_form_body">
			<div class="buttons">
				<a class="button main_button" onclick="Reports.generateReport();"><i18n:text key="administration.reports.button.generate_report" /></a>
			</div>
			<div class="clear"></div>
		</div>
	</fieldset>
	
	
</layout:body>
