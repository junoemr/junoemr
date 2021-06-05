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

package org.oscarehr.integration.myhealthaccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.springframework.beans.BeanUtils;
import oscar.util.Jackson.LocalDateSerializer;

import java.time.LocalDate;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientTo1
{
	private String id;
	private String email;
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("middle_name")
	private String middleName;
	@JsonProperty("last_name")
	private String lastName;
	@JsonProperty("birth_date")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate birthDate;
	private String sex;
	@JsonProperty("health_number")
	private String healthNumber;
	@JsonProperty("health_number_version")
	private String healthNumberVersion;
	@JsonProperty("health_care_province_code")
	private String healthCareProvinceCode;
	@JsonProperty("address_1")
	private String address1;
	@JsonProperty("address_2")
	private String address2;
	private String city;
	@JsonProperty("address_province_code")
	private String addressProvinceCode;
	@JsonProperty("postal_code")
	private String postalCode;
	@JsonProperty("country_code")
	private String countryCode;
	@JsonProperty("home_phone")
	private String homePhone;
	@JsonProperty("cell_phone")
	private String cellPhone;
	@JsonProperty("work_phone")
	private String workPhone;
	@JsonProperty("primary_fax")
	private String primaryFax;
	@JsonProperty("link_status")
	private String linkStatus;
	@JsonProperty("can_message_clinic")
	@Getter
	@Setter
	private boolean canMessage;
	@JsonProperty("local_id")
	@Getter
	@Setter
	private String demographicNo;

	public PatientTo1()
	{
	}

	public PatientTo1(MHAPatient mhaPatient)
	{
		BeanUtils.copyProperties(mhaPatient, this, "addressProvinceCode", "healthCareProvinceCode", "linkStatus", "demographicNo");
		this.healthCareProvinceCode = mhaPatient.getHealthCareProvinceCode().name();
		this.addressProvinceCode = mhaPatient.getAddressProvinceCode().name();
		this.linkStatus = mhaPatient.getLinkStatus().name();
		this.demographicNo = mhaPatient.getDemographicNo().orElse(null);
	}

	public PatientTo1(Demographic demographic, String cellPhone)
	{
		this.id = null; // This should be the MHA id
		this.email = demographic.getEmail();
		this.firstName = demographic.getFirstName();
		this.middleName = null;
		this.lastName = demographic.getLastName();
		this.birthDate = demographic.getDateOfBirth();
		this.sex = demographic.getSex();
		this.healthNumber = demographic.getHin();
		this.healthNumberVersion = demographic.getVer();
		this.healthCareProvinceCode = demographic.getHcType();
		this.address1 = demographic.getAddress();
		this.address2 = null;
		this.city = demographic.getCity();
		this.addressProvinceCode = demographic.getProvince();
		this.postalCode = demographic.getPostal();
		this.countryCode = demographic.getCountryOfOrigin();
		this.homePhone = demographic.getPhone();
		this.workPhone = demographic.getPhone2();
		this.cellPhone = cellPhone;
		this.primaryFax = null;
		this.linkStatus = MHAPatient.LINK_STATUS.NO_LINK.name();
		this.demographicNo = demographic.getId().toString();
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

	public LocalDate getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate)
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

	public String getHealthCareProvinceCode()
	{
		return healthCareProvinceCode;
	}

	public void setHealthCareProvinceCode(String healthCareProvinceCode)
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

	public String getAddressProvinceCode()
	{
		return addressProvinceCode;
	}

	public void setAddressProvinceCode(String addressProvinceCode)
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

	public String getLinkStatus()
	{
		return linkStatus;
	}

	public void setLinkStatus(String link_status)
	{
		this.linkStatus = link_status;
	}
}
