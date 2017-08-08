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

	Core.subscribe(CirculationSearch.prefix + 'record-selected', function(e, id) {
		CirculationSearch.receiptList = [];
		
		if (!HoldingSearch.lastSearchResult) {
			return;
		}
		
		setTimeout(function() {
			HoldingSearch.processResultTemplate();
		}, 50);
	});
	
	Core.subscribe(CirculationSearch.prefix + 'display-search', function(e, id) {
		delete CirculationSearch.selectedRecord;
	});
	
	Core.subscribe(HoldingSearch.prefix + 'holding-returned', function(e, id) {
		if (!CirculationSearch.lastSearchResult) {
			return;
		}

		CirculationSearch.receiptList.push(id);		
		
		for (var key in CirculationSearch.lastSearchResult) {
			if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CirculationSearch.lastSearchResult[key];
			if (!result.lendingInfo) {
				continue;
			}

			for (var i = 0; i < result.lendingInfo.length; i++) {
				if (result.lendingInfo[i].lending.id == id) {
					result.lendingInfo.splice(i, 1);

					(function(localResult) {
						CirculationSearch.root.find('.user_lending[rel="' + id + '"]').fadeOut('normal', function() {
							// Update circulation search
							CirculationSearch.processResultTemplate();
							
							// Update highlight
							if (CirculationSearch.selectedRecord && CirculationSearch.selectedRecord.id == localResult.id) {
								CirculationSearch.root.find('.selected_highlight .clone').processTemplate(localResult);

								$('#lending_receipt_button').removeClass('disabled');
							}							
						});
					})(result); 
					
					break;
				}
			}
		}
	});
	
	Core.subscribe(HoldingSearch.prefix + 'holding-returned', function(e, id) {
		if (!HoldingSearch.lastSearchResult) {
			return;
		}

		for (var key in HoldingSearch.lastSearchResult) {
			if (!HoldingSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = HoldingSearch.lastSearchResult[key];
			if (!result.lending) {
				continue;
			}

			if (result.lending.id == id) {
				delete result.lending;
				delete result.user;

				HoldingSearch.processResultTemplate();
				break;
			}
		}
	});

	Core.subscribe(HoldingSearch.prefix + 'holding-renewed', function(e, id, lendingInfo) {
		if (!CirculationSearch.lastSearchResult) {
			return;
		}
		
		CirculationSearch.receiptList.push(lendingInfo.lending.id);

		for (var key in CirculationSearch.lastSearchResult) {
			if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CirculationSearch.lastSearchResult[key];
			if (!result.lendingInfo) {
				continue;
			}

			for (var i = 0; i < result.lendingInfo.length; i++) {
				if (result.lendingInfo[i].lending.id == id) {
					result.lendingInfo[i] = lendingInfo;

					(function(localResult) {
						// Update highlight
						if (CirculationSearch.selectedRecord && CirculationSearch.selectedRecord.id == localResult.id) {
							CirculationSearch.root.find('.selected_highlight .clone').processTemplate(localResult);
							
							$('#lending_receipt_button').removeClass('disabled');
						}							
					})(result); 
					
					break;
				}
			}
		}
	});

	Core.subscribe(HoldingSearch.prefix + 'holding-renewed', function(e, id, lendingInfo) {
		if (!HoldingSearch.lastSearchResult) {
			return;
		}

		for (var key in HoldingSearch.lastSearchResult) {
			if (!HoldingSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = HoldingSearch.lastSearchResult[key];
			if (!result.lending) {
				continue;
			}

			if (result.lending.id == id) {
				result.lending = lendingInfo.lending;
				result.user = lendingInfo.user;

				HoldingSearch.processResultTemplate();
				break;
			}
		}
	});

	Core.subscribe(HoldingSearch.prefix + 'holding-lent', function(e, id, lendingInfo) {
		if (!CirculationSearch.lastSearchResult) {
			return;
		}

		CirculationSearch.receiptList.push(lendingInfo.lending.id);
		
		for (var key in CirculationSearch.lastSearchResult) {
			if (!CirculationSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = CirculationSearch.lastSearchResult[key];
			if (result.user.id == lendingInfo.user.id) {
				result.lendingInfo = result.lendingInfo || [];
				result.lendingInfo.push(lendingInfo);

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

						$('#lending_receipt_button').removeClass('disabled');
						
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

	Core.subscribe(HoldingSearch.prefix + 'holding-lent', function(e, id, lendingInfo) {
		if (!HoldingSearch.lastSearchResult) {
			return;
		}

		for (var key in HoldingSearch.lastSearchResult) {
			if (!HoldingSearch.lastSearchResult.hasOwnProperty(key)) {
				continue;
			}

			var result = HoldingSearch.lastSearchResult[key];			
			if (result.holding.id == lendingInfo.holding.id) {
				result.lending = lendingInfo.lending;
				result.user = lendingInfo.user;

				HoldingSearch.processResultTemplate();
				break;
			}
		}
	});
});
