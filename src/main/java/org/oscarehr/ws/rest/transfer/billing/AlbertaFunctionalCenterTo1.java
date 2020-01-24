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

import org.oscarehr.billing.CA.AB.model.AlbertaFunctionalCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlbertaFunctionalCenterTo1 implements Serializable
{
	private String code;
	private String description;

	/**
	 * Create a list of alberta functional center transfer objects from a list of alberta functional centers
	 * @param albertaFunctionalCenters - the functional centers to convert
	 * @return - a new list of functional center transfer objects
	 */
	public static List<AlbertaFunctionalCenterTo1> fromList(List<AlbertaFunctionalCenter> albertaFunctionalCenters)
	{
		ArrayList<AlbertaFunctionalCenterTo1> albertaFunctionalCenterTo1s = new ArrayList<>();
		for (AlbertaFunctionalCenter functionalCenter : albertaFunctionalCenters)
		{
			albertaFunctionalCenterTo1s.add(new AlbertaFunctionalCenterTo1(functionalCenter));
		}
		return albertaFunctionalCenterTo1s;
	}

	public AlbertaFunctionalCenterTo1 (AlbertaFunctionalCenter albertaFunctionalCenter)
	{
		this.code = albertaFunctionalCenter.getCode();
		this.description = albertaFunctionalCenter.getDescription();
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
