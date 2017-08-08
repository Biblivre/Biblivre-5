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
var Input = function(extend) {
	$.extend(this, extend);
	
	$(document).ready($.proxy(function() {
		this.prefix = this.type;
		this.root = $(this.root);
		this.initialize();

		this.initializeSearchArea();
		this.initializeSelectedRecordArea();
	}, this));
};

Input.prototype.initializeSearchArea = function() {
	Core.subscribe(this.prefix + 'edit-record-start', function(e, id) {
		this.search.displayPagingButtons(id);
		this.root.find('.page_navigation .button').disable();
	}, this);

	Core.subscribe(this.prefix + 'edit-record-end', function(e, id) {
		this.search.displayPagingButtons(id);
		this.root.find('.page_navigation .button').enable();
	}, this);
};

Input.prototype.initializeSelectedRecordArea = function() {
	Core.subscribe(this.prefix + 'edit-record-start', function(e, id) {
		var newRecord = id == ' ';

		this.root.find('.selected_highlight, .selected_record').addClass(newRecord ? 'new' : 'edit');
		this.root.find('.selected_highlight .clone').fixButtonsHeight();
	}, this);

	Core.subscribe(this.prefix + 'edit-record-end', function(e, id) {
		this.root.find('.selected_highlight, .selected_record').removeClass('new edit');
		this.root.find('.selected_highlight .clone').fixButtonsHeight();
	}, this);

	Core.subscribe(this.prefix + 'record-deleted', function(e, id) {
		this.search.closeResult();
	}, this);
	
	Core.subscribe(this.prefix + 'record-changed', function(e, record) {
		this.root.find('.selected_highlight .clone[rel=' + record.id + ']').processTemplate(record).fixButtonsHeight();
	}, this);
	
	if ($.isFunction(this.onInitializeSelectedRecordArea)) {
		this.onInitializeSelectedRecordArea();
	}
};

Input.prototype.cancelEdit = function() {
	Core.popup({
		title: _(this.prefix + '.confirm_cancel_editing_title'),
		description: _(this.prefix + '.confirm_cancel_editing.1'),
		confirm: _(this.prefix + '.confirm_cancel_editing.2'),
		okText: _('common.yes'),
		cancelText: _('common.no'),
		okHandler: $.proxy(this.closeEdit, this),
		cancelHandler: $.proxy($.noop, this)
	});
};

Input.prototype.closeEdit = function() {
	if (!this.editing) {
		return;
	}
	
	if (Core.trigger(this.prefix + 'edit-record-end', this.recordIdBeingEdited) === false) {
		return;
	}

	var recordId = this.recordIdBeingEdited;

	this.editing = false;
	this.recordIdBeingEdited = null;

	if (recordId === ' ') {
		this.search.closeResult();
	} else {
		this.search.clearAll();
		this.search.tabHandler(this.search.selectedTab);
	}
};

Input.prototype.newRecord = function() {
	this.search.toggleSearch(true, true);
	this.search.clearAll();
	
	var record = this.getNewRecord();

	Core.trigger(this.prefix + 'record-new', record);

	var selectedHighlight = this.root.find('.selected_highlight').empty();
	this.search
		.createRecordHighlight(null, record)
		.css({ position: 'relative' })
		.appendTo(selectedHighlight);

	this.root.find('.selected_highlight, .selected_record').fadeIn(300);

	Core.trigger(this.prefix + 'record-selected');

	this.search.selectedRecord = record;

	this.editRecord(record.id);
};

Input.prototype.saveRecord = function(saveAsNew) {
	if (!this.editing) {
		return;
	}

	Core.clearFormErrors();

	var data = $.extend({
		controller: 'json',
		module: this.type,
		action: 'save'
	}, this.getSaveRecord(saveAsNew));

	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'json',
		data: data,
		loadingTimedOverlay: true,
		context: this
	}).done(function(response) {
		if (response.success && response.data) {
			if (data.oldId != response.data.id) {
				this.root.find('.selected_highlight .clone[rel="' + data.oldId + '"]').attr('rel', response.data.id);

				Core.trigger(this.prefix + 'record-created', response.data);

				this.search.selectedRecord = {
					id: response.data.id
				};

				this.recordIdBeingEdited = response.data.id;
			}

			var callback = $.proxy(function(response) {
				this.search.clearAll();

				var index = this.search.selectedRecord.index;
				this.search.selectedRecord = response.data;
				this.search.selectedRecord.index = index;

				Core.trigger(this.prefix + 'record-changed', response.data);

				if (this.prefix != this.search.prefix) {
					Core.trigger(this.search.prefix + 'record-changed', response.data);
				}

				this.closeEdit();
			}, this);

			if (response.full_data) {
				callback(response);
			} else if (!response.data.promise) {
				this.search.loadRecord(this.search.selectedRecord, callback);
			} else {
				var input = this;

				response.data.promise.done(function() {
					input.search.loadRecord(input.search.selectedRecord, callback);
				});
			}
		} else {
			Core.formErrors(response.errors);
		}

		Core.msg(response);
	});
};


Input.prototype.deleteRecordTitle = function() {
	return _(this.prefix + '.confirm_delete_record_title');
};

Input.prototype.deleteRecordQuestion = function() {
	return _(this.prefix + '.confirm_delete_record_question');
};

Input.prototype.deleteRecordConfirm = function() {
	return _(this.prefix + '.confirm_delete_record_confirm');
};

Input.prototype.deleteRecord = function(id) {
	var data = $.extend({
		controller: 'json',
		module: this.type,
		action: 'delete'
	}, this.getDeleteRecord(id));

	Core.popup({
		title: this.deleteRecordTitle(),
		description: this.deleteRecordQuestion(),
		confirm: this.deleteRecordConfirm(),
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
					Core.trigger(this.prefix + 'record-deleted', id);
				}

				Core.msg(response);
			}, this));
		}, this),
		cancelHandler: $.proxy($.noop, this)
	});
};