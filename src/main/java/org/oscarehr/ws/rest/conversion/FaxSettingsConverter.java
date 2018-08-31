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

import org.oscarehr.fax.model.FaxConfig;
import org.oscarehr.ws.rest.transfer.fax.FaxSettingsTransferInbound;
import org.oscarehr.ws.rest.transfer.fax.FaxSettingsTransferOutbound;

import java.util.ArrayList;
import java.util.List;

public class FaxSettingsConverter
{
	public static FaxConfig getAsDomainObject(FaxSettingsTransferInbound transfer)
	{
		FaxConfig config = new FaxConfig();
		config.setActive(transfer.isEnabled());
		config.setFaxPasswd(transfer.getPassword());
		config.setFaxUser(transfer.getAccountLogin());
		config.setActiveInbound(transfer.isEnableInbound());
		config.setActiveOutbound(transfer.isEnableOutbound());
		config.setDisplayName(transfer.getDisplayName());
		config.setCoverLetterOption(transfer.getCoverLetterOption());
		return config;
	}

	public static FaxSettingsTransferOutbound getAsOutboundTransferObject(FaxConfig config)
	{
		FaxSettingsTransferOutbound transfer = new FaxSettingsTransferOutbound();
		transfer.setId(config.getId());
		transfer.setAccountLogin(config.getFaxUser());
		transfer.setEnabled(config.isActive());
		transfer.setEnableInbound(config.isActiveInbound());
		transfer.setEnableOutbound(config.isActiveOutbound());
		transfer.setDisplayName(config.getDisplayName());
		transfer.setCoverLetterOption(config.getCoverLetterOption());
		return transfer;
	}

	public static List<FaxSettingsTransferOutbound> getAllAsOutboundTransferObject(List<FaxConfig> configList)
	{
		List<FaxSettingsTransferOutbound> transferList = new ArrayList<>(configList.size());
		for(FaxConfig config : configList)
		{
			transferList.add(getAsOutboundTransferObject(config));
		}
		return transferList;
	}
}
