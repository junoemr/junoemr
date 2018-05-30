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
package org.oscarehr.ws.external.rest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.oauth.data.OAuthContext;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.oscarehr.ws.rest.AbstractServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AbstractExternalRestWs extends AbstractServiceImpl
{
	protected OAuthContext getOAuthContext()
	{
		Message m = PhaseInterceptorChain.getCurrentMessage();
		return m.getContent(OAuthContext.class);
	}
	protected String getOAuthProviderNo()
	{
		return getOAuthContext().getSubject().getLogin();
	}

	protected HttpServletRequest getHttpServletRequest()
	{
		Message message = PhaseInterceptorChain.getCurrentMessage();
		return (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
	}
}
