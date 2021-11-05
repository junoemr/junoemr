/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.provider.service;

import org.oscarehr.provider.dao.RecentDemographicAccessDao;
import org.oscarehr.provider.model.RecentDemographicAccess;
import org.oscarehr.provider.model.RecentDemographicAccessPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecentDemographicAccessService
{
	@Autowired
	private RecentDemographicAccessDao recentDemographicAccessDao;

	/**
	 * retrieve a list of recent demographics for a given provider
	 * @param providerNo provider id
	 * @param offset offset results. ignored if offset &lt; 1
	 * @param limit limit results. ignored if limit &lt; 1
	 * @return list of recent access results
	 */
	public List<RecentDemographicAccess> getRecentAccessList(Integer providerNo, int offset, int limit)
	{
		return recentDemographicAccessDao.findByProviderNo(providerNo, offset, limit);
	}

	/**
	 * update a recorded access datetime
	 * @param providerNo set the provider who accessed the record
	 * @param demographicNo demographic id of the record
	 */
	public synchronized void updateAccessRecord(Integer providerNo, Integer demographicNo)
	{
		RecentDemographicAccessPK primaryKey = new RecentDemographicAccessPK(providerNo, demographicNo);
		RecentDemographicAccess record = recentDemographicAccessDao.find(primaryKey);
		if(record == null)
		{
			record = new RecentDemographicAccess(providerNo, demographicNo);
			record.setAccessDateTimeToNow();
			recentDemographicAccessDao.persistAndFlush(record);
		}
		else
		{
			record.setAccessDateTimeToNow();
			recentDemographicAccessDao.merge(record);
		}
	}
}
