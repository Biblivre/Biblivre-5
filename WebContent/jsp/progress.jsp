<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.administration.setup.css" />

	<script type="text/javascript" src="static/scripts/biblivre.administration.progress.js"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {
			Administration.progress.autoReload = true;
			Administration.progress.showPopupProgress();
			Administration.progress.progress();
		});
	</script>
</layout:head>

<layout:body disableMenu="true">	
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
