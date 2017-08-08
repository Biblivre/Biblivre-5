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
package biblivre.acquisition.supplier;

import biblivre.core.AbstractDTO;

public class SupplierDTO extends AbstractDTO {
	private static final long serialVersionUID = 1L;

	private int id;
	private String trademark;
	private String name;
	private String supplierNumber;
	private String vatRegistrationNumber;
	private String address;
	private String addressNumber;
	private String complement;
	private String area;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private String telephone1;
	private String telephone2;
	private String telephone3;
	private String telephone4;
	private String contact1;
	private String contact2;
	private String contact3;
	private String contact4;
	private String info;
	private String url;
	private String email;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSupplierNumber() {
		return this.supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public String getTrademark() {
		return this.trademark;
	}

	public void setTrademark(String trademark) {
		this.trademark = trademark;
	}

	public String getVatRegistrationNumber() {
		return this.vatRegistrationNumber;
	}

	public void setVatRegistrationNumber(String vatRegistrationNumber) {
		this.vatRegistrationNumber = vatRegistrationNumber;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressNumber() {
		return this.addressNumber;
	}

	public void setAddressNumber(String addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getComplement() {
		return this.complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getTelephone1() {
		return this.telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return this.telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	public String getTelephone3() {
		return this.telephone3;
	}

	public void setTelephone3(String telephone3) {
		this.telephone3 = telephone3;
	}

	public String getTelephone4() {
		return this.telephone4;
	}

	public void setTelephone4(String telephone4) {
		this.telephone4 = telephone4;
	}

	public String getContact1() {
		return this.contact1;
	}

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public String getContact2() {
		return this.contact2;
	}

	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	public String getContact3() {
		return this.contact3;
	}

	public void setContact3(String contact3) {
		this.contact3 = contact3;
	}

	public String getContact4() {
		return this.contact4;
	}

	public void setContact4(String contact4) {
		this.contact4 = contact4;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
