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
package org.oscarehr.ws.rest.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.oauth.data.OAuthContext;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.RestServiceLog;
import org.oscarehr.util.MiscUtils;
import oscar.log.LogAction;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

@Priority(Priorities.USER)
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
	private static Logger logger = MiscUtils.getLogger();

	private static final String PROP_REQUEST_BODY = "LoggingFilter.requestBody";
	private static final String PROP_REQUEST_PROVIDER = "LoggingFilter.requestProviderNo";
	private static final String PROP_REQUEST_DATETIME = "LoggingFilter.requestDateTime";

	public static final String PROP_SKIP_LOGGING = "LoggingFilter.doNotLog";

	@Context
	private ContextResolver<ObjectMapper> mapperResolver;

	@Context
	private HttpServletRequest httpRequest;

	@Context
	private MessageContext messageContext;

	/** Request filter
	 * This collects data that is only available in the request filter and stores it in the
	 * request properties.
	 */
	public void filter(ContainerRequestContext request)
	{
		OAuthContext oAuthContext = messageContext.getContent(OAuthContext.class);

		if(oAuthContext != null && oAuthContext.getSubject() != null)
		{
			request.setProperty(PROP_REQUEST_PROVIDER, oAuthContext.getSubject().getLogin());
		}

		// Get the message body and put it in a property
		String body = null;

		if(request.hasEntity())
		{
			body = readEntityStream(request);
		}

		request.setProperty(PROP_REQUEST_BODY, body);
		request.setProperty(PROP_REQUEST_DATETIME, new Date());
	}

	/**
	 * Response filter
	 * Collects the data for the log entry and logs it
	 */
	public void filter(ContainerRequestContext request, ContainerResponseContext response)
	{
		// some things should not be logged, if they set this property, we deliberatly skip the logging process
		Boolean skipLogging = (Boolean) request.getProperty(PROP_SKIP_LOGGING);
		if(skipLogging != null && skipLogging)
		{
			return;
		}

		String providerNo = (String) request.getProperty(PROP_REQUEST_PROVIDER);
		String rawPostData = (String) request.getProperty(PROP_REQUEST_BODY);
		Date requestDateTime = (Date) request.getProperty(PROP_REQUEST_DATETIME);
		String rawResponseData = null;
		long duration = 0L;

		UriInfo uriInfo = request.getUriInfo();
		String url = null;
		String queryString = null;

		if(requestDateTime != null)
		{
			duration = new Date().getTime() - requestDateTime.getTime();
		}
		else
		{
			logger.warn("request start date is missing");
		}

		if(uriInfo != null)
		{
			try
			{
				url = uriInfo.getRequestUri().toURL().toString();
				queryString = uriInfo.getRequestUri().getQuery();
			}
			catch(MalformedURLException e)
			{
				logger.error("Malformed URL", e);
			}
		}

		if(response.getEntity() != null)
		{
			Object entity = response.getEntity();
			final ObjectMapper objectMapper = mapperResolver.getContext(Object.class);

			try
			{
				rawResponseData = objectMapper.writeValueAsString(entity);
			}
			catch(Exception e)
			{
				logger.error("Error writing API response as JSON", e);
			}
		}

		try
		{
			RestServiceLog restLog = new RestServiceLog();

			restLog.setProviderNo(providerNo);
			restLog.setIp(httpRequest.getRemoteAddr());
			restLog.setUserAgent(request.getHeaderString("User-Agent"));
			restLog.setUrl(url);
			restLog.setMethod(request.getMethod());
			restLog.setRequestMediaType(String.valueOf(request.getMediaType()));
			restLog.setRawQueryString(queryString);
			restLog.setRawPost(rawPostData);

			restLog.setStatusCode(response.getStatus());
			restLog.setRawOutput(rawResponseData);
			restLog.setDuration(duration);
			restLog.setResponseMediaType(String.valueOf(response.getMediaType()));

			LogAction.saveRestLogEntry(restLog);
		}
		catch(Exception e)
		{
			logger.error("Failed to save REST Log Entry", e);
		}
	}


	private String readEntityStream(ContainerRequestContext requestContext)
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		final InputStream inputStream = requestContext.getEntityStream();
		final StringBuilder builder = new StringBuilder();
		try
		{
			IOUtils.copy(inputStream, outStream);
			byte[] requestEntity = outStream.toByteArray();

			if (requestEntity.length != 0)
			{
				builder.append(new String(requestEntity));
			}
			requestContext.setEntityStream(new ByteArrayInputStream(requestEntity) );
		}
		catch (IOException e)
		{
			logger.debug("Error reading input stream for logging",e);
		}

		return builder.toString();
	}
}
