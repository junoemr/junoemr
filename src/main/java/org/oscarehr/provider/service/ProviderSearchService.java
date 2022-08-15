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
package org.oscarehr.provider.service;

import org.oscarehr.dataMigration.converter.out.ProviderDbToModelConverter;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("provider.service.ProviderSearchService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProviderSearchService
{
	@Autowired
	protected ProviderDataDao providerDataDao;

	@Autowired
	private ProviderDbToModelConverter providerDbToModelConverter;

	public List<ProviderModel> providerCriteriaSearch(ProviderCriteriaSearch criteriaSearch)
	{
		List<ProviderData> providers = providerDataDao.criteriaSearch(criteriaSearch);
		return providerDbToModelConverter.convert(providers);
	}

	public int providerCriteriaSearchCount(ProviderCriteriaSearch criteriaSearch)
	{
		return providerDataDao.criteriaSearchCount(criteriaSearch);
	}

	/**
	 * @return providers assigned to the given site, ordered by lastName, firstName
	 * @deprecated for legacy use only
	 */
	@Deprecated
	public List<ProviderData> getBySite(Integer siteId)
	{
		ProviderCriteriaSearch criteriaSearch = new ProviderCriteriaSearch();
		criteriaSearch.setSiteId(siteId);
		return providerDataDao.criteriaSearch(criteriaSearch)
				.stream()
				.sorted(Comparator.comparing((ProviderData o) -> (o.getLastName() + o.getFirstName())))
				.collect(Collectors.toList());
	}

}
