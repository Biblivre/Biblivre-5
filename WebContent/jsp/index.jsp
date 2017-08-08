<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="biblivre.core.schemas.SchemaDTO"%>
<%@page import="biblivre.core.schemas.Schemas"%>
<%@page import="biblivre.core.ExtendedRequest"%>
<%@ page import="biblivre.login.LoginDTO "%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.index.css" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.multi_schema.css" />
</layout:head>

<layout:body>
	<% 	
	ExtendedRequest req = (ExtendedRequest) request;
	
	if (!req.isGlobalSchema()) {
	%>
		<div class="picture">
			<img src="static/images/main_picture_1.jpg"/>
		</div>

		<div class="text">
			<%
				LoginDTO login = (LoginDTO) session.getAttribute(request.getAttribute("schema") + ".logged_user");
				pageContext.setAttribute("login", login);
	
				if (login != null) {
					pageContext.setAttribute("name", login.getFirstName());
				}
			%>
	
			<c:choose>
				<c:when test="${empty login}">
					<i18n:text key="text.main.logged_out" />
				</c:when>
				<c:otherwise>
					<i18n:text key="text.main.logged_in" param1="${name}" />
				</c:otherwise>
			</c:choose>
		</div>
	<%
	} else {
	%>
		<div class="multischema biblivre_form">
			<fieldset>
				<legend><i18n:text key="text.multi_schema.select_library" /></legend>
				<% for (SchemaDTO schema : Schemas.getSchemas()) { %>
					<% if (schema.isDisabled()) { continue; } %>
					<div class="library">
						<a href="<%= schema.getSchema() %>/"><%= Configurations.getHtml(schema.getSchema(), Constants.CONFIG_TITLE) %></a>
						<div class="subtitle"><%= Configurations.getHtml(schema.getSchema(), Constants.CONFIG_SUBTITLE) %></div>
					</div>
				<% } %>
			</fieldset>	
		</div>
	<%
	}	
	%>
</layout:body>
