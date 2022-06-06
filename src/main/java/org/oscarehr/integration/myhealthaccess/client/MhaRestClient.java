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

import org.oscarehr.config.JunoProperties;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;

import java.net.URI;
import java.net.URISyntaxException;

public class MhaRestClient extends RestClientBase
{
	private static final JunoProperties junoProps = SpringUtils.getBean(JunoProperties.class);
	JunoProperties.Myhealthaccess mhaConfig = junoProps.getMyhealthaccess();

	//==========================================================================
	// Public Methods
	//==========================================================================

	public MhaRestClient(Integration integration)
	{
		super(integration);
	}

	@Override
	public URI baseEndpoint()
	{
		OscarProperties oscarProps = OscarProperties.getInstance();
		try
		{
			return new URI(
					oscarProps.getProperty("myhealthaccess_protocol"),
					mhaConfig.getMyhealthaccessDomain(),
					oscarProps.getProperty("myhealthaccess_api_uri"),
					"");
		}
		catch(URISyntaxException e)
		{
			throw new RuntimeException("Error building MHA API URL", e);
		}
	}

	@Override
	public URI getRootURI()
	{
		OscarProperties oscarProps = OscarProperties.getInstance();
		try
		{
			return new URI(
					oscarProps.getProperty("myhealthaccess_protocol"),
					mhaConfig.getMyhealthaccessDomain(),
					null,
					null);
		}
		catch(URISyntaxException e)
		{
			throw new RuntimeException("Error building MHA root uri", e);
		}
	}
}
