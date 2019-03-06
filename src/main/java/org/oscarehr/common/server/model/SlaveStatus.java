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
package org.oscarehr.common.server.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SlaveStatus extends AbstractModel<String>
{
	@Id
	@Column(name="Master_Host")
	private String id;

	@Column(name="Slave_IO_Running")
	private String slaveIORunning;

	@Column(name="Slave_SQL_Running")
	private String slaveSQLRunning;

	@Override
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}

	public String getSlaveIORunning()
	{
		return slaveIORunning;
	}

	public void setSlaveIORunning(String slaveIORunning)
	{
		this.slaveIORunning = slaveIORunning;
	}

	public String getSlaveSQLRunning()
	{
		return slaveSQLRunning;
	}

	public void setSlaveSQLRunning(String slaveSQLRunning)
	{
		this.slaveSQLRunning = slaveSQLRunning;
	}
}
