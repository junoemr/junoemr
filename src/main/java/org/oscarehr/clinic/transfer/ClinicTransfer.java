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

package org.oscarehr.clinic.transfer;

import org.oscarehr.common.model.Clinic;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

public class ClinicTransfer implements Serializable
{
	private String clinicName;
	private String clinicAddress;
	private String clinicCity;
	private String clinicPostal;
	private String clinicPhone;
	private String clinicFax;
	private String clinicLocationCode;
	private String status;
	private String clinicProvince;
	private String clinicDelimPhone;
	private String clinicDelimFax;
	private String clinicEmail;
	private String albertaConnectCareLabId;
	private String albertaConnectCareDepartmentId;
	private String bcFacilityNumber;

	public static ClinicTransfer toTransferObj(Clinic clinic)
	{
		ClinicTransfer transfer = new ClinicTransfer();
		BeanUtils.copyProperties(clinic, transfer);

		return transfer;
	}

	public String getClinicName()
	{
		return clinicName;
	}

	public void setClinicName(String clinicName)
	{
		this.clinicName = clinicName;
	}

	public String getClinicAddress()
	{
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress)
	{
		this.clinicAddress = clinicAddress;
	}

	public String getClinicCity()
	{
		return clinicCity;
	}

	public void setClinicCity(String clinicCity)
	{
		this.clinicCity = clinicCity;
	}

	public String getClinicPostal()
	{
		return clinicPostal;
	}

	public void setClinicPostal(String clinicPostal)
	{
		this.clinicPostal = clinicPostal;
	}

	public String getClinicPhone()
	{
		return clinicPhone;
	}

	public void setClinicPhone(String clinicPhone)
	{
		this.clinicPhone = clinicPhone;
	}

	public String getClinicFax()
	{
		return clinicFax;
	}

	public void setClinicFax(String clinicFax)
	{
		this.clinicFax = clinicFax;
	}

	public String getClinicLocationCode()
	{
		return clinicLocationCode;
	}

	public void setClinicLocationCode(String clinicLocationCode)
	{
		this.clinicLocationCode = clinicLocationCode;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getClinicProvince()
	{
		return clinicProvince;
	}

	public void setClinicProvince(String clinicProvince)
	{
		this.clinicProvince = clinicProvince;
	}

	public String getClinicDelimPhone()
	{
		return clinicDelimPhone;
	}

	public void setClinicDelimPhone(String clinicDelimPhone)
	{
		this.clinicDelimPhone = clinicDelimPhone;
	}

	public String getClinicDelimFax()
	{
		return clinicDelimFax;
	}

	public void setClinicDelimFax(String clinicDelimFax)
	{
		this.clinicDelimFax = clinicDelimFax;
	}

	public String getClinicEmail()
	{
		return clinicEmail;
	}

	public void setClinicEmail(String clinicEmail)
	{
		this.clinicEmail = clinicEmail;
	}

	public String getAlbertaConnectCareLabId()
	{
		return albertaConnectCareLabId;
	}

	public void setAlbertaConnectCareLabId(String albertaConnectCareLabId)
	{
		this.albertaConnectCareLabId = albertaConnectCareLabId;
	}

	public String getAlbertaConnectCareDepartmentId()
	{
		return albertaConnectCareDepartmentId;
	}

	public void setAlbertaConnectCareDepartmentId(String albertaConnectCareDepartmentId)
	{
		this.albertaConnectCareDepartmentId = albertaConnectCareDepartmentId;
	}

	public String getBcFacilityNumber()
	{
		return bcFacilityNumber;
	}

	public void setBcFacilityNumber(String bcFacilityNumber)
	{
		this.bcFacilityNumber = bcFacilityNumber;
	}
}

