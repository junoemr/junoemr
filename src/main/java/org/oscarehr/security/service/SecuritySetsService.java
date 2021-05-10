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
package org.oscarehr.security.service;

import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.security.dao.SecDemographicSetDao;
import org.oscarehr.security.model.SecDemographicSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;
import oscar.log.LogConst;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SecuritySetsService
{
	@Autowired
	private SecDemographicSetDao secDemographicSetDao;

	@Autowired
	private ProviderDataDao providerDao;

	public List<String> getSecurityDemographicSetNamesBlacklist(String providerId)
	{
		List<SecDemographicSet> assignedSets = secDemographicSetDao.findAllByProvider(providerId);
		return assignedSets.stream()
				.map(SecDemographicSet::getSetName)
				.collect(Collectors.toList());
	}

	public void setSecurityDemographicSetsBlacklist(String loggedInProvider, String providerId, List<String> assignedSetNames)
	{
		List<SecDemographicSet> currentlyAssignedSets = secDemographicSetDao.findAllByProvider(providerId);

		// set removed entries as deleted
		for(SecDemographicSet currentSet : currentlyAssignedSets)
		{
			if(!assignedSetNames.contains(currentSet.getSetName()))
			{
				currentSet.setDeletedAt(LocalDateTime.now());
				currentSet.setDeletedBy(loggedInProvider);
				secDemographicSetDao.merge(currentSet);
			}
		}

		// add any new assignments
		for(String setName : assignedSetNames)
		{
			SecDemographicSet demographicSet = secDemographicSetDao.findByProviderAndSetName(providerId, setName);
			if(demographicSet == null)
			{
				demographicSet = new SecDemographicSet();
				demographicSet.setProvider(providerDao.find(providerId));
				demographicSet.setSetName(setName);
				demographicSet.setSetTypeBlacklist();
				demographicSet.setCreatedBy(loggedInProvider);
				secDemographicSetDao.persist(demographicSet);
			}
		}

		LogAction.addLogEntry(loggedInProvider, null, LogConst.ACTION_UPDATE, LogConst.CON_SECURITY, LogConst.STATUS_SUCCESS,
				providerId, null, "Demographic Set Blacklist: [" + String.join(", ", assignedSetNames) + "]");
	}
}
