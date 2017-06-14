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


package org.oscarehr.common.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="waitingList")
public class WaitingList extends AbstractModel<Integer> {
	
	public static final String IS_HISTORY_YES = "Y";
	public static final String IS_HISTORY_NO = "N";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
//	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@ManyToOne(optional=false)
	@JoinColumn(name="listID")
	private WaitingListName waitingListName;

//	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@ManyToOne(optional=false)
	@JoinColumn(name="demographic_no")
	private Demographic demographic;

	private String note;

	private long position;

	@Temporal(TemporalType.TIMESTAMP)
	private Date onListSince;

	@Column(name="is_history")
	private String isHistory;

	public Integer getId() {
    	return id;
    }

	public void setId(Integer id) {
    	this.id = id;
    }

	/**
	 * Use getWaitingListName() to get the object instead
	 * @return id associated with the waitingListName object, or 0 if not correctly linked to a valid waitingListName
	 */
	@Deprecated
	public Integer getListId() {
		if(waitingListName == null) {
			return 0;
		}
    	return waitingListName.getId();
    }
	
	public WaitingListName getWaitingListName() {
		return waitingListName;
	}

	public void setWaitingListName(WaitingListName waitingListName) {
		this.waitingListName = waitingListName;
	}

	public int getDemographicNo() {
    	return demographic.getDemographicNo();
    }
	
	public Demographic getDemographic() {
		return demographic;
	}
	
	public void setDemographic(Demographic demographic) {
		this.demographic = demographic;
	}

	public String getNote() {
    	return note;
    }

	public void setNote(String note) {
    	this.note = note;
    }

	public long getPosition() {
    	return position;
    }

	public void setPosition(long position) {
    	this.position = position;
    }

	public Date getOnListSince() {
    	return onListSince;
    }

	public void setOnListSince(Date onListSince) {
    	this.onListSince = onListSince;
    }

	public String getIsHistory() {
    	return isHistory;
    }

	public void setIsHistory(String isHistory) {
    	this.isHistory = isHistory;
    }
	
	public void setHistory(boolean isHistory) {
		setIsHistory(isHistory ? IS_HISTORY_YES : IS_HISTORY_NO);
	}

}
