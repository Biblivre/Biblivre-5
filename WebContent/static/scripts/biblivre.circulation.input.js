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
var CirculationInput = new Input({
	initialize: function() {
		$('#biblivre_circulation_form').setTemplateElement('biblivre_circulation_form_template');
		$('#biblivre_circulation_form_body').setTemplateElement('biblivre_circulation_form_body_template');
		$('#biblivre_circulation_lendings').setTemplateElement('biblivre_circulation_lendings_template');
		$('#biblivre_circulation_reservations').setTemplateElement('biblivre_circulation_reservations_template');
		$('#biblivre_circulation_fines').setTemplateElement('biblivre_circulation_fines_template');
		
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
				
			case 'lendings':
				break;
				
			case 'reservations':
				break;	
				
			case 'fines':
				break;
		}
	},
	clearAll: function() {
		this.clearTab('form');
		this.clearTab('lendings');
		this.clearTab('reservations');
		this.clearTab('fines');
	},
	setAsReadOnly: function() {
		var root = $('#biblivre_circulation_form_body, #biblivre_circulation_form');

		root.find('input:text, textarea:not(.template)').each(function() {
			var input = $(this);

			var value = input.val();
			input.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});

		root.find('input:checkbox').each(function() {
			$(this).addClass('readonly_checkbox').attr('disabled', 'disabled');
		});

		
		root.find('select').each(function() {
			var combo = $(this);

			var value = combo.find('option[value="' + combo.val() + '"]').text();
			combo.after($('<div class="readonly_text"></div>').text(value)).addClass('readonly_hidden');
		});

		root.find('.photo_field').hide();

		var currentPhoto = this.root.find('img.user_photo');
		if (currentPhoto.data('original_src')) {
			currentPhoto.attr('src', currentPhoto.data('original_src'));
		}
	},
	setAsEditable: function(tab) {
		var root = $('#biblivre_circulation_form_body, #biblivre_circulation_form');

		root.find('.readonly_text').remove();
		root.find('.readonly_hidden').removeClass('readonly_hidden');
		root.find('.readonly_checkbox').removeClass('readonly_checkbox').removeAttr('disabled');
	},
	enablePhotoUpload: function(root) {
		var photo = root.find('.photo_field').show();
		var currentPhoto = this.root.find('img.user_photo');
		currentPhoto.data('original_src', currentPhoto.attr('src'));
		
		this.setupPhotoUpload(photo);
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

		Core.changeTab(tab, this.search, { force: true, keepEditing: true, skipConvert: true });
		this.enablePhotoUpload($('#biblivre_circulation_form_body, #biblivre_circulation_form'));
		this.setupDatePicker();
	},
	getNewRecord: function() {
		return {
			id: ' ',
			name: ' ',
			status: 'active'
		};
	},
	getSaveRecord: function(saveAsNew) {
		var body = $('#biblivre_circulation_form_body');

		var params = {};
		$('#biblivre_circulation_form :input').each(function() {
			var input = $(this);
			params[input.attr('name')] = input.val();
		});

		var img = this.root.find('img.user_photo');
		if (img.data('changed')) {
			params['photo_data'] = img.attr('src').split(',')[1];
		}
		
		return $.extend(params, {
			oldId: this.recordIdBeingEdited || 0,
			id: (saveAsNew) ? 0 : this.recordIdBeingEdited,
			name: body.find(':input[name=name]').val(),
			type: body.find(':input[name=type]').val(),
			status: body.find(':input[name=status]').val()
		});
	},	
	deleteRecordTitle: function() {
		return _(this.search.selectedRecord.status == 'inactive' ? this.type + '.confirm_delete_record_title.forever' : this.type + '.confirm_delete_record_title.inactive');
	},
	deleteRecordQuestion: function() {
		return _(this.search.selectedRecord.status == 'inactive' ? this.type + '.confirm_delete_record_question.forever' : this.type + '.confirm_delete_record_question.inactive');
	},
	deleteRecordConfirm: function() {
		return _(this.search.selectedRecord.status == 'inactive' ? this.type + '.confirm_delete_record.forever' : this.type + '.confirm_delete_record.inactive');
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
			return _('circulation.user.user_deleted');
		}
		
		return '';
	},
	photoChanged: false,	
	closePhotoUploadPopup: function() {
		$('#photo_area img').imgAreaSelect({ remove: true });
		$('#photo_upload_popup').hide();
	},
	applyPhotoSelection: function() {
		var result = $('#photo_area canvas').get(0).toDataURL();

		this.root.find('img.user_photo').attr('src', result).data('changed', true);

		this.closePhotoUploadPopup();
	},
	setupPhotoUpload: function(div) {
		var $popup = $('#photo_upload_popup');
		var $photo_area = $('#photo_area');
		var $input = div.find('input[type="file"]');

		var $photo = null;
		var $canvas = null;

		var settings = {
			width: 300,
			height: 400
		};

		var showLoading = function() {
			$popup.appendTo('body').show().center().find('.progress').show().continuousProgress();
		};

		var hideLoading = function() {
			var viewPortHeight = $(window).height() - $popup.outerHeight() - 50;
			var viewPortWidth = $popup.width() - 20;
			
			$photo.appendTo($photo_area);

			if ($photo.height() > viewPortHeight) {
				$photo.css({
					height: viewPortHeight
				});
			}

			if ($photo.width() > viewPortWidth) {
				$photo.css({
					height: 'auto',
					width: viewPortWidth
				});
			}
 			
			$popup.center().find('.progress').stopContinuousProgress().hide();
		};

		var fileAllowed = function(name) {
			return name.match(/\.(jpg|png|gif|jpeg)$/mi);
		};

		var setOriginalSize = function() {
			var tempImage = new Image();
			tempImage.onload = function() {
				$photo.attr({
					'data-original-width': tempImage.width,
					'data-original-height': tempImage.height
				});
			};
			tempImage.src = $photo.attr('src');
		};

		var drawImage = function(img, x, y, width, height) {
			var oWidth = img.attr('data-original-width');
			var oHeight = img.attr('data-original-height');
			var r;
			
			if (oWidth > oHeight) {
				r = oHeight / img.height();
			} else {
				r = oWidth / img.width();
			}
			
			var sourceX = Math.round(x * r);
			var sourceY = Math.round(y * r);
			var sourceWidth = Math.round(width * r);
			var sourceHeight = Math.round(height * r);
			var destX = 0;
			var destY = 0;
			var destWidth = settings.width;
			var destHeight = settings.height;
			var context = $canvas.get(0).getContext('2d');

			context.drawImage(img.get(0), sourceX, sourceY, sourceWidth, sourceHeight, destX, destY, destWidth, destHeight);
		};

		var setAreaSelect = function() {
			var image = $photo;
			var x2;
			var y2;
			
			if (image.width() / settings.width >= image.height() / settings.height) {
				y2 = image.height();
				x2 = Math.round(settings.width * (image.height() / settings.height));
			} else {
				x2 = image.width();
				y2 = Math.round(settings.height * (image.width() / settings.width));
			}

			var oX = (image.width() - x2) / 2;
			var oY = (image.height() - y2) / 2;
			
			var rX1 = Math.round(oX + (x2 * 0.1));
			var rX2 = Math.round(oX + (x2 * 0.9));
			var rY1 = Math.round(oY + (y2 * 0.1));
			var rY2 = Math.round(oY + (y2 * 0.9));

			setTimeout(function() {
				drawImage(image, rX1, rY1, rX2 - rX1 - 1, rY2 - rY1 - 1);				
			}, 1);

			image.imgAreaSelect({
				aspectRatio: settings.width + ":" + settings.height,
				handles: true,
				x1: rX1,
				y1: rY1,
				x2: rX2,
				y2: rY2,
				onSelectEnd: function(img, selection) {
					return drawImage(image, selection.x1, selection.y1, selection.width - 1, selection.height - 1);
				}
			});
		};

		var readFile = function(file) {
			var reader = new FileReader();

			$photo_area.empty();
			showLoading();

			reader.onload = function(e) {
				$photo = $('<img />');
				$canvas = $('<canvas />').addClass('hidden').appendTo($photo_area).attr(settings);

				$photo
					.attr('src', e.target.result)
					.load(function() {
						hideLoading();
						setOriginalSize();
						setAreaSelect();
					});
			};

			reader.readAsDataURL(file);
		};

		if (!$input.data('photo')) {
			$input.on('change', function(e) {
				if (e.target.files[0] !== null) {
					if (!fileAllowed(e.target.files[0].name)) {
						alert(_('circulation.user.error.invalid_photo_extension'));
						return;
					}

					readFile(e.target.files[0]);
					e.target.value = "";
				}
			}).data('photo', true);
		}
	},
	setupDatePicker: function() {
		var global = Globalize.culture().calendars.standard;

		$('input.datepicker').Zebra_DatePicker({
			days: global.days.names,
			days_abbr: global.days.namesAbbr,
			months: global.months.names,
			months_abbr: global.months.namesAbbr,
			format: Core.convertDateFormat(global.patterns.d),
			show_select_today: _('common.today'),
			lang_clear_date: _('common.clear'),
			direction: false,
			offset: [-19, -7],
			readonly_element: false
		});
	}
});