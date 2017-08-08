<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.search.css" />
</layout:head>


<%
	char selected = 'a';

	String l = request.getParameter("letter");
	if (l != null && l.length() > 0) {
		selected = l.charAt(0);
	}
	
%>
<layout:body>
	<div class="paging_bar center">
		<% for (char letter : new Character[] { '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' }) { %>
			<% if (letter == Character.toUpperCase(selected)) { %>
				<span class="paging actual_page"><%= letter == '0' ? '#' : letter %></span>
			<% } else { %>
				<a href="?action=list_bibliographic&letter=<%= Character.toLowerCase(letter) %>" class="paging"><%= letter == '0' ? '#' : letter %></a>
			<% } %>
		<% } %>
	</div>
	
	<div id="list_results">
		<c:forEach var="record" varStatus="status" items="${records}" >
			<div class="result ${status.index % 2 == 0 ? 'even' : 'odd'}">
				<div class="record">
					<label>Título:</label> <a href="?action=search_bibliographic#query=${record.id}&group=id&search=advanced">${record.title}</a><br>
					<label>Autor:</label> ${record.author}<br>
					<label>Ano:</label> ${record.publicationYear}<br>
				</div>
			</div>
		</c:forEach>
	</div>
</layout:body>
