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
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import lombok.Synchronized;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Lazy
@Component
public class RingcentralApiConnector
{
	private String FAX_CREDENTIALS_DIR = "";

	private DataStoreFactory dataStoreFactory;
	private HttpTransport httpTransport;
	private HttpRequestFactory requestFactory;		// TODO: can this be made each request?

	private Credential credential;

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

	public RingcentralApiConnector()
	{
	}

	@Synchronized
	public DataStoreFactory getDataStoreFactory()
	{
		return this.dataStoreFactory;
	}

	@Synchronized
	public Credential getCredential()
	{
		return this.credential;
	}

	@Synchronized
	public void setCredential(Credential credential)
	{
		this.credential = credential;
	}

	@PostConstruct
	@Synchronized
	public void init() throws Exception
	{
		// TODO: move all this into constructor
		dataStoreFactory = new FileDataStoreFactory(new File(FAX_CREDENTIALS_DIR));
		httpTransport = new NetHttpTransport();

		// TODO: check if credential needs to be initialized by this point
		Credential credential = getCredential();
		requestFactory = httpTransport.createRequestFactory(new HttpRequestInitializer()
		{
			@Override
			public void initialize(HttpRequest httpRequest) throws IOException
			{
				credential.initialize(httpRequest);
				httpRequest.setParser(new JsonObjectParser(new JacksonFactory()));		// Use request factory here to talk to ringcentral
			}
		});

	}
}