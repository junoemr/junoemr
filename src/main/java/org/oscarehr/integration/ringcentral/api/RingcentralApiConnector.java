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
package org.oscarehr.integration.ringcentral.api;

import com.google.api.client.auth.oauth2.Credential;
import org.oscarehr.integration.ringcentral.api.input.RingCentralSendFaxInput;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralSendFaxResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import oscar.util.RESTClient;

import java.text.MessageFormat;
import org.oscarehr.fax.oauth.RingCentralCredentialStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Lazy
@Component
public class RingcentralApiConnector extends RESTClient
{
	protected static final String REST_API_BASE = "platform.devtest.ringcentral.com/restapi/v1.0/";
	private String BASE_URL = "https://platform.devtest.ringcentral.com";

	public static final String RESPONSE_STATUS_RECEIVED="Received";
	public static final String RESPONSE_STATUS_QUEUED="Queued";
	public static final String RESPONSE_STATUS_SENT="Sent";
	public static final String RESPONSE_STATUS_DELIVERED="Delivered";
	public static final String RESPONSE_STATUS_SEND_FAILED="SendingFailed";
	public static final String RESPONSE_STATUS_DELIVERY_FAILED="DeliveryFailed";

	public static final List<String> RESPONSE_STATUSES_FINAL = new ArrayList<>(Arrays.asList(
		RESPONSE_STATUS_DELIVERED,
		RESPONSE_STATUS_SEND_FAILED,
		RESPONSE_STATUS_DELIVERY_FAILED
	));

	public static final List<String> RESPONSE_STATUSES_FAILED = new ArrayList<>(Arrays.asList(
		RESPONSE_STATUS_SEND_FAILED,
		RESPONSE_STATUS_DELIVERY_FAILED
	));

	public Credential getCredential() throws IOException
	{
		return RingCentralCredentialStore.getCredential(RingCentralCredentialStore.LOCAL_USER_ID);
	}

	public RingCentralAccountInfoResult getAccountInfo()
	{
		return getAccountInfo("~");
	}
	public RingCentralAccountInfoResult getAccountInfo(String accountId)
	{
		String url = buildUrl(DEFAULT_PROTOCOL, REST_API_BASE + "account/" + accountId);
		return doGet(url, getAuthorizationHeaders(), RingCentralAccountInfoResult.class);
	}

	public RingCentralSendFaxResult sendFax(String accountId, String extensionId, RingCentralSendFaxInput input)
	{
		String url = buildUrl(DEFAULT_PROTOCOL, REST_API_BASE +
				"account/" + accountId + "/extension/" + extensionId + "/fax");

		return doPost(url, getAuthorizationHeaders(), input, RingCentralSendFaxResult.class);
	}

	protected HttpHeaders getAuthorizationHeaders()
	{
		try
		{
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", MessageFormat.format("Bearer {0}", getCredential().getAccessToken()));
			return headers;
		}
		catch (IOException e)
		{
			// TODO: Robert handle me
			return null;
		}
	}
}