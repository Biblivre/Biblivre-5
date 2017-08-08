<%@page import="biblivre.core.auth.AuthorizationSchemaScope"%>
<%@page import="biblivre.core.auth.AuthorizationUserScope"%>
<%@page import="biblivre.core.translations.TranslationsMap"%>
<%@page import="biblivre.core.translations.Translations"%>
<%@page import="biblivre.core.translations.TranslationDTO"%>
<%@page import="biblivre.core.auth.AuthorizationPointGroups"%>
<%@page import="biblivre.core.auth.AuthorizationPointTypes"%>
<%@page import="biblivre.circulation.user.UserFields"%>
<%@page import="biblivre.core.utils.Constants"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.circulation.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.circulation.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.permissions.js"></script>

	<script type="text/javascript">
		var CirculationSearch = CreateSearch(PermissionsSearchClass, {
			type: 'administration.permissions',
			root: '#circulation_search',
			paginateAction: 'search',
			autoSelect: false,
			enableTabs: true,
			enableHistory: false,
			defaultTab: 'login'
		});

		PermissionsInput.type = 'administration.permissions';
		PermissionsInput.root = '#circulation_user';
		PermissionsInput.search = CirculationSearch;
	</script>
</layout:head>

<layout:body>
	<c:set var="user_field_prefix" value="<%= Constants.TRANSLATION_USER_FIELD %>" scope="page" />
	<div class="page_help"><i18n:text key="administration.permissions.page_help" /></div>

	<div id="circulation_search">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="text">
				<i18n:text key="administration.permissions.title" />
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
				{#if $T.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.user.name}<br/>{#/if}
				{#if $T.login}<label><i18n:text key="circulation.user_field.login" /></label>: {$T.login.login}<br/>{#/if}
				<label><i18n:text key="circulation.user_field.id" /></label>: {$T.user.enrollment}<br/>
				<label><i18n:text key="circulation.user_field.type" /></label>: {$T.user.type_name}<br/>
			</div>
		--></textarea>
		
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="login"><i18n:text key="administration.permissions.login_data" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="login">
					<div id="biblivre_circulation_permissions_form"></div>
					<textarea id="biblivre_circulation_permissions_form_template" class="template"><!-- 
						<input type="hidden" name="user_id" value="{$T.id}"/>
						
						<div class="hidden chrome_bug">
							<input type="text">
							<input type="password">
						</div>
	
						<div class="field">
							<div class="label"><i18n:text key="administration.permissions.login" /></div>
							<div class="value">
								{#if $T.login}
									{$T.login.login}
								{#else}
									<input type="text" name="new_login" autocomplete="off" maxlength="512" value="">
								{#/if}
							</div>
							<div class="clear"></div>
						</div>
	
						<div class="field">
							<div class="label"><i18n:text key="administration.permissions.password" /></div>
							<div class="value"><input type="password" name="new_password" autocomplete="off" maxlength="512"></div>
							<div class="clear"></div>
						</div>
	
						<div class="field">
							<div class="label"><i18n:text key="administration.permissions.repeat_password" /></div>
							<div class="value"><input type="password" name="repeat_password" autocomplete="off" maxlength="512"></div>
							<div class="clear"></div>
						</div>
						
						<div class="field">
							<div class="label"><i18n:text key="administration.permissions.employee" /></div>
							<select name="employee" onchange="Core.toggleAreas('permissions_area', this.value)">
								<option value=""><i18n:text key="administration.permissions.select.default" /></option>
								<option value="true" {#if $T.login.employee == 'true'}selected="selected"{#/if}><i18n:text key="administration.permissions.employee" /></option>
								<option value="false" {#if $T.login.employee == 'false'}selected="selected"{#/if}><i18n:text key="administration.permissions.reader" /></option>
							</select>
							<div class="clear"></div>
						</div>
					--></textarea>					
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" id="permissions" data-tab="login">
					<div class="permissions_area" data="true">
					<%
						TranslationsMap translations = (TranslationsMap) request.getAttribute("translationsMap");
						AuthorizationPointGroups lastGroup = null;
						for (AuthorizationPointTypes atp : AuthorizationPointTypes.values()) {
							if (atp.isPublic() || atp.isPublicForLoggedUsers()) {
								continue;
							}
							
							if (atp.getUserScope() == AuthorizationUserScope.READER) {
								continue;
							}
							
							if (atp.getSchemaScope() == AuthorizationSchemaScope.GLOBAL_SCHEMA) {
								continue;
							}

	
							if (!atp.getGroup().equals(lastGroup)) {
								if (lastGroup != null) {
									out.println("</div></fieldset>");
								}
	
								lastGroup = atp.getGroup();
	
								out.println("<fieldset>");
								out.println("<legend><input type=\"checkbox\" onclick=\"PermissionsInput.checkAllPermissions(this);\" class=\"legend\" style=\"vertical-align: middle\"/>&#160;" + translations.getText("administration.permissions.groups." + lastGroup.toString().toLowerCase()) + "</legend>");
								out.println("<div class=\"fields\">");
							}
	
							out.println("<div><input type=\"checkbox\" value=\"" + atp + "\" name=\"employee_permissions\" id=\"permission_" + atp + "\" />&#160;<label for=\"permission_" + atp + "\">" + translations.getText("administration.permissions.items." + atp.toString().toLowerCase()) + "</label></div>");
						}
	
						if (lastGroup != null) {
							out.println("</fieldset>");
							lastGroup = null;
						}
					%>
					</div>
					<div class="permissions_area" data="false">
					<%
						for (AuthorizationPointTypes atp : AuthorizationPointTypes.values()) {
							if (atp.isPublic() || atp.isPublicForLoggedUsers()) {
								continue;
							}
							
							if (atp.getUserScope() == AuthorizationUserScope.EMPLOYEE) {
								continue;
							}

							if (atp.getSchemaScope() == AuthorizationSchemaScope.GLOBAL_SCHEMA) {
								continue;
							}
	
							if (!atp.getGroup().equals(lastGroup)) {
								if (lastGroup != null) {
									out.println("</div></fieldset>");
								}
	
								lastGroup = atp.getGroup();
	
								out.println("<fieldset>");
								out.println("<legend><input type=\"checkbox\" onclick=\"PermissionsInput.checkAllPermissions(this);\" class=\"legend\" style=\"vertical-align: middle\"/>&#160;" + translations.getText("administration.permissions.groups." + lastGroup.toString().toLowerCase()) + "</legend>");
								out.println("<div class=\"fields\">");
							}
	
							out.println("<div><input type=\"checkbox\" value=\"" + atp + "\" name=\"reader_permissions\" id=\"permission_" + atp + "\" />&#160;<label for=\"permission_" + atp + "\">" + translations.getText("administration.permissions.items." + atp.toString().toLowerCase()) + "</label></div>");
						}
	
						if (lastGroup != null) {
							out.println("</fieldset>");
						}
					%>
					</div>
				</div>		
			</div>
			<div class="footer_buttons">
				<a class="main_button center" onclick="PermissionsInput.saveRecord();"><i18n:text key="common.save" /></a>
				<a class="danger_button center delete" onclick="PermissionsInput.deleteRecord(CirculationSearch.selectedRecord.id);"><i18n:text key="administration.permissions.button.remove_login" /></a>
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
					<a class="main_button arrow_right" onclick="CirculationSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
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
							<div class="buttons user_status_{$T.record.user.status}">
								<a class="button center" rel="open_item" onclick="CirculationSearch.openResult('{$T.record.id}');"><i18n:text key="administration.permissions.button.select_user" /></a>
							</div>
							<div class="record">
								{#if $T.record.user.name}<label><i18n:text key="circulation.user_field.name" /></label>: {$T.record.user.name}<br/>{#/if}
								{#if $T.record.login}<label><i18n:text key="circulation.user_field.login" /></label>: {$T.record.login.login}<br/>{#/if}
								<label><i18n:text key="circulation.user_field.id" /></label>: {$T.record.user.enrollment}<br/>
								<label><i18n:text key="circulation.user_field.type" /></label>: {$T.user.type_name}<br/>
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
