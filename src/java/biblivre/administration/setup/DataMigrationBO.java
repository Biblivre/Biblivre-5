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
package biblivre.administration.setup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biblivre.acquisition.order.OrderBO;
import biblivre.acquisition.quotation.QuotationBO;
import biblivre.acquisition.request.RequestBO;
import biblivre.acquisition.supplier.SupplierBO;
import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.usertype.UserTypeBO;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.enums.RecordType;
import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.lending.LendingBO;
import biblivre.circulation.lending.LendingDTO;
import biblivre.circulation.lending.LendingFineBO;
import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.reservation.ReservationBO;
import biblivre.circulation.user.UserBO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.DAOException;
import biblivre.core.file.MemoryFile;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.login.LoginBO;
import biblivre.z3950.Z3950BO;

public class DataMigrationBO extends AbstractBO {

	private DataMigrationDAO dao;
	private SetupDAO setupDao;
	private final Integer limit = 50;

	private boolean migratingDatabase = false;
	private DataMigrationPhase currentPhase;
	private Integer currentCount;

	private Map<Integer, Integer> lendingMap = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> lendingHistoryMap = new HashMap<Integer, Integer>();
	private String userSchema;
	
	@Override
	public String getSchema() {
		return this.userSchema;
	}
	
	public static DataMigrationBO getInstance(String schema, String datasource) {
		DataMigrationBO bo = AbstractBO.getInstance(DataMigrationBO.class, schema + ":" + datasource);
		
		bo.userSchema = schema;
		
		if (bo.dao == null) {
			bo.dao = DataMigrationDAO.getInstance(schema, datasource);
		}
		
		if (bo.setupDao == null) {
			bo.setupDao = SetupDAO.getInstance(schema);
		}
		
		return bo;
	}
	
	public boolean isBiblivre3Available() {
		return this.dao.testDatabaseConnection();
	}
	
	public boolean migrate(List<DataMigrationPhase> selectedPhases) {
		synchronized (this) {
			this.migratingDatabase = true;
			
			if (selectedPhases == null || selectedPhases.isEmpty()) {
				selectedPhases = Arrays.asList(DataMigrationPhase.values());
			}
			
			State.setSteps(selectedPhases.size());
			
			//DIGITAL MEDIA FILES CAN BE QUITE LARGE, SO WE WILL MIGRATE THEM
			//SEPARATELY AND SLOWER (THE LIMIT VALUE IS LOWER) THAN THE REST.
			boolean migrateDigitalMedia = false;
			if (selectedPhases.contains(DataMigrationPhase.DIGITAL_MEDIA)) {
				migrateDigitalMedia = true;
				selectedPhases.remove(DataMigrationPhase.DIGITAL_MEDIA);
			}
			
			for (DataMigrationPhase phase : selectedPhases) {
				this.currentPhase = phase;
				this.setCurrentCount(0);
				int page = 0;
				int offset = this.limit * page;

				boolean firstPass = true;
				boolean hasMore = true;
				State.setCurrentSecondaryStep(0);
				int retries = 0;
				while (hasMore) {
					try {
						List<? extends AbstractDTO> dtoList = this.listDTOs(phase, this.limit, offset);
						if (dtoList == null || dtoList.size() == 0) {
							hasMore = false;
						} else {
							if (firstPass) {
								if (!phase.equals(DataMigrationPhase.ACCESS_CONTROL_HISTORY)) {
									this.setupDao.deleteAll(phase);
								}
								firstPass = false;
							}
							this.setCurrentCount(this.getCurrentCount() + dtoList.size());
							offset = this.limit * ++page;
							this.saveDTOs(phase, dtoList);
							State.incrementCurrentSecondaryStep(dtoList.size());
						}
					} catch (DAOException e) {
						if (++retries > 3) {
							State.writeLog("DataMigration failed after 3 attempts at phase: " + phase.toString());
							throw e;
						}
					}
				}
				this.setupDao.fixSequence(this.currentPhase);
				State.incrementCurrentStep();
			}
			
			if (migrateDigitalMedia) {
				this.currentPhase = DataMigrationPhase.DIGITAL_MEDIA;
				this.setCurrentCount(0);
				int page = 0;
				int digitalMediaLimit = 1;
				int offset = digitalMediaLimit * page;
				
				boolean firstPass = true;
				boolean hasMore = true;
				while (hasMore) {
					List<MemoryFile> dtoList = this.listDigitalMedia(digitalMediaLimit, offset);
					if (dtoList == null || dtoList.size() == 0) {
						hasMore = false;
					} else {
						if (firstPass) {
							this.setupDao.deleteAll(DataMigrationPhase.DIGITAL_MEDIA);
							firstPass = false;
						}

						this.setCurrentCount(this.getCurrentCount() + dtoList.size());
						offset = digitalMediaLimit * ++page;
						this.saveDigitalMedia(dtoList);
					}
				}
				this.setupDao.fixSequence(this.currentPhase);
				State.incrementCurrentStep();
			}
			
			return true;
		}
	}

