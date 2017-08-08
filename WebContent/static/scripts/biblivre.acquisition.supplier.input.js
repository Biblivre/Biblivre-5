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
var SupplierInput = new Input({
	initialize: function() {
		$('#biblivre_supplier_form_body').setTemplateElement('biblivre_supplier_form_body_template');
		$('#biblivre_supplier_form').setTemplateElement('biblivre_supplier_form_template');
		$('#biblivre_supplier_info_form').setTemplateElement('biblivre_supplier_info_form_template');
		
		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {			
			if (this.search.lastSearchResult && this.search.lastSearchResult[id]) {
				this.search.lastSearchResult[id].deleted = true;
				
				this.search.processResultTemplate();
			}

			this.search.closeResult();
		}, this);
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'form':
				this.setAsEditable();
				break;
		}
	},
	clearAll: function() {
		this.clearTab('form');
	},
	setAsReadOnly: function() {
		var root = $('#biblivre_supplier_form_body, #biblivre_supplier_form, #biblivre_supplier_info_form');

		root.find('input:text, textarea:not(.template)').each(function() {
			var input = $(this);

			var value = input.val();
			input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});
	},
	setAsEditable: function(tab) {
		var root = $('#biblivre_supplier_form_body, #biblivre_supplier_form, #biblivre_supplier_info_form');

		root.find('.readonly_text').remove();
		root.find('.readonly_hidden').removeClass('readonly_hidden');
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
		if (tab != 'form') {
			tab = 'form';
		} else {
			this.setAsEditable('form');
		}

		Core.changeTab(tab, this.search, { keepEditing: true, skipConvert: true });
	},
	getNewRecord: function() {
		return {
			id: ' '
		};
	},
	getSaveRecord: function(saveAsNew) {
		var params = {};
		$('#biblivre_supplier_form_body :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		$('#biblivre_supplier_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		$('#biblivre_supplier_info_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		
		return $.extend(params, {
			oldId: this.recordIdBeingEdited || 0,
			id: (saveAsNew) ? 0 : this.recordIdBeingEdited
		});
	},
	deleteRecordTitle: function() {
		return _(this.type + '.confirm_delete_record_title.forever');
	},
	deleteRecordQuestion: function() {
		return _(this.type + '.confirm_delete_record_question.forever');
	},
	deleteRecordConfirm: function() {
		return _(this.type + '.confirm_delete_record.forever');
	},
	getDeleteRecord: function(id) {
		return {
			id: id,
		};
	},
	getOverlayClass: function(record) {
		if (record.deleted) {
			return 'overlay_error';
		}
		
		return '';
	},
	getOverlayText: function(record) {
		if (record.deleted) {
			return _('common.deleted');
		}
		
		return '';
	}
});