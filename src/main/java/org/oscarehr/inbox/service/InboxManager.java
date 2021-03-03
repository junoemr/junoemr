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
package org.oscarehr.inbox.service;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.InboxResultsDao;
import org.oscarehr.common.dao.IncomingLabRulesDao;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.model.IncomingLabRules;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.inbox.InboxManagerResponse;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class InboxManager
{
	private static final Logger logger = MiscUtils.getLogger();

	public static final String INBOX_TYPE_HL7 = "HL7";
	public static final String INBOX_TYPE_DOCUMENT = "DOC";

	public static final String STATUS_NEW = "N";

	public static final String NORMAL = "normal";
	public static final String ALL = "all";
	public static final String ABNORMAL = "abnormal";
	public static final String LABS = "labs";
	public static final String DOCUMENTS = "documents";

	@Autowired
	private InboxResultsDao inboxResultsDao;

	@Autowired
	private IncomingLabRulesDao incomingLabRulesDao;

	@Autowired
	private ProviderInboxRoutingDao providerInboxRoutingDao;

	public InboxManagerResponse getInboxResults(
			LoggedInInfo loggedInInfo,
			String view,
			String providerNo,
			String searchProviderNo,
			String demographicNo,
			String patientFirstName,
			String patientLastName,
			String patientHealthNumber,
			String ackStatus,
			Integer page,
			Integer pageSize,
			Date startDate,
			Date endDate
	)
	{
		if(view == null || "".equals(view))
		{
			view = ALL;
		}

		Boolean isAbnormal = null;
		if(ABNORMAL.equals(view))
			isAbnormal = true;
		if(NORMAL.equals(view))
			isAbnormal = false;

		boolean mixLabsAndDocs = NORMAL.equals(view) || ALL.equals(view);

		if(ackStatus == null)
		{
			ackStatus = STATUS_NEW;
		}

		if(providerNo == null)
		{
			providerNo = "";
		}

		if(searchProviderNo == null)
		{
			searchProviderNo = providerNo;
		}

		if(page > 0)
		{
			page--;
		}

		String labType = null;
		if(LABS.equals(view))
		{
			labType = INBOX_TYPE_HL7;
		}
		else if(DOCUMENTS.equals(view))
		{
			labType = INBOX_TYPE_DOCUMENT;
		}

		ArrayList<LabResultData> labdocs = inboxResultsDao.getInboxResults(
				loggedInInfo,
				searchProviderNo,
				demographicNo,
				patientFirstName,
				patientLastName,
				patientHealthNumber,
				ackStatus,
				true,
				page,
				pageSize,
				mixLabsAndDocs,
				isAbnormal,
				null,
				false,
				labType,
				startDate,
				endDate);

		logger.debug("labdocs.size()=" + labdocs.size());

		InboxManagerResponse response = new InboxManagerResponse();
		response.setPageNum(page);
		response.setProviderNo(providerNo);
		response.setSearchProviderNo(searchProviderNo);
		response.setDemographicNo(demographicNo != null ? Integer.parseInt(demographicNo) : null);
		response.setAckStatus(ackStatus);
		response.setLabdocs(labdocs);

		return response;
	}

	public void addDocumentToProviderInbox(Integer labNo, String ... providerIds)
	{
		addToProviderInbox(labNo, INBOX_TYPE_DOCUMENT, providerIds);
	}

	public void addDocumentToProviderInbox(Integer labNo, boolean applyForwardingRules, boolean alwaysFileLabs, String ... providerIds)
	{
		addToProviderInbox(labNo, INBOX_TYPE_DOCUMENT, applyForwardingRules, alwaysFileLabs, providerIds);
	}

	public void addLabToProviderInbox(Integer labNo, String ... providerIds)
	{
		addToProviderInbox(labNo, INBOX_TYPE_HL7, providerIds);
	}

	public void addLabToProviderInbox(Integer labNo, boolean applyForwardingRules, boolean alwaysFileLabs, String ... providerIds)
	{
		addToProviderInbox(labNo, INBOX_TYPE_HL7, applyForwardingRules, alwaysFileLabs, providerIds);
	}

	public void addToProviderInbox(Integer labNo, String labType, String... providerIds)
	{
		addToProviderInbox(labNo, labType, true, false, providerIds);
	}

	public void addToProviderInbox(Integer labNo, String labType, boolean applyForwardingRules, boolean alwaysFileLabs, String... providerIds)
	{
		HashSet<String> providerSet = new HashSet<>(providerIds.length);
		String routeStatus = (alwaysFileLabs ? ProviderInboxItem.FILE : ProviderInboxItem.NEW);

		if(applyForwardingRules)
		{
			Map<String, List<IncomingLabRules>> incomingLabRulesByProviderId = incomingLabRulesDao.findActiveAsProviderMap();
			for(String providerId : providerIds)
			{
				addProviderToHashSet(providerId, providerSet, incomingLabRulesByProviderId);
			}
		}
		else // no forwarding, only specified providers will get inbox entries
		{
			providerSet.addAll(Arrays.asList(providerIds));
		}

		// get all existing routes for the lab, mapped by provider ID
		Map<String, ProviderInboxItem> inboxItems = providerInboxRoutingDao.findAllByTableId(labType, labNo);

		// the provider set now contains all the providers that need a route, without duplicates
		for(String providerId : providerSet)
		{
			ProviderInboxItem inboxItem = inboxItems.get(providerId);
			if(inboxItem == null)
			{
				// new inbox route
				inboxItem = new ProviderInboxItem();
				inboxItem.setProviderNo(providerId);
				inboxItem.setLabNo(labNo);
				inboxItem.setLabType(labType);
				inboxItem.setStatus(routeStatus);
				providerInboxRoutingDao.persist(inboxItem);
			}
			else
			{
				// update the existing route
				//If the document is archived for the provider, move the document from the archive back to their inbox as they've been re-linked to the document
				if(inboxItem.getStatus().equals(ProviderInboxItem.ARCHIVED))
				{
					try
					{
						//TODO this could be refactored to reduce queries
						CommonLabResultData.updateReportStatus(labNo, providerId, routeStatus, null, labType);
					}
					catch(SQLException throwable)
					{
						throw new RuntimeException(throwable);
					}
				}
			}
		}
	}

	/**
	 * recursive method that adds a provider to the hash, as well as all providers that have forwarding rules set up from this provider
	 * @param providerId- the provider to add
	 * @param providerSet - the set to add the provider too. this is modified by this method
	 * @param incomingLabRulesByProviderId - the pre-fetched forwarding rules map
	 */
	private void addProviderToHashSet(
			String providerId,
			HashSet<String> providerSet,
			Map<String, List<IncomingLabRules>> incomingLabRulesByProviderId)
	{
		if(providerId == null || providerSet.contains(providerId))
		{
			//we have already looked up everything for this provider, so return without further action
			return;
		}
		// add the current provider to the set
		providerSet.add(providerId);

		List<IncomingLabRules> providerForwardingRules = incomingLabRulesByProviderId.get(providerId);
		if(providerForwardingRules != null)
		{
			// all providers that have forwarding set up should also get added to the provider hash
			for(IncomingLabRules labRule : providerForwardingRules)
			{
				String forwardToProviderNo = labRule.getFrwdProviderNo();
				addProviderToHashSet(forwardToProviderNo, providerSet, incomingLabRulesByProviderId);
			}
		}
	}
}


