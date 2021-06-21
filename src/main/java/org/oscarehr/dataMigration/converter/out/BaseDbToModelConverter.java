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
package org.oscarehr.dataMigration.converter.out;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public abstract class BaseDbToModelConverter<I, E> extends AbstractModelConverter<I, E>
{
	protected static final OscarProperties properties = OscarProperties.getInstance();
	private static final Logger logger = MiscUtils.getLogger();
	private static final ConcurrentMap<String, Provider> providerLookupCache = new ConcurrentHashMap<>();

	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private ProviderDbToModelConverter providerConverter;

	public synchronized static void clearProviderCache()
	{
		logger.info("Clearing model provider cache");
		providerLookupCache.clear();
	}

	protected synchronized Provider findProvider(String providerId)
	{
		Provider providerRecord = null;
		providerId = StringUtils.trimToNull(providerId);
		if(providerId != null)
		{
			if(providerLookupCache.containsKey(providerId))
			{
				providerRecord = providerLookupCache.get(providerId);
			}
			else
			{
				providerRecord = providerConverter.convert(providerDao.find(providerId));
				providerLookupCache.put(providerId, providerRecord);
			}
		}

		return providerRecord;
	}

	protected synchronized Provider findProvider(ProviderData provider)
	{
		Provider providerRecord = null;
		if(provider != null)
		{
			providerRecord = findProvider(provider.getId());
		}
		return providerRecord;
	}

	protected Provider getProviderFromString(String referralProviderName, String referralProviderNumber)
	{
		Provider referralProvider = null;
		if(referralProviderName != null && referralProviderName.contains(","))
		{
			String[] nameArray = referralProviderName.split(",", 2);
			String firstName = StringUtils.trimToNull(nameArray[1]);
			String lastName = StringUtils.trimToNull(nameArray[0]);

			referralProvider = new Provider();
			referralProvider.setFirstName((firstName != null) ? firstName : "Missing");
			referralProvider.setLastName((lastName != null) ? lastName : "Missing");
			referralProvider.setOhipNumber(StringUtils.trimToNull(referralProviderNumber));
		}
		return referralProvider;
	}
}
