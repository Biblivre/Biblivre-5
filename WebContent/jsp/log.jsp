<%@ page import="biblivre.administration.setup.State" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head></layout:head>

<layout:body disableMenu="true">
	<div class="buttons">
		<a class="main_button arrow_right" href="."><i18n:text key="administration.setup.button.continue_to_biblivre" /></a>
	</div>

	<textarea id="log_textarea"><%= State.getLog() %></textarea>
</layout:body>






