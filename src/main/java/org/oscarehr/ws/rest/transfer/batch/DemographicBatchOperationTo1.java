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
package org.oscarehr.ws.rest.transfer.batch;

import java.io.Serializable;

public class DemographicBatchOperationTo1 implements Serializable
{
	private String 		operation;
	private Integer[] 	demographicNumbers;

	public Integer[] getDemographicNumbers()
	{
		return demographicNumbers;
	}

	public void setDemographicNumbers(Integer[] demographicNumbers)
	{
		this.demographicNumbers = demographicNumbers;
	}

	public String getOperation()
	{
		return operation;
	}

	public void setOperation(String operation)
	{
		this.operation = operation;
	}


	@Override
	public String toString()
	{
		String outString = "Operation: " + operation + " demographic numbers: ";
		for (Integer number : demographicNumbers)
		{
			outString += number +",";
		}
		return outString;
	}
}
