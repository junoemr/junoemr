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
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxStatusResult;
import org.oscarehr.fax.externalApi.srfax.result.GetUsageResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service should be responsible for handling all logic around fax setup and configuration
 */
@Service
@Transactional
public class FaxAccountService
{
	private static final Logger logger = Logger.getLogger(FaxAccountService.class);

	@Autowired
	private FaxOutboundDao faxOutboundDao;

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

	public List<FaxOutboxTransferOutbound> getOutboxResults(FaxAccount faxAccount, FaxOutboundCriteriaSearch criteriaSearch)
	{
		SRFaxApiConnector apiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());

		// find the list of all outbound results based on the search criteria
		List<FaxOutbound> outboundList = faxOutboundDao.criteriaSearch(criteriaSearch);

		// filter out the results that were actually sent to srfax
		List<String> referenceIdList = new ArrayList<>(outboundList.size());
		for(FaxOutbound faxOutbound : outboundList)
		{
			// if there is an expected api result, add the additional info to the transfer object
			// only do this if the account info has not changed, or the api call will fail
			if(FaxOutbound.Status.SENT.equals(faxOutbound.getStatus())
					&& faxAccount.getLoginId().equals(faxOutbound.getExternalAccountId())
					&& faxAccount.getIntegrationType().equals(faxOutbound.getExternalAccountType()))
			{
				referenceIdList.add(String.valueOf(faxOutbound.getExternalReferenceId()));
			}
		}

		Map<String, GetFaxStatusResult> statusResultMap = new HashMap<>(referenceIdList.size());
		if(!referenceIdList.isEmpty())
		{
			// ask srfax for information on the outbound faxes that were successfully sent to srfax
			ListWrapper<GetFaxStatusResult> resultList = apiConnector.Get_MultiFaxStatus(referenceIdList, SRFaxApiConnector.RESPONSE_FORMAT_JSON);
			logger.debug(resultList);

			// if the api response is a success, map the results
			if(resultList.isSuccess())
			{
				for(GetFaxStatusResult result : resultList.getResult())
				{
					// need to parse the filename property for the referenceId in the returned data
					String filename = result.getFileName();
					String referenceId = filename.split("\\|")[1];
					statusResultMap.put(referenceId, result);
				}
			}
			else
			{
				logger.warn("SRFAX API Connection Failure: " + resultList.getError());
			}
		}

		// merge the local outbound fax results with the results from srfax into transfer objects
		ArrayList<FaxOutboxTransferOutbound> transferList = new ArrayList<>(outboundList.size());
		for(FaxOutbound faxOutbound : outboundList)
		{
			// set the locally available field info
			FaxOutboxTransferOutbound transfer = new FaxOutboxTransferOutbound();
			transfer.setFaxAccountId(faxAccount.getId());
			transfer.setProviderNo(faxOutbound.getProviderNo());
			transfer.setDemographicNo(faxOutbound.getDemographicNo());
			transfer.setSystemStatus(String.valueOf(faxOutbound.getStatus()));
			transfer.setSystemDateSent(ConversionUtils.toTimestampString(faxOutbound.getCreatedAt()));
			transfer.setToFaxNumber(faxOutbound.getSentTo());
			transfer.setFileType(faxOutbound.getFileType().name());

			// add the data from srfax on relevant objects
			if(FaxOutbound.Status.SENT.equals(faxOutbound.getStatus()))
			{
				GetFaxStatusResult apiResult = statusResultMap.get(String.valueOf(faxOutbound.getExternalReferenceId()));
				if(apiResult != null)
				{
					transfer.setIntegrationDateQueued(apiResult.getDateQueued());
					transfer.setIntegrationDateSent(apiResult.getDateSent());
					transfer.setIntegrationStatus(apiResult.getSentStatus());
				}
			}
			transferList.add(transfer);
		}
		return transferList;
	}
}
