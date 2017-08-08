<%@page import="biblivre.administration.indexing.IndexingGroups"%>
<%@page import="biblivre.cataloging.enums.RecordType"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@page import="biblivre.marc.MaterialType"%>
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
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.circulation.css" />	
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.cataloging.css" />

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.cataloging.search.js"></script>	
	<script type="text/javascript" src="static/scripts/biblivre.circulation.reservation.js"></script>
	<script type="text/javascript">
		var CirculationSearch = CreateSearch(CirculationSearchClass, {
			type: 'circulation.reservation',
			prefix: 'reservation.user',
			root: '#circulation_search',
			searchAction: 'user_search',
			paginateAction: 'user_search',
			openAction: 'list',
			autoSelect: true,
			enableTabs: false,
			enableHistory: false
		});
		
		var CatalogingSearch = CreateSearch(CatalogingSearchClass, {
			type: 'circulation.reservation',
			prefix: 'reservation.record',
			root: '#cataloging_search',
			autoSelect: false,			
			enableTabs: false,
			enableHistory: false
		});
	</script>
</layout:head>

<%
	List<UserTypeDTO> userTypes = UserTypeBO.getInstance((String) request.getAttribute("schema")).list();
%>

<layout:body>
	<c:set var="user_field_prefix" value="<%= Constants.TRANSLATION_USER_FIELD %>" scope="page" />
	<div class="page_help"><i18n:text key="circulation.reservation.page_help" /></div>

	<div id="circulation_search">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="text">
				<i18n:text key="circulation.reservation.users.title" />
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
				<div class="fright" style="text-align: center">
					{#if $T.user.photo_id}
						<img class="user_photo" style="margin: 0; float: none; display: inline-block; width: 106px;" src="DigitalMediaController/?id={$T.user.photo_id}"/>
					{#else}
						<img class="user_photo" style="margin: -15px 0 -30px 0; float: none; display: inline-block; width: 106px;" src="static/images/photo.png"/>
					{#/if}
					<div class="ncspacer"></div>
					<a class="button center disabled" onclick="CirculationSearch.printReceipt();" id="lending_receipt_button"><i18n:text key="circulation.lending.button.print_receipt" /></a>
				</div>
			
				{#if $T.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.user.name}<br/>{#/if}
				<label><i18n:text key="circulation.user_field.id" /></label>: {$T.user.enrollment}<br/>
				<label><i18n:text key="circulation.user_field.type" /></label>: {$T.user.type_name}<br/>
				<div class="user_status_{$T.user.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.user.status)}</div>
				{#if $T.user.fines}<label><i18n:text key="circulation.user_field.fines" /></label>: {$T.user.fines}<br/>{#/if}

				<div class="ncspacer"></div>
				<div class="ncspacer"></div>

				<label><i18n:text key="circulation.reservation.reservation_count" /></label>:  {($T.reservationInfoList || {}).length || 0}

				<div class="clear"></div>

				{#if $T.reservationInfoList && $T.reservationInfoList.length > 0}
					{#foreach $T.reservationInfoList as info}
						<div class="result user_reservation" rel="{$T.info.reservation.id}">
							<div class="record">
								{#if $T.info.biblio.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.info.biblio.title}<br/>{#/if}
								{#if $T.info.biblio.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.info.biblio.author}<br/>{#/if}
								{#if $T.info.biblio.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.info.biblio.publication_year}<br/>{#/if}
								{#if $T.info.biblio.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.info.biblio.shelf_location}<br/>{#/if}
								{#if $T.info.biblio.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.info.biblio.isbn}<br/>{#/if}
								{#if $T.info.biblio.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.info.biblio.issn}<br/>{#/if}
								{#if $T.info.biblio.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.info.biblio.isrc}<br/>{#/if}

								<div class="ncspacer"></div>
								<div class="ncspacer"></div>

								<label><i18n:text key="circulation.reservation.reserve_date" /></label>: {_d($T.info.reservation.created, 'f')}<br/>
								<label><i18n:text key="circulation.reservation.expiration_date" /></label>: {_d($T.info.reservation.expires, 'D')}<br/>
							</div>
							<div class="reservation_buttons">
								<a class="button center" onclick="CatalogingSearch.deleteReservation({#var $T.info});"><i18n:text key="circulation.reservation.button.delete" /></a>
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				{#/if}
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
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.record.id}">
							<div class="buttons user_status_{$T.record.user.status}">
								<a class="button center" rel="open_item" onclick="CirculationSearch.openResult('{$T.record.id}');"><i18n:text key="circulation.reservation.button.select_reader" /></a>
							</div>
							<div class="record">
								{#if $T.record.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.user.name}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.user.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.user.type_name}<br/>
								<div class="user_status_{$T.record.user.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.record.user.status)}</div>

								<div class="ncspacer"></div>
								<div class="ncspacer"></div>

								<label><i18n:text key="circulation.reservation.reservation_count" /></label>:  {($T.record.reservationInfoList || {}).length || 0}
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
			
			<div class="paging_bar"></div>		
		</div>
	</div>

	<div id="cataloging_search">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="text">
				<i18n:text key="circulation.reservation.holdings.title" />
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
		
		<div class="clear"></div>
	
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
			<div class="filter_search">
				<div class="filter_checkbox">
					<input type="checkbox" name="record_list_reserved" id="record_list_reserved" value="true">
					<label class="search_label" for="record_list_reserved"><i18n:text key="circulation.reservation.record_list_reserved" /></label>
				</div>
			</div>
			<div class="clear_simple_search ico_clear mt10">
				<a href="javascript:void(0);" onclick="CatalogingSearch.clearSimpleSearch();"><i18n:text key="search.common.clear_simple_search" /></a>
			</div>
		</div>
	
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
		
			<div class="paging_bar"></div>

			<div class="search_results_box">
				<div class="search_results"></div>
				<textarea class="search_results_template template"><!-- 
					{#foreach $T.data as record}
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.record.id}">
							<div class="buttons">
								<a class="button center" onclick="CatalogingSearch.reserve('{$T.record.id}');"><i18n:text key="circulation.reservation.button.reserve" /></a>
							</div>

							<div class="record">
								{#if $T.record.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.record.title}<br/>{#/if}
								{#if $T.record.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.author}<br/>{#/if}
								{#if $T.record.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.record.publication_year}<br/>{#/if}
								{#if $T.record.shelf_location}<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.record.shelf_location}<br/>{#/if}
								{#if $T.record.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.record.isbn}<br/>{#/if}
								{#if $T.record.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.record.issn}<br/>{#/if}
								{#if $T.record.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.record.isrc}<br/>{#/if}
	
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>						

								<label><i18n:text key="search.bibliographic.holdings_count" /></label>: {$T.record.holdings_count}
								-
								<small>
									<label><i18n:text key="search.bibliographic.holdings_available" /></label>: {$T.record.holdings_available}&#160;
									<label><i18n:text key="search.bibliographic.holdings_lent" /></label>: {$T.record.holdings_lent}&#160;
									<label><i18n:text key="search.bibliographic.holdings_reserved" /></label>: {$T.record.holdings_reserved}
								</small>


								
								{#if $T.record.reservationInfo !== undefined}
									<div class="ncspacer"></div>
									<div class="ncspacer"></div>						
									<label><i18n:text key="circulation.reservation.record_reserved_to_the_following_readers" />:</label><br/>

									{#foreach $T.record.reservationInfo as info}
										<div class="user_reservation">
											{#if $T.info.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.info.user.name}<br/>{#/if}
											<label><i18n:text key="circulation.user_field.id" /></label>: {$T.info.user.enrollment}<br/>
											<label><i18n:text key="circulation.user_field.type" /></label>: {$T.info.user.type_name}<br/>
											<label><i18n:text key="circulation.reservation.reserve_date" /></label>: {_d($T.info.reservation.created, 'f')}<br/>
											<label><i18n:text key="circulation.reservation.expiration_date" /></label>: {_d($T.info.reservation.expires, 'D')}<br/>
										</div>
									{#/foreach}
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
