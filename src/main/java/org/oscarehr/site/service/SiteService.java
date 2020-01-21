/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.site.service;

import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.ProviderSitePK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService
{

	@Autowired
	private ProviderSiteDao providerSiteDao;

	/**
	 * assign the provider to the specified sites.
	 * @param siteIds - site ids to assign
	 * @param providerNo - the provider to assign sites for
	 */
	public synchronized void assignProviderSites(List<Integer> siteIds, Integer providerNo)
	{
		if (siteIds != null)
		{
			for (Integer siteId : siteIds)
			{
				if (providerSiteDao.find(new ProviderSitePK(providerNo.toString(), siteId)) == null)
				{
					ProviderSite providerSite = new ProviderSite();
					providerSite.setId(new ProviderSitePK(providerNo.toString(), siteId));
					providerSiteDao.persist(providerSite);
				}
			}
		}
	}

	/**
	 * remove any site from the provider that is not in the site list
	 * @param siteIds - site ids to keep
	 * @param providerNo - the provider on which to perform the operation.
	 */
	public synchronized void removeOtherSites(List<Integer> siteIds, Integer providerNo)
	{
		if (siteIds != null)
		{
			List<ProviderSite> currentSites = providerSiteDao.findByProviderNo(providerNo.toString());

			for (ProviderSite pSite : currentSites)
			{
				boolean contains = false;
				for (Integer siteId : siteIds)
				{
					if (pSite.getId().getSiteId() == siteId)
					{
						contains = true;
						break;
					}
				}

				if (!contains)
				{
					providerSiteDao.remove(pSite);
				}
			}
		}
	}
}
