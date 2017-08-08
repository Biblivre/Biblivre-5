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
var AccessCardsInput = new Input({
	initialize: function() {
		var me = this;
		
		$('#biblivre_accesscards_form_body').setTemplateElement('biblivre_accesscards_form_body_template');
		$('#biblivre_accesscards_single_form_body').setTemplateElement('biblivre_accesscards_single_form_body_template');
		$('#biblivre_accesscards_multiple_form_body').setTemplateElement('biblivre_accesscards_multiple_form_body_template');
		
		$('#biblivre_accesscards_single_form_body').on('keyup', 'input[name=code]', function() {
			var enabled = $(this).val();
			$('#biblivre_accesscards_multiple_form_body input:text:not([name=preview])').each(function() {
				if (enabled) {
					$(this).attr('disabled', 'disabled');
				} else {
					$(this).val('').removeAttr('disabled');
				};
			});
		});
		
		$('#biblivre_accesscards_multiple_form_body').on('keyup', 'input', function() {
			var preview = me.updatePreview();
			var code = $('#biblivre_accesscards_single_form_body input[name=code]');
			
			if (preview) {
				code.attr('disabled', 'disabled');
			} else {
				code.val('').removeAttr('disabled');
			}
		});
		
		
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
		var root = $('#biblivre_accesscards_form_body');

		root.find('input:text, textarea:not(.template)').each(function() {
			var input = $(this);

			var value = input.val();
			input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});
	},
	setAsEditable: function(tab) {
		var root = $('#biblivre_accesscards_form_body');

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
	updatePreview: function() {
		var first = $('#prefix').val() + $('#start').val() + $('#suffix').val();
	    $('#preview').val(first);
	    return first;
	},
	getNewRecord: function() {
		return {
			id: ' '
		};
	},
	getSaveRecord: function(saveAsNew) {
		var body = $('#biblivre_accesscards_form_body');
		
		var params = {};
		
		$('#biblivre_accesscards_single_form_body, #biblivre_accesscards_multiple_form_body').find(':input:enabled').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});
		
		return $.extend(params, {
			oldId: this.recordIdBeingEdited || 0,
			id: (saveAsNew) ? 0 : this.recordIdBeingEdited,
			status: body.find(':input[name=status]').val()
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
	changeStatusTitle: function(status, old_status) {
		if (status == 'blocked' || status == 'in_use_and_blocked') {
			return _(this.type + '.change_status.title.block');
		} else if (status == 'in_use') {
			return _(this.type + '.change_status.title.unblock');
		} else if (status == 'available') {
			if (old_status && old_status == 'cancelled') {
				return _(this.type + '.change_status.title.uncancel');
			} else {
				return _(this.type + '.change_status.title.unblock');
			}
		} else if (status == 'cancelled') {
			return _(this.type + '.change_status.title.cancel');
		}
	},
	changeStatusQuestion: function(status, old_status) {
		if (status == 'blocked' || status == 'in_use_and_blocked') {
			return _(this.type + '.change_status.question.block');
		} else if (status == 'in_use') {
			return _(this.type + '.change_status.question.unblock');
		} else if (status == 'available') {
			if (old_status && old_status == 'cancelled') {
				return _(this.type + '.change_status.question.uncancel');
			} else {
				return _(this.type + '.change_status.question.unblock');
			}
		} else if (status == 'cancelled') {
			return _(this.type + '.change_status.question.cancel');
		}
	},
	changeStatusConfirm: function(status, old_status) {
		if (status == 'blocked' || status == 'in_use_and_blocked') {
			return _(this.type + '.change_status.block');
		} else if (status == 'in_use') {
			return _(this.type + '.change_status.unblock');
		} else if (status == 'available') {
			if (old_status && old_status == 'cancelled') {
				return _(this.type + '.change_status.uncancel');
			} else {
				return _(this.type + '.change_status.unblock');
			}
		} else if (status == 'cancelled') {
			return _(this.type + '.change_status.cancel');
		}
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
	},
	changeStatus: function(id, status, old_status) {
		var data = {
			controller: 'json',
			module: this.type,
			action: 'change_status',
			id: id,
			status: status
		};

		Core.popup({
			title: this.changeStatusTitle(status, old_status),
			description: this.changeStatusQuestion(status, old_status),
			confirm: this.changeStatusConfirm(status, old_status),
			okText: _('common.yes'),
			cancelText: _('common.no'),
			okHandler: $.proxy(function() {
				$.ajax({
					url: window.location.pathname,
					type: 'POST',
					dataType: 'json',
					data: data,
					loadingTimedOverlay: true
				}).done($.proxy(function(response) {
					if (response.success) {
						var oldRecord = this.search.getSearchResult(id);
						if (oldRecord) {
							var index = oldRecord.index;
							response.data.index = index;
						}
						
						Core.trigger(this.prefix + 'record-changed', response.data);
					}

					Core.msg(response);
				}, this));
			}, this),
			cancelHandler: $.proxy($.noop, this)
		});
	}
	
});