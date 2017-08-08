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
var AccessControl = {
	getOverlayClass: function(record) {
		switch (record.status) {
			case 'blocked':
			case 'in_use_and_blocked':
			case 'cancelled':
				return 'overlay_warning';
				break;
				
			default:
				return '';
		}
	},
	getOverlayText: function(record) {
		switch (record.status) {
			case 'blocked':
			case 'in_use_and_blocked':
			case 'cancelled':
				return _('cartao bloqueado');
				break;
				
			default:
				return '';
		}
	},
	bindCard: function(cardId, askForConfirmation) {
		if (!CirculationSearch.selectedRecord) {
			Core.msg({
				message_level: 'normal',
				message: _('TODO')
			});
			return;
		}

		var bind = function() {
			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				data: {
					controller: 'json',
					module: 'circulation.accesscontrol',
					action: 'bind',
					card_id: cardId,
					user_id: CirculationSearch.selectedRecord.id
				},
				loadingTimedOverlay: true
			}).done($.proxy(function(response) {
				if (response.success) {
					var oldRecord = AccessCardsSearch.getSearchResult(cardId);
					if (oldRecord) {
						var index = oldRecord.index;
						response.data.index = index;
					}

					Core.trigger(AccessCardsSearch.prefix + 'record-changed', response.data);
				}

				Core.msg(response);
			}, this));
		};
		
		if (askForConfirmation) {			
			Core.popup({
				title: 'TODO',
				description: 'TODO',
				confirm: 'TODO!',
				okText: _('common.yes'),
				cancelText: _('common.no'),
				okHandler: $.proxy(function() {
					$.proxy(bind, this)();
					$(':focus').blur();							
				}, this),
				cancelHandler: $.proxy($.noop, this)
			});
		} else {
			$.proxy(bind, this)();
		}
	},
	unbindCard: function(cardId, userId, askForConfirmation) {
		var bind = function() {
			$.ajax({
				url: window.location.pathname,
				type: 'POST',
				dataType: 'json',
				data: {
					controller: 'json',
					module: 'circulation.accesscontrol',
					action: 'unbind',
					card_id: cardId,
					user_id: userId
				},
				loadingTimedOverlay: true
			}).done($.proxy(function(response) {
				if (response.success) {
					var oldRecord = AccessCardsSearch.getSearchResult(cardId);
					if (oldRecord) {
						var index = oldRecord.index;
						response.data.index = index;
					}

					Core.trigger(AccessCardsSearch.prefix + 'record-changed', response.data);
				}

				Core.msg(response);
			}, this));
		};
		
		if (askForConfirmation) {			
			Core.popup({
				title: 'TODO',
				description: 'TODO',
				confirm: 'TODO!',
				okText: _('common.yes'),
				cancelText: _('common.no'),
				okHandler: $.proxy(function() {
					$.proxy(bind, this)();
					$(':focus').blur();							
				}, this),
				cancelHandler: $.proxy($.noop, this)
			});
		} else {
			$.proxy(bind, this)();
		}
	}
};