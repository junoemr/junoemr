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
package org.oscarehr.dataMigration.converter.in;

import org.apache.log4j.Logger;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import org.oscarehr.provider.service.ProviderRoleService;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;

import java.util.HashMap;
import java.util.List;

@Component
public abstract class BaseModelToDbConverter<I, E> extends AbstractModelConverter<I, E>
{
	protected static final OscarProperties properties = OscarProperties.getInstance();
	protected static final String IMPORT_PROVIDER = properties.getProperty("import_service.system_provider_no", "999900");
	protected static final String DEFAULT_PROVIDER_LAST_NAME = properties.getProperty("import_service.default_provider.last_name", "import");
	protected static final String DEFAULT_PROVIDER_FIRST_NAME = properties.getProperty("import_service.default_provider.first_name", "provider");

	private static final Logger logger = MiscUtils.getLogger();
	private static final HashMap<String, ProviderData> providerLookupCache = new HashMap<>();

	@Autowired
	private ProviderDataDao providerDataDao;

	@Autowired
	private ProviderService providerService;

	@Autowired
	private ProviderRoleService providerRoleService;

	@Autowired
	private ProviderModelToDbConverter providerModelToDbConverter;

	public static void clearProviderCache()
	{
		logger.info("Clearing db provider cache");
		providerLookupCache.clear();
	}

	/**
	 * look up an existing provider record matched on first and last name, or create a new one if none is found.
	 * If first and last name are not set, then a default will be selected. null if the nullable flag is true, or a default provider record otherwise
	 * @param provider - the provider model to find a database object for
	 * @param nullable - determines if null can be returned in the event that there is insufficient information to create a provider record
	 * @return - the provider record if possible, otherwise null if nullable is true, and a default if not nullable
	 */
	protected ProviderData findOrCreateProviderRecord(Provider provider, boolean nullable)
	{
		Provider newProvider;
		if(provider == null && nullable)
		{
			return null;
		}
		else if(provider == null || provider.getFirstName() == null || provider.getLastName() == null)
		{
			logger.warn("Not enough provider info found to link or create provider record (first and last name are required). \n" +
					"Default provider (" + DEFAULT_PROVIDER_LAST_NAME + "," + DEFAULT_PROVIDER_FIRST_NAME + ") will be assigned.");
			newProvider = getDefaultProvider();
		}
		else
		{
			newProvider = provider;
		}

		String cacheKey = newProvider.getFirstName() + newProvider.getLastName();

		ProviderData dbProvider;
		if(providerLookupCache.containsKey(cacheKey))
		{
			dbProvider = providerLookupCache.get(cacheKey);
			logger.info("Use existing cached Provider record " + dbProvider.getId() + " (" + dbProvider.getLastName() + "," + dbProvider.getFirstName() + ")");
		}
		else
		{
			ProviderCriteriaSearch criteriaSearch = new ProviderCriteriaSearch();
			criteriaSearch.setFirstName(newProvider.getFirstName());
			criteriaSearch.setLastName(newProvider.getLastName());

			List<ProviderData> matchedProviders = providerDataDao.criteriaSearch(criteriaSearch);
			if(matchedProviders.isEmpty())
			{
				dbProvider = providerModelToDbConverter.convert(newProvider);
				// providers don't have auto-generated id's, so we have to pick one
				Integer newProviderId = providerService.getNextProviderNumberInSequence(9999, 900000);
				newProviderId = (newProviderId == null) ? 10000 : newProviderId;
				dbProvider.set(String.valueOf(newProviderId));

				String billCenterCode = properties.getProperty("default_bill_center", "");
				dbProvider = providerService.addNewProvider(IMPORT_PROVIDER, dbProvider, billCenterCode);
				providerRoleService.setDefaultRoleForNewProvider(dbProvider.getId());

				logger.info("Created new Provider record " + dbProvider.getId() + " (" + dbProvider.getLastName() + "," + dbProvider.getFirstName() + ")");
			}
			else if(matchedProviders.size() == 1)
			{
				dbProvider = matchedProviders.get(0);
				logger.info("Use existing uncached Provider record " + dbProvider.getId() + " (" + dbProvider.getLastName() + "," + dbProvider.getFirstName() + ")");
			}
			else
			{
				throw new RuntimeException("Multiple providers exist in the system with the same name (" + newProvider.getLastName() + "," + newProvider.getFirstName() + ").");
			}
			providerLookupCache.put(cacheKey, dbProvider);
		}
		return dbProvider;
	}

	private Provider getDefaultProvider()
	{
		Provider provider = new Provider();
		provider.setFirstName(DEFAULT_PROVIDER_FIRST_NAME);
		provider.setLastName(DEFAULT_PROVIDER_LAST_NAME);
		return provider;
	}
}
