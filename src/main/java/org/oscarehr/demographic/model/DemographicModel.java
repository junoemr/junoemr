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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.contact.Contact;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographic.entity.ElectronicMessagingConsentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DemographicModel extends AbstractTransientModel implements Person, Contact
{
	public enum OFFICIAL_LANGUAGE
	{
		ENGLISH("English"),
		FRENCH("French"),
		OTHER("Other");

		private final String value;

		OFFICIAL_LANGUAGE(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static OFFICIAL_LANGUAGE fromValueString(String value)
		{
			for(OFFICIAL_LANGUAGE language : OFFICIAL_LANGUAGE.values())
			{
				if(language.getValue().equalsIgnoreCase(value))
				{
					return language;
				}
			}
			return null;
		}
	}

	private Integer id;

	// base info
	private String firstName;
	private String lastName;
	private TITLE title;
	private LocalDate dateOfBirth;
	private SEX sex;
	private String healthNumber;
	private String healthNumberVersion;
	private String healthNumberProvinceCode;
	private String healthNumberCountryCode;
	private LocalDate healthNumberEffectiveDate;
	private LocalDate healthNumberRenewDate;
	private String chartNumber;
	private String sin;
	private String patientStatus;
	private LocalDate patientStatusDate;
	private LocalDate dateJoined;
	private LocalDate dateEnded;

	//contact info
	private List<AddressModel> addressList;
	private String email;
	private PhoneNumberModel homePhone;
	private PhoneNumberModel workPhone;
	private PhoneNumberModel cellPhone;
	private String phoneComment;

	// physician info
	private ProviderModel mrpProvider;
	private ProviderModel nurseProvider;
	private ProviderModel midwifeProvider;
	private ProviderModel residentProvider;
	private ProviderModel referralDoctor;
	private ProviderModel familyDoctor;

	// roster info
	private List<RosterData> rosterHistory;

	// other info
	private String lastUpdateProviderId;
	private LocalDateTime lastUpdateDateTime;

	private String alias;
	private String citizenship;
	private String spokenLanguage;
	private OFFICIAL_LANGUAGE officialLanguage;
	private String countryOfOrigin;
	private String newsletter;
	private String nameOfMother;
	private String nameOfFather;
	private String veteranNumber;
	private String patientNote;
	private String patientAlert;

	private Boolean aboriginal;
	private String cytolNum;
	private Boolean paperChartArchived;
	private LocalDate paperChartArchivedDate;
	private String usSigned;
	private String privacyConsent;
	private String informedConsent;
	private String securityQuestion1;
	private String securityAnswer1;
	private String rxInteractionWarningLevel;

	private ElectronicMessagingConsentStatus electronicMessagingConsentStatus;
	private LocalDate electronicMessagingConsentGivenAt;
	private LocalDate electronicMessagingConsentRejectedAt;

	public DemographicModel()
	{
		this.addressList = new ArrayList<>();
		this.rosterHistory = new ArrayList<>();
	}

	public void addAddress(AddressModel address)
	{
		this.addressList.add(address);
	}

	@Override
	@JsonIgnore
	public String getIdString()
	{
		return String.valueOf(getId());
	}

	@Override
	@JsonIgnore
	public String getTitleString()
	{
		if(this.title != null)
		{
			return this.title.name();
		}
		return null;
	}

	@Override
	@JsonIgnore
	public String getSexString()
	{
		if(this.sex != null)
		{
			return this.sex.getValue();
		}
		return null;
	}

	@JsonIgnore
	public AddressModel getAddress()
	{
		if(this.addressList != null && !this.addressList.isEmpty())
		{
			for(AddressModel address : addressList)
			{
				// return the first current address found
				if(address.isCurrentAddress())
				{
					return address;
				}
			}
		}
		return null;
	}

	@JsonIgnore
	public RosterData getCurrentRosterData()
	{
		if(this.rosterHistory != null && !this.rosterHistory.isEmpty())
		{
			//the last record is the most recent
			return rosterHistory.get(rosterHistory.size() - 1);
		}
		return null;
	}
	
	public void setAddress(AddressModel address)
	{
		this.addAddress(address);
	}

	@Override
	public TYPE getContactType()
	{
		return TYPE.DEMOGRAPHIC;
	}

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
