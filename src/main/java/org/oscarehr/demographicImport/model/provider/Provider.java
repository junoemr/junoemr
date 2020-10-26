package org.oscarehr.demographicImport.model.provider;

import lombok.Data;
import org.oscarehr.demographicImport.model.AbstractTransientModel;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.PhoneNumber;

import java.time.LocalDate;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Provider extends AbstractTransientModel
{
	private String id;
	// basic info
	private String firstName;
	private String lastName;
	private String providerType;

	private String sex;
	private LocalDate dateOfBirth;
	private String title;

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

	public void addAddress(Address address)
	{
		if(this.addressList == null)
		{
			this.addressList = new ArrayList<>();
		}
		this.addressList.add(address);
	}
}
