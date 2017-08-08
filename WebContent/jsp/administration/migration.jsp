<%@page import="biblivre.administration.setup.DataMigrationPhaseGroup"%>
<%@page import="biblivre.core.translations.TranslationsMap"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		
		var migrate = function(button) {
			
			var phaseGroups = [];
			$('input:checkbox:checked[name="phases"]').each(function() {
				phaseGroups.push($(this).val());
			});
			
			Core.clearFormErrors();
			
			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				data: {
					controller: 'json',
					module: 'administration.datamigration',
					action: 'migrate',
					groups: phaseGroups
				},
				loadingButton: button,
				loadingTimedOverlay: true
			}).done($.proxy(function(response) {
				Core.msg(response);
				
				if (!response.success) {
					Core.formErrors(response.errors);
				}
			}, this));
	
		};
	</script>
</layout:head>

<layout:body>

	<div class="page_help"><i18n:text key="administration.migration.page_help" /></div>

	<div class="biblivre_form">
		<fieldset>
			<legend><i18n:text key="administration.migration.title" /></legend>
			<div class="description"><i18n:text key="administration.migration.description" /></div>
			<div class="fields">
				<%
					TranslationsMap translations = (TranslationsMap) request.getAttribute("translationsMap");
					for (DataMigrationPhaseGroup phase : DataMigrationPhaseGroup.values()) {
						out.println("<div><input type=\"checkbox\" value=\"" + phase + "\" name=\"phases\" id=\"phase_" + phase + "\" />&#160;<label for=\"phase_" + phase + "\">" + translations.getText("administration.migration.groups." + phase.toString()) + "</label></div>");
					}
				%>
			</div>
		</fieldset>	
		
		<div class="footer_buttons">
			<a class="button center main_button" onclick="migrate(this);"><i18n:text key="administration.migration.button.migrate" /></a>
		</div>		
	</div>
			
</layout:body>
