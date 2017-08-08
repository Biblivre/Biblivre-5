<%@ page import="biblivre.marc.MaterialType" %>
<%@ page import="biblivre.cataloging.enums.RecordType" %>
<%@ page import="biblivre.cataloging.Fields" %>
<%@ page import="biblivre.administration.indexing.IndexingGroups" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.cataloging.css" />

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.search.js"></script>	

	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.input.js"></script>

	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "authorities").getCacheFileName() %>"></script>

	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">

	<script type="text/javascript">
		var CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'cataloging.authorities',
			root: '#authorities',
			enableTabs: true,
			enableHistory: true
		});

		CatalogingInput.type = 'cataloging.authorities';
		CatalogingInput.root = '#authorities';

		CatalogingInput.search = CatalogingSearch;
		CatalogingInput.defaultMaterialType = 'authorities';

	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="search.authorities.page_help" param1="<%= IndexingGroups.getSearchableGroupsText((String) request.getAttribute(\"schema\"), RecordType.AUTHORITIES, (String) request.getAttribute(\"language\")) %>" /></div>
	
	<div id="authorities">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
			
			<div class="simple_search text">
				<i18n:text key="search.authorities.simple_search" />
			</div>
			
			<div class="clear"></div>
		</div>
	
		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="CatalogingSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="CatalogingSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="CatalogingSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="record">
				{#if $T.name}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.name}<br/>{#/if}
				{#if $T.other_name}<label><i18n:text key="cataloging.authorities.other_name" /></label>: {$T.other_name}<br/>{#/if}
				{#if $T.author_type}<label><i18n:text key="cataloging.authorities.author_type" /></label>: {_('cataloging.authorities.author_type.' + $T.author_type)}<br/>{#/if}
				<label><i18n:text key="search.bibliographic.id" /></label>: {$T.id}<br/>
			</div>
		--></textarea>		
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="record"><i18n:text key="cataloging.tabs.brief" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="form"><i18n:text key="cataloging.tabs.form" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="marc"><i18n:text key="cataloging.tabs.marc" /></li>
			</ul>
	
			<div class="tabs_body">
				<div class="tab_body" data-tab="record">
					<div id="biblivre_record"></div>
					<textarea id="biblivre_record_template" class="template"><!-- 
						<input type="hidden" name="author_type" value="{$T.author_type}"/>
						<table class="record_fields">	
							<tr>
								<td class="label">{_('cataloging.authorities.author_type')}:</td>
								<td class="value">{_('cataloging.authorities.author_type.' + $T.author_type)}</td>
							</tr>
							{#foreach $T.fields as field}
								<tr>
									<td class="label">{_('cataloging.tab.record.custom.field_label.authorities_' + $T.field.datafield)}:</td>
									<td class="value">{$T.field.value}</td>
								</tr>
							{#/for}
						</table>
					--></textarea>
				</div>
				
				<div class="tab_body" data-tab="form">
					<div class="biblivre_form_body">
						<div class="field">
							<div class="label"><i18n:text key="cataloging.authorities.author_type"/></div>
							<div class="value">
								<select name="author_type" onchange="CatalogingInput.toggleAuthorType(this.value);">
		                            <option value="all"><i18n:text key="cataloging.authorities.author_type.select_author_type"/></option>
		                            <option value="100"><i18n:text key="cataloging.authorities.author_type.100"/></option>
		                            <option value="111"><i18n:text key="cataloging.authorities.author_type.111"/></option>
		                            <option value="110"><i18n:text key="cataloging.authorities.author_type.110"/></option>
		                        </select>								
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
	
				<div class="tab_body" data-tab="marc">
					<div class="biblivre_marc_body">
						<div class="field">
							<div class="label"><i18n:text key="cataloging.authorities.author_type"/></div>
							<div class="value">
								<select name="author_type" onchange="CatalogingInput.toggleAuthorType(this.value);">
		                            <option value="all"><i18n:text key="cataloging.authorities.author_type.select_author_type"/></option>
		                            <option value="100"><i18n:text key="cataloging.authorities.author_type.100"/></option>
		                            <option value="111"><i18n:text key="cataloging.authorities.author_type.111"/></option>
		                            <option value="110"><i18n:text key="cataloging.authorities.author_type.110"/></option>
		                        </select>								
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_form"></div>
				</div>
	
				<div class="tab_extra_content biblivre_marc" data-tab="marc">
					<fieldset>
						<div id="biblivre_marc"></div>
						<textarea id="biblivre_marc_template" class="template"><!-- 
								<table class="record_fields readonly_text">				
									{#foreach $T.fields as field}
										<tr>
											<td class="label">{$T.field.field}</td>
											<td class="value">{$T.field.value}</td>
										</tr>
									{#/for}
								</table>
						--></textarea>
						<textarea id="biblivre_marc_textarea"></textarea>
					</fieldset>
				</div>				
			</div>
		</div>
		
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="wide_query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="CatalogingSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>			
		</div>
		
		<div class="selected_results_area">
		</div>

		<textarea class="selected_results_area_template template"><!--
			{#if $T.length > 0}
				<fieldset class="block">
					<legend>{_p('cataloging.bibliographic.selected_records', $T.length)}</legend>
					<ul>
						{#foreach $T as record}
							<li rel="{$T.record.id}">{$T.record.name} <a class="xclose" onclick="CatalogingSearch.unselectRecord({$T.record.id});">&times;</a></li>
						{#/for}
					</ul>
					<div class="buttons">
						<a class="button center" onclick="CatalogingSearch.exportSelectedRecords();"><i18n:text key="cataloging.bibliographic.button.export_records" /></a>
					</div>
				</fieldset>
			{/#if}
		--></textarea>
	
		<div class="search_results_area">
			<div class="search_ordering_bar">
	
				<div class="search_indexing_groups"></div>
				<textarea class="search_indexing_groups_template template"><!--
					{#foreach $T.search.indexing_group_count as group_count}
						{#foreach $T.indexing_groups as group}
							{#if $T.group_count.group_id == $T.group.id}
								{#if $T.group.id == CatalogingSearch.lastPagingParameters.indexing_group}
									<div class="group selected">
										<span class="name">{_('cataloging.authorities.indexing_groups.' + ($T.group.id ? $T.group.translation_key : 'total'))}</span>
										<span class="value">({_f($T.group_count.result_count)})</span>
									</div>
								{#else}
									<div class="group">
										<a href="javascript:void(0);" onclick="CatalogingSearch.changeIndexingGroup('{$T.group.id}');">{_('cataloging.authorities.indexing_groups.' + ($T.group.id ? $T.group.translation_key : 'total'))}</a>
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
								<option value="{$T.group.id}"{#if ((CatalogingSearch.lastPagingParameters.sort == $T.group.id) || (!CatalogingSearch.lastPagingParameters.sort && $T.group.default_sort))} selected="selected" {#/if}>{_('cataloging.authorities.indexing_groups.' + $T.group.translation_key)}</option>
							{#/if}
						{#/for}
					</select>
				--></textarea>			
				
				<div class="clear"></div>
			</div>
	
			<div class="search_loading_indicator loading_indicator"></div>

		
			<div class="select_bar">
				<a class="button center" onclick="CatalogingSearch.selectPageResults();"><i18n:text key="cataloging.bibliographic.button.select_page" /></a> 
			</div>
			<div class="paging_bar"></div>
			<div class="clear"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']} {CatalogingInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{CatalogingInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="CatalogingSearch.openResult('{$T.record.id}');"><i18n:text key="search.bibliographic.open_item_button" /></a>
								<a class="button center" rel="select_item" onclick="CatalogingSearch.selectRecord('{$T.record.id}');"><i18n:text key="cataloging.bibliographic.button.select_item" /></a>
							</div>
							<div class="record">
								{#if $T.record.name}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.name}<br/>{#/if}
								{#if $T.record.other_name}<label><i18n:text key="cataloging.authorities.other_name" /></label>: {$T.record.other_name}<br/>{#/if}
								{#if $T.record.author_type}<label><i18n:text key="cataloging.authorities.author_type" /></label>: {_('cataloging.authorities.author_type.' + $T.record.author_type)}<br/>{#/if}
								<label><i18n:text key="search.bibliographic.id" /></label>: {$T.record.id}<br/>
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
