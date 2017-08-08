<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<layout:head></layout:head>

<layout:body multiPart="true">
	<h2>Hello Books!</h2>
	<input type="hidden" name="module" value="administration.backup"/>		
	<input type="hidden" name="action" value="restore_biblivre3"/>
	
	<input type="file" name="file"/>
	
	<input type="submit"/>
</layout:body>
