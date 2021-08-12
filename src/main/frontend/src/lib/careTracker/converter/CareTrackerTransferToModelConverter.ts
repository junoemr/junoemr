/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

 */

import CareTrackerModel from "../model/CareTrackerModel";
import {CareTracker, CareTrackerItemGroup} from "../../../../generated";
import CareTrackerItemGroupModel from "../model/CareTrackerItemGroupModel";
import AbstractConverter from "../../conversion/AbstractConverter";
import DxCodeTransferToModelConverter from "../../dx/converter/DxCodeTransferToModelConverter";
import CareTrackerItemTransferToModelConverter from "./CareTrackerItemTransferToModelConverter";

export default class CareTrackerTransferToModelConverter extends AbstractConverter<CareTracker, CareTrackerModel>
{
	protected readonly careTrackerItemTransferToModelConverter = new CareTrackerItemTransferToModelConverter();
	protected readonly dxCodeTransferToModelConverter = new DxCodeTransferToModelConverter();

	public convert(careTracker: CareTracker): CareTrackerModel
	{
		if (!careTracker)
		{
			return null;
		}

		const careTrackerModel = new CareTrackerModel();
		careTrackerModel.id = careTracker.id;
		careTrackerModel.name = careTracker.name;
		careTrackerModel.description = careTracker.description;
		careTrackerModel.enabled = careTracker.enabled;
		careTrackerModel.systemManaged = careTracker.systemManaged;
		careTrackerModel.careTrackerItemGroups = this.convertAllGroups(careTracker.careTrackerItemGroups);
		careTrackerModel.triggerCodes = this.dxCodeTransferToModelConverter.convertList(careTracker.triggerCodes);
		careTrackerModel.parentCareTrackerId = careTracker.parentCareTrackerId;
		careTrackerModel.ownerProviderId = careTracker.ownerProviderId;
		careTrackerModel.ownerDemographicId = careTracker.ownerDemographicId;

		return careTrackerModel;
	}

	private convertAllGroups(itemGroups: CareTrackerItemGroup[]): CareTrackerItemGroupModel[]
	{
		return itemGroups.map(itemGroup =>
		{
			const groupModel = new CareTrackerItemGroupModel();
			groupModel.id = itemGroup.id;
			groupModel.name = itemGroup.name;
			groupModel.description = itemGroup.description;
			groupModel.careTrackerItems = this.careTrackerItemTransferToModelConverter.convertList(itemGroup.careTrackerItems);

			return groupModel;
		});
	}
}
