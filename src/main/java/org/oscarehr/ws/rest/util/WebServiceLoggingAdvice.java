/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest.util;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.stereotype.Component;

import oscar.log.LogConst;


// TODO Consider moving this configuration into XML
// TODO Consider dropping this in favour of POJO aspects
// This needs a bit of work. The output looks like
//  "2013-04-25 14:44:34,884 INFO  [WebServiceLoggingAdvice:66] Logging access for execution(OscarSearchResponse org.oscarehr.ws.rest.PharmacyService.getPharmacies(Integer,Integer))"
// which isn't very informative..but it's a first stab.


@Aspect
@Component
public class WebServiceLoggingAdvice {

	private static Logger logger = Logger.getLogger(WebServiceLoggingAdvice.class);
	
	@Pointcut("execution(public * org.oscarehr.ws.rest.*.*(..))")
	public void pointcut() {
		logger.info("called pointcut");
	}
	
	private String getServiceCallDescription(ProceedingJoinPoint joinpoint) {
		Signature signature = joinpoint.getSignature();
		String type = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		return type + "." + methodName;
	}
	
	@Around("execution(public * org.oscarehr.ws.rest.*.*(..))")
	public Object logAccess(ProceedingJoinPoint joinpoint) throws Throwable {
		if (logger.isInfoEnabled()) {
			logger.info("Logging access for " + joinpoint);
		}
		Object result = null;
		String status = LogConst.STATUS_SUCCESS;

		try {
			long duration = System.currentTimeMillis();
			result = joinpoint.proceed();
			duration = System.currentTimeMillis() - duration;
		}
		catch (Throwable t) {
			logger.error("WS Failure", t);
			status = LogConst.STATUS_FAILURE;
			throw t;
		}
		finally {
			logAccess(joinpoint, status);
		}
		return result;
	}
	private void logAccess(ProceedingJoinPoint joinpoint, String satus) {
		Message currentMessage = PhaseInterceptorChain.getCurrentMessage();
		HttpServletRequest request = (HttpServletRequest) currentMessage.get("HTTP.REQUEST");
		
		Signature signature = joinpoint.getSignature();
		String type = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		
		@SuppressWarnings("unchecked")
		Map<String,String[]> params = request.getParameterMap();
		
		String url = request.getRequestURL().toString();
		
		//TODO log all rest service calls to their own log
		logger.info("REST WS: " + getServiceCallDescription(joinpoint) + "("+url+"): " + printableParameterMap(params));
		
	}
	/** display a map with values in a readable way */
	private String printableParameterMap(Map<?, ?> map) {
		String printable = "{";
	
		for(Object key: map.keySet())
	    {
	            String keyStr = (String)key;
	            String[] value = (String[])map.get(keyStr);
	            printable += keyStr + "=" + Arrays.toString(value) + ",";
	    }
		printable += "}";
		return printable;
	}
}
