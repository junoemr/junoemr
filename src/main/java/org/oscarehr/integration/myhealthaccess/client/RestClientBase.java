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

package org.oscarehr.integration.myhealthaccess.client;

import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.service.DevelopmentTrustManager;
import org.oscarehr.util.MiscUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import oscar.OscarProperties;
import oscar.util.RESTClient;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class RestClientBase extends RESTClient
{
	private Integration integration = null;


	//==========================================================================
	// Abstract Methods
	//==========================================================================

	// return base REST endpoint
	public abstract URI baseEndpoint();

	//==========================================================================
	// Public Methods
	//==========================================================================

	public RestClientBase(Integration integration)
	{
		this.integration = integration;
		this.setErrorHandler(new ErrorHandler());
	}

	public String formatEndpoint(String endpoint, Object... args)
	{
		return formatEndpointFull(endpoint, Arrays.asList(args), null);
	}

	/**
	 * generate a url.
	 * @param endpoint - the rest endpoint to hit.
	 * @param pathParams - [optional] path parameters for substitution in to the endpoint path.
	 * @param queryParams - [optional] query parameters
	 * @return - the url
	 */
	public String formatEndpointFull(String endpoint, @Nullable List<Object> pathParams, @Nullable MultiValueMap<String, String> queryParams)
	{
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(baseEndpoint());
		if (pathParams != null)
		{
			uriBuilder.path(String.format(endpoint, pathParams.toArray()));
		}
		else
		{
			uriBuilder.path(endpoint);
		}

		if (queryParams != null)
		{
			uriBuilder.queryParams(queryParams);
		}
		uriBuilder.fragment(null);
		return uriBuilder.build().toUriString();
	}

	public <U, T> T doPostWithToken(String url, String token, U body, Class<T> responseClass)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Auth-Token", token);
		return executeRequest(url, HttpMethod.POST, headers, null, body, responseClass);
	}

	public <T> T doGetWithToken(String url, String token, Class<T> responseClass)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Auth-Token", token);
		return executeRequest(url, HttpMethod.GET, headers, null, null, responseClass);
	}

	//==========================================================================
	// Protected Methods
	//==========================================================================

	/**
	 * make a request to a rest endpoint
	 * @param url - the url of the request
	 * @param method - the method of the request
	 * @param headers - the HTTP headers of the request
	 * @param queryParams - the url query params of the request
	 * @param body - the body of the request
	 * @param responseClass - the object to deserialize in to for the request response
	 * @param <S> - body type
	 * @param <T> - response type
	 * @param <U> - error type
	 * @return - request response object
	 */
	@Override
	protected <S, T, U> T executeRequest(
			String url,
			HttpMethod method,
			HttpHeaders headers,
			Map<String, Object> queryParams,
			S body,
			Class<T> responseClass)
	{
		IgnoreSSLVerifyInDevMode();
		headers = setDefaultHeaders(headers);
		return super.executeRequest(url, method, headers, queryParams, body, responseClass);
	}

	/**
	 * apply default headers to request.
	 * @param headers - headers object to apply defaults to.
	 * @return - modified headers
 	 */
	protected HttpHeaders setDefaultHeaders(HttpHeaders headers)
	{
		if (headers == null)
		{
			headers = new HttpHeaders();
		}

		headers.setContentType(MediaType.APPLICATION_JSON);
		if (headers.get("X-API-Key") == null)
		{
			headers.set("X-API-Key", this.integration.getApiKey());
		}
		return headers;
	}

	//==========================================================================
	// Private Methods
	//==========================================================================

	private void IgnoreSSLVerifyInDevMode()
	{
		if (OscarProperties.getInstance().isPropertyActive("myhealthaccess_dev_mode"))
		{
			try
			{
				SSLContext sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, DevelopmentTrustManager.trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			} catch (Exception e)
			{
				MiscUtils.getLogger().debug("+++++++++++++++++++++++++++++++");
				MiscUtils.getLogger().debug("Error Ignoring SSL verify: " + e.getMessage());
				MiscUtils.getLogger().debug("Error: " + org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e));
				MiscUtils.getLogger().debug("+++++++++++++++++++++++++++++++");
			}
		}
	}

}
