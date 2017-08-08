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
var Administration = Administration || {};

var PermissionsSearchClass = $.extend({}, CirculationSearchClass, {
	tabHandler: function(tab, params) {
		params = params || {};
		data = params.data || this.selectedRecord;

		this.selectedTab = tab;

		switch (tab) {
			case 'login':
				this.loadPermissionsForm(data, params);
				break;
		}
	},
	clearTab: function(tab) {
		switch (tab) {
			case 'fines':
				$('#biblivre_circulation_permissions_form').empty().data('loaded', false);
				this.root.find('input:checkbox').removeAttr('checked');
				break;
		}
	},	
	clearAll: function() {
		this.clearTab('fines');
	},	
	loadPermissionsForm: function(record, params) {
		var div = $('#biblivre_circulation_permissions_form');

		if (div.data('loaded')) {
			if (params.force) {
				this.clearTab('form');
			} else {
				return;
			}
		}

		
		$('#biblivre_circulation_permissions_form').processTemplate(record);

		if (record.permissions) {
			for (var i = 0; i < record.permissions.length; i++) {
				var permission = record.permissions[i];
				this.root.find('input:checkbox[value="' + permission + '"]').attr('checked', 'checked');
			}
		}
		
		//MARK CHECKED_ALL FOR EACH GROUP
		var employee =  $(':input[name="employee"]').val();
		
		this.root.find('#permissions .permissions_area[data="' + employee + '"] fieldset').each(function() {
			var fieldset = $(this);
			var inputs = fieldset.find('input:checkbox:not(.legend):not(:checked)');
			
			if (inputs.size() === 0) {
				fieldset.find('input:checkbox.legend').attr('checked', 'checked');
			} else {
				fieldset.find('input:checkbox.legend').removeAttr('checked');
			}
		});
		
		Core.toggleAreas('permissions_area', record.login ? record.login.employee : '');
		
	}
});

var PermissionsInput = new Input({
	initialize: function() {
		$('#biblivre_circulation_permissions_form').setTemplateElement('biblivre_circulation_permissions_form_template');
		
		Core.subscribe(this.search.prefix + 'open-record', function(e, record) {
			this.editRecord(record.id);
		}, this);

		Core.subscribe(this.prefix + 'record-deleted', function(e, id) {
			if (this.search.lastSearchResult && this.search.lastSearchResult[id]) {
				delete this.search.lastSearchResult[id].login;
				delete this.search.lastSearchResult[id].permissions;
			}

			this.search.processResultTemplate();
		}, this);		

	},
	setAsReadOnly: function() {
	},
	setAsEditable: function(tab) {
	},
	editing: false,
	recordIdBeingEdited: null,
	editRecord: function(id) {
		this.editing = true;
		this.recordIdBeingEdited = id;
	},
	getSaveRecord: function(saveAsNew) {
		var permissions = [];
		var employee =  $(':input[name="employee"]').val();
		
		$('input:checkbox:checked[name="' + ((employee == "true") ? 'employee_permissions' : 'reader_permissions') + '"]').each(function() {
			permissions.push($(this).val());
		});
	
		var oldId = this.recordIdBeingEdited;
		var id = this.recordIdBeingEdited;
		
		return {
			oldId: oldId,
			id: id,			
			user_id: $(':input[name="user_id"]').val(),
			new_login: $(':input[name="new_login"]').val(),
			new_password: $(':input[name="new_password"]').val(),
			repeat_password: $(':input[name="repeat_password"]').val(),
			employee: $(':input[name="employee"]').val(),
			permissions: permissions
		};
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
			user_id: id,
		};
	},
	checkAllPermissions: function(cb) {
		var employee =  $(':input[name="employee"]').val();
	    $(cb).closest('fieldset')
	    	.find('input:checkbox[name="' + ((employee == "true") ? 'employee_permissions' : 'reader_permissions') + '"]')
	    	.attr('checked', cb.checked);
	}
});
