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
package org.oscarehr.flowsheet.service;


import org.oscarehr.flowsheet.converter.FlowsheetEntityToModelConverter;
import org.oscarehr.flowsheet.dao.FlowsheetDao;
import org.oscarehr.flowsheet.entity.ItemType;
import org.oscarehr.flowsheet.entity.ValueType;
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.model.FlowsheetItem;
import org.oscarehr.flowsheet.model.FlowsheetItemGroup;
import org.oscarehr.flowsheet.model.RecommendationRule;
import org.oscarehr.flowsheet.model.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetService
{
	@Autowired
	private FlowsheetDao flowsheetDao;

	@Autowired
	private FlowsheetEntityToModelConverter flowsheetEntityToModelConverter;


	public List<Flowsheet> getFlowsheets(int offset, int perPage)
	{
		return flowsheetEntityToModelConverter.convert(flowsheetDao.findAll(offset, perPage));
	}

	public Flowsheet getFlowsheet(Integer flowsheetId)
	{
		return flowsheetEntityToModelConverter.convert(flowsheetDao.find(flowsheetId));
	}

	private Flowsheet dummyFlowsheet(Integer id)
	{
		Flowsheet flowsheet = new Flowsheet();
		flowsheet.setId(id);
		flowsheet.setName("sample flowsheet");
		flowsheet.setDescription("flowsheet description text goes here");

		FlowsheetItemGroup flowsheetItemGroup = new FlowsheetItemGroup();
		flowsheetItemGroup.setName("item Group");
		flowsheetItemGroup.setDescription("This represents a grouping of similar or related flowsheet items");

		FlowsheetItem flowsheetItem1 = dummyMeasurementItem(100, "Review Blood Glucose Records", "REBG",
				"Fasting or pre-meal glucose level 4-7; 2hrs after meal 5-10", ValueType.STRING);
		FlowsheetItem flowsheetItem2 = dummyMeasurementItem(200, "Education Nutrition", "EDNL", null, ValueType.BOOLEAN);

		flowsheetItemGroup.setFlowsheetItems(Arrays.asList(flowsheetItem1, flowsheetItem2));


		FlowsheetItemGroup flowsheetItemGroup2 = new FlowsheetItemGroup();
		flowsheetItemGroup2.setDescription("This represents a single element without specific a grouping");


		FlowsheetItem flowsheetItem3 = dummyMeasurementItem(300, "BMI", "BMI", "Target: 18.5 - 24.9 (kg/m<sup>2</sup>)", ValueType.STRING);
		flowsheetItemGroup2.setFlowsheetItems(Arrays.asList(flowsheetItem3));

		ValidationRule validationRule = new ValidationRule();
		validationRule.setValidationRegex("([0-9]+)\\/([0-9]+)");
		validationRule.setValidationFailMessage("Invalid data format");

		flowsheetItem3.setValidationRules(Arrays.asList(validationRule));


		RecommendationRule recommendationRule = new RecommendationRule();
		recommendationRule.setMessage("Value should be within range 0-10");
		recommendationRule.setStrength(RecommendationRule.Strength.RECOMMENDATION);
		flowsheetItem3.setRecommendationRules(Arrays.asList(recommendationRule));

		flowsheet.setFlowsheetItemGroups(Arrays.asList(flowsheetItemGroup, flowsheetItemGroup2));
		return flowsheet;
	}

	private FlowsheetItem dummyMeasurementItem(Integer id, String name, String typeCode, String guideline, ValueType valueType)
	{
		FlowsheetItem flowsheetItem = new FlowsheetItem();
		flowsheetItem.setId(id);
		flowsheetItem.setName(name);
		flowsheetItem.setType(ItemType.MEASUREMENT);
		flowsheetItem.setTypeCode(typeCode);

		flowsheetItem.setValueType(valueType);

		return flowsheetItem;
	}
}
