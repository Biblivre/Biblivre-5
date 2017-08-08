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
var CatalogingFormClass = {
	formInitialized: false,
	initializeForm: function(form, fields, type) {
		if (this.formInitialized) {
			return;
		}
	
		this.createForm(fields);
	
		this.initializeMarcHelp(form, type);
		this.initializeFormCollapse(form);
		this.initializeRepeatableDataFields(form);
		this.initializeRepeatableSubFields(form);
	
		this.formInitialized = true;
		Core.trigger(this.prefix + 'form-initialized');
	},
	createForm: function(datafields) {
		// Function optimized for better performance,
		// unfortunately we reduced its readability
		var html = [];
		var translationPrefix = 'marc.bibliographic.datafield.';
		if (this.formElementId && this.formElementId == '#biblivre_holding_form') {
			translationPrefix = 'marc.holding.datafield.';
		}
		
		for (var i = 0; i < datafields.length; i++) {
			var datafield = datafields[i];
			var hasMaterialType = datafield.material_type && datafield.material_type.length > 0;
	
			if (hasMaterialType) {
				html.push('<div class="material_type" data="', datafield.material_type.join(','), '">');
			}
			html.push('<fieldset class="datafield');
	
			if (datafield.repeatable) {
				html.push(' repeatable');
			}
	
			if (datafield.collapsed) {
				html.push(' collapsed');
			}
	
			html.push('" data="', datafield.datafield, '">');
	
			html.push('<legend>');
			html.push(_(translationPrefix + datafield.datafield));
			html.push('<span class="marc_numbering">(', datafield.datafield, ')</span>');
			html.push('</legend>');
	
			html.push('<div class="collapse"></div>');
	
			html.push('<div class="subfields">');
			
			for (var ind = 1; ind <= 2; ind++) {
				var indicators = datafield['indicator' + ind];
	
				if (indicators) {
					html.push('<div class="indicator">');
					
					html.push('<div class="label">');
					html.push(_(translationPrefix + datafield.datafield + '.indicator.' + ind));
					html.push('</div>');
					
					html.push('<div class="value">');
					html.push('<select name="ind',  ind,  '">');
					
					for (var j = 0; j < indicators.length; j++) {
						var indicator = indicators[j];
	
						html.push('<option value="', indicator, '">');
						html.push(_(translationPrefix + datafield.datafield + '.indicator.' + ind + '.' + indicator));
						html.push('</option>');
					}
					
					html.push('</select>');
					html.push('</div>');
	
					html.push('<div class="extra"><span class="marc_numbering">#', ind, '</span></div>');
					html.push('<div class="clear"></div>');
					
					html.push('</div>');
				}
			}
			
			if (!datafield.subfields) {
				datafield.subfields = [];
			}
			
			for (var sub = 0; sub < datafield.subfields.length; sub++) {
				var subfield = datafield.subfields[sub];
	
				html.push('<div class="subfield');
	
				if (subfield.repeatable) {
					html.push(' repeatable');
				}
	
				if (subfield.collapsed) {
					html.push(' secondary');
				}
	
				html.push('" data="', subfield.datafield, '">');
				
				html.push('<div class="label">');
				html.push(_(translationPrefix + datafield.datafield + '.subfield.' + subfield.subfield));
				html.push('</div>');
	
				var autocompleteClass = '';
				var dataAutocomplete = '';
				if (subfield.autocomplete_type != 'disabled') {
					autocompleteClass = 'autocomplete autocomplete_' + subfield.autocomplete_type;
					dataAutocomplete = 'data-ac="' +  subfield.autocomplete_type + '"';
				}
				
				
				html.push('<div class="value"><input type="text" ', dataAutocomplete ,' name="', subfield.subfield, '" class="finput ', autocompleteClass, '" /></div>');
				html.push('<div class="extra"><span class="marc_numbering">$' + subfield.subfield + '</span></div>');
				html.push('<div class="clear"></div>');
				
				html.push('</div>');
			}
	
			html.push('</div>');
			html.push('</fieldset>');
			
			if (hasMaterialType) {
				html.push('</div>');
			}
		}

		$(this.formElementId).html(html.join(''));
		this.autocomplete();
	},
	initializeMarcHelp: function(root, type) {
		root = root || this.root;
		
		var urls = {
			'cataloging.authorities': {
				prefix: 'http://www.loc.gov/marc/authority/ad',
				suffix: '.html'
			},
			'cataloging.bibliographic': {
				prefix: 'http://www.loc.gov/marc/bibliographic/bd',
				suffix: '.html'
			},
			'cataloging.holding': {
				prefix: 'http://www.loc.gov/marc/holdings/hd',
				suffix: '.html'
			},
			'cataloging.vocabulary': {
				prefix: 'http://www.loc.gov/marc/classification/cd',
				suffix: '.html'
			}
		};
	
		var url = urls[type];
		if (!url) {
			return false;
		}
	
		root.find('fieldset.datafield[data]:not(.dont_show_help)').each(function() {
			var fieldset = $(this);
	
			var tag = fieldset.attr('data');
			var legend = fieldset.children('legend');
	
			legend.append('<a href="' + url.prefix + tag + url.suffix + '" target="_blank" class="marc_help">[ ? ]</a>');
		});
	},
	initializeFormCollapse: function(root) {
		root = root || this.root;

		root.find('.collapse').click(function() {
			$(this).parents('fieldset:first').toggleClass('collapsed');
		
			return false;
		});
	
		root.find('fieldset').click(function() {
			$(this).removeClass('collapsed');
		}).each(function() {
			var fieldset = $(this);
			var hiddenSubfields = fieldset.find('.secondary');
	
			if (hiddenSubfields.size() > 0) {
				$('<div class="expand"></div>').html(_p('cataloging.form.hidden_subfields', [hiddenSubfields.size()])).click(function() {
					$(this).parents('fieldset:first').find('.secondary').show();
					$(this).remove();
				}).appendTo(fieldset);
			}
		});
	},
	repeatableDataFieldsInitialized: false,
	initializeRepeatableDataFields: function(root) {
		this.repeatableDataFieldsInitialized = true;
		root = root || this.root;
		
		var me = this;
		
		root.find('fieldset.repeatable').each(function() {
			var fieldset = $(this);
	
			fieldset.children('legend').append(
				$('<a href="javascript:void(0);" class="marc_repeat">[ ' + _('cataloging.tab.form.repeat') + ' ]</a>').click(function() {
					me.repeatDataField($(this).parents('fieldset'));
				})
			);
		});
	},
	repeatDataField: function(dataField) {
		var clone = dataField.clone(true);
	
		clone.find(':input').val('');
	
		if (!dataField.is('.autocreated')) {
			if (this.repeatableDataFieldsInitialized) {
				clone.find('legend a.marc_repeat').remove();
	
				clone.children('legend').append(
					$('<a href="javascript:void(0);" class="marc_remove">[ ' + _('cataloging.tab.form.remove') + ' ]</a>').click(function() {
						$(this).parents('fieldset').remove();
					})
				);
			}
	
			clone.removeClass('repeatable').addClass('repeated');
		}
	
		clone.insertAfter(dataField);
		
		if (clone.find('.autocomplete').size() > 0) {
			this.autocomplete(clone.find('.autocomplete').unautocomplete());
		}

		return clone;
	},
	repeatableSubFieldsInitialized: false,
	initializeRepeatableSubFields: function(root) {
		root = root || this.root;
		this.repeatableSubFieldsInitialized = true;
		var me = this;

		root.find('div.repeatable').each(function() {
			var div = $(this);
	
			var extra = div.find('div.extra');
	
			if (!extra.size()) {
				extra = $('<div class="extra"></div>').insertAfter(div.find('div.value'));
			}
	
			extra.append(
				$('<a href="javascript:void(0);" class="marc_repeat">[ ' + _('cataloging.tab.form.repeat') + ' ]</a>').click(function() {
					me.repeatSubField($(this).parents('div.subfield'));
				})
			);
		});
	},
	repeatSubField: function(subField) {
		var clone = subField.clone(true);
	
		clone.find(':input').val('');
	
		if (!subField.is('.autocreated')) {
			if (this.repeatableSubFieldsInitialized) {
				clone.find('.extra a.marc_repeat').remove();
		
				clone.find('.extra').append(
					$('<a href="javascript:void(0);" class="marc_remove">[ ' + _('cataloging.tab.form.remove') + ' ]</a>').click(function() {
						$(this).parents('div.subfield').remove();
					})
				);
			}
	
			clone.removeClass('repeatable').addClass('repeated');
		}
		
		clone.insertAfter(subField);
		
		if (clone.find('.autocomplete').size() > 0) {
			this.autocomplete(clone.find('.autocomplete').unautocomplete());
		}
	
		return clone;
	},
	populateDataField: function(fieldset, datafield) {
		for (var subfieldtag in datafield) {
			if (!datafield.hasOwnProperty(subfieldtag)) {
				continue;
			}
	
			var originalInput = fieldset.find(':input[name="' + subfieldtag + '"]:first');
			if (originalInput.size() === 0) {
				var autoCreatedSubField = $('<div class="subfield autocreated"></div>').appendTo(fieldset);
				originalInput = $('<input type="hidden" name="' + subfieldtag + '"/>').appendTo(autoCreatedSubField);
			}
	
			var subfields = datafield[subfieldtag];
	
			if (!$.isArray(subfields)) {
				// ind1 or ind2
				originalInput.val(subfields);
				continue;
			}
	
			// other subfields
			for (var i = 0; i < subfields.length; i++) {
				var subfield = subfields[i];
				var input = (i == 0) ? originalInput : this.repeatSubField(originalInput.parents('div.subfield')).find(':input[name="' + subfieldtag + '"]');
	
				input.val(subfield);
			}
		}
	},
	createJson: function(root) {
		var json = {};
		root = root || this.root;
	
		root.find('input.autocreated.controlfield[data]').each(function() {
			var controlField = $(this);
			json[controlField.attr('data')] = controlField.val();
		});
	
		root.find('fieldset.datafield[data]')
	//		.filter(function() {
	//			var $this = $(this);
	//			return ($this.is(':visible') || $this.is('.autocreated'));
	//		})
			.each(function() {
			var fieldSet = $(this);
			var dataFieldTag = fieldSet.attr('data');
			var dataField = {};
			var foundSubfield = false;
	
			fieldSet.find(':input').each(function() {
				var input = $(this);
				var subFieldTag = input.attr('name');
				var value = input.val();
	
				if (!value) {
					return;
				}
	
				if (subFieldTag == 'ind1' || subFieldTag == 'ind2') {
					dataField[subFieldTag] = value;
				} else {
					foundSubfield = true;
	
					if (dataField[subFieldTag]) {
						dataField[subFieldTag].push(value);
					} else {
						dataField[subFieldTag] = [value];
					}
				}
			});
	
			if (!foundSubfield) {
				return;
			}
	
			if (json[dataFieldTag]) {
				json[dataFieldTag].push(dataField);
			} else {
				json[dataFieldTag] = [dataField];
			}
		});
	
		return JSON.stringify(json);
	},
	loadJson: function(root, record) {
		root = root || this.root;
		
		for (var datafieldtag in record) {
			if (!record.hasOwnProperty(datafieldtag)) {
				continue;
			}
	
			if (parseInt(datafieldtag, 10) < 10) {
				$('<input type="hidden" class="controlfield autocreated" data="' + datafieldtag + '"/>').val(record[datafieldtag]).appendTo(root);
				continue;
			}
	
			var originalFieldset = root.find('fieldset.datafield[data="' + datafieldtag + '"]:first');
			if (originalFieldset.size() == 0) {
				originalFieldset = $('<fieldset class="datafield autocreated" data="' + datafieldtag + '"></fieldset>').appendTo(root);
			}
	
			var datafields = record[datafieldtag];
	
			for (var i = 0; i < datafields.length; i++) {
				var datafield = datafields[i];
				var fieldset = (i == 0) ? originalFieldset : this.repeatDataField(originalFieldset);
	
				this.populateDataField(fieldset, datafield);
			}
		}
	
		return record;
	},
	setAsReadOnly: function(tab) {
		var root;
		var me = this;
	
		switch (tab) {
			case 'form':
			case 'holding_form':
				root = $('#biblivre_' + tab + ', .biblivre_' + tab + '_body');
				me.search.selectedRecord.json = me.search.selectedRecord.json || {};

				root.find('fieldset[data]').each(function() {
					var form = $(this);

					if (!me.search.selectedRecord.json[form.attr('data')]) {
						form.addClass('readonly_hidden');
					}
				});
			
				root.find('input:not(.autocreated)').each(function() {
					var input = $(this);
			
					var value = input.val();
					if (value == '') {
						input.parents('.subfield:first').addClass('readonly_hidden');
					} else {
						input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
					}
				});
			
				root.find('select:not(.autocreated)').each(function() {
					var combo = $(this);
			
					var value = combo.find('option[value=' + combo.val() + ']').text();
					combo.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
				});
			
				root.find('.marc_repeat, .marc_remove').addClass('readonly_hidden');
				break;
	
			case 'marc':
			case 'holding_marc':
				root = $('#biblivre_' + tab + ', .biblivre_' + tab + '_body');
				var textarea = $('#biblivre_' + tab + '_textarea').addClass('readonly_hidden');
	
				var marcLines = textarea.val().split(/\r?\n/);
				var fields = [];
	
				for (var i = 0, len = marcLines.length; i < len; i++) {
					var line = marcLines[i];
	
					if (line && line.match(/(\d\d\d) (.*)/)) {
						var field = RegExp.$1;
						var value = RegExp.$2;
	
						fields.push({
							field: field,
							value: value
						});
					}
				}
				
				root.find('select').each(function() {
					var combo = $(this);
			
					var value = combo.find('option[value=' + combo.val() + ']').text();
					combo.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
				});
	
				root.processTemplate({ fields: fields });
				break;
		}
	},
	setAsEditable: function(tab) {
		var root;
		
		switch (tab) {
			case 'form':
			case 'holding_form':
				root = $('#biblivre_' + tab + ', .biblivre_' + tab + '_body');
				break;
	
			case 'marc':
			case 'holding_marc':
				root = $('div.tab_body[data-tab=' + tab + '], div.tab_extra_content[data-tab=' + tab + ']');
				break;
	
			default: 
				root = $(document);
				break;
		}
	
		root.find('.readonly_text').remove();
		root.find('.readonly_hidden').removeClass('readonly_hidden');
	},
	autocomplete: function(root) {
		if (!root) {
			root = this.root.find('.autocomplete');
		}
		
		var module = this.type;
		root.each(function() {
			var type = $(this).data('ac');
			$(this).autocomplete(window.location.pathname, {
				cacheLength: 1,
				matchContains: true,
				matchSubset: false,
				formatItem: function(item) {
					return (item.phrase) ? item.phrase : item;
				},
				extraParams: {
					controller: 'json',
					module: module,
					action: 'autocomplete',
					type: type,
					datafield: $(this).closest('.subfield').attr('data'),
					subfield: $(this).attr('name')
				},
				scroll: true,
				parse: function(json) {
 					var parsed = [];
					
					if (json.success && json.data) {
						if (type == 'authorities' || type == 'vocabulary') {
							if (json.data.data) {
								for (var i=0; i < json.data.data.length; i++) {
									var row = json.data.data[i];
									
									parsed.push({
										data: row,
										value: row.phrase,
										result: row.phrase
									});
								}
							}
						} else {
							for (var i=0; i < json.data.length; i++) {
								var row = json.data[i];
								
								parsed.push({
									data: row,
									value: row,
									result: row
								});
							}
						}
					}

					return parsed;
				}
			}).result(function(event, item) {
				var field = $(this);
				var type = field.data('ac');
				
				if (type == 'authorities' || type == 'vocabulary') {
					var datafield = field.closest('.subfield').attr('data');
					var marc = ((item.record || {}).json || {})[type == 'vocabulary' ? '150' : datafield];

					if (!marc || !marc[0]) {
						return;
					}
					
					marc = marc[0];
					
					var fieldset = field.closest('.subfields');
					
					for (var sub in marc) {
						if (!marc.hasOwnProperty(sub)) {
							continue;
						}
						
						var data = marc[sub];
						if (!$.isArray(data)) {
							fieldset.find(':input[name="' + sub + '"]').val(data);
						} else {
							fieldset.find(':input[name="' + sub + '"]').eq(0).val(data[0]);
							for (var i = 1; i < data.length; i++) {
								fieldset.find(':input[name="' + sub + '"]').eq(0).closest('.subfield').find('.marc_repeat').trigger('click');
								fieldset.find(':input[name="' + sub + '"]').eq(1).val(data[i]);
							}
						}
					}
				}
			});
		});
	},
	_convertXHR: null,
	convert: function(from, to, callback) {
		var data = this.getRecordData(from);
		var material_type = this.getMaterialType(from);
		var author_type = this.getAuthorType ? this.getAuthorType(from) : '';
		var database = this.getCurrentDatabase();
	
		if (this._convertXHR) {
			this._convertXHR.abort();
		}
	
		this._convertXHR = $.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: {
				controller: 'json',
				module: this.type,
				action: 'convert',
				from: from,
				to: to,
				material_type: material_type,
				author_type: author_type,
				data: data,
				id: this.recordIdBeingEdited,
				database: database
			},
			loadingTimedOverlay: true
		}).done(function(response) {
			if (response.success) {
				if ($.isFunction(callback)) {
					callback(response.data);		
				}
			} else {
				Core.msg(response);
			}
		});
	}
};

