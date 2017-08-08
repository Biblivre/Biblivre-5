<%@page import="biblivre.cataloging.enums.HoldingAvailability"%>
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
	<script type="text/javascript" src="static/scripts/biblivre.holding.search.js"></script>	

	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.holding.input.js"></script>

	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "biblio").getCacheFileName() %>"></script>
	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "holding").getCacheFileName() %>"></script>

	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">

	<script type="text/javascript">
		var CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'cataloging.bibliographic',
			root: '#cataloging_search',
			//autoSelect: true,
			enableTabs: true,
			enableHistory: true
		});

		CatalogingInput.type = 'cataloging.bibliographic';
		CatalogingInput.root = '#cataloging_search';

		CatalogingInput.search = CatalogingSearch;
		CatalogingInput.defaultMaterialType = 'book';

		var HoldingSearch = CreateSearch(HoldingSearchClass, {
			type: 'cataloging.holding',
			root: '#holding_search',
			enableTabs: true,
			enableHistory: false,
			enablePaging: false,
			defaultTab: 'holding_form'
		});

		HoldingInput.type = 'cataloging.holding';
		HoldingInput.root = '#holding_search';
		HoldingInput.search = HoldingSearch;
		
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

<layout:body banner="true" multiPart="true">
	<div id="cataloging_search">
		<div id="database_selection">
			<div class="title"><i18n:text key="cataloging.database.title" />: </div>
			<select name="database" id="database_selection_combo" class="combo combo_auto_size" onchange="Core.trigger(CatalogingInput.type + 'cataloging-database-change');">
				<option value="main"><i18n:text key="cataloging.database.main" /></option>
				<option value="work"><i18n:text key="cataloging.database.work" /></option>
				<option value="private"><i18n:text key="cataloging.database.private" /></option>
				<option value="trash"><i18n:text key="cataloging.database.trash" /></option>
			</select>

			<div class="buttons">
				<a class="button center" id="new_record_button" onclick="CatalogingInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>

			<div class="count" id="database_count"></div>

			<div class="clear"></div>
		</div>

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
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="CatalogingInput.editRecord('{$T.id}');"><i18n:text key="cataloging.bibliographic.button.edit" /></a>
					<a class="danger_button center" onclick="CatalogingInput.deleteRecord('{$T.id}');"><i18n:text key="cataloging.bibliographic.button.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.saveRecord(true);"><i18n:text key="cataloging.bibliographic.button.save_as_new" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>

				<div class="new">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>
			</div>	
			<div class="record">
				{#if $T.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.title}<br/>{#/if}
				{#if $T.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.author}<br/>{#/if}
				{#if $T.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.publication_year}<br/>{#/if}
				{#if $T.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.shelf_location}<br/>{#/if}
				{#if $T.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.isbn}<br/>{#/if}
				{#if $T.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.issn}<br/>{#/if}
				{#if $T.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.isrc}<br/>{#/if}
				<label><i18n:text key="search.bibliographic.id" /></label>: {$T.id}<br/>
			</div>
		--></textarea>		
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="record"><i18n:text key="cataloging.tabs.brief" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="form"><i18n:text key="cataloging.tabs.form" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="marc"><i18n:text key="cataloging.tabs.marc" /></li>
				<li class="tab" onclick="Core.changeTab(this, CatalogingSearch);" data-tab="holding"><i18n:text key="cataloging.tabs.holdings" /></li>
			</ul>
	
			<div class="tabs_body">
				<div class="tab_body" data-tab="record">
					<div id="biblivre_record"></div>
					<textarea id="biblivre_record_template" class="template"><!-- 
						<input type="hidden" name="material_type" value="{$T.material_type}"/>
						<table class="record_fields">	
							<tr>
								<td class="label">{_('cataloging.bibliographic.material_type')}:</td>
								<td class="value">{_('marc.material_type.' + $T.material_type)}</td>
							</tr>
							{#foreach $T.fields as field}
								<tr>
									<td class="label">{_('cataloging.tab.record.custom.field_label.biblio_' + $T.field.datafield)}:</td>
									<td class="value">{($T.field.value || '').replace(/\n/g, '<br>')}</td>
								</tr>
							{#/for}
						</table>
					--></textarea>
				</div>
				
				<div class="tab_body" data-tab="form">
					<div class="biblivre_form_body">
						<div class="field">
							<div class="label"><i18n:text key="cataloging.bibliographic.material_type"/></div>
							<div class="value">
								<select name="material_type" onchange="CatalogingInput.toggleMaterialType(this.value);">
									<c:forEach var="material" items="<%= MaterialType.bibliographicValues() %>" >
										<option value="${material.string}"><i18n:text key="marc.material_type.${material.string}" /></option>
									</c:forEach>
								</select>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
	
				<div class="tab_body" data-tab="marc">
					<div class="biblivre_marc_body">
						<div class="field">
							<div class="label"><i18n:text key="cataloging.bibliographic.material_type"/></div>
							<div class="value">
								<select name="material_type" onchange="CatalogingInput.toggleMaterialType(this.value);">
									<c:forEach var="material" items="<%= MaterialType.bibliographicValues() %>" >
										<option value="${material.string}"><i18n:text key="marc.material_type.${material.string}" /></option>
									</c:forEach>
								</select>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>

				<div class="tab_body" data-tab="holding">
					<div class="biblivre_holdings"></div>
					<textarea class="biblivre_holdings_template template"><!-- 
						<div class="fright"><a class="button center" id="new_holding_button" onclick="HoldingInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new_holding" /></a></div>
						<label><i18n:text key="search.bibliographic.holdings_count" /></label>: {$T.holdings_count}
						<br/>
						<label><i18n:text key="search.bibliographic.holdings_available" /></label>: {$T.holdings_available}
						<br/>
						<label><i18n:text key="search.bibliographic.holdings_lent" /></label>: {$T.holdings_lent}
						<br/>
						<label><i18n:text key="search.bibliographic.holdings_reserved" /></label>: {$T.holdings_reserved}
					--></textarea>
				</div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_record_extra" data-tab="record">
					<fieldset>
						<legend><i18n:text key="cataloging.common.digital_files" /></legend>
						
						<ul id="biblivre_attachments"></ul>
						<textarea id="biblivre_attachments_template" class="template"><!-- 
							{#if !$T.attachments || $T.attachments.length == 0}
								<div class="center" style="margin-bottom: 10px;"><strong><i18n:text key="cataloging.bibliographic.no_attachments" /></strong></div>
							{#/if}
							{#foreach $T.attachments as attachment}
								<li><a href="{($T.attachment.uri.substring(0,10) == "/biblivre3") ? ("/Biblivre5" + $T.attachment.uri.substring(10)) : ($T.attachment.uri)}" target="_blank">{$T.attachment.name}</a> <a class="xclose" onclick="CatalogingInput.removeAttachment(this, '{$T.attachment.name}', '{$T.attachment.uri}');">&times;</a></li>
							{#/for}
						--></textarea>
						<textarea id="biblivre_record_textarea"></textarea>
						<div class="upload">
							<input type="file" class="import_file" name="file" onchange="CatalogingInput.upload(this);" />
							<a class="fright arrow_right button" onclick="CatalogingInput.upload(this);"><i18n:text key="cataloging.common.button.upload" /></a>
							<div class="clear"></div>
						</div>
					</fieldset>
				</div>

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
				
				<div class="tab_extra_content biblivre_form" data-tab="form marc">
					<div class="automatic_holding">
						<div class="form_help form_help_cataloging"><i18n:text key="cataloging.bibliographic.automatic_holding_help" /></div>
						<fieldset>
							<legend><i18n:text key="cataloging.bibliographic.automatic_holding_title" /></legend>
							<div>
								<div class="label"><i18n:text key="cataloging.bibliographic.automatic_holding.holding_count" /></div>
								<div class="value"><input type="text" name="holding_count" class="finput" /></div>
								<div class="clear"></div>
							</div>
							<div>
								<div class="label"><i18n:text key="cataloging.bibliographic.automatic_holding.holding_volume_number" /></div>
								<div class="value"><input type="text" name="holding_volume_number" class="finput" /></div>
								<div class="extra"><input type="radio" name="holding_volume_type" value="number"></div>
								<div class="clear"></div>
							</div>
							<div>
								<div class="label"><i18n:text key="cataloging.bibliographic.automatic_holding.holding_volume_count" /></div>
								<div class="value"><input type="text" name="holding_volume_count" class="finput" /></div>
								<div class="extra"><input type="radio" name="holding_volume_type" value="count"></div>								
								<div class="clear"></div>
							</div>
							<div>
								<div class="label"><i18n:text key="cataloging.bibliographic.automatic_holding.holding_acquisition_date" /></div>
								<div class="value"><input type="text" name="holding_acquisition_date" class="finput" /></div>
								<div class="clear"></div>
							</div>
							<div>
								<div class="label"><i18n:text key="cataloging.bibliographic.automatic_holding.holding_library" /></div>
								<div class="value"><input type="text" name="holding_library" class="finput" /></div>
								<div class="clear"></div>
							</div>
							<div>
								<div class="label"><i18n:text key="cataloging.bibliographic.automatic_holding.holding_acquisition_type" /></div>
								<div class="value"><input type="text" name="holding_acquisition_type" class="finput" /></div>
								<div class="clear"></div>
							</div>
						</fieldset>
					</div>				
				</div>
			</div>
			
			<div class="footer_buttons">
				<div class="edit">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.saveRecord(true);"><i18n:text key="cataloging.bibliographic.button.save_as_new" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>

				<div class="new">
					<a class="main_button center" onclick="CatalogingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="CatalogingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>
			</div>	
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
							<option value="holding_accession_number"><i18n:text key="cataloging.bibliographic.search.holding_accession_number" /></option>
							<option value="holding_id"><i18n:text key="cataloging.bibliographic.search.holding_id" /></option>
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
						<input type="text" name="created_start" class="small_input datepicker" /><input type="text" name="created_end" class="small_input datepicker" />
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.modified_between" /></label>
						<input type="text" name="modified_start" class="small_input datepicker" /><input type="text" name="modified_end" class="small_input datepicker" />
					</div>
					<div class="clear"></div>
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
					<legend>{_p('cataloging.bibliographic.selected_records', $T.length)}</legend>
					<ul>
						{#foreach $T as record}
							<li rel="{$T.record.id}">{$T.record.title} <a class="xclose" onclick="CatalogingSearch.unselectRecord({$T.record.id});">&times;</a></li>
						{#/for}
					</ul>
					<div class="buttons">
						<select name="move" class="combo combo_hide_empty_value" onchange="CatalogingInput.confirmMoveSelectedRecords(this.value);">
							<option value=""><i18n:text key="cataloging.bibliographic.button.move_records" /></option>
							<option value="main"><i18n:text key="cataloging.database.main_full" /></option>
							<option value="work"><i18n:text key="cataloging.database.work_full" /></option>
							<option value="private"><i18n:text key="cataloging.database.private_full" /></option>
							<option value="trash"><i18n:text key="cataloging.database.trash_full" /></option>
						</select>
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
								<a class="danger_button center" onclick="CatalogingInput.deleteRecord('{$T.record.id}');"><i18n:text key="cataloging.bibliographic.button.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.record.title}<br/>{#/if}
								{#if $T.record.author}<label><i18n:text key="search.bibliographic.author" /></label>: {CatalogingSearch.linkToSearch($T.record.author, 1)}<br/>{#/if}
								{#if $T.record.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.record.publication_year}<br/>{#/if}
								{#if $T.record.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.record.shelf_location}<br/>{#/if}
								{#if $T.record.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.record.isbn}<br/>{#/if}
								{#if $T.record.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.record.issn}<br/>{#/if}
								{#if $T.record.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.record.isrc}<br/>{#/if}
	
								{#if $T.record.subject}
									<label><i18n:text key="search.bibliographic.subject" /></label>: {CatalogingSearch.linkToSearch($T.record.subject, 4)}<br/>
								{#/if}
	
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>						
								<label><i18n:text key="search.bibliographic.holdings_count" /></label>: {$T.record.holdings_count}
								-
								<small>
								<label><i18n:text key="search.bibliographic.holdings_available" /></label>: {$T.record.holdings_available}&#160;
								<label><i18n:text key="search.bibliographic.holdings_lent" /></label>: {$T.record.holdings_lent}&#160;
								<label><i18n:text key="search.bibliographic.holdings_reserved" /></label>: {$T.record.holdings_reserved}
								</small>
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
	
			<div class="paging_bar"></div>
		</div>
	</div>

	<div id="holding_search">
		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="HoldingSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>
			
			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="HoldingSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="HoldingSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="HoldingInput.editRecord('{$T.id}');"><i18n:text key="cataloging.bibliographic.button.edit" /></a>
					<a class="danger_button center" onclick="HoldingInput.deleteRecord('{$T.id}');"><i18n:text key="cataloging.bibliographic.button.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="HoldingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="HoldingInput.saveRecord(true);"><i18n:text key="cataloging.bibliographic.button.save_as_new" /></a>
					<a class="button center" onclick="HoldingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>

				<div class="new">
					<a class="main_button center" onclick="HoldingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="HoldingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>
			</div>	
			<div class="record">
				{#if $T.accession_number}<label><i18n:text key="search.holding.accession_number" /></label>: {$T.accession_number}<br/>{#/if}
				{#if $T.lent}<label><i18n:text key="search.holding.lending_state" /></label>: {$T.lent}<br/>{#/if}
				{#if $T.availability}<label><i18n:text key="search.holding.availability" /></label>: {_('cataloging.holding.availability.' + $T.availability)}<br/>{#/if}
				{#if $T.shelf_location || $T.location_d}
					<label><i18n:text key="search.holding.shelf_location" /></label>: {$T.shelf_location || ''} {$T.location_d || ''}<br/>
				{#/if}

				<label><i18n:text key="search.bibliographic.id" /></label>: {$T.id}<br/>

				{#if $T.attachments && $T.attachments.length > 0}
					<div class="ncspacer"></div>
					{#foreach $T.attachments as attachment}
						<li><a href="{($T.attachment.uri.substring(0,10) == "/biblivre3") ? ("/Biblivre5" + $T.attachment.uri.substring(10)) : ($T.attachment.uri)}" target="_blank">{$T.attachment.name}</a></li>
					{#/for}
				{#/if}
			</div>
		--></textarea>
		
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" onclick="Core.changeTab(this, HoldingSearch);" data-tab="holding_form"><i18n:text key="cataloging.tabs.form" /></li>
				<li class="tab" onclick="Core.changeTab(this, HoldingSearch);" data-tab="holding_marc"><i18n:text key="cataloging.tabs.marc" /></li>
			</ul>
	
			<div class="tabs_body">
				<div class="tab_body" data-tab="holding_form">
					<div class="biblivre_holding_form_body">
						<div class="field">
							<div class="label"><i18n:text key="cataloging.holding.availability"/></div>
							<div class="value">
								<select name="holding_availability">
									<c:forEach var="availability" items="<%= HoldingAvailability.values() %>" >
										<option value="${availability.string}"><i18n:text key="cataloging.holding.availability.${availability.string}" /></option>
									</c:forEach>								
								</select>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
	
				<div class="tab_body" data-tab="holding_marc">
					<div class="biblivre_holding_marc_body">
						<div class="field">
							<div class="label"><i18n:text key="cataloging.holding.availability"/></div>
							<div class="value">
								<select name="holding_availability">
									<c:forEach var="availability" items="<%= HoldingAvailability.values() %>" >
										<option value="${availability.string}"><i18n:text key="cataloging.holding.availability.${availability.string}" /></option>
									</c:forEach>								
								</select>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="holding_form">
					<div id="biblivre_holding_form"></div>
				</div>
	
				<div class="tab_extra_content biblivre_marc" data-tab="holding_marc">
					<fieldset>
						<div id="biblivre_holding_marc"></div>
						<textarea id="biblivre_holding_marc_template" class="template"><!-- 
								<table class="record_fields readonly_text">				
									{#foreach $T.fields as field}
										<tr>
											<td class="label">{$T.field.field}</td>
											<td class="value">{$T.field.value}</td>
										</tr>
									{#/for}
								</table>
						--></textarea>
						<textarea id="biblivre_holding_marc_textarea"></textarea>
					</fieldset>
				</div>
			</div>

			<div class="footer_buttons">
				<div class="edit">
					<a class="main_button center" onclick="HoldingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="HoldingInput.saveRecord(true);"><i18n:text key="cataloging.bibliographic.button.save_as_new" /></a>
					<a class="button center" onclick="HoldingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>

				<div class="new">
					<a class="main_button center" onclick="HoldingInput.saveRecord();"><i18n:text key="cataloging.bibliographic.button.save" /></a>
					<a class="button center" onclick="HoldingInput.cancelEdit();"><i18n:text key="cataloging.bibliographic.button.cancel" /></a>
				</div>
			</div>	
		</div>

		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{$T.data.overlay_text}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="HoldingSearch.openResult('{$T.record.id}');"><i18n:text key="search.bibliographic.open_item_button" /></a>
								<a class="danger_button center" onclick="HoldingInput.deleteRecord('{$T.record.id}');"><i18n:text key="cataloging.bibliographic.button.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.accession_number}<label><i18n:text key="search.holding.accession_number" /></label>: {$T.record.accession_number}<br/>{#/if}
								{#if $T.record.lent}<label><i18n:text key="search.holding.lending_state" /></label>: {$T.record.lent}<br/>{#/if}
								{#if $T.record.availability}<label><i18n:text key="search.holding.availability" /></label>: {_('cataloging.holding.availability.' + $T.record.availability)}<br/>{#/if}
								{#if $T.record.shelf_location || $T.record.location_d}
									<label><i18n:text key="search.holding.shelf_location" /></label>: {$T.record.shelf_location || ''} {$T.record.location_d || ''}<br/>
								{#/if}

								<label><i18n:text key="search.bibliographic.id" /></label>: {$T.record.id}<br/>
																
								{#if $T.record.attachments && $T.record.attachments.length > 0}
									<div class="ncspacer"></div>
									{#foreach $T.record.attachments as attachment}
										<li><a href="{($T.attachment.uri.substring(0,10) == "/biblivre3") ? ("/Biblivre5" + $T.attachment.uri.substring(10)) : ($T.attachment.uri)}" target="_blank">{$T.attachment.name}</a></li>
									{#/for}
								{#/if}
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
		</div>
	</div>
	
	<div id="upload_popup" class="popup">
		<fieldset class="upload">
			<legend><i18n:text key="cataloging.upload_popup.title" /></legend>

			<div class="description">
				<p class="uploading"><i18n:text key="cataloging.upload_popup.uploading" /></p>
			</div>

			<div class="progress">
				<div class="progress_text"><i18n:text key="common.wait" /></div>
				<div class="progress_bar">
					<div class="progress_bar_outer"><div class="progress_bar_inner"></div></div>
				</div>
			</div>
		</fieldset>
	</div>
	
</layout:body>
