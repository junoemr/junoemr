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
package org.oscarehr.inbox.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.IncomingLabRulesDao;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.model.IncomingLabRules;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderInboxItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InboxManagerTest
{
	@Autowired
	@InjectMocks
	private InboxManager inboxManager;

	@Mock
	private IncomingLabRulesDao incomingLabRulesDao;

	@Mock
	private ProviderInboxRoutingDao providerInboxRoutingDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAddToProviderInbox_noForwarding_noPreviousRoutes()
	{
		String unclaimedProviderNo = Provider.UNCLAIMED_PROVIDER_NO;
		Integer docId = 1;

		// no forwarding rules exist - empty map
		when(incomingLabRulesDao.findActiveAsProviderMap()).thenReturn(new HashMap<>());

		// no previous routes exist - empty map
		when(providerInboxRoutingDao.findAllByTableId(InboxManager.INBOX_TYPE_DOCUMENT, docId)).thenReturn(new HashMap<>());

		inboxManager.addToProviderInbox(docId, InboxManager.INBOX_TYPE_DOCUMENT, unclaimedProviderNo);

		verify(providerInboxRoutingDao, times(1)).persist(Mockito.any(ProviderInboxItem.class));
	}

	@Test
	public void testAddToProviderInbox_singleForwardingRule_noPreviousRoutes()
	{
		String baseProviderId = "101";
		String forwardProviderId = "201";
		Integer docId = 1;

		Map<String, List<IncomingLabRules>> forwardingRules = new HashMap<>();
		forwardingRules.put(baseProviderId, createIncomingLabRulesForward(forwardProviderId));
		when(incomingLabRulesDao.findActiveAsProviderMap()).thenReturn(forwardingRules);

		// no previous routes exist - empty map
		when(providerInboxRoutingDao.findAllByTableId(InboxManager.INBOX_TYPE_DOCUMENT, docId)).thenReturn(new HashMap<>());

		inboxManager.addToProviderInbox(docId, InboxManager.INBOX_TYPE_DOCUMENT, baseProviderId);

		verify(providerInboxRoutingDao, times(2)).persist(Mockito.any(ProviderInboxItem.class));
	}

	@Test
	public void testAddToProviderInbox_manyForwardingRule_noPreviousRoutes()
	{
		String baseProviderId = "101";
		String[] forwardProviderIds = {"201", "202", "203", "204"};
		Integer docId = 1;

		Map<String, List<IncomingLabRules>> forwardingRules = new HashMap<>();
		forwardingRules.put(baseProviderId, createIncomingLabRulesForward(forwardProviderIds));
		when(incomingLabRulesDao.findActiveAsProviderMap()).thenReturn(forwardingRules);

		// no previous routes exist - empty map
		when(providerInboxRoutingDao.findAllByTableId(InboxManager.INBOX_TYPE_DOCUMENT, docId)).thenReturn(new HashMap<>());

		inboxManager.addToProviderInbox(docId, InboxManager.INBOX_TYPE_DOCUMENT, baseProviderId);

		verify(providerInboxRoutingDao, times(5)).persist(Mockito.any(ProviderInboxItem.class));
	}

	@Test
	public void testAddToProviderInbox_manyForwardingRuleWithDuplicates_noPreviousRoutes()
	{
		String[] baseProviderIds = {"101", "102", "102"};
		String[] forwardProviderIds0 = {"201", "202", "203", "203"};
		String[] forwardProviderIds1 = {"301", "202"};
		String[] forwardProviderIds2 = {"301", "202"};
		Integer docId = 1;

		Map<String, List<IncomingLabRules>> forwardingRules = new HashMap<>();
		when(incomingLabRulesDao.findActiveAsProviderMap()).thenReturn(forwardingRules);
		forwardingRules.put("101", createIncomingLabRulesForward(forwardProviderIds0));
		forwardingRules.put("102", createIncomingLabRulesForward(forwardProviderIds1));
		forwardingRules.put("201", createIncomingLabRulesForward(forwardProviderIds2));

		when(providerInboxRoutingDao.findAllByTableId(InboxManager.INBOX_TYPE_DOCUMENT, docId)).thenReturn(new HashMap<>());

		inboxManager.addToProviderInbox(docId, InboxManager.INBOX_TYPE_DOCUMENT, baseProviderIds);

		verify(providerInboxRoutingDao, times(6)).persist(Mockito.any(ProviderInboxItem.class));
	}

	@Test
	public void testAddToProviderInbox_forwardingRulesWithLoop_noPreviousRoutes()
	{
		String[] baseProviderIds = {"101"};
		String[] forwardProviderIds0 = {"201"};
		String[] forwardProviderIds1 = {"101"};
		Integer docId = 1;

		Map<String, List<IncomingLabRules>> forwardingRules = new HashMap<>();
		when(incomingLabRulesDao.findActiveAsProviderMap()).thenReturn(forwardingRules);
		forwardingRules.put("101", createIncomingLabRulesForward(forwardProviderIds0));
		forwardingRules.put("201", createIncomingLabRulesForward(forwardProviderIds1));

		when(providerInboxRoutingDao.findAllByTableId(InboxManager.INBOX_TYPE_DOCUMENT, docId)).thenReturn(new HashMap<>());

		inboxManager.addToProviderInbox(docId, InboxManager.INBOX_TYPE_DOCUMENT, baseProviderIds);

		verify(providerInboxRoutingDao, times(2)).persist(Mockito.any(ProviderInboxItem.class));
	}

	@Test
	public void testAddToProviderInbox_ignoreForwardingRules_noPreviousRoutes()
	{
		String[] baseProviderIds = {"101", "102", "103"};
		String[] forwardProviderIds0 = {"201", "202", "203", "203"};
		String[] forwardProviderIds1 = {"301", "302"};
		String[] forwardProviderIds2 = {"401"};
		Integer docId = 1;

		Map<String, List<IncomingLabRules>> forwardingRules = new HashMap<>();
		when(incomingLabRulesDao.findActiveAsProviderMap()).thenReturn(forwardingRules);
		forwardingRules.put("101", createIncomingLabRulesForward(forwardProviderIds0));
		forwardingRules.put("102", createIncomingLabRulesForward(forwardProviderIds1));
		forwardingRules.put("103", createIncomingLabRulesForward(forwardProviderIds2));

		when(providerInboxRoutingDao.findAllByTableId(InboxManager.INBOX_TYPE_DOCUMENT, docId)).thenReturn(new HashMap<>());

		inboxManager.addToProviderInbox(docId, InboxManager.INBOX_TYPE_DOCUMENT, false, false,  baseProviderIds);

		verify(providerInboxRoutingDao, times(3)).persist(Mockito.any(ProviderInboxItem.class));
	}

	private List<IncomingLabRules> createIncomingLabRulesForward(String... forwardIds)
	{
		List<IncomingLabRules> rulesList = new ArrayList<>(forwardIds.length);
		for(String forwardId : forwardIds)
		{
			IncomingLabRules labRules = mock(IncomingLabRules.class);
			when(labRules.getFrwdProviderNo()).thenReturn(forwardId);
			rulesList.add(labRules);
		}
		return rulesList;
	}
}
