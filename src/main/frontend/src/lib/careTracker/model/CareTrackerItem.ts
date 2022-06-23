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

import DsRuleModel from "../../decisionSupport/model/DsRuleModel";
import {CareTrackerItemType} from "./CareTrackerItemType";
import {CareTrackerItemValueType} from "./CareTrackerItemValueType";
import Alert from "./Alert";
import CareTrackerItemData from "./CareTrackerItemData";

export default class CareTrackerItem
{
	private readonly _id: number;
	private _name: string;
	private _description: string;
	private _guideline: string;
	private _type: CareTrackerItemType;
	private _typeCode: string;
	private _hidden: boolean;
	private _valueType: CareTrackerItemValueType;
	private _valueLabel: string;
	private _careTrackerItemAlerts: Alert[];
	private _data: CareTrackerItemData[];
	private _rules: DsRuleModel[];

	public constructor(id: number = null)
	{
		this.careTrackerItemAlerts = [];
		this.data = [];
		this.rules = [];
		this._id = id;
	}

	public itemTypeIsPrevention(): boolean
	{
		return this.type === CareTrackerItemType.Prevention;
	}

	public itemTypeIsMeasurement(): boolean
	{
		return this.type === CareTrackerItemType.Measurement;
	}

	public valueTypeIsBoolean(): boolean
	{
		return this.valueType === CareTrackerItemValueType.Boolean;
	}

	public valueTypeIsFreeText(): boolean
	{
		return this.valueType === CareTrackerItemValueType.String;
	}

	public valueTypeIsNumeric(): boolean
	{
		return this.valueType === CareTrackerItemValueType.Numeric;
	}

	public valueTypeIsDate(): boolean
	{
		return this.valueType === CareTrackerItemValueType.Date;
	}

	public hasAttachedData(): boolean
	{
		return (this.data && this.data.length > 0);
	}

	public toString(): string
	{
		return this.name + " (" + this.typeCode + ")";
	}

	public sortDataByObservationDate(ascending: boolean = true): void
	{
		this.data = this.data.sort((itemA: CareTrackerItemData, itemB: CareTrackerItemData) =>
		{
			if(ascending)
			{
				// newest items at end of the list
				return itemA.observationDateTime.diff(itemB.observationDateTime);
			}
			else
			{
				// newest items at beginning of the list
				return itemB.observationDateTime.diff(itemA.observationDateTime);
			}
		});
	}

	get id(): number
	{
		return this._id;
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

	get guideline(): string
	{
		return this._guideline;
	}

	set guideline(value: string)
	{
		this._guideline = value;
	}

	get type(): CareTrackerItemType
	{
		return this._type;
	}

	set type(value: CareTrackerItemType)
	{
		this._type = value;
	}

	get typeCode(): string
	{
		return this._typeCode;
	}

	set typeCode(value: string)
	{
		this._typeCode = value;
	}

	get hidden(): boolean
	{
		return this._hidden;
	}

	set hidden(value: boolean)
	{
		this._hidden = value;
	}

	get valueType(): CareTrackerItemValueType
	{
		return this._valueType;
	}

	set valueType(value: CareTrackerItemValueType)
	{
		this._valueType = value;
	}

	get valueLabel(): string
	{
		return this._valueLabel;
	}

	set valueLabel(value: string)
	{
		this._valueLabel = value;
	}

	get careTrackerItemAlerts(): Alert[]
	{
		return this._careTrackerItemAlerts;
	}

	set careTrackerItemAlerts(value: Alert[])
	{
		this._careTrackerItemAlerts = value;
	}

	get data(): CareTrackerItemData[]
	{
		return this._data;
	}

	set data(value: CareTrackerItemData[])
	{
		this._data = value;
	}

	get rules(): DsRuleModel[]
	{
		return this._rules;
	}

	set rules(value: DsRuleModel[])
	{
		this._rules = value;
	}
}
