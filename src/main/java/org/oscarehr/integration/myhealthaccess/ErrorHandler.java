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

package org.oscarehr.integration.myhealthaccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.oscarehr.integration.myhealthaccess.dto.BaseErrorTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.util.MiscUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;

public class ErrorHandler <T> extends DefaultResponseErrorHandler
{
	Class<T> errorClass;

	public ErrorHandler(Class<T> errorClass)
	{
		this.errorClass = errorClass;
	}

	@Override
	public void handleError(ClientHttpResponse response)
	{
		MiscUtils.getLogger().error("EEEEEEEEEEEEEEEEEEEEEe");
		MiscUtils.getLogger().error("Enter Handler");
		try
		{
			InputStream inputStream = response.getBody();
			String body = IOUtils.toString(inputStream);
			MiscUtils.getLogger().error("body: " + body);
			BaseErrorTo1 baseError = new ObjectMapper().readValue(body, BaseErrorTo1.class);
			if(baseError.hasAuthError())
			{
				MiscUtils.getLogger().error("Auth Error: " +
						baseError.getAuthError().getCode());
				MiscUtils.getLogger().error(baseError.getAuthError().getMessage());
			}
			if(baseError.hasGenericErrors())
			{
				MiscUtils.getLogger().error("baseError status: " +
						baseError.getGenericErrors().get(0).getCode());
				MiscUtils.getLogger().error(baseError.getGenericErrors().get(0).getMessage());
			}
			BaseException baseException = new BaseException(body);
			baseException.setErrorObject(baseError);
			MiscUtils.getLogger().error("eeeeeeeeeeeeeeee");
			throw baseException;

		} catch (IOException e)
		{
			MiscUtils.getLogger().error("Error parsing: ", e);
			BaseErrorTo1 baseError = new BaseErrorTo1();
			baseError.addGenericError("juno_error", "Failed to parse yo");
			BaseException baseException = new BaseException("Failed to process error response");
			baseException.setErrorObject(baseError);
			MiscUtils.getLogger().error("eeeeeeeeeeeeeeee");
			throw baseException;
		}
	}
}
