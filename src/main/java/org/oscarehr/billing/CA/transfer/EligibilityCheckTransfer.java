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

package org.oscarehr.billing.CA.transfer;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

public class EligibilityCheckTransfer implements Serializable
{
	public enum ValidationStatus
	{
		COMPLETE,
		INCOMPLETE,
		UNAVAILABLE
	}

	private Boolean isEligible = false;
	private String result;
	private String error;
	private String message;
	private ValidationStatus validationStatus = ValidationStatus.INCOMPLETE;

	//TODO-legacy refactor this out of the transfer object
	@XmlTransient
	private String realFilename;

	public Boolean getEligible()
	{
		return isEligible;
	}

	public void setEligible(Boolean eligible)
	{
		isEligible = eligible;
	}

	public ValidationStatus getValidationStatus()
	{
		return validationStatus;
	}

	public void setValidationStatus(ValidationStatus validationStatus)
	{
		this.validationStatus = validationStatus;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getRealFilename()
	{
		return realFilename;
	}

	public void setRealFilename(String realFilename)
	{
		this.realFilename = realFilename;
	}
}
