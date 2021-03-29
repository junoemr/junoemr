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
package org.oscarehr.ws.rest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.oscarehr.common.model.Provider;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Base class for RESTful web services
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class AbstractServiceImpl {

	@Autowired
	protected SecurityInfoManager securityInfoManager;

	private static final int MAX_PAGE_RESULTS = 100;

	protected HttpServletRequest getHttpServletRequest()
	{
		Message message = PhaseInterceptorChain.getCurrentMessage();
	    HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
	    return(request);
	}
	
	/**
	 * Gets the client's locale
	 * 
	 * @return Locale
	 */
	protected Locale getLocale()
	{
		return getHttpServletRequest().getLocale();
	}
	
	/**
	 * 
	 * Get the UI resource bundle for locale specific messages
	 * 
	 * @return ResourceBundle
	 */
	protected ResourceBundle getResourceBundle() 
	{
		return ResourceBundle.getBundle("uiResources", getLocale());
	}

	/**
	 * Get the oscar resources bundle i.e. OscarResources_en.properties
	 * @return - the oscar resources bundle for this local
	 */
	protected ResourceBundle getOscarResourcesBundle()
	{
		return ResourceBundle.getBundle("oscarResources", getLocale());
	}

	/**
	 * Gets current provider.
	 * 
	 * @return
	 * 		Returns the provider authenticated for the current request processing. 
	 */
	protected Provider getCurrentProvider() {
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		return (loggedInInfo.getLoggedInProvider());
	}

	/**
	 * ensure the requested result limit does not exceed the maximum
	 * @param paramResultsPerPage - proposed limit
	 * @param maximum - maximum result limit
	 * @return - the new limit
	 */
	protected int limitedResultCount(int paramResultsPerPage, int maximum)
	{
		return (maximum < paramResultsPerPage) ? maximum : paramResultsPerPage;
	}
	protected int limitedResultCount(int paramResultsPerPage)
	{
		return limitedResultCount(paramResultsPerPage, MAX_PAGE_RESULTS);
	}

	/**
	 * ensure the page number is valid (non negative)
	 * @param pageNo proposed page number
	 * @return valid page number
	 */
	protected int validPageNo(int pageNo)
	{
		if(pageNo < 1) pageNo = 1;
		return pageNo;
	}
	/**
	 * calculate the offset based on the current page number and resultCount
	 * @param pageNo - page
	 * @param resultsPerPage - limit of results
	 * @return offset
	 */
	protected int calculatedOffset(int pageNo, int resultsPerPage)
	{
		return resultsPerPage * (pageNo - 1);
	}

	protected boolean parametersNotAllNull(Object... parameters)
	{
		for(Object parameter : parameters)
		{
			if(parameter != null)
			{
				return true;
			}
		}
		return false;
	}
	protected boolean parametersAllNull(Object... parameters)
	{
		return !parametersNotAllNull(parameters);
	}

	/**
	 * Gets the login information associated with the current request.
	 * 
	 * @return
	 * 		Returns the login information
	 * 
	 * @throws IllegalStateException
	 * 		IllegalStateException is thrown in case authentication info is not available 
	 */
	protected LoggedInInfo getLoggedInInfo() {

		Message message = PhaseInterceptorChain.getCurrentMessage();
	    HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
	    
	    LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromSession(request);
		if (info == null) {
			throw new IllegalStateException("Authentication info is not available.");
		}
		return info;
	}

	/**
	 * @return the current logged in provider id based on the loggedInInfo
	 */
	protected String getLoggedInProviderId()
	{
		return getLoggedInInfo().getLoggedInProviderNo();
	}
}
