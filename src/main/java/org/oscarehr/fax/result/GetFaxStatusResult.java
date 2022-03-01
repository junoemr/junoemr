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

package org.oscarehr.fax.result;

import java.util.Date;
import java.util.Optional;

public interface GetFaxStatusResult
{
	/**
	 * Determine if the API request was successful.  Broadly speaking, implementers should set this to true
	 * if the api response code is 2XX, otherwise false if response code is either 4XX or 5XX.
	 * @return true if API call was successful.
	 */
	boolean isSuccess();

	/**
	 * Return a provider-specific string indicating that the remote service has sent the fax
	 * @return provider-specific sent string.
	 */
	String getRemoteSentStatus();

	/**
	 * The time at which the remote service sent the fax
	 * @return Optional sending time as Java.date
	 */
	Optional<Date> getRemoteSendTime();

	/**
	 * API Error code. If the API call is successful, or if no error code is sent on an error this value will be empty.
	 * @return Optional error code.
	 */
	Optional<String> getErrorCode();

	/**
	 * API error message.  If the API call is successful, or if no error reason is provided,
	 * then this value will be empty.
	 * @return Optional error reason
	 */
	Optional<String> getError();
}