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
package org.oscarehr.dataMigration.model.demographic;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.contact.Contact;
import org.oscarehr.dataMigration.model.provider.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Demographic extends AbstractTransientModel implements Person, Contact
{
	public enum OFFICIAL_LANGUAGE
	{
		ENGLISH("English"),
		FRENCH("French");

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
	private String middleName;
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
	private List<Address> addressList;
	private String email;
	private PhoneNumber homePhone;
	private PhoneNumber workPhone;
	private PhoneNumber cellPhone;

	// physician info
	private Provider mrpProvider;
	private Provider referralDoctor;
	private Provider familyDoctor;

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

	public Demographic()
	{
		this.addressList = new ArrayList<>();
		this.rosterHistory = new ArrayList<>();
	}

	public void addAddress(Address address)
	{
		this.addressList.add(address);
	}

	@Override
	public String getIdString()
	{
		return String.valueOf(getId());
	}

	@Override
	public String getTitleString()
	{
		if(this.title != null)
		{
			return this.title.name();
		}
		return null;
	}

	@Override
	public String getSexString()
	{
		if(this.sex != null)
		{
			return this.sex.getValue();
		}
		return null;
	}

	public Address getAddress()
	{
		if(this.addressList != null && !this.addressList.isEmpty())
		{
			for(Address address : addressList)
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

	public RosterData getCurrentRosterData()
	{
		if(this.rosterHistory != null && !this.rosterHistory.isEmpty())
		{
			//the last record is the most recent
			return rosterHistory.get(rosterHistory.size() - 1);
		}
		return null;
	}

	public void setAddress(Address address)
	{
		this.addAddress(address);
	}

	@Override
	public TYPE getContactType()
	{
		return TYPE.DEMOGRAPHIC;
	}

	public String getDisplayName()
	{
		return this.getLastName() + ", " + this.getFirstName() + (StringUtils.isBlank(this.getMiddleName()) ? "" : " " + this.getMiddleName());
	}

	public Optional<PhoneNumber> getPreferredPhone()
	{
		PhoneNumber cellPhone = this.getCellPhone();
		if(cellPhone != null && cellPhone.isPrimaryContactNumber())
		{
			return Optional.of(cellPhone);
		}
		PhoneNumber workPhone = this.getWorkPhone();
		if(workPhone != null && workPhone.isPrimaryContactNumber())
		{
			return Optional.of(workPhone);
		}
		return Optional.ofNullable(this.getHomePhone());
	}

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
