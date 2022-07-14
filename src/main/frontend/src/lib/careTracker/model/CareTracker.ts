'use strict';

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

import CareTrackerItemGroup from "./CareTrackerItemGroup";
import DxCodeModel from "../../dx/model/DxCodeModel";

export default class CareTracker
{
	private _id: number;
	private _name: string;
	private _description: string;
	private _enabled: boolean;
	private _systemManaged: boolean;
	private _careTrackerItemGroups: CareTrackerItemGroup[];
	private _triggerCodes: DxCodeModel[];
	private _parentCareTrackerId: number;
	private _ownerDemographicId: number;
	private _ownerProviderId: string;

	public constructor()
	{
		this.enabled = true;
		this.systemManaged = false;
		this.careTrackerItemGroups = [];
		this.triggerCodes = [];
	}

	public isDemographicLevel(): boolean
	{
		return Boolean(this.ownerDemographicId);
	}
	public isProviderLevel(): boolean
	{
		return Boolean(this.ownerProviderId);
	}
	public isClinicLevel(): boolean
	{
		return !(this.isProviderLevel() || this.isDemographicLevel());
	}

	public getItemCount(): number
	{
		return this.careTrackerItemGroups.reduce((previousValue: number, group: CareTrackerItemGroup) =>
			previousValue + group.careTrackerItems.length, 0);
	}

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get name(): string
	{
		return this._name;
	}

	set name(value: string)
	{
		this._name = value;
	}

	get description(): string
	{
		return this._description;
	}

	set description(value: string)
	{
		this._description = value;
	}

	get enabled(): boolean
	{
		return this._enabled;
	}

	set enabled(value: boolean)
	{
		this._enabled = value;
	}

	get systemManaged(): boolean
	{
		return this._systemManaged;
	}

	set systemManaged(value: boolean)
	{
		this._systemManaged = value;
	}

	get careTrackerItemGroups(): CareTrackerItemGroup[]
	{
		return this._careTrackerItemGroups;
	}

	set careTrackerItemGroups(value: CareTrackerItemGroup[])
	{
		this._careTrackerItemGroups = value;
	}

	get triggerCodes(): DxCodeModel[]
	{
		return this._triggerCodes;
	}

	set triggerCodes(value: DxCodeModel[])
	{
		this._triggerCodes = value;
	}


	get parentCareTrackerId(): number
	{
		return this._parentCareTrackerId;
	}

	set parentCareTrackerId(value: number)
	{
		this._parentCareTrackerId = value;
	}

	get ownerDemographicId(): number
	{
		return this._ownerDemographicId;
	}

	set ownerDemographicId(value: number)
	{
		this._ownerDemographicId = value;
	}

	get ownerProviderId(): string
	{
		return this._ownerProviderId;
	}

	set ownerProviderId(value: string)
	{
		this._ownerProviderId = value;
	}
}
