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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.integration.ringcentral.api.input.RingCentralSendFaxInput;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralMessageInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralSendFaxResult;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import oscar.util.RESTClient;

import java.io.IOException;
import java.text.MessageFormat;
import org.oscarehr.fax.oauth.RingCentralCredentialStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public RingCentralSendFaxResult sendFax(String accountId, String extensionId, RingCentralSendFaxInput input) throws IOException
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/fax";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId));

		GenericFile attachment = input.getAttachment();
		Gson gson = new GsonBuilder().create();

		HttpHeaders headers = getAuthorizationHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

		FileSystemResource fileSystemResource = new FileSystemResource(attachment.getFileObject());

		//todo add other fields and maybe move this to the input somehow? can we construct from object using annotations?
		body.add("attachment", fileSystemResource);
		body.add("to", gson.toJson(input.getTo()));

		return doPost(url, headers, body, RingCentralSendFaxResult.class);
	}

	public RingCentralMessageInfoResult getMessage(String accountId, String extensionId, String messageId)
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/message-store/{2}";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId, messageId));
		return doGet(url, getAuthorizationHeaders(), RingCentralMessageInfoResult.class);
	}

	public String getMessageContent(String accountId, String extensionId, String messageId, String attachmentId)
	{
		String endpoint = REST_API_BASE + "account/{0}/extension/{1}/message-store/{2}/content/{3}";
		String url = buildUrl(DEFAULT_PROTOCOL, MessageFormat.format(endpoint, accountId, extensionId, messageId, attachmentId));
		return doGet(url, getAuthorizationHeaders(), String.class);
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