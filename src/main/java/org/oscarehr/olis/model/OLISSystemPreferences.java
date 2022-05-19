/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.olis.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Optional;

@Entity
public class OLISSystemPreferences extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String startTime;
	private String endTime;
	private Integer pollFrequency;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRun;

	private boolean filterPatients;
	
	
	@Override
	public Integer getId() {
		return id;
	}	
	
	public String getStartTime() {
    	return startTime;
    }

	public Optional<String> getOptionalStartTime()
	{
		return Optional.ofNullable(startTime);
	}

	public void setStartTime(String startTime) {
    	this.startTime = startTime;
    }

	public String getEndTime() {
    	return endTime;
    }

	public void setEndTime(String endTime) {
    	this.endTime = endTime;
    }

	public Optional<Integer> getPollFrequency()
	{
		return Optional.ofNullable(pollFrequency);
	}

	public void setPollFrequency(Integer pollFrequency) {
    	this.pollFrequency = pollFrequency;
    }

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public Optional<Date> getOptionalLastRunTime()
	{
		return Optional.ofNullable(lastRun);
	}
	

	public boolean isFilterPatients() {
    	return filterPatients;
    }

	public void setFilterPatients(boolean filterPatients) {
    	this.filterPatients = filterPatients;
    }

	public OLISSystemPreferences(){
		super();
	}	
}
