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
package org.oscarehr.demographicArchive.service;

import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.demographicArchive.dao.DemographicArchiveDao;
import org.oscarehr.demographicArchive.entity.DemographicArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DemographicArchiveService
{
	@Autowired
	private DemographicArchiveDao demographicArchiveDao;

	public void archiveDemographic(Demographic demographic)
	{
		DemographicArchive archive = new DemographicArchive(demographic);
		archiveDemographic(archive);
	}

	@Deprecated
	public void archiveDemographic(org.oscarehr.common.model.Demographic demographic,
	                               DemographicCust demographicCust,
	                               List<DemographicExt> extList)
	{
		DemographicArchive archive = new DemographicArchive(demographic);
		archive.setDemographicExtArchiveSet(null);//TODO
		archive.setDemographicCustArchive(null);
		archiveDemographic(archive);
	}

	protected void archiveDemographic(DemographicArchive archive)
	{
		// cascading persist will save any linked ext / cust entries attached
		demographicArchiveDao.persist(archive);
	}
}
