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


import org.oscarehr.flowsheet.converter.FlowsheetRuleDbToModelConverter;
import org.oscarehr.flowsheet.entity.Flowsheet;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarEncounter.oscarMeasurements.MeasurementInfo;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetRuleService
{
	@Autowired
	private FlowsheetRuleDbToModelConverter flowsheetRuleDbToModelConverter;

	public void applyFlowsheetRules(MeasurementInfo measurementInfo, Flowsheet flowsheetEntity)
	{
		for(FlowsheetItem flowsheetItem : flowsheetEntity.getFlowsheetItems())
		{
			// filter out rules that do not meet all conditions, and apply consequences for rules where all conditions are met.
			flowsheetRuleDbToModelConverter.convert(flowsheetItem.getFlowsheetRules())
					.stream()
					.filter((rule) -> rule.getConditions()
							.stream()
							.allMatch((condition) -> condition.meetsRequirements(flowsheetItem, measurementInfo)))
					.forEach((rule) -> rule.getConsequences()
							.forEach((consequence) -> consequence.apply(flowsheetItem, measurementInfo))
					);
		}
	}
}
