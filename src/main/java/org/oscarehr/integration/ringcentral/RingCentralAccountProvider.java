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

package org.oscarehr.integration.ringcentral;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import org.apache.log4j.Logger;
import org.oscarehr.fax.exception.FaxIntegrationException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxAccountConnectionStatus;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.integration.ringcentral.api.RingCentralApiConnector;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralCoverLetterListResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralCoverLetterResult;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RingCentralAccountProvider implements FaxAccountProvider
{
	protected final Logger logger = MiscUtils.getLogger();
	protected final FaxAccount faxAccount;
	protected final RingCentralApiConnector ringCentralApiConnector;

	public RingCentralAccountProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
		this.ringCentralApiConnector = new RingCentralApiConnector();
	}

	@Override
	public FaxAccountConnectionStatus testConnectionStatus()
	{
		try
		{
			if (!ringCentralApiConnector.getCredential().isPresent())
			{
				// No credential, probably logged out and never logged back in.
				logger.info("RingCentral connection signed out. Reason: missing credential");
				return FaxAccountConnectionStatus.SIGNED_OUT;
			}
			else
			{
				Credential authToken = ringCentralApiConnector.getCredential().get();
				boolean refreshed = authToken.refreshToken();
				if (!refreshed)
				{
					// Could not refresh for some reason that isn't a 4XX response or disk IO
					logger.error("RingCentral connection signed out. Reason: unknown");
					return FaxAccountConnectionStatus.SIGNED_OUT;
				}

				RingCentralAccountInfoResult accountInfo = ringCentralApiConnector.getAccountInfo(faxAccount.getLoginId());
				if (accountInfo != null)
				{
					return FaxAccountConnectionStatus.SUCCESS;
				}

				logger.info("RingCentral connection unknown state. token refreshed but account info is missing");
				return FaxAccountConnectionStatus.UNKNOWN;
			}
		}
		catch (TokenResponseException e)
		{
			// 4XX response to refresh request
			logger.warn("RingCentral connection signed out. Reason: token exception", e);
			return FaxAccountConnectionStatus.SIGNED_OUT;
		}
		catch (IOException e)
		{
			// Probably due to accessing credential store on disk
			logger.error("RingCentral connection failure. Reason: IO exception", e);
			return FaxAccountConnectionStatus.FAILURE;
		}
	}

	@Override
	public List<String> getCoverLetterOptions()
	{
		RingCentralCoverLetterListResult result = ringCentralApiConnector.getFaxCoverPageList();
		return Arrays.stream(result.getRecords())
				.map(RingCentralCoverLetterResult::getName)
				.collect(Collectors.toList());
	}

	@Override
	public void disconnectAccount()
	{
		try
		{
			ringCentralApiConnector.logOut();
		}
		catch(IOException e)
		{
			throw new FaxIntegrationException("Error logging out" + e.getMessage());
		}
	}
}