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
package org.oscarehr.integration.aqs.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import oscar.OscarProperties;
import oscar.util.RESTClient;

import java.util.Map;

public class BaseService extends org.oscarehr.integration.BaseService
{
	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected final String AQS_PROTOCOL = oscarProps.getProperty("aqs_protocol");
	protected final String AQS_DOMAIN = oscarProps.getProperty("aqs_domain");
	protected final String BASE_API_URI = oscarProps.getProperty("aqs_api_uri");
	protected final String BASE_END_POINT = concatEndpointStrings(AQS_DOMAIN, BASE_API_URI);

	private final RESTClient restClient = new RESTClient();

	/**
	 * build an endpoint url. the BASE AQS url is prepended.
	 * @param endpoint - the endpoint to hit
	 * @return - the complete url
	 */
	public String formatEndpoint(String endpoint)
	{
		return concatEndpointStrings(BASE_END_POINT, endpoint);
	}

	/**
	 * perform a get request on the AQS server
	 * @param url - url to hit. use formatEndpoint to generate this
	 * @param headers - headers to send with the request
	 * @param queryParams - query params for the request
	 * @param responseClass - response class
	 * @param <T> - type of response class
	 * @return - the response as parse by response class
	 */
	public <T> T doGet(String url, HttpHeaders headers, Map<String, Object> queryParams, Class<T> responseClass)
	{
		headers = this.httpHeadersForRequest(headers, null, null);
		return restClient.doGet(url, headers, queryParams, responseClass);
	}

	// just like doGet by sets the X-Auth-Token header
	public <T> T doGetWithToken(String url, String token, HttpHeaders headers, Map<String, Object> queryParams, Class<T> responseClass)
	{
		headers = this.httpHeadersForRequest(headers, token, null);
		return restClient.doGet(url, headers, queryParams, responseClass);
	}

	/**
	 * perform a post request on the AQS server
	 * @param url - url to hit. Use formatEndpoint to generate this
	 * @param headers - headers to send with the request
	 * @param queryParams - map of query params to send
	 * @param body - Jax object to use as request body
	 * @param responseClass - response object
	 * @param <U> - type of body
	 * @param <T> - type of response
	 * @return - response object
	 */
	public <U, T> T doPost(String url, HttpHeaders headers, Map<String, Object> queryParams, U body, Class<T> responseClass)
	{
		headers = this.httpHeadersForRequest(headers, null, null);
		return restClient.doPost(url, headers, queryParams, body, responseClass);
	}

	// Like doPost but with an token param. This is sent in the X-Auth-Token header
	public <U, T> T doPostWithToken(String url, String token, HttpHeaders headers, Map<String, Object> queryParams, U body, Class<T> responseClass)
	{
		headers = this.httpHeadersForRequest(headers, token, null);
		return restClient.doPost(url, headers, queryParams, body, responseClass);
	}

	public RESTClient getRestClient()
	{
		return restClient;
	}

	/**
	 * prepare / create headers for request
	 * @param headers - headers object to add to. Can be NULL. If Null the object is created
	 * @param token - auth token. Can be NULL.
	 * @param apiKey - api key. Can be NULL
	 * @return - HTTP headers
	 */
	protected HttpHeaders httpHeadersForRequest(HttpHeaders headers, String token, String apiKey)
	{
		if (headers == null)
		{
			headers = new HttpHeaders();
		}
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (token != null)
		{
			headers.set("X-Auth-token", token);
		}
		if (apiKey != null)
		{
			headers.set("X-API-Key", apiKey);
		}

		return headers;
	}
}
