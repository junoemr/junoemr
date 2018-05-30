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
package org.oscarehr.eform.transfer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InstancedEFormListTransfer implements Serializable
{
	private Integer demographicId;

	private Integer formDataId;
	private String formName;
	private String formSubject;
	private LocalDateTime formDateTime;
	private LocalDateTime instanceCreationDateTime;

	public Integer getDemographicId()
	{
		return demographicId;
	}

	public void setDemographicId(Integer demographicId)
	{
		this.demographicId = demographicId;
	}

	public Integer getFormDataId()
	{
		return formDataId;
	}

	public void setFormDataId(Integer formDataId)
	{
		this.formDataId = formDataId;
	}

	public String getFormName()
	{
		return formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public String getFormSubject()
	{
		return formSubject;
	}

	public void setFormSubject(String formSubject)
	{
		this.formSubject = formSubject;
	}

	public LocalDateTime getFormDateTime()
	{
		return formDateTime;
	}

	public void setFormDateTime(LocalDateTime formDateTime)
	{
		this.formDateTime = formDateTime;
	}

	public LocalDateTime getInstanceCreationDateTime()
	{
		return instanceCreationDateTime;
	}

	public void setInstanceCreationDateTime(LocalDateTime instanceCreationDateTime)
	{
		this.instanceCreationDateTime = instanceCreationDateTime;
	}
}
