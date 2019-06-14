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

package org.oscarehr.log.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name="log_report_by_template")
public class LogReportByTemplate extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "template_id")
	private Integer templateId;

	@Column(name = "provider_no")
	private String providerNo;

	@Column(name = "datetime_start")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeStart;

	@Column(name = "datetime_end")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeEnd;

	@Column(name = "rows_returned")
	private Long rowsReturned;

	@Column(name = "query_string")
	private String queryString;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getTemplateId()
	{
		return templateId;
	}

	public void setTemplateId(Integer templateId)
	{
		this.templateId = templateId;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public Date getDatetimeStart()
	{
		return datetimeStart;
	}

	public void setDatetimeStart(Date datetimeStart)
	{
		this.datetimeStart = datetimeStart;
	}

	public Date getDatetimeEnd()
	{
		return datetimeEnd;
	}

	public void setDatetimeEnd(Date datetimeEnd)
	{
		this.datetimeEnd = datetimeEnd;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
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

