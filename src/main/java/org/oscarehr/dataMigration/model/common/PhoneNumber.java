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
package org.oscarehr.dataMigration.model.common;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;

@Data
public class PhoneNumber extends AbstractTransientModel
{
	public enum PHONE_TYPE {
		HOME,
		WORK,
		CELL,
	}

	private String number;
	private String extension;
	private boolean primaryContactNumber;
	private PHONE_TYPE phoneType;

	public PhoneNumber()
	{
		this(null, null);
	}

	public PhoneNumber(String number)
	{
		this(number, null);
	}

	public PhoneNumber(String number, String extension)
	{
		this(number, extension, false);
	}

	public PhoneNumber(String number, String extension, boolean primaryContactNumber)
	{
		this.setNumber(number);
		this.setExtension(extension);
		this.primaryContactNumber = primaryContactNumber;
	}

	public void setNumber(String number)
	{
		// only valid digits allowed
		this.number = stripInvalidChars(number);
	}

	public void setExtension(String extension)
	{
		// only valid digits allowed
		this.extension = stripInvalidChars(extension);
	}

	public String getNumberFormattedHL7()
	{
		if(number != null && number.length() == 10)
		{
			return "(" + number.substring(0,3) + ")" + number.substring(3,6) + "-" + number.substring(6);
		}
		return null;
	}

	public String getNumberFormattedDisplay()
	{
		if(number != null && number.length() == 10)
		{
			return "(" + number.substring(0,3) + ") " + number.substring(3,6) + "-" + number.substring(6);
		}
		return "";
	}

	public void setPhoneTypeHome()
	{
		this.setPhoneType(PHONE_TYPE.HOME);
	}
	public void setPhoneTypeWork()
	{
		this.setPhoneType(PHONE_TYPE.WORK);
	}
	public void setPhoneTypeCell()
	{
		this.setPhoneType(PHONE_TYPE.CELL);
	}

	public boolean isTypeHome()
	{
		return (PHONE_TYPE.HOME.equals(this.getPhoneType()));
	}
	public boolean isTypeWork()
	{
		return (PHONE_TYPE.WORK.equals(this.getPhoneType()));
	}
	public boolean isTypeCell()
	{
		return (PHONE_TYPE.CELL.equals(this.getPhoneType()));
	}

	public static PhoneNumber of(String number)
	{
		return PhoneNumber.of(number, null, false);
	}
	public static PhoneNumber of(String number, String extension)
	{
		return PhoneNumber.of(number, extension, false);
	}
	public static PhoneNumber of(String number, String extension, boolean primaryContactNumber)
	{
		if(number != null)
		{
			return new PhoneNumber(number, extension, primaryContactNumber);
		}
		return null;
	}

	private String stripInvalidChars(String value)
	{
		return (value == null) ? null : value.replaceAll("[^a-zA-Z0-9]", "");
	}
}
