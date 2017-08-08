/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.circulation.accesscontrol;

import java.util.List;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.administration.accesscards.AccessCardStatus;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.ValidationException;

public class AccessControlBO extends AbstractBO {
	private AccessControlDAO dao;

	public static AccessControlBO getInstance(String schema) {
		AccessControlBO bo = AbstractBO.getInstance(AccessControlBO.class, schema);

		if (bo.dao == null) {
			bo.dao = AccessControlDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public AccessControlDTO populateDetails(AccessControlDTO dto) {
		if (dto == null) {
			return null;
		}
		
		if (dto.getAccessCardId() != null) {
			AccessCardBO cardBo = AccessCardBO.getInstance(this.getSchema());
			dto.setAccessCard(cardBo.get(dto.getAccessCardId()));
		}

		if (dto.getUserId() != null) {
			UserBO userBo = UserBO.getInstance(this.getSchema());
			dto.setUser(userBo.get(dto.getUserId()));
		}
		
		return dto;
	}
	
    public boolean lendCard(AccessControlDTO dto) {
    	
    	UserBO userBO = UserBO.getInstance(this.getSchema());
		UserDTO udto = null;
		try {
			udto = userBO.get(dto.getUserId());
		} catch (Exception e) {
			this.logger.error(e);
		}
		if (udto == null) {
			throw new ValidationException("circulation.error.user_not_found");
		}
		
		AccessCardBO cardBO = AccessCardBO.getInstance(this.getSchema());
		AccessCardDTO cardDto = cardBO.get(dto.getAccessCardId());
		if (cardDto == null) {
			throw new ValidationException("circulation.access_control.card_not_found");
		} else if (!cardDto.getStatus().equals(AccessCardStatus.AVAILABLE)) {
			throw new ValidationException("circulation.access_control.card_unavailable");
		}
		
		AccessControlDTO existingAccess = this.getByCardId(dto.getAccessCardId());
		if (existingAccess != null) {
			throw new ValidationException("circulation.access_control.card_in_use");
		}
		existingAccess = this.getByUserId(dto.getUserId());
		if (existingAccess != null) {
			throw new ValidationException("circulation.access_control.user_has_card");
		}
    	
		try {
			cardDto.setStatus(AccessCardStatus.IN_USE);
			cardBO.update(cardDto);
			return this.dao.save(dto);
		} catch (Exception e) {
			this.logger.error(e);
		}
		
        return false;
    }

    public boolean returnCard(AccessControlDTO dto) {
    	
    	UserBO userBO = UserBO.getInstance(this.getSchema());
		UserDTO udto = null;
		try {
			udto = userBO.get(dto.getUserId());
		} catch (Exception e) {
			this.logger.error(e);
		}
		if (udto == null) {
			throw new ValidationException("circulation.error.user_not_found");
		}
		
		AccessCardBO cardBO = AccessCardBO.getInstance(this.getSchema());
		AccessControlDTO existingAccess = null;

		if (dto.getAccessCardId() != 0) {
			AccessCardDTO cardDto = cardBO.get(dto.getAccessCardId());
			if (cardDto == null) {
				throw new ValidationException("circulation.access_control.card_not_found");
			} else if (cardDto.getStatus().equals(AccessCardStatus.AVAILABLE)) {
				throw new ValidationException("circulation.access_control.card_available");
			}
			
			existingAccess = this.getByCardId(dto.getAccessCardId());
			if (existingAccess == null) {
				existingAccess = this.getByUserId(dto.getUserId());
				if (existingAccess == null) {
					throw new ValidationException("circulation.access_control.user_has_no_card");
				}
			}
		}
		
		try {
			if (existingAccess != null) {
				AccessCardDTO cardDto = cardBO.get(existingAccess.getAccessCardId());
				//If the cardId was sent in the parameters, it means that the user has returned it.
				//Else, it means that the user left the library without returning the card, so we have to block it.
				cardDto.setStatus(dto.getAccessCardId() != 0 ? AccessCardStatus.AVAILABLE : AccessCardStatus.IN_USE_AND_BLOCKED);
				cardBO.update(cardDto);
				return this.dao.update(existingAccess);
			}
		} catch (Exception e) {
			this.logger.error(e);
		}
		
        return false;
    }


    public boolean update(AccessControlDTO dto) {
        return this.dao.update(dto);
    }

    public AccessControlDTO getByCardId(Integer cardId) {
        return this.dao.getByCardId(cardId);
    }

    public AccessControlDTO getByUserId(Integer userId) {
        return this.dao.getByUserId(userId);
    }

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}
}
