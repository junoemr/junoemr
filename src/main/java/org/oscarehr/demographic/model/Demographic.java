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
package org.oscarehr.demographic.model;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity(name = "model.Demographic") // use a name to prevent autowire conflict with old model
@Table(name = "demographic")
public class Demographic extends AbstractModel<Integer> implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "demographic_no")
	private Integer demographicId;

	// base info
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "title")
	private String title;
	@Column(name = "date_of_birth")
	private String dayOfBirth;
	@Column(name = "month_of_birth")
	private String monthOfBirth;
	@Column(name = "year_of_birth")
	private String yearOfBirth;
	@Column(name = "sex")
	private String sex;
	@Column(name = "hin")
	private String hin;
	@Column(name = "ver")
	private String ver;
	@Column(name = "hc_type")
	private String hcType;
	@Column(name = "eff_date")
	@Temporal(TemporalType.DATE)
	private Date hcEffectiveDate;
	@Column(name = "hc_renew_date")
	@Temporal(TemporalType.DATE)
	private Date hcRenewDate;
	@Column(name = "chart_no")
	private String chartNo;
	@Column(name = "sin")
	private String sin;
	@Column(name = "patient_status")
	private String patientStatus;
	@Column(name = "patient_status_date")
	@Temporal(TemporalType.DATE)
	private Date patientStatusDate;
	@Column(name = "date_joined")
	@Temporal(TemporalType.DATE)
	private Date dateJoined;
	@Column(name = "end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	//contact info
	@Column(name = "address")
	private String address;
	@Column(name = "city")
	private String city;
	@Column(name = "province")
	private String province;
	@Column(name = "postal")
	private String postal;
	@Column(name = "email")
	private String email;
	@Column(name = "phone")
	private String phone;
	@Column(name = "phone2")
	private String phone2;
	@Column(name = "previousAddress")
	private String previousAddress;

	// physician info
	@Column(name = "provider_no")
	private String providerNo;
	@Column(name = "family_doctor")
	private String referralDoctor;
	@Column(name = "family_doctor_2")
	private String familyDoctor;

	// roster info
	@Column(name = "roster_status")
	private String rosterStatus;
	@Column(name = "roster_date")
	@Temporal(TemporalType.DATE)
	private Date rosterDate;
	@Column(name = "roster_termination_date")
	@Temporal(TemporalType.DATE)
	private Date rosterTerminationDate;
	@Column(name = "roster_termination_reason")
	private String rosterTerminationReason;

	// other info
	@Column(name = "lastUpdateUser")
	private String lastUpdateUser;
	@Column(name = "lastUpdateDate")
	@Temporal(TemporalType.DATE)
	private Date lastUpdateDate = new Date();

	@Column(name = "pcn_indicator")
	private String pcnIndicator;
	@Column(name = "alias")
	private String alias;
	@Column(name = "children")
	private String children;
	@Column(name = "sourceOfIncome")
	private String sourceOfIncome;
	@Column(name = "citizenship")
	private String citizenship;
	@Column(name = "anonymous")
	private String anonymous;
	@Column(name = "spoken_lang")
	private String spokenLanguage;
	@Column(name = "official_lang")
	private String officialLanguage;
	@Column(name = "country_of_origin")
	private String countryOfOrigin;
	@Column(name = "newsletter")
	private String newsletter;
	@Column(name = "veteran_no")
	private String veteranNo;
	@Column(name = "name_of_mother")
	private String nameOfMother;
	@Column(name = "name_of_father")
	private String nameOfFather;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "id")
	private List<DemographicCust> demographicCust;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "demographicNo")
	private List<DemographicExt> demographicExtList;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "demographicNo")
	@Where(clause="deleted=0")
	private List<DemographicMerged> mergedDemographicsList;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "mergedTo")
	private List<DemographicMerged> mergedToDemographicsList;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="provider_no", insertable=false, updatable=false)
	private ProviderData provider;

	public static final String BC_NEWBORN_BILLING_CODE = "66";

	public enum HC_TYPE
	{
		OT,
		AB,
		BC,
		MB,
		NB,
		NL,
		NT,
		NS,
		NU,
		ON,
		PE,
		QC,
		SK,
		YT,
		PP
	}


	/**
	 * Determine if demographic is a newborn.  A demographic is a newborn if the HIN version code is 66 in BC, or
	 * under a year old in all other cases.
	 *
	 * @return true if the demographic meets newborn criteria listed above.
	 */
	public static boolean isNewBorn(LocalDate birthDate, String HINVersion)
	{
		OscarProperties oscarProperties = OscarProperties.getInstance();

		if (oscarProperties.isBritishColumbiaInstanceType())
		{
			return (HINVersion != null && HINVersion.equals(BC_NEWBORN_BILLING_CODE));
		}

		LocalDate now = LocalDate.now();

		long dayDifference = DAYS.between(birthDate, now);

		return dayDifference < 365;
	}

	@Override
	public Integer getId()
	{
		return demographicId;
	}

	public Integer getDemographicId()
	{
		return demographicId;
	}

	public void setDemographicId(Integer demographicId)
	{
		this.demographicId = demographicId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getDisplayName()
	{
		return getFormattedName();
	}

	public String getFormattedName()
	{
		String lastName = (getLastName() == null) ? "" : getLastName().trim();
		String firstName = (getFirstName() == null) ? "" : getFirstName().trim();
		if(!lastName.isEmpty() && !firstName.isEmpty())
		{
			lastName += ", ";
		}
		return lastName + firstName;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDayOfBirth()
	{
		return dayOfBirth;
	}

	public void setDayOfBirth(String dateOfBirth)
	{
		this.dayOfBirth = dateOfBirth;
	}

	public String getMonthOfBirth()
	{
		return monthOfBirth;
	}

	public void setMonthOfBirth(String monthOfBirth)
	{
		this.monthOfBirth = monthOfBirth;
	}

	public String getYearOfBirth()
	{
		return yearOfBirth;
	}

	public LocalDate getDateOfBirth()
	{
		try
		{
			return LocalDate.of(Integer.parseInt(yearOfBirth), Integer.parseInt(monthOfBirth), Integer.parseInt(dayOfBirth));
		}
		catch (NumberFormatException | DateTimeException ex)
		{
			MiscUtils.getLogger().error("Demographic [" + getId() + "] has invalid dob with error: " + ex.getMessage());
		}
		return null;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		setDayOfBirth(StringUtils.leftPad(String.valueOf(dateOfBirth.getDayOfMonth()), 2,"0"));
		setMonthOfBirth(StringUtils.leftPad(String.valueOf(dateOfBirth.getMonthValue()), 2,"0"));
		setYearOfBirth(StringUtils.leftPad(String.valueOf(dateOfBirth.getYear()), 4,"0"));
	}

	public void setYearOfBirth(String yearOfBirth)
	{
		this.yearOfBirth = yearOfBirth;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getHin()
	{
		return hin;
	}

	public void setHin(String hin)
	{
		this.hin = hin;
	}

	public String getVer()
	{
		return ver;
	}

	public void setVer(String ver)
	{
		this.ver = ver;
	}

	public String getHcType()
	{
		return hcType;
	}

	public void setHcType(String hcType)
	{
		this.hcType = hcType;
	}

	public Date getHcEffectiveDate()
	{
		return hcEffectiveDate;
	}

	public void setHcEffectiveDate(Date effDate)
	{
		this.hcEffectiveDate = effDate;
	}

	public Date getHcRenewDate()
	{
		return hcRenewDate;
	}

	public void setHcRenewDate(Date hcRenewDate)
	{
		this.hcRenewDate = hcRenewDate;
	}

	public String getChartNo()
	{
		return chartNo;
	}

	public void setChartNo(String chartNo)
	{
		this.chartNo = chartNo;
	}

	public String getSin()
	{
		return sin;
	}

	public void setSin(String sin)
	{
		this.sin = sin;
	}

	public String getPatientStatus()
	{
		return patientStatus;
	}

	public void setPatientStatus(String patientStatus)
	{
		this.patientStatus = patientStatus;
	}

	public Date getPatientStatusDate()
	{
		return patientStatusDate;
	}

	public void setPatientStatusDate(Date patientStatusDate)
	{
		this.patientStatusDate = patientStatusDate;
	}

	public Date getDateJoined()
	{
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined)
	{
		this.dateJoined = dateJoined;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getPostal()
	{
		return postal;
	}

	public void setPostal(String postal)
	{
		this.postal = postal;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getPhone2()
	{
		return phone2;
	}

	public void setPhone2(String phone2)
	{
		this.phone2 = phone2;
	}

	public String getPreviousAddress()
	{
		return previousAddress;
	}

	public void setPreviousAddress(String previousAddress)
	{
		this.previousAddress = previousAddress;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getReferralDoctor()
	{
		return referralDoctor;
	}

	public String getReferralDoctorName()
	{
		return StringUtils.substringBetween(getReferralDoctor(), "<rd>", "</rd>");
	}

	public String getReferralDoctorNumber()
	{
		return StringUtils.substringBetween(getReferralDoctor(), "<rdohip>", "</rdohip>");
	}

	public void setReferralDoctor(String familyDoctor)
	{
		this.referralDoctor = familyDoctor;
	}

	public String getFamilyDoctor()
	{
		return familyDoctor;
	}

	public String getFamilyDoctorName()
	{
		return StringUtils.substringBetween(getFamilyDoctor(), "<fdname>", "</fdname>");
	}

	public String getFamilyDoctorNumber()
	{
		return StringUtils.substringBetween(getFamilyDoctor(), "<fd>", "</fd>");
	}

	public void setFamilyDoctor(String familyDoctor2)
	{
		this.familyDoctor = familyDoctor2;
	}

	public String getRosterStatus()
	{
		return rosterStatus;
	}

	public void setRosterStatus(String rosterStatus)
	{
		this.rosterStatus = rosterStatus;
	}

	public Date getRosterDate()
	{
		return rosterDate;
	}

	public void setRosterDate(Date rosterDate)
	{
		this.rosterDate = rosterDate;
	}

	public Date getRosterTerminationDate()
	{
		return rosterTerminationDate;
	}

	public void setRosterTerminationDate(Date rosterTerminationDate)
	{
		this.rosterTerminationDate = rosterTerminationDate;
	}

	public String getRosterTerminationReason()
	{
		return rosterTerminationReason;
	}

	public void setRosterTerminationReason(String rosterTerminationReason)
	{
		this.rosterTerminationReason = rosterTerminationReason;
	}

	public String getLastUpdateUser()
	{
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser)
	{
		this.lastUpdateUser = lastUpdateUser;
	}

	public Date getLastUpdateDate()
	{
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate)
	{
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getPcnIndicator()
	{
		return pcnIndicator;
	}

	public void setPcnIndicator(String pcnIndicator)
	{
		this.pcnIndicator = pcnIndicator;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public String getChildren()
	{
		return children;
	}

	public void setChildren(String children)
	{
		this.children = children;
	}

	public String getSourceOfIncome()
	{
		return sourceOfIncome;
	}

	public void setSourceOfIncome(String sourceOfIncome)
	{
		this.sourceOfIncome = sourceOfIncome;
	}

	public String getCitizenship()
	{
		return citizenship;
	}

	public void setCitizenship(String citizenship)
	{
		this.citizenship = citizenship;
	}

	public String getAnonymous()
	{
		return anonymous;
	}

	public void setAnonymous(String anonymous)
	{
		this.anonymous = anonymous;
	}

	public String getSpokenLanguage()
	{
		return spokenLanguage;
	}

	public void setSpokenLanguage(String spokenLanguage)
	{
		this.spokenLanguage = spokenLanguage;
	}

	public String getOfficialLanguage()
	{
		return officialLanguage;
	}

	public void setOfficialLanguage(String officialLanguage)
	{
		this.officialLanguage = officialLanguage;
	}

	public String getCountryOfOrigin()
	{
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String countryOfOrigin)
	{
		this.countryOfOrigin = countryOfOrigin;
	}

	public String getNewsletter()
	{
		return newsletter;
	}

	public void setNewsletter(String newsletter)
	{
		this.newsletter = newsletter;
	}

	public String getVeteranNo()
	{
		return veteranNo;
	}

	public void setVeteranNo(String veteranNo)
	{
		this.veteranNo = veteranNo;
	}

	public String getNameOfMother()
	{
		return nameOfMother;
	}

	public void setNameOfMother(String mother)
	{
		this.nameOfMother = mother;
	}

	public String getNameOfFather()
	{
		return nameOfFather;
	}

	public void setNameOfFather(String father)
	{
		this.nameOfFather = father;
	}

	public List<DemographicCust> getDemographicCust()
	{
		return demographicCust;
	}

	public void setDemographicCust(List<DemographicCust> demographicCust)
	{
		this.demographicCust = demographicCust;
	}

	public List<DemographicExt> getDemographicExtList()
	{
		return demographicExtList;
	}

	public void setDemographicExtList(List<DemographicExt> demographicExtList)
	{
		this.demographicExtList = demographicExtList;
	}

	public List<DemographicMerged> getMergedDemographicsList()
	{
		return mergedDemographicsList;
	}

	public void setMergedDemographicsList(List<DemographicMerged> mergedDemographicsList)
	{
		this.mergedDemographicsList = mergedDemographicsList;
	}

	public List<DemographicMerged> getMergedToDemographicsList()
	{
		return mergedToDemographicsList;
	}

	public void setMergedToDemographicsList(List<DemographicMerged> mergedToDemographicsList)
	{
		this.mergedToDemographicsList = mergedToDemographicsList;
	}

	public ProviderData getProvider()
	{
		return provider;
	}

	public void setProvider(ProviderData provider)
	{
		this.provider = provider;
	}

	public boolean isNewBorn()
	{
		return Demographic.isNewBorn(getDateOfBirth(), getVer());
	}
}
