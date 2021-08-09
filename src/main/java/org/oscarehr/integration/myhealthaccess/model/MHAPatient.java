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

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.oscarehr.integration.myhealthaccess.dto.PatientTo1;
import org.springframework.beans.BeanUtils;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.Optional;

@NoArgsConstructor
public class MHAPatient
{
	private String id;
	private String email;
	private String firstName;
	private String middleName;
	private String lastName;
	private LocalDate birthDate;
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

	@Setter
	@Getter
	private Boolean hasVoipToken;
	private LINK_STATUS linkStatus;
	@Getter
	@Setter
	private boolean canMessage;
	@Setter
	private String demographicNo;

	public enum PROVINCE_CODES
	{
		UNKNOWN,
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

	public enum LINK_STATUS
	{
		NO_LINK,
		PATIENT_REJECTED,
		CLINIC_REJECTED,
		PENDING_CLINIC_CONFIRM,
		PENDING_PATIENT_CONFIRM,
		CONFIRMED,
		VERIFIED;

		@JsonCreator
		public static LINK_STATUS fromString(String str)
		{
			// Old MHA server will send status 'active' instead of 'confirmed' or 'verified'
			// Convert the value. TODO Can be removed once MHA master branch includes MHA-2069.
			if (str.equals("active"))
			{
				str = "confirmed";
			}

			return LINK_STATUS.valueOf(str.toUpperCase());
		}
	}

	public static boolean isValidProvinceCode(String provinceCode)
	{
		for (PROVINCE_CODES validProvinceCode : PROVINCE_CODES.values())
		{
			if (validProvinceCode.name().equals(provinceCode))
			{
				return true;
			}
		}

		return false;
	}

	public static PROVINCE_CODES stringToProvinceCode(String provinceCode)
	{
		if (ConversionUtils.hasContent(provinceCode))
		{
			return PROVINCE_CODES.valueOf(provinceCode);
		}
		return PROVINCE_CODES.UNKNOWN;
	}

	public MHAPatient(PatientTo1 patientTo1)
	{
		BeanUtils.copyProperties(patientTo1, this, "addressProvinceCode", "healthCareProvinceCode", "linkStatus");
		this.healthCareProvinceCode = stringToProvinceCode(patientTo1.getHealthCareProvinceCode());
		this.addressProvinceCode = stringToProvinceCode(patientTo1.getAddressProvinceCode());
		this.linkStatus = patientTo1.getLinkStatus();
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

	public LINK_STATUS getLinkStatus()
	{
		return linkStatus;
	}

	public void setLinkStatus(LINK_STATUS linkStatus)
	{
		this.linkStatus = linkStatus;
	}

	public Optional<String> getDemographicNo()
	{
		return Optional.ofNullable(this.demographicNo);
	}
}
