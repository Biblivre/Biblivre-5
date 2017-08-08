<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@page import="biblivre.core.schemas.SchemaDTO"%>
<%@page import="org.json.JSONObject"%>
<%@page import="biblivre.administration.backup.BackupBO"%>
<%@page import="biblivre.core.schemas.Schemas"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.multi_schema.css" />

	<script type="text/javascript" src="static/scripts/biblivre.administration.maintenance.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.progress.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.setup.js"></script>

	<script type="text/javascript" src="static/scripts/biblivre.multi_schema.js"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#multischema .library').on('click', '.title, .subtitle', function() {
				var ck = $(this).siblings('.checkbox').find('input');
				ck.prop('checked', !ck.prop('checked'));
			});
			
			$('#schemas_match').setTemplateElement('schemas_match_template');
		});
		
		<%
			JSONObject schemas = new JSONObject();
			for (String schema : Schemas.getEnabledSchemasList()) {
				schemas.put(schema, true);
			}
		%>
		
		Schemas.loadedSchemas = <%= schemas.toString() %>;
		
		Administration.setup.multiLibrary = true;
	</script>
</layout:head>

<layout:body multiPart="true">

	<!--div class="page_help"><i18n:text key="multi_schema.backup.page_help" /></div-->

	<div class="biblivre_form">
		<fieldset class="multischema" id="multischema">
			<legend><i18n:text key="multi_schema.backup.schemas.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.backup.schemas.description" /></div>
			<div class="spacer"></div>
			<% for (SchemaDTO schema : Schemas.getSchemas()) { %>
				<% if (schema.isDisabled()) { continue; } %>

				<div class="library">
					<div class="checkbox"><input type="checkbox" name="library" checked="checked" value="<%= schema.getSchema() %>" /></div>
					<div class="title"><%= Configurations.getHtml(schema.getSchema(), Constants.CONFIG_TITLE) %></div>
					<div class="subtitle"><%= Configurations.getHtml(schema.getSchema(), Constants.CONFIG_SUBTITLE) %></div>
					<div><span class="address"></span><strong><%= schema.getSchema() %></strong>/</div>
				</div>
			<% } %>
	
			<div class="buttons">
				<a class="button main_button" onclick="Administration.backup.submit('full');"><i18n:text key="administration.maintenance.backup.button_full" /></a>
				<a class="button main_button" onclick="Administration.backup.submit('exclude_digital_media');"><i18n:text key="administration.maintenance.backup.button_exclude_digital_media" /></a>
				<a class="button main_button" onclick="Administration.backup.submit('digital_media_only');"><i18n:text key="administration.maintenance.backup.button_digital_media_only" /></a>
			</div>
			
			<div class="last_backups">
				<p><strong><i18n:text key="administration.maintenance.backup.title_last_backups" /></strong></p>
				<p><i18n:text key="administration.maintenance.backup.description_last_backups_1" /></p>
				<p><i18n:text key="administration.maintenance.backup.description_last_backups_2" /></p>
	
				<div id="last_backups_list" class="last_backups_list">
				</div>
				<textarea id="last_backups_list_template" class="template"><!-- 
					{#if $T.backups == null || !$T.backups.length}
						<p><strong><i18n:text key="administration.maintenance.backup.no_backups_found" /></strong></p>
					{#/if}
	
					{#foreach $T.backups as backup}
						{#if $T.backup$index == 5}
							<div class="expand" onclick="Administration.backup.showAll(this);">{_('administration.maintenance.backup.show_all', [$T.backups.length])}</div>
						{#/if}
					
						<a class="backup {#if $T.backup$first}last_backup{#/if} {#if $T.backup$index > 4}hidden_backup{#/if}" rel="{$T.backup.id}" href="?controller=download&module=administration.backup&action=download&id={$T.backup.id}" target="_blank">
	
							{_d($T.backup.created, 'd t')} - {_('administration.maintenance.backup.label_' + $T.backup.type)}
	
							{#if $T.backup.steps != $T.backup.current_step}
								<div class="backup_never_downloaded"><i18n:text key="administration.maintenance.backup.backup_not_complete" /></div>
							{#elseif !$T.backup.exists}
								<div class="backup_never_downloaded"><i18n:text key="administration.maintenance.backup.unavailable" /></div>
							{#elseif !$T.backup.downloaded}
								<div class="backup_never_downloaded"><i18n:text key="administration.maintenance.backup.backup_never_downloaded" /></div>
							{#/if}
	
							{#if $T.backup$first}
								<div class="last_backup_description"><i18n:text key="administration.maintenance.backup.last_backup" /></div>
							{#/if}
						</a>		
					{#/foreach}
				--></textarea>
			</div>
		</fieldset>
		
		<fieldset class="multischema" id="multischema_select_restore">
			<legend><i18n:text key="multi_schema.select_restore.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.select_restore.description" /></div>
			<div class="spacer"></div>
				
			<div class="restore">
				<div class="found_backups">
					<p><strong><i18n:text key="administration.setup.biblivre4restore.title_found_backups" /></strong></p>
					<p><i18n:text key="multi_schema.select_restore.description_found_backups" param1="<%= BackupBO.getInstance(\"global\").getBackupPath() %>" /></p>
		
					<div id="found_backups_list" class="found_backups_list"></div>
					<textarea id="found_backups_list_template" class="template"><!-- 
						{#if $T.restores == null || !$T.restores.length}
							<p><strong><i18n:text key="administration.setup.no_backups_found" /></strong></p>
						{#/if}
		
						{#foreach $T.restores as backup}
							{#if $T.backup.valid}
								<a class="backup {#if $T.backup$first}last_backup{#/if}" rel="{$T.backup.id}" onclick="Schemas.restore({#var $T.backup});">
									{_d($T.backup.created, 'd t')} - {_('administration.maintenance.backup.label_' + $T.backup.type)}<br/>
									
									{#if $T.backup$first}
										<div class="last_backup_description"><i18n:text key="administration.setup.biblivre4restore.newest_backup" /></div>
									{#/if}
								</a>

								<div class="backup_schemas">
									<strong><i18n:text key="multi_schema.select_restore.library_list_inside_backup" />:</strong> {$T.backup.schemas_list}
								</div>
							{#/if}
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


		<fieldset class="multischema" id="multischema_full_restore">
			<legend><i18n:text key="multi_schema.restore.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.restore.restore_complete_backup.description" /></div>

			<div class="buttons">
				<a class="button main_button" onclick="Schemas.fullRestore();"><i18n:text key="multi_schema.restore.restore_complete_backup.title" /></a>
			</div>
		</fieldset>

		<fieldset class="multischema" id="multischema_partial_restore">
			<legend><i18n:text key="multi_schema.restore.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.restore.restore_partial_backup.description" /></div>
		</fieldset>		

		<fieldset class="multischema" id="multischema_restore_limit">
			<legend><i18n:text key="multi_schema.restore.limit.title" /></legend>
			<div class="description"><i18n:text key="multi_schema.restore.limit.description" /></div>
			<ul></ul>
		</fieldset>

		<div id="schemas_match"></div>
		<textarea id="schemas_match_template" class="template"><!-- 
			{#foreach $T.schemas as schema}
				{#if $T.schema$key != 'global'}
					<fieldset class="library">
						<legend>{$T.schema$index + 1}. {$T.schema.left}</legend>
						<div class="subtitle">{$T.schema.right}</div>

						<div class="spacer"></div>

						<div class="original_schema">
							<input type="radio" name="restore_library_{$T.schema$key}" id="restore_library_original_{$T.schema$key}" value="original" checked="checked">
							<label for="restore_library_original_{$T.schema$key}"><i18n:text key="multi_schema.restore.restore_with_original_schema_name" /></label><br/>
							<span class="address"></span><strong>{$T.schema$key}</strong>/

							{#if Schemas.loadedSchemas[$T.schema$key]}
								<div class="warn"><i18n:text key="multi_schema.restore.warning_overwrite" /></div>
							{#/if}
						</div>

						<div class="spacer"></div>
						
						<div class="new_schema">
							<input type="radio" name="restore_library_{$T.schema$key}" id="restore_library_new_{$T.schema$key}" value="new">
							<label for="restore_library_new_{$T.schema$key}"><i18n:text key="multi_schema.restore.restore_with_new_schema_name" /></label><br/>
							<span class="address"></span><strong><input type="text" name="schema_name_{$T.schema$key}" class="finput"/></strong>/
							<div class="warn"><i18n:text key="multi_schema.restore.warning_overwrite" /></div>								
						</div>

						<div class="spacer"></div>

						<div class="dont_restore">
							<input type="radio" name="restore_library_{$T.schema$key}" id="restore_library_skip_{$T.schema$key}" value="skip">
							<label for="restore_library_skip_{$T.schema$key}"><i18n:text key="multi_schema.restore.dont_restore" /></label> 
						</div>
					</fieldset>
				{#/if}
			{#/foreach}
		--></textarea>
		
		<div class="buttons" id="partial_restore_button">
			<a class="button main_button" onclick="Schemas.partialRestore();"><i18n:text key="multi_schema.restore.restore_partial_backup.title" /></a>
		</div>
	</div>
			
	<div id="backup_popup" class="popup">
		<div class="close" onclick="Administration.backup.cancel();"><i18n:text key="common.close" /></div>

		<fieldset class="backup">
			<legend><i18n:text key="administration.maintenance.backup.title" /></legend>

			<div class="progress">
				<div class="progress_text"><i18n:text key="common.wait" /></div>
				<div class="progress_bar">
					<div class="progress_bar_outer"><div class="progress_bar_inner"></div></div>
				</div>
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
