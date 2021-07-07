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

import FlowsheetItemGroupModel from "./FlowsheetItemGroupModel";
import DxCodeModel from "../../dx/model/DxCodeModel";

export default class FlowsheetModel
{
	private _id: number;
	private _name: string;
	private _description: string;
	private _enabled: boolean;
	private _systemManaged: boolean;
	private _flowsheetItemGroups: FlowsheetItemGroupModel[];
	private _triggerCodes: DxCodeModel[];

	public constructor()
	{
		this.enabled = true;
		this.systemManaged = false;
		this.flowsheetItemGroups = [];
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

	get flowsheetItemGroups(): FlowsheetItemGroupModel[]
	{
		return this._flowsheetItemGroups;
	}

	set flowsheetItemGroups(value: FlowsheetItemGroupModel[])
	{
		this._flowsheetItemGroups = value;
	}

	get triggerCodes(): DxCodeModel[]
	{
		return this._triggerCodes;
	}

	set triggerCodes(value: DxCodeModel[])
	{
		this._triggerCodes = value;
	}
}
