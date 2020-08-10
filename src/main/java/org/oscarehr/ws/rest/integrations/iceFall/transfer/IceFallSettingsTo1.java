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

package org.oscarehr.ws.rest.integrations.iceFall.transfer;

import org.oscarehr.integration.iceFall.model.IceFallCredentials;

import java.io.Serializable;

public class IceFallSettingsTo1 implements Serializable
{
	private Boolean enabled;
	private Boolean visible;

	// clinic info
	private String  clinicUserName;
	private String  clinicEmail;
	private String  clinicPassword;

	public IceFallSettingsTo1() {}

	public IceFallSettingsTo1(Boolean visible, Boolean enabled, String clinicUserName, String clinicEmail)
	{
		setVisible(visible);
		setEnabled(enabled);
		setClinicUserName(clinicUserName);
		setClinicEmail(clinicEmail);
		// do not send password outgoing.
		setClinicPassword("");
	}

	/**
	 * copy values from this transfer object in to an iceFall credentials object for saving in the the DB.
	 * @param iceFallCredentials - the credentials object to copy to.
	 * @return - updated credentials object
	 */
	public IceFallCredentials updateCredentials(IceFallCredentials iceFallCredentials)
	{
		iceFallCredentials.setUsername(getClinicUserName());
		iceFallCredentials.setEmail(getClinicEmail());
		if (getClinicPassword() != null && !getClinicPassword().isEmpty())
		{
			iceFallCredentials.setPassword(getClinicPassword());
		}

		return iceFallCredentials;
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

	public String getClinicUserName()
	{
		return clinicUserName;
	}

	public void setClinicUserName(String clinicUserName)
	{
		this.clinicUserName = clinicUserName;
	}

	public String getClinicPassword()
	{
		return clinicPassword;
	}

	public void setClinicPassword(String clinicPassword)
	{
		this.clinicPassword = clinicPassword;
	}

	public String getClinicEmail()
	{
		return clinicEmail;
	}

	public void setClinicEmail(String clinicEmail)
	{
		this.clinicEmail = clinicEmail;
	}
}
