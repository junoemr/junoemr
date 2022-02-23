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
import org.oscarehr.fax.exception.FaxIntegrationException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.service.OutgoingFaxService;
import java.util.HashMap;

public class SRFaxUploadProvider implements FaxUploadProvider
{
	@Override
	public boolean sendQueuedFax(FaxOutbound faxOutbound, GenericFile file) throws Exception
	{
		FaxAccount faxAccount = faxOutbound.getFaxAccount();

		if (!faxAccount.getIntegrationType().equals(FaxProvider.SRFAX))
		{
			throw new FaxIntegrationException("SRFax provider is processing non-SRFAX outbound fax");
		}

		SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());

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

		boolean success = resultWrapper.isSuccess();
		if (success)
		{
			faxOutbound.setStatusSent();
			faxOutbound.setStatusMessage(OutgoingFaxService.STATUS_MESSAGE_IN_TRANSIT);
			faxOutbound.setExternalReferenceId(resultWrapper.getResult().longValue());
		}
		else
		{
			faxOutbound.setStatusError();
			faxOutbound.setStatusMessage(resultWrapper.getError());
		}

		return success;
	}
}