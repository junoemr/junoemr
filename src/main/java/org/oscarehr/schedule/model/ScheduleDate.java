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


package org.oscarehr.schedule.model;

import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Site;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="scheduledate")
public class ScheduleDate extends AbstractModel<Integer>
{
	public static final char STATUS_ACTIVE = 'A';
	public static final char STATUS_DELETED = 'D';
	public static final String AVAILABLE = "1";
	public static final String UNAVAILABLE = "0";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	@Column(name="sdate")
	private Date date;
	@Column(name="provider_no")
	private String providerNo;
	private String available;
	private char priority;
	@Column(name="reason")
	private String reason;
	private String hour;
	private String creator;
	private char status;

	@Column(name="site_id")
	private Integer siteId;

	@OneToOne(fetch= FetchType.LAZY)
	@JoinColumn(name="site_id", referencedColumnName="site_id", insertable=false, updatable=false)
	private Site site;
	
	public Integer getId() {
		return id;
	}

	public Date getDate() {
    	return date;
    }

	public void setDate(Date date) {
    	this.date = date;
    }

	public String getProviderNo() {
    	return providerNo;
    }

	public void setProviderNo(String providerNo) {
    	this.providerNo = providerNo;
    }

	public boolean isAvailable() {
    	if (AVAILABLE.equals(available))
		{
			return true;
		} else
		{
			return false;
		}
    }

	public void setAvailable(boolean available) {
    	if (available)
		{
			this.available = AVAILABLE;
		} else
		{
			this.available = UNAVAILABLE;
		}

    }

	public char getPriority() {
    	return priority;
    }

	public void setPriority(char priority) {
    	this.priority = priority;
    }

	public String getReason() {
    	return reason;
    }

	public void setReason(String reason) {
    	this.reason = reason;
    }

	public String getHour() {
    	return hour;
    }

	public void setHour(String hour) {
    	this.hour = hour;
    }

	public String getCreator() {
    	return creator;
    }

	public void setCreator(String creator) {
    	this.creator = creator;
    }

	public char getStatus() {
    	return status;
    }

	public void setStatus(char status) {
    	this.status = status;
    }

	public Site getSite()
	{
		return site;
	}

	public void setSite(Site site)
	{
		this.site = site;
	}

	public int getSiteId()
	{
		return siteId;
	}

	public void setSiteId(int siteId)
	{
		this.siteId = siteId;
	}
}
