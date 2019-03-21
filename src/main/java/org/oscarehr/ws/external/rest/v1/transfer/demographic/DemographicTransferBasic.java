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
package org.oscarehr.ws.external.rest.v1.transfer.demographic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.oscarehr.ws.validator.DemographicTransferHinConstraint;
import org.oscarehr.ws.validator.ProviderNoConstraint;
import org.oscarehr.ws.validator.StringValueConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement
@Schema(description = "Demographic record data transfer object")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
@DemographicTransferHinConstraint(allowNull = true)
public class DemographicTransferBasic implements Serializable
{
	// demographic base info
	@NotNull
	@Size(min=1, max=30)
	@Schema(description = "patient first name")
	private String firstName;
	@NotNull
	@Size(min=1, max=30)
	@Schema(description = "patient last name")
	private String lastName;
	@Size(max=10)
	@Schema(description = "patient title (Mr, Mrs, Dr, etc.)")
	private String title;
	@NotNull
	@Size(min=1,max=1)
	@Schema(description = "patient gender", allowableValues = {"M", "F", "T", "O", "U"})
	@StringValueConstraint(allows = {"M","F","T","O","U"})
	private String sex;
	@Size(max=12)
	@Schema(description = "patient health insurance number")
	private String hin;
	@Size(max=15)
	@Schema(description = "patient social insurance number")
	private String sin;
	@Size(max=20)
	@Schema(description = "patient health card type")
	private String hcType;
	@Size(max=3)
	@Schema(description = "patient health card version code")
	private String hcVersion;
	@Schema(description = "patient health card end date")
	private LocalDate hcRenewDate;
	@Schema(description = "patient health card start date")
	private LocalDate hcEffectiveDate;
	@NotNull
	@Schema(description = "patient start date")
	private LocalDate dateJoined;
	@Schema(description = "patient end date")
	private LocalDate endDate;
	@NotNull
	@Schema(description = "patient date of birth")
	private LocalDate dateOfBirth;
	@StringValueConstraint(allows = {"AC","IN","DE","FI","MO"})
	@Schema(description = "patient status", example = "AC")
	private String patientStatus;
	@Schema(description = "date of last patient status change")
	private LocalDate patientStatusDate;
	@Size(max=32)
	@Schema(description = "patient veteran number")
	private String veteranNo;

	// address and contact info
	@Size(max=100)
	@Schema(description = "patient email address")
	private String email;
	@Size(max=60)
	@Schema(description = "patient residence street address")
	private String address;
	@Size(max=50)
	@Schema(description = "patient residence city")
	private String city;
	@Size(max=9)
	@Schema(description = "patient residence postal code")
	private String postal;
	@Size(max=20)
	@Schema(description = "patient residence province")
	private String province;
	@Size(max=255)
	@Schema(description = "patient past residence full address line")
	private String previousAddress;
	@Size(max=20)
	@Schema(description = "patient primary (home) phone number")
	private String primaryPhone;
	@Size(max=20)
	@Schema(description = "patient alternate (work) phone number")
	private String secondaryPhone;
	@Schema(description = "patient cell phone number")
	private String cellPhone;
	@Size(max=20)
	private String pcnIndicator;

	// roster info
	@Size(max=20)
	@Schema(description = "patient roster status")
	private String rosterStatus;
	@Size(max=2)
	@Schema(description = "roster termination 2 digit code")
	private String rosterTerminationReason;
	@Schema(description = "date rostered")
	private LocalDate rosterDate;
	@Schema(description = "date of roster termination")
	private LocalDate rosterTerminationDate;

