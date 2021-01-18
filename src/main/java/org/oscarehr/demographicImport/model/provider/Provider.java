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
package org.oscarehr.demographicImport.model.provider;

import lombok.Data;
import org.oscarehr.demographicImport.model.AbstractTransientModel;
import org.oscarehr.demographicImport.model.common.Address;
import org.oscarehr.demographicImport.model.common.Person;
import org.oscarehr.demographicImport.model.common.PhoneNumber;
import org.oscarehr.demographicImport.model.contact.Contact;

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
	private List<Address> addressList;
	private String email;
	private PhoneNumber homePhone;
	private PhoneNumber workPhone;
	private PhoneNumber cellPhone;

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

	public void addAddress(Address address)
	{
		if(this.addressList == null)
		{
			this.addressList = new ArrayList<>();
		}
		this.addressList.add(address);
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

	public void setAddress(Address address)
	{
		this.addAddress(address);
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
