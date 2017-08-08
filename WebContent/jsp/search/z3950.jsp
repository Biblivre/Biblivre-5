<%@ page import="biblivre.marc.MaterialType" %>
<%@ page import="biblivre.cataloging.Fields" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.cataloging.css" />

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.search.js"></script>	
	<script type="text/javascript" src="static/scripts/biblivre.z3950.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.input.js"></script>
	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "biblio").getCacheFileName() %>"></script>

	<script type="text/javascript">
		var Z3950Search = CreateSearch(Z3950SearchClass, {
			type: 'cataloging.bibliographic',
			root: '#distributed_search',
			//autoSelect: true,
			enableTabs: true,
			enableHistory: true
		});
	
		CatalogingInput.type = 'cataloging.bibliographic';
		CatalogingInput.root = '#distributed_search';
	
		CatalogingInput.search = Z3950Search;
		CatalogingInput.defaultMaterialType = 'book';
	</script>
	
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="search.distributed.page_help" /></div>

	<div id="distributed_search">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="search.common.distributed_search" />
			</div>
		</div>
	
		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="Z3950Search.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="Z3950Search.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="Z3950Search.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
	
	
		<div class="clear"></div>
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="record">
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
		--></textarea>
		
		<c:choose>
			<c:when test="${empty servers}">
				<div class="description">
					<p><i18n:text key="search.distributed.no_servers"></i18n:text></p>
				</div>
			</c:when>
			<c:otherwise>
				<div class="search_box submit_on_enter">
					<div class="simple_search">
						<div class="distributed_query">
							<input type="text" name="query" class="search_input big_input auto_focus" placeholder="<i18n:text key="search.distributed.query_placeholder" />"/>
						</div>
			
						<div class="attributes">
							<label class="search_label"><i18n:text key="search.common.on_the_field" /> </label>
							<select name="attribute" class="combo combo_expand">
								<option value="4"><i18n:text key="search.distributed.title" /></option>
								<option value="1003"><i18n:text key="search.distributed.author" /></option>
								<option value="7"><i18n:text key="search.distributed.isbn" /></option>
								<option value="8"><i18n:text key="search.distributed.issn" /></option>
								<option value="21"><i18n:text key="search.distributed.subject" /></option>
								<option value="1016"><i18n:text key="search.distributed.any" /></option>
							</select>
						</div>
					</div>
				
					<div class="distributed_servers">
						<label class="search_label"><i18n:text key="search.common.in_these_libraries" /> </label>
						<fieldset>
						<c:forEach var="server" items="${servers}">
							<div class="server"><input type="checkbox" name="server" id="server_${server.id}" value="${server.id}" /> <label for="server_${server.id}">${server.name}</label></div>
						</c:forEach>
						</fieldset>
					</div>
					
					<div class="clear"></div>
					
					<div class="distributed_search_button">
						<a class="main_button arrow_right" onclick="Z3950Search.search('simple');"><i18n:text key="search.common.button.search" /></a>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="record" onclick="Core.changeTab(this, Z3950Search);"><i18n:text key="cataloging.tabs.brief" /></li>
				<li class="tab" data-tab="form" onclick="Core.changeTab(this, Z3950Search);"><i18n:text key="cataloging.tabs.form" /></li>
				<li class="tab" data-tab="marc" onclick="Core.changeTab(this, Z3950Search);"><i18n:text key="cataloging.tabs.marc" /></li>
			</ul>
	
			<div class="tabs_body">
				<div class="tab_body" data-tab="record">
					<div id="biblivre_record"></div>
					<textarea id="biblivre_record_template" class="template"><!-- 
						<input type="hidden" name="material_type" value="{$T.material_type}"/>
						<table class="record_fields">	
							{#foreach $T.fields as field}
								<tr>
									<td class="label">{_('cataloging.tab.record.custom.field_label.biblio_' + $T.field.datafield)}:</td>
									<td class="value">{$T.field.value}</td>
								</tr>
							{#/for}
						</table>
					--></textarea>
				</div>
				
				<div class="tab_body" data-tab="form">
					<fieldset class="noborder">
						<div class="biblivre_form_body">
							<div class="field">
								<div class="label"><i18n:text key="cataloging.bibliographic.material_type"/></div>
								<div class="value">
									<select name="material_type" onchange="CatalogingInput.toggleMaterialType(this.value);">
										<c:forEach var="material" items="<%= MaterialType.values() %>" >
											<c:if test="${material.searchable && material.string ne 'all'}">
												<option value="${material.string}"><i18n:text key="marc.material_type.${material.string}" /></option>
											</c:if>
										</c:forEach>
									</select>
								</div>
								<div class="clear"></div>
							</div>
						</div>
					</fieldset>
				</div>
	
				<div class="tab_body" data-tab="marc">
					<div class="biblivre_marc_body">
						<div class="label"><i18n:text key="cataloging.bibliographic.material_type"/></div>
	
						<div class="value">
							<select name="material_type" onchange="CatalogingInput.toggleMaterialType(this.value);">
								<c:forEach var="material" items="<%= MaterialType.values() %>" >
									<c:if test="${material.searchable && material.string ne 'all'}">
										<option value="${material.string}"><i18n:text key="marc.material_type.${material.string}" /></option>
									</c:if>
								</c:forEach>
							</select>
						</div>
	
						<div class="clear"></div>
					</div>
				</div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="record">
					<textarea id="biblivre_record_textarea"></textarea>
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
			</div>
		</div>
	
		<div class="search_results_area">
			<div class="search_loading_indicator loading_indicator"></div>
		
			<div class="paging_bar"></div>
	
			<div class="search_results_box">
			
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as result}
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.result.id}">
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="Z3950Search.openResult('{$T.result.id}');"><i18n:text key="search.bibliographic.open_item_button" /></a>
							</div>
							<div class="record">
								{#if $T.result.server_name}<label><i18n:text key="search.common.library" /></label>: {$T.result.server_name}<br/>{#/if}
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>
								{#if $T.result.record.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.result.record.title}<br/>{#/if}
								{#if $T.result.record.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.result.record.author}<br/>{#/if}
								{#if $T.result.record.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.result.record.publication_year}<br/>{#/if}
								{#if $T.result.record.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.result.record.shelf_location}<br/>{#/if}
								{#if $T.result.record.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.result.record.isbn}<br/>{#/if}
								{#if $T.result.record.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.result.record.issn}<br/>{#/if}
								{#if $T.result.record.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.result.record.isrc}<br/>{#/if}
	
								{#if $T.result.record.subject}
									<label><i18n:text key="search.bibliographic.subject" /></label>: {$T.result.record.subject}<br/>
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
