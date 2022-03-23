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

package org.oscarehr.integration.SRFax;

import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.integration.SRFax.api.SRFaxApiConnector;
import org.oscarehr.integration.SRFax.api.result.GetUsageResult;
import org.oscarehr.integration.SRFax.api.resultWrapper.ListWrapper;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class SRFaxAccountProvider implements FaxAccountProvider
{
	protected FaxAccount faxAccount;

	public SRFaxAccountProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}

	@Override
	public boolean testConnectionStatus()
	{
		String accountId = faxAccount.getLoginId();
		String password = faxAccount.getLoginPassword();

		// don't hit the api if username or password are not set
		if(accountId == null || password == null)
		{
			return false;
		}

		SRFaxApiConnector apiConnector = new SRFaxApiConnector(accountId, password);
		String currentDateStr = ConversionUtils.toDateString(LocalDate.now(), SRFaxApiConnector.DATE_FORMAT);

		ListWrapper<GetUsageResult> result = apiConnector.getFaxUsageByRange(currentDateStr, currentDateStr, null);
		return (result != null && result.isSuccess());
	}

	@Override
	public List<String> getCoverLetterOptions()
	{
		return Arrays.asList("None", "Basic", "Standard", "Company", "Personal");
	}

	@Override
	public void disconnectAccount()
	{
	}
}