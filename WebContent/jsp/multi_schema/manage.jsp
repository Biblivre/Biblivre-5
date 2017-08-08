<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@page import="biblivre.core.schemas.SchemaDTO"%>
<%@page import="biblivre.core.schemas.Schemas"%>
<%@page import="biblivre.core.translations.Languages"%>
<%@page import="biblivre.core.translations.TranslationsMap"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<script type="text/javascript" src="static/scripts/biblivre.multi_schema.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.multi_schema.css" />
</layout:head>

<layout:body multiPart="true">

	<div class="page_help"><i18n:text key="multi_schema.manage.page_help" /></div>

	<div class="biblivre_form">
		<fieldset class="multischema" id="multischema">
			<legend><i18n:text key="multi_schema.manage.schemas.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.manage.schemas.description" /></div>
			<div class="spacer"></div>
			<% for (SchemaDTO schema : Schemas.getSchemas()) { %>
				<div class="library <% if (schema.isDisabled()) { %>disabled<% } %>">
					<a href="<%= schema.getSchema() %>/" target="_blank"><%= Configurations.getHtml(schema.getSchema(), Constants.CONFIG_TITLE) %></a>
					<div class="subtitle"><%= Configurations.getHtml(schema.getSchema(), Constants.CONFIG_SUBTITLE) %></div>
					<div><span class="address"></span><strong><%= schema.getSchema() %></strong>/</div>
					<a href="javascript:void(0);" onclick="Schemas.toggle('<%= schema.getSchema() %>', false, this);" class="enable">[<i18n:text key="multi_schema.manage.enable" />]</a>
					<a href="javascript:void(0);" onclick="Schemas.deleteSchema('<%= schema.getSchema() %>', this);" class="enable">[<i18n:text key="common.delete" />]</a>
					<a href="javascript:void(0);" onclick="Schemas.toggle('<%= schema.getSchema() %>', true, this);" class="disable">[<i18n:text key="multi_schema.manage.disable" />]</a>
				</div>
			<% } %>
		</fieldset>	
		
		<fieldset>
			<legend><i18n:text key="multi_schema.manage.new_schema.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.manage.new_schema.description" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="multi_schema.manage.new_schema.field.title" /></div>
					<div class="value"><input type="text" id="schema_title" name="title" maxlength="60" class="finput"/></div>
					<div class="clear"></div>
				</div>

				<div>
					<div class="label"><i18n:text key="multi_schema.manage.new_schema.field.subtitle" /></div>
					<div class="value"><input type="text" name="subtitle" class="finput"/></div>
					<div class="clear"></div>
				</div>

				<div>
					<div class="label"><i18n:text key="multi_schema.manage.new_schema.field.schema" /></div>
					<div class="value"><span id="address"></span><input type="text" id="schema_schema" name="schema" maxlength="60" class="finput"/></div>
					<div class="clear"></div>
				</div>
				
				<div class="buttons">
					<a class="main_button arrow_right" onclick="Schemas.create(this);"><i18n:text key="multi_schema.manage.new_schema.button.create" /></a>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>	
		
	</div>
			
</layout:body>
