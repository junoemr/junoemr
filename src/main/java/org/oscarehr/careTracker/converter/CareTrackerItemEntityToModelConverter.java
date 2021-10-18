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
package org.oscarehr.careTracker.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.careTrackerDecisionSupport.converter.DsRuleDbToModelConverter;
import org.oscarehr.careTracker.model.CareTrackerItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CareTrackerItemEntityToModelConverter extends AbstractModelConverter<org.oscarehr.careTracker.entity.CareTrackerItem, CareTrackerItem>
{
	@Autowired
	private DsRuleDbToModelConverter dsRuleDbToModelConverter;

	@Override
	public CareTrackerItem convert(org.oscarehr.careTracker.entity.CareTrackerItem input)
	{
		CareTrackerItem careTrackerItemModel = new CareTrackerItem();
		BeanUtils.copyProperties(input, careTrackerItemModel, "careTrackerItemGroup", "dsRules");

		careTrackerItemModel.setRules(dsRuleDbToModelConverter.convert(input.getDsRules()));

		return careTrackerItemModel;
	}
}
