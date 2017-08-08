/**
 *  Este arquivo é parte do Biblivre5.
 *  
 *  Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 *  Licença, ou (caso queira) qualquer versão posterior.
 *  
 *  Este programa é distribuído na esperança de que possa ser  útil, 
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *  
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 * 
 */
$(document).ready(function() {

	Core.subscribe(CirculationSearch.prefix + 'display-search', function(e, id) {
		delete CirculationSearch.selectedRecord;
	});
	
	Core.subscribe(CatalogingSearch.prefix + 'reservation-deleted', function(e, id) {
		if (!CirculationSearch.lastSearchResult) {
			return;
		}

		for (var key in CirculationSearch.lastSearchResult) {
			if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CirculationSearch.lastSearchResult[key];
			if (!result.reservationInfoList) {
				continue;
			}

			for (var i = 0; i < result.reservationInfoList.length; i++) {
				if (result.reservationInfoList[i].reservation.id == id) {
					result.reservationInfoList.splice(i, 1);

					(function(localResult) {
						CirculationSearch.root.find('.user_reservation[rel="' + id + '"]').fadeOut('normal', function() {
							// Update circulation search
							CirculationSearch.processResultTemplate();
							
							// Update highlight
							if (CirculationSearch.selectedRecord && CirculationSearch.selectedRecord.id == localResult.id) {
								CirculationSearch.root.find('.selected_highlight .clone').processTemplate(localResult);
							}							
						});
					})(result); 
					
					break;
				}
			}
		}
	});
	
	Core.subscribe(CatalogingSearch.prefix + 'reservation-deleted', function(e, id) {
		if (!CatalogingSearch.lastSearchResult) {
			return;
		}

		for (var key in CatalogingSearch.lastSearchResult) {
			if (!CatalogingSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CatalogingSearch.lastSearchResult[key];
			if (!result.reservationInfo) {
				continue;
			}

			for (var i = 0; i < result.reservationInfo.length; i++) {
				if (result.reservationInfo[i].reservation.id == id) {
					result.reservationInfo.splice(i, 1);

					CatalogingSearch.processResultTemplate();
					return;
				}
			}
		}
	});

	Core.subscribe(CatalogingSearch.prefix + 'reservation-created', function(e, id, reservationInfo) {
		if (!CirculationSearch.lastSearchResult) {
			return;
		}

		for (var key in CirculationSearch.lastSearchResult) {
			if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CirculationSearch.lastSearchResult[key];
			if (result.user.id == reservationInfo.user.id) {
				result.reservationInfoList = result.reservationInfoList || [];
				result.reservationInfoList.push(reservationInfo);

				(function(localResult) {
					// Update circulation search
					CirculationSearch.processResultTemplate();

					// Update highlight
					if (CirculationSearch.selectedRecord && CirculationSearch.selectedRecord.id == localResult.user.id) {
						var contentOuter = $('#content_outer');
						var contentInner = $('#content_inner');
						
						var scroll = contentOuter.scrollTop();
						var innerHeight = contentInner.height();
						var outerHeight = contentOuter.height();
						
						CirculationSearch.root.find('.selected_highlight .clone').processTemplate(localResult);

						var innerHeightDiff = contentInner.height() - innerHeight;
						var outerHeightDiff = contentOuter.height() - outerHeight;
						
						setTimeout(function() {
							contentOuter.scrollTop(scroll + innerHeightDiff + outerHeightDiff);
						}, 10);
					}							
				})(result);

				break;
			}
		}
	});

	Core.subscribe(CatalogingSearch.prefix + 'reservation-created', function(e, id, reservationInfo) {
		if (!CatalogingSearch.lastSearchResult) {
			return;
		}

		for (var key in CatalogingSearch.lastSearchResult) {
			if (!CatalogingSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CatalogingSearch.lastSearchResult[key];			
			if (result.id == reservationInfo.biblio.id) {
				if (!result.reservationInfo) {
					result.reservationInfo = [];
				}
				
				result.reservationInfo.push(reservationInfo);

				CatalogingSearch.processResultTemplate();
				break;
			}
		}
	});
	
	CirculationSearch.search('simple');
});
