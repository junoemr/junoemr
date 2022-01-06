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
package org.oscarehr.ws.external.rest.v1.transfer.tickler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.ws.validator.DemographicNoConstraint;
import org.oscarehr.ws.validator.ProviderNoConstraint;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import oscar.util.ConversionUtils;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement
@Schema(description = "Tickler data transfer object")
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public abstract class TicklerTransferBase implements Serializable
{
	@NotNull
	@DemographicNoConstraint(allowNull = false)
	@Schema(description="The demographic number to which this tickler pertains")
	private Integer demographicNo;

	@NotNull
	@Schema(description="The ticklers message")
	private String message;

	@NotNull
	@Schema(description="The status of the tickler, Ex: 'A' for active, 'D' for deleted", allowableValues = {"A", "C", "D"}, example = "A")
	private Tickler.STATUS status;

	@NotNull
	@Schema(description = "The date after which the tickler becomes 'overdue'")
	private LocalDateTime serviceDateTime;

	@ProviderNoConstraint(allowNull = false)
	@Size(max=6)
	@Schema(description = "The provider number for the creator of the tickler")
	private String creator;

	@Schema(description = "The priority fo the tickler.", allowableValues = {"High", "Low", "Normal"}, example="Normal")
	private Tickler.PRIORITY priority;

	@NotNull
	@ProviderNoConstraint(allowNull = false)
	@Size(max=255)
	@Schema(description = "The provider number of the provider that the tickler is assigned to")
	private String taskAssignedTo;

	@Schema(description = "The category of the tickler")
	private Integer categoryId;

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Tickler.STATUS getStatus()
	{
		return status;
	}

	public void setStatus(Tickler.STATUS status)
	{
		this.status = status;
	}

	public LocalDateTime getServiceDateTime()
	{
		return serviceDateTime;
	}

	public void setServiceDateTime(LocalDateTime serviceDateTime)
	{
		this.serviceDateTime = serviceDateTime;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public Tickler.PRIORITY getPriority()
	{
		return priority;
	}

	public void setPriority(Tickler.PRIORITY priority)
	{
		this.priority = priority;
	}

	public String getTaskAssignedTo()
	{
		return taskAssignedTo;
	}

	public void setTaskAssignedTo(String taskAssignedTo)
	{
		this.taskAssignedTo = taskAssignedTo;
	}

	public Integer getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(Integer categoryId)
	{
		this.categoryId = categoryId;
	}

	/**
	 * get a new tickler model object from this transfer object.
	 * @return - a new tickler model.
	 */
	public Tickler toTickler()
	{
		Tickler t = new Tickler();
		String [] ignore = {"serviceDate"};
		BeanUtils.copyProperties(this, t, ignore);
		//dates do not copy properly, copy manually
		t.setServiceDate(ConversionUtils.toNullableLegacyDateTime(getServiceDateTime()));
		return t;
	}

	public String[] getNullPropertyNames (Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<>();
		for(java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
}
