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
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.order.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.order.input.js"></script>
	
	<script type="text/javascript" src="static/scripts/zebra_datepicker.js"></script>
	<link rel="stylesheet" type="text/css" href="static/styles/zebra.bootstrap.css">
	
	
	<script type="text/javascript">
		var OrderSearch = CreateSearch(OrderSearchClass, {
			type: 'acquisition.order',
			root: '#orders',
			enableTabs: true,
			enableHistory: false
		});	
	
		OrderInput.type = 'acquisition.order';
		OrderInput.root = '#orders';
		OrderInput.search = OrderSearch;		
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="acquisition.order.page_help" /></div>
	
	<div id="orders">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.acquisition_order" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="OrderInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="OrderSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="OrderSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="OrderSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="OrderInput.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="OrderInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="OrderInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="OrderInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="OrderInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="OrderInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="OrderInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>
			<div class="record">
				{#if $T.supplierName}<label><i18n:text key="acquisition.order.field.supplier" /></label>: {$T.supplierName}<br/>{#/if}
				{#if $T.created}<label><i18n:text key="common.created" /></label>: {_d($T.created, 'd')}<br/>{#/if}
				{#if $T.id}<label><i18n:text key="acquisition.order.field.id" /></label>: {$T.id}<br/>{#/if}
				<div class="ncspacer"></div>
				<div class="ncspacer"></div>
				{#if $T.quotationsList && $T.quotationsList.length > 0}
					<label><i18n:text key="acquisition.order.fieldset.title.values" />:</label><br/>
					{#foreach $T.quotationsList as quotation}
						<div class="order_quotation">
							{#if $T.quotation.author}<label><i18n:text key="acquisition.order.title.author" /></label>: {$T.quotation.author}<br/>{#/if}
							{#if $T.quotation.title}<label><i18n:text key="acquisition.order.title.title" /></label>: {$T.quotation.title}<br/>{#/if}
							{#if $T.quotation.quantity}<label><i18n:text key="acquisition.order.title.quantity" /></label>: {$T.quotation.quantity}<br/>{#/if}
							{#if $T.quotation.unitValue}<label><i18n:text key="acquisition.order.title.unit_value" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.quotation.unitValue || 0, 'n2')}<br/>{#/if}
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
					<div id="biblivre_order_form_body"></div>
					<textarea id="biblivre_order_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.order.field.supplier" /></div>
							<div class="value">
								<select name="supplier" onchange="OrderInput.listQuotations(this.value);">
		                            <option value=""><i18n:text key="acquisition.order.field.supplier_select"/></option>
									<c:forEach var="supplier" items="${suppliers}">
										<option value="${supplier.id}" {#if $T.supplierId == ${supplier.id}}selected{#/if}>${supplier.trademark}</option>
									</c:forEach>
		                        </select>
							</div>
							<div class="clear"></div>
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.order.field.quotation" /></div>
							<div class="value">
								<select id="quotation" name="quotation" onchange="OrderInput.updateQuotationList(this.value);">
		                            <option value=""><i18n:text key="acquisition.order.field.quotation_select"/></option>
		                        </select>
							</div>
							<div class="clear"></div>
						</div>						
						<div class="field">
							<div class="label"><i18n:text key="acquisition.order.field.created" /></div>
							<div class="value"><input type="text" name="created" class="datepicker" {#if $T.created}value="{_d($T.created, 'd')}"{#/if}></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.order.field.deadline_date" /></div>
							<div class="value"><input type="text" name="deadline_date" class="datepicker"  {#if $T.deadlineDate}value="{_d($T.deadlineDate, 'd')}"{#/if}></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.order.field.delivery_time" /></div>
							<div class="value"><input type="text" name="delivery_time" value="{$T.deliveryTime}" disabled="disabled"></div>
							<div class="clear"></div>
						</div>
					--></textarea>
					<div class="selected_results_area">
					</div>
					<textarea class="selected_results_area_template template"><!--
						{#if $T.length > 0}
							<fieldset class="block">
								<legend>{_p('acquisition.order.selected_records', $T.length)}</legend>
								<ul>
									{#foreach $T as order}
										<li rel="{$T.order.id}">{$T.order.name} - {$T.order.quantity} (<%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.order.value || 0, 'n2')})</li>
									{#/for}
								</ul>
							</fieldset>
						{/#if}
					--></textarea>					
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_order_form"></div>
					<textarea id="biblivre_order_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.order.fieldset.title.values" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.delivered" /></div>
									<div class="value"><input type="checkbox" name="delivered" onchange="OrderInput.toggleDeliveryInputs(this.checked);" style="width: auto;" {#if $T.status == 'closed'}checked="checked"{#/if}></div>
									<div class="clear"></div>
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.invoice_number" /></div>
									<div class="value"><input type="text" name="invoice_number" value="{$T.invoiceNumber}" disabled="disabled"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.receipt_date" /></div>
									<div class="value"><input type="text" name="receipt_date" class="datepicker" {#if $T.receiptDate}value="{_d($T.receiptDate, 'd')}"{#/if} disabled="disabled"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.total_value" /></div>
									<div class="value"><input type="text" name="total_value" value="{_f($T.totalValue || 0, 'n2')}" disabled="disabled"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.delivered_quantity" /></div>
									<div class="value"><input type="text" name="delivered_quantity" value="{$T.deliveredQuantity}" disabled="disabled"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.terms_of_payment" /></div>
									<div class="value"><input type="text" name="terms_of_payment" value="{$T.termsOfPayment}" disabled="disabled"></div>
									<div class="clear"></div>	
								</div>
							</div>
						</fieldset>
					--></textarea>
				</div>		
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_order_info_form"></div>
					<textarea id="biblivre_order_info_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.order.field.info" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.order.field.info" /></div>
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
					<a class="main_button arrow_right" onclick="OrderSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
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
						<div class="result {#cycle values=['odd', 'even']} {OrderInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{OrderInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="OrderSearch.openResult('{$T.record.id}');"><i18n:text key="common.open" /></a>
								<a class="danger_button center delete" onclick="OrderInput.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.supplierName}<label><i18n:text key="acquisition.order.field.supplier" /></label>: {$T.record.supplierName}<br/>{#/if}
								{#if $T.record.created}<label><i18n:text key="common.created" /></label>: {_d($T.record.created, 'd')}<br/>{#/if}
								{#if $T.record.id}<label><i18n:text key="acquisition.order.field.id" /></label>: {$T.record.id}<br/>{#/if}
								<div class="ncspacer"></div>
								<div class="ncspacer"></div>
								{#if $T.record.quotationsList && $T.record.quotationsList.length > 0}
									<label><i18n:text key="acquisition.order.fieldset.title.values" />:</label><br/>
									{#foreach $T.record.quotationsList as quotation}
										<div class="order_quotation">
											{#if $T.quotation.author}<label><i18n:text key="acquisition.order.title.author" /></label>: {$T.quotation.author}<br/>{#/if}
											{#if $T.quotation.title}<label><i18n:text key="acquisition.order.title.title" /></label>: {$T.quotation.title}<br/>{#/if}
											{#if $T.quotation.quantity}<label><i18n:text key="acquisition.order.title.quantity" /></label>: {$T.quotation.quantity}<br/>{#/if}
											{#if $T.quotation.unitValue}<label><i18n:text key="acquisition.order.title.unit_value" /></label>: <%= Configurations.getString((String) request.getAttribute("schema"), Constants.CONFIG_CURRENCY) %> {_f($T.quotation.unitValue || 0, 'n2')}<br/>{#/if}
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

