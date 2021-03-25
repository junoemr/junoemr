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


package org.oscarehr.measurements.model;

import org.oscarehr.common.model.AbstractModel;

import java.io.Serializable;

import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class FlowSheetUserCreated extends AbstractModel<Integer> implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name="name")
	private String name;

	@Column(name="dxcodeTriggers")
	private String dxcodeTriggers;

	@Column(name="displayName")
	private String displayName;

	@Column(name="warningColour")
	private String warningColour;

	@Column(name="recommendationColour")
	private String recommendationColour;

	@Column(name="topHTML")
	private String topHTML;

	@Column(name="archived")
	private Boolean archived;

	@Temporal(TemporalType.DATE)
	private Date createdDate;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
    	return name;
    }

	public void setName(String name)
	{
    	this.name = name;
    }

	public String getDxcodeTriggers()
	{
    	return dxcodeTriggers;
    }

	public void setDxcodeTriggers(String dxcodeTriggers)
	{
    	this.dxcodeTriggers = dxcodeTriggers;
    }

	public String getDisplayName()
	{
    	return displayName;
    }

	public void setDisplayName(String displayName)
	{
    	this.displayName = displayName;
    }

	public String getWarningColour()
	{
    	return warningColour;
    }

	public void setWarningColour(String warningColour)
	{
    	this.warningColour = warningColour;
    }

	public String getRecommendationColour()
	{
    	return recommendationColour;
    }

	public void setRecommendationColour(String recommendationColour)
	{
    	this.recommendationColour = recommendationColour;
    }

	public String getTopHTML()
	{
    	return topHTML;
    }

	public void setTopHTML(String topHTML)
	{
    	this.topHTML = topHTML;
    }

	public Boolean getArchived()
	{
    	return archived;
    }

	public void setArchived(Boolean archived)
	{
    	this.archived = archived;
    }

	public Date getCreatedDate()
	{
    	return createdDate;
    }

	public void setCreatedDate(Date createdDate)
	{
    	this.createdDate = createdDate;
    }
}
