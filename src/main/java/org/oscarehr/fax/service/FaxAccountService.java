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
package org.oscarehr.fax.service;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.fax.exception.FaxApiException;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxOutboxResult;
import org.oscarehr.fax.externalApi.srfax.result.GetUsageResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This service should be responsible for handling all logic around fax setup and configuration
 */
@Service
@Transactional
public class FaxAccountService
{
	private static final Logger logger = Logger.getLogger(FaxAccountService.class);

	/**
	 * Test the connection to the fax service based on the configuration settings
	 * @return true if the connection succeeded, false otherwise
	 */
	public boolean testConnectionStatus(String accountId, String password)
	{
		// don't hit the api if username or password are empty/missing
		if(StringUtils.trimToNull(accountId) == null || StringUtils.trimToNull(password) == null)
		{
			return false;
		}

		SRFaxApiConnector apiConnector = new SRFaxApiConnector(accountId, password);

		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String currentDateStr = localDate.format(formatter);

		ListWrapper<GetUsageResult> result = apiConnector.Get_Fax_Usage(
				SRFaxApiConnector.RESPONSE_FORMAT_JSON,
				SRFaxApiConnector.PERIOD_RANGE,
				currentDateStr,
				currentDateStr,
				null);

		logger.info(String.valueOf(result));

		return (result != null && result.isSuccess());
	}

	public List<FaxOutboxTransferOutbound> getOutboxResults(FaxAccount faxAccount, LocalDate startDate, LocalDate endDate)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());
		ListWrapper<GetFaxOutboxResult> resultList = apiConnector.Get_Fax_Outbox(
				SRFaxApiConnector.RESPONSE_FORMAT_JSON,
				SRFaxApiConnector.PERIOD_RANGE,
				startDate.format(formatter),
				endDate.format(formatter),
				null
		);

		ArrayList<FaxOutboxTransferOutbound> transferList;
		if(resultList.isSuccess())
		{
			transferList = new ArrayList<>(resultList.getResult().size());
			for(GetFaxOutboxResult result : resultList.getResult())
			{
				FaxOutboxTransferOutbound transfer = new FaxOutboxTransferOutbound();
				transfer.setFaxAccountId(faxAccount.getId());
				transfer.setFileName(result.getFileName());
				transfer.setSubject(result.getSubject());
				transfer.setDateQueued(result.getDateQueued());
				transfer.setDateSent(result.getDateSent());
				transfer.setSentStatus(result.getSentStatus());
				transfer.setToFaxNumber(result.getToFaxNumber());
				transferList.add(transfer);
			}
		}
		else
		{
			throw new FaxApiException("Fax API Error:" + resultList.getError());
		}
		return transferList;
	}
}
