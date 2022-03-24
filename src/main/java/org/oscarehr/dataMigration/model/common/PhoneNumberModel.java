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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;

import java.util.Optional;

@Data
public class PhoneNumberModel extends AbstractTransientModel
{
	public enum PHONE_TYPE {
		HOME,
		WORK,
		CELL,
		FAX,
	}

	private String number;
	private String extension;
	private boolean primaryContactNumber;
	private PHONE_TYPE phoneType;

	public PhoneNumberModel()
	{
		this(null, null);
	}

	public PhoneNumberModel(String number)
	{
		this(number, null);
	}

	public PhoneNumberModel(String number, String extension)
	{
		this(number, extension, false);
	}

	public PhoneNumberModel(String number, String extension, boolean primaryContactNumber)
	{
		this(number, extension, primaryContactNumber, null);
	}

	public PhoneNumberModel(String number, String extension, boolean primaryContactNumber, PHONE_TYPE type)
	{
		this.setNumber(number);
		this.setExtension(extension);
		this.primaryContactNumber = primaryContactNumber;
		this.phoneType = type;
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

	@JsonIgnore
	public String getNumberFormattedHL7()
	{
		if(number != null && number.length() == 10)
		{
			return "(" + number.substring(0,3) + ")" + number.substring(3,6) + "-" + number.substring(6);
		}
		return null;
	}

	@JsonIgnore
	public String getNumberFormattedDisplay()
	{
		if(number != null && number.length() == 10)
		{
			return "(" + number.substring(0,3) + ") " + number.substring(3,6) + "-" + number.substring(6);
		}
		return "";
	}

	/**
	 * @return the optional number with 11 digits, adds 1 to the front of a 10 digit number.
	 * returns empty if the internal number is null, or does not conform to the 10 or 11 digit standard
	 */
	@JsonIgnore
	public Optional<String> getNumber11DigitsOnly()
	{
		if(number != null && (number.length() == 10 || number.length() == 11))
		{
			if(number.length() == 10)
			{
				return Optional.of("1" + number);
			}
			return Optional.of(number);
		}
		return Optional.empty();
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

	@JsonIgnore
	public boolean isTypeHome()
	{
		return (PHONE_TYPE.HOME.equals(this.getPhoneType()));
	}
	@JsonIgnore
	public boolean isTypeWork()
	{
		return (PHONE_TYPE.WORK.equals(this.getPhoneType()));
	}
	@JsonIgnore
	public boolean isTypeCell()
	{
		return (PHONE_TYPE.CELL.equals(this.getPhoneType()));
	}
	@JsonIgnore
	public boolean isTypeFax()
	{
		return (PHONE_TYPE.FAX.equals(this.getPhoneType()));
	}

	public static PhoneNumberModel of(String number)
	{
		return PhoneNumberModel.of(number, null, false, null);
	}
	public static PhoneNumberModel of(String number, PHONE_TYPE type)
	{
		return PhoneNumberModel.of(number, null, false, type);
	}
	public static PhoneNumberModel of(String number, String extension)
	{
		return PhoneNumberModel.of(number, extension, false, null);
	}
	public static PhoneNumberModel of(String number, String extension, boolean primaryContactNumber, PHONE_TYPE type)
	{
		if(number != null)
		{
			return new PhoneNumberModel(number, extension, primaryContactNumber, type);
		}
		return null;
	}

	private String stripInvalidChars(String value)
	{
		return (value == null) ? null : value.replaceAll("[^a-zA-Z0-9]", "");
	}
}
