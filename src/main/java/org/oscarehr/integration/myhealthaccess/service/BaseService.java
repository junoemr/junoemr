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

package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.integration.myhealthaccess.dto.BaseErrorTo1;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import oscar.OscarProperties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

@Service
public class BaseService extends org.oscarehr.integration.BaseService
{
	@Autowired
	IntegrationService integrationService;

	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected final String MYHEALTHACCESS_PROTOCOL = oscarProps.getProperty("myhealthaccess_protocol");
	protected final String MYHEALTHACCESS_DOMAIN = oscarProps.getProperty("myhealthaccess_domain");
	protected final String BASE_API_URI = oscarProps.getProperty("myhealthaccess_api_uri");
	protected final String BASE_END_POINT = concatEndpointStrings(MYHEALTHACCESS_DOMAIN, BASE_API_URI);

	public String buildUrl(String endPoint)
	{
		endPoint = endPoint.replaceAll("http(s)?://", "");
		return MYHEALTHACCESS_PROTOCOL + "://" + endPoint;
	}

	protected <S, T, U> T executeRequest(
			String endPoint, HttpMethod method, HttpHeaders headers, S body,
			Class<T> responseClass, Class<U> errorClass)
	{
		IgnoreSSLVerifyInDevMode();

		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(messageConverter);
		ResponseErrorHandler errorHandler = new ErrorHandler(errorClass);
		restTemplate.setErrorHandler(errorHandler);
		HttpEntity<S> request = new HttpEntity<S>(body, headers);
//		try
//		{
		ResponseEntity<T> response = restTemplate.exchange(
				buildUrl(endPoint),
				method,
				request,
				responseClass);

//		} catch (BaseException e)
//		{
//			String body = e.getResponseBodyAsString();
//			MiscUtils.getLogger().debug("+++++++++++++++++++++++++++++++");
//			MiscUtils.getLogger().debug("Error Ignoring SSL verify: " + e.getMessage());
//			MiscUtils.getLogger().debug("Error: " + org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e));
//			MiscUtils.getLogger().debug("+++++++++++++++++++++++++++++++");
//		}

		return response.getBody();
	}

	protected <S, T, U> T executeRequest(String endPoint, String apiKey, HttpMethod method, S body,
									  Class<T> responseClass, Class<U> errorClass)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-API-Key", apiKey);
		return executeRequest(endPoint, method, headers, body, responseClass, errorClass);
	}

	protected <S, T, U> T executeRequestWithToken(String endPoint, String apiKey, HttpMethod method,
												  String token, S body, Class<T> responseClass, Class<U> errorClass)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-API-Key", apiKey);
		headers.set("X-Auth-Token", token);

		return executeRequest(endPoint, method, headers, body, responseClass, errorClass);
	}

	protected <T, U> T executeRequest(String endPoint, String apiKey, HttpMethod method,
								   Class<T> responseClass, Class<U> errorClass)
	{
		return executeRequest(endPoint, apiKey, method, null, responseClass, errorClass);
	}

	/*
	 * Helper Methods
	 */
	protected String formatEndpoint(String endpoint, Object... args)
	{
		return concatEndpointStrings(BASE_END_POINT, String.format(endpoint, args));
	}

	protected <S, T> T get(String endPoint, String apiKey, Class<T> responseClass)
	{
		return executeRequest(endPoint, apiKey, HttpMethod.GET, null, responseClass, BaseErrorTo1.class);
	}

	protected <S, T> T getWithToken(String endPoint, String apiKey, S body, Class<T> responseClass, String token)
	{
		return executeRequestWithToken(endPoint, apiKey, HttpMethod.GET, token, body, responseClass, BaseErrorTo1.class);
	}

	protected <S, T> T post(String endPoint, String apiKey, S body, Class<T> responseClass)
	{
		return executeRequest(endPoint, apiKey, HttpMethod.POST, body, responseClass, BaseErrorTo1.class);
	}

	protected <S, T> T put(String endPoint, String apiKey, S body, Class<T> responseClass)
	{
		return executeRequest(endPoint, apiKey, HttpMethod.PUT, body, responseClass, BaseErrorTo1.class);
	}

	protected <S, T> T postWithToken(String endPoint, String apiKey, S body, Class<T> responseClass, String token)
	{
		return executeRequestWithToken(endPoint, apiKey, HttpMethod.POST, token, body, responseClass, BaseErrorTo1.class);
	}

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
