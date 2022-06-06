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
package org.oscarehr.ws.external.rest.v1.transfer.demographic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.oscarehr.ws.validator.DemographicNoConstraint;
import org.oscarehr.ws.validator.ProviderNoConstraint;
import org.oscarehr.ws.validator.StringValueConstraint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;

@XmlRootElement
@Schema(description = "Partial Demographic record data transfer object")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public class ApiDemographicListTransfer implements Serializable
{
	// demographic base info
	@Schema(description = "patient demographic record identifier")
	@DemographicNoConstraint
	private Integer demographicNo;
	@NotNull
	@Size(min=1, max=30)
	@Schema(description = "patient first name")
	private String firstName;
	@NotNull
	@Size(min=1, max=30)
	@Schema(description = "patient last name")
	private String lastName;
	@NotNull
	@Size(min=1,max=1)
	@Schema(description = "patient gender", allowableValues = {"M", "F", "T", "O", "U"})
	@StringValueConstraint(allows = {"M","F","T","O","U"})
	private String sex;
	@Size(max=10)
	@Schema(description = "patient health insurance number")
	private String hin;
	@NotNull
	@Schema(description = "patient date of birth")
	private LocalDate dateOfBirth;
	@DefaultValue("AC")
	@StringValueConstraint(allows = {"AC","IN","DE","FI","MO"})
	@Schema(description = "patient status", example = "AC")
	private String patientStatus;

	// physician info
	@Size(max=6)
	@ProviderNoConstraint(allowNull = true)
	@Schema(description = "patient assigned physician unique identifier. Id must match an existing provider record.")
	private String providerNo;

	// other info
	@Size(max=10)
	@Schema(description = "patient chart number")
	private String chartNo;

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getHin()
	{
		return hin;
	}

	public void setHin(String hin)
	{
		this.hin = hin;
	}

	public LocalDate getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getPatientStatus()
	{
		return patientStatus;
	}

	public void setPatientStatus(String patientStatus)
	{
		this.patientStatus = patientStatus;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getChartNo()
	{
		return chartNo;
	}

	public void setChartNo(String chartNo)
	{
		this.chartNo = chartNo;
	}

	@Override
	public String toString() {
		return (ReflectionToStringBuilder.toString(this));
	}

}
