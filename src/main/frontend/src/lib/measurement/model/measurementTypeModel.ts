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

export default class MeasurementTypeModel
{
	private readonly _id: number;
	private _name: string;
	private _code: string;
	private _description: string;
	private _instructions: string;

	constructor(id)
	{
		this._id = id;
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

	get code(): string
	{
		return this._code;
	}

	set code(value: string)
	{
		this._code = value;
	}

	get description(): string
	{
		return this._description;
	}

	set description(value: string)
	{
		this._description = value;
	}

	get instructions(): string
	{
		return this._instructions;
	}

	set instructions(value: string)
	{
		this._instructions = value;
	}
}