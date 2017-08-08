<%@ page import="biblivre.administration.indexing.IndexingGroups" %>
<%@ page import="biblivre.cataloging.enums.RecordType" %>
<%@ page import="biblivre.marc.MaterialType" %>
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
	
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.labels.js"></script>		

	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">

	<script type="text/javascript">
		var CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'cataloging.bibliographic',
			root: '#cataloging_search',
			autoSelect: false,
			enableTabs: false,
			enableHistory: false,
			advancedSearchAsDefault: true,
			extraParams: {
				holding_search: true
			}
		});
		
		CatalogingInput.type = 'cataloging.bibliographic';
		CatalogingInput.root = '#cataloging_search';
		
		CatalogingInput.search = CatalogingSearch;

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

<layout:body banner="true" >
	<div id="cataloging_search">
		<div id="database_selection">
			<div class="title"><i18n:text key="cataloging.database.title" />: </div>
			<select name="database" id="database_selection_combo" class="combo combo_auto_size" onchange="Core.trigger(CatalogingInput.type + 'cataloging-database-change');">
				<option value="main"><i18n:text key="cataloging.database.main" /></option>
				<option value="work"><i18n:text key="cataloging.database.work" /></option>
				<option value="private"><i18n:text key="cataloging.database.private" /></option>
				<option value="trash"><i18n:text key="cataloging.database.trash" /></option>
			</select>

			<div class="count" id="database_count"></div>

			<div class="clear"></div>
		</div>

		<div class="page_help"><i18n:text key="cataloging.labels.page_help" /></div>

		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text contains_subtext">
				<i18n:text key="search.common.simple_search" />
				<div class="subtext"><i18n:text key="search.common.switch_to" /> <a href="javascript:void(0);" onclick="CatalogingSearch.switchToAdvancedSearch();"><i18n:text key="search.common.advanced_search" /></a></div>
			</div>
	
			<div class="advanced_search text contains_subtext">
				<i18n:text key="search.common.advanced_search" />
				<div class="subtext"><i18n:text key="search.common.switch_to" /> <a href="javascript:void(0);" onclick="CatalogingSearch.switchToSimpleSearch();"><i18n:text key="search.common.simple_search" /></a></div>
			</div>
			
			<div class="clear"></div>
		</div>
	
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<label class="search_label"><i18n:text key="search.bibliographic.material_type" /></label>
					<select name="material" class="combo combo_expand">
						<c:forEach var="material" items="<%= MaterialType.searchableValues() %>" >
							<option value="${material.string}"><i18n:text key="marc.material_type.${material.string}" /></option>
						</c:forEach>
					</select>
					<a class="main_button arrow_right" onclick="CatalogingSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>
	
			<div class="advanced_search submit_on_enter">
				<div class="search_entry">
					<div class="operator">
						<label class="search_label"><i18n:text key="search.common.operator" /></label>
						<select name="operator" class="combo combo_expand">
							<option value="AND"><i18n:text key="search.common.operator.and" /></option>
							<option value="OR"><i18n:text key="search.common.operator.or" /></option>
							<option value="AND_NOT"><i18n:text key="search.common.operator.and_not" /></option>
						</select>
					</div>
					<div class="query">
						<label class="search_label"><i18n:text key="search.common.containing_text" /></label>
						<input type="text" name="query" class="big_input"/>
					</div>
					<div class="field">
						<label class="search_label"><i18n:text key="search.common.on_the_field" /></label>
						<select name="field" class="combo combo_expand">
							<c:forEach var="group" items="<%= IndexingGroups.getGroups((String) request.getAttribute(\"schema\"), RecordType.BIBLIO) %>">
								<option value="${group.id}"><i18n:text key="cataloging.bibliographic.indexing_groups.${group.translationKey}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="clear"></div>
				</div>
	
				<div class="add_term ico_add">
					<a href="javascript:void(0);" onclick="CatalogingSearch.addEntryAdvancedSearch();"><i18n:text key="search.common.add_field" /></a>
				</div>
						
				<div class="filter_search">
					<div class="fleft filter_material">
						<label class="search_label"><i18n:text key="search.bibliographic.material_type" /></label>
						<select name="material" class="combo combo_expand">
							<c:forEach var="material" items="<%= MaterialType.searchableValues() %>" >
								<option value="${material.string}"><i18n:text key="marc.material_type.${material.string}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.created_between" /></label>
						<input type="text" name="created_start" class="small_input datepicker" />-<input type="text" name="created_end" class="small_input datepicker" />
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.modified_between" /></label>
						<input type="text" name="modified_start" class="small_input datepicker" />-<input type="text" name="modified_end" class="small_input datepicker" />
					</div>
					<div class="clear"></div>
					<div class="filter_checkbox">
						<input type="checkbox" name="holding_label_never_printed" id="holding_label_never_printed" value="true" checked="checked">
						<label class="search_label" for="holding_label_never_printed"><i18n:text key="search.bibliographic.labels.never_printed" /></label>
					</div>					
				</div>

				<div class="fleft clear_search ico_clear">
					<a href="javascript:void(0);" onclick="CatalogingSearch.clearAdvancedSearch();"><i18n:text key="search.common.clear_search" /></a>
				</div>

				<div class="buttons">
					<a class="main_button arrow_right" onclick="CatalogingSearch.search('advanced');"><i18n:text key="search.common.button.list_all" /></a>
					<div class="clear"></div>
				</div>
			</div>
		</div>
		
		<div class="selected_results_area">
		</div>
		<textarea class="selected_results_area_template template"><!--
			{#if $T.length > 0}
				<fieldset class="block">
					<legend>{_p('cataloging.labels.selected_records', $T.length)}</legend>
					<ul>
						{#foreach $T as record}
							<li rel="{$T.record.id}">{$T.record.accession_number} - {$T.record.biblio.title} <a class="xclose" onclick="CatalogingSearch.unselectRecord({$T.record.id});">&times;</a></li>
						{#/for}
					</ul>
					<div class="buttons">
						<a class="main_button center" onclick="CatalogingLabels.printLabels();"><i18n:text key="cataloging.labels.button.print_labels" /></a>
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
		
			<div class="select_bar">
				<a class="button center" onclick="CatalogingSearch.selectPageResults();"><i18n:text key="cataloging.labels.button.select_page" /></a> 
			</div>
			<div class="paging_bar"></div>
			<div class="clear"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']} {CatalogingInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="buttons">
								<a class="button center" rel="select_item" onclick="CatalogingSearch.selectRecord('{$T.record.id}');"><i18n:text key="cataloging.labels.button.select_item" /></a>
							</div>
							<div class="record">
								{#if $T.record.accession_number}<label><i18n:text key="search.holding.accession_number" /></label>: {$T.record.accession_number}<br/>{#/if}
								{#if $T.record.shelf_location}<label><i18n:text key="search.holding.shelf_location" /></label>: {$T.record.shelf_location}<br/>{#/if}

								<div class="ncspacer"></div>

								{#if $T.record.biblio.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.record.biblio.title}<br/>{#/if}
								{#if $T.record.biblio.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.biblio.author}<br/>{#/if}
								{#if $T.record.biblio.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.record.biblio.publication_year}<br/>{#/if}
								{#if $T.record.biblio.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.record.biblio.shelf_location}<br/>{#/if}
								{#if $T.record.biblio.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.record.biblio.isbn}<br/>{#/if}
								{#if $T.record.biblio.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.record.biblio.issn}<br/>{#/if}
								{#if $T.record.biblio.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.record.biblio.isrc}<br/>{#/if}
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
		<div class="close" onclick="CatalogingLabels.hidePopup();"><i18n:text key="common.close" /></div>

		<fieldset>
			<legend><i18n:text key="cataloging.labels.popup.title" /></legend>
			<div class="content"><select id="label_format_select"></select></div>
		</fieldset>
		
		<fieldset>
			<legend><i18n:text key="cataloging.labels.popup.description" /></legend>
			<div class="content">
				<table id="label_table"><tbody></tbody></table>
			</div>
		</fieldset>
	</div>
</layout:body>
