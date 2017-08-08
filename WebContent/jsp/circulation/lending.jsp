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
	<script type="text/javascript" src="static/scripts/biblivre.holding.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.lending.js"></script>
	<script type="text/javascript">
		var CirculationSearch = CreateSearch(CirculationSearchClass, {
			type: 'circulation.lending',
			prefix: 'lending.user',
			root: '#circulation_search',
			searchAction: 'user_search',
			paginateAction: 'user_search',
			openAction: 'list',
			autoSelect: true,
			enableTabs: false,
			enableHistory: false
		});

		var HoldingSearch = CreateSearch(HoldingSearchClass, {
			type: 'circulation.lending',
			prefix: 'lending.holding',
			root: '#holding_search',
			paginateAction: 'search',
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
	<div class="page_help"><i18n:text key="circulation.lending.page_help" /></div>

	<div id="circulation_search">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="text">
				<i18n:text key="circulation.lending.users.title" />
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

				<label><i18n:text key="circulation.lending.lending_count" /></label>:  {($T.lendingInfo || {}).length || 0}

				<div class="clear"></div>
				
				{#if $T.lendingInfo && $T.lendingInfo.length > 0}
					{#foreach $T.lendingInfo as info}
						<div class="result user_lending" rel="{$T.info.lending.id}">
							<div class="record">
								{#if $T.info.biblio.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.info.biblio.title}<br/>{#/if}
								{#if $T.info.biblio.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.info.biblio.author}<br/>{#/if}
								{#if $T.info.biblio.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.info.biblio.publication_year}<br/>{#/if}
								{#if $T.info.biblio.shelf_location || $T.info.holding.shelf_location || $T.info.holding.location_d}
									<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.info.holding.shelf_location || $T.info.biblio.shelf_location || ''} {$T.info.holding.location_d || ''}<br/>
								{#/if}
								{#if $T.info.biblio.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.info.biblio.isbn}<br/>{#/if}
								{#if $T.info.biblio.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.info.biblio.issn}<br/>{#/if}
								{#if $T.info.biblio.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.info.biblio.isrc}<br/>{#/if}

								<div class="ncspacer"></div>
								<div class="ncspacer"></div>						

								<label><i18n:text key="search.holding.accession_number" /></label>: {$T.info.holding.accession_number}<br/>
								<label><i18n:text key="circulation.lending.lending_date" /></label>: {_d($T.info.lending.created,'f')}<br/>
								{#if $T.info.lending.expectedReturnDate}<label><i18n:text key="circulation.lending.expected_return_date" /></label>: {_d($T.info.lending.expectedReturnDate, 'D')}<br/>{#/if}

								{#if $T.info.lending.daysLate > 0}
									<div class="ncspacer"></div>
									<div class="ncspacer"></div>

									<label><i18n:text key="circulation.lending.days_late" /></label>: <span class="value_error">{ _f($T.info.lending.daysLate || 0) }</span><br/>
									<label><i18n:text key="circulation.lending.daily_fine" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.info.lending.dailyFine || 0, 'n2') }<br/>
									<label><i18n:text key="circulation.lending.estimated_fine" /></label>: <span class="value_error"><%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.info.lending.estimatedFine || 0, 'n2') }</span><br/>
								{#/if}
							</div>
							<div class="lending_buttons">
								{#if $T.info.lending}
									{#if $T.info.lending.daysLate > 0}
										<a class="button center disabled"><i18n:text key="circulation.lending.button.renew" /></a>
									{#else}
										<a class="button center" onclick="HoldingSearch.renewLending({#var $T.info});"><i18n:text key="circulation.lending.button.renew" /></a>
									{#/if}

									<a class="button center" onclick="HoldingSearch.returnLending({#var $T.info});"><i18n:text key="circulation.lending.button.return" /></a>
								{#/if}
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
					<a class="main_button arrow_right" onclick="CirculationSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
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
								<a class="button center" rel="open_item" onclick="CirculationSearch.openResult('{$T.record.id}');"><i18n:text key="circulation.lending.button.select_reader" /></a>
							</div>
							<div class="record">
								{#if $T.record.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.user.name}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.user.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.user.type_name}<br/>
								<div class="user_status_{$T.record.user.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.record.user.status)}</div>

								<div class="ncspacer"></div>
								<div class="ncspacer"></div>

								<label><i18n:text key="circulation.lending.lending_count" /></label>:  {($T.record.lendingInfo || {}).length || 0}
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
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="text">
				<i18n:text key="circulation.lending.holdings.title" />
			</div>

			<div class="clear"></div>
		</div>
				
		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="HoldingSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="HoldingSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="HoldingSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
	
		<div class="clear"></div>
	
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
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
		
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="wide_query">
					<input type="text" name="query" class="big_input" placeholder="<i18n:text key="search.holding.accession_number" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="HoldingSearch.search('simple');"><i18n:text key="search.common.button.list_all" /></a>
				</div>
			</div>
			<div class="filter_search">
				<div class="filter_checkbox">
					<input type="checkbox" name="holding_list_lendings" id="holding_list_lendings" value="true">
					<label class="search_label" for="holding_list_lendings"><i18n:text key="circulation.lendings.holding_list_lendings" /></label>
				</div>
			</div>
			<div class="clear_simple_search ico_clear mt10">
				<a href="javascript:void(0);" onclick="HoldingSearch.clearSimpleSearch();"><i18n:text key="search.common.clear_simple_search" /></a>
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
							<div class="buttons">
								{#if $T.record.lending}
									{#if $T.record.lending.daysLate > 0}										
										<a class="button center disabled"><i18n:text key="circulation.lending.button.renew" /></a>
									{#else}
										<a class="button center" onclick="HoldingSearch.renewLending({#var $T.record});"><i18n:text key="circulation.lending.button.renew" /></a>
									{#/if}
									
									<a class="button center" onclick="HoldingSearch.returnLending({#var $T.record});"><i18n:text key="circulation.lending.button.return" /></a>
								{#elseif $T.record.holding.availability == 'available'}
									<a class="button center" onclick="HoldingSearch.lend('{$T.record.holding.id}');"><i18n:text key="circulation.lending.button.lend" /></a>
								{#else}
									<a class="button disabled center"><i18n:text key="circulation.lending.button.unavailable" /></a>
								{#/if}
							</div>
							<div class="record">
								{#if $T.record.biblio.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.record.biblio.title}<br/>{#/if}
								{#if $T.record.biblio.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.record.biblio.author}<br/>{#/if}
								{#if $T.record.biblio.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.record.biblio.publication_year}<br/>{#/if}
								{#if $T.record.biblio.shelf_location || $T.record.holding.shelf_location || $T.record.holding.location_d}
									<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.record.holding.shelf_location || $T.record.biblio.shelf_location || ''} {$T.record.holding.location_d || ''}<br/>
								{#/if}
								{#if $T.record.biblio.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.record.biblio.isbn}<br/>{#/if}
								{#if $T.record.biblio.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.record.biblio.issn}<br/>{#/if}
								{#if $T.record.biblio.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.record.biblio.isrc}<br/>{#/if}

								<div class="ncspacer"></div>
								<div class="ncspacer"></div>						
							
								{#if $T.record.holding.accession_number}<label><i18n:text key="search.holding.accession_number" /></label>: {$T.record.holding.accession_number}<br/>{#/if}
								{#if $T.record.holding.availability}<label><i18n:text key="search.holding.availability" /></label>: {_('cataloging.holding.availability.' + $T.record.holding.availability)}<br/>{#/if}


								{#if ($T.record.lending == undefined) && ($T.record.biblio.holdings_reserved > 0) && ($T.record.biblio.holdings_reserved >= $T.record.biblio.holdings_available) && CirculationSearch.selectedRecord && ($.inArray($T.record.biblio.id, CirculationSearch.selectedRecord.reservedRecords) == -1)}
									<div class="ncspacer"></div>
									<div class="warn"><strong><i18n:text key="circulation.lending.reserved.warning" /></strong></div>
									<div class="ncspacer"></div>
								{#/if}
								
								{#if $T.record.lending !== undefined}
									<div class="user_lending">				
										<label><i18n:text key="circulation.lending.holding_lent_to_the_following_reader" />:</label><br/>
	
										<div class="ncspacer"></div>
										<div class="ncspacer"></div>


										{#if $T.record.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.user.name}<br/>{#/if}
										<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.user.enrollment}<br/>
										<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.user.type_name}<br/>

										<div class="ncspacer"></div>
										<div class="ncspacer"></div>

										<label><i18n:text key="circulation.lending.lending_date" /></label>: {_d($T.record.lending.created,'f')}<br/>
										{#if $T.record.lending.expectedReturnDate}<label><i18n:text key="circulation.lending.expected_return_date" /></label>: {_d($T.record.lending.expectedReturnDate, 'D')}<br/>{#/if}
	
										{#if $T.record.lending.daysLate > 0}
											<div class="ncspacer"></div>
											<div class="ncspacer"></div>
		
											<label><i18n:text key="circulation.lending.days_late" /></label>: <span class="value_error">{ _f($T.record.lending.daysLate || 0) }</span><br/>
											<label><i18n:text key="circulation.lending.daily_fine" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.record.lending.dailyFine || 0, 'n2') }<br/>
											<label><i18n:text key="circulation.lending.estimated_fine" /></label>: <span class="value_error"><%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.record.lending.estimatedFine || 0, 'n2') }</span><br/>
										{#/if}
									</div>
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
		
	<div id="fine_popup" class="popup">
		<div class="close" onclick="HoldingSearch.closeFinePopup();"><i18n:text key="common.close" /></div>

		<fieldset class="fine">
			<legend><i18n:text key="circulation.lending.fine_popup.title" /></legend>

			<div class="description">
				<p><i18n:text key="circulation.lending.fine_popup.description" /></p>
				<label><i18n:text key="circulation.lending.days_late" /></label>: <span class="days_late"></span><br/>
				<label><i18n:text key="circulation.lending.daily_fine" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> <span class="daily_fine"></span><br/>
				<label><i18n:text key="circulation.lending.fine_value" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> <input type="text" name="fine_value" /><br/>
			</div>

			<div class="buttons">
				<a class="button" onclick="HoldingSearch.applyFine();"><i18n:text key="circulation.lending.buttons.apply_fine" /></a>
				<a class="button" onclick="HoldingSearch.payFine();"><i18n:text key="circulation.lending.buttons.pay_fine" /></a>
				<a class="button" onclick="HoldingSearch.dismissFine();"><i18n:text key="circulation.lending.buttons.dismiss_fine" /></a>
			</div>
		</fieldset>
	</div>
</layout:body>
