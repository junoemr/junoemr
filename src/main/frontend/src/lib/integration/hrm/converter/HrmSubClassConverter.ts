/*
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

import {
	HrmSubClassModel,
	HRMSubClassTransferInbound
} from "../../../../../generated";
import HrmSubClass, {HrmReportClass} from "../model/HrmSubClass";

export default class HrmSubClassConverter
{
	public static fromTransfer(transfer: HrmSubClassModel): HrmSubClass
	{
		if (transfer == null)
		{
			return null;
		}

		const hrmSubClass = new HrmSubClass();
		hrmSubClass.id = transfer.id;
		hrmSubClass.hrmCategoryId = transfer.hrmCategoryId;
		hrmSubClass.facilityNumber = transfer.facilityNumber;
		hrmSubClass.reportClass = transfer.className as HrmReportClass;
		hrmSubClass.subClassName = transfer.subClassName;
		hrmSubClass.accompanyingSubClassName = transfer.accompanyingSubClassName;

		return hrmSubClass;
	}

	public static fromTransfers(transferList: HrmSubClassModel[]): HrmSubClass[]
	{
		if (!transferList)
		{
			return null;
		}

		return transferList.map(transfer => { return HrmSubClassConverter.fromTransfer(transfer)})
	}

	public static toTransfer(hrmSubClass: HrmSubClass): HRMSubClassTransferInbound
	{
		return {
			id: hrmSubClass.id,
			facilityNumber: hrmSubClass.facilityNumber,
			className: hrmSubClass.reportClass,
			subClassName: hrmSubClass.subClassName,
			accompanyingSubClassName: hrmSubClass.accompanyingSubClassName,
		}
	}

	public static toTransfers(subClasses: HrmSubClass[]): HRMSubClassTransferInbound[]
	{
		if (subClasses === null)
		{
			return null;
		}

		return subClasses.map(subClass => HrmSubClassConverter.toTransfer(subClass));
	}
}