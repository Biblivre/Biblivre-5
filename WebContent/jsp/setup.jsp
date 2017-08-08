<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="biblivre.administration.setup.DataMigrationBO"%>
<%@page import="biblivre.administration.setup.DataMigrationPhaseGroup"%>
<%@page import="biblivre.core.translations.TranslationsMap"%>
<%@ page import="biblivre.login.LoginDTO "%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.setup.css" />

	<script type="text/javascript" src="static/scripts/biblivre.administration.progress.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.setup.js"></script>
</layout:head>

<layout:body disableMenu="true" multiPart="true">
	<div class="page_help"><i18n:text key="administration.setup.page_help" /></div>

	<% String schema = (String) request.getAttribute("schema"); %>
	<% String force = StringUtils.defaultString(request.getParameter("force_setup"), "false"); %>	

	<div class="biblivre_form">
		<% if (force.equals("true")) { %>
			<fieldset>
				<legend><i18n:text key="administration.setup.cancel" /></legend>
				<div class="description"><i18n:text key="administration.setup.cancel.description" /></div>
				<div class="spacer"></div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="Administration.setup.cancel();"><i18n:text key="common.cancel" /></a>
				</div>
			</fieldset>	
		<% } %>

		<% if (DataMigrationBO.getInstance(schema, "biblivre3").isBiblivre3Available()) { %>
		<fieldset id="biblivre3import">
			<legend><i18n:text key="administration.setup.biblivre3import" /></legend>
			<div class="description"><i18n:text key="administration.setup.biblivre3import.description" /></div>
			<div class="spacer"></div>
			<div class="fields">
				<%
					TranslationsMap translations = (TranslationsMap) request.getAttribute("translationsMap");
					for (DataMigrationPhaseGroup phase : DataMigrationPhaseGroup.values()) {
						out.println("<div><input type=\"checkbox\" value=\"" + phase + "\" name=\"phases\" id=\"phase_" + phase + "\" / checked=\"checked\">&#160;<label for=\"phase_" + phase + "\">" + translations.getText("administration.migration.groups." + phase.toString()) + "</label></div>");
					}
				%>
			</div>
			<div class="spacer"></div>
			<div class="buttons">
				<a class="main_button arrow_right" onclick="Administration.setup.biblivre3Import();"><i18n:text key="administration.setup.biblivre3import.button" /></a>
			</div>
		</fieldset>	
		<% } %>

		<fieldset>
			<legend><i18n:text key="administration.setup.clean_install" /></legend>
			<div class="description"><i18n:text key="administration.setup.clean_install.description" /></div>
			<div class="spacer"></div>
			<div class="buttons">
				<a class="main_button arrow_right" onclick="Administration.setup.cleanInstall(this);"><i18n:text key="administration.setup.clean_install.button" /></a>
			</div>
		</fieldset>	

		<fieldset>
			<legend><i18n:text key="administration.setup.biblivre4restore" /></legend>
			<div class="description"><i18n:text key="administration.setup.biblivre4restore.description" /></div>
			<div class="spacer"></div>
				
			<div class="restore">
				<div class="found_backups">
					<p><strong><i18n:text key="administration.setup.biblivre4restore.title_found_backups" /></strong></p>
					<p><i18n:text key="administration.setup.biblivre4restore.description_found_backups_1" /></p>
		
					<div id="found_backups_list" class="found_backups_list"></div>
					<textarea id="found_backups_list_template" class="template"><!-- 
						{#if $T.restores == null || !$T.restores.length}
							<p><strong><i18n:text key="administration.setup.no_backups_found" /></strong></p>
						{#/if}
		
						{#foreach $T.restores as backup}
							<a class="backup {#if $T.backup$first}last_backup{#/if}" rel="{$T.backup.id}" onclick="Administration.setup.biblivre4Restore('{$T.backup.file}');">
		
								{_d($T.backup.created, 'd t')} - {_('administration.maintenance.backup.label_' + $T.backup.type)}
		
								{#if $T.backup$first}
									<div class="last_backup_description"><i18n:text key="administration.setup.biblivre4restore.newest_backup" /></div>
								{#/if}
							</a>
						{#/foreach}
					--></textarea>
				</div>
			</div>

			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.setup.biblivre4restore.field.upload_file" /></div>
					<div class="value">
						<input type="file" class="import_file" name="biblivre4backup"/>
					</div>
					<div class="clear"></div>
				</div>
			</div>

			<div class="buttons">
				<a class="main_button arrow_right" onclick="Administration.setup.biblivre4RestoreFromFile();"><i18n:text key="administration.setup.biblivre4restore.button" /></a>
			</div>
		</fieldset>	

		<fieldset class="digital_media_restore">
			<legend><i18n:text key="administration.setup.biblivre4restore.select_digital_media" /></legend>
			<div class="description"><i18n:text key="administration.setup.biblivre4restore.select_digital_media.description" /></div>
			<div class="spacer"></div>
				
			<div class="restore">
				<div id="found_media_backups_list" class="found_backups_list"></div>
				<textarea id="found_media_backups_list_template" class="template"><!-- 
					{#if $T.restores == null || !$T.restores.length}
						<p><strong><i18n:text key="administration.setup.no_backups_found" /></strong></p>
					{#/if}

					{#foreach $T.restores as backup}
						<a class="backup {#if $T.backup$first}last_backup{#/if}" rel="{$T.backup.id}" onclick="Administration.setup.biblivre4RestoreMedia('{$T.backup.file}');">
	
							{_d($T.backup.created, 'd t')} - {_('administration.maintenance.backup.label_' + $T.backup.type)}
	
							{#if $T.backup$first}
								<div class="last_backup_description"><i18n:text key="administration.setup.biblivre4restore.newest_backup" /></div>
							{#/if}
						</a>
					{#/foreach}
				--></textarea>
			</div>
						
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.setup.biblivre4restore.field.upload_file" /></div>
					<div class="value">
						<input type="file" class="import_file" name="biblivre4backupmedia"/>
					</div>
					<div class="clear"></div>
				</div>
			</div>

			<div class="buttons">
				<a class="danger_button" onclick="Administration.setup.cancelRestore();"><i18n:text key="common.cancel" /></a>

				<a class="button" onclick="Administration.setup.confirmBiblivre4Restore(Administration.setup.confirmBiblivre4RestoreFile, { skip: true });"><i18n:text key="administration.setup.biblivre4restore.skip" /></a>

				<a class="main_button arrow_right" onclick="Administration.setup.biblivre4RestoreFromFileMedia();"><i18n:text key="administration.setup.biblivre4restore.button" /></a>
			</div>
		</fieldset>	

		<fieldset id="biblivre3restore">
			<legend><i18n:text key="administration.setup.biblivre3restore" /></legend>
			<div class="description"><i18n:text key="administration.setup.biblivre3restore.description" /></div>
			<div class="spacer"></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.setup.biblivre3restore.field.upload_file" /></div>
					<div class="value">
						<input type="file" class="import_file" name="biblivre3backup"/>
					</div>
					<div class="clear"></div>
				</div>
			</div>
			<div class="spacer"></div>
			<div class="fields">
				<%
					TranslationsMap translations = (TranslationsMap) request.getAttribute("translationsMap");
					for (DataMigrationPhaseGroup phase : DataMigrationPhaseGroup.values()) {
						out.println("<div><input type=\"checkbox\" value=\"" + phase + "\" name=\"phases\" id=\"phase_" + phase + "\" / checked=\"checked\">&#160;<label for=\"phase_" + phase + "\">" + translations.getText("administration.migration.groups." + phase.toString()) + "</label></div>");
					}
				%>
			</div>
			<div class="spacer"></div>

			<div class="buttons">
				<a class="main_button arrow_right" onclick="Administration.setup.biblivre3ImportFromFile();"><i18n:text key="administration.setup.biblivre3restore.button" /></a>
			</div>
		</fieldset>	
	</div>
		
	<div id="upload_popup" class="popup">
		<fieldset class="upload">
			<legend><i18n:text key="administration.setup.upload_popup.title" /></legend>

			<div class="description">
				<p class="uploading"><i18n:text key="administration.setup.upload_popup.uploading" /></p>
				<p class="processing"><i18n:text key="administration.setup.upload_popup.processing" /></p>
			</div>

			<div class="progress">
				<div class="progress_text"><i18n:text key="common.wait" /></div>
				<div class="progress_bar">
					<div class="progress_bar_outer"><div class="progress_bar_inner"></div></div>
				</div>
			</div>
		</fieldset>
	</div>
	
	<div id="progress_popup" class="popup">
		<fieldset class="upload">
			<legend><i18n:text key="administration.setup.progress_popup.title" /></legend>

			<div class="description">
				<p class="processing"><i18n:text key="administration.setup.progress_popup.processing" /></p>
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
