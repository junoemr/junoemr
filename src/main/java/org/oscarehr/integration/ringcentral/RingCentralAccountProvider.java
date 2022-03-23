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

import org.oscarehr.fax.exception.FaxIntegrationException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.integration.ringcentral.api.RingCentralApiConnector;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralCoverLetterListResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralCoverLetterResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RingCentralAccountProvider implements FaxAccountProvider
{
	protected final FaxAccount faxAccount;
	protected final RingCentralApiConnector ringcentralApiConnector;

	public RingCentralAccountProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
		this.ringcentralApiConnector = new RingCentralApiConnector();
	}

	@Override
	public boolean testConnectionStatus()
	{
		RingCentralAccountInfoResult accountInfo = ringcentralApiConnector.getAccountInfo(faxAccount.getLoginId());
		return (accountInfo != null);
	}

	@Override
	public List<String> getCoverLetterOptions()
	{
		RingCentralCoverLetterListResult result = ringcentralApiConnector.getFaxCoverPageList();
		return Arrays.stream(result.getRecords())
				.map(RingCentralCoverLetterResult::getName)
				.collect(Collectors.toList());
	}

	@Override
	public void disconnectAccount()
	{
		try
		{
			ringcentralApiConnector.revokeCredential();
		}
		catch(IOException e)
		{
			throw new FaxIntegrationException("Error revoking oAuth token: " + e.getMessage());
		}
	}
}