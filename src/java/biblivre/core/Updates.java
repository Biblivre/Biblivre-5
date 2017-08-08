/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core;

import java.sql.Connection;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;

import biblivre.administration.indexing.IndexingBO;
import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.schemas.Schemas;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;

public class Updates {

	public static String getVersion() {
		return Constants.BIBLIVRE_VERSION;
	}

	public static boolean globalUpdate() {
		UpdatesDAO dao = UpdatesDAO.getInstance("global");

		Connection con = null;
		try {
			Set<String> installedVersions = dao.getInstalledVersions();

			String version = "4.0.0b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.1b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.2b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.3b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.4b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.5b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.6b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				Translations.addSingleTranslation("global", "pt-BR", "administration.reports.title.custom_count", "Relatório de contagem pelo campo Marc", 0);
				Translations.addSingleTranslation("global", "pt-BR", "cataloging.bibliographic.search.holding_accession_number", "Tombo patrimonial", 0);
				Translations.addSingleTranslation("global", "pt-BR", "cataloging.bibliographic.search.holding_id", "Código de barras da etiqueta", 0);
				Translations.addSingleTranslation("global", "pt-BR", "search.holding.shelf_location", "Localização", 0);
				Translations.addSingleTranslation("global", "pt-BR", "circulation.lending.no_holding_found", "Nenhum exemplar encontrado", 0);

				Translations.addSingleTranslation("global", "en-US", "administration.reports.title.custom_count", "Marc field counting report", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.bibliographic.search.holding_accession_number", "Asset number", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.bibliographic.search.holding_id", "Label barcode number", 0);
				Translations.addSingleTranslation("global", "en-US", "search.holding.shelf_location", "Location", 0);
				Translations.addSingleTranslation("global", "en-US", "circulation.lending.no_holding_found", "No copy found", 0);

				Translations.addSingleTranslation("global", "es", "administration.reports.title.custom_count", "Informe de recuento del campo Marc", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.bibliographic.search.holding_accession_number", "Sello patrimonial", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.bibliographic.search.holding_id", "Código de barras de la etiqueta", 0);
				Translations.addSingleTranslation("global", "es", "search.holding.shelf_location", "Localización", 0);
				Translations.addSingleTranslation("global", "es", "circulation.lending.no_holding_found", "Ningún ejemplar encontrado", 0);

				dao.commitUpdate(version, con);
			}

			version = "4.0.7b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.8b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre3restore.log_header", "[Log de restauração de backup do Biblivre 3]\n\n", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre3restore.log_header", "[Log for Biblivre 3 backup restoration]\n\n", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre3restore.log_header", "[Log de restauración de backup del Biblivre 3]\n\n", 0);

