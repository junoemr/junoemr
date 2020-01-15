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
package org.oscarehr.integration.iceFall.service.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import oscar.util.Jackson.LocalDateSerializer;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallCreatePrescriptionTo1
{
	@JsonProperty("customer")
	private Integer customerId;
	@JsonProperty("dosagedaily")
	private Float dosage;
	@JsonProperty("regexpiry")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate registrationExpiry;
	private String type;
	@JsonProperty("thclimit")
	private Float thcLimit;
	private String diagnosis;
	@JsonProperty("clinic")
	private Integer clinicId;
	@JsonProperty("document")
	private String documentData;
	private Integer pages;
	@JsonProperty("doctor")
	private Integer doctorId;

	public Integer getCustomerId()
	{
		return customerId;
	}

	public void setCustomerId(Integer customerId)
	{
		this.customerId = customerId;
	}

	public Float getDosage()
	{
		return dosage;
	}

	public void setDosage(Float dosage)
	{
		this.dosage = dosage;
	}

	public LocalDate getRegistrationExpiry()
	{
		return registrationExpiry;
	}

	public void setRegistrationExpiry(LocalDate registrationExpiry)
	{
		this.registrationExpiry = registrationExpiry;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Float getThcLimit()
	{
		return thcLimit;
	}

	public void setThcLimit(Float thcLimit)
	{
		this.thcLimit = thcLimit;
	}

	public String getDiagnosis()
	{
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis)
	{
		this.diagnosis = diagnosis;
	}

	public Integer getClinicId()
	{
		return clinicId;
	}

	public void setClinicId(Integer clinicId)
	{
		this.clinicId = clinicId;
	}

	public String getDocumentData()
	{
		return documentData;
	}

	public void setDocumentData(String documentData)
	{
		this.documentData = documentData;
	}

	public Integer getPages()
	{
		return pages;
	}

	public void setPages(Integer pages)
	{
		this.pages = pages;
	}

	public Integer getDoctorId()
	{
		return doctorId;
	}

	public void setDoctorId(Integer doctorId)
	{
		this.doctorId = doctorId;
	}
}
