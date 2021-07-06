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
package org.oscarehr.flowsheet.converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.decisionSupport2.dao.DsRuleDao;
import org.oscarehr.decisionSupport2.entity.DsRule;
import org.oscarehr.decisionSupport2.model.condition.ConditionType;
import org.oscarehr.decisionSupport2.model.consequence.ConsequenceType;
import org.oscarehr.decisionSupport2.model.consequence.SeverityLevel;
import org.oscarehr.decisionSupport2.transfer.DsRuleConditionUpdateInput;
import org.oscarehr.decisionSupport2.transfer.DsRuleConsequenceUpdateInput;
import org.oscarehr.decisionSupport2.transfer.DsRuleUpdateInput;
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.entity.Flowsheet;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.entity.FlowsheetItemGroup;
import org.oscarehr.flowsheet.transfer.FlowsheetCreateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetItemGroupCreateUpdateTransfer;
import org.oscarehr.flowsheet.transfer.FlowsheetUpdateTransfer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FlowsheetTransferToEntityConverterTest
{
	@Autowired
	@InjectMocks
	private FlowsheetTransferToEntityConverter converter;

	@Mock
	protected DsRuleDao dsRuleDao;

	@Mock
	protected FlowsheetDao flowsheetDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testConverter_Null()
	{
		assertNull(converter.convert((FlowsheetCreateTransfer) null));
	}

	@Test
	public void testConverter_NewFlowsheet_noItems()
	{
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";
		boolean enabled = true;

		FlowsheetCreateTransfer transfer = buildSimpleCreateFlowsheetTransfer(expectedName, expectedDescription, null);
		transfer.setEnabled(enabled);

		Flowsheet flowsheet = converter.convert(transfer);

		assertNull(flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());
		assertEquals(enabled, flowsheet.isEnabled());
	}

	@Test
	public void testConverter_NewFlowsheet_NewSimpleGroup()
	{
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		flowsheetGroups.add(buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, null));

		FlowsheetCreateTransfer transfer = buildSimpleCreateFlowsheetTransfer(expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertNull(flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertNull(itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());
	}

	@Test
	public void testConverter_NewFlowsheet_NEwSimpleGroupWithItem()
	{
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";


		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		flowsheetGroupItems.add(buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, null));

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		flowsheetGroups.add(buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems));

		FlowsheetCreateTransfer transfer = buildSimpleCreateFlowsheetTransfer(expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertNull(flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertNull(itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());

		FlowsheetItem item = itemGroup.getFlowsheetItems().get(0);
		assertNull(item.getId());
		assertEquals(expectedItemName, item.getName());
		assertEquals(expectedItemDescription, item.getDescription());
		assertEquals(expectedItemGuideline, item.getGuideline());
	}

	@Test
	public void testConverter_NewFlowsheet_NewSimpleGroupWithItemAndRule()
	{
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";

		Integer sampleRuleId = 1;
		String sampleRuleName = "rule name";
		String sampleRuleDescription = "rule description";

		DsRule dsRuleMock = Mockito.mock(DsRule.class);
		Mockito.when(dsRuleDao.find(Mockito.anyInt())).thenReturn(dsRuleMock);
		Mockito.when(dsRuleMock.getId()).thenReturn(sampleRuleId);

		List<DsRuleUpdateInput> ruleList = new ArrayList<>();
		ruleList.add(buildSampleDsRuleInput(sampleRuleId, sampleRuleName, sampleRuleDescription));

		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		flowsheetGroupItems.add(buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList));

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		flowsheetGroups.add(buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems));

		FlowsheetCreateTransfer transfer = buildSimpleCreateFlowsheetTransfer(expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertNull(flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertNull(itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());

		FlowsheetItem item = itemGroup.getFlowsheetItems().get(0);
		assertNull(item.getId());
		assertEquals(expectedItemName, item.getName());
		assertEquals(expectedItemDescription, item.getDescription());
		assertEquals(expectedItemGuideline, item.getGuideline());

		assertEquals(1, item.getDsRules().size());
		Optional<DsRule> ruleOptional = item.getDsRules().stream().findFirst();
		assertTrue(ruleOptional.isPresent());
		assertSame(dsRuleMock, ruleOptional.get());
	}

	@Test
	public void testConverter_UpdateFlowsheet_NewSimpleGroupWithItemAndRule()
	{
		Integer expectedFlowsheetId = 100;
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";

		Integer sampleRuleId = 1;
		String sampleRuleName = "rule name";
		String sampleRuleDescription = "rule description";

		DsRule dsRuleMock = Mockito.mock(DsRule.class);
		Mockito.when(dsRuleDao.find(Mockito.anyInt())).thenReturn(dsRuleMock);
		Mockito.when(dsRuleMock.getId()).thenReturn(sampleRuleId);

		Flowsheet existingFlowsheet = new Flowsheet();
		existingFlowsheet.setId(expectedFlowsheetId);
		existingFlowsheet.setName("Old flowsheet name");
		existingFlowsheet.setDescription("Old flowsheet description");
		Mockito.when(flowsheetDao.find(Mockito.anyInt())).thenReturn(existingFlowsheet);

		List<DsRuleUpdateInput> ruleList = new ArrayList<>();
		ruleList.add(buildSampleDsRuleInput(sampleRuleId, sampleRuleName, sampleRuleDescription));

		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		flowsheetGroupItems.add(buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList));

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		flowsheetGroups.add(buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems));

		FlowsheetCreateTransfer transfer = buildSimpleUpdateFlowsheetTransfer(expectedFlowsheetId, expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertSame(existingFlowsheet, flowsheet);
		assertEquals(expectedFlowsheetId, flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		assertEquals(1, flowsheet.getFlowsheetItemGroups().size());
		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertNull(itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());

		FlowsheetItem item = itemGroup.getFlowsheetItems().get(0);
		assertNull(item.getId());
		assertEquals(expectedItemName, item.getName());
		assertEquals(expectedItemDescription, item.getDescription());
		assertEquals(expectedItemGuideline, item.getGuideline());

		assertEquals(1, item.getDsRules().size());
		Optional<DsRule> ruleOptional = item.getDsRules().stream().findFirst();
		assertTrue(ruleOptional.isPresent());
		assertSame(dsRuleMock, ruleOptional.get());
	}

	@Test
	public void testConverter_UpdateFlowsheet_NewSimpleGroup_removeOldGroup()
	{
		Integer expectedFlowsheetId = 100;
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";

		Integer sampleRuleId = 1;
		String sampleRuleName = "rule name";
		String sampleRuleDescription = "rule description";

		DsRule dsRuleMock = Mockito.mock(DsRule.class);
		Mockito.when(dsRuleDao.find(Mockito.anyInt())).thenReturn(dsRuleMock);
		Mockito.when(dsRuleMock.getId()).thenReturn(sampleRuleId);

		// build group
		FlowsheetItemGroup existingGroup = new FlowsheetItemGroup();
		existingGroup.setId(200);
		existingGroup.setName("existing group name");
		existingGroup.setDeletedAt(null);
		List<FlowsheetItemGroup> mockExistingGroups = new ArrayList<>();
		mockExistingGroups.add(existingGroup);

		// build flowsheet
		Flowsheet existingFlowsheet = new Flowsheet();
		existingFlowsheet.setId(expectedFlowsheetId);
		existingFlowsheet.setName("Old flowsheet name");
		existingFlowsheet.setDescription("Old flowsheet description");
		existingFlowsheet.setFlowsheetItemGroups(mockExistingGroups);
		Mockito.when(flowsheetDao.find(Mockito.anyInt())).thenReturn(existingFlowsheet);

		existingGroup.setFlowsheet(existingFlowsheet);

		// build transfers
		List<DsRuleUpdateInput> ruleList = new ArrayList<>();
		ruleList.add(buildSampleDsRuleInput(sampleRuleId, sampleRuleName, sampleRuleDescription));

		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		flowsheetGroupItems.add(buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList));

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		flowsheetGroups.add(buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems));

		FlowsheetCreateTransfer transfer = buildSimpleUpdateFlowsheetTransfer(expectedFlowsheetId, expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertSame(existingFlowsheet, flowsheet);
		assertEquals(expectedFlowsheetId, flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		// old group was not in the update, should be flagged as deleted
		assertEquals(2, flowsheet.getFlowsheetItemGroups().size());
		FlowsheetItemGroup oldGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertNotNull(oldGroup.getDeletedAt());

		// a new group was also created
		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(1);
		assertNull(itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());
	}

	@Test
	public void testConverter_UpdateFlowsheet_MergeOldGroupAndItems()
	{
		Integer expectedFlowsheetId = 100;
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		Integer expectedGroupId = 200;
		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		Integer expectedItemId = 300;
		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";

		Integer sampleRuleId = 1;
		String sampleRuleName = "rule name";
		String sampleRuleDescription = "rule description";

		// build rules
		DsRule dsRuleMock = Mockito.mock(DsRule.class);
		Mockito.when(dsRuleDao.find(Mockito.anyInt())).thenReturn(dsRuleMock);
		Mockito.when(dsRuleMock.getId()).thenReturn(sampleRuleId);

		// build item
		FlowsheetItem existingItem = new FlowsheetItem();
		existingItem.setId(expectedItemId);
		existingItem.setName("old item name");
		existingItem.setDescription("old item description");
		existingItem.setGuideline("old item guideline");
		List<FlowsheetItem> mockExistingItems = new ArrayList<>();
		mockExistingItems.add(existingItem);

		// build group
		FlowsheetItemGroup existingGroup = new FlowsheetItemGroup();
		existingGroup.setId(expectedGroupId);
		existingGroup.setName("existing group name");
		existingGroup.setDescription("existing group description");
		existingGroup.setFlowsheetItems(mockExistingItems);
		List<FlowsheetItemGroup> mockExistingGroups = new ArrayList<>();
		mockExistingGroups.add(existingGroup);

		// build flowsheet
		Flowsheet existingFlowsheet = new Flowsheet();
		existingFlowsheet.setId(expectedFlowsheetId);
		existingFlowsheet.setName("Old flowsheet name");
		existingFlowsheet.setDescription("Old flowsheet description");
		existingFlowsheet.setFlowsheetItemGroups(mockExistingGroups);
		Mockito.when(flowsheetDao.find(Mockito.anyInt())).thenReturn(existingFlowsheet);

		existingItem.setFlowsheetItemGroup(existingGroup);
		existingItem.setFlowsheet(existingFlowsheet);
		existingGroup.setFlowsheet(existingFlowsheet);

		// build transfer
		List<DsRuleUpdateInput> ruleList = new ArrayList<>();
		ruleList.add(buildSampleDsRuleInput(sampleRuleId, sampleRuleName, sampleRuleDescription));

		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		FlowsheetItemCreateUpdateTransfer itemUpdateTransfer = buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList);
		itemUpdateTransfer.setId(expectedItemId);
		FlowsheetItemCreateUpdateTransfer itemCreateTransfer = buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList);
		flowsheetGroupItems.add(itemUpdateTransfer);
		flowsheetGroupItems.add(itemCreateTransfer);

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		FlowsheetItemGroupCreateUpdateTransfer groupUpdateTransfer = buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems);
		groupUpdateTransfer.setId(expectedGroupId);
		flowsheetGroups.add(groupUpdateTransfer);

		FlowsheetCreateTransfer transfer = buildSimpleUpdateFlowsheetTransfer(expectedFlowsheetId, expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertSame(existingFlowsheet, flowsheet);
		assertEquals(expectedFlowsheetId, flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		// existing group is merged
		assertEquals(1, flowsheet.getFlowsheetItemGroups().size());
		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertEquals(expectedGroupId, itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());

		// existing item is merged
		assertEquals(2, itemGroup.getFlowsheetItems().size());
		FlowsheetItem updatedItem = itemGroup.getFlowsheetItems().get(0);
		assertEquals(expectedItemId, updatedItem.getId());
		assertEquals(expectedItemName, updatedItem.getName());
		assertEquals(expectedItemDescription, updatedItem.getDescription());
		assertEquals(expectedItemGuideline, updatedItem.getGuideline());

		// new item is also created
		FlowsheetItem newItem = itemGroup.getFlowsheetItems().get(1);
		assertNull(newItem.getId());
		assertEquals(expectedItemName, newItem.getName());
		assertEquals(expectedItemDescription, newItem.getDescription());
		assertEquals(expectedItemGuideline, newItem.getGuideline());
	}

	@Test
	public void testConverter_UpdateFlowsheet_MergeOldGroupAndDeleteOldItems()
	{
		Integer expectedFlowsheetId = 100;
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		Integer expectedGroupId = 200;
		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		Integer expectedItemId = 300;
		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";

		Integer sampleRuleId = 1;
		String sampleRuleName = "rule name";
		String sampleRuleDescription = "rule description";

		// build rules
		DsRule dsRuleMock = Mockito.mock(DsRule.class);
		Mockito.when(dsRuleDao.find(Mockito.anyInt())).thenReturn(dsRuleMock);
		Mockito.when(dsRuleMock.getId()).thenReturn(sampleRuleId);

		// build item
		FlowsheetItem existingItem = new FlowsheetItem();
		existingItem.setId(expectedItemId);
		existingItem.setName("old item name");
		existingItem.setDescription("old item description");
		existingItem.setGuideline("old item guideline");
		List<FlowsheetItem> mockExistingItems = new ArrayList<>();
		mockExistingItems.add(existingItem);

		// build group
		FlowsheetItemGroup existingGroup = new FlowsheetItemGroup();
		existingGroup.setId(expectedGroupId);
		existingGroup.setName("existing group name");
		existingGroup.setDescription("existing group description");
		existingGroup.setFlowsheetItems(mockExistingItems);
		List<FlowsheetItemGroup> mockExistingGroups = new ArrayList<>();
		mockExistingGroups.add(existingGroup);

		// build flowsheet
		Flowsheet existingFlowsheet = new Flowsheet();
		existingFlowsheet.setId(expectedFlowsheetId);
		existingFlowsheet.setName("Old flowsheet name");
		existingFlowsheet.setDescription("Old flowsheet description");
		existingFlowsheet.setFlowsheetItemGroups(mockExistingGroups);
		Mockito.when(flowsheetDao.find(Mockito.anyInt())).thenReturn(existingFlowsheet);

		existingItem.setFlowsheetItemGroup(existingGroup);
		existingItem.setFlowsheet(existingFlowsheet);
		existingGroup.setFlowsheet(existingFlowsheet);

		// build transfer
		List<DsRuleUpdateInput> ruleList = new ArrayList<>();
		ruleList.add(buildSampleDsRuleInput(sampleRuleId, sampleRuleName, sampleRuleDescription));

		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		flowsheetGroupItems.add(buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList));

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		FlowsheetItemGroupCreateUpdateTransfer groupUpdateTransfer = buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems);
		groupUpdateTransfer.setId(expectedGroupId);
		flowsheetGroups.add(groupUpdateTransfer);

		FlowsheetCreateTransfer transfer = buildSimpleUpdateFlowsheetTransfer(expectedFlowsheetId, expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertSame(existingFlowsheet, flowsheet);
		assertEquals(expectedFlowsheetId, flowsheet.getId());
		assertEquals(expectedName, flowsheet.getName());
		assertEquals(expectedDescription, flowsheet.getDescription());

		// existing group is merged
		assertEquals(1, flowsheet.getFlowsheetItemGroups().size());
		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertEquals(expectedGroupId, itemGroup.getId());
		assertEquals(expectedGroupName, itemGroup.getName());
		assertEquals(expectedGroupDescription, itemGroup.getDescription());

		// deleted at must be set on the old item
		assertEquals(2, itemGroup.getFlowsheetItems().size());
		FlowsheetItem updatedItem = itemGroup.getFlowsheetItems().get(0);
		assertEquals(expectedItemId, updatedItem.getId());
		assertNotNull(updatedItem.getDeletedAt());

		// the inbound transfer creates a new item
		FlowsheetItem newItem = itemGroup.getFlowsheetItems().get(1);
		assertNull(newItem.getId());
		assertEquals(expectedItemName, newItem.getName());
		assertEquals(expectedItemDescription, newItem.getDescription());
		assertEquals(expectedItemGuideline, newItem.getGuideline());
	}

	@Test
	public void testConverter_UpdateFlowsheet_MergeOldGroupAndItems_EntityCheck()
	{
		Integer expectedFlowsheetId = 100;
		String expectedName = "flowsheetName";
		String expectedDescription = "flowsheetDescription";

		Integer expectedGroupId = 200;
		String expectedGroupName = "groupName";
		String expectedGroupDescription = "groupDescription";

		Integer expectedItemId = 300;
		String expectedItemName = "itemName";
		String expectedItemDescription = "itemDescription";
		String expectedItemGuideline = "itemGuideline";

		Integer sampleRuleId = 1;
		String sampleRuleName = "rule name";
		String sampleRuleDescription = "rule description";

		// build rules
		DsRule dsRuleMock = Mockito.mock(DsRule.class);
		Mockito.when(dsRuleDao.find(Mockito.anyInt())).thenReturn(dsRuleMock);
		Mockito.when(dsRuleMock.getId()).thenReturn(sampleRuleId);

		// build item
		FlowsheetItem existingItem = new FlowsheetItem();
		existingItem.setId(expectedItemId);
		List<FlowsheetItem> mockExistingItems = new ArrayList<>();
		mockExistingItems.add(existingItem);

		// build group
		FlowsheetItemGroup existingGroup = new FlowsheetItemGroup();
		existingGroup.setId(expectedGroupId);
		existingGroup.setFlowsheetItems(mockExistingItems);
		List<FlowsheetItemGroup> mockExistingGroups = new ArrayList<>();
		mockExistingGroups.add(existingGroup);

		// build flowsheet
		Flowsheet existingFlowsheet = new Flowsheet();
		existingFlowsheet.setId(expectedFlowsheetId);
		existingFlowsheet.setFlowsheetItemGroups(mockExistingGroups);
		Mockito.when(flowsheetDao.find(Mockito.anyInt())).thenReturn(existingFlowsheet);

		existingItem.setFlowsheetItemGroup(existingGroup);
		existingItem.setFlowsheet(existingFlowsheet);
		existingGroup.setFlowsheet(existingFlowsheet);

		// build transfer
		List<DsRuleUpdateInput> ruleList = new ArrayList<>();
		ruleList.add(buildSampleDsRuleInput(sampleRuleId, sampleRuleName, sampleRuleDescription));

		List<FlowsheetItemCreateUpdateTransfer> flowsheetGroupItems = new ArrayList<>();
		FlowsheetItemCreateUpdateTransfer itemUpdateTransfer = buildSimpleFlowsheetItemTransfer(expectedItemName, expectedItemDescription, expectedItemGuideline, ruleList);
		itemUpdateTransfer.setId(expectedItemId);
		flowsheetGroupItems.add(itemUpdateTransfer);

		List<FlowsheetItemGroupCreateUpdateTransfer> flowsheetGroups = new ArrayList<>();
		FlowsheetItemGroupCreateUpdateTransfer groupUpdateTransfer = buildSimpleFlowsheetGroupTransfer(expectedGroupName, expectedGroupDescription, flowsheetGroupItems);
		groupUpdateTransfer.setId(expectedGroupId);
		flowsheetGroups.add(groupUpdateTransfer);

		FlowsheetCreateTransfer transfer = buildSimpleUpdateFlowsheetTransfer(expectedFlowsheetId, expectedName, expectedDescription, flowsheetGroups);

		Flowsheet flowsheet = converter.convert(transfer);

		assertSame(existingFlowsheet, flowsheet);

		// group entity relations match
		assertEquals(1, flowsheet.getFlowsheetItemGroups().size());
		FlowsheetItemGroup itemGroup = flowsheet.getFlowsheetItemGroups().get(0);
		assertSame(flowsheet, itemGroup.getFlowsheet());

		// item entity relations match
		assertEquals(1, itemGroup.getFlowsheetItems().size());
		FlowsheetItem updatedItem = itemGroup.getFlowsheetItems().get(0);
		assertSame(flowsheet, updatedItem.getFlowsheet());
		assertSame(itemGroup, updatedItem.getFlowsheetItemGroup());

		// rule entity relations match
		assertEquals(1, updatedItem.getDsRules().size());
		Optional<DsRule> ruleOptional = updatedItem.getDsRules().stream().findFirst();
		assertTrue(ruleOptional.isPresent());
		assertSame(dsRuleMock, ruleOptional.get());
	}

	private FlowsheetCreateTransfer buildSimpleCreateFlowsheetTransfer(String name, String description, List<FlowsheetItemGroupCreateUpdateTransfer> groups)
	{
		FlowsheetCreateTransfer transfer = new FlowsheetCreateTransfer();
		transfer.setName(name);
		transfer.setDescription(description);
		transfer.setFlowsheetItemGroups(groups);
		return transfer;
	}

	private FlowsheetCreateTransfer buildSimpleUpdateFlowsheetTransfer(Integer id, String name, String description, List<FlowsheetItemGroupCreateUpdateTransfer> groups)
	{
		FlowsheetUpdateTransfer transfer = new FlowsheetUpdateTransfer();
		transfer.setId(id);
		transfer.setName(name);
		transfer.setDescription(description);
		transfer.setFlowsheetItemGroups(groups);
		return transfer;
	}

	private FlowsheetItemGroupCreateUpdateTransfer buildSimpleFlowsheetGroupTransfer(String name, String description, List<FlowsheetItemCreateUpdateTransfer> items)
	{
		FlowsheetItemGroupCreateUpdateTransfer groupTransfer = new FlowsheetItemGroupCreateUpdateTransfer();
		groupTransfer.setName(name);
		groupTransfer.setDescription(description);
		groupTransfer.setFlowsheetItems(items);

		return groupTransfer;
	}

	private FlowsheetItemCreateUpdateTransfer buildSimpleFlowsheetItemTransfer(String name, String description, String guideline, List<DsRuleUpdateInput> rules)
	{
		FlowsheetItemCreateUpdateTransfer itemTransfer = new FlowsheetItemCreateUpdateTransfer();
		itemTransfer.setName(name);
		itemTransfer.setDescription(description);
		itemTransfer.setGuideline(guideline);
		itemTransfer.setRules(rules);

		return itemTransfer;
	}

	private DsRuleUpdateInput buildSampleDsRuleInput(Integer id, String name, String description)
	{
		DsRuleUpdateInput ruleTransfer = new DsRuleUpdateInput();
		ruleTransfer.setId(id);
		ruleTransfer.setName(name);
		ruleTransfer.setDescription(description);

		DsRuleConditionUpdateInput conditon = new DsRuleConditionUpdateInput();
		conditon.setId(1);
		conditon.setType(ConditionType.NEVER_GIVEN);
		ruleTransfer.setConditions(Arrays.asList(conditon));

		DsRuleConsequenceUpdateInput consequence = new DsRuleConsequenceUpdateInput();
		consequence.setId(1);
		consequence.setType(ConsequenceType.HIDDEN);
		consequence.setSeverityLevel(SeverityLevel.RECOMMENDATION);
		ruleTransfer.setConsequences(Arrays.asList(consequence));

		return ruleTransfer;
	}
}
