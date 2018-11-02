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

import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxSettingsTransferInbound;
import org.oscarehr.ws.rest.transfer.fax.FaxSettingsTransferOutbound;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

public class FaxTransferConverter
{
	public static FaxAccount getAsDomainObject(FaxSettingsTransferInbound transfer)
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

	public static FaxSettingsTransferOutbound getAsOutboundTransferObject(FaxAccount config)
	{
		FaxSettingsTransferOutbound transfer = new FaxSettingsTransferOutbound();
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

	public static List<FaxSettingsTransferOutbound> getAllAsOutboundTransferObject(List<FaxAccount> configList)
	{
		List<FaxSettingsTransferOutbound> transferList = new ArrayList<>(configList.size());
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
		transfer.setDemographicNo(faxOutbound.getDemographicNo());
		transfer.setSystemStatus(String.valueOf(faxOutbound.getStatus()));
		transfer.setSystemDateSent(ConversionUtils.toTimestampString(faxOutbound.getCreatedAt()));
		transfer.setToFaxNumber(faxOutbound.getSentTo());
		transfer.setFileType(faxOutbound.getFileType().name());

		return transfer;
	}
}