				dao.commitUpdate(version, con);
			}

			version = "4.0.9b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.fixUpdateTranslationFunction(con);
				dao.fixUpdateUserFunction(con);
				if (!dao.checkTableExistance("backups")) {
					dao.fixBackupTable(con);
				}

				dao.commitUpdate(version, con);
			}

			version = "4.0.10b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);

				for (String schema : Schemas.getEnabledSchemasList()) {
					try {
						IndexingBO bo = IndexingBO.getInstance(schema);
						bo.reindex(RecordType.AUTHORITIES);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			version = "4.0.11b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.12b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.skip", "Ignorar", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.skip", "Skip", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.skip", "Pasar", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.error.digital_media_only_selected",
						"O Backup selecionado contém apenas arquivos digitais. Tente novamente usando um backup completo ou parcial sem arquivos digitais", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.error.digital_media_only_selected",
						"The selected Backup is a Digital Media Only file.  Try again using a Complete Backup file or one without Digital Media", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.error.digital_media_only_selected",
						"La copia de seguridad seleccionada contiene sólo los archivos digitales. Trate de usar una copia de seguridad completa o parcial sin archivos digitales",
						0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
						"O segundo arquivo de backup selecionado não contém apenas arquivos digitais", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
						"The second file selected is not a Digital Media Only file", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
						" El segundo archivo que seleccionó no  contiene sólo archivos digitales", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.select_digital_media", "Selecione um Backup de Mídias Digitais", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.select_digital_media", "Select a Digital Media Backup file", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.select_digital_media",
						"Seleccione una copia de seguridad de archivos digitales", 0);

				Translations
						.addSingleTranslation(
								"global",
								"pt-BR",
								"administration.setup.biblivre4restore.select_digital_media.description",
								"O arquivo de backup selecionado anteriormente não possui Mídias Digitais.  Caso você possua um backup somente de Mídias Digitais, selecione abaixo o arquivo desejado, ou faça o upload do mesmo. Caso não deseje importar Mídias Digitais, clique no botão <strong>Ignorar</strong>.",
								0);
				Translations
						.addSingleTranslation(
								"global",
								"en-US",
								"administration.setup.biblivre4restore.select_digital_media.description",
								"The previously selected Backup file doesn't have any Digital Media. If you have a Digital Media Only backup, select the desired one below, or upload the Digital Media Only backup file. If you don't want to import Digital Media, click on <strong>Skip</strong>.",
								0);
				Translations
						.addSingleTranslation(
								"global",
								"es",
								"administration.setup.biblivre4restore.select_digital_media.description",
								"El archivo de copia de seguridad seleccionado previamente no contiene archivos digitales. Si usted tiene una copia de seguridad de sólo archivos digitales, seleccione el archivo que desee a continuación, o cargar el mismo. Si no desea importar Digital Media, haga clic en <strong>Pasar</ strong>.",
								0);

				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.manage.drop_schema.confirm_title", "Excluir biblioteca", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.manage.drop_schema.confirm_title", "Delete library", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.manage.drop_schema.confirm_title", "Excluir biblioteca", 0);

				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.manage.drop_schema.confirm_description", "Você realmente deseja excluir esta biblioteca?", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.manage.drop_schema.confirm_description", "Do you really want to delete this library?", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.manage.drop_schema.confirm_description", "¿Usted realmente desea excluir esta biblioteca?", 0);

				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.manage.drop_schema.confirm", "Ela será excluída permanentemente do sistema e não poderá ser recuperada", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.manage.drop_schema.confirm", "It will be deleted from the system forever and cannot be restored", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.manage.drop_schema.confirm", "La biblioteca será excluida permanentemente del sistema y no podrá ser recuperada", 0);

				dao.commitUpdate(version, con);
			}

			version = "4.1.0";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.1";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.backup.display_and_select_libraries", "Exibir e selecionar bibliotecas {min} a {max}", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.backup.display_and_select_libraries", "Show and select libraries from {min} to {max}", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.backup.display_and_select_libraries", "Ver y seleccionar las bibliotecas de {min} a {max}", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.select_file", "Selecione um arquivo de backup do Biblivre 4", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.select_file", "Select a Biblivre 4 backup file", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.select_file", "Seleccione un archivo de copia de seguridad Biblivre 4", 0);

				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.restore.limit.title", "Bibliotecas no arquivo selecionado", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.restore.limit.title", "Libraries in the selected file", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.restore.limit.title", "Bibliotecas en el archivo seleccionado", 0);

				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.restore.limit.description", "O arquivo selecionado possui um número muito grande de bibliotecas. Por limites do banco de dados, a restauração deverá ser feita em passos, de no máximo 20 bibliotecas por passo. Clique nos links abaixo para listar as bibliotecas desejadas, e selecione as bibliotecas que serão restauradas. Repita esse procedimento até que todas as bibliotecas desejadas tenham sido restauradas.", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.restore.limit.description", "The selected file contains a high number of libraries. Due to database limitations, you should restore those libraries in steps, limited to 20 libraries in each step. Click in a link below to list the desired libraries, and select the ones you want to restore. Repeat these steps untill you've restored all the libraries you need.", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.restore.limit.description", " El archivo seleccionado tiene un gran número de bibliotecas. Debido a las limitaciones de la base de datos, la restauración debe hacerse en pasos de hasta 20 bibliotecas a paso. Haga clic en los enlaces abajo para enumerar la biblioteca que desee y seleccione las bibliotecas que se restaurarán. Repita este procedimiento hasta que se hayan restaurado todas las bibliotecas deseadas.", 0);

				dao.commitUpdate(version, con);
			}
			

			version = "4.1.2";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.3";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.4";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.5";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.6";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.7";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.8";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.9";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.10";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				Translations.addSingleTranslation("global", "pt-BR", "cataloging.bibliographic.indexing_groups.publisher", "Editora", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.bibliographic.indexing_groups.publisher", "Publisher", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.bibliographic.indexing_groups.publisher", "Editora", 0);

				Translations.addSingleTranslation("global", "pt-BR", "cataloging.bibliographic.indexing_groups.series", "Série", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.bibliographic.indexing_groups.series", "Series", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.bibliographic.indexing_groups.series", "Serie", 0);

				dao.commitUpdate(version, con);
			}
			
			version = "4.1.10a";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				Translations.addSingleTranslation("global", "pt-BR", "cataloging.tab.record.custom.field_label.biblio_501", "Notas", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.tab.record.custom.field_label.biblio_501", "Notes", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.tab.record.custom.field_label.biblio_501", "Notas", 0);

				Translations.addSingleTranslation("global", "pt-BR", "cataloging.tab.record.custom.field_label.biblio_530", "Notas", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.tab.record.custom.field_label.biblio_530", "Notes", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.tab.record.custom.field_label.biblio_530", "Notas", 0);

				Translations.addSingleTranslation("global", "pt-BR", "cataloging.tab.record.custom.field_label.biblio_595", "Notas", 0);
				Translations.addSingleTranslation("global", "en-US", "cataloging.tab.record.custom.field_label.biblio_595", "Notes", 0);
				Translations.addSingleTranslation("global", "es", "cataloging.tab.record.custom.field_label.biblio_595", "Notas", 0);

				dao.commitUpdate(version, con);
			}
			
			version = "4.1.11";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "5.0.0";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				Translations.addSingleTranslation("global", "pt-BR", "menu.administration_brief_customization", "Personalização de Resumo Catalográfico", 0);
				Translations.addSingleTranslation("global", "en-US", "menu.administration_brief_customization", "Catalographic Summary Customization", 0);
				Translations.addSingleTranslation("global", "es",    "menu.administration_brief_customization", "Personalización del Resumen Catalográfico", 0);

				Translations.addSingleTranslation("global", "pt-BR", "menu.administration_form_customization", "Personalização de Formulário Catalográfico", 0);
				Translations.addSingleTranslation("global", "en-US", "menu.administration_form_customization", "Catalographic Form Customization", 0);
				Translations.addSingleTranslation("global", "es",    "menu.administration_form_customization", "Personalización del Formulario Catalográfico", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.permissions.items.administration_customization", "Personalização", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.permissions.items.administration_customization", "Customization", 0);
				Translations.addSingleTranslation("global", "es",    "administration.permissions.items.administration_customization", "Personalización", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.separators.space-dash-space", "Espaço - hífen - espaço", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.separators.space-dash-space", "Blank - dash - blank", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.separators.space-dash-space", "Espacio - guión - espacio", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.separators.comma-space", "Vírgula - espaço", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.separators.comma-space", "Comma - blank", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.separators.comma-space", "Coma - espacio", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.separators.dot-space", "Ponto - espaço", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.separators.dot-space", "Dot - blank", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.separators.dot-space", "Punto - espacio", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.separators.colon-space", "Dois pontos - espaço", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.separators.colon-space", "Colon - blank", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.separators.colon-space", "Dos Puntos - espacio", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.separators.semicolon-space", "Ponto e vírgula - espaço", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.separators.semicolon-space", "Semicolon - blank", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.separators.semicolon-space", "Punto y coma - espacio", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.aggregators.left-parenthesis", "Abre parênteses", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.aggregators.left-parenthesis", "Left parenthesis", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.aggregators.left-parenthesis", "Paréntesis izquierdo", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.aggregators.right-parenthesis", "Fecha parênteses", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.aggregators.right-parenthesis", "Right parenthesis", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.aggregators.right-parenthesis", "Paréntesis derecho", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.confirm_disable_datafield_title", "Desabilitar a exibição", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.confirm_disable_datafield_title", "Hide field", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.confirm_disable_datafield_title", "Ocultar campo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.confirm_disable_datafield_question", "Marcando esta opção você estará escondendo o campo na aba de Resumo Catalográfico. Você poderá exibir o mesmo novamente depois, caso mude de idéia.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.confirm_disable_datafield_question", "By selecting this option, you'll be hiding the Field from the Catalographic Summary tab. You'll be able to show the field back if you change your mind.", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.confirm_disable_datafield_question", "Al activar esta opción se esconden el campo en lo Resumen Catalográfico. Usted será capaz de mostrar el campo de vuelta si cambia de opinión.", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.confirm_disable_datafield_confirm", "Tem certeza que deseja remover este campo do Resumo Catalográfico?", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.confirm_disable_datafield_confirm", "Are you sure you want to hide this field from the Catalographic Summary tab?", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.confirm_disable_datafield_confirm", "¿Seguro que quieres ocultar este campo desde lo Resumen Catalográfico?", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.page_help", "<p>A rotina de Personalizaçao de Resumo Catalográfico permite configurar quais campos e subcampos MARC serão apresentados nas rotinas de Catalogação Bibliográfica, de Autoridades e de Vocabulários.  Os campos e subcampos configurados aqui serão apresentados na aba de Resumo Catalográfico nas rotinas de Catalogação. Você poderá configurar a ordem dos campos e subcampos, assim como os separadores que irão aparecer entre os subcampos.</p><p>Os campos exibidos nesta tela são os campos disponíveis no Formulário Catalográfico. Para criar novos campos, ou alterar seus subcampos, utilize a tela de <b>Personalização de Formulário Catalográfico.</b></p>", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.page_help", "<p>The Catalographic Summary Customization page lets you customize which MARC Tags and Subfields will be displayed in the Cataloging pages. The Tags and Subfields customized in this page will be displayed in the Catalographic Summary tabs in the Cataloging pages. You can customize the order for the Tags and Subfields, and also customize the separators or aggregators for the Subfields.</p><p>All the Tags and Subfields displayed here are the ones available in the Catalographic Form page. To create new Tags or Subfields, go to the <b>Catalographic Form Customization</b> page.</p>", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.page_help", "<p>La página Personalización de lo Resumen Catalográfico le permite personalizar cual Campos y Subcampos MARC se mostrarán en las páginas de Catalogación. Los Campos y Subcampos personalizados en esta página se mostrarán en las fichas de lo Resumen catalográfico en las páginas de Catalogación. Usted puede personalizar el orden de los Campos y Subcampos, y también personalizar los separadores o agregadores del subcampos.</p><p>Todas los Campos y Subcampos que se muestra aquí son los que están disponibles en la página de Formulario Catalográfico. Para crear nuevas etiquetas o subcampos, vaya a la <b>Personalización del Formulario Catalográfico</b>.</p>", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.select_record_type", "Selecione o Tipo de Registro", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.select_record_type", "Select the Record Type", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.select_record_type", "Seleccione el Tipo de Registro", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.biblio", "Registro Bibliográfico", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.biblio", "Bibliographic Record", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.biblio", "Registro Bibliográfico", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.authorities", "Registro de Autoridades", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.authorities", "Authorities Record", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.authorities", "Registro de Autoridad", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.vocabulary", "Registro de Vocabulário", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.vocabulary", "Vocabulary record", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.vocabulary", "Registro de Vocabulario", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.subfields_title", "Subcampos", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.subfields_title", "Subfields", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.subfields_title", "Subcampo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.separators_title", "Separadores de subcampo", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.separators_title", "Subfield separators", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.separators_title", "Separadores de subcampo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.aggregators_title", "Agregadores de subcampo", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.aggregators_title", "Subfield aggregators", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.aggregators_title", "Agregadores de subcampo", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.page_help", "<p>A rotina de Personalização de Formulário Catalográfico permite configurar quais Campos, Subcampos e Indicadores MARC serão apresentados nas rotinas de Catalogação Bibliográfica, de Autoridades e de Vocabulários. Os Campos, Subcampos e Indicadores configurados aqui serão apresentados na aba de Formulário Catalográfico nas rotinas de Catalogação. Você poderá configurar a ordem dos Campos, Subcampos e Indicadores, assim como editar cada Campo, adicionando ou removendo Subcampos e Indicadores, ou alterando os textos dos elementos MARC.</p>", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.page_help", "<p>The Catalographic Form Customization allows you to configure which MARC Tags, Subfields and Indicators will be displayed in the Cataloging pages. The Tags, Subfields and Indicators set here will be displayed in the Cataloging Form tab in the Cataloging pages. You can customize the order of the Tags, Subfields and Indicators, as well as edit each Tag by adding or removing Subfields and Indicators, or changing the text of the MARC elements.</p>", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.page_help", "<p>La página Personalización del Formulario Catalográfico le permite configurar cual Campos, Subcampos e Indicadores MARC se mostrarán en las páginas de catalogación. Los Campos, Subcampos e Indicadores establecidos aquí se mostrarán en la pestaña Formulario Catalografico en las páginas de Catalogación. Puede personalizar el orden de los Campos, Subcampos e Indicadores, así como editar cada etiqueta mediante la adición o eliminación de Subcampos e Indicadores, o cambiando el texto de los elementos MARC.</p>", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.field", "Campo MARC", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.field", "MARC Tag", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.field", "Campo MARC", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.field_name", "Nome do Campo", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.field_name", "Tag Name", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.field_name", "Nombre del Campo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.field_repeatable", "Repetível", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.field_repeatable", "Repeatable", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.field_repeatable", "Repetible", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.field_collapsed", "Colapsado", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.field_collapsed", "Collapsed", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.field_collapsed", "Colapsado", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.indicator_number", "Indicador", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.indicator_number", "Indicator", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.indicator_number", "Indicador", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.indicator_name", "Nome do indicador", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.indicator_name", "Indicator name", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.indicator_name", "Nombre del indicador", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.indicator_values", "Valores", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.indicator_values", "Values", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.indicator_values", "Valores", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.change_indicators", "Alterar", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.change_indicators", "Change", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.change_indicators", "Cambio", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.material_type", "Tipos de Material", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.material_type", "Material Type", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.material_type", "Tipos de Material", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield", "MARC", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield", "MARC", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield", "MARC", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_name", "Nome do Subcampo", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_name", "Subfield name", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_name", "Nombre del Subcampo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_repeatable", "Repetível", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_repeatable", "Repeatable", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_repeatable", "Repetible", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_collapsed", "Oculto", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_collapsed", "Hidden", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_collapsed", "Oculto", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.label", "Auto Completar", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.label", "Autocomplete", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.label", "Autocompletar", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.", "Auto Completar", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.", "Autocomplete", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.", "Autocompletar", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.disabled", "Desabilitado", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.disabled", "Disabled", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.disabled", "Inactivo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.previous_values", "Valores anteriores", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.previous_values", "Previous Values", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.previous_values", "Valores anteriores", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.fixed_table", "Tabela fixa", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.fixed_table", "Fixed Table", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.fixed_table", "Tabla fija", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.fixed_table_with_previous_values", "Tabela e Valores", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.fixed_table_with_previous_values", "Table and Values", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.fixed_table_with_previous_values", "Tabla e Valores", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.biblio", "Bibliográfico", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.biblio", "Bibliographic", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.biblio", "Bibliografico", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.authorities", "Autoridades", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.authorities", "Authorities", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.authorities", "Autoridades", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfield_autocomplete.vocabulary", "Vocabulário", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfield_autocomplete.vocabulary", "Vocabulary", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfield_autocomplete.vocabulary", "Vocabulario", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.translations.error.invalid_language", "Idioma em branco ou desconhecido", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.translations.error.invalid_language", "The \"language_code\" field is mandatory", 0);
				Translations.addSingleTranslation("global", "es",    "administration.translations.error.invalid_language", "El campo \"language_code\" es obligatorio", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.subfields", "Subcampos", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.subfields", "Subfields", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.subfields", "Subcampos", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.translations.save", "Salvar traduções", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.translations.save", "Save translations", 0);
				Translations.addSingleTranslation("global", "es",    "administration.translations.save", "Guardar traducciones", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.translations.edit.title", "Editar traduções", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.translations.edit.title", "Edit translations", 0);
				Translations.addSingleTranslation("global", "es",    "administration.translations.edit.title", "Editar traducciones", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.translations.edit.description", "<p>Abaixo você pode editar as traduções sem ter que baixar o arquivo. Esta tela é ideal para rápidas alterações em textos do Biblivre. O idioma exibido abaixo é o mesmo que está atualmente em uso. Para editar as traduções de outro idioma, troque o idioma atual do Biblivre por outro no topo da página. Caso você tenha personalizado seu Biblivre na tela de Personalizacao, você precisará ajustar os nomes dos campos criados para todos os idiomas instalados. Para facilitar nesse trabalho, clique na caixa \"Exibir apenas os campos sem tradução\".</p><p>Você pode também adicionar um novo idioma diretamente nesta tela. Para tanto, basta alterar o valor do campo \"language_code\".</p>", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.translations.edit.description", "<p>Below you can edit the translations without downloading the translations file. This screen is ideal for rapid changes in Biblivre texts. The language displayed below is the same as the one currently in use. To edit translations from another language, change the current language at the top of the page. If you have customized your Biblivre in the Customization screen, you need to adjust the field names created for all languages installed. To facilitate this work, click the box \"Display only untranslated fields\".</p><p>You can also add a new language directly on this screen. To do so, just change the value of the \"language_code\" field.</p>", 0);
				Translations.addSingleTranslation("global", "es",    "administration.translations.edit.description", "<p>A continuación puede editar las traducciones sin tener que descargar el archivo. Esta pantalla es ideal para los rápidos cambios en los textos Biblivre. El idioma que se muestra a continuación es la misma que está actualmente en uso. Para editar las traducciones de otro idioma, cambie el idioma en la parte superior de la página. Si ha personalizado su Biblivre en la pantalla de Personalización, es necesario ajustar los nombres de los campos creados para todos los idiomas instalados. Para facilitar este trabajo, haga click en la casilla \"Mostrar sólo los campos sin traducir\".</p><p>También puede añadir un nuevo idioma directamente en esta pantalla. Para ello, basta cambiar el valor del campo \"language_code\".</p>", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.translations.edit.filter", "Exibir apenas os campos sem tradução", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.translations.edit.filter", "Display only untranslated fields", 0);
				Translations.addSingleTranslation("global", "es",    "administration.translations.edit.filter", "Mostrar sólo los campos sin traducir", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.brief_customization.available_fields.description", "Os campos abaixo estão configurados no Formulário Catalográfico, porém não serão exibidos no Resumo Catalográfico.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.brief_customization.available_fields.description", "Save translations", 0);
				Translations.addSingleTranslation("global", "es",    "administration.brief_customization.available_fields.description", "Guardar traducciones", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.indicator.label_value", "Valor", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.indicator.label_value", "Value", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.indicator.label_value", "Valor", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.indicator.label_text", "Texto", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.indicator.label_text", "Text", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.indicator.label_text", "Texto", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.button_add_field", "Adicionar Campo", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.button_add_field", "Add Tag", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.button_add_field", "Agregar Campo", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.error.existing_tag", "Já existe um Campo com esta tag.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.error.existing_tag", "Tag already exists.", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.error.existing_tag", "Campo ya existe.", 0);

				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.error.existing_subfield", "Já existe um Subcampo com esta tag.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.error.existing_subfield", "Subfield already exists.", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.error.existing_subfield", "Subcampo ya existe.", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.confirm_delete_datafield_title", "Excluir Campo", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.confirm_delete_datafield_title", "Delete Datafield", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.confirm_delete_datafield_title", "Excluir Campo", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.confirm_delete_datafield_description", "Você realmente deseja excluir este campo? Esta operação é irreversível, e o campo só será apresentado na aba Marc.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.confirm_delete_datafield_description", "Do you really wish to delete this datafield? This operation cannot be undone, and the field will be displayed only on Marc tab.", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.confirm_delete_datafield_description", "¿Usted realmente desea excluir este campo? Esta operación es irreversible, y el campo sólo se mostrará en la pestaña Marc.", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.form_customization.error.invalid_tag", "Campo Marc inválido. O campo Marc deve ser numérico, e possuir 3 digitos.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.form_customization.error.invalid_tag", "Invalid Datafield Tag. The datafield Tag should be a 3 digits number.", 0);
				Translations.addSingleTranslation("global", "es",    "administration.form_customization.error.invalid_tag", "Campo Marc inválido. El campo Marc debe ser numérico con 3 dígitos.", 0);
				
				dao.commitUpdate(version, con);
			}

			 version = "5.0.1";
			 if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.replaceBiblivreVersion(con);

				dao.commitUpdate(version, con);

				Translations.reset();
				Configurations.reset();
			 }

			 version = "5.0.1b";
			 if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.description", "Use esta opção caso você queira restaurar um backup existente do Biblivre 4. Caso o Biblivre encontre backups salvos em seus documentos, você poderá restaurá-los diretamente da lista abaixo. Caso contrário, você deverá enviar um arquivo de backup (extensão <strong>.b4bz</strong> ou <strong>.b5bz</strong>) através do formulário.", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.description", "Use this option should you wish to restore an existing Biblivre 4 backup. Should Biblivre find backups saved in your documents, you will be able to restore them directly from the list below. Otherwise, you will have to send a backup file (extension <strong>.b4bz</strong> or <strong>.b5bz</strong>) by means of the form.", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.description", "administration.setup.biblivre4restore.description', 'Use esta opción en caso que usted quiera restaurar un backup existente del Biblivre 4. En caso que el Biblivre encuentre backups guardados en sus documentos, usted podrá restaurarlos directamente de la lista abajo. En caso contrario, usted deberá enviar un archivo de backup (extensión <strong>.b4bz</strong> o <strong>.b5bz</strong>) a través del formulario.", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "multi_schema.select_restore.description", "Use esta opção caso você queira restaurar um backup existente do Biblivre 4. Caso o Biblivre encontre backups salvos em seus documentos, você poderá restaurá-los diretamente da lista abaixo. Caso contrário, você deverá enviar um arquivo de backup (extensão <strong>.b4bz</strong>) através do formulário.", 0);
				Translations.addSingleTranslation("global", "en-US", "multi_schema.select_restore.description", "Use this option if you wish to restore an existing Biblivre 4 backup. When the Biblivre find backups saved among your documents, you will be able to restore the, directly from the list below. Otherwise, you will have to send a backup file (extension <strong>.b4bz</strong>) through the form.", 0);
				Translations.addSingleTranslation("global", "es", "multi_schema.select_restore.description", "Use esta opción en caso de desear restaurar un backup existente del Biblivre 4. En el caso de que el Biblivre encuentre backups guardados en sus documentos, usted podrá restaurarlos directamente de la lista siguiente. De lo contrario, usted deberá enviar un archivo de backup (extensión <strong>.b4bz</strong>) a través del formulario.", 0);
				
				Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore", "Restaurar um Backup do Biblivre 4 ou Biblivre 5", 0);
				Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore", "Restore a Biblivre 4 or Biblivre 5 Backup", 0);
				Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore", "Restaurar un Backup del Biblivre 4 o Biblivre 5", 0);
				 
				dao.commitUpdate(version, con);

				Translations.reset();
				Configurations.reset();
			 }
			
			// version = "4.0.X";
			// if (!installedVersions.contains(version)) {
			// con = dao.beginUpdate();
			// dao.commitUpdate(version, con);
			// }

			return true;
		} catch (Exception e) {
			dao.rollbackUpdate(con);
			e.printStackTrace();
		}

		return false;
	}

	public static boolean schemaUpdate(String schema) {
		UpdatesDAO dao = UpdatesDAO.getInstance(schema);

		Connection con = null;
		try {
			if (!dao.checkTableExistance("versions")) {
				dao.fixVersionsTable();
			}

			Set<String> installedVersions = dao.getInstalledVersions();

			String version = "4.0.0b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.1b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.2b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.3b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.4b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.5b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.fixUserNameAscii(con);
				dao.commitUpdate(version, con);
			}

			version = "4.0.6b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.7b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.8b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.9b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.10b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				try {
					IndexingBO bo = IndexingBO.getInstance(schema);
					bo.reindex(RecordType.AUTHORITIES);
				} catch (Exception e) {
					e.printStackTrace();
				}

				dao.commitUpdate(version, con);
			}

			version = "4.0.11b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.0.12b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.fixAuthoritiesAutoComplete();
				dao.fixVocabularyAutoComplete();

				try {
					IndexingBO bo = IndexingBO.getInstance(schema);
					bo.reindex(RecordType.VOCABULARY);
				} catch (Exception e) {
					e.printStackTrace();
				}

				dao.commitUpdate(version, con);
			}

			version = "4.1.0";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.1";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.2";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.3";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.4";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.5";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.6";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				try {
					IndexingBO bo = IndexingBO.getInstance(schema);
					bo.reindex(RecordType.BIBLIO);
					bo.reindex(RecordType.AUTHORITIES);
					bo.reindex(RecordType.VOCABULARY);
				} catch (Exception e) {
					e.printStackTrace();
				}

				
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.7";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				dao.fixHoldingCreationTable(con);
				dao.fixCDDBiblioBriefFormat(con);
				
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.8";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.9";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "4.1.10";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				dao.addIndexingGroup(con, RecordType.BIBLIO, "publisher", "260_b", true);
				dao.addIndexingGroup(con, RecordType.BIBLIO, "series", "490_a", true);

				dao.addBriefFormat(con, RecordType.BIBLIO, "501", "${a}", 28);
				dao.addBriefFormat(con, RecordType.BIBLIO, "530", "${a}", 31);
				dao.addBriefFormat(con, RecordType.BIBLIO, "595", "${a}", 33);
				
				dao.updateBriefFormat(con, RecordType.BIBLIO, "245", "${a}_{: }${b}_{ / }${c}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "100", "${a}_{ - }${d}_{ }(${q})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "110", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "111", "${a}_{. }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "130", "${a}_{. }${l}_{. }${f}");
				
				dao.updateBriefFormat(con, RecordType.BIBLIO, "600", "${a}_{. }${b}_{. }${c}_{. }${d}_{ - }${x}_{ - }${y}_{ - }${z}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "610", "${a}_{. }${b}_{ - }${x}_{ - }${y}_{ - }${z}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "611", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})_{ - }${x}_{ - }${y}_{ - }${z}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "630", "${a}_{. }(${d})_{ - }${x}_{ - }${y}_{ - }${z}");
				
				dao.updateBriefFormat(con, RecordType.BIBLIO, "700", "${a}_{. }${d}");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "710", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "711", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "630", "${a}_{. }(${d})");
				
				dao.updateIndexingGroup(con, RecordType.BIBLIO, "title", "245_a_b,243_a_f,240_a,730_a,740_a_n_p,830_a_v,250_a,130_a");

				Fields.reset(schema, RecordType.BIBLIO);
				IndexingGroups.reset(schema, RecordType.BIBLIO);
				
				dao.commitUpdate(version, con);
			}

			version = "4.1.10a";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();

				dao.updateBriefFormat(con, RecordType.BIBLIO, "490", "(${a}_{ ; }${v})");
				dao.updateBriefFormat(con, RecordType.BIBLIO, "830", "${a}_{. }${p}_{ ; }${v}");

				dao.invalidateIndex(con, RecordType.BIBLIO);

				
				Fields.reset(schema, RecordType.BIBLIO);				

				dao.commitUpdate(version, con);
			}

			version = "4.1.11";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}
			
			version = "4.1.11a";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				dao.fixAuthoritiesBriefFormat(con);
				
				dao.commitUpdate(version, con);
			}
			
			version = "5.0.0";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				for (RecordType recordType : RecordType.values()) {
					dao.addDatafieldSortOrderColumns(con, recordType);
					dao.addSubfieldSortOrderColumns(con, recordType);
				}
			
				dao.commitUpdate(version, con);
			}
			
			 version = "5.0.1";
			 if (!installedVersions.contains(version)) {
				 con = dao.beginUpdate();
				 dao.replaceBiblivreVersion(con);

				 dao.commitUpdate(version, con);

				 Translations.reset();
				 Configurations.reset();
			 }

			version = "5.0.1b";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				dao.addBriefFormatSortOrderColumns(con, RecordType.BIBLIO);
				dao.addBriefFormatSortOrderColumns(con, RecordType.AUTHORITIES);
				dao.addBriefFormatSortOrderColumns(con, RecordType.VOCABULARY);
			
				dao.commitUpdate(version, con);
			}

			version = "5.0.2";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}
			
			version = "5.0.3";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				dao.commitUpdate(version, con);
			}

			version = "5.0.4c";
			if (!installedVersions.contains(version)) {
				con = dao.beginUpdate();
				
				dao.updateZ3950Address(con, "Library of Congress Online Catalog - EUA", "lx2.loc.gov");
				
				dao.commitUpdate(version, con);
			}

			// version = "4.0.X";
			// if (!installedVersions.contains(version)) {
			// con = dao.beginUpdate();
			// dao.commitUpdate(version, con);
			// }

			
			return true;
		} catch (Exception e) {
			dao.rollbackUpdate(con);
			e.printStackTrace();
		}

		return false;
	}

	public static String getUID() {
		String uid = Configurations.getString(Constants.GLOBAL_SCHEMA, Constants.CONFIG_UID);

		if (StringUtils.isBlank(uid)) {
			uid = UUID.randomUUID().toString();

			ConfigurationsDTO config = new ConfigurationsDTO();
			config.setKey(Constants.CONFIG_UID);
			config.setValue(uid);
			config.setType("string");
			config.setRequired(false);

			Configurations.save(Constants.GLOBAL_SCHEMA, config, 0);
		}

		return uid;
	}

	public static String checkUpdates() {
		String uid = Updates.getUID();
		String version = Updates.getVersion();

		PostMethod updatePost = new PostMethod(Constants.UPDATE_URL);
		updatePost.addParameter("uid", TextUtils.biblivreEncrypt(uid));
		updatePost.addParameter("version", TextUtils.biblivreEncrypt(version));

		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		try {
			int status = client.executeMethod(updatePost);

			if (status == HttpStatus.SC_OK) {
				return updatePost.getResponseBodyAsString();
			}
			updatePost.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void fixPostgreSQL81() {
		UpdatesDAO dao = UpdatesDAO.getInstance("public");

		try {
			if (!dao.checkFunctionExistance("array_agg")) {
				String version = dao.getPostgreSQLVersion();

				if (version.contains("8.1")) {
					dao.create81ArrayAgg();
				} else {
					dao.createArrayAgg();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
