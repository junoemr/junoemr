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

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.exception.FaxIntegrationException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.result.FaxStatusResult;
import org.oscarehr.fax.service.FaxUploadService;
import org.oscarehr.integration.ringcentral.api.RingcentralApiConnector;
import org.oscarehr.integration.ringcentral.api.input.RingCentralSendFaxInput;
import org.oscarehr.integration.ringcentral.api.result.RingCentralSendFaxResult;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

public class RingCentralUploadProvider implements FaxUploadProvider
{
	protected FaxAccount faxAccount;
	protected RingcentralApiConnector ringcentralApiConnector = SpringUtils.getBean(RingcentralApiConnector.class); //todo how to access in pojo?

	public RingCentralUploadProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}

	@Override
	public FaxOutbound sendQueuedFax(FaxOutbound faxOutbound, GenericFile file) throws Exception
	{
		RingCentralSendFaxInput input = new RingCentralSendFaxInput();
		input.setAttachment(file);
		input.setTo(new String[]{faxOutbound.getSentTo()});

		try
		{
			RingCentralSendFaxResult result = ringcentralApiConnector.sendFax(faxAccount.getLoginId(), "~", input);
			faxOutbound.setStatusSent();
			faxOutbound.setStatusMessage(FaxUploadService.STATUS_MESSAGE_IN_TRANSIT);
			faxOutbound.setExternalStatus(result.getMessageStatus().name());
			faxOutbound.setExternalReferenceId(result.getId());
		}
		catch(RestClientResponseException e)
		{
			throw new FaxIntegrationException(e.getMessage());
		}
		return faxOutbound;
	}

	@Override
	public List<String> getRemoteFinalStatusIndicators()
	{
		return RingcentralApiConnector.RESPONSE_STATUSES_FINAL;
	}

	@Override
	public boolean isFaxInRemoteSentState(String externalStatus)
	{
		return RingcentralApiConnector.RESPONSE_STATUS_DELIVERED.equals(externalStatus);
	}

	@Override
	public boolean isFaxInRemoteFailedState(String externalStatus)
	{
		return RingcentralApiConnector.RESPONSE_STATUSES_FAILED.contains(externalStatus);
	}

	@Override
	public FaxStatusResult getFaxStatus(FaxOutbound faxOutbound) throws Exception
	{
		return ringcentralApiConnector.getMessage(faxAccount.getLoginId(), "~", String.valueOf(faxOutbound.getExternalReferenceId()));
	}
}