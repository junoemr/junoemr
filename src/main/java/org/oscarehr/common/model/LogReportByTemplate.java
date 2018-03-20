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

	//TODO link with a template jpa object when ported to oscar 15.
	@Column(name = "template_id")
	private Integer templateId;

	@Column(name = "provider_no")
	private Integer providerNo;

	@Column(name = "datetime_start")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeStart;

	@Column(name = "datetime_end")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeEnd;

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

	public Integer getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(Integer providerNo)
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
}

