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
package org.oscarehr.integration.SRFax.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.integration.SRFax.api.resultWrapper.ListWrapper;
import org.oscarehr.integration.SRFax.api.resultWrapper.SingleWrapper;
import org.oscarehr.util.MiscUtils;

import static org.oscarehr.integration.SRFax.api.resultWrapper.SingleWrapper.STATUS_SUCCESS;

public class SRFaxApiConnectorTest
{
	static final Logger logger = MiscUtils.getLogger();

	@Test
	public void test_processSingleResponse_success_integer()
	{
		Integer expectedResult = 10;
		String apiResponse = buildSRFaxResponseJson(true, String.valueOf(expectedResult));
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		SingleWrapper<Integer> response = SRFaxApiConnector.processSingleResponse(apiResponse, new TypeReference<SingleWrapper<Integer>>(){});

		Assert.assertTrue("Should be a success response", response.isSuccess());
		Assert.assertEquals("result should be an integer", expectedResult, response.getResult());
		Assert.assertNull("no error on success response", response.getError());
	}

	@Test
	public void test_processSingleResponse_success_string()
	{
		String expectedResult = "some string response";
		String apiResponse = buildSRFaxResponseJson(true, "\"" + expectedResult + "\"");
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		SingleWrapper<String> response = SRFaxApiConnector.processSingleResponse(apiResponse, new TypeReference<SingleWrapper<String>>(){});

		Assert.assertTrue("Should be a success response", response.isSuccess());
		Assert.assertEquals("result should be a string", expectedResult, response.getResult());
		Assert.assertNull("no error on success response", response.getError());
	}

	@Test
	public void test_processSingleResponse_error_string()
	{
		String expectedResult = "some error response message";
		String apiResponse = buildSRFaxResponseJson(false, "\"" + expectedResult + "\"");
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		SingleWrapper<String> response = SRFaxApiConnector.processSingleResponse(apiResponse, new TypeReference<SingleWrapper<String>>(){});

		Assert.assertFalse("Should be a error response", response.isSuccess());
		Assert.assertNull("no result on error response", response.getResult());
		Assert.assertEquals("response should have an error message", expectedResult, response.getError());
	}

	@Test(expected = FaxApiConnectionException.class)
	public void test_processSingleResponse_error_blockedIP()
	{
		String apiResponse = ""; // blank indicates srafax has blocked the IP, for some reason
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		SingleWrapper<String> response = SRFaxApiConnector.processSingleResponse(apiResponse, new TypeReference<SingleWrapper<String>>(){});
	}

	@Test(expected = FaxApiConnectionException.class)
	public void test_processSingleResponse_error_invalidJson()
	{
		String apiResponse = "<html>Error 500 Response<html>"; // srfax returns html sometimes when they break something
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		SingleWrapper<String> response = SRFaxApiConnector.processSingleResponse(apiResponse, new TypeReference<SingleWrapper<String>>(){});
	}

	@Test(expected = FaxApiConnectionException.class)
	public void test_processListResponse_error_blockedIP()
	{
		String apiResponse = ""; // blank indicates srafax has blocked the IP, for some reason
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		ListWrapper<String> response = SRFaxApiConnector.processListResponse(apiResponse, new TypeReference<ListWrapper<String>>(){});
	}

	@Test(expected = FaxApiConnectionException.class)
	public void test_processListResponse_error_invalidJson()
	{
		String apiResponse = "<html>Error 500 Response<html>"; // srfax returns html sometimes when they break something
		logger.info("[test response] SRFax API Response:\n" + apiResponse);

		ListWrapper<String> response = SRFaxApiConnector.processListResponse(apiResponse, new TypeReference<ListWrapper<String>>(){});
	}

	private String buildSRFaxResponseJson(boolean success, Object result)
	{
		return "{\"Status\": " + (success ? "\"" + STATUS_SUCCESS + "\"" : "\"Error\"") + ", " +
				"\"Result\": " + result.toString() + "}";
	}
}