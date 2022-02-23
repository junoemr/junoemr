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
import org.oscarehr.fax.conversion.FaxAccountCreateToEntityConverter;
import org.oscarehr.fax.conversion.FaxAccountToModelConverter;
import org.oscarehr.fax.conversion.FaxAccountUpdateToEntityConverter;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetUsageResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.fax.provider.FaxProviderFactory;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.fax.search.FaxInboundCriteriaSearch;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.fax.transfer.FaxAccountCreateInput;
import org.oscarehr.fax.transfer.FaxAccountTransferOutbound;
import org.oscarehr.fax.transfer.FaxAccountUpdateInput;
import org.oscarehr.ws.rest.conversion.FaxTransferConverter;
import org.oscarehr.fax.transfer.FaxInboxTransferOutbound;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
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

	@Autowired
	private FaxAccountCreateToEntityConverter faxAccountCreateToEntityConverter;

	@Autowired
	private FaxAccountUpdateToEntityConverter faxAccountUpdateToEntityConverter;

	@Autowired
	private FaxAccountToModelConverter faxAccountToModelConverter;

	/**
	 * Test the connection to the fax service based on the configuration settings
	 *
	 * @return true if the connection succeeded, false otherwise
	 */
	@Deprecated // TODO remove
	public boolean testConnectionStatus(String accountId, String password)
	{
		// don't hit the api if username or password are empty/missing
		if (StringUtils.trimToNull(accountId) == null || StringUtils.trimToNull(password) == null)
		{
			return false;
		}

		SRFaxApiConnector apiConnector = new SRFaxApiConnector(accountId, password);
		String currentDateStr = ConversionUtils.toDateString(LocalDate.now(), SRFaxApiConnector.DATE_FORMAT);

		ListWrapper<GetUsageResult> result = apiConnector.getFaxUsageByRange(currentDateStr, currentDateStr, null);
		logger.debug(String.valueOf(result));

		return (result != null && result.isSuccess());
	}

	public boolean testConnectionStatus(FaxAccountCreateInput createInput)
	{
		FaxAccount faxAccount = faxAccountCreateToEntityConverter.convert(createInput);
		FaxAccountProvider faxAccountProvider = new FaxProviderFactory().createFaxAccountProvider(faxAccount);
		return faxAccountProvider.testConnectionStatus();
	}

	public boolean testConnectionStatus(FaxAccountUpdateInput updateInput)
	{
		FaxAccount faxAccount = faxAccountDao.find(updateInput.getId());// todo detatch entity to prevent auto saving it

		// if the password is not changed, use the saved one
		String password = updateInput.getPassword();
		if (StringUtils.isBlank(password))
		{
			password = faxAccount.getLoginPassword();
		}
		faxAccount.setLoginPassword(password);
		FaxAccountProvider faxAccountProvider = new FaxProviderFactory().createFaxAccountProvider(faxAccount);
		return faxAccountProvider.testConnectionStatus();
	}

	/**
	 * get the default fax account to be used. return null if none exists
	 */
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

	public List<FaxAccountTransferOutbound> listAccounts(FaxAccountCriteriaSearch criteriaSearch)
	{
		return faxAccountToModelConverter.convert(faxAccountDao.criteriaSearch(criteriaSearch));
	}

	public List<FaxOutboxTransferOutbound> getOutboxResults(FaxAccount faxAccount, FaxOutboundCriteriaSearch criteriaSearch)
	{
		return FaxTransferConverter.getAllAsOutboxTransferObject(faxAccount, faxOutboundDao.criteriaSearch(criteriaSearch));
	}

	public List<FaxInboxTransferOutbound> getInboxResults(FaxInboundCriteriaSearch criteriaSearch)
	{
		// find the list of all inbound results based on the search criteria
		return FaxTransferConverter.getAllAsInboxTransferObject(faxInboundDao.criteriaSearch(criteriaSearch));
	}
}
