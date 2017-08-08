<%@page import="biblivre.core.configurations.Configurations"%>
<%@page import="biblivre.core.utils.Constants" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.acquisition.css" />

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.quotation.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.quotation.input.js"></script>
	
	<script type="text/javascript" src="static/scripts/zebra_datepicker.src.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">
	
	
	<script type="text/javascript">
		var QuotationSearch = CreateSearch(QuotationSearchClass, {
			type: 'acquisition.quotation',
			root: '#quotations',
			enableTabs: true,
			enableHistory: false
		});	
	
		QuotationInput.type = 'acquisition.quotation';
		QuotationInput.root = '#quotations';
		QuotationInput.search = QuotationSearch;		
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="acquisition.quotation.page_help" /></div>
	
	<div id="quotations">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.acquisition_quotation" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="QuotationInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="QuotationSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="QuotationSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="QuotationSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="QuotationInput.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="QuotationInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="QuotationInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="QuotationInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="QuotationInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="QuotationInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="QuotationInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>
			<div class="record">
				<label><i18n:text key="acquisition.quotation.field.supplier" /></label>: {$T.supplierName}<br/>
				<label><i18n:text key="common.created" /></label>: {#if $T.created}{_d($T.created, 'd')}{#/if}<br/>
				<label><i18n:text key="acquisition.quotation.field.id" /></label>: {$T.id}<br/>
				<div class="ncspacer"></div>
				<div class="ncspacer"></div>
				{#if $T.quotationsList && $T.quotationsList.length > 0}
					<label><i18n:text key="acquisition.quotation.fieldset.title.values" />:</label><br/>
					{#foreach $T.quotationsList as quotation}
						<div class="request_quotation">
							{#if $T.quotation.author}<label><i18n:text key="acquisition.quotation.title.author" /></label>: {$T.quotation.author}<br/>{#/if}
							{#if $T.quotation.title}<label><i18n:text key="acquisition.quotation.title.title" /></label>: {$T.quotation.title}<br/>{#/if}
							{#if $T.quotation.quantity}<label><i18n:text key="acquisition.quotation.title.quantity" /></label>: {$T.quotation.quantity}<br/>{#/if}
							{#if $T.quotation.unitValue}<label><i18n:text key="acquisition.quotation.title.unit_value" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.quotation.unitValue || 0, 'n2')}<br/>{#/if}
						</div>
					{#/for}
				{#/if}
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form"><i18n:text key="common.form" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_quotation_form_body"></div>
					<textarea id="biblivre_quotation_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.quotation.field.supplier" /></div>
							<div class="value">
								<select name="supplier">
		                            <option value=""><i18n:text key="acquisition.quotation.field.supplier_select"/></option>
									<c:forEach var="supplier" items="${suppliers}">
										<option value="${supplier.id}" {#if $T.supplierId == ${supplier.id}}selected{#/if}>${supplier.trademark}</option>
									</c:forEach>
		                        </select>
							</div>
							<div class="clear"></div>
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.quotation.field.quotation_date" /></div>
							<div class="value"><input type="text" name="quotation_date" class="datepicker" {#if $T.created}value="{_d($T.created, 'd')}"{#/if}></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.quotation.field.response_date" /></div>
							<div class="value"><input type="text" name="response_date" class="datepicker"  {#if $T.responseDate}value="{_d($T.responseDate, 'd')}"{#/if}></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.quotation.field.expiration_date" /></div>
							<div class="value"><input type="text" name="expiration_date" class="datepicker" {#if $T.expirationDate}value="{_d($T.expirationDate, 'd')}"{#/if}></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.quotation.field.delivery_time" /></div>
							<div class="value"><input type="text" name="delivery_time" value="{$T.deliveryTime}"></div>
							<div class="clear"></div>
						</div>
					--></textarea>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_quotation_form"></div>
					<textarea id="biblivre_quotation_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.quotation.fieldset.title.values" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.quotation.field.requisition" /></div>
									<div class="value">
										<select name="request" onchange="QuotationInput.setRequestQuantity(this.value);">
				                            <option value=""><i18n:text key="acquisition.quotation.field.requisition_select"/></option>
				                            <c:forEach var="request" items="${requests}">
												<option value="${request.id}">${request.author} - ${request.title}</option>
											</c:forEach>
				                        </select>
									</div>
									<div class="clear"></div>
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.quotation.field.quantity" /></div>
									<div class="value"><input type="text" name="quantity" value="{$T.quantity}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.quotation.field.unit_value" /></div>
									<div class="value"><input type="text" name="unit_value" value="{_f($T.unitValue || 0, 'n2')}"></div>
									<div class="clear"></div>	
								</div>
								<div class="buttons">
									<a class="main_button center" onclick="QuotationInput.addQuotation();"><i18n:text key="acquisition.quotation.button.add" /></a>
								</div>
							</div>
						</fieldset>
					--></textarea>
					<div class="selected_results_area">
					</div>
					<textarea class="selected_results_area_template template"><!--
						{#if $T.length > 0}
							<fieldset class="block">
								<legend>{_p('acquisition.quotation.selected_records', $T.length)}</legend>
								<ul>
									{#foreach $T as quotation}
										<li rel="{$T.quotation.id}">{$T.quotation.name} - {$T.quotation.quantity} (<%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.quotation.value || 0, 'n2')})<a class="xclose" onclick="QuotationInput.removeQuotation({$T.quotation.id});">&times;</a></li>
									{#/for}
								</ul>
							</fieldset>
						{/#if}
					--></textarea>
					
				</div>		
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_quotation_info_form"></div>
					<textarea id="biblivre_quotation_info_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.quotation.field.info" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.quotation.field.info" /></div>
									<div class="value"><input type="text" name="info" value="{$T.info}"></div>
									<div class="clear"></div>	
								</div>
							</div>
						</fieldset>
					--></textarea>
				</div>		
			</div>
		</div>
		
		<div class="search_box">
			<div class="simple_search submit_on_enter">
				<div class="wide_query">
					<input type="text" name="query" class="big_input auto_focus" placeholder="<i18n:text key="search.user.simple_term_title" />"/>
				</div>
				<div class="buttons">
					<a class="main_button arrow_right" onclick="QuotationSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
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
						<div class="result {#cycle values=['odd', 'even']} {QuotationInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{QuotationInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="QuotationSearch.openResult('{$T.record.id}');"><i18n:text key="common.open" /></a>
								<a class="danger_button center delete" onclick="QuotationInput.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.supplierName}<label><i18n:text key="acquisition.quotation.field.supplier" /></label>: {$T.record.supplierName}<br/>{#/if}
								{#if $T.record.created}<label><i18n:text key="common.created" /></label>: {_d($T.record.created, 'd')}<br/>{#/if}
								{#if $T.record.id}<label><i18n:text key="acquisition.quotation.field.id" /></label>: {$T.record.id}<br/>{#/if}
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>
								{#if $T.record.quotationsList && $T.record.quotationsList.length > 0}
									<label><i18n:text key="acquisition.quotation.fieldset.title.values" />:</label><br/>
									{#foreach $T.record.quotationsList as quotation}
										<div class="request_quotation">
											{#if $T.quotation.author}<label><i18n:text key="acquisition.quotation.title.author" /></label>: {$T.quotation.author}<br/>{#/if}
											{#if $T.quotation.title}<label><i18n:text key="acquisition.quotation.title.title" /></label>: {$T.quotation.title}<br/>{#/if}
											{#if $T.quotation.quantity}<label><i18n:text key="acquisition.quotation.title.quantity" /></label>: {$T.quotation.quantity}<br/>{#/if}
											{#if $T.quotation.unitValue}<label><i18n:text key="acquisition.quotation.title.unit_value" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.quotation.unitValue || 0, 'n2')}<br/>{#/if}
										</div>
									{#/for}
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
</layout:body>

