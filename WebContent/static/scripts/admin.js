(function($) {	
	$.fn.recordTabConfigUpdateSeparatorDisplay = function() {
		return this.each(function() {
			var el = $(this);

			el.find('.datafield_format_separator').show();
			el.find('.datafield_format_separator:first').hide();
		});
	};

	var _recordTabConfigBuildFormatField = null;
	$.recordTabConfigBuildFormatField = function() {
		if (!_recordTabConfigBuildFormatField) {
			_recordTabConfigBuildFormatField = $([
	  			'<div class="datafield_format">',
	  				'<div class="datafield_format_separator">',
	  			   		'<span class="text_separator">asasasa</span> ',
	  			   		'<input type="text" name="datafield_format_separator"/> ',
	  			   	'</div>',
	  		   		'<span class="text_subfield">asasasa</span> ',
	  		   		'<input type="text" name="datafield_format_subfield" maxlength="1"/> ',
	  		   		'<span class="text_repeater">asasasa</span> ',
	  		   		'<input type="text" name="datafield_format_repeater"/> ',
	  		   	'</div>'
	  		].join('')).append(
	  			$('<div class="button_minus"></div>').click(function() {
	  				var formats = $(this).parents('.datafield_formats');
	  				$(this).parent('.datafield_format').remove();
	  				formats.recordTabConfigUpdateSeparatorDisplay();
	  			})
	  		);
		}

		return _recordTabConfigBuildFormatField.clone(true);
	};

	$.fn.recordTabConfigBuildFormatForm = function(format) {
		if (!format) {
			return this;
		}

		return this.each(function() {
			var pattern = /(.)\{(.*?)\}/g;
			var matches = null;

			var lastSeparator = '';

			while (matches = pattern.exec(format)) {
				var separator = '';
				var subfield = '';
				var repeater = '';
				
				switch (matches[1]) {
					case '_': 
						lastSeparator = matches[2];
						break;

					case '$':
						separator = lastSeparator;
						subfield = matches[2].charAt(0);
						repeater = matches[2].substring(1) || ', ';

						var div = $.recordTabConfigBuildFormatField();
						
						div.find('input[name=datafield_format_separator]').val(separator);
						div.find('input[name=datafield_format_subfield]').val(subfield);
						div.find('input[name=datafield_format_repeater]').val(repeater);
						div.appendTo(this);
						
						break;
				}
			}
		});	
	};
})(jQuery);

$(document).ready(function() {

	var saveButton = $('<button>Salvar</button>').click(function() {
		var button = $(this);
		var entry = button.parent('.datafield_entry');
		
		entry.find('button:not(.menu_button)').remove();
		entry.find('.datafield_edit').hide();
		entry.find('.menu_button').show();
	});

	var cancelButton = $('<button>Cancelar</button>').click(function() {
		var button = $(this);
		var entry = button.parent('.datafield_entry');
		
		entry.find('button:not(.menu_button)').remove();
		entry.find('.datafield_edit').hide();
		entry.find('.menu_button').show();
	});
	
	var editButton = $('<button><img src="static/images/ico_gear.png"/></button>').addClass('menu_button').click(function() {
		var button = $(this);
		var entry = button.parent('.datafield_entry');
		
		entry.find('.datafield_edit').show();
		entry.append(saveButton.clone(true));
		entry.append(cancelButton.clone(true));
		entry.append('<div class="clear"></div>');
		button.hide();
	});
	
	$('.datafield_entry').prepend(editButton.clone(true));

	$('.datafield_list').sortable();
	$('.datafield_formats')
		.recordTabConfigBuildFormatForm('${a, }_{ }${b}_{ - }${c}')
		.recordTabConfigUpdateSeparatorDisplay()
		.sortable({
			update: function(event, ui) {
				$(this).recordTabConfigUpdateSeparatorDisplay();
			}
	});
	
	$('.datafield_edit .button_plus').click(function() {
		var div = $.recordTabConfigBuildFormatField();

		$(this).siblings('.datafield_formats').append(div).recordTabConfigUpdateSeparatorDisplay();
		
		div.find('input:visible:first').focus();
	});
});