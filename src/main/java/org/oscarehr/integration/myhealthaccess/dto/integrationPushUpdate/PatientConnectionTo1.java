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

package org.oscarehr.integration.myhealthaccess.dto.integrationPushUpdate;

// internal transfer for integration push updates queue.
// type: PATIENT_CONNECTION
public class PatientConnectionTo1 extends IntegrationPushUpdateBaseTo1
{
	private Integer demographicNo;
	private Integer securityNo;
	private Boolean rejected;

	public PatientConnectionTo1() { }

	public PatientConnectionTo1(Integer securityNo, Integer demographicNo, Boolean rejected)
	{
		this.demographicNo = demographicNo;
		this.securityNo = securityNo;
		this.rejected = rejected;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Integer getSecurityNo()
	{
		return securityNo;
	}

	public void setSecurityNo(Integer securityNo)
	{
		this.securityNo = securityNo;
	}

	public Boolean getRejected()
	{
		return rejected;
	}

	public void setRejected(Boolean rejected)
	{
		this.rejected = rejected;
	}
}
