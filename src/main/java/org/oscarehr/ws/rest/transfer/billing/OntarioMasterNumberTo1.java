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
package org.oscarehr.ws.rest.transfer.billing;

import org.oscarehr.billing.CA.ON.model.OntarioMasterNumber;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OntarioMasterNumberTo1 implements Serializable
{
	private String masterNumber;
	private String location;
	private String name;
	private String type;
	private String facilityNumber;

	public static List<OntarioMasterNumberTo1> fromList(List<OntarioMasterNumber> ontarioMasterNumbers)
	{
		ArrayList<OntarioMasterNumberTo1> ontarioMasterNumberTo1s = new ArrayList<>();
		for (OntarioMasterNumber ontarioMasterNumber : ontarioMasterNumbers)
		{
			ontarioMasterNumberTo1s.add(new OntarioMasterNumberTo1(ontarioMasterNumber));
		}
		return ontarioMasterNumberTo1s;
	}

	public OntarioMasterNumberTo1(OntarioMasterNumber ontarioMasterNumber)
	{
		this.masterNumber = ontarioMasterNumber.getMasterNumber();
		this.location = ontarioMasterNumber.getLocation();
		this.name = ontarioMasterNumber.getName();
		this.type = ontarioMasterNumber.getType();
		this.facilityNumber = ontarioMasterNumber.getFacilityNumber();
	}

	public String getMasterNumber()
	{
		return masterNumber;
	}

	public void setMasterNumber(String masterNumber)
	{
		this.masterNumber = masterNumber;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getFacilityNumber()
	{
		return facilityNumber;
	}

	public void setFacilityNumber(String facilityNumber)
	{
		this.facilityNumber = facilityNumber;
	}
}
