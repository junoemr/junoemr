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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.fax.converter.FaxAccountCreateToEntityConverter;
import org.oscarehr.fax.converter.FaxAccountToModelConverter;
import org.oscarehr.fax.converter.FaxAccountUpdateToEntityConverter;
import org.oscarehr.fax.converter.FaxInboundToModelConverter;
import org.oscarehr.fax.converter.FaxOutboundToModelConverter;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.dao.FaxInboundDao;
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxAccountProvider;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.provider.FaxProviderFactory;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.fax.search.FaxInboundCriteriaSearch;
import org.oscarehr.fax.search.FaxOutboundCriteriaSearch;
import org.oscarehr.fax.transfer.FaxAccountCreateInput;
import org.oscarehr.fax.transfer.FaxAccountTransferOutbound;
import org.oscarehr.fax.transfer.FaxAccountUpdateInput;
import org.oscarehr.fax.transfer.FaxInboxTransferOutbound;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
	private SystemPreferenceService systemPreferenceService;

	@Autowired
	private FaxAccountCreateToEntityConverter faxAccountCreateToEntityConverter;

	@Autowired
	private FaxAccountUpdateToEntityConverter faxAccountUpdateToEntityConverter;

	@Autowired
	private FaxAccountToModelConverter faxAccountToModelConverter;

	@Autowired
	private FaxOutboundToModelConverter faxOutboundToModelConverter;

	@Autowired
	private FaxInboundToModelConverter faxInboundToModelConverter;

	/**
	 * Test the connection to the fax service based on the configuration settings
	 *
	 * @return true if the connection succeeded, false otherwise
	 */
	public boolean testConnectionStatus(FaxAccountCreateInput createInput)
	{
		FaxAccount faxAccount = faxAccountCreateToEntityConverter.convert(createInput);
		FaxAccountProvider faxAccountProvider = FaxProviderFactory.createFaxAccountProvider(faxAccount);
		return faxAccountProvider.testConnectionStatus();
	}

	public boolean testConnectionStatus(FaxAccountUpdateInput updateInput)
	{
		FaxAccount faxAccount = faxAccountDao.find(updateInput.getId());
		if (StringUtils.isNotBlank(updateInput.getPassword()))
		{
			// detach entity to prevent auto saving it with updated password
			faxAccountDao.detach(faxAccount);
			faxAccount.setLoginPassword(updateInput.getPassword());
		}
		FaxAccountProvider faxAccountProvider = FaxProviderFactory.createFaxAccountProvider(faxAccount);
		return faxAccountProvider.testConnectionStatus();
	}

	/**
	 * get the default fax account to be used. return null if none exists
	 */
	public FaxAccount getSystemActiveFaxAccount()
	{
		String accountIdStr = systemPreferenceService.getPreferenceValue(UserProperty.SYSTEM_ACTIVE_FAX_ACCOUNT, null);
		if(accountIdStr != null)
		{
			Long accountId = Long.parseLong(accountIdStr);
			return faxAccountDao.find(accountId);
		}
		else
		{
			// fallback to simple logic of using first active fax account.
			// mostly for legacy purposes where some systems may not have set the active account id.
			FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
			criteriaSearch.setIntegrationEnabledStatus(true);
			criteriaSearch.setOutboundEnabledStatus(true);
			criteriaSearch.setLimit(1);
			criteriaSearch.setSortDirAscending();
			List<FaxAccount> faxAccountList = faxAccountDao.criteriaSearch(criteriaSearch);

			return faxAccountList.isEmpty() ? null : faxAccountList.get(0);
		}
	}

	public FaxAccountTransferOutbound getActiveFaxAccount(String providerId)
	{
		//todo provider specific account selection
		return faxAccountToModelConverter.convert(getSystemActiveFaxAccount());
	}

	public FaxAccountTransferOutbound getFaxAccount(Long id)
	{
		return faxAccountToModelConverter.convert(faxAccountDao.find(id));
	}

	public boolean accountExists(FaxProvider faxProvider, String loginId)
	{
		List<FaxAccount> faxAccounts = faxAccountDao.findByLoginId(faxProvider, loginId);
		return !faxAccounts.isEmpty();
	}

	public boolean isFaxAccountEnabled(Long id)
	{
		return faxAccountDao.find(id).isIntegrationEnabled();
	}

	public List<String> getAccountCoverLetterOptions(Long id)
	{
		FaxAccountProvider accountProvider = FaxProviderFactory.createFaxAccountProvider(faxAccountDao.find(id));
		return accountProvider.getCoverLetterOptions();
	}

	public FaxAccountTransferOutbound createFaxAccount(FaxAccountCreateInput createInput)
	{
		FaxAccount faxAccount = faxAccountCreateToEntityConverter.convert(createInput);
		faxAccountDao.persist(faxAccount);
		return faxAccountToModelConverter.convert(faxAccount);
	}

	public FaxAccountTransferOutbound updateFaxAccount(FaxAccountUpdateInput updateInput)
	{
		FaxAccount faxAccount = faxAccountUpdateToEntityConverter.convert(updateInput);
		faxAccountDao.merge(faxAccount);
		return faxAccountToModelConverter.convert(faxAccount);
	}

	public boolean deleteFaxAccount(Long id)
	{
		FaxAccount faxAccount = faxAccountDao.find(id);
		faxAccount.setDeletedAt(new Date());
		faxAccount.setLoginPassword(null); // wipe the password
		faxAccountDao.merge(faxAccount);
		return true;
	}

	public List<FaxAccountTransferOutbound> listAccounts(FaxAccountCriteriaSearch criteriaSearch)
	{
		return faxAccountToModelConverter.convert(faxAccountDao.criteriaSearch(criteriaSearch));
	}

	public List<FaxOutboxTransferOutbound> getOutboxResults(FaxOutboundCriteriaSearch criteriaSearch)
	{
		return faxOutboundToModelConverter.convert(faxOutboundDao.criteriaSearch(criteriaSearch));
	}

	public List<FaxInboxTransferOutbound> getInboxResults(FaxInboundCriteriaSearch criteriaSearch)
	{
		// find the list of all inbound results based on the search criteria
		return faxInboundToModelConverter.convert(faxInboundDao.criteriaSearch(criteriaSearch));
	}
}