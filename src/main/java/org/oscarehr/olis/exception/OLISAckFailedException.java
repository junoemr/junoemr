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
package org.oscarehr.olis.exception;

import lombok.Getter;
import oscar.oscarLab.ca.all.parsers.OLISHL7Handler;

import java.util.List;
import java.util.stream.Collectors;

public class OLISAckFailedException extends OLISException
{
	public enum QAKStatus {
		OK, // data found, no errors
		NF, // no data found, no errors
		AE, // application error
		AR, // application reject
	}
	@Getter
	private final QAKStatus statusCode;
	@Getter
	private final List<OLISHL7Handler.OLISError> errors;

	public OLISAckFailedException()
	{
		this(null, (QAKStatus) null);
	}
	public OLISAckFailedException(String message)
	{
		this(message, (QAKStatus) null);
	}
	public OLISAckFailedException(String message, String statusCode)
	{
		this(message, QAKStatus.valueOf(statusCode));
	}
	public OLISAckFailedException(String message, QAKStatus statusCode)
	{
		this(message, statusCode, null);
	}
	public OLISAckFailedException(String message, String statusCode, List<OLISHL7Handler.OLISError> errors)
	{
		this(message, QAKStatus.
				valueOf(statusCode), errors);
	}
	public OLISAckFailedException(String message, QAKStatus statusCode, List<OLISHL7Handler.OLISError> errors)
	{
		super(message);
		this.statusCode = statusCode;
		this.errors = errors;
	}

	public boolean isStatusOK()
	{
		return this.getStatusCode().equals(QAKStatus.OK);
	}
	public boolean isStatusNotFound()
	{
		return this.getStatusCode().equals(QAKStatus.NF);
	}
	public boolean isStatusRejection()
	{
		return this.getStatusCode().equals(QAKStatus.AR);
	}
	public boolean isStatusError()
	{
		return this.getStatusCode().equals(QAKStatus.AE);
	}

	@Override
	public String getMessage()
	{
		String message = super.getMessage();
		if(this.statusCode != null)
		{
			message = message + " [" + this.statusCode + "]";
		}
		if(this.errors != null)
		{
			message = message + ":\n" +
					this.errors.stream()
							.map(OLISHL7Handler.OLISError::getText)
							.collect(Collectors.joining("\n"));
		}
		return message;
	}
}
