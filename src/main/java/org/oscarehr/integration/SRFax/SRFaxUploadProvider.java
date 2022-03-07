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

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxIntegrationException;
import org.oscarehr.fax.result.FaxStatusResult;
import org.oscarehr.integration.SRFax.api.SRFaxApiConnector;
import org.oscarehr.integration.SRFax.api.result.SRFaxFaxStatusResult;
import org.oscarehr.integration.SRFax.api.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.service.FaxUploadService;
import java.util.HashMap;
import java.util.List;

public class SRFaxUploadProvider implements FaxUploadProvider
{
	private SRFaxApiConnector makeApiConnector(FaxOutbound fax) throws FaxIntegrationException
	{
		FaxAccount faxAccount = fax.getFaxAccount();

		if (!faxAccount.getIntegrationType().equals(FaxProvider.SRFAX))
		{
			throw new FaxIntegrationException("SRFax provider is processing non-SRFAX outbound fax");
		}

		return new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());
	}


	@Override
	public FaxOutbound sendQueuedFax(FaxOutbound faxOutbound, GenericFile file) throws Exception
	{
		SRFaxApiConnector apiConnector = makeApiConnector(faxOutbound);
		FaxAccount faxAccount = faxOutbound.getFaxAccount();

		String coverLetterOption = faxAccount.getCoverLetterOption();
		if(coverLetterOption == null || !SRFaxApiConnector.validCoverLetterNames.contains(coverLetterOption))
		{
			coverLetterOption = null;
		}

		HashMap<String, String> fileMap = new HashMap<>(1);
		fileMap.put(file.getName(), file.toBase64());

		// external api call
		SingleWrapper<Integer> resultWrapper = apiConnector.queueFax(
			faxAccount.getReplyFaxNumber(),
			faxAccount.getEmail(),
			faxOutbound.getSentTo(),
			fileMap,
			coverLetterOption
		);

		if (resultWrapper.isSuccess())
		{
			faxOutbound.setStatusSent();
			faxOutbound.setStatusMessage(FaxUploadService.STATUS_MESSAGE_IN_TRANSIT);
			faxOutbound.setExternalReferenceId(resultWrapper.getResult().longValue());
		}
		else
		{
			throw new FaxIntegrationException(resultWrapper.getError());
		}

		return faxOutbound;
	}

	@Override
	public List<String> getRemoteFinalStatusIndicators()
	{
		return SRFaxApiConnector.RESPONSE_STATUSES_FINAL;
	}

	@Override
	public boolean isFaxInRemoteSentState(FaxStatusResult result)
	{
		return result.getRemoteSentStatus().equalsIgnoreCase(SRFaxApiConnector.RESPONSE_STATUS_SENT);
	}

	@Override
	public boolean isFaxInRemoteFailedState(FaxOutbound faxOutbound)
	{
		return faxOutbound.getExternalStatus().equalsIgnoreCase(SRFaxApiConnector.RESPONSE_STATUS_FAILED);
	}


	@Override
	public FaxStatusResult getFaxStatus(FaxOutbound faxOutbound) throws Exception
	{
		SRFaxApiConnector apiConnector = makeApiConnector(faxOutbound);
		SingleWrapper<SRFaxFaxStatusResult> apiResult = apiConnector.getFaxStatus(String.valueOf(faxOutbound.getExternalReferenceId()));

		if (!apiResult.isSuccess())
		{
			throw new FaxApiConnectionException(apiResult.getError());
		}

		return apiResult.getResult();
	}
}