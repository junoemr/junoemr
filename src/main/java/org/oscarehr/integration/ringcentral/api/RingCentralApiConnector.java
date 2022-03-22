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
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.oauth.RingCentralCredentialStore;
import org.oscarehr.integration.ringcentral.api.input.RingCentralMessageListInput;
import org.oscarehr.integration.ringcentral.api.input.RingCentralMessageUpdateInput;
import org.oscarehr.integration.ringcentral.api.input.RingCentralSendFaxInput;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralCoverLetterListResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralMessageInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralMessageListResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralSendFaxResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import oscar.util.RESTClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RingCentralApiConnector extends RESTClient
{
	public static final String CURRENT_SESSION_INDICATOR = "~";
	protected static final String REST_API_BASE = "platform.devtest.ringcentral.com/restapi/v1.0/";

	public static final String RESPONSE_STATUS_RECEIVED="Received";
	public static final String RESPONSE_STATUS_QUEUED="Queued";
	public static final String RESPONSE_STATUS_SENT="Sent";
	public static final String RESPONSE_STATUS_DELIVERED="Delivered";
	public static final String RESPONSE_STATUS_SEND_FAILED="SendingFailed";
	public static final String RESPONSE_STATUS_DELIVERY_FAILED="DeliveryFailed";

	public static final List<String> RESPONSE_STATUSES_FINAL = new ArrayList<>(Arrays.asList(
		RESPONSE_STATUS_SENT,
		RESPONSE_STATUS_DELIVERED,
		RESPONSE_STATUS_SEND_FAILED,
		RESPONSE_STATUS_DELIVERY_FAILED
	));

	public static final List<String> RESPONSE_STATUSES_SUCCESS = new ArrayList<>(Arrays.asList(
		RESPONSE_STATUS_SENT,
		RESPONSE_STATUS_DELIVERED
	));


	public static final List<String> RESPONSE_STATUSES_FAILED = new ArrayList<>(Arrays.asList(
		RESPONSE_STATUS_SEND_FAILED,
		RESPONSE_STATUS_DELIVERY_FAILED
	));

	public RingCentralApiConnector()
	{
		this.setErrorHandler(new RingCentralApiErrorHandler());
	}

	public Optional<Credential> getCredential() throws IOException
	{
		return Optional.ofNullable(RingCentralCredentialStore.getCredential(RingCentralCredentialStore.LOCAL_USER_ID));
	}

	public RingCentralCoverLetterListResult getFaxCoverPageList()
	{
		String endpoint = REST_API_BASE + "dictionary/fax-cover-page";
		String url = buildUrl(DEFAULT_PROTOCOL, endpoint);
		return doGet(url, getAuthorizationHeaders(), RingCentralCoverLetterListResult.class);
	}

	public RingCentralAccountInfoResult getAccountInfo()
	{
		return getAccountInfo(CURRENT_SESSION_INDICATOR);
	}
	public RingCentralAccountInfoResult getAccountInfo(String accountId)
	{
		String endpoint = REST_API_BASE + "account/{0}";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId));
		return doGet(url, getAuthorizationHeaders(), RingCentralAccountInfoResult.class);
	}

	public RingCentralSendFaxResult sendFax(String accountId, String extensionId, RingCentralSendFaxInput input) throws IOException
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/fax";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId));

		HttpHeaders headers = getAuthorizationHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		return doPost(url, headers, input.toMultiValueMap(), RingCentralSendFaxResult.class);
	}

	public RingCentralMessageListResult getMessageList(String accountId, String extensionId, RingCentralMessageListInput input)
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/message-store";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId));
		return doGet(url, getAuthorizationHeaders(), input.toParameterMap(), RingCentralMessageListResult.class);
	}

	public RingCentralMessageInfoResult getMessage(String accountId, String extensionId, String messageId)
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/message-store/{2}";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId, messageId));
		return doGet(url, getAuthorizationHeaders(), RingCentralMessageInfoResult.class);
	}

	public InputStream getMessageContent(String accountId, String extensionId, String messageId, String attachmentId)
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/message-store/{2}/content/{3}";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId, messageId, attachmentId));
		byte[] byteArray = doGet(url, getAuthorizationHeaders(), byte[].class);
		return new ByteArrayInputStream(byteArray);
	}

	public RingCentralMessageInfoResult updateMessage(String accountId, String extensionId, String messageId, RingCentralMessageUpdateInput input)
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/message-store/{2}";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId, messageId));
		return doPut(url, getAuthorizationHeaders(), input.getParameterMap(), input, RingCentralMessageInfoResult.class);
	}

	protected HttpHeaders getAuthorizationHeaders()
	{
		try
		{
			Optional<Credential> oAuthCredential = getCredential();
			if(oAuthCredential.isPresent())
			{
				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", MessageFormat.format("Bearer {0}", oAuthCredential.get().getAccessToken()));
				return headers;
			}
			else
			{
				throw new FaxApiConnectionException("Missing oAuth credentials. Log in and try again");
			}
		}
		catch (IOException e)
		{
			throw new FaxApiConnectionException("Error loading access token:\n" + e.getMessage());
		}
	}
}