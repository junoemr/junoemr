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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.oscarehr.common.model.EFormValue;
import org.springframework.beans.BeanUtils;

public final class EFormValueTransfer {

	private Integer id;
	private Integer formDataId;
	private Integer formId;
	private Integer demographicId;
	private String varName;
	private String varValue;



	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getFormDataId()
	{
		return formDataId;
	}

	public void setFormDataId(Integer formDataId)
	{
		this.formDataId = formDataId;
	}

	public Integer getFormId()
	{
		return formId;
	}

	public void setFormId(Integer formId)
	{
		this.formId = formId;
	}

	public Integer getDemographicId()
	{
		return demographicId;
	}

	public void setDemographicId(Integer demographicId)
	{
		this.demographicId = demographicId;
	}

	public String getVarName()
	{
		return varName;
	}

	public void setVarName(String varName)
	{
		this.varName = varName;
	}

	public String getVarValue()
	{
		return varValue;
	}

	public void setVarValue(String varValue)
	{
		this.varValue = varValue;
	}



	public static EFormValueTransfer toTransfer(EFormValue eformValue) 
	{
		if (eformValue==null) return(null);
		
		EFormValueTransfer eformValueTransfer = new EFormValueTransfer();

		BeanUtils.copyProperties(eformValue, eformValueTransfer);
		
		return (eformValueTransfer);
	}

	public static EFormValueTransfer[] toTransfers(List<EFormValue> eformValues) {
		ArrayList<EFormValueTransfer> results = new ArrayList<EFormValueTransfer>();

		for (EFormValue eformValue : eformValues) {
			results.add(toTransfer(eformValue));
		}

		return (results.toArray(new EFormValueTransfer[0]));
	}



	@Override
	public String toString() {
		return (ReflectionToStringBuilder.toString(this));
	}

	public EFormValue copyTo(EFormValue eformValue) {
		BeanUtils.copyProperties(this, eformValue);

		return (eformValue);
	}
}


