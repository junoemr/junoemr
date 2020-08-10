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
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallPrescriptionTo1 implements Serializable
{
	private Integer id;
	@JsonProperty("dosagedaily")
	private Float dosageDaily;
	@JsonProperty("regexpiry")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate regExpiry;
	@JsonProperty("regdate")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate regDate;
	@JsonProperty("isactive")
	private Boolean active;
	@JsonProperty("iscancel")
	private Boolean canceled;
	@JsonProperty("isrefused")
	private Boolean refused;
	@JsonProperty("isoilonly")
	private Boolean oilOnly;
	@JsonProperty("isbudonly")
	private Boolean budOnly;
	private String notes;
	@JsonProperty("certdate")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate certDate;
	private String diagnosis;
	@JsonProperty("dtcreated")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate createDate;
	@JsonProperty("dtupdated")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate updateDate;
	@JsonProperty("thclimit")
	private Integer thcLimit;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Float getDosageDaily()
	{
		return dosageDaily;
	}

	public void setDosageDaily(Float dosageDaily)
	{
		this.dosageDaily = dosageDaily;
	}

	public LocalDate getRegExpiry()
	{
		return regExpiry;
	}

	public void setRegExpiry(LocalDate regExpiry)
	{
		this.regExpiry = regExpiry;
	}

	public LocalDate getRegDate()
	{
		return regDate;
	}

	public void setRegDate(LocalDate regDate)
	{
		this.regDate = regDate;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public Boolean getCanceled()
	{
		return canceled;
	}

	public void setCanceled(Boolean canceled)
	{
		this.canceled = canceled;
	}

	public Boolean getRefused()
	{
		return refused;
	}

	public void setRefused(Boolean refused)
	{
		this.refused = refused;
	}

	public Boolean getOilOnly()
	{
		return oilOnly;
	}

	public void setOilOnly(Boolean oilOnly)
	{
		this.oilOnly = oilOnly;
	}

	public Boolean getBudOnly()
	{
		return budOnly;
	}

	public void setBudOnly(Boolean budOnly)
	{
		this.budOnly = budOnly;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public LocalDate getCertDate()
	{
		return certDate;
	}

	public void setCertDate(LocalDate certDate)
	{
		this.certDate = certDate;
	}

	public String getDiagnosis()
	{
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis)
	{
		this.diagnosis = diagnosis;
	}

	public LocalDate getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(LocalDate createDate)
	{
		this.createDate = createDate;
	}

	public LocalDate getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(LocalDate updateDate)
	{
		this.updateDate = updateDate;
	}

	public Integer getThcLimit()
	{
		return thcLimit;
	}

	public void setThcLimit(Integer thcLimit)
	{
		this.thcLimit = thcLimit;
	}
}
