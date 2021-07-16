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

import {FlowsheetItemAlert, FlowsheetItemData} from "../../../../generated";
import DsRuleModel from "../../decisionSupport/model/DsRuleModel";
import {FlowsheetItemType} from "./FlowsheetItemType";
import {FlowsheetItemValueType} from "./FlowsheetItemValueType";

export default class FlowsheetItemModel
{
	private _id: number;
	private _name: string;
	private _description: string;
	private _guideline: string;
	private _type: FlowsheetItemType;
	private _typeCode: string;
	private _hidden: boolean;
	private _valueType: FlowsheetItemValueType;
	private _valueLabel: string;
	private _flowsheetItemAlerts: FlowsheetItemAlert[];
	private _data: FlowsheetItemData[];
	private _rules: DsRuleModel[];

	public constructor()
	{
		this.flowsheetItemAlerts = [];
		this.data = [];
		this.rules = [];
	}

	public itemTypeIsPrevention = (): boolean =>
	{
		return this.type === FlowsheetItemType.PREVENTION;
	}

	public itemTypeIsMeasurement = (): boolean =>
	{
		return this.type === FlowsheetItemType.MEASUREMENT;
	}

	public valueTypeIsBoolean = (): boolean =>
	{
		return this.valueType === FlowsheetItemValueType.BOOLEAN;
	}

	public valueTypeIsFreeText = (): boolean =>
	{
		return this.valueType === FlowsheetItemValueType.STRING;
	}

	public valueTypeIsNumeric = (): boolean =>
	{
		return this.valueType === FlowsheetItemValueType.NUMERIC;
	}

	public valueTypeIsDate = (): boolean =>
	{
		return this.valueType === FlowsheetItemValueType.DATE;
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

	get guideline(): string
	{
		return this._guideline;
	}

	set guideline(value: string)
	{
		this._guideline = value;
	}

	get type(): FlowsheetItemType
	{
		return this._type;
	}

	set type(value: FlowsheetItemType)
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

	get valueType(): FlowsheetItemValueType
	{
		return this._valueType;
	}

	set valueType(value: FlowsheetItemValueType)
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

	get flowsheetItemAlerts(): FlowsheetItemAlert[]
	{
		return this._flowsheetItemAlerts;
	}

	set flowsheetItemAlerts(value: FlowsheetItemAlert[])
	{
		this._flowsheetItemAlerts = value;
	}

	get data(): FlowsheetItemData[]
	{
		return this._data;
	}

	set data(value: FlowsheetItemData[])
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
