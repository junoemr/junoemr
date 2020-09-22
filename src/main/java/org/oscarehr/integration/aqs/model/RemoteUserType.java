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
package org.oscarehr.integration.aqs.model;

public class RemoteUserType
{
	private ca.cloudpractice.aqs.client.model.RemoteUserType aqsRemoteUserType;

	public RemoteUserType(ca.cloudpractice.aqs.client.model.RemoteUserType aqsRemoteUserType)
	{
		this.aqsRemoteUserType = aqsRemoteUserType;
	}

	public String getValue()
	{
		return this.aqsRemoteUserType.getValue();
	}

	public String name()
	{
		return this.aqsRemoteUserType.name();
	}

	public boolean equals(RemoteUserType other)
	{
		return other.getValue().equals(this.getValue());
	}

	public boolean equals(ca.cloudpractice.aqs.client.model.RemoteUserType  other)
	{
		return this.equals(new RemoteUserType(other));
	}

	@Override
	public String toString()
	{
		return this.aqsRemoteUserType.toString();
	}
}
