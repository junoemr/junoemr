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
import org.apache.log4j.Logger;
import org.oscarehr.log.model.RestServiceLog;
import org.oscarehr.metrics.prometheus.service.SystemMetricsService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.common.annotation.MaskParameter;
import org.oscarehr.ws.common.annotation.SkipAllLogging;
import org.oscarehr.ws.common.annotation.SkipContentLoggingInbound;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.log.LogAction;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Priority(Priorities.USER)
public abstract class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter
{
	private static Logger logger = MiscUtils.getLogger();
	private static final String PROP_REQUEST_BODY = "LoggingFilter.requestBody";
	private static final String PROP_REQUEST_DATETIME = "LoggingFilter.requestDateTime";
    private static final String PROP_REQUEST_PROVIDER = "LoggingFilter.requestProviderNo";
	private static final String PROP_SKIP_LOGGING_CONTENT_OUTBOUND = "LoggingFilter.doNotLogContentOutbound";
	public static final String PROP_SKIP_LOGGING = "LoggingFilter.doNotLog";

	@Autowired
	SystemMetricsService systemMetricsService;

	@Context
	ContextResolver<ObjectMapper> mapperResolver;

	@Context
	HttpServletRequest httpRequest;

	@Context
	MessageContext messageContext;

	@Context
	ResourceInfo resourceInfo;

    /**
     * Retrieve the providerNo (ie: the User).  Filters for different REST services should override this method
     * with the their own implementation.
     *
     * @return The providerNo
     */
    protected abstract String getProviderNo();

	/**
     * Request filter
	 *
     * This collects data that is only available in the request filter and stores it in the
	 * request properties.
	 */
	public void filter(ContainerRequestContext request)
	{
	    request.setProperty(PROP_REQUEST_PROVIDER, getProviderNo());

		// Get the message body and put it in a property
		String body = null;

		if(request.hasEntity())
		{
			body = readEntityStream(request);

			SkipContentLoggingInbound skipContentLoggingInbound = resourceInfo.getResourceMethod().getAnnotation(SkipContentLoggingInbound.class);
			SkipAllLogging skipAllLogging = resourceInfo.getResourceMethod().getAnnotation(SkipAllLogging.class);
			MaskParameter filterAnnotation = resourceInfo.getResourceMethod().getAnnotation(MaskParameter.class);

			if (skipAllLogging != null)
			{
				request.setProperty(LoggingFilter.PROP_SKIP_LOGGING, true);
			}
			// if the skip logging inbound annotation exists on the target method, set the body to the dummy value
			else if(skipContentLoggingInbound != null)
			{
				body = SkipContentLoggingInbound.SKIP_CONTENT_LOGGING_INBOUND;
			}
			// if the filter annotation exists on the target method, filter the fields from the request body before logging
			else if(filterAnnotation != null)
			{
				body = removePasswordData(body, filterAnnotation.fields());
			}
		}
		/* allow methods annotated with this custom annotation to skip the outbound content logging step.
		 * This is useful for large responses such as encoded documents & non-json responses */
		SkipContentLoggingOutbound skipContentLoggingOutbound = resourceInfo.getResourceMethod().getAnnotation(SkipContentLoggingOutbound.class);
		if(skipContentLoggingOutbound != null)
		{
			request.setProperty(PROP_SKIP_LOGGING_CONTENT_OUTBOUND, true);
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
		// some things should not be logged, if they set this property, we deliberately skip the logging process
		Boolean skipLogging = (Boolean) request.getProperty(PROP_SKIP_LOGGING);
		Boolean skipContentLoggingOutbound = (Boolean) request.getProperty(PROP_SKIP_LOGGING_CONTENT_OUTBOUND);
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
			systemMetricsService.recordRestApiRequestLatency(duration);
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
			if(skipContentLoggingOutbound == null || !skipContentLoggingOutbound)
			{
				try
				{
					Object entity = response.getEntity();
					final ObjectMapper objectMapper = mapperResolver.getContext(Object.class);

					rawResponseData = objectMapper.writeValueAsString(entity);
				}
				catch(Exception e)
				{
					logger.error("Error writing API response as JSON", e);
				}
			}
			else
			{
				// use dummy data in place of the actual body to signify that the data exists but was not logged
				rawResponseData = SkipContentLoggingOutbound.SKIP_CONTENT_LOGGING_OUTBOUND;
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

	static String removePasswordData(String rawString, String...fields)
	{
		if(rawString == null)
		{
			return null;
		}
		for(String fieldName : fields)
		{
			Pattern p = Pattern.compile("(\\\""+ fieldName +"\\\"\\s*\\:\\s*\\\")((?:\\\\.|[^\\\"])*?)(\\\")");
			Matcher m = p.matcher(rawString);

			rawString = m.replaceAll("$1"+ MaskParameter.MASK+"$3");
		}
		return rawString;
	}
}
