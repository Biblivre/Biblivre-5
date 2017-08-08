<%@page import="biblivre.core.schemas.Schemas"%>
<%@page import="biblivre.core.enums.PrinterType"%>
<%@page import="biblivre.core.utils.FileIOUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="biblivre.core.translations.LanguageDTO"%>
<%@page import="java.io.File"%>
<%@page import="biblivre.core.utils.DatabaseUtils"%>
<%@page import="biblivre.administration.backup.BackupBO"%>
<%@page import="biblivre.core.translations.Languages"%>
<%@page import="biblivre.core.utils.Constants"%>
<%@page import="biblivre.core.configurations.Configurations"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/translations.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:head>
	<script type="text/javascript" src="static/scripts/biblivre.administration.configurations.js"></script>

	<script>
		Configurations.businessDays = '<%= Constants.CONFIG_BUSINESS_DAYS %>';
	</script>
</layout:head>

<layout:body>

	<div class="page_help"><i18n:text key="multi_schema.configurations.page_help" /></div>
	
	<% String value; %>
	<% String key; %>
	<% boolean active; %>
	<% String schema = (String) request.getAttribute("schema"); %>
	<div class="biblivre_form">
		<fieldset>
			<% 
				key = Constants.CONFIG_TITLE;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend><i18n:text key="multi_schema.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="multi_schema.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">Biblivre IV</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>	

		<fieldset>
			<% 
				key = Constants.CONFIG_SUBTITLE;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend><i18n:text key="multi_schema.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="multi_schema.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">Versão ${version}</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>	

		<fieldset>
			<% 
				key = Constants.CONFIG_MULTI_SCHEMA;
				active = Schemas.isMultipleSchemasEnabled();
				request.setAttribute("key", key);
				request.setAttribute("active", active);
			%>
			<legend><i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" param1="${schema}"/></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><input type="checkbox" id="multi_schema_active" name="${key}" class="finput" <c:if test="${active}">checked="checked"</c:if> style="width: auto;"/></div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_ACCESSION_NUMBER_PREFIX;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">Bib</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_BUSINESS_DAYS;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value" id="business_days_current"><script>var BusinessValues = '<c:out value="${value}"/>';</script></div>
					<div class="clear"></div>
					
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="fleft" id="business_days">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_DEFAULT_LANGUAGE;
				value = Configurations.getString(schema, key);

				LanguageDTO ldto = Languages.getLanguage(schema, value);
				if (ldto != null) {
					request.setAttribute("default_language", ldto.getName());
				}
				
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">Português (Brasil)</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${default_language}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<select name="${key}">
							<c:forEach var="language" items="<%= Languages.getLanguages(schema) %>">
								<c:choose>
									<c:when test="${language.language == value}">
										<option value="${language.language}" selected="selected">${language.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${language.language}">${language.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>						
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_CURRENCY;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">R$</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>	

		<fieldset>
			<% 
				key = Constants.CONFIG_SEARCH_RESULTS_PER_PAGE;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">25</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_SEARCH_RESULT_LIMIT;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">6000</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_Z3950_RESULT_LIMIT;
				value = Configurations.getString(schema, key);
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.original_value" /></div>
					<div class="value">100</div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<div class="value"><c:out value="${value}"/></div>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_BACKUP_PATH;
				value = BackupBO.getInstance(schema).getBackupPath();
				request.setAttribute("key", key);
				request.setAttribute("value", value);
				
				boolean writeable = FileIOUtils.isWritablePath(value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<% if (writeable) {%>
						<div class="value"><c:out value="${value}"/></div>
					<% } else {%>
						<div class="value value_error"><c:out value="${value}"/><br><i18n:text key="administration.configuration.invalid_backup_path" /></div>
					<% }%>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_PGDUMP_PATH;
				File pgDump = DatabaseUtils.getPgDump(schema);
				value = (pgDump == null) ? null : pgDump.getAbsolutePath();
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<% if (value != null) {%>
						<div class="value"><c:out value="${value}"/></div>
					<% } else {%>
						<div class="value value_error"><i18n:text key="administration.configuration.invalid_pg_dump_path" /></div>
					<% }%>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<% 
				key = Constants.CONFIG_PSQL_PATH;
				File psql = DatabaseUtils.getPsql(schema);
				value = (psql == null) ? null : psql.getAbsolutePath();
				request.setAttribute("key", key);
				request.setAttribute("value", value);
			%>
			<legend>* <i18n:text key="administration.configuration.title.${key}" /></legend>
			<div class="description"><i18n:text key="administration.configuration.description.${key}" /></div>
			<div class="fields">
				<div>
					<div class="label"><i18n:text key="administration.configuration.current_value" /></div>
					<% if (value != null) {%>
						<div class="value"><c:out value="${value}"/></div>
					<% } else {%>
						<div class="value value_error"><i18n:text key="administration.configuration.invalid_psql_path" /></div>
					<% }%>
					<div class="clear"></div>
				</div>
				<div>
					<div class="label"><i18n:text key="administration.configuration.new_value" /></div>
					<div class="value">
						<input type="text" name="${key}" class="finput" value="<c:out value="${value}"/>">
					</div>
					<div class="clear"></div>
				</div>
			</div>
		</fieldset>
		
		<div class="footer_buttons">
			<a class="button center main_button" onclick="Configurations.save(this);"><i18n:text key="common.save" /></a>
		</div>		
	</div>
</layout:body>
