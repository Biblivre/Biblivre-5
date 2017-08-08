<%@page import="biblivre.core.configurations.Configurations"%>
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

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.input.js"></script>
	<script type="text/javascript" src="static/scripts/<%= UserFields.getFields((String) request.getAttribute("schema")).getCacheFileName() %>"></script>
	
	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">

	<script type="text/javascript" src="static/scripts/jquery.imgareaselect.js"></script>

	
	<script type="text/javascript">
		var CirculationSearch = CreateSearch(CirculationSearchClass, {
			type: 'circulation.user',
			prefix: 'circulation.user',
			root: '#circulation_user'
		});

		CirculationInput.type = 'circulation.user';
		CirculationInput.root = '#circulation_user';
		CirculationInput.search = CirculationSearch;
		

		$(document).ready(function() {
			var global = Globalize.culture().calendars.standard;

			$('#search_box input.datepicker').Zebra_DatePicker({
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

<%
	List<UserTypeDTO> userTypes = UserTypeBO.getInstance((String) request.getAttribute("schema")).list();
%>

<layout:body>
	<div class="page_help"><i18n:text key="circulation.user.page_help" /></div>

	<div id="circulation_user">
		<c:set var="user_field_prefix" value="<%= Constants.TRANSLATION_USER_FIELD %>" scope="page" />
			
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text contains_subtext">
				<i18n:text key="search.common.simple_search" />
				<div class="subtext"><i18n:text key="search.common.switch_to" /> <a href="#search=advanced"><i18n:text key="search.common.advanced_search" /></a></div>
			</div>
	
			<div class="advanced_search text contains_subtext">
				<i18n:text key="search.common.advanced_search" />
				<div class="subtext"><i18n:text key="search.common.switch_to" /> <a href="#search=simple"><i18n:text key="search.common.simple_search" /></a></div>
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="CirculationInput.newRecord();"><i18n:text key="circulation.user.button.new" /></a>
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
			<div class="buttons photo_buttons user_status_{$T.status}">
				<div class="view">
					<a class="button center" onclick="CirculationInput.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="CirculationInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
					<a class="danger_button center inactive" onclick="CirculationInput.deleteRecord('{$T.id}');"><i18n:text key="circulation.user.button.inactive" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="CirculationInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="CirculationInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="CirculationInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="CirculationInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="CirculationInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>
			{#if $T.photo_id}
				<img class="user_photo" src="DigitalMediaController/?id={$T.photo_id}"/>
			{#else}
				<img class="user_photo" src="static/images/photo.png"/>
			{#/if}
			<div class="record">
				{#if $T.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.name}<br/>{#/if}
				<label><i18n:text key="circulation.user_field.id" /></label>: {$T.enrollment}<br/>
				{#if $T.type}<label><i18n:text key="circulation.user_field.type" /></label>: {$T.type_name}<br/>{#/if}
				<div class="user_status_{$T.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.status)}</div>
	
				<div class="ncspacer"></div>
				<div class="ncspacer"></div>						
				{#if $T.created}<label><i18n:text key="common.created" /></label>: {_d($T.created, 'd t')}{#/if} 
				{#if $T.modified && $T.modified != $T.created}<br/><label><i18n:text key="common.modified" /></label>: {_d($T.modified, 'd t')}{#/if}
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form" onclick="Core.changeTab(this, CirculationSearch);"><i18n:text key="circulation.user.tabs.form" /></li>
				<li class="tab" data-tab="lendings" onclick="Core.changeTab(this, CirculationSearch);"><i18n:text key="circulation.user.tabs.lendings" /></li>
				<li class="tab" data-tab="reservations" onclick="Core.changeTab(this, CirculationSearch);"><i18n:text key="circulation.user.tabs.reservations" /></li>
				<li class="tab" data-tab="fines" onclick="Core.changeTab(this, CirculationSearch);"><i18n:text key="circulation.user.tabs.fines" /></li>
			</ul>
	
			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_circulation_form_body"></div>
					<textarea id="biblivre_circulation_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
	
						<div class="field">
							<div class="label"><i18n:text key="circulation.user_field.name" /></div>
							<div class="value"><input type="text" name="name" maxlength="512" value="{$.trim($T.name)}"></div>
							<div class="clear"></div>	
						</div>
	
						<div class="field">
							<div class="label"><i18n:text key="circulation.user_field.id" /></div>
							<div class="value"><div class="text">{$T.enrollment}</div></div>
							<div class="clear"></div>	
						</div>
	
						<div class="field">
							<div class="label"><i18n:text key="circulation.user_field.type" /></div>
							<div class="value">
								<select name="type">
									<c:forEach var="userType" items="<%= userTypes %>" >
										<option value="${userType.id}" {#if $T.type == '${userType.id}'}selected{#/if}>${userType.name}</option>
									</c:forEach>
								</select>						
							</div>
							<div class="clear"></div>	
						</div>

						<div class="field">
							<div class="label"><i18n:text key="circulation.user_field.status" /></div>
							<div class="value">
								<select name="status">
									<c:forEach var="status" items="<%= UserStatus.values() %>" >
										<option value="${status.string}" {#if $T.status == '${status.string}'}selected{#/if}><i18n:text key="circulation.user_status.${status.string}" /></option>
									</c:forEach>					
								</select>						
							</div>
							<div class="clear"></div>	
						</div>
						
						<div class="field photo_field">
							<div class="label"><i18n:text key="circulation.user_field.photo" /></div>
							<div>
								<input type="file" />
								<input type="hidden" name="photo_id" value="{$T.photo_id}">
							</div>
							<div class="clear"></div>	
						</div>
					--></textarea>
				</div>
				
				<div class="tab_body" data-tab="lendings">
					<div id="biblivre_circulation_lendings"></div>
					<textarea id="biblivre_circulation_lendings_template" class="template"><!-- 
						{#if $T.data && $T.data.length > 0}
							{#foreach $T.data as info}
								<div class="result user_lending {#if $T.info.lending.returnDate}user_lending_returned_lending{#else}user_lending_active_lending{#/if}" rel="{$T.info.lending.id}">
									<div class="record">
										{#if $T.info.biblio.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.info.biblio.title}<br/>{#/if}
										{#if $T.info.biblio.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.info.biblio.author}<br/>{#/if}
										{#if $T.info.biblio.publication_year}<label><i18n:text key="search.bibliographic.publication_year" /></label>: {$T.info.biblio.publication_year}<br/>{#/if}
										{#if $T.info.biblio.shelf_location || $T.info.holding.location_d}
											<label><i18n:text key="search.bibliographic.shelf_location" /></label>: {$T.info.biblio.shelf_location || ''} {$T.info.holding.location_d || ''}<br/>
										{#/if}
										{#if $T.info.biblio.isbn}<label><i18n:text key="search.bibliographic.isbn" /></label>: {$T.info.biblio.isbn}<br/>{#/if}
										{#if $T.info.biblio.issn}<label><i18n:text key="search.bibliographic.issn" /></label>: {$T.info.biblio.issn}<br/>{#/if}
										{#if $T.info.biblio.isrc}<label><i18n:text key="search.bibliographic.isrc" /></label>: {$T.info.biblio.isrc}<br/>{#/if}
		
										<div class="ncspacer"></div>
										<div class="ncspacer"></div>						
		
										<label><i18n:text key="search.holding.accession_number" /></label>: {$T.info.holding.accession_number}<br/>
										<label><i18n:text key="circulation.lending.lending_date" /></label>: {_d($T.info.lending.created,'f')}<br/>
										{#if $T.info.lending.returnDate}
											<label><i18n:text key="circulation.lending.return_date" /></label>: {_d($T.info.lending.returnDate, 'f')}<br/>
										{#else}
											<label><i18n:text key="circulation.lending.expected_return_date" /></label>: {_d($T.info.lending.expectedReturnDate, 'D')}<br/>

											{#if $T.info.lending.daysLate > 0}
												<div class="ncspacer"></div>
												<div class="ncspacer"></div>
			
												<label><i18n:text key="circulation.lending.days_late" /></label>: <span class="value_error">{ _f($T.info.lending.daysLate || 0) }</span><br/>
												<label><i18n:text key="circulation.lending.daily_fine" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.info.lending.dailyFine || 0, 'n2') }<br/>
												<label><i18n:text key="circulation.lending.estimated_fine" /></label>: <span class="value_error"><%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.info.lending.estimatedFine || 0, 'n2') }</span><br/>
											{#/if}
										{#/if}		
									</div>
									<div class="lending_buttons">
										{#if $T.info.lending.returnDate}
											<a class="button center" onclick="CirculationSearch.printReceipt('{$T.info.lending.id}');"><i18n:text key="circulation.lending.button.print_return_receipt" /></a>
										{#else}
											<a class="button center" onclick="CirculationSearch.printReceipt('{$T.info.lending.id}');"><i18n:text key="circulation.lending.button.print_lending_receipt" /></a>
										{#/if}		
									</div>
									<div class="clear"></div>
								</div>
							{#/for}
						{#else}
							<p><i18n:text key="circulation.user.no_lendings" /></p>
						{#/if}
					--></textarea>
				</div>
				<div class="clear"></div>
				
				<div class="tab_body" data-tab="reservations">
					<div id="biblivre_circulation_reservations"></div>
					<textarea id="biblivre_circulation_reservations_template" class="template"><!-- 
						{#if $T.data[0].reservationInfoList && $T.data[0].reservationInfoList.length > 0}
							{#foreach $T.data[0].reservationInfoList as info}
								<div class="result user_reservation" rel="{$T.info.id}">
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
									<div class="clear"></div>
								</div>
							{#/for}
						{#else}
							<p><i18n:text key="circulation.user.no_reserves" /></p>
						{#/if}
					--></textarea>
				</div>
				<div class="clear"></div>
				
				<div class="tab_body" data-tab="fines">
					<div id="biblivre_circulation_fines"></div>
					<textarea id="biblivre_circulation_fines_template" class="template"><!-- 
						{#if $T.data && $T.data.length > 0}
							{#foreach $T.data as info}
								<div class="result user_fines" rel="{$T.info.id}">
									<div class="record">
										{#if $T.info.title}<label><i18n:text key="search.bibliographic.title" /></label>: {$T.info.title}<br/>{#/if}
										{#if $T.info.author}<label><i18n:text key="search.bibliographic.author" /></label>: {$T.info.author}<br/>{#/if}
										<label><i18n:text key="circulation.lending.fine_value" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.info.value || 0, 'n2')}<br/>
										
										{#if $T.info.payment}<label><i18n:text key="circulation.lending.payment_date" /></label>: {_d($T.info.payment, 'D')}<br/>
										{#else}
											<div class="description">
												<p><i18n:text key="circulation.user.fine.pending" /></p>
											</div>
										{#/if}
									</div>
									{#if !$T.info.payment}
										<div class="fines_buttons">
											<a class="button center" onclick="CirculationSearch.payFine('{$T.info.id}', false);"><i18n:text key="circulation.lending.buttons.pay_fine" /></a>
											<a class="button center" onclick="CirculationSearch.payFine('{$T.info.id}', true);"><i18n:text key="circulation.lending.buttons.dismiss_fine" /></a>
										</div>
									{#/if}
									<div class="clear"></div>
								</div>
							{#/for}
						{#else}
							<p><i18n:text key="circulation.user.no_fines" /></p>
						{#/if}
					--></textarea>
				</div>
				<div class="clear"></div>
			</div>
			
	
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_circulation_form"></div>
					<textarea id="biblivre_circulation_form_template" class="template"><!-- 
						<fieldset>
							<div class="fields">
								{#foreach CirculationInput.userFields as userfield}
									<div class="field">
										<div class="label">{_('circulation.custom.user_field.' + $T.userfield.key)}</div>
										<div class="value">
											{#if $T.userfield.type == 'string'}
												<input type="text" name="{$T.userfield.key}" maxlength="{$T.userfield.maxLength || ''}" value="{($T.fields || {})[$T.userfield.key]}">
											{#/if}
											{#if $T.userfield.type == 'text'}
												{$('<textarea/>').attr('name', $T.userfield.key).text(($T.fields || {})[$T.userfield.key] || '')[0].outerHTML}
											{#/if}
											{#if $T.userfield.type == 'list' && $T.userfield.maxLength > 0}
												<select name="{$T.userfield.key}">
													{#if !$T.userfield.required}<option value="">{_('circulation.custom.user_field.select.default')}</option>{#/if}
													{#for index = 1 to $T.userfield.maxLength}
														<option value="{$T.index}" {#if ($T.fields && $T.fields[$T.userfield.key] && $T.fields[$T.userfield.key] == $T.index)}selected="selected"{#/if}>{_('circulation.custom.user_field.' + $T.userfield.key + '.' + $T.index)}</option> 
													{#/for}
												</select>
											{#/if}
											{#if $T.userfield.type == 'date' || $T.userfield.type == 'datetime'}
												<input type="text" class="datepicker" name="{$T.userfield.key}" maxlength="{$T.userfield.maxLength || ''}" value="{($T.fields || {})[$T.userfield.key]}">
											{#/if}
											{#if $T.userfield.type == 'boolean'}
												<input type="checkbox" name="{$T.userfield.key}" value="true" {#if ($T.fields && $T.fields[$T.userfield.key] && $T.fields[$T.userfield.key] == 'true')}checked="checked"{#/if} style="width: auto;">
											{#/if}
										</div>
										<div class="clear"></div>
									</div>
								{#/for}							
							</div>
						</fieldset>
					--></textarea>
				</div>		
			</div>		
			
			<div class="footer_buttons">
				<div class="edit">
					<a class="main_button center" onclick="CirculationInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="CirculationInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="CirculationInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="CirculationInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="CirculationInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>	
		</div>
	
	
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
			
			<div class="advanced_search submit_on_enter">
				<div class="query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="filter_search">
					<div class="fleft filter_field">
						<label class="search_label"><i18n:text key="search.user.field" /></label>
						<select name="field" class="combo combo_expand">
							<option value=""><i18n:text key="search.user.name_or_id" /></option>
							<c:forEach var="field" items="<%= UserFields.getSearchableFields((String) request.getAttribute(\"schema\")) %>" >
								<option value="${field.key}"><i18n:text key="${user_field_prefix}${field.key}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.registered_between" /></label>
						<input type="text" name="created_start" class="small_input datepicker" />-<input type="text" name="created_end" class="small_input datepicker" />
					</div>
					<div class="fleft filter_date">
						<label class="search_label"><i18n:text key="search.common.modified_between" /></label>
						<input type="text" name="modified_start" class="small_input datepicker" />-<input type="text" name="modified_end" class="small_input datepicker" />
					</div>
					<div class="clear"></div>
					<div class="filter_checkbox">
						<input type="checkbox" name="users_with_pending_fines" id="users_with_pending_fines" value="true">
						<label class="search_label" for="users_with_pending_fines"><i18n:text key="circulation.user.users_with_pending_fines" /></label>
					</div>
					<div class="filter_checkbox">
						<input type="checkbox" name="users_with_late_lendings" id="users_with_late_lendings" value="true">
						<label class="search_label" for="users_with_late_lendings"><i18n:text key="circulation.user.users_with_late_lendings" /></label>
					</div>
					<div class="filter_checkbox">
						<input type="checkbox" name="users_who_have_login_access" id="users_who_have_login_access" value="true">
						<label class="search_label" for="users_who_have_login_access"><i18n:text key="circulation.user.users_who_have_login_access" /></label>
					</div>
					<div class="filter_checkbox">
						<input type="checkbox" name="users_without_user_card" id="users_without_user_card" value="true">
						<label class="search_label" for="users_without_user_card"><i18n:text key="circulation.user.users_without_user_card" /></label>
					</div>
					<div class="filter_checkbox">
						<input type="checkbox" name="inactive_users_only" id="inactive_users_only" value="true">
						<label class="search_label" for="inactive_users_only"><i18n:text key="circulation.user.inactive_users_only" /></label>
					</div>
				</div>

				<div class="fleft clear_search ico_clear">
					<a href="javascript:void(0);" onclick="CirculationSearch.clearAdvancedSearch();"><i18n:text key="search.common.clear_search" /></a>
				</div>

				<div class="buttons">
					<a class="main_button arrow_right" onclick="CirculationSearch.search('advanced');"><i18n:text key="search.common.button.list_all" /></a>
					<div class="clear"></div>
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
						<div class="result {#cycle values=['odd', 'even']}" rel="{$T.record.id}">
							<div class="buttons user_status_{$T.record.status}">
								<a class="button center" rel="open_item" onclick="CirculationSearch.openResult('{$T.record.id}');"><i18n:text key="search.user.open_item_button" /></a>
								{#if $T.record.status != 'blocked'}
									<a class="button center" rel="block_user" onclick="CirculationSearch.blockUser('{$T.record.id}');"><i18n:text key="circulation.user.button.block" /></a>
								{#else}
									<a class="button center" rel="unblock_user" onclick="CirculationSearch.unblockUser('{$T.record.id}');"><i18n:text key="circulation.user.button.unblock" /></a>
								{#/if}
							</div>
							<div class="record">
								{#if $T.record.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.name}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.record.type_name}<br/>
								<div class="user_status_{$T.record.status}"><label><i18n:text key="circulation.user_field.status" /></label>: {_('circulation.user_status.' + $T.record.status)}</div>
	
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>						
								<label><i18n:text key="common.created" /></label>: {_d($T.record.created, 'd t')} {#if $T.record.modified && $T.record.modified != $T.record.created}&#160;&#160;&#160;<label><i18n:text key="common.modified" /></label>: {_d($T.record.modified, 'd t')}{#/if}
							</div>
							<div class="clear"></div>
						</div>
					{#/for}
				--></textarea>
			</div>
			
			<div class="paging_bar"></div>		
		</div>
	</div>	

	<div id="photo_upload_popup" class="popup">
		<div class="close" onclick="CirculationInput.closePhotoUploadPopup();"><i18n:text key="common.close" /></div>

		<fieldset>
			<legend><i18n:text key="circulation.user_field.photo" /></legend>

			<div id="photo_area"></div>

			<div class="progress">
				<div class="progress_bar">
					<div class="progress_bar_outer"><div class="progress_bar_inner"></div></div>
				</div>
			</div>
			
			<div class="buttons">
				<a class="button" onclick="CirculationInput.closePhotoUploadPopup();"><i18n:text key="common.cancel" /></a>
				<a class="button main_button" onclick="CirculationInput.applyPhotoSelection();"><i18n:text key="common.ok" /></a>
			</div>			
		</fieldset>
	</div>
</layout:body>
