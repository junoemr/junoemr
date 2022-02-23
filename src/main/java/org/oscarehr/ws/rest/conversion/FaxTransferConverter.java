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
package org.oscarehr.ws.rest.conversion;

import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxInbound;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.transfer.FaxAccountCreateInput;
import org.oscarehr.fax.transfer.FaxAccountTransferOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxInboxTransferOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

public class FaxTransferConverter
{
	public static FaxAccount getAsDomainObject(FaxAccountCreateInput transfer)
	{
		FaxAccount config = new FaxAccount();
		config.setIntegrationEnabled(transfer.isEnabled());
		config.setLoginPassword(transfer.getPassword());
		config.setLoginId(transfer.getAccountLogin());
		config.setInboundEnabled(transfer.isEnableInbound());
		config.setOutboundEnabled(transfer.isEnableOutbound());
		config.setDisplayName(transfer.getDisplayName());
		config.setCoverLetterOption(transfer.getCoverLetterOption());
		config.setEmail(transfer.getAccountEmail());
		config.setReplyFaxNumber(transfer.getFaxNumber());
		return config;
	}

	public static FaxAccountTransferOutbound getAsOutboundTransferObject(FaxAccount config)
	{
		FaxAccountTransferOutbound transfer = new FaxAccountTransferOutbound();
		transfer.setId(config.getId());
		transfer.setAccountLogin(config.getLoginId());
		transfer.setEnabled(config.isIntegrationEnabled());
		transfer.setEnableInbound(config.isInboundEnabled());
		transfer.setEnableOutbound(config.isOutoundEnabled());
		transfer.setDisplayName(config.getDisplayName());
		transfer.setCoverLetterOption(config.getCoverLetterOption());
		transfer.setFaxNumber(config.getReplyFaxNumber());
		transfer.setAccountEmail(config.getEmail());
		return transfer;
	}

	public static List<FaxAccountTransferOutbound> getAllAsOutboundTransferObject(List<FaxAccount> configList)
	{
		List<FaxAccountTransferOutbound> transferList = new ArrayList<>(configList.size());
		for(FaxAccount config : configList)
		{
			transferList.add(getAsOutboundTransferObject(config));
		}
		return transferList;
	}

	public static FaxOutboxTransferOutbound getAsOutboxTransferObject(FaxAccount faxAccount, FaxOutbound faxOutbound)
	{
		FaxOutboxTransferOutbound transfer = new FaxOutboxTransferOutbound();
		transfer.setId(faxOutbound.getId());
		transfer.setFaxAccountId(faxAccount.getId());
		transfer.setProviderNo(faxOutbound.getProviderNo());
		transfer.setProviderName(faxOutbound.getProvider().getDisplayName());
		transfer.setDemographicNo(faxOutbound.getDemographicNo());
		transfer.setSystemStatus(faxOutbound.getStatus());
		transfer.setSystemStatusMessage(faxOutbound.getStatusMessage());
		transfer.setArchived(faxOutbound.getArchived());
		transfer.setNotificationStatus(faxOutbound.getNotificationStatus().name());
		transfer.setSystemDateSent(ConversionUtils.toTimestampString(faxOutbound.getCreatedAt()));
		transfer.setToFaxNumber(faxOutbound.getSentTo());
		transfer.setFileType(faxOutbound.getFileType().name());
		transfer.setIntegrationStatus(faxOutbound.getExternalStatus());
		transfer.setIntegrationDateSent(ConversionUtils.toTimestampString(faxOutbound.getExternalDeliveryDate()));
		transfer.setCombinedStatus(toCombinedStatus(faxOutbound.getStatus(), faxOutbound.getExternalStatus()));

		return transfer;
	}

	public static List<FaxOutboxTransferOutbound>  getAllAsOutboxTransferObject(FaxAccount faxAccount, List<FaxOutbound> faxOutbound)
	{
		List<FaxOutboxTransferOutbound> transferList = new ArrayList<>(faxOutbound.size());
		for(FaxOutbound fax : faxOutbound)
		{
			transferList.add(getAsOutboxTransferObject(faxAccount, fax));
		}
		return transferList;
	}

	public static FaxInboxTransferOutbound getAsInboxTransferObject(FaxInbound faxInbound)
	{
		FaxInboxTransferOutbound transfer = new FaxInboxTransferOutbound();
		transfer.setId(faxInbound.getId());
		transfer.setDocumentId(faxInbound.getDocument().getDocumentNo());
		transfer.setFaxAccountId(faxInbound.getFaxAccount().getId());
		transfer.setSystemDateReceived(ConversionUtils.toTimestampString(faxInbound.getCreatedAt()));
		transfer.setExternalReferenceId(faxInbound.getExternalReferenceId());
		transfer.setSentFrom(faxInbound.getSentFrom());

		return transfer;
	}

	public static List<FaxInboxTransferOutbound>  getAllAsInboxTransferObject(List<FaxInbound> faxInbound)
	{
		ArrayList<FaxInboxTransferOutbound> transferList = new ArrayList<>(faxInbound.size());
		for(FaxInbound fax : faxInbound)
		{
			transferList.add(getAsInboxTransferObject(fax));
		}
		return transferList;
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
