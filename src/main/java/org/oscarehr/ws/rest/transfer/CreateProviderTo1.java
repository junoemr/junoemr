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
package org.oscarehr.ws.rest.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProviderTo1 implements Serializable
{
	// user info
	private String firstName;
	private String lastName;
	private String type;
	private String speciality;
	private String team;
	private String sex;
	private LocalDate dateOfBirth;

	// login info
	private String email;
	private String userName;
	private String password;
	private String secondLevelPasscode;

	// Contact Information
	private String address;
	private String homePhone;
	private String workPhone;
	private String cellPhone;
	private String otherPhone;
	private String fax;
	private String contactEmail;
	private String pagerNumber;

	// access Roles
	private String[] userRoles;

	// site assignments
	private String[] siteAssignment;

	// BC billing
	private String bcBillingNo;
	private String bcRuralRetentionCode;
	private String bcServiceLocation;

	// ON billing
	private String onGroupNumber;
	private String onSpecialityCode;
	private String onVisitLocation;
	private String onServiceLocationIndicator;

	// AB billing
	private String abClinic;
	private String abSourceCode;
	private String abSkillCode;
	private String abLocationCode;
	private String abBANumber;
	private String abFacilityNumber;
	private String abFunctionalCenter;
	private String abRoleModifier;

	// SK billing
	private String skMode;
	private String skLocationCode;
	private String skSubmissionType;
	private String skCorporationIndicator;

	// Common Billing
	private String ohipNo;
	private String thirdPartyBillingNo;
	private String alternateBillingNo;

	//3rd Party Identifiers
	private String cpsid;
	private String ihaProviderMnemonic;
	private String connectCareId;
	private String takNumber;
	private String lifeLabsClientIds;
	private String eDeliveryIds;

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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getSpeciality()
	{
		return speciality;
	}

	public void setSpeciality(String speciality)
	{
		this.speciality = speciality;
	}

	public String getTeam()
	{
		return team;
	}

	public void setTeam(String team)
	{
		this.team = team;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public LocalDate getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getSecondLevelPasscode()
	{
		return secondLevelPasscode;
	}

	public void setSecondLevelPasscode(String secondLevelPasscode)
	{
		this.secondLevelPasscode = secondLevelPasscode;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getHomePhone()
	{
		return homePhone;
	}

	public void setHomePhone(String homePhone)
	{
		this.homePhone = homePhone;
	}

	public String getWorkPhone()
	{
		return workPhone;
	}

	public void setWorkPhone(String workPhone)
	{
		this.workPhone = workPhone;
	}

	public String getCellPhone()
	{
		return cellPhone;
	}

	public void setCellPhone(String cellPhone)
	{
		this.cellPhone = cellPhone;
	}

	public String getOtherPhone()
	{
		return otherPhone;
	}

	public void setOtherPhone(String otherPhone)
	{
		this.otherPhone = otherPhone;
	}

	public String getFax()
	{
		return fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getContactEmail()
	{
		return contactEmail;
	}

	public void setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
	}

	public String getPagerNumber()
	{
		return pagerNumber;
	}

	public void setPagerNumber(String pagerNumber)
	{
		this.pagerNumber = pagerNumber;
	}

	public String[] getUserRoles()
	{
		return userRoles;
	}

	public void setUserRoles(String[] userRoles)
	{
		this.userRoles = userRoles;
	}

	public String[] getSiteAssignment()
	{
		return siteAssignment;
	}

	public void setSiteAssignment(String[] siteAssignment)
	{
		this.siteAssignment = siteAssignment;
	}

	public String getBcBillingNo()
	{
		return bcBillingNo;
	}

	public void setBcBillingNo(String bcBillingNo)
	{
		this.bcBillingNo = bcBillingNo;
	}

	public String getBcRuralRetentionCode()
	{
		return bcRuralRetentionCode;
	}

	public void setBcRuralRetentionCode(String bcRuralRetentionCode)
	{
		this.bcRuralRetentionCode = bcRuralRetentionCode;
	}

	public String getBcServiceLocation()
	{
		return bcServiceLocation;
	}

	public void setBcServiceLocation(String bcServiceLocation)
	{
		this.bcServiceLocation = bcServiceLocation;
	}

	public String getOnGroupNumber()
	{
		return onGroupNumber;
	}

	public void setOnGroupNumber(String onGroupNumber)
	{
		this.onGroupNumber = onGroupNumber;
	}

	public String getOnSpecialityCode()
	{
		return onSpecialityCode;
	}

	public void setOnSpecialityCode(String onSpecialityCode)
	{
		this.onSpecialityCode = onSpecialityCode;
	}

	public String getOnVisitLocation()
	{
		return onVisitLocation;
	}

	public void setOnVisitLocation(String onVisitLocation)
	{
		this.onVisitLocation = onVisitLocation;
	}

	public String getOnServiceLocationIndicator()
	{
		return onServiceLocationIndicator;
	}

	public void setOnServiceLocationIndicator(String onServiceLocationIndicator)
	{
		this.onServiceLocationIndicator = onServiceLocationIndicator;
	}

	public String getAbClinic()
	{
		return abClinic;
	}

	public void setAbClinic(String abClinic)
	{
		this.abClinic = abClinic;
	}

	public String getAbSourceCode()
	{
		return abSourceCode;
	}

	public void setAbSourceCode(String abSourceCode)
	{
		this.abSourceCode = abSourceCode;
	}

	public String getAbSkillCode()
	{
		return abSkillCode;
	}

	public void setAbSkillCode(String abSkillCode)
	{
		this.abSkillCode = abSkillCode;
	}

	public String getAbLocationCode()
	{
		return abLocationCode;
	}

	public void setAbLocationCode(String abLocationCode)
	{
		this.abLocationCode = abLocationCode;
	}

	public String getAbBANumber()
	{
		return abBANumber;
	}

	public void setAbBANumber(String abBANumber)
	{
		this.abBANumber = abBANumber;
	}

	public String getAbFacilityNumber()
	{
		return abFacilityNumber;
	}

	public void setAbFacilityNumber(String abFacilityNumber)
	{
		this.abFacilityNumber = abFacilityNumber;
	}

	public String getAbFunctionalCenter()
	{
		return abFunctionalCenter;
	}

	public void setAbFunctionalCenter(String abFunctionalCenter)
	{
		this.abFunctionalCenter = abFunctionalCenter;
	}

	public String getAbRoleModifier()
	{
		return abRoleModifier;
	}

	public void setAbRoleModifier(String abRoleModifier)
	{
		this.abRoleModifier = abRoleModifier;
	}

	public String getSkMode()
	{
		return skMode;
	}

	public void setSkMode(String skMode)
	{
		this.skMode = skMode;
	}

	public String getSkLocationCode()
	{
		return skLocationCode;
	}

	public void setSkLocationCode(String skLocationCode)
	{
		this.skLocationCode = skLocationCode;
	}

	public String getSkSubmissionType()
	{
		return skSubmissionType;
	}

	public void setSkSubmissionType(String skSubmissionType)
	{
		this.skSubmissionType = skSubmissionType;
	}

	public String getSkCorporationIndicator()
	{
		return skCorporationIndicator;
	}

	public void setSkCorporationIndicator(String skCorporationIndicator)
	{
		this.skCorporationIndicator = skCorporationIndicator;
	}

	public String getOhipNo()
	{
		return ohipNo;
	}

	public void setOhipNo(String ohipNo)
	{
		this.ohipNo = ohipNo;
	}

	public String getThirdPartyBillingNo()
	{
		return thirdPartyBillingNo;
	}

	public void setThirdPartyBillingNo(String thirdPartyBillingNo)
	{
		this.thirdPartyBillingNo = thirdPartyBillingNo;
	}

	public String getAlternateBillingNo()
	{
		return alternateBillingNo;
	}

	public void setAlternateBillingNo(String alternateBillingNo)
	{
		this.alternateBillingNo = alternateBillingNo;
	}

	public String getCpsid()
	{
		return cpsid;
	}

	public void setCpsid(String cpsid)
	{
		this.cpsid = cpsid;
	}

	public String getIhaProviderMnemonic()
	{
		return ihaProviderMnemonic;
	}

	public void setIhaProviderMnemonic(String ihaProviderMnemonic)
	{
		this.ihaProviderMnemonic = ihaProviderMnemonic;
	}

	public String getConnectCareId()
	{
		return connectCareId;
	}

	public void setConnectCareId(String connectCareId)
	{
		this.connectCareId = connectCareId;
	}

	public String getTakNumber()
	{
		return takNumber;
	}

	public void setTakNumber(String takNumber)
	{
		this.takNumber = takNumber;
	}

	public String getLifeLabsClientIds()
	{
		return lifeLabsClientIds;
	}

	public void setLifeLabsClientIds(String lifeLabsClientIds)
	{
		this.lifeLabsClientIds = lifeLabsClientIds;
	}

	public String geteDeliveryIds()
	{
		return eDeliveryIds;
	}

	public void seteDeliveryIds(String eDeliveryIds)
	{
		this.eDeliveryIds = eDeliveryIds;
	}
}
