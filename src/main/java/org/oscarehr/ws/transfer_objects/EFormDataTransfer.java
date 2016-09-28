/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package org.oscarehr.ws.transfer_objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.oscarehr.common.model.EFormData;
import org.springframework.beans.BeanUtils;

public final class EFormDataTransfer {

	private Integer id;
	private Integer formId;
	private String formName;
	private String subject;
	private Integer demographicId;
	private boolean current;
	private Date formDate;
	private Date formTime;
	private String providerNo;
	private String formData;
	private boolean patientIndependent;
	private String roleType;



	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getFormId()
	{
		return formId;
	}

	public void setFormId(Integer formId)
	{
		this.formId = formId;
	}

	public String getFormName()
	{
		return formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public Integer getDemographicId()
	{
		return demographicId;
	}

	public void setDemographicId(Integer demographicId)
	{
		this.demographicId = demographicId;
	}

	public boolean getCurrent()
	{
		return current;
	}

	public void setCurrent(boolean current)
	{
		this.current = current;
	}

	public Date getFormDate()
	{
		return formDate;
	}

	public void setFormDate(Date formDate)
	{
		this.formDate = formDate;
	}

	public Date getFormTime()
	{
		return formTime;
	}

	public void setFormTime(Date formTime)
	{
		this.formTime = formTime;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getFormData()
	{
		return formData;
	}

	public void set(String formData)
	{
		this.formData = formData;
	}

	public boolean getPatientIndependent()
	{
		return patientIndependent;
	}

	public void setPatientIndependent(boolean patientIndependent)
	{
		this.patientIndependent = patientIndependent;
	}

	public String getRoleType()
	{
		return roleType;
	}

	public void setRoleType(String roleType)
	{
		this.roleType = roleType;
	}



	public static EFormDataTransfer toTransfer(EFormData eformData) 
	{
		if (eformData==null) return(null);
		
		EFormDataTransfer eformDataTransfer = new EFormDataTransfer();

		BeanUtils.copyProperties(eformData, eformDataTransfer);
		
		return (eformDataTransfer);
	}

	public static EFormDataTransfer[] toTransfers(List<EFormData> eformDatas) {
		ArrayList<EFormDataTransfer> results = new ArrayList<EFormDataTransfer>();

		for (EFormData eformData : eformDatas) {
			results.add(toTransfer(eformData));
		}

		return (results.toArray(new EFormDataTransfer[0]));
	}



	@Override
	public String toString() {
		return (ReflectionToStringBuilder.toString(this));
	}

	public EFormData copyTo(EFormData eformData) {
		BeanUtils.copyProperties(this, eformData);

		return (eformData);
	}
}

