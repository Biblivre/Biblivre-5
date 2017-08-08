<%@ page import="java.io.FileNotFoundException" %>
<%@ page import="biblivre.core.ExtendedRequest" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>

<% 
	try {
		ExtendedRequest xRequest = (ExtendedRequest) request;
	} catch (Exception e) {
		request.setAttribute("javax.servlet.jsp.jspException", new FileNotFoundException());
		request.getRequestDispatcher("/jsp/error_fatal.jsp").forward(request, response);
		return;
	}
%>

<layout:head></layout:head>

<layout:body></layout:body>