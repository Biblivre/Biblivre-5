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

var HoldingInputClass = {
	formElementId: '#biblivre_holding_form',
	initialize: function() {
		$('#biblivre_holding_marc').setTemplateElement('biblivre_holding_marc_template');


		Core.subscribe(this.prefix + 'record-created', function(e, data) {
			if (CatalogingSearch.selectedRecord && CatalogingSearch.selectedRecord.id == data.record_id) {
				CatalogingSearch.selectedRecord.holdings = CatalogingSearch.selectedRecord.holdings || [];
				CatalogingSearch.selectedRecord.holdings.push(data);

				var searchResult = (CatalogingSearch.lastSearchResult || {})[CatalogingSearch.selectedRecord.id] || {};

				
				CatalogingSearch.selectedRecord.holdings_count++;
				searchResult.holdings_count++;

				if (data.availability == 'available') {
					CatalogingSearch.selectedRecord.holdings_available++;
					searchResult.holdings_available++;
				}
				
				CatalogingSearch.loadHoldingList(CatalogingSearch.selectedRecord);
				CatalogingSearch.processResultTemplate();
			}
		}, this);
		
		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {
			if (CatalogingSearch.selectedRecord) {
				var holdings = CatalogingSearch.selectedRecord.holdings || [];
				
				for (var i = 0; i < holdings.length; i++) {
					var holding = holdings[i];

					if (holding.id == id) {
						holdings.splice(i, 1);

						var searchResult = (CatalogingSearch.lastSearchResult || {})[CatalogingSearch.selectedRecord.id] || {};
						
						CatalogingSearch.selectedRecord.holdings_count--;
						searchResult.holdings_count--;

						if (holding.availability == 'available') {
							CatalogingSearch.selectedRecord.holdings_available--;
							searchResult.holdings_available--;
						}

						CatalogingSearch.processResultTemplate();				
						CatalogingSearch.loadHoldingList(CatalogingSearch.selectedRecord);

						return;
					}
				}
			}
		}, this);
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'holding_form':
				root = $('#biblivre_holding_form, .biblivre_holding_form_body');
	
				this.setAsEditable('holding_form');
	
				root.find('fieldset.repeated, div.repeated, fieldset.autocreated, input.autocreated').remove();
				root.find('fieldset.datafield').find(':input').not('.dont_clear').val('');
				break;

			case 'holding_marc':
				this.setAsEditable('holding_marc');
				break;			
		}
	},
	clearAll: function() {
		this.clearTab('holding_form');
		this.clearTab('holding_marc');
	},
	editing: false,
	recordIdBeingEdited: null,

	editRecord: function(id) {
		if (this.editing) {
			return;
		}

		if (Core.trigger(this.prefix + 'edit-record-start', id) === false) {
			return;
		}

		this.editing = true;
		this.recordIdBeingEdited = id;
	
		var tab = this.search.selectedTab;
		if (tab != 'holding_form' && tab != 'holding_marc') {
			tab = 'holding_form';
		} else {
			this.setAsEditable('holding_form');
			this.setAsEditable('holding_marc');
		}

		Core.changeTab(tab, this.search, { keepEditing: true, skipConvert: true });
	},
	getRecordData: function(from) {
		if (from == 'holding_form') {
			return this.createJson($('#biblivre_holding_form'));
		} else if (from == 'holding_marc') {
			return $('#biblivre_holding_marc_textarea').val();
		}
	},
	getAvailability: function(from) {
		if (from == 'holding_form') {
			return $('div.biblivre_holding_form_body select[name=holding_availability]').val();
		} else if (from == 'holding_marc') {
			return $('div.biblivre_holding_marc_body select[name=holding_availability]').val();
		}
	},
	getMaterialType: function() {
		return 'holdings';
	},
	getCurrentDatabase: function() {
		return CatalogingInput.getCurrentDatabase();
	},
	getNewRecord: function() {
		return {
			id: ' ',
			record_id: CatalogingSearch.selectedRecord.id,
			json: {},
			marc: ''
		};
	},
	getSaveRecord: function(saveAsNew) {
		var oldId = this.recordIdBeingEdited || 0;
		var id = (saveAsNew) ? 0 : this.recordIdBeingEdited;
		var record_id = CatalogingSearch.selectedRecord.id;
		var from = this.search.selectedTab;
		var data = this.getRecordData(from);
		var availability = this.getAvailability(from);
		
		return {
			oldId: oldId,
			id: id,
			record_id: record_id,
			availability: availability,
			from: from,
			data: data
		};
	},
	deleteRecordConfirm: function() {
		return _(this.type + '.confirm_delete_record.forever');
	},
	getDeleteRecord: function(id) {
		return {
			id: id,
			database: this.getCurrentDatabase()
		};
	}
};

var HoldingInput = new Input($.extend(HoldingInputClass, CatalogingFormClass));