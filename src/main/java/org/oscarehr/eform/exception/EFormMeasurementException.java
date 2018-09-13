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

package org.oscarehr.eform.exception;

import org.apache.struts.action.ActionMessages;
import oscar.eform.data.EForm;

/**
 * Custom exception class for saving EForms.
 * This is intended as a way to allow specific errors and error messages to be
 * passed out of the transactional service for backwards compatibility.
 */
public class EFormMeasurementException extends RuntimeException
{
	private ActionMessages errors;
	private EForm eformData;

	public EFormMeasurementException(String message)
	{
		super(message);
		eformData = null;
		errors = null;
	}

	public EFormMeasurementException(String message, ActionMessages errors, EForm eformData)
	{
		super(message);
		this.eformData = eformData;
		this.errors = errors;
	}
	public ActionMessages getErrors()
	{
		return errors;
	}
	public EForm getEformData()
	{
		return eformData;
	}
}
