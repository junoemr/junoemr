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
package org.oscarehr.decisionSupport2.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.decisionSupport2.entity.DsRuleCondition;
import org.oscarehr.decisionSupport2.model.condition.ConditionIsGender;
import org.oscarehr.decisionSupport2.model.condition.ConditionIsNotGender;
import org.oscarehr.decisionSupport2.model.condition.ConditionMonthsSince;
import org.oscarehr.decisionSupport2.model.condition.ConditionNeverGiven;
import org.oscarehr.decisionSupport2.model.condition.DsCondition;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DsConditionDbToModelConverter extends AbstractModelConverter<DsRuleCondition, DsCondition>
{
	@Override
	public DsCondition convert(DsRuleCondition input)
	{
		if(input == null)
		{
			return null;
		}
		DsCondition condition;
		switch(input.getType())
		{
			case NEVER_GIVEN: condition = new ConditionNeverGiven(); break;
			case MONTHS_SINCE: condition = new ConditionMonthsSince(); break;
			case IS_GENDER: condition = new ConditionIsGender(); break;
			case NOT_GENDER: condition = new ConditionIsNotGender(); break;
			default: throw new IllegalStateException("condition type " + input.getType() + " is not defined");
		}

		BeanUtils.copyProperties(input, condition);

		return condition;
	}
}
