<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<script type="text/javascript" src="static/scripts/biblivre.administration.password.js"></script>
</layout:head>

<layout:body>
	<div class="page_help">
		<p><i18n:text key="administration.change_password.description.1" /></p>
		<p><i18n:text key="administration.change_password.description.2" /></p>
		<ul>
			<li><i18n:text key="administration.change_password.description.3" /></li>
			<li><i18n:text key="administration.change_password.description.4" /></li>
			<li><i18n:text key="administration.change_password.description.5" /></li>
		</ul>
		<p><i18n:text key="administration.change_password.description.6" /></p>
	</div>

	<div class="biblivre_form submit_on_enter">
		<fieldset>
			<legend><i18n:text key="administration.change_password.title" /></legend>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.change_password.current_password" /></div>
					<div class="value">
						<input type="password" name="current_password" maxlength="50" class="finput auto_focus">
					</div>
					<div class="extra"></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.change_password.new_password" /></div>
					<div class="value">
						<input type="password" name="new_password" maxlength="50" class="finput">
					</div>
					<div class="extra"></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.change_password.repeat_password" /></div>
					<div class="value">
						<input type="password" name="repeat_password" maxlength="50" class="finput">
					</div>
					<div class="extra"></div>
					<div class="clear"></div>
				</div>
			</div>
			
			<div class="buttons">
				<a class="main_button arrow_right" onclick="Administration.password.submit(this);"><i18n:text key="administration.change_password.submit_button" /></a>
				<div class="clear"></div>
			</div>
		</fieldset>	
	</div>
</layout:body>
