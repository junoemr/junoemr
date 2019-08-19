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
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetUsageResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxInbound;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.fax.search.FaxInboundCriteriaSearch;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.ws.rest.conversion.FaxTransferConverter;
import org.oscarehr.ws.rest.transfer.fax.FaxInboxTransferOutbound;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
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

	@Autowired
	private FaxOutboundDao faxOutboundDao;

	@Autowired
	private FaxInboundDao faxInboundDao;

	@Autowired
	private FaxAccountDao faxAccountDao;

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
		String currentDateStr = ConversionUtils.toDateString(LocalDate.now(), SRFaxApiConnector.DATE_FORMAT);

		ListWrapper<GetUsageResult> result = apiConnector.getFaxUsageByRange(currentDateStr, currentDateStr, null);
		logger.debug(String.valueOf(result));

		return (result != null && result.isSuccess());
	}

	/** get the default fax account to be used. return null if none exists */
	public FaxAccount getDefaultFaxAccount()
	{
		//TODO provider specific logic etc?
		FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
		criteriaSearch.setIntegrationEnabledStatus(true);
		criteriaSearch.setOutboundEnabledStatus(true);
		criteriaSearch.setLimit(1);
		criteriaSearch.setSortDirAscending();
		List<FaxAccount> faxAccountList = faxAccountDao.criteriaSearch(criteriaSearch);

		return faxAccountList.isEmpty() ? null : faxAccountList.get(0);
	}

	public List<FaxOutboxTransferOutbound> getOutboxResults(FaxAccount faxAccount, FaxOutboundCriteriaSearch criteriaSearch)
	{
		List<FaxOutbound> outboundList = faxOutboundDao.criteriaSearch(criteriaSearch);

		ArrayList<FaxOutboxTransferOutbound> transferList = new ArrayList<>(outboundList.size());
		for(FaxOutbound faxOutbound : outboundList)
		{
			transferList.add(FaxTransferConverter.getAsOutboxTransferObject(faxAccount, faxOutbound));
		}
		return transferList;
	}

	public List<FaxInboxTransferOutbound> getInboxResults(FaxInboundCriteriaSearch criteriaSearch)
	{
		// find the list of all inbound results based on the search criteria
		List<FaxInbound> inboundList = faxInboundDao.criteriaSearch(criteriaSearch);

		ArrayList<FaxInboxTransferOutbound> transferList = new ArrayList<>(inboundList.size());
		for(FaxInbound faxInbound : inboundList)
		{
			transferList.add(FaxTransferConverter.getAsInboxTransferObject(faxInbound));
		}

		return transferList;
	}
}
