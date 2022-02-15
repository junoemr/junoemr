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
package org.oscarehr.waitList.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.WaitingList;
import org.oscarehr.waitList.model.DemographicWaitListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class DemographicWaitListToModelConverter extends AbstractModelConverter<WaitingList, DemographicWaitListModel>
{
	@Autowired
	private WaitListNameToModelConverter waitListNameToModelConverter;

	@Override
	public DemographicWaitListModel convert(WaitingList input)
	{
		if(input == null)
		{
			return null;
		}
		DemographicWaitListModel model = new DemographicWaitListModel();
		model.setWaitListModel(waitListNameToModelConverter.convert(input.getWaitingListName()));
		model.setDateAddedToWaitList(ConversionUtils.toNullableLocalDate(input.getOnListSince()));
		model.setNote(input.getNote());
		model.setPosition(input.getPosition());
		model.setArchived(input.getIsHistory().equals("Y"));

		return model;
	}
}
