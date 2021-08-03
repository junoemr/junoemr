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

import {Moment} from "moment";

export default class MeasurementModel
{
	private _id: number;
	private _comment: string;
	private _createdDateTime: Moment;
	private _unit: string;
	private _value: string;
	private _instruction: string;
	private _observationDateTime: Moment;
	private _typeCode: string;

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get comment(): string
	{
		return this._comment;
	}

	set comment(value: string)
	{
		this._comment = value;
	}

	get createdDateTime(): Moment
	{
		return this._createdDateTime;
	}

	set createdDateTime(value: Moment)
	{
		this._createdDateTime = value;
	}

	get unit(): string
	{
		return this._unit;
	}

	set unit(value: string)
	{
		this._unit = value;
	}

	get value(): string
	{
		return this._value;
	}

	set value(value: string)
	{
		this._value = value;
	}

	get instruction(): string
	{
		return this._instruction;
	}

	set instruction(value: string)
	{
		this._instruction = value;
	}

	get observationDateTime(): Moment
	{
		return this._observationDateTime;
	}

	set observationDateTime(value: Moment)
	{
		this._observationDateTime = value;
	}

	get typeCode(): string
	{
		return this._typeCode;
	}

	set typeCode(value: string)
	{
		this._typeCode = value;
	}
}