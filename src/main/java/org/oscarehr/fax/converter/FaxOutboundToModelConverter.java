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
package org.oscarehr.fax.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.SRFax.api.SRFaxApiConnector;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class FaxOutboundToModelConverter extends AbstractModelConverter<FaxOutbound, FaxOutboxTransferOutbound>
{
	@Override
	public FaxOutboxTransferOutbound convert(FaxOutbound entity)
	{
		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setId(entity.getId());
		model.setFaxAccountId(entity.getFaxAccount().getId());
		model.setProviderNo(entity.getProviderNo());
		model.setProviderName(entity.getProvider().getDisplayName());
		model.setDemographicNo(entity.getDemographicNo());
		model.setSystemStatus(entity.getStatus());
		model.setSystemStatusMessage(entity.getStatusMessage());
		model.setArchived(entity.getArchived());
		model.setNotificationStatus(entity.getNotificationStatus().name());
		model.setSystemDateSent(ConversionUtils.toTimestampString(entity.getCreatedAt()));
		model.setToFaxNumber(entity.getSentTo());
		model.setFileType(entity.getFileType().name());
		model.setIntegrationStatus(entity.getExternalStatus());
		model.setIntegrationDateSent(ConversionUtils.toTimestampString(entity.getExternalDeliveryDate()));
		model.setCombinedStatus(toCombinedStatus(entity.getStatus(), entity.getExternalStatus()));

		return model;
	}

	private static FaxOutboxTransferOutbound.CombinedStatus toCombinedStatus(FaxOutbound.Status systemStatus, String remoteStatus)
	{
		FaxOutboxTransferOutbound.CombinedStatus combinedStatus = null;
		if(FaxOutbound.Status.ERROR.equals(systemStatus))
		{
			combinedStatus = FaxOutboxTransferOutbound.CombinedStatus.ERROR;
		}
		else if(FaxOutbound.Status.QUEUED.equals(systemStatus))
		{
			combinedStatus = FaxOutboxTransferOutbound.CombinedStatus.QUEUED;
		}
		else if(FaxOutbound.Status.SENT.equals(systemStatus)
				&& SRFaxApiConnector.RESPONSE_STATUS_SENT.equalsIgnoreCase(remoteStatus))
		{
			combinedStatus = FaxOutboxTransferOutbound.CombinedStatus.INTEGRATION_SUCCESS;
		}
		else if(FaxOutbound.Status.SENT.equals(systemStatus)
				&& SRFaxApiConnector.RESPONSE_STATUS_FAILED.equalsIgnoreCase(remoteStatus))
		{
			combinedStatus = FaxOutboxTransferOutbound.CombinedStatus.INTEGRATION_FAILED;
		}
		else if(FaxOutbound.Status.SENT.equals(systemStatus))
		{
			combinedStatus = FaxOutboxTransferOutbound.CombinedStatus.IN_PROGRESS;
		}
		return combinedStatus;
	}
}