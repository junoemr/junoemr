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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="log_report_by_template_explain")
public class LogReportByTemplateExplain extends AbstractModel<Integer>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "log_report_by_template_id")
	private LogReportByTemplate report;

	@Column(name="select_type")
	private String selectType;

	@Column(name="\"table\"")
	private String table;

	@Column(name="type")
	private String type;

	@Column(name="possible_keys")
	private String possibleKeys;

	@Column(name="\"key\"")
	private String key;

	@Column(name="key_len")
	private Integer keyLen;

	@Column(name="ref")
	private String ref;

	@Column(name="\"rows\"")
	private Integer rows;

	@Column(name="extra")
	private String extra;

	@Override
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public LogReportByTemplate getReport()
	{
		return report;
	}

	public void setReport(LogReportByTemplate report)
	{
		this.report = report;
	}

	public String getSelectType()
	{
		return selectType;
	}

	public void setSelectType(String selectType)
	{
		this.selectType = selectType;
	}

	public String getTable()
	{
		return table;
	}

	public void setTable(String table)
	{
		this.table = table;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getPossibleKeys()
	{
		return possibleKeys;
	}

	public void setPossibleKeys(String possibleKeys)
	{
		this.possibleKeys = possibleKeys;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Integer getKeyLen()
	{
		return keyLen;
	}

	public void setKeyLen(Integer keyLen)
	{
		this.keyLen = keyLen;
	}

	public String getRef()
	{
		return ref;
	}

	public void setRef(String ref)
	{
		this.ref = ref;
	}

	public Integer getRows()
	{
		return rows;
	}

	public void setRows(Integer rows)
	{
		this.rows = rows;
	}

	public String getExtra()
	{
		return extra;
	}

	public void setExtra(String extra)
	{
		this.extra = extra;
	}
}
