<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.css" />
	
	<script type="text/javascript" src="static/scripts/biblivre.administration.maintenance.js"></script>
</layout:head>

<layout:body>
	<fieldset class="block backup">
		<legend><i18n:text key="administration.maintenance.backup.title" /></legend>
		<div class="description">
			<p><i18n:text key="administration.maintenance.backup.description.1" /></p>
			<ul>
				<li><i18n:text key="administration.maintenance.backup.description.2" /></li>
				<li><i18n:text key="administration.maintenance.backup.description.3" /></li>
				<li><i18n:text key="administration.maintenance.backup.description.4" /></li>
			</ul>
			<p><i18n:text key="administration.maintenance.backup.warning" /></p>
		</div>

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
	
	<fieldset class="block reindex">
		<legend><i18n:text key="administration.maintenance.reindex.title" /></legend>
		<div class="description">
			<p><i18n:text key="administration.maintenance.reindex.description.1" /></p>
			<ul>
				<li><i18n:text key="administration.maintenance.reindex.description.2" /></li>
				<li><i18n:text key="administration.maintenance.reindex.description.3" /></li>
				<li><i18n:text key="administration.maintenance.reindex.description.4" /></li>
			</ul>
			<p><i18n:text key="administration.maintenance.reindex.warning" /></p>
		</div>
		
		<div class="buttons">
			<a class="button main_button" onclick="Administration.reindex.confirm('biblio');"><i18n:text key="administration.maintenance.reindex.button_bibliographic" /></a>
			<a class="button main_button" onclick="Administration.reindex.confirm('authorities');"><i18n:text key="administration.maintenance.reindex.button_authorities" /></a>
			<a class="button main_button" onclick="Administration.reindex.confirm('vocabulary');"><i18n:text key="administration.maintenance.reindex.button_vocabulary" /></a>
		</div>
	</fieldset>

	<fieldset class="block reinstall">
		<legend><i18n:text key="administration.maintenance.reinstall.title" /></legend>
		<div class="description">
			<p><i18n:text key="administration.maintenance.reinstall.description" /></p>
		</div>
		
		<div class="buttons">
			<a class="button main_button" onclick="Administration.reinstall.confirm();"><i18n:text key="administration.maintenance.reinstall.button" /></a>
		</div>
	</fieldset>

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
	
	<div id="reindex_popup" class="popup">
		<div class="close" onclick="Administration.reindex.cancel();"><i18n:text key="common.close" /></div>

		<fieldset class="reindex">
			<legend><i18n:text key="administration.maintenance.reindex.title" /></legend>

			<div class="description">
				<p><i18n:text key="administration.maintenance.reindex.wait" /></p>
			</div>

			<div class="confirm">
				<p><i18n:text key="administration.maintenance.reindex.confirm" /></p>
			</div>
			
			<div class="buttons">
				<a class="button" onclick="Administration.reindex.cancel();"><i18n:text key="common.cancel" /></a>
				<a class="button main_button" onclick="Administration.reindex.submit();"><i18n:text key="common.ok" /></a>
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
