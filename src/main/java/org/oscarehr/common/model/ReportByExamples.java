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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="reportByExamples")
public class ReportByExamples extends AbstractModel<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String providerNo;

	private String query;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="datetime_end")
	private Date datetimeEnd;

	@Column(name = "rows_returned")
	private Long rowsReturned;

	public Integer getId() {
    	return id;
    }

	public void setId(Integer id) {
    	this.id = id;
    }

	public String getProviderNo() {
    	return providerNo;
    }

	public void setProviderNo(String providerNo) {
    	this.providerNo = providerNo;
    }

	public String getQuery() {
    	return query;
    }

	public void setQuery(String query) {
    	this.query = query;
    }

	public Date getDate() {
    	return date;
    }

	public void setDate(Date date) {
    	this.date = date;
    }

	public Date getDatetimeEnd()
	{
		return datetimeEnd;
	}

	public void setDatetimeEnd(Date datetimeEnd)
	{
		this.datetimeEnd = datetimeEnd;
	}

	public Long getRowsReturned()
	{
		return rowsReturned;
	}

	public void setRowsReturned(Long rowsReturned)
	{
		this.rowsReturned = rowsReturned;
	}
}
