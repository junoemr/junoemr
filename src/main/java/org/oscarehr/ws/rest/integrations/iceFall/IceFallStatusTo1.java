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

package org.oscarehr.ws.rest.integrations.iceFall;

import java.io.Serializable;

public class IceFallStatusTo1 implements Serializable
{
	private Boolean enabled;
	private Boolean visible;

	// clinic info
	private Integer clinicNo;
	private String  clinicUserName;
	// password intentionally omitted

	public IceFallStatusTo1(Boolean visible, Boolean enabled, Integer clinicNo, String clinicUserName)
	{
		setVisible(visible);
		setEnabled(enabled);
		setClinicNo(clinicNo);
		setClinicUserName(clinicUserName);
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public Boolean getVisible()
	{
		return visible;
	}

	public void setVisible(Boolean visible)
	{
		this.visible = visible;
	}

	public Integer getClinicNo()
	{
		return clinicNo;
	}

	public void setClinicNo(Integer clinicNo)
	{
		this.clinicNo = clinicNo;
	}

	public String getClinicUserName()
	{
		return clinicUserName;
	}

	public void setClinicUserName(String clinicUserName)
	{
		this.clinicUserName = clinicUserName;
	}
}