	// physician info
	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "patient assigned physician unique identifier. Id must match an existing provider record.")
	private String providerNo;
	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "patient assigned resident unique identifier. Id must match an existing provider record.")
	private String resident;
	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "patient assigned nurse unique identifier. Id must match an existing provider record.")
	private String nurse;
	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "patient assigned midwife unique identifier. Id must match an existing provider record.")
	private String midwife;
	@Size(max=45)
	@Schema(description = "referring doctor full name (Last, First)", example = "LastName,FirstName")
	private String referralDoctorName;
	@Size(max=10)
	@Schema(description = "referring doctor number")
	private String referralDoctorNo;
	@Size(max=45)
	@Schema(description = "family doctor full name (Last, First)", example = "LastName,FirstName")
	private String familyDoctorName;
	@Size(max=10)
	@Schema(description = "family doctor number")
	private String familyDoctorNo;

	// other info
	@Size(max=10)
	@Schema(description = "patient chart number")
	private String chartNo;
	@Size(max=70)
	@Schema(description = "patient alias")
	private String alias;
	@Size(max=255)
	private String children;
	@Size(max=255)
	private String sourceOfIncome;
	@Size(max=40)
	private String citizenship;
	@Size(max=4)
	@Schema(description = "country of origin country code", example = "CA")
	private String countryOfOrigin;
	@Size(max=60)
	private String spokenLanguage;
	@Size(max=60)
	private String officialLanguage;
	@Size(max=32)
	@StringValueConstraint(allows = {"No","Paper","Electronic"})
	@Schema(description = "newsletter type", allowableValues = {"No","Paper","Electronic"})
	private String newsletter;
	@Size(max=32)
	private String anonymous;
	// notes
	@Schema(description = "The note on the patient master file")
	private String notes;
	@Schema(description = "The alert on the patient master file")
	private String alert;


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

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
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

	public String getHcType()
	{
		return hcType;
	}

	public void setHcType(String hcType)
	{
		this.hcType = hcType;
	}

	public String getHcVersion()
	{
		return hcVersion;
	}

	public void setHcVersion(String hcVersion)
	{
		this.hcVersion = hcVersion;
	}

	public String getSin()
	{
		return sin;
	}

	public void setSin(String sin)
	{
		this.sin = sin;
	}

	public LocalDate getDateJoined()
	{
		return dateJoined;
	}

	public void setDateJoined(LocalDate dateJoined)
	{
		this.dateJoined = dateJoined;
	}

	public LocalDate getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getPatientStatus()
	{
		return patientStatus;
	}

	public void setPatientStatus(String patientStatus)
	{
		this.patientStatus = patientStatus;
	}

	public LocalDate getPatientStatusDate()
	{
		return patientStatusDate;
	}

	public void setPatientStatusDate(LocalDate patientStatusDate)
	{
		this.patientStatusDate = patientStatusDate;
	}

	public String getPrimaryPhone()
	{
		return primaryPhone;
	}

	public void setPrimaryPhone(String primaryPhone)
	{
		this.primaryPhone = primaryPhone;
	}

	public String getCellPhone()
	{
		return cellPhone;
	}

	public void setCellPhone(String cellPhone)
	{
		this.cellPhone = cellPhone;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getResident()
	{
		return resident;
	}

	public void setResident(String resident)
	{
		this.resident = resident;
	}

	public String getNurse()
	{
		return nurse;
	}

	public void setNurse(String nurse)
	{
		this.nurse = nurse;
	}

	public String getMidwife()
	{
		return midwife;
	}

	public void setMidwife(String midwife)
	{
		this.midwife = midwife;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	public String getPreviousAddress()
	{
		return previousAddress;
	}

	public void setPreviousAddress(String previousAddress)
	{
		this.previousAddress = previousAddress;
	}

	public String getReferralDoctorName()
	{
		return referralDoctorName;
	}

	public void setReferralDoctorName(String referralDoctorName)
	{
		this.referralDoctorName = referralDoctorName;
	}

	public String getReferralDoctorNo()
	{
		return referralDoctorNo;
	}

	public void setReferralDoctorNo(String referralDoctorNo)
	{
		this.referralDoctorNo = referralDoctorNo;
	}

	public String getFamilyDoctorName()
	{
		return familyDoctorName;
	}

	public void setFamilyDoctorName(String familyDoctorName)
	{
		this.familyDoctorName = familyDoctorName;
	}

	public String getFamilyDoctorNo()
	{
		return familyDoctorNo;
	}

	public void setFamilyDoctorNo(String familyDoctorNo)
	{
		this.familyDoctorNo = familyDoctorNo;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getPostal()
	{
		return postal;
	}

	public void setPostal(String postal)
	{
		this.postal = postal;
	}

	public LocalDate getHcRenewDate()
	{
		return hcRenewDate;
	}

	public void setHcRenewDate(LocalDate hcRenewDate)
	{
		this.hcRenewDate = hcRenewDate;
	}

	public String getSecondaryPhone()
	{
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone)
	{
		this.secondaryPhone = secondaryPhone;
	}

	public String getPcnIndicator()
	{
		return pcnIndicator;
	}

	public void setPcnIndicator(String pcnIndicator)
	{
		this.pcnIndicator = pcnIndicator;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}

	public String getChartNo()
	{
		return chartNo;
	}

	public void setChartNo(String chartNo)
	{
		this.chartNo = chartNo;
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	public LocalDate getHcEffectiveDate()
	{
		return hcEffectiveDate;
	}

	public void setHcEffectiveDate(LocalDate hcEffectiveDate)
	{
		this.hcEffectiveDate = hcEffectiveDate;
	}

	public String getRosterStatus()
	{
		return rosterStatus;
	}

	public void setRosterStatus(String rosterStatus)
	{
		this.rosterStatus = rosterStatus;
	}

	public LocalDate getRosterDate()
	{
		return rosterDate;
	}

	public void setRosterDate(LocalDate rosterDate)
	{
		this.rosterDate = rosterDate;
	}

	public LocalDate getRosterTerminationDate()
	{
		return rosterTerminationDate;
	}

	public void setRosterTerminationDate(LocalDate rosterTerminationDate)
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

	public String getVeteranNo()
	{
		return veteranNo;
	}

	public void setVeteranNo(String veteranNo)
	{
		this.veteranNo = veteranNo;
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

	public String getAnonymous()
	{
		return anonymous;
	}

	public void setAnonymous(String anonymous)
	{
		this.anonymous = anonymous;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getAlert()
	{
		return alert;
	}

	public void setAlert(String alert)
	{
		this.alert = alert;
	}

	@Override
	public String toString() {
		return (ReflectionToStringBuilder.toString(this));
	}

}
