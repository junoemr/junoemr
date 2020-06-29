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
package oscar.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@SuppressWarnings("unchecked")
public class RESTClient
{
	private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	public RESTClient()
	{

	}

	public RESTClient(ResponseErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	// ---------------------------- HTTP POST ----------------------------------------- //
	public <U, T> T doPost(String url, HttpHeaders headers, U body, Class<T> responseClass)
	{
		return executeRequest(url, HttpMethod.POST, headers, null, body, responseClass);
	}

	public <U, T> T doPost(String url, HttpHeaders headers, Map<String, Object> queryParams, U body, Class<T> responseClass)
	{
		return executeRequest(url, HttpMethod.POST, headers, queryParams, body, responseClass);
	}

	// ---------------------------- HTTP GET ----------------------------------------- //
	public <T> T doGet(String url, HttpHeaders headers, Class<T> responseClass)
	{
		return executeRequest(url, HttpMethod.GET, headers, null, null, responseClass);
	}

	public <T> T doGet(String url, HttpHeaders headers, Map<String, Object> queryParams, Class<T> responseClass)
	{
		return executeRequest(url, HttpMethod.GET, headers, queryParams, null, responseClass);
	}

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
	protected <S, T, U> T executeRequest(
					String url,
					HttpMethod method,
					HttpHeaders headers,
					Map<String, Object> queryParams,
					S body,
					Class<T> responseClass)
	{
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(messageConverter);
		restTemplate.setErrorHandler(errorHandler);
		HttpEntity<S> request = new HttpEntity<S>(body, headers);

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
		uriBuilder = setQueryParams(uriBuilder, queryParams);

		ResponseEntity<T> response = restTemplate.exchange(uriBuilder.build().toUriString(), method, request, responseClass);
		return response.getBody();
	}

	public ResponseErrorHandler getErrorHandler()
	{
		return errorHandler;
	}

	public void setErrorHandler(ResponseErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	/**
	 * set query params contained within map on to the uriBuilder.
	 * @param uriBuilder - the uri builder on which the parameters are set
	 * @param queryParams - the query parameters to set
	 * @return - the uri builder with the query parameters set
	 */
	private UriComponentsBuilder setQueryParams(UriComponentsBuilder uriBuilder, Map<String, Object> queryParams)
	{
		if (queryParams != null)
		{
			for (Map.Entry<String, Object> param : queryParams.entrySet())
			{
				uriBuilder.queryParam(param.getKey(), param.getValue());
			}
		}
		return uriBuilder;
	}
}
