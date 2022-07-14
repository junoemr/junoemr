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

import org.oscarehr.careTracker.entity.CareTracker;
import org.oscarehr.careTracker.entity.CareTrackerItem;
import org.oscarehr.careTracker.entity.CareTrackerItemGroup;
import org.oscarehr.careTracker.model.CareTrackerItemGroupModel;
import org.oscarehr.careTracker.model.CareTrackerItemModel;
import org.oscarehr.careTracker.model.CareTrackerModel;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Icd9;
import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.dx.converter.Icd9EntityToDxCodeConverter;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class CareTrackerEntityToModelConverter extends AbstractModelConverter<CareTracker, CareTrackerModel>
{
	@Autowired
	private CareTrackerItemEntityToModelConverter careTrackerItemEntityToModelConverter;

	@Autowired
	private Icd9EntityToDxCodeConverter icd9EntityToDxCodeConverter;

	@Override
	public CareTrackerModel convert(CareTracker input)
	{
		CareTrackerModel careTrackerModel = new CareTrackerModel();
		BeanUtils.copyProperties(input, careTrackerModel,
				"careTrackerItems",
				"careTrackerItemGroups",
				"icd9Triggers",
				"parentCareTracker",
				"ownerProvider",
				"ownerDemographic");

		careTrackerModel.setCareTrackerItemGroups(buildGroups(input));
		careTrackerModel.setTriggerCodes(buildTriggers(input));

		careTrackerModel.setParentCareTrackerId(input.getOptionalParentCareTracker().map(CareTracker::getId).orElse(null));
		careTrackerModel.setOwnerProviderId(input.getOptionalOwnerProvider().map(ProviderData::getId).orElse(null));
		careTrackerModel.setOwnerDemographicId(input.getOptionalOwnerDemographic().map(Demographic::getId).orElse(null));

		return careTrackerModel;
	}

	private List<CareTrackerItemGroupModel> buildGroups(CareTracker input)
	{
		// add items by group
		List<CareTrackerItemGroupModel> groups = new LinkedList<>();
		for(CareTrackerItemGroup group: input.getCareTrackerItemGroups())
		{
			CareTrackerItemGroupModel groupModel = new CareTrackerItemGroupModel();
			groupModel.setId(group.getId());
			groupModel.setName(group.getName());
			groupModel.setDescription(group.getDescription());
			groupModel.setCareTrackerItems(careTrackerItemEntityToModelConverter.convert(group.getCareTrackerItems()));
			groups.add(groupModel);
		}

		// add un-grouped items
		for(CareTrackerItem item : input.getCareTrackerItems())
		{
			if(item.getCareTrackerItemGroup() == null)
			{
				List<CareTrackerItemModel> items = new ArrayList<>(1);
				items.add(careTrackerItemEntityToModelConverter.convert(item));
				CareTrackerItemGroupModel groupModel = new CareTrackerItemGroupModel();
				groupModel.setCareTrackerItems(items);
				groups.add(groupModel);
			}
		}
		return groups;
	}

	private List<DxCode> buildTriggers(CareTracker input)
	{
		Set<Icd9> icd9TriggerCodes = input.getIcd9Triggers();
		return icd9EntityToDxCodeConverter.convert(icd9TriggerCodes);
	}
}