	private List<? extends AbstractDTO> listDTOs(DataMigrationPhase phase, int limit, int offset) {
		switch (phase) {

		case CATALOGING_BIBLIOGRAPHIC:
			return this.dao.listCatalogingRecords(RecordType.BIBLIO, limit, offset);
			
		case CATALOGING_HOLDINGS:
			return this.dao.listCatalogingHoldings(limit, offset);
			
		case CATALOGING_AUTHORITIES:
			return this.dao.listCatalogingRecords(RecordType.AUTHORITIES, limit, offset);
			
		case CATALOGING_VOCABULARY:
			return this.dao.listCatalogingRecords(RecordType.VOCABULARY, limit, offset);
			
		case ACCESS_CARDS:
			return this.dao.listAccessCards(limit, offset);
			
		case LOGINS:
			return this.dao.listLogins(limit, offset);
			
		case USER_TYPES:
			return this.dao.listUsersTypes(limit, offset);
			
		case USERS:
			return this.dao.listUsers(limit, offset);
			
		case ACQUISITION_SUPPLIER:
			return this.dao.listAquisitionSupplier(limit, offset);
			
		case ACQUISITION_REQUISITION:
			return this.dao.listAquisitionRequisition(limit, offset);
			
		case ACQUISITION_QUOTATION:
			return this.dao.listAquisitionQuotation(limit, offset);
			
		case ACQUISITION_ITEM_QUOTATION:
			return this.dao.listAquisitionItemQuotation(limit, offset);
			
		case ACQUISITION_ORDER:
			return this.dao.listAquisitionOrder(limit, offset);
			
		case Z3950_SERVERS:
			return this.dao.listZ3950Servers(limit, offset);
			
		case ACCESS_CONTROL:
			return this.dao.listAccessControl(limit, offset);
			
		case ACCESS_CONTROL_HISTORY:
			return this.dao.listAccessControlHistory(limit, offset);
			
		case LENDINGS: {
			List<LendingDTO> list = this.dao.listLendings(limit, offset);
			
			int i = offset + 1;
			for (LendingDTO dto : list) {
				if (dto.getExpectedReturnDate() == null) {
					this.lendingHistoryMap.put(dto.getId(), i);
				} else {
					this.lendingMap.put(dto.getId(), i);					
				}
				
				dto.setId(i);
				
				i++;
			}
			
			return list;
		}
			
		case LENDING_FINE: {
			List<LendingFineDTO> list = this.dao.listLendingFines(limit, offset);

			for (LendingFineDTO dto : list) {
				dto.setLendingId(this.lendingHistoryMap.get(dto.getLendingId()));
			}

			return list;
		}
		
		case RESERVATIONS:
			return this.dao.listReservations(limit, offset);
			
		case DIGITAL_MEDIA:
		default:
			return null;
		}
	}
	
	private boolean saveDTOs(DataMigrationPhase phase, List<? extends AbstractDTO> dtoList) {
		String schema = this.getSchema();
		
		switch (phase) {
				
			case CATALOGING_BIBLIOGRAPHIC:
				RecordBO biblioBo = RecordBO.getInstance(schema, RecordType.BIBLIO);
				return biblioBo.saveFromBiblivre3(dtoList);
				
			case CATALOGING_AUTHORITIES:
				RecordBO authoritiesBo = RecordBO.getInstance(schema,  RecordType.AUTHORITIES);
				return authoritiesBo.saveFromBiblivre3(dtoList);
				
			case CATALOGING_VOCABULARY:
				RecordBO vocabularyBo = RecordBO.getInstance(schema, RecordType.VOCABULARY);
				return vocabularyBo.saveFromBiblivre3(dtoList);
				
			case CATALOGING_HOLDINGS:
				RecordBO holdingsBo = RecordBO.getInstance(schema, RecordType.HOLDING);
				return holdingsBo.saveFromBiblivre3(dtoList);
				
			case ACCESS_CARDS:
				return AccessCardBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case LOGINS:
				return LoginBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case USER_TYPES:
				return UserTypeBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case USERS:
				return UserBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACQUISITION_SUPPLIER:
				return SupplierBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACQUISITION_REQUISITION:
				return RequestBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACQUISITION_QUOTATION:
				return QuotationBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACQUISITION_ITEM_QUOTATION:
				return QuotationBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACQUISITION_ORDER:
				return OrderBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case Z3950_SERVERS:
				return Z3950BO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACCESS_CONTROL:
				return AccessControlBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case ACCESS_CONTROL_HISTORY:
				return AccessControlBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case LENDINGS:
				return LendingBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case LENDING_FINE:
				return LendingFineBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case RESERVATIONS:
				return ReservationBO.getInstance(schema).saveFromBiblivre3(dtoList);
				
			case DIGITAL_MEDIA:
			default:
				return true;
		}
	}
	
	private List<MemoryFile> listDigitalMedia(int limit, int offset) {
		return this.dao.listDigitalMedia(limit, offset);
	}
	
	private boolean saveDigitalMedia(List<MemoryFile> dtoList) {
		DigitalMediaBO bo = DigitalMediaBO.getInstance(this.getSchema());
		for (MemoryFile file : dtoList) {
			bo.save(file);
		}
		return true;
	}

	public boolean isMigratingDatabase() {
		return this.migratingDatabase;
	}

	public void setMigratingDatabase(boolean migratingDatabase) {
		this.migratingDatabase = migratingDatabase;
	}
	
	public DataMigrationPhase getCurrentPhase() {
		return this.currentPhase;
	}

	public void setCurrentPhase(DataMigrationPhase currentPhase) {
		this.currentPhase = currentPhase;
	}
	
	public Integer getCurrentCount() {
		if (this.currentCount == null) {
			this.currentCount = 0;
		}
		return this.currentCount;
	}

	public void setCurrentCount(Integer currentCount) {
		this.currentCount = currentCount;
	}
}
