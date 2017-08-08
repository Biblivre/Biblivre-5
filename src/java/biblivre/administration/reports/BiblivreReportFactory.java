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
package biblivre.administration.reports;


public class BiblivreReportFactory {

	private BiblivreReportFactory(){}

	public static IBiblivreReport getBiblivreReport(ReportType type) {
		switch (type) {
			case SEARCHES_BY_DATE: return new SearchesByDateReport(); 
			case LENDINGS_BY_DATE: return new LendingsByDateReport(); 
			case ALL_USERS: return new AllUsersReport(); 
			case DEWEY: return new DeweyReport(); 
			case LATE_LENDINGS: return new LateReturnLendingsReport(); 
			case AUTHOR_BIBLIOGRAPHY: return new BibliographyReport(); 
			case HOLDING_CREATION_BY_DATE: return new HoldingCreationByDatetReport();
			case ACQUISITION: return new RequestsByDateReport(); 
			case SUMMARY: return new SummaryReport(); 
			case USER: return new UserReport();
			case RESERVATION: return new ReservationReport(); 
			case ASSET_HOLDING: return new AssetHoldingReport(); 
			case ASSET_HOLDING_FULL: return new AssetHoldingFullReport(false); 
			case TOPOGRAPHIC_FULL: return new AssetHoldingFullReport(true); 
			case ASSET_HOLDING_BY_DATE: return new AssetHoldingByDateReport(); 
			case CUSTOM_COUNT: return new CustomCountReport();
			default:
				return null;
		}
	}

}
