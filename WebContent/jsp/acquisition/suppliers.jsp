<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.supplier.search.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.input.js"></script>
	<script type="text/javascript" src="static/scripts/biblivre.acquisition.supplier.input.js"></script>
	<script type="text/javascript">
		var SupplierSearch = CreateSearch(SupplierSearchClass, {
			type: 'acquisition.supplier',
			root: '#suppliers',
			enableTabs: true,
			enableHistory: false
		});	
	
		SupplierInput.type = 'acquisition.supplier';
		SupplierInput.root = '#suppliers';
		SupplierInput.search = SupplierSearch;
	</script>
</layout:head>

<layout:body>
	<div class="page_help"><i18n:text key="acquisition.supplier.page_help" /></div>
	
	<div id="suppliers">
		<div class="page_title">
			<div class="image"><img src="static/images/titles/search.png" /></div>
	
			<div class="simple_search text">
				<i18n:text key="menu.acquisition_supplier" />
			</div>

			<div class="buttons">
				<a class="button center new_record_button" onclick="SupplierInput.newRecord();"><i18n:text key="cataloging.bibliographic.button.new" /></a>
			</div>
			
			<div class="clear"></div>
		</div>

		<div class="page_navigation">
			<a href="javascript:void(0);" class="button paging_button back_to_search" onclick="SupplierSearch.closeResult();"><i18n:text key="search.common.back_to_search" /></a>

			<div class="fright">
				<a href="javascript:void(0);" class="button paging_button paging_button_prev" onclick="SupplierSearch.previousResult();"><i18n:text key="search.common.previous" /></a>
				<span class="search_count"></span>
				<a href="javascript:void(0);" class="button paging_button paging_button_next" onclick="SupplierSearch.nextResult();"><i18n:text key="search.common.next" /></a>
			</div>

			<div class="clear"></div>
		</div>
		
		<div class="selected_highlight"></div>
		<textarea class="selected_highlight_template template"><!-- 
			<div class="buttons">
				<div class="view">
					<a class="button center" onclick="SupplierInput.editRecord('{$T.id}');"><i18n:text key="common.edit" /></a>
					<a class="danger_button center delete" onclick="SupplierInput.deleteRecord('{$T.id}');"><i18n:text key="common.delete" /></a>
				</div>
	
				<div class="edit">
					<a class="main_button center" onclick="SupplierInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="SupplierInput.saveRecord(true);"><i18n:text key="common.save_as_new" /></a>
					<a class="button center" onclick="SupplierInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
				
				<div class="new">
					<a class="main_button center" onclick="SupplierInput.saveRecord();"><i18n:text key="common.save" /></a>
					<a class="button center" onclick="SupplierInput.cancelEdit();"><i18n:text key="common.cancel" /></a>
				</div>
			</div>
			<div class="record">
				{#if $T.trademark}<label><i18n:text key="acquisition.supplier.field.trademark" /></label>: {$T.trademark}<br/>{#/if}
				{#if $T.name}<label><i18n:text key="acquisition.supplier.field.name" /></label>: {$T.name}<br/>{#/if}
				{#if $T.supplierNumber}<label><i18n:text key="acquisition.supplier.field.supplier_number" /></label>: {$T.supplierNumber}<br/>{#/if}
				{#if $T.vatRegistrationNumber}<label><i18n:text key="acquisition.supplier.field.vat_registration_number" /></label>: {$T.vatRegistrationNumber}<br/>{#/if}
				{#if $T.city}<label><i18n:text key="acquisition.supplier.field.city" /></label>: {$T.city}<br/>{#/if}
			</div>
		--></textarea>
	
		<div class="selected_record tabs">
			<ul class="tabs_head">
				<li class="tab" data-tab="form"><i18n:text key="common.form" /></li>
			</ul>

			<div class="tabs_body">
				<div class="tab_body biblivre_form_body" data-tab="form">
					<div id="biblivre_supplier_form_body"></div>
					<textarea id="biblivre_supplier_form_body_template" class="template"><!-- 
						<input type="hidden" name="id" value="{$T.id}"/>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.trademark" /></div>
							<div class="value"><input type="text" name="trademark" value="{$T.trademark}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.name" /></div>
							<div class="value"><input type="text" name="supplier_name" value="{$T.name}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.supplier_number" /></div>
							<div class="value"><input type="text" name="supplier_number" value="{$T.supplierNumber}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.vat_registration_number" /></div>
							<div class="value"><input type="text" name="vat_registration_number" value="{$T.vatRegistrationNumber}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.address" /></div>
							<div class="value"><input type="text" name="address" value="{$T.address}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.address_number" /></div>
							<div class="value"><input type="text" name="address_number" value="{$T.addressNumber}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.complement" /></div>
							<div class="value"><input type="text" name="complement" value="{$T.complement}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.area" /></div>
							<div class="value"><input type="text" name="area" value="{$T.area}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.city" /></div>
							<div class="value"><input type="text" name="city" value="{$T.city}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.state" /></div>
							<div class="value"><input type="text" name="state" value="{$T.state}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.country" /></div>
							<div class="value"><input type="text" name="country" value="{$T.country}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.zip_code" /></div>
							<div class="value"><input type="text" name="zip_code" value="{$T.zipCode}"></div>
							<div class="clear"></div>	
						</div>
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.url" /></div>
							<div class="value"><input type="text" name="url" value="{$T.url}"></div>
							<div class="clear"></div>	
						</div>						
						<div class="field">
							<div class="label"><i18n:text key="acquisition.supplier.field.email" /></div>
							<div class="value"><input type="text" name="email" value="{$T.email}"></div>
							<div class="clear"></div>	
						</div>
					--></textarea>
				</div>
				<div class="clear"></div>
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_supplier_form"></div>
					<textarea id="biblivre_supplier_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.supplier.fieldset.contact" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><input type="text" name="contact_1" value="{$T.contact1}"></div>
									<div class="value"><input type="text" name="telephone_1" value="{$T.telephone1}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><input type="text" name="contact_2" value="{$T.contact2}"></div>
									<div class="value"><input type="text" name="telephone_2" value="{$T.telephone2}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><input type="text" name="contact_3" value="{$T.contact3}"></div>
									<div class="value"><input type="text" name="telephone_3" value="{$T.telephone3}"></div>
									<div class="clear"></div>	
								</div>
								<div class="field">
									<div class="label"><input type="text" name="contact_4" value="{$T.contact4}"></div>
									<div class="value"><input type="text" name="telephone_4" value="{$T.telephone4}"></div>
									<div class="clear"></div>	
								</div>
							</div>
						</fieldset>
					--></textarea>
				</div>		
			</div>
			
			<div class="tabs_extra_content">
				<div class="tab_extra_content biblivre_form" data-tab="form">
					<div id="biblivre_supplier_info_form"></div>
					<textarea id="biblivre_supplier_info_form_template" class="template"><!-- 
						<fieldset>
							<legend><i18n:text key="acquisition.supplier.field.info" /></legend>
							<div class="fields">
								<div class="field">
									<div class="label"><i18n:text key="acquisition.supplier.field.info" /></div>
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
					<a class="main_button arrow_right" onclick="SupplierSearch.search('simple');"><i18n:text key="search.common.button.search" /></a>
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
						<div class="result {#cycle values=['odd', 'even']} {SupplierInput.getOverlayClass($T.record)}" rel="{$T.record.id}">
							<div class="result_overlay"><div class="text">{SupplierInput.getOverlayText($T.record)}</div></div>
							<div class="buttons">
								<a class="button center" rel="open_item" onclick="SupplierSearch.openResult('{$T.record.id}');"><i18n:text key="common.open" /></a>
								<a class="danger_button center delete" onclick="SupplierInput.deleteRecord('{$T.record.id}');"><i18n:text key="common.delete" /></a>
							</div>
							<div class="record">
								{#if $T.record.trademark}<label><i18n:text key="acquisition.supplier.field.trademark" /></label>: {$T.record.trademark}<br/>{#/if}
								{#if $T.record.name}<label><i18n:text key="acquisition.supplier.field.name" /></label>: {$T.record.name}<br/>{#/if}
								{#if $T.record.supplierNumber}<label><i18n:text key="acquisition.supplier.field.supplier_number" /></label>: {$T.record.supplierNumber}<br/>{#/if}
								{#if $T.record.vatRegistrationNumber}<label><i18n:text key="acquisition.supplier.field.vat_registration_number" /></label>: {$T.record.vatRegistrationNumber}<br/>{#/if}
								{#if $T.record.city}<label><i18n:text key="acquisition.supplier.field.city" /></label>: {$T.record.city}<br/>{#/if}
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

