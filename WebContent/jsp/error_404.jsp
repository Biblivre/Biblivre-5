<%@ page import="biblivre.core.ExtendedResponse" %>
<%@ page import="org.apache.commons.httpclient.HttpStatus" %>
<%@ page import="biblivre.core.utils.NetworkUtils" %>
<%@ page import="biblivre.core.exceptions.DAOException" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" import="java.io.*" %> 

<!doctype html>
<html class="noscript">
<head>
	<meta charset="utf-8">
	<meta name="google" content="notranslate" />
	<title>Biblivre IV</title>
	<link rel="shortcut icon" type="image/x-icon" href="static/images/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.core.css" />
</head>

<body>
	<div id="header">
		<div id="logo_biblivre">
			<a href="http://biblivre.org.br/" target="_blank"> <img src="static/images/logo_biblivre.png" width="117" height="66" alt="Biblivre V"></a>
		</div>
	</div>
	<div id="notifications">
		<div id="messages">
			<div class="message sticky error">
				<div>
					Arquivo não encontrado.
					<br><br>
					File not found.
				</div>
			</div>
		</div>
	</div>
</body>
</html>







