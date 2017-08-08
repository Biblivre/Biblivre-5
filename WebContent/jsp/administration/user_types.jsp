<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<!-- link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.z3950.css" /-->	

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.usertype.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.usertype.input.js"></script>
	<script type="text/javascript">
		var UserTypeSearch = CreateSearch(UserTypeSearchClass, {
			type: 'administration.usertype',
			root: '#user_types',
			enableTabs: true,
			enableHistory: false
		});	
	
		UserTypeInput.type = 'administration.usertype';
		UserTypeInput.root = '#user_types';
		UserTypeInput.search = UserTypeSearch;
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="administration.user_type.page_help" /></div>

	<div id="user_types">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.administration_user_types" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="UserTypeInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="UserTypeSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="UserTypeSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="UserTypeSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="UserTypeInput.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="UserTypeInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="UserTypeInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="UserTypeInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="UserTypeInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="UserTypeInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="UserTypeInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>			
			<div class="record">
				<label><i18n:text key="administration.user_type.field.name" /></label>: {$T.name}<br/>
				<label><i18n:text key="administration.user_type.field.description" /></label>: {$T.description}<br/>
				<label><i18n:text key="administration.user_type.field.lending_limit" /></label>: {$T.lendingLimit}<br/>
				<label><i18n:text key="administration.user_type.field.reservation_limit" /></label>: {$T.reservationLimit}<br/>
				<label><i18n:text key="administration.user_type.field.lending_time_limit" /></label>: {$T.lendingTimeLimit}<br/>
				<label><i18n:text key="administration.user_type.field.reservation_time_limit" /></label>: {$T.reservationTimeLimit}<br/>
				<label><i18n:text key="administration.user_type.field.fine_value" /></label>: {Globalize.format($T.fineValue, 'c')}<br/>
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form"><i18n:text key="common.form" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_usertype_form_body"></div>
					<textarea id="biblivre_usertype_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.name" /></div>
							<div class="value"><input type="text" name="name" maxlength="512" value="{$T.name}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.description" /></div>
							<div class="value"><input type="text" name="description" maxlength="512" value="{$T.description}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.lending_limit" /></div>
							<div class="value"><input type="text" name="lending_limit" maxlength="3" value="{$T.lendingLimit}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.reservation_limit" /></div>
							<div class="value"><input type="text" name="reservation_limit" maxlength="3" value="{$T.reservationLimit}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.lending_time_limit" /></div>
							<div class="value"><input type="text" name="lending_time_limit" maxlength="3" value="{$T.lendingTimeLimit}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.reservation_time_limit" /></div>
							<div class="value"><input type="text" name="reservation_time_limit" maxlength="3" value="{$T.reservationTimeLimit}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.user_type.field.fine_value" /></div>
							<div class="value"><input type="text" name="fine_value" maxlength="6" value="{Globalize.format($T.fineValue, 'n')}"></div>
							<div class="clear"></div>	
						</div>
					--></textarea>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="wide_query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="UserTypeSearch.search('simple');"><i18n:text key="search.common.button.filter" /></a>
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
						<div class="result {#cycle values=['odd', 'even']} {UserTypeInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{UserTypeInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="UserTypeSearch.openResult('{$T.record.id}');"><i18n:text key="common.open" /></a>
								<a class="danger_button center delete" onclick="UserTypeInput.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.name}<label><i18n:text key="administration.user_type.field.name" /></label>: {$T.record.name}<br/>{#/if}
								{#if $T.record.description}<label><i18n:text key="administration.user_type.field.description" /></label>: {$T.record.description}<br/>{#/if}
								{#if $T.record.lendingLimit || $T.record.lendingLimit === 0}<label><i18n:text key="administration.user_type.field.lending_limit" /></label>: {$T.record.lendingLimit}<br/>{#/if}
								{#if $T.record.reservationLimit || $T.record.reservationLimit === 0}<label><i18n:text key="administration.user_type.field.reservation_limit" /></label>: {$T.record.reservationLimit}<br/>{#/if}
								{#if $T.record.lendingTimeLimit || $T.record.lendingTimeLimit === 0}<label><i18n:text key="administration.user_type.field.lending_time_limit" /></label>: {$T.record.lendingTimeLimit}<br/>{#/if}
								{#if $T.record.reservationTimeLimit || $T.record.reservationTimeLimit === 0}<label><i18n:text key="administration.user_type.field.reservation_time_limit" /></label>: {$T.record.reservationTimeLimit}<br/>{#/if}
								{#if $T.record.fineValue || $T.record.fineValue === 0}<label><i18n:text key="administration.user_type.field.fine_value" /></label>: {Globalize.format($T.record.fineValue, 'c')}<br/>{#/if}
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

