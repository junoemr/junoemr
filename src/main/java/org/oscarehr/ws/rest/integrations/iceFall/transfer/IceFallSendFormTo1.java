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
package org.oscarehr.ws.rest.integrations.iceFall.transfer;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallSendFormTo1 implements Serializable
{
	private Integer fid;
	private Integer fdid;
	private Integer demographicNo;

	// prescription information fields. to be submitted to ice fall.
	private Float dosage;
	private LocalDate expiryDate;
	private String type;
	private Float thcLimit;
	private String diagnosis;

	// any other values sent in the request
	private Map<String, String> eformValues = new HashMap<>();

	// Capture all other fields that Jackson do not match other members
	@JsonAnyGetter
	public Map<String, String> otherFields() {
		return eformValues;
	}

	@JsonAnySetter
	public void setOtherField(String name, String value) {
		eformValues.put(name, value);
	}

	public Integer getFdid()
	{
		return fdid;
	}

	public void setFdid(Integer fdid)
	{
		this.fdid = fdid;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Integer getFid()
	{
		return fid;
	}

	public void setFid(Integer fid)
	{
		this.fid = fid;
	}

	public Map<String, String> getEformValues()
	{
		return eformValues;
	}

	public void setEformValues(Map<String, String> eformValues)
	{
		this.eformValues = eformValues;
	}

	public Float getDosage()
	{
		return dosage;
	}

	public void setDosage(Float dosage)
	{
		this.dosage = dosage;
	}

	public LocalDate getExpiryDate()
	{
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate)
	{
		this.expiryDate = expiryDate;
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
}
