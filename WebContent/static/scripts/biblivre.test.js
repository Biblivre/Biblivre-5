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
var Test = {};

$(document).ready(function() {
	Test.change($('#test_action')[0]);
});

Test.submit = function() {
	var module = $('#test_module').val();
	var action = $('#test_action').val();
	var controller = $('#test_controller').val();

	var json = $('.json[rel="' + action + '"]').val();
	var request = JSON.parse(json);
	
	request.module = request.module || module;
	request.action = request.action || action;
	request.controller = request.controller || controller;
	
	$.ajax({
		url: window.location.pathname,
		type: 'POST',
		dataType: 'text',
		data: request,
		success: function(response) {
			$('#test_response').html(response);
		},
		failure: function() {
			console.log('falhou');
		}
	});
};

Test.change = function(combo) {
	$('.json').hide();
	$('.json[rel="' + combo.value + '"]').show();
};
