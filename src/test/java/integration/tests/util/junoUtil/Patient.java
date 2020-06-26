/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package integration.tests.util.junoUtil;

  public class Patient
{

	private String lastName = "";
	private String firstName = "";
	private String dobYear = "";
	private String dobMonth = "";
	private String dobDate = "";
	private String sex = "";
	private String hin = "";

	private String language = "English";
	private String title = "MS";
	private String spoken = "English";
	private String address = "31 Bastion Square #302";
	private String city = "Victoria";
	private String province = "BC";
	private String postal = "V8W 1J1";
	private String homePhone = "686-8560";
	private String homePhoneExt = "101";
	private String workPhone = "250-250-2500";
	private String workPhoneExt = "102";
	private String cellPhone = "250-250-2500";
	private String phoneComment = "Prefer Cell";
	private String newsletter = "No";
	private String aboriginal = "No";
	private String email = "ailin.zhu@cloudpractice.ca";
	private String phrUserName = "TTestLastName";
	private String effYear = "2020";
	private String effMonth = "06";
	private String effDate = "01";
	private String hcType = "BC";
	private String hcRenewYear = "2018";
	private String hcRenewMonth = "08";
	private String hcRenewDate = "08";
	private String countryOfOrigin = "CA";
	private String sin = "987654321";
	private String cytology = "123456";
	private String motherName = "Mom TestLastName";
	private String fatherName = "Dad TestLastName";
	private String referralDoctor = "Dr Referral";
	private String referralDoctorNo = "111111";//max 6 numbers
	private String rosterStatus = "RO";
	private String rosteredYear = "2018";
	private String rosteredMonth = "08";
	private String rosteredDate = "08";
	private String patientStatus = "AC";
	private String chartNo = "10001";



	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDobYear() {
		return dobYear;
	}

	public void setDobYear(String dobYear) {
		this.dobYear = dobYear;
	}

	public String getDobMonth() {
		return dobMonth;
	}

	public void setDobMonth(String dobMonth) {
		this.dobMonth = dobMonth;
	}

	public String getDobDate() {
		return dobDate;
	}

	public void setDobDate(String dobDate) {
		this.dobDate = dobDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getHin() {
		return hin;
	}

	public void setHin(String hin) {
		this.hin = hin;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSpoken() {
		return spoken;
	}

	public void setSpoken(String spoken) {
		this.spoken = spoken;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostal() {
		return postal;
	}

	public void setPostal(String postal) {
		this.postal = postal;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getHomePhoneExt() {
		return homePhoneExt;
	}

	public void setHomePhoneExt(String homePhoneExt) {
		this.homePhoneExt = homePhoneExt;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getWorkPhoneExt() {
		return workPhoneExt;
	}

	public void setWorkPhoneExt(String workPhoneExt) {
		this.workPhoneExt = workPhoneExt;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getPhoneComment() {
		return phoneComment;
	}

	public void setPhoneComment(String phoneComment) {
		this.phoneComment = phoneComment;
	}

	public String getNewsletter() {
		return newsletter;
	}

	public void setNewsletter(String newsletter) {
		this.newsletter = newsletter;
	}

	public String getAboriginal() {
		return aboriginal;
	}

	public void setAboriginal(String aboriginal) {
		this.aboriginal = aboriginal;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhrUserName() {
		return phrUserName;
	}

	public void setPhrUserName(String phrUserName) {
		this.phrUserName = phrUserName;
	}

	public String getEffYear() {
		return effYear;
	}

	public void setEffYear(String effYear) {
		this.effYear = effYear;
	}

	public String getEffMonth() {
		return effMonth;
	}

	public void setEffMonth(String effMonth) {
		this.effMonth = effMonth;
	}

	public String getEffDate() {
		return effDate;
	}

	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	public String getHcType() {
		return hcType;
	}

	public void setHcType(String hcType) {
		this.hcType = hcType;
	}

	public String getHcRenewYear() {
		return hcRenewYear;
	}

	public void setHcRenewYear(String hcRenewYear) {
		this.hcRenewYear = hcRenewYear;
	}

	public String getHcRenewMonth() {
		return hcRenewMonth;
	}

	public void setHcRenewMonth(String hcRenewMonth) {
		this.hcRenewMonth = hcRenewMonth;
	}

	public String getHcRenewDate() {
		return hcRenewDate;
	}

	public void setHcRenewDate(String hcRenewDate) {
		this.hcRenewDate = hcRenewDate;
	}

	public String getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin) {
		this.countryOfOrigin = countryOfOrigin;
	}

	public String getSin() {
		return sin;
	}

	public void setSin(String sin) {
		this.sin = sin;
	}

	public String getCytology() {
		return cytology;
	}

	public void setCytology(String cytology) {
		this.cytology = cytology;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getReferralDoctor() {
		return referralDoctor;
	}

	public void setReferralDoctor(String referralDoctor) {
		this.referralDoctor = referralDoctor;
	}

	public String getReferralDoctorNo() {
		return referralDoctorNo;
	}

	public void setReferralDoctorNo(String referralDoctorNo) {
		this.referralDoctorNo = referralDoctorNo;
	}

	public String getRosterStatus() {
		return rosterStatus;
	}

	public void setRosterStatus(String rosterStatus) {
		this.rosterStatus = rosterStatus;
	}

	public String getRosteredYear() {
		return rosteredYear;
	}

	public void setRosteredYear(String rosteredYear) {
		this.rosteredYear = rosteredYear;
	}

	public String getRosteredMonth() {
		return rosteredMonth;
	}

	public void setRosteredMonth(String rosteredMonth) {
		this.rosteredMonth = rosteredMonth;
	}

	public String getRosteredDate() {
		return rosteredDate;
	}

	public void setRosteredDate(String rosteredDate) {
		this.rosteredDate = rosteredDate;
	}

	public String getPatientStatus() {
		return patientStatus;
	}

	public void setPatientStatus(String patientStatus) {
		this.patientStatus = patientStatus;
	}

	public String getChartNo() {
		return chartNo;
	}

	public void setChartNo(String chartNo) {
		this.chartNo = chartNo;
	}


}
