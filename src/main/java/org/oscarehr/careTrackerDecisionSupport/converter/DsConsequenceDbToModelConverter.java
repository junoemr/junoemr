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
package org.oscarehr.careTrackerDecisionSupport.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.careTrackerDecisionSupport.entity.DsRuleConsequence;
import org.oscarehr.careTrackerDecisionSupport.model.consequence.ConsequenceAlert;
import org.oscarehr.careTrackerDecisionSupport.model.consequence.ConsequenceHideItemType;
import org.oscarehr.careTrackerDecisionSupport.model.consequence.DsConsequence;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DsConsequenceDbToModelConverter extends AbstractModelConverter<DsRuleConsequence, DsConsequence>
{
	@Override
	public DsConsequence convert(DsRuleConsequence input)
	{
		if(input == null)
		{
			return null;
		}
		DsConsequence consequence;
		switch(input.getType())
		{
			case ALERT: consequence = new ConsequenceAlert(); break;
			case HIDDEN: consequence = new ConsequenceHideItemType(); break;
			default: throw new IllegalStateException(input.getType() + " is not a valid consequence type");
		}

		BeanUtils.copyProperties(input, consequence);

		return consequence;
	}
}