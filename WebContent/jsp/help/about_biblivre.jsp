<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<link rel="stylesheet" type="text/css" href="static/styles/biblivre.about.css" />
</layout:head>

<layout:body>

	<div class="biblivre_form">
		<fieldset class="history">
			<legend>Biblivre ${version}</legend>
		
			<p>A SABIN, sob a presidência do Dr. Jean-Louis de Lacerda Soares, propôs em meados de 2006 um projeto de desenvolvimento de uma nova versão ampliada do sistema de automação de bibliotecas BIBLIVRE.</p>
			<p>A proposta foi aprovada pelo MINISTÉRIO DA CULTURA, sob os auspícios da Lei Rouanet de incentivo ao desenvolvimento  sociocultural (Lei 8.313/91).</p>
			<p>O projeto previu, desde o seu início, que os programas desenvolvidos fossem oferecidos às bibliotecas que desejassem utilizar esta tecnologia na modalidade conhecida atualmente como "programas livres" (software livre ou free software). Devido a esta característica, o projeto passou a chamar-se Biblioteca Livre (Biblivre Internacional).</p>
			<p>Em 2008, o Instituto Cultural Itaú inteirou-se do objetivo e da relevância social e cultural do projeto e decidiu participar deste como patrocinador. Em junho de 2008, foi iniciado o desenvolvimento atual versão.</p>
			<p>O primeiro projeto de desenvolvimento do sistema BIBLIVRE foi inicialmente proposto pela SABIN sob a presidência do Dr. Paulo Marcondes Ferraz e foi completamente realizado já sob a presidência do Dr. Jean-Louis de Lacerda Soares. Nesta ocasião, a IBM Brasil participou do projeto como patrocinadora.</p>
		</fieldset>
		
		<fieldset class="copyright">
			<p><strong>BIBLIVRE</strong></p>
			<p>&copy;2010-2013 Ubaldo Santos Miranda, Alberto Wagner Collavizza, Danniel Willian B. do Nascimento e Clarice Muhlethaler de Souza</p>
			<p>Todos os direitos reservados</p>
		</fieldset>
		
		<fieldset class="gnu">
			<p><b>LICENÇA PÚBLICA GERAL GNU</b></p>
			<p>Versão 3, 29 de junho de 2007 Copyright © 2007 Free Software Foundation, Inc. <a href="http://fsf.org/" target="_blank">http://fsf.org/</a></p>
		</fieldset>	
		
		<fieldset class="license">
			<p>Este sistema é um Programa Livre. Você pode instalá-lo, usá-lo, modificá-lo e redistribuí-lo nos termos da licença GPL em português, fornecida com este sistema, ou nos termos da licenca GPL original, como publicada em inglês pela <i>Free Software Foundation</i>.</p>
			<p>ESTE PROGRAMA É DISTRIBUÍDO NO ESTADO EM QUE SE ENCONTRA, COM A ESPERANÇA QUE SEJA ÚTIL, MAS SEM NENHUMA GARANTIA, MESMO QUE IMPLÍCITA, SEJA DE COMERCIALIZAÇÃO, NÃO VIOLAÇÃO DE DIREITOS OU DE SER APROPRIADO PARA ALGUMA FINALIDADE PARTICULAR.</p>
		</fieldset>
				
		<fieldset class="softwares">
			<p>O BIBLIVRE, além do código desenvolvido pelo próprio projeto, usa outros programas, linguagens e bibliotecas escritas por terceiros, que são protegidas por suas próprias licenças:</p>
			<ul>
				<li>java, Sun Corporation (http://java.sun.com);</li>
				<li>Apache Tomcat, Apache Software Foundation (http://tomcat.apache.org);</li>
				<li>marc4j, Tigris.org (http://marc4j.tigris.org);</li>
				<li>jzkit, Knowledge Integration Ltd (http://developer.k-int.com);</li>
				<li>PostgreSQL, The PostgreSQL Global Development Group (http://www.postgresql.org);</li>
				<li>jQuery, The jQuery Project (http://jquery.com).</li>
			</ul>
		</fieldset>
	</div>
</layout:body>