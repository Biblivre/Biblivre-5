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
			<% if (exception instanceof DAOException) { %>
				<div>
					Erro ao estabelecer conexão com o Banco de Dados.<br>
					Por favor verifique se o mesmo está ativo e acessível pelo servidor Biblivre.
					<br><br>
					Error establishing a connection to the Database Server.<br>
					Please check if the database server is running and accessible by the Biblivre server.
				</div>
			<% } else { %>
				<div>
					Ocorreu um erro durante a execução deste pedido.<br>
					Por favor entre em contato com o administrador do sistema ou um bibliotecário responsável.
					<br><br>
					An error has occurred during the execution of this request.<br>
					Please contact the system administrator or a head librarian.
				</div>
			<% } %>
			</div>
		</div>
	</div>
	<div id="stacktrace">
		<%
			if (exception != null) {
				if (NetworkUtils.isLocalRequest(request)) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
	
					exception.printStackTrace(pw);
				
					sw.close();
					pw.close();
				
					out.print("<pre>");
					out.print(sw);
					out.print("</pre>");
				} else {
					%>
					Ocorreu um erro de aplicação durante a execução deste pedido. Os detalhes deste erro não podem ser visualizados a partir de um computador remoto (por medidas de segurança), mas podem ser vistos através de um navegador executado a partir do computador onde o Biblivre está instalado.<br><br>
					An application error occurred on the server. The application prevents the details of this error from being viewed remotely (for security reasons). It could, however, be viewed by browsers running on the local Biblivre server machine.
					<%
				} 
			}
		%>
	</div>
</body>
</html>







