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

package org.oscarehr.integration.myhealthaccess.model;

import org.oscarehr.integration.myhealthaccess.dto.PatientTo1;
import org.springframework.beans.BeanUtils;
import java.time.ZonedDateTime;

public class MHAPatient
{
	private String id;
	private String email;
	private String firstName;
	private String middleName;
	private String lastName;
	private ZonedDateTime birthDate;
	private String sex;

	private String healthNumber;
	private String healthNumberVersion;
	private PROVINCE_CODES healthCareProvinceCode;

	private String address1;
	private String address2;
	private String city;
	private PROVINCE_CODES addressProvinceCode;
	private String postalCode;
	private String countryCode;

	private String homePhone;
	private String cellPhone;
	private String workPhone;
	private String primaryFax;

	public enum PROVINCE_CODES
	{
		NL,
		PE,
		NS,
		NB,
		QC,
		ON,
		MB,
		SK,
		AB,
		BC,
		YT,
		NT,
		NU
	}

	public MHAPatient()
	{

	}

	public MHAPatient(PatientTo1 patientTo1)
	{
		BeanUtils.copyProperties(patientTo1, this, "addressProvinceCode", "healthCareProvinceCode");
		this.healthCareProvinceCode = PROVINCE_CODES.valueOf(patientTo1.getHealthCareProvinceCode());
		this.addressProvinceCode 		= PROVINCE_CODES.valueOf(patientTo1.getAddressProvinceCode());
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public ZonedDateTime getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(ZonedDateTime birthDate)
	{
		this.birthDate = birthDate;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getHealthNumber()
	{
		return healthNumber;
	}

	public void setHealthNumber(String healthNumber)
	{
		this.healthNumber = healthNumber;
	}

	public String getHealthNumberVersion()
	{
		return healthNumberVersion;
	}

	public void setHealthNumberVersion(String healthNumberVersion)
	{
		this.healthNumberVersion = healthNumberVersion;
	}

	public PROVINCE_CODES getHealthCareProvinceCode()
	{
		return healthCareProvinceCode;
	}

	public void setHealthCareProvinceCode(PROVINCE_CODES healthCareProvinceCode)
	{
		this.healthCareProvinceCode = healthCareProvinceCode;
	}

	public String getAddress1()
	{
		return address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public PROVINCE_CODES getAddressProvinceCode()
	{
		return addressProvinceCode;
	}

	public void setAddressProvinceCode(PROVINCE_CODES addressProvinceCode)
	{
		this.addressProvinceCode = addressProvinceCode;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}

	public String getHomePhone()
	{
		return homePhone;
	}

	public void setHomePhone(String homePhone)
	{
		this.homePhone = homePhone;
	}

	public String getCellPhone()
	{
		return cellPhone;
	}

	public void setCellPhone(String cellPhone)
	{
		this.cellPhone = cellPhone;
	}

	public String getWorkPhone()
	{
		return workPhone;
	}

	public void setWorkPhone(String workPhone)
	{
		this.workPhone = workPhone;
	}

	public String getPrimaryFax()
	{
		return primaryFax;
	}

	public void setPrimaryFax(String primaryFax)
	{
		this.primaryFax = primaryFax;
	}
}
