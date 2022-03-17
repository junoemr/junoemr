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

import org.oscarehr.fax.exception.FaxApiResultException;
import org.oscarehr.fax.exception.FaxIntegrationException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxDownloadProvider;
import org.oscarehr.fax.result.FaxInboxResult;
import org.oscarehr.integration.ringcentral.api.RingcentralApiConnector;
import org.oscarehr.integration.ringcentral.api.input.RingCentralMessageListInput;
import org.oscarehr.integration.ringcentral.api.input.RingCentralMessageUpdateInput;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAttachment;
import org.oscarehr.integration.ringcentral.api.result.RingCentralMessageInfoResult;
import org.oscarehr.integration.ringcentral.api.result.RingCentralMessageListResult;
import org.oscarehr.util.SpringUtils;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.oscarehr.integration.ringcentral.api.RingcentralApiConnector.CURRENT_SESSION_INDICATOR;

public class RingcentralDownloadProvider implements FaxDownloadProvider
{
	protected FaxAccount faxAccount;
	protected RingcentralApiConnector ringcentralApiConnector = SpringUtils.getBean(RingcentralApiConnector.class); //todo how to access in pojo?

	public RingcentralDownloadProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}

	/**
	 * Retrieves a List of FaxInboxResults of unread faxes from Ringcentral
	 * @param faxDaysPast Number of days to retrieve result from Ringcentral for
	 * @return The list of unread FaxInboxResults
	 * @throws FaxApiResultException if result is not success
	 */

	@Override
	public List<? extends FaxInboxResult> getFaxInbox(int faxDaysPast) throws FaxApiResultException
	{
		RingCentralMessageListInput input = new RingCentralMessageListInput();
		input.setDateFrom(ZonedDateTime.now().minusDays(faxDaysPast));
		input.setAvailability(new String[] {"Alive"});
		input.setDirection(new String[] {"Inbound"});
		input.setReadStatus(new String[] {"Unread"});
		input.setMessageType(new String[] {"Fax"});

		RingCentralMessageListResult result = ringcentralApiConnector.getMessageList(
				faxAccount.getLoginId(),
				CURRENT_SESSION_INDICATOR,
				input);
		return Arrays.asList(result.getRecords());
	}


	/**
	 * Retrieves a fax from Ringcentral
	 * @param referenceIdStr Ringcentral fax reference Id of the fax to retrieve
	 * @return Fax document as an input stream
	 * @throws FaxApiResultException if result is not success
	 */
	@Override
	public InputStream retrieveFax(String referenceIdStr) throws FaxApiResultException
	{
		RingCentralMessageInfoResult messageResult = ringcentralApiConnector.getMessage(
				faxAccount.getLoginId(),
				CURRENT_SESSION_INDICATOR,
				referenceIdStr);

		List<RingCentralAttachment> attachments = messageResult.getAttachmentsList();
		if(attachments.isEmpty())
		{
			throw new FaxIntegrationException("Message has no attachments");
		}
		else if(attachments.size() > 1)
		{
			throw new FaxIntegrationException("Multi-attachment faxes not currently supported"); //TODO how to handle this?
		}
		else
		{
			RingCentralAttachment attachment = attachments.get(0);
			return ringcentralApiConnector.getMessageContent(
					faxAccount.getLoginId(),
					CURRENT_SESSION_INDICATOR,
					referenceIdStr,
					String.valueOf(attachment.getId()));
		}
	}

	/**
	 * Marks the fax as read in Ringcentral
	 * @param referenceIdStr reference id of the fax to make as read
	 * @throws FaxApiResultException if result is not success
	 */
	@Override
	public void markAsDownloaded(String referenceIdStr) throws FaxApiResultException
	{
		RingCentralMessageUpdateInput input = new RingCentralMessageUpdateInput();
		input.setMessageType("Fax");
		input.setReadStatus("Read");
		ringcentralApiConnector.updateMessage(faxAccount.getLoginId(), CURRENT_SESSION_INDICATOR, referenceIdStr, input);
	}
}