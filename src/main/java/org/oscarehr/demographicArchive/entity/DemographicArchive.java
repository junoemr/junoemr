/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.demographicArchive.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.util.MiscUtils;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "demographicArchive")
public class DemographicArchive extends AbstractModel<Long> implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id = null;
	@Column(name = "demographic_no")
	private Integer demographicNo = null;
	@Column(name = "title")
	private String title = null;
	@Column(name = "last_name")
	private String lastName = null;
	@Column(name = "first_name")
	private String firstName = null;
	@Column(name = "address")
	private String address = null;
	@Column(name = "city")
	private String city = null;
	@Column(name = "province")
	private String province = null;
	@Column(name = "postal")
	private String postal = null;
	@Column(name = "phone")
	private String phone = null;
	@Column(name = "phone2")
	private String phone2 = null;
	@Column(name = "email")
	private String email = null;
	private String myOscarUserName = null;
	@Column(name = "year_of_birth")
	private String yearOfBirth = null;
	@Column(name = "month_of_birth")
	private String monthOfBirth = null;
	@Column(name = "date_of_birth")
	private String dayOfBirth = null;
	@Column(name = "hin")
	private String hin = null;
	@Column(name = "ver")
	private String ver = null;
	@Column(name = "roster_status")
	private String rosterStatus = null;
	@Column(name = "roster_date")
	@Temporal(TemporalType.DATE)
	private Date rosterDate = null;
	@Column(name = "roster_termination_date")
	@Temporal(TemporalType.DATE)
	private Date rosterTerminationDate = null;
	@Column(name = "roster_termination_reason")
	private String rosterTerminationReason = null;
	@Column(name = "patient_status")
	private String patientStatus = null;
	@Column(name = "patient_status_date")
	@Temporal(TemporalType.DATE)
	private Date patientStatusDate = null;
	@Column(name = "date_joined")
	@Temporal(TemporalType.DATE)
	private Date dateJoined = null;
	@Column(name = "chart_no")
	private String chartNo = null;
	@Column(name = "official_lang")
	private String officialLanguage = null;
	@Column(name = "spoken_lang")
	private String spokenLanguage = null;
	@Column(name = "provider_no")
	private String providerNo = null;
	@Column(name = "sex")
	private String sex = null;
	@Column(name = "end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate = null;
	@Column(name = "eff_date")
	@Temporal(TemporalType.DATE)
	private Date effDate = null;
	@Column(name = "pcn_indicator")
	private String pcnIndicator = null;
	@Column(name = "hc_type")
	private String hcType = null;
	@Column(name = "hc_renew_date")
	@Temporal(TemporalType.DATE)
	Date hcRenewDate = null;
	@Column(name = "family_doctor")
	private String familyDoctor = null;
	@Column(name = "family_doctor_2")
	private String familyDoctor2 = null;
	@Column(name = "alias")
	private String alias = null;
	@Column(name = "previousAddress")
	private String previousAddress = null;
	@Column(name = "children")
	private String children = null;
	@Column(name = "sourceOfIncome")
	private String sourceOfIncome = null;
	@Column(name = "citizenship")
	private String citizenship = null;
	@Column(name = "sin")
	private String sin = null;
	@Column(name = "country_of_origin")
	private String countryOfOrigin = null;
	@Column(name = "newsletter")
	private String newsletter = null;
	@Column(name = "anonymous")
	private String anonymous = null;
	@Column(name = "lastUpdateUser")
	private String lastUpdateUser = null;
	@Column(name = "lastUpdateDate")
	@Temporal(TemporalType.DATE)
	private Date lastUpdateDate = null;
	@Column(name = "veteran_no")
	private String veteranNo = null;
	@Column(name = "electronic_messaging_consent_given_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date electronicMessagingConsentGivenAt;
	@Column(name = "electronic_messaging_consent_rejected_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date electronicMessagingConsentRejectedAt;

	@OneToOne(fetch= FetchType.LAZY, mappedBy = "demographicNo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private DemographicCustArchive demographicCustArchive;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "demographicNo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private Set<DemographicExtArchive> demographicExtArchiveSet;

	public DemographicArchive()
	{
	}

	@Deprecated
	public DemographicArchive(Demographic demographic)
	{
		this.address = demographic.getAddress();
		this.alias = demographic.getAlias();
		this.anonymous = demographic.getAnonymous();
		this.chartNo = demographic.getChartNo();
		this.children = demographic.getChildren();
		this.citizenship = demographic.getCitizenship();
		this.city = demographic.getCity();
		this.countryOfOrigin = demographic.getCountryOfOrigin();
		this.dateJoined = demographic.getDateJoined();
		this.dayOfBirth = demographic.getDateOfBirth();
		this.monthOfBirth = demographic.getMonthOfBirth();
		this.yearOfBirth = demographic.getYearOfBirth();
		this.demographicNo = demographic.getDemographicNo();
		this.effDate = demographic.getEffDate();
		this.email = demographic.getEmail();
		this.endDate = demographic.getEndDate();
		this.familyDoctor = demographic.getFamilyDoctor();
		this.familyDoctor2 = demographic.getFamilyDoctor2();
		this.firstName = demographic.getFirstName();
		this.hcRenewDate = demographic.getHcRenewDate();
		this.hcType = demographic.getHcType();
		this.hin = demographic.getHin();
		this.lastName = demographic.getLastName();
		this.lastUpdateDate = demographic.getLastUpdateDate();
		this.lastUpdateUser = demographic.getLastUpdateUser();
		this.monthOfBirth = demographic.getMonthOfBirth();
		this.myOscarUserName = demographic.getMyOscarUserName();
		this.newsletter = demographic.getNewsletter();
		this.officialLanguage = demographic.getOfficialLanguage();
		this.patientStatus = demographic.getPatientStatus();
		this.patientStatusDate = demographic.getPatientStatusDate();
		this.pcnIndicator = demographic.getPcnIndicator();
		this.phone = demographic.getPhone();
		this.phone2 = demographic.getPhone2();
		this.postal = demographic.getPostal();
		this.previousAddress = demographic.getPreviousAddress();
		this.providerNo = demographic.getProviderNo();
		this.province = demographic.getProvince();
		this.rosterDate = demographic.getRosterDate();
		this.rosterStatus = demographic.getRosterStatus();
		this.rosterTerminationDate = demographic.getRosterTerminationDate();
		this.rosterTerminationReason = demographic.getRosterTerminationReason();
		this.sex = demographic.getSex();
		this.sin = demographic.getSin();
		this.sourceOfIncome = demographic.getSourceOfIncome();
		this.spokenLanguage = demographic.getSpokenLanguage();
		this.title = demographic.getTitle();
		this.ver = demographic.getVer();
		this.yearOfBirth = demographic.getYearOfBirth();
		this.setElectronicMessagingConsentGivenAt(demographic.getElectronicMessagingConsentGivenAt());
		this.setElectronicMessagingConsentRejectedAt(demographic.getElectronicMessagingConsentRejectedAt());
	}

	public DemographicArchive(org.oscarehr.demographic.entity.Demographic demographic)
	{
		this.address = demographic.getAddress();
		this.alias = demographic.getAlias();
		this.anonymous = demographic.getAnonymous();
		this.chartNo = demographic.getChartNo();
		this.children = demographic.getChildren();
		this.citizenship = demographic.getCitizenship();
		this.city = demographic.getCity();
		this.countryOfOrigin = demographic.getCountryOfOrigin();
		this.dateJoined = demographic.getDateJoined();
		this.setDateOfBirth(demographic.getDateOfBirth());
		this.demographicNo = demographic.getDemographicId();
		this.effDate = demographic.getHcEffectiveDate();
		this.email = demographic.getEmail();
		this.endDate = demographic.getEndDate();
		this.familyDoctor = demographic.getReferralDoctor();
		this.familyDoctor2 = demographic.getFamilyDoctor();
		this.firstName = demographic.getFirstName();
		this.hcRenewDate = demographic.getHcRenewDate();
		this.hcType = demographic.getHcType();
		this.hin = demographic.getHin();
		this.lastName = demographic.getLastName();
		this.lastUpdateDate = demographic.getLastUpdateDate();
		this.lastUpdateUser = demographic.getLastUpdateUser();
		this.monthOfBirth = demographic.getMonthOfBirth();
		this.newsletter = demographic.getNewsletter();
		this.officialLanguage = demographic.getOfficialLanguage();
		this.patientStatus = demographic.getPatientStatus();
		this.patientStatusDate = demographic.getPatientStatusDate();
		this.pcnIndicator = demographic.getPcnIndicator();
		this.phone = demographic.getPhone();
		this.phone2 = demographic.getPhone2();
		this.postal = demographic.getPostal();
		this.previousAddress = demographic.getPreviousAddress();
		this.providerNo = demographic.getProviderNo();
		this.province = demographic.getProvince();
		this.rosterDate = demographic.getRosterDate();
		this.rosterStatus = demographic.getRosterStatus();
		this.rosterTerminationDate = demographic.getRosterTerminationDate();
		this.rosterTerminationReason = demographic.getRosterTerminationReason();
		this.sex = demographic.getSex();
		this.sin = demographic.getSin();
		this.sourceOfIncome = demographic.getSourceOfIncome();
		this.spokenLanguage = demographic.getSpokenLanguage();
		this.title = demographic.getTitle();
		this.ver = demographic.getVer();
		this.yearOfBirth = demographic.getYearOfBirth();
	}

	public LocalDate getDateOfBirth()
	{
		try
		{
			return LocalDate.of(Integer.parseInt(yearOfBirth), Integer.parseInt(monthOfBirth), Integer.parseInt(dayOfBirth));
		}
		catch (NumberFormatException | DateTimeException ex)
		{
			MiscUtils.getLogger().error("DemographicArchive [" + getId() + "] has invalid dob with error: " + ex.getMessage());
		}
		return null;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		setDayOfBirth(StringUtils.leftPad(String.valueOf(dateOfBirth.getDayOfMonth()), 2,"0"));
		setMonthOfBirth(StringUtils.leftPad(String.valueOf(dateOfBirth.getMonthValue()), 2,"0"));
		setYearOfBirth(StringUtils.leftPad(String.valueOf(dateOfBirth.getYear()), 4,"0"));
	}

	@Override
	public Long getId()
	{
		return this.id;
	}
}
