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

import CareTracker from "../model/CareTracker";
import {CareTrackerItemGroupModel, CareTrackerModel} from "../../../../generated";
import CareTrackerItemGroup from "../model/CareTrackerItemGroup";
import AbstractConverter from "../../conversion/AbstractConverter";
import DxCodeTransferToModelConverter from "../../dx/converter/DxCodeTransferToModelConverter";
import CareTrackerItemToModelConverter from "./CareTrackerItemToModelConverter";

export default class CareTrackerToModelConverter extends AbstractConverter<CareTrackerModel, CareTracker>
{
	protected readonly careTrackerItemToModelConverter = new CareTrackerItemToModelConverter();
	protected readonly dxCodeTransferToModelConverter = new DxCodeTransferToModelConverter();

	public convert(modelTransfer: CareTrackerModel): CareTracker
	{
		if (!modelTransfer)
		{
			return null;
		}

		const careTrackerModel = new CareTracker();
		careTrackerModel.id = modelTransfer.id;
		careTrackerModel.name = modelTransfer.name;
		careTrackerModel.description = modelTransfer.description;
		careTrackerModel.enabled = modelTransfer.enabled;
		careTrackerModel.systemManaged = modelTransfer.systemManaged;
		careTrackerModel.careTrackerItemGroups = this.convertAllGroups(modelTransfer.careTrackerItemGroups);
		careTrackerModel.triggerCodes = this.dxCodeTransferToModelConverter.convertList(modelTransfer.triggerCodes);
		careTrackerModel.parentCareTrackerId = modelTransfer.parentCareTrackerId;
		careTrackerModel.ownerProviderId = modelTransfer.ownerProviderId;
		careTrackerModel.ownerDemographicId = modelTransfer.ownerDemographicId;

		return careTrackerModel;
	}

	private convertAllGroups(itemGroups: CareTrackerItemGroupModel[]): CareTrackerItemGroup[]
	{
		return itemGroups.map(itemGroup =>
		{
			const groupModel = new CareTrackerItemGroup();
			groupModel.id = itemGroup.id;
			groupModel.name = itemGroup.name;
			groupModel.description = itemGroup.description;
			groupModel.careTrackerItems = this.careTrackerItemToModelConverter.convertList(itemGroup.careTrackerItems);

			return groupModel;
		});
	}
}
