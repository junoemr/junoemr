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
package org.oscarehr.demographic.transfer;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DemographicCreateInput extends AbstractTransientModel
{
	// base info
	private String firstName;
	private String lastName;
	private Person.TITLE title;
	private LocalDate dateOfBirth;
	private Person.SEX sex;
	private String healthNumber;
	private String healthNumberVersion;
	private String healthNumberProvinceCode;
	private String healthNumberCountryCode;
	private LocalDate healthNumberEffectiveDate;
	private LocalDate healthNumberRenewDate;
	private String chartNumber;

	//contact info
	private List<AddressModel> addressList;
	private String email;
	private PhoneNumberModel homePhone;
	private PhoneNumberModel workPhone;
	private PhoneNumberModel cellPhone;

	// physician info
	private String mrpProviderId;

	// other info
	private String alias;

	public DemographicCreateInput()
	{
		this.addressList = new ArrayList<>();
	}
}
