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
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.Security;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.providerBilling.model.ProviderBilling;
import oscar.SxmlMisc;
import oscar.util.ConversionUtils;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ProviderEditFormTo1 implements Serializable
{
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
	@Getter
	@Setter
	private String bookingNotificationNumbers;
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
	private String onCnoNumber;
	private String ihaProviderMnemonic;
	private String connectCareProviderId;
	private String takNumber;
	private String lifeLabsClientId;
	private String eDeliveryIds;
	private String imdHealthUuid;

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
		this.setBookingNotificationNumbers(providerData.getBookingNotificationNumbers());
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
		this.setOnCnoNumber(providerData.getOntarioCnoNumber());
		this.setEDeliveryIds(providerData.getAlbertaEDeliveryIds());
		this.setTakNumber(providerData.getAlbertaTakNo());
		this.setConnectCareProviderId(providerData.getAlbertaConnectCareId());
		this.setCpsid(providerData.getPractitionerNo());
		this.setLifeLabsClientId(providerData.getOntarioLifeLabsId());

		if (providerData.getImdHealthUuid() != null)
		{
			this.setImdHealthUuid(providerData.getImdHealthUuid());
		}
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
		providerData.setBookingNotificationNumbers(this.getBookingNotificationNumbers());
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
		if (this.getEDeliveryIds() != null && !this.getEDeliveryIds().isEmpty())
		{
			providerData.setAlbertaEDeliveryIds(this.getEDeliveryIds());
		}
		providerData.setAlbertaTakNo(this.getTakNumber());
		providerData.setAlbertaConnectCareId(this.getConnectCareProviderId());
		providerData.setPractitionerNo(this.getCpsid());
		providerData.setOntarioLifeLabsId(this.getLifeLabsClientId());
		providerData.setOntarioCnoNumber(this.getOnCnoNumber());
		providerData.setImdHealthUuid(this.getImdHealthUuid());

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
			newSecurityRecord.setEmail((securityRecordTo1.getEmail() == null || securityRecordTo1.getEmail().isEmpty()) ?
					null: securityRecordTo1.getEmail());
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
}