var CatalogingInputClass = {
	formElementId: '#biblivre_form',
	initialize: function() {
		if ($('#biblivre_record').length) {
			$('#biblivre_record').setTemplateElement('biblivre_record_template');
		}

		if ($('#biblivre_marc').length) {
			$('#biblivre_marc').setTemplateElement('biblivre_marc_template');
		}

		if ($('#biblivre_attachments').length) {
			$('#biblivre_attachments').setTemplateElement('biblivre_attachments_template');
		}

		//	$('#biblivre_holdings')
		//		.setTemplateElement('biblivre_holdings_template');

		this.initializeDatabaseArea();
	
		$.History.bind($.proxy(function(trigger) {
			return this.historyRead(trigger);
		}, this));

		var database = Core.qhs('database');
		if (!database) {
			this.updateDatabaseCount();
		}
	},
	initializeDatabaseArea: function() {
		if (this.type == 'cataloging.bibliographic') {
			$('.automatic_holding input[name="holding_volume_number"]').anyChange(function() {
				var el = $(this);
				if (el.val() != '') {
					$('.automatic_holding input[name="holding_volume_type"][value="number"]').prop('checked', true).trigger('change');
				} else {
					$('.automatic_holding input[name="holding_volume_type"][value="number"]').prop('checked', false).trigger('change');
				}
			});
	
			$('.automatic_holding input[name="holding_volume_count"]').anyChange(function() {
				var el = $(this);
				if (el.val() != '') {
					$('.automatic_holding input[name="holding_volume_type"][value="count"]').prop('checked', true).trigger('change');
				} else {
					$('.automatic_holding input[name="holding_volume_type"][value="count"]').prop('checked', false).trigger('change');
				}
			});
	
			$('.automatic_holding input[name="holding_volume_type"]').on('change', function() {
				var el = $(this);
				var value = el.val();
	
				switch (value) {
					case 'count':
						var other = $('.automatic_holding input[name="holding_volume_type"][value="number"]');
						$('.automatic_holding input[name="holding_volume_number"]').prop('disabled', el.prop('checked'));
						$('.automatic_holding input[name="holding_volume_count"]').prop('disabled', other.prop('checked'));
						break;
	
					case 'number':
						var other = $('.automatic_holding input[name="holding_volume_type"][value="count"]');					
						$('.automatic_holding input[name="holding_volume_count"]').prop('disabled', el.prop('checked'));
						$('.automatic_holding input[name="holding_volume_number"]').prop('disabled', other.prop('checked'));
						break;
				}
			});
			
			Core.subscribe(this.search.prefix + 'open-record', function(e, record) {
				$('.automatic_holding').hide();
			}, this);
		}
		
		Core.subscribe(this.prefix + 'edit-record-start', function(e, id) {
			if (id == ' ') {
				$('.automatic_holding').show().find(':input:not(:radio)').val('').trigger('change');
				$('.automatic_holding :radio').removeAttr('checked').trigger('change');
			} else {
				$('.automatic_holding').hide();
			}

			$('#database_selection_combo, #new_record_button').disable();
		}, this);

		Core.subscribe(this.prefix + 'edit-record-end', function() {
			$('.automatic_holding').hide();

			$('#database_selection_combo, #new_record_button').enable();
		}, this);

		Core.subscribe(this.prefix + 'cataloging-database-change', function() {
			Core.historyTrigger({
				database: this.getCurrentDatabase()
			});
	
			this.updateDatabaseCount();
			this.search.redoSearch();
			//	this.clearExportList();
		}, this);

		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {			
			if (this.search.lastSearchResult && this.search.lastSearchResult[id]) {
				var database = this.getCurrentDatabase();
				
				if (database == 'trash') {
					this.search.lastSearchResult[id].database = 'permanent_trash';
				} else {					
					this.search.lastSearchResult[id].database = 'trash';
				}
				
				this.search.processResultTemplate();
			}

			this.updateDatabaseCount();
			this.search.closeResult();
		}, this);

		Core.subscribe(this.prefix + 'record-created', function(e, record) {
			if (this.type == 'cataloging.bibliographic') {
				var autoHolding = {};
				var auto = false;
				
				$('.automatic_holding :input:not(:disabled)').each(function() {
					var el = $(this);

					if (!el.is(':radio') || el.prop('checked')) {
						autoHolding[el.attr('name')] = el.val();
					}

					if (!el.is(':radio') && $.trim(el.val())) {
						auto = true;
					}
				});

				if (auto) {
					autoHolding.record_id = record.id;
					autoHolding.database = record.database;
					autoHolding.controller = 'json';
					autoHolding.module = 'cataloging.holding';
					autoHolding.action = 'create_automatic_holding';
					
					record.promise = $.ajax({
						url: window.location.pathname,
						type: 'POST',
						dataType: 'json',
						data: autoHolding,
						context: this
					}).done(function(response) {
						if (!response) {
							Core.msg(response);
						}
					});
				}
			}
			
			this.updateDatabaseCount();
		}, this);
		
		Core.subscribe(this.prefix + 'record-moved', function(e, id) {
			this.updateDatabaseCount();
		}, this);

		Core.subscribe(this.prefix + 'search-submit', function() {
			this.updateDatabaseCount();
		}, this);
	},
	onInitializeSelectedRecordArea: function() {
		Core.subscribe(this.prefix + 'material-type-change', function(e, val) {
			if (val == 'all') {
				this.root.find('.material_type').show();
			} else {
				Core.toggleAreas('material_type', val, this.root);
			}

			if (this.search.selectedTab == 'marc' && this.editing) {
				this.convert(this.search.selectedTab, this.search.selectedTab, $.proxy(function(newData) {
					Core.changeTab('marc', this.search, { data: newData, keepEditing: true, skipConvert: true, force: true });
				}, this));
			}
		}, this);

		Core.subscribe(this.prefix + 'author-type-change', function(e, val) {
			if (val == 'all') {
				this.root.find('.material_type').show();
			} else {
				Core.toggleAreas('material_type', val, this.root);				
			}
		}, this);
	},
	historyRead: function(trigger) {
		var database = Core.historyCheckAndSet(trigger, 'database');
		if (database.changed) {
			this.setCurrentDatabase(database.value);
		}
	},
	setCurrentDatabase: function(database) {
		$('#database_selection_combo select').trigger('setvalue', database);
	},
	getCurrentDatabase: function() {
		var database = $('#database_selection_combo select').val() || 'main';
		return database;
	},
	_updateDatabaseCountXHR: null,
	updateDatabaseCount: function() {
		var database = this.getCurrentDatabase();
	
		if (this._updateDatabaseCountXHR) {
			this._updateDatabaseCountXHR.abort();
		}
	
		this._updateDatabaseCountXHR = $.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: {
				controller: 'json',
				module: this.type,
				action: 'item_count',
				database: database
			},
			loadingElement: '#database_count',
			context: this
		}).done(function(response) {
			var count = '-';
			if (response && response.count !== undefined) {
				count = _f(response.count);
			}

			this.setDatabaseCount(count);
		}).fail(function() {
			this.setDatabaseCount('-');
		});
	},
	setDatabaseCount: function(count) {
		$('#database_count').html(_('cataloging.database.record_count', [count]));
	},
	toggleAuthorType: function(val) {
		Core.trigger(this.prefix + 'author-type-change', val);
	},
	toggleMaterialType: function(val) {
		Core.trigger(this.prefix + 'material-type-change', val);
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'form':
				root = $('#biblivre_form, .biblivre_form_body');
	
				this.setAsEditable('form');
	
				root.find('fieldset.repeated, div.repeated, fieldset.autocreated, input.autocreated').remove();
				root.find('fieldset.datafield').find(':input').not('.dont_clear').val('');
				break;

			case 'marc':
				this.setAsEditable('marc');
				break;			
		}
	},
	clearAll: function() {
		this.clearTab('form');
		this.clearTab('marc');
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
		if (tab != 'form' && tab != 'marc') {
			tab = 'form';
		} else {
			this.setAsEditable('form');
			this.setAsEditable('marc');
		}

		Core.changeTab(tab, this.search, { keepEditing: true, skipConvert: true, force: true });
	},
	getRecordData: function(from) {
		if (from == 'form') {
			return this.createJson($('#biblivre_form'));
		} else if (from == 'marc') {
			return $('#biblivre_marc_textarea').val();
		} else if (from == 'record') {
			return $('#biblivre_record_textarea').val();
		}
	},
	getMaterialType: function(from) {
		if (from == 'form') {
			return $('div.biblivre_form_body select[name=material_type]').val();
		} else if (from == 'marc') {
			return $('div.biblivre_marc_body select[name=material_type]').val();
		} else if (from == 'record') {
			return $('#biblivre_record input[name=material_type]').val();
		}
	},
	getAuthorType: function(from) {
		if (from == 'form') {
			return $('div.biblivre_form_body select[name=author_type]').val();
		} else if (from == 'marc') {
			return $('div.biblivre_marc_body select[name=author_type]').val();
		} else if (from == 'record') {
			return $('#biblivre_record input[name=author_type]').val();
		}
	},
	getNewRecord: function() {
		return {
			id: ' ',
			author: ' ',
			title: ' ',
			publication_year: ' ',
			shelf_location: ' ',
			material_type: this.defaultMaterialType,
			json: {},
			marc: ''
		};
	},
	getSaveRecord: function(saveAsNew) {
		var oldId = this.recordIdBeingEdited || 0;
		var id = (saveAsNew) ? 0 : this.recordIdBeingEdited;
		var from = this.search.selectedTab;
		var database = this.getCurrentDatabase();
		var data = this.getRecordData(from);
		var material_type = this.getMaterialType(from);
		var author_type = this.getAuthorType(from);
		
		return {
			oldId: oldId,
			id: id,
			from: from,
			data: data,
			material_type: material_type,
			author_type: author_type,
			database: database
		};
	},
	deleteRecordConfirm: function() {
		return _(this.getCurrentDatabase() == 'trash' ? this.type + '.confirm_delete_record.forever' : this.type + '.confirm_delete_record.trash');
	},
	getDeleteRecord: function(id) {
		return {
			id: id,
			database: this.getCurrentDatabase()
		};
	},
	getOverlayClass: function(record) {
		var database = this.getCurrentDatabase();

		if (record.database != database) {
			if (record.database == 'trash' || record.database == 'permanent_trash') {
				return 'overlay_error';
			} else {
				return 'overlay_normal';
			}
		}
		
		return '';
	},
	getOverlayText: function(record) {
		var database = this.getCurrentDatabase();

		if (record.database != database) {
			if (record.database == 'permanent_trash') {
				return _('cataloging.database.record_deleted');
			} else {
				return _('cataloging.database.record_moved', [_('cataloging.database.' + record.database + '_full')]);
			}
		}
		
		return '';
	},
	confirmMoveSelectedRecords: function(database) {
		Core.popup({
			//title: _(this.type + '.confirm_move_record_title'),
			//description: _p(this.type + '.confirm_move_record_description', this.search.selectedList.length),
			title: _('cataloging.bibliographic.confirm_move_record_title'),
			description: _p('cataloging.bibliographic.confirm_move_record_description', this.search.selectedList.length),
			okHandler: $.proxy(function() {
				this.moveSelectedRecords(database);
			}, this),
			cancelHandler: $.proxy(function() {
				this.search.updateSelectList();
			}, this)
		});
	},
	moveSelectedRecords: function(database) {
		this.search.updateSelectList();

		var list = [];
		
		for (var i = 0; i < this.search.selectedList.length; i++) {
			list.push(this.search.selectedList[i].id);
		}
		
		if (list.length === 0) {
			return;
		}
		
		$.ajax({
			url: window.location.pathname,
			type: 'POST',
			dataType: 'json',
			data: {
				controller: 'json',
				module: this.type,
				action: 'move_records',
				id_list: list.join(','),
				database: database
			},
			loadingTimedOverlay: true,
			context: this
		}).done(function(response) {
			if (response.success) {
				Core.msg(response);
				this.search.selectedList = [];
				this.search.updateSelectList();
				Core.trigger(this.prefix + 'record-moved');
				this.search.paginate(this.search.lastPagingParameters, null, true);
		} else {
				Core.msg(response);
				this.search.updateSelectList();
			}
		});
	},
	showUploadProgress: function() {
		Core.fadeInOverlay('fast');
		$('#upload_popup').fadeIn('fast').center().progressbar();
	},
	hideUploadProgress: function() {
		Core.hideOverlay();
		$('#upload_popup').hide().stopContinuousProgress();	
	},
	_continuousUploadProgressTimeout: null,
	continuousUploadProgress: function() {
		$('#upload_popup').continuousProgress();
	},
	stopContinuousUploadProgress: function() {
		clearTimeout(this._continuousUploadProgressTimeout);	
		$('#upload_popup').stopContinuousProgress();
	},
	updateUploadProgress: function(current, total, percentComplete) {
		this.stopContinuousUploadProgress();

		$('#upload_popup .progress').progressbar({
			current: current,
			total: total
		});
	},
	removeAttachment: function(button, name, uri) {
		Core.popup({
			title: _(this.type + '.confirm_remove_attachment'),
			description: _(this.type + '.confirm_remove_attachment_description'),
			okHandler: $.proxy(function() {
				$.ajax({
					url: window.location.pathname,
					type: 'POST',
					dataType: 'json',
					data: {
						controller: 'json',
						module: this.type,
						action: 'remove_attachment',
						id: this.search.selectedRecord.id,
						description: name,
						uri: uri
					},
					loadingTimedOverlay: true,
					context: this
				}).done(function(response) {
					Core.msg(response);
					
					if (response.success) {
						this.search.reloadResult();
					}
				});
			}, this),
			cancelHandler:  $.proxy(function() {
			}, this)
		});
	},
	upload: function(button) {
		Core.clearFormErrors();
		
		var description = prompt(_('cataloging.bibliographic.attachment.alias'));
		
		if (!description) {
			return;
		}
		
		var me = this;
		
		$('#page_submit').ajaxSubmit({
			beforeSerialize: function($form, options) { 
				$('#controller').val('json');
				$('#module').val('digitalmedia');
				$('#action').val('upload');
			},
			beforeSubmit: function() {
				me.showUploadProgress();
				me._continuousUploadProgressTimeout = setTimeout(me.continuousUploadProgress, 500);
			},
			dataType: 'json',
			forceSync: true,
			complete: function() { 
				$('#controller').val('jsp');
				me.hideUploadProgress();
			},
			success: function(response) {
				
				if (response.success && response.id) {
					$.ajax({
						url: window.location.pathname,
						type: 'POST',
						dataType: 'json',
						data: {
							controller: 'json',
							module: me.type,
							action: 'add_attachment',
							id: me.search.selectedRecord.id,
							description: description,
							uri: 'DigitalMediaController/?id=' + response.id
						},
						loadingTimedOverlay: true,
						context: me
					}).done(function(response) {
						Core.msg(response);
						
						if (response.success) {
							me.search.reloadResult();
						}
					});
				} else {
					Core.msg(response);
				}
			},
			error: function() {
				Core.msg({
					message_level: 'warning',
					message: _('cataloging.import.error.file_upload_error')
				});
			},
			uploadProgress: function(event, current, total, percentComplete) {
				me.updateUploadProgress(current, total, percentComplete);
			}
		}); 
	}
};

var CatalogingInput = new Input($.extend(CatalogingInputClass, CatalogingFormClass));