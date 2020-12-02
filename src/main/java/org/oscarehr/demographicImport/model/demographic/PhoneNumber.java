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
package org.oscarehr.demographicImport.model.demographic;

import lombok.Data;
import org.oscarehr.demographicImport.model.AbstractTransientModel;

@Data
public class PhoneNumber extends AbstractTransientModel
{
	private String number;
	private String extension;
	private boolean primaryContactNumber;

	public PhoneNumber()
	{
		this(null, null);
	}

	public PhoneNumber(String number)
	{
		this(number, null, false);
	}

	public PhoneNumber(String number, String extension)
	{
		this(number, extension, false);
	}

	public PhoneNumber(String number, String extension, boolean primaryContactNumber)
	{
		this.number = number;
		this.extension = extension;
		this.primaryContactNumber = primaryContactNumber;
	}

	public String getNumberFormattedHL7()
	{
		if(number != null && number.length() == 10)
		{
			return "(" + number.substring(0,3) + ")" + number.substring(3,6) + "-" + number.substring(6);
		}
		return null;
	}
}
