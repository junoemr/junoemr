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
package org.oscarehr.ws.rest.transfer.providerManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.oscarehr.common.model.Security;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.providerBilling.model.ProviderBilling;
import org.oscarehr.util.SpringUtils;
import oscar.SxmlMisc;
import oscar.util.ConversionUtils;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderEditFormTo1 implements Serializable
{

	private ProviderDataDao providerDataDao = SpringUtils.getBean(ProviderDataDao.class);

	// user info
	private String firstName;
	private String lastName;
	private String type;
	private String speciality;
	private String team;
	private String sex;
	private LocalDate dateOfBirth;
	private String status;

	// login info
	private List<SecurityRecordTo1> securityRecords;
	private Integer currentSecurityRecord;
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
	private List<Integer> userRoles;

	// site assignments
	private List<Integer> siteAssignments;

	// BC billing
	private String bcBillingNo;
	private JunoTypeaheadTo1 bcRuralRetentionCode;
	private String bcServiceLocation;
	private List<Integer> bcpSites;

	// ON billing
	private String onGroupNumber;
	private String onSpecialityCode;
	private String onVisitLocation;
	private String onServiceLocationIndicator;

	// AB billing
	private String abSourceCode;
	private String abSkillCode;
	private String abLocationCode;
	private Integer abBANumber;
	private Integer abFacilityNumber;
	private String abFunctionalCenter;
	private String abRoleModifier;

	// SK billing
	private Integer skMode;
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
	private String connectCareProviderId;
	private String takNumber;
	private String lifeLabsClientId;
	private String eDeliveryIds;

	/**
	 * initialize this object using the provided provider data.
	 * @param providerData - the provider data
	 */
	@JsonIgnore
	public void setProviderData(ProviderData providerData)
	{
		// general
		this.setLastName(providerData.getLastName());
		this.setFirstName(providerData.getFirstName());
		this.setType(providerData.getProviderType());
		this.setSpeciality(providerData.getSpecialty());
		this.setTeam(providerData.getTeam());
		this.setSex(providerData.getSex());
		this.setDateOfBirth(ConversionUtils.toNullableLocalDate(providerData.getDob()));
		this.setStatus(providerData.getStatus());

		this.setAddress(providerData.getAddress());
		this.setHomePhone(providerData.getPhone());
		this.setWorkPhone(providerData.getWorkPhone());
		this.setContactEmail(providerData.getEmail());
		this.setFax(SxmlMisc.getXmlContent(providerData.getComments(), ProviderData.COMMENT_FAX_TAG));
		this.setCellPhone(SxmlMisc.getXmlContent(providerData.getComments(), ProviderData.COMMENT_CELL_TAG));
		this.setPagerNumber(SxmlMisc.getXmlContent(providerData.getComments(), ProviderData.COMMENT_PAGER_TAG));
		this.setOtherPhone(SxmlMisc.getXmlContent(providerData.getComments(), ProviderData.COMMENT_OTHER_PHONE_TAG));
		this.setOnGroupNumber(SxmlMisc.getXmlContent(providerData.getComments(), ProviderData.COMMENT_ON_BILLING_GROUP_NO));
		this.setOnSpecialityCode(SxmlMisc.getXmlContent(providerData.getComments(), ProviderData.COMMENT_ON_SPECIALITY_CODE));

		// fill out provider billing (old fields. new ones in setProviderBilling())
		this.setOhipNo(providerData.getOhipNo());
		this.setThirdPartyBillingNo(providerData.getRmaNo());
		this.setAlternateBillingNo(providerData.getHsoNo());

		// bc
		this.setBcBillingNo(providerData.getBillingNo());

		// 3rd party identifiers
		this.setIhaProviderMnemonic(providerData.getAlbertaEDeliveryIds());
		this.seteDeliveryIds(providerData.getAlbertaEDeliveryIds());
		this.setTakNumber(providerData.getAlbertaTakNo());
		this.setConnectCareProviderId(providerData.getAlbertaConnectCareId());
		this.setCpsid(providerData.getPractitionerNo());
		this.setLifeLabsClientId(providerData.getOntarioLifeLabsId());
	}

	/**
	 * extract the data in this transfer object in to a provider object for persisting to the database
	 * @return - a providerData object filled out with data contained in this object.
	 */
	@JsonIgnore
	public ProviderData getProviderData()
	{
		ProviderData providerData = new ProviderData();

		// fill out general provider record
		providerData.setLastName(this.getLastName());
		providerData.setFirstName(this.getFirstName());
		providerData.setProviderType(this.getType());
		providerData.setSpecialty(this.getSpeciality());
		providerData.setTeam(this.getTeam());
		providerData.setSex(this.getSex());
		providerData.setDob(this.getDateOfBirth() != null ? ConversionUtils.toLegacyDate(this.getDateOfBirth()) : null);
		providerData.setStatus(this.getStatus());

		providerData.setAddress(this.getAddress());
		providerData.setPhone(this.getHomePhone());
		providerData.setWorkPhone(this.getWorkPhone());
		providerData.setEmail(this.getContactEmail());

		// set provider extended settings. yes it is an xml string shoved in to the comments column *face palm*
		String providerXmlSettingsString = "";
		providerXmlSettingsString = SxmlMisc.addElement(providerXmlSettingsString, ProviderData.COMMENT_FAX_TAG, this.getFax());
		providerXmlSettingsString = SxmlMisc.addElement(providerXmlSettingsString, ProviderData.COMMENT_CELL_TAG, this.getCellPhone());
		providerXmlSettingsString = SxmlMisc.addElement(providerXmlSettingsString, ProviderData.COMMENT_PAGER_TAG, this.getPagerNumber());
		providerXmlSettingsString = SxmlMisc.addElement(providerXmlSettingsString, ProviderData.COMMENT_OTHER_PHONE_TAG, this.getOtherPhone());
		providerXmlSettingsString = SxmlMisc.addElement(providerXmlSettingsString, ProviderData.COMMENT_ON_SPECIALITY_CODE, this.getOnSpecialityCode());
		providerXmlSettingsString = SxmlMisc.addElement(providerXmlSettingsString, ProviderData.COMMENT_ON_BILLING_GROUP_NO, this.getOnGroupNumber());
		providerData.setComments(providerXmlSettingsString);

		// fill out provider billing
		providerData.setOhipNo(this.getOhipNo());
		providerData.setRmaNo(this.getThirdPartyBillingNo());
		providerData.setHsoNo(this.getAlternateBillingNo());

		// bc
		providerData.setBillingNo(this.getBcBillingNo());

		// 3rd party identifiers
		if (this.getIhaProviderMnemonic() != null && !this.getIhaProviderMnemonic().isEmpty())
		{// yes both IHA and alberta e-delivery ids use the same column.
			providerData.setAlbertaEDeliveryIds(this.getIhaProviderMnemonic());
		}
		if (this.geteDeliveryIds() != null && !this.geteDeliveryIds().isEmpty())
		{
			providerData.setAlbertaEDeliveryIds(this.geteDeliveryIds());
		}
		providerData.setAlbertaTakNo(this.getTakNumber());
		providerData.setAlbertaConnectCareId(this.getConnectCareProviderId());
		providerData.setPractitionerNo(this.getCpsid());
		providerData.setOntarioLifeLabsId(this.getLifeLabsClientId());

		return providerData;
	}

	/**
	 * set fields in this form based on the providerBilling. this only does new billing fields. old ones should be migrated.
	 * @param providerBilling - data to use to populate the form.
	 */
	@JsonIgnore
	public void setProviderBilling(ProviderBilling providerBilling)
	{
		// BC
		JunoTypeaheadTo1 ruralRetentionCode = new JunoTypeaheadTo1();
		ruralRetentionCode.setLabel(providerBilling.getBcRuralRetentionName());
		ruralRetentionCode.setValue(providerBilling.getBcRuralRetentionCode());
		this.setBcRuralRetentionCode(ruralRetentionCode);
		this.setBcServiceLocation(providerBilling.getBcServiceLocationCode());

		// ON
		this.setOnVisitLocation(providerBilling.getOnMasterNumber());
		this.setOnServiceLocationIndicator(providerBilling.getOnServiceLocation());

		// AB
		this.setAbSourceCode(providerBilling.getAbSourceCode());
		this.setAbSkillCode(providerBilling.getAbSkillCode());
		this.setAbLocationCode(providerBilling.getAbLocationCode());
		this.setAbBANumber(providerBilling.getAbBANumber());
		this.setAbFacilityNumber(providerBilling.getAbFacilityNumber());
		this.setAbFunctionalCenter(providerBilling.getAbFunctionalCenter());
		this.setAbRoleModifier(providerBilling.getAbTimeRoleModifier());

		// SK
		this.setSkMode(providerBilling.getSkMode());
		this.setSkLocationCode(providerBilling.getSkLocation());
		this.setSkSubmissionType(providerBilling.getSkSubmissionType());
		this.setSkCorporationIndicator(providerBilling.getSkCorporationIndicator());
	}

	/**
	 * get provider billing fields. only covers new fields. old fields should be migrated here.
	 * @return - provider billing data.
	 */
	@JsonIgnore
	public ProviderBilling getProviderBilling()
	{
		ProviderBilling providerBilling = new ProviderBilling();

		// BC
		if (this.getBcRuralRetentionCode() != null)
		{
			providerBilling.setBcRuralRetentionCode(this.getBcRuralRetentionCode().getValue());
			providerBilling.setBcRuralRetentionName(this.getBcRuralRetentionCode().getLabel());
		}
		providerBilling.setBcServiceLocationCode(this.getBcServiceLocation());

		// ON
		providerBilling.setOnMasterNumber(this.getOnVisitLocation());
		providerBilling.setOnServiceLocation(this.getOnServiceLocationIndicator());

		// AB
		providerBilling.setAbSourceCode(this.getAbSourceCode());
		providerBilling.setAbSkillCode(this.getAbSkillCode());
		providerBilling.setAbLocationCode(this.getAbLocationCode());
		providerBilling.setAbBANumber(this.getAbBANumber());
		providerBilling.setAbFacilityNumber(this.getAbFacilityNumber());
		providerBilling.setAbFunctionalCenter(this.getAbFunctionalCenter());
		providerBilling.setAbTimeRoleModifier(this.getAbRoleModifier());

		// SK
		providerBilling.setSkMode(this.getSkMode());
		providerBilling.setSkLocation(this.getSkLocationCode());
		providerBilling.setSkSubmissionType(this.getSkSubmissionType());
		providerBilling.setSkCorporationIndicator(this.getSkCorporationIndicator());

		return providerBilling;
	}

	/**
	 * get security records. at least one of email or user_name or both security records.
	 * @param providerNo - provider to create the records for
	 * @return - a list of security records
	 */
	@JsonIgnore
	public List<Security> getSecurityRecords(Integer providerNo, boolean edit) throws NoSuchAlgorithmException
	{
		ArrayList<Security> newSecurityRecords = new ArrayList<>();

		for (SecurityRecordTo1 securityRecordTo1 : this.getSecurityRecords())
		{
			Security newSecurityRecord = new Security();
			if (edit)
			{
				newSecurityRecord.setSecurityNo(securityRecordTo1.getSecurityNo());
			}
			newSecurityRecord.setUserName((securityRecordTo1.getUserName() == null || securityRecordTo1.getUserName().isEmpty()) ?
					null : securityRecordTo1.getUserName());
			newSecurityRecord.setEmail(securityRecordTo1.getEmail());
			setSecurityRecordCommonFields(newSecurityRecord, securityRecordTo1, providerNo);
			newSecurityRecords.add(newSecurityRecord);
		}

		return newSecurityRecords;
	}

	/**
	 * helper to reduce duplicate code in getSecurityRecords.
	 * @param security - security record to set common fields on
	 * @param providerNo - the provider to assign the record to.
	 */
	@JsonIgnore
	private void setSecurityRecordCommonFields(Security security, SecurityRecordTo1 securityRecordTo1, Integer providerNo) throws NoSuchAlgorithmException
	{
		if (securityRecordTo1.getPassword() != null && !securityRecordTo1.getPassword().isEmpty())
		{
			// hash password
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] btNewPasswd = md.digest(securityRecordTo1.getPassword().getBytes());
			StringBuilder sbTemp = new StringBuilder();
			for (int i = 0; i < btNewPasswd.length; i++)
			{
				sbTemp.append(btNewPasswd[i]);
			}
			security.setPassword(sbTemp.toString());
		}

		if (securityRecordTo1.getPin() != null && !securityRecordTo1.getPin().isEmpty())
		{
			security.setPin(securityRecordTo1.getPin());
		}
		security.setProviderNo(providerNo.toString());
		security.setBExpireset(0);
		security.setBLocallockset(1);
		security.setBRemotelockset(1);
		security.setForcePasswordReset(false);
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

	public List<Integer> getUserRoles()
	{
		return userRoles;
	}

	public void setUserRoles(List<Integer> userRoles)
	{
		this.userRoles = userRoles;
	}

	public List<Integer> getSiteAssignments()
	{
		return siteAssignments;
	}

	public void setSiteAssignments(List<Integer> siteAssignments)
	{
		this.siteAssignments = siteAssignments;
	}

	public String getBcBillingNo()
	{
		return bcBillingNo;
	}

	public void setBcBillingNo(String bcBillingNo)
	{
		this.bcBillingNo = bcBillingNo;
	}

	public JunoTypeaheadTo1 getBcRuralRetentionCode()
	{
		return bcRuralRetentionCode;
	}

	public void setBcRuralRetentionCode(JunoTypeaheadTo1 bcRuralRetentionCode)
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

	public Integer getAbBANumber()
	{
		return abBANumber;
	}

	public void setAbBANumber(Integer abBANumber)
	{
		this.abBANumber = abBANumber;
	}

	public Integer getAbFacilityNumber()
	{
		return abFacilityNumber;
	}

	public void setAbFacilityNumber(Integer abFacilityNumber)
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

	public Integer getSkMode()
	{
		return skMode;
	}

	public void setSkMode(Integer skMode)
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

	public String getConnectCareProviderId()
	{
		return connectCareProviderId;
	}

	public void setConnectCareProviderId(String connectCareProviderId)
	{
		this.connectCareProviderId = connectCareProviderId;
	}

	public String getTakNumber()
	{
		return takNumber;
	}

	public void setTakNumber(String takNumber)
	{
		this.takNumber = takNumber;
	}

	public String getLifeLabsClientId()
	{
		return lifeLabsClientId;
	}

	public void setLifeLabsClientId(String lifeLabsClientId)
	{
		this.lifeLabsClientId = lifeLabsClientId;
	}

	public String geteDeliveryIds()
	{
		return eDeliveryIds;
	}

	public void seteDeliveryIds(String eDeliveryIds)
	{
		this.eDeliveryIds = eDeliveryIds;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public List<SecurityRecordTo1> getSecurityRecords()
	{
		return securityRecords;
	}

	public void setSecurityRecords(List<SecurityRecordTo1> securityRecords)
	{
		this.securityRecords = securityRecords;
	}

	public Integer getCurrentSecurityRecord()
	{
		return currentSecurityRecord;
	}

	public void setCurrentSecurityRecord(Integer currentSecurityRecord)
	{
		this.currentSecurityRecord = currentSecurityRecord;
	}

	public List<Integer> getBcpSites()
	{
		return bcpSites;
	}

	public void setBcpSites(List<Integer> bcpSites)
	{
		this.bcpSites = bcpSites;
	}
}
