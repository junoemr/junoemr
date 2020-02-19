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
package org.oscarehr.integration.iceFall.service.exceptions;

public class IceFallException extends RuntimeException
{
	public enum USER_ERROR_MESSAGE {
		UNKNOWN_ERROR,
		INTEGRATION_DISABLED,
		NO_CUST_ID_OR_EMAIL,
		CUST_EMAIL_ALREADY_EXISTS,
		USER_CREATION_ERROR,
		DOCTOR_LOOKUP_ERROR,
		PRESCRIPTION_SEND_ERROR,
		PRESCRIPTION_CREATION_ERROR,
		INTERNAL_SERVER_ERROR,
	}

	/**
	 * get the user facing error message based on error type
	 * @param errorMessage - the error message type
	 * @param provider - provider that caused the error (some messages include provider name).
	 * @return - the message
	 */
	static public String getUserErrorMessage(IceFallException.USER_ERROR_MESSAGE errorMessage, org.oscarehr.common.model.Provider provider)
	{
		switch (errorMessage)
		{
			case UNKNOWN_ERROR:
				return "An error occurred when attempting to connect " +
								"to the remote system used in receiving " +
								"the customer’s prescription electronically. Please fax the prescription.";
			case INTEGRATION_DISABLED:
				return "The Ice Fall Integration is not currently enabled. Please enable it on the Admin Page, " +
								"admin privileges required. In the mean time, please fax the prescription";
			case NO_CUST_ID_OR_EMAIL:
				return "This patient does not have a valid email address or a canopy customerid and as " +
								"a result their prescription cannot be sent electronically. Please fax " +
								"the prescription.";
			case CUST_EMAIL_ALREADY_EXISTS:
				return "The email associated with this patient is already in use. Please fax the prescription";
			case USER_CREATION_ERROR:
				return "An error occurred when attempting to " +
								"retrieve the canopy customerid for this " +
								"patient. Please fax the prescription.";
			case DOCTOR_LOOKUP_ERROR:
				return  provider.getDisplayName() + " does not have a valid doctor id and cannot send " +
								"prescriptions electronically. Please fax the prescription.";
			case PRESCRIPTION_SEND_ERROR:
				return "An error occurred when attempting to send " +
								"the customer’s prescription electronically. Please fax the prescription.";
			case PRESCRIPTION_CREATION_ERROR:
				return "An internal error occurred while preparing this form for submission. Please fax the prescription";
			case INTERNAL_SERVER_ERROR:
				return "An internal server error has occurred while trying to electronically submit this prescription. Please fax the prescription";
		}

		return "";
	}

	USER_ERROR_MESSAGE userErrorMessage = USER_ERROR_MESSAGE.UNKNOWN_ERROR;

	public IceFallException(String msg)
	{
		super(msg);
	}

	public IceFallException(String msg, USER_ERROR_MESSAGE error_message)
	{
		super(msg);
		userErrorMessage = error_message;
	}

	public IceFallException(String msg, Exception e)
	{
		super(msg, e);
	}

	public IceFallException(String msg, Exception e, USER_ERROR_MESSAGE error_message)
	{
		super(msg, e);
		userErrorMessage = error_message;
	}

	public String getUserErrorMessage(org.oscarehr.common.model.Provider provider)
	{
		return IceFallException.getUserErrorMessage(userErrorMessage, provider);
	}

	public USER_ERROR_MESSAGE getUserErrorMessageEnum()
	{
		return userErrorMessage;
	}
}
