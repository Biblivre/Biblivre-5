<%@ page import="biblivre.cataloging.Fields" %>
<%@ page import="biblivre.marc.MaterialType" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.customization.css" />	

	<script type="text/javascript" src="static/scripts/biblivre.administration.form_customization.js"></script>
	
	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "biblio").getCacheFileName() %>"></script>
	<script>FormCustomization.formFields['bibliographic'] = ld.keyBy(CatalogingInput.formFields, 'datafield');</script>
	
	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "authorities").getCacheFileName() %>"></script>
	<script>FormCustomization.formFields['authorities'] = ld.keyBy(CatalogingInput.formFields, 'datafield');</script>
	
	<script type="text/javascript" src="static/scripts/<%= Fields.getFormFields((String) request.getAttribute("schema"), "vocabulary").getCacheFileName() %>"></script>
	<script>FormCustomization.formFields['vocabulary'] = ld.keyBy(CatalogingInput.formFields, 'datafield');</script>
	
	<script>FormCustomization.materialTypes = <%= MaterialType.toJavascriptArray()%>;</script>
	
</layout:head>

<layout:body multiPart="true">

	<div class="page_help"><i18n:text key="administration.form_customization.page_help" /></div>
	<div id="form_customization">
		<fieldset class="block">
			<legend><i18n:text key="administration.brief_customization.select_record_type" /></legend>
	
			<div class="record_type_selection">
				<select id="record_type_select" name="record_type_field" class="combo combo_auto_size">
					<option value="bibliographic"><i18n:text key="administration.brief_customization.biblio" /></option>
					<option value="authorities"><i18n:text key="administration.brief_customization.authorities" /></option>
					<option value="vocabulary"><i18n:text key="administration.brief_customization.vocabulary" /></option>
				</select>
			</div>
			
			<div class="buttons">
				<a class="main_button center" onclick="FormCustomization.addDatafield();"><i18n:text key="administration.form_customization.button_add_field" /></a>
			</div>
		</fieldset>
		
		<div id="datafields"></div>
		<textarea id="datafields_template" class="template"><!-- 
			{#foreach $T.datafields as datafield}
				<fieldset class="block" data-datafield="{ $T.datafield.datafield }">
					<legend>{$T.datafield.datafield} - { _('marc.bibliographic.datafield.' + $T.datafield.datafield) }</legend>
					<div class="buttons">
						<span class="cancel-datafield"><i class="fa fa-close"></i></span>
						<span class="save-datafield"><i class="fa fa-check"></i></span>
						<span class="trash-datafield"><i class="fa fa-trash-o"></i></span>
						<span class="edit-datafield"><i class="fa fa-pencil"></i></span>
						<span class="move-datafield"><i class="fa fa-bars"></i></span>
					</div>
					<div class="edit_area"></div>
				</fieldset>
			{#/for}
		--></textarea>
		<textarea id="datafields_edit_template" class="template"><!--
		 	<div class="content">
		 		<table class="datafield_table">
					<thead>
						<tr>
							<th><i18n:text key="administration.form_customization.field" /></th>
							<th><i18n:text key="administration.form_customization.field_name" /></th>
							<th><i18n:text key="administration.form_customization.field_repeatable" /></th>
							<th><i18n:text key="administration.form_customization.field_collapsed" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="text" class="datafield_tag" name="datafield_tag" maxlength="3" value="{ $T.datafieldTag }"/></td>
							<td><input type="text" class="datafield_name" name="datafield_name" value="{ $T.datafieldTag ? _('marc.bibliographic.datafield.' + $T.datafieldTag) : '' }"/></td>
							<td class="checkbox"><input type="checkbox" name="datafield_repeatable" {#if $T.datafield.repeatable}checked="checked"{#/if} /></td>
							<td class="checkbox"><input type="checkbox" name="datafield_collapsed" {#if $T.datafield.collapsed}checked="checked"{#/if} /></td>
						</tr>
					</tbody>
				</table>

				<table class="indicator_table">
					<thead>
						<tr>
							<th><i18n:text key="administration.form_customization.indicator_number" /></th>
							<th><i18n:text key="administration.form_customization.indicator_name" /></th>
							<th><i18n:text key="administration.form_customization.indicator_values" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><label><input type="checkbox" name="indicator_1_enabled" {#if $T.datafield.indicator1}checked="checked"{#/if} /> #1</label></td>
							<td><input type="text" class="indicator_name" name="indicator_1_name" {#if $T.datafield.indicator1}value="{ _('marc.bibliographic.datafield.' + $T.datafieldTag + '.indicator.1') }"{#/if} /></td>
							<td>
								<select name="indicator_1_values" class="combo indicator_values">
									{#foreach $T.datafield.indicator1 as indicator}
										<option value="{ $T.indicator }">{ _('marc.bibliographic.datafield.' + $T.datafieldTag + '.indicator.1.' + $T.indicator) }</option>
									{#/for}
								</select>
								<a href="javascript:void(0);" onclick="FormCustomization.editIndicator(this)"><i class="fa fa-fw fa-pencil"></i></a>
							</td>
						</tr>
						<tr>
							<td><label><input type="checkbox" name="indicator_2_enabled" {#if $T.datafield.indicator2}checked="checked"{#/if} /> #2</label></td>
							<td><input type="text" class="indicator_name" name="indicator_2_name" {#if $T.datafield.indicator2}value="{ _('marc.bibliographic.datafield.' + $T.datafieldTag + '.indicator.2') }"{#/if} /></td>
							<td>
								<select name="indicator_2_values" class="combo indicator_values">
									{#foreach $T.datafield.indicator2 as indicator}
										<option value="{ $T.indicator }">{ _('marc.bibliographic.datafield.' + $T.datafieldTag + '.indicator.2.' + $T.indicator) }</option>
									{#/for}
								</select>
								<a href="javascript:void(0);" onclick="FormCustomization.editIndicator(this)"><i class="fa fa-fw fa-pencil"></i></a>
							</td>
						</tr>
					</tbody>
				</table>	
	
				<div class="title"><i18n:text key="administration.form_customization.material_type" /></div>
				<ul class="material-types">
				{#foreach FormCustomization.materialTypes as materialType}
					<li>
						<label>
							<input type="checkbox" name="{$T.materialType}" {#if $T.datafield.material_type.indexOf($T.materialType) > -1}checked="checked"{#/if} />
							{ _('marc.material_type.' + $T.materialType) }
						</label>
					</li>							
				{#/for}
				</ul>
				
				<div class="title"><i18n:text key="administration.form_customization.subfields" /></div>
				<table class="subfield_table">
					<thead>
						<tr>
							<th class="tr_icon">&nbsp</th>
							<th class="tr_marc"><i18n:text key="administration.form_customization.subfield" /></th>
							<th><i18n:text key="administration.form_customization.subfield_name" /></th>
							<th class="center"><i18n:text key="administration.form_customization.subfield_repeatable" /></th>
							<th class="center"><i18n:text key="administration.form_customization.subfield_collapsed" /></th>
							<th><i18n:text key="administration.form_customization.subfield_autocomplete.label" /></th>
							<th class="tr_icon">&nbsp</th>
						</tr>
					</thead>
					<tbody>
						<tr class="subfield_template hidden">
							<td class="tr_icon"><a href="javascript:void(0);" class="move-subfield"><i class="fa fa-bars"></i></a></td>
							<td class="tr_marc"><input type="text" class="subfield_tag" name="subfield_tag" maxlength="1" value=""/></td>
							<td><input type="text" class="subfield_name" name="subfield_name" value=""/></td>
							<td class="checkbox"><input type="checkbox" name="subfield_repeatable" /></td>
							<td class="checkbox"><input type="checkbox" name="subfield_collapsed" /></td>
							<td>
								<select name="sufield_autocomplete" class="combo indicator_values">
									<option value="disabled">{ _('administration.form_customization.subfield_autocomplete.disabled') }</option>
									<option value="previous_values">{ _('administration.form_customization.subfield_autocomplete.previous_values') }</option>
									<option value="fixed_table">{ _('administration.form_customization.subfield_autocomplete.fixed_table') }</option>
									<option value="fixed_table_with_previous_values">{ _('administration.form_customization.subfield_autocomplete.fixed_table_with_previous_values') }</option>
									<option value="biblio">{ _('administration.form_customization.subfield_autocomplete.biblio') }</option>
									<option value="authorities">{ _('administration.form_customization.subfield_autocomplete.authorities') }</option>
									<option value="vocabulary">{ _('administration.form_customization.subfield_autocomplete.vocabulary') }</option>
								</select>
							</td>
							<td class="tr_icon"><a href="javascript:void(0);" class="remove-subfield" onclick="FormCustomization.removeSubfield(this)"><i class="fa fa-times"></i></a></td>
						</tr>
						{#foreach $T.datafield.subfields as subfield}
							<tr class="subfield_row">
								<td class="tr_icon"><a href="javascript:void(0);" class="move-subfield"><i class="fa fa-bars"></i></a></td>
								<td class="tr_marc"><input type="text" class="subfield_tag" name="subfield_tag" maxlength="1" value="{ $T.subfield.subfield }"/></td>
								<td><input type="text" class="subfield_name" name="subfield_name" value="{ _('marc.bibliographic.datafield.' + $T.datafieldTag + '.subfield.' + $T.subfield.subfield) }"/></td>
								<td class="checkbox"><input type="checkbox" name="subfield_repeatable" {#if $T.subfield.repeatable}checked="checked"{#/if} /></td>
								<td class="checkbox"><input type="checkbox" name="subfield_collapsed" {#if $T.subfield.collapsed}checked="checked"{#/if} /></td>
								<td>
									<select name="sufield_autocomplete" class="combo indicator_values">
										<option value="disabled" {#if $T.subfield.autocomplete_type == 'disabled'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.disabled') }</option>
										<option value="previous_values" {#if $T.subfield.autocomplete_type == 'previous_values'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.previous_values') }</option>
										<option value="fixed_table" {#if $T.subfield.autocomplete_type == 'fixed_table'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.fixed_table') }</option>
										<option value="fixed_table_with_previous_values" {#if $T.subfield.autocomplete_type == 'fixed_table_with_previous_values'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.fixed_table_with_previous_values') }</option>
										<option value="biblio" {#if $T.subfield.autocomplete_type == 'biblio'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.biblio') }</option>
										<option value="authorities" {#if $T.subfield.autocomplete_type == 'authorities'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.authorities') }</option>
										<option value="vocabulary" {#if $T.subfield.autocomplete_type == 'vocabulary'}selected="selected"{#/if}>{ _('administration.form_customization.subfield_autocomplete.vocabulary') }</option>
									</select>
								</td>
								<td class="tr_icon"><a href="javascript:void(0);" class="remove-subfield" onclick="FormCustomization.removeSubfield(this)"><i class="fa fa-times"></i></a></td>
							</tr>
						{#/for}
						<tr>
							<td colspan=6>&#160;</td>
							<td class="tr_icon"><a href="javascript:void(0);" class="add-subfield" onclick="FormCustomization.addSubfield(this)"><i class="fa fa-fw fa-plus"></i></a></td>
						</tr>
					</tbody>
				</table>
		 	</div>
		--></textarea>		
		
	</div>
</layout:body>
