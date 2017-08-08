<%@page import="biblivre.core.translations.Languages"%>
<%@page import="biblivre.core.translations.TranslationsMap"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.customization.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.administration.translations.js"></script>
</layout:head>

<layout:body multiPart="true">

	<input type="hidden" name="from_translations" value="true"/>
	
	<div class="page_help"><i18n:text key="administration.translations.page_help" /></div>
	
	<% String schema = (String) request.getAttribute("schema"); %>
	<div class="biblivre_form">
		<fieldset>
			<legend><i18n:text key="administration.translations.upload.title" /></legend>
			<div class="description"><i18n:text key="administration.translations.upload.description" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.translations.upload.field.user_created" /></div>
					<div class="value"><input type="checkbox" id="user_created" name="user_created" class="finput" style="width:auto;" checked="checked"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.translations.upload.field.upload_file" /></div>
					<div class="value">
						<input type="file" class="import_file" name="file"/>
					</div>
					<div class="clear"></div>
				</div>
				
				<div class="buttons">
					<a class="main_button arrow_right" onclick="Translation.upload(this);"><i18n:text key="administration.translations.upload.button" /></a>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>	
		<fieldset>
			<legend><i18n:text key="administration.translations.download.title" /></legend>
			<div class="description"><i18n:text key="administration.translations.download.description" /></div>
			<div class="spacer"></div>
			<div class="fields">
				<div>
					<div class="label"></div>
					<div class="value">
						<c:forEach var="language" items="<%= Languages.getLanguages(schema) %>">
							<a href="javascript:void(0);" onclick="Translation.dump('${language.language}');">${language.name}</a><br/>
						</c:forEach>
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>
		
		<fieldset>
			<legend><i18n:text key="administration.translations.edit.title" /></legend>
			<div class="description"><i18n:text key="administration.translations.edit.description" /></div>
			<div class="spacer"></div>
			<div class="fields">
				<div>
					<div class="label"></div>
					<div class="value">
						<c:forEach var="language" items="<%= Languages.getLanguages(schema) %>">
							<a href="javascript:void(0);" onclick="Translation.selectLanguage('${language.language}');">${language.name}</a><br/>
						</c:forEach>
					</div>
					<div class="clear"></div>
				</div>
			</div>
			<div class="translations_filter"><label><input type="checkbox" id="translations_filter" /> <i18n:text key="administration.translations.edit.filter" /></label></div>
			<div id="translations_tree"></div>
			<div class="buttons">
				<a class="main_button arrow_right" onclick="Translation.save();"><i18n:text key="administration.translations.save" /></a>
			</div>
		</fieldset>	
	</div>
	
	<div id="upload_popup" class="popup">
		<fieldset class="upload">
			<legend><i18n:text key="administration.translations.upload_popup.title" /></legend>

			<div class="description">
				<p class="uploading"><i18n:text key="administration.translations.upload_popup.uploading" /></p>
				<p class="processing"><i18n:text key="administration.translations.upload_popup.processing" /></p>
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
