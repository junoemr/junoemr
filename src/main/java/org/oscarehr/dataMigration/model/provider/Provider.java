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
package org.oscarehr.dataMigration.model.provider;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.contact.Contact;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Provider extends AbstractTransientModel implements Person, Contact
{
	private String id;
	// basic info
	private String firstName;
	private String lastName;
	private String providerType;

	private SEX sex;
	private LocalDate dateOfBirth;
	private TITLE title;

	// contact info
	private List<AddressModel> addressList;
	private String email;
	private PhoneNumberModel homePhone;
	private PhoneNumberModel workPhone;
	private PhoneNumberModel cellPhone;

	// professional info
	private String specialty;
	private String team;
	private String ohipNumber;
	private String rmaNumber;
	private String billingNumber;
	private String hsoNumber;
	private String practitionerNumber;
	private String jobTitle;

	// other info
	private String status;
	private String lastUpdateUserId;
	private LocalDateTime lastUpdateDateTime;
	private LocalDateTime signedConfidentialityDateTime;
	private Provider supervisor;

	public Provider()
	{
		this.addressList = new ArrayList<>();
	}

	public void addAddress(AddressModel address)
	{
		if(this.addressList == null)
		{
			this.addressList = new ArrayList<>();
		}
		this.addressList.add(address);
	}

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

	public void setAddress(AddressModel address)
	{
		this.addAddress(address);
	}

	@Override
	public TYPE getContactType()
	{
		return TYPE.PROVIDER;
	}

	@Override
	public String getIdString()
	{
		return getId();
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
}
