<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.z3950.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.z3950.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.administration.z3950.input.js"></script>
	<script type="text/javascript">
		var Z3950Search = CreateSearch(Z3950SearchClass, {
			type: 'administration.z3950',
			root: '#z3950_servers',
			enableTabs: true,
			enableHistory: false
		});
	
		Z3950Input.type = 'administration.z3950';
		Z3950Input.root = '#z3950_servers';
		Z3950Input.search = Z3950Search;
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="administration.z3950.page_help" /></div>

	<div id="z3950_servers">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.administration_z3950_servers" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="Z3950Input.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="Z3950Search.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="Z3950Search.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="Z3950Search.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="Z3950Input.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="Z3950Input.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="Z3950Input.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="Z3950Input.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="Z3950Input.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="Z3950Input.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="Z3950Input.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>			
			<div class="record">
				<label><i18n:text key="administration.z3950.field.name" /></label>: {$T.name}<br/>
				<label><i18n:text key="administration.z3950.field.url" /></label>: {$T.url}<br/>
				<label><i18n:text key="administration.z3950.field.port" /></label>: {$T.port}<br/>
				<label><i18n:text key="administration.z3950.field.collection" /></label>: {$T.collection}<br/>
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form"><i18n:text key="common.form" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_z3950_form_body"></div>
					<textarea id="biblivre_z3950_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="administration.z3950.field.name" /></div>
							<div class="value"><input type="text" name="name" maxlength="512" value="{$T.name}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.z3950.field.url" /></div>
							<div class="value"><input type="text" name="url" maxlength="512" value="{$T.url}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.z3950.field.port" /></div>
							<div class="value"><input type="text" name="port" maxlength="5" value="{$T.port}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="administration.z3950.field.collection" /></div>
							<div class="value"><input type="text" name="collection" maxlength="512" value="{$T.collection}"></div>
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
					<input type="text" name="query" class="big_input auto_focus" />
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="Z3950Search.search('simple');"><i18n:text key="search.common.button.filter" /></a>
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
						<div class="result {#cycle values=['odd', 'even']} {Z3950Input.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{Z3950Input.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="Z3950Search.openResult('{$T.record.id}');"><i18n:text key="common.open" /></a>
								<a class="danger_button center delete" onclick="Z3950Input.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.name}<label><i18n:text key="administration.z3950.field.name" /></label>: {$T.record.name}<br/>{#/if}
								{#if $T.record.url}<label><i18n:text key="administration.z3950.field.url" /></label>: {$T.record.url}<br/>{#/if}
								{#if $T.record.port}<label><i18n:text key="administration.z3950.field.port" /></label>: {$T.record.port}<br/>{#/if}
								{#if $T.record.collection}<label><i18n:text key="administration.z3950.field.collection" /></label>: {$T.record.collection}<br/>{#/if}
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
