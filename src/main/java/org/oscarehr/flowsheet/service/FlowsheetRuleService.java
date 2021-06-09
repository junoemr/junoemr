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


import org.oscarehr.flowsheet.entity.Flowsheet;
import org.oscarehr.flowsheet.entity.FlowsheetItem;
import org.oscarehr.flowsheet.entity.FlowsheetRule;
import org.oscarehr.flowsheet.entity.FlowsheetRuleCondition;
import org.oscarehr.flowsheet.entity.FlowsheetRuleConsequence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarEncounter.oscarMeasurements.MeasurementInfo;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FlowsheetRuleService
{
	public void fillMeasurementInfo(MeasurementInfo measurementInfo, Flowsheet flowsheetEntity)
	{
		for(FlowsheetItem flowsheetItem : flowsheetEntity.getFlowsheetItems())
		{
			for(FlowsheetRule rule : flowsheetItem.getFlowsheetRules())
			{
				applyRule(measurementInfo, flowsheetItem, rule);
			}
		}
	}

	private void applyRule(MeasurementInfo measurementInfo, FlowsheetItem flowsheetItem, FlowsheetRule rule)
	{
		String measurementType = flowsheetItem.getTypeCode();
		for(FlowsheetRuleCondition condition : rule.getConditions())
		{
			switch(condition.getType())
			{
				case MONTHS_SINCE:
				{
					int monthsSince = measurementInfo.getLastDateRecordedInMonths(measurementType);
					if(monthsSince >= Integer.parseInt(condition.getValue()))
					{
						applyConsequences(measurementInfo, measurementType, rule.getConsequences());
					}
					break;
				}
				case NEVER_GIVEN:
				{
					int monthsSince = measurementInfo.getLastDateRecordedInMonths(measurementType);
					if(monthsSince < 0)
					{
						applyConsequences(measurementInfo, measurementType, rule.getConsequences());
					}
					break;
				}
			}
		}
	}

	private void applyConsequences(MeasurementInfo measurementInfo, String measurementType, List<FlowsheetRuleConsequence> consequences)
	{
		for(FlowsheetRuleConsequence consequence : consequences)
		{
			switch(consequence.getType())
			{
				case ALERT:
				{
					switch(consequence.getSeverityLevel())
					{
						case RECOMMENDATION:
						{
							measurementInfo.addRecommendation(measurementType, consequence.getMessage()); break;
						}
						case WARNING:
						case DANGER:
						{
							measurementInfo.addWarning(measurementType, consequence.getMessage()); break;
						}
					}
					break;
				}
				case HIDDEN:
				{
					measurementInfo.addHidden(measurementType, true);
					break;
				}
			}
		}
	}
}
