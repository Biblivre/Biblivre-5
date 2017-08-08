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
var CatalogingLabels = {
	labels: [{
		model: 'A4365',
		width: 99,
		height: 67.7,
		columns: 2,
		rows: 4,
		paper_size: 'A4'
	}, {
		model: 'A4350',
		width: 99,
		height: 55.8,
		columns: 2,
		rows: 5,
		paper_size: 'A4'
	}, {
		model: 'A4363',
		width: 99,
		height: 38.1,
		columns: 2,
		rows: 7,
		paper_size: 'A4'
	}, {
		model: 'A4362',
		width: 99,
		height: 33.9,
		columns: 2,
		rows: 8,
		paper_size: 'A4'
	}, {
		model: 'A4361',
		width: 63.5,
		height: 46.5,
		columns: 3,
		rows: 6,
		paper_size: 'A4'
	}, {
		model: 'A4360',
		width: 63.5,
		height: 38.1,
		columns: 3,
		rows: 7,
		paper_size: 'A4'
	}, {
		model: 'A4354',
		width: 99,
		height: 25.4,
		columns: 2,
		rows: 11,
		paper_size: 'A4'
	}, {
		model: 'A4355',
		width: 63.5,
		height: 31,
		columns: 3,
		rows: 9,
		paper_size: 'A4',
		selected: true
	}, {
		model: 'A4356',
		width: 63.5,
		height: 25.4,
		columns: 3,
		rows: 11,
		paper_size: 'A4'
	}],
	printLabels: function() {
		this.configureLabelFormat();
		this.showPopup();
	},
	showPopup: function() {
		Core.fadeInOverlay('fast');
		$('#label_select_popup').fadeIn('fast').center();
	},
	hidePopup: function() {
		Core.hideOverlay();
		$('#label_select_popup').hide();
	},
	configureLabelFormat: function() {
		var select = $('#label_format_select');
		select.empty();

		for (var i = 0; i < CatalogingLabels.labels.length; i++) {
			var option = $('<option></option>');
			var label = CatalogingLabels.labels[i];

			label.count = label.columns * label.rows;

			option
				.val(i)
				.text(_('cataloging.labels.paper_description', label))
				.appendTo(select);

			if (label.selected) {
				option.attr('selected', 'selected');
			}
		}

		select.trigger('change');
	},
	drawLabelTable: function(label) {
		var table = $('#label_table tbody');

		table.empty();

		var cell = 1;
		
		for (var i = 0; i < label.rows; i++) {
			var tr = $('<tr></tr>');
			
			for (var j = 0; j < label.columns; j++) {
				var td = $('<td></td>');

				td.text(cell).data('cell', cell).click(function() {
					CatalogingLabels.printPdf($(this).data('cell') - 1, label);
				}).appendTo(tr);

				cell++;
			}
			
			table.append(tr);
		}

		table.find('td').width((100 / label.columns) + '%');
	},
	printPdf: function(offset, label) {
		this.hidePopup();

		var list = [];

		for (var i = 0; i < CatalogingSearch.selectedList.length; i++) {
			list.push(CatalogingSearch.selectedList[i].id);
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
				module: 'cataloging.labels',
				action: 'create_pdf',
				id_list: list.join(','),
				offset: offset,
				width: label.width,
				height: label.height,
				columns: label.columns,
				rows: label.rows
			},
			loadingTimedOverlay: true,
			context: this
		}).done(function(response) {
			if (response.success) {
				window.open(window.location.pathname + '?controller=download&module=cataloging.labels&action=download_pdf&id=' + response.uuid);
				CatalogingSearch.selectedList = [];
				CatalogingSearch.updateSelectList();
			} else {
				Core.msg(response);
			}
		});
	}
};


$(document).ready(function() {	  
	$('#label_format_select').change(function() {
		var label = CatalogingLabels.labels[$(this).val()];

		CatalogingLabels.drawLabelTable(label);
	});
});