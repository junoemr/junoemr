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

/**
 * Service for logging Web service data
 * @author robert
 *
 */
@Aspect
@Component
public class WebServiceLoggingAdvice {

	private static Logger logger = Logger.getLogger(WebServiceLoggingAdvice.class);
	
	@Pointcut("execution(public * org.oscarehr.ws.rest.*.*(..))")
	public void pointcut() {
		logger.info("called pointcut");
	}

	/**
	 * this wraps all rest ws calls and logs them
	 * @param joinpoint
	 * @return Object
	 * @throws Throwable
	 */
	@Around("execution(public * org.oscarehr.ws.rest.*.*(..))")
	public Object logAccess(ProceedingJoinPoint joinpoint) throws Throwable {

		Object result = null;
		long duration = 0;

		try {
			duration = System.currentTimeMillis();
			result = joinpoint.proceed();
			duration = System.currentTimeMillis() - duration;
		}
		catch (Throwable t) {
			logger.error("WS Failure", t);
			duration = 0;
			throw t;
		}
		finally {
			logAccess(joinpoint, result, duration);
		}
		return result;
	}
	private void logAccess(ProceedingJoinPoint joinpoint, Object response, long duration) {
		
		Signature signature = joinpoint.getSignature();
		String type = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();
		
		Message currentMessage = PhaseInterceptorChain.getCurrentMessage();
		HttpServletRequest request = (HttpServletRequest) currentMessage.get("HTTP.REQUEST");
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		
		//LogAction.addRestLogEntry(request, providerNo, type, methodName, response.toString(), duration);
		logger.info("REST WS: " + type + "." + methodName);
		
	}
}
