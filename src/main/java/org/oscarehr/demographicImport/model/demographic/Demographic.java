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
package org.oscarehr.demographicImport.model.demographic;

import lombok.Data;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.demographicImport.model.AbstractTransientModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Demographic extends AbstractTransientModel
{
	private Integer demographicId;

	// base info
	private String firstName;
	private String lastName;
	private String title;
	private LocalDate dateOfBirth;
	private String sex;
	private String healthNumber;
	private String healthNumberVersion;
	private String healthNumberProvinceCode;
	private LocalDate healthNumberEffectiveDate;
	private LocalDate healthNumberRenewDate;
	private String chartNumber;
	private String sin;
	private String patientStatus;
	private LocalDateTime patientStatusDateTime;
	private LocalDate dateJoined;
	private LocalDate dateEnded;

	//contact info
	private List<Address> addressList;
	private String email;
	private String homePhone;
	private String workPhone;
	private String cellPhone;

	// physician info
	private String mrpProviderId;
	private String referralDoctorId;
	private String familyDoctorId;

	// roster info
	private String rosterStatus;
	private LocalDate rosterDate;
	private LocalDate rosterTerminationDate;
	private String rosterTerminationReason;

	// other info
	private String lastUpdateProviderId;
	private LocalDateTime lastUpdateDateTime;

	private String alias;
	private String citizenship;
	private String spokenLanguage;
	private String officialLanguage;
	private String countryOfOrigin;
	private String newsletter;
	private String nameOfMother;
	private String nameOfFather;
	private String veteranNumber;

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
