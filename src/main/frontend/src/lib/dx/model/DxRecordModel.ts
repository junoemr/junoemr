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

import {DxRecordStatus} from "./DxRecordStatus";
import DxCodeModel from "./DxCodeModel";

export default class DxRecordModel
{
	private _id: number;
	private _status: DxRecordStatus;
	private _dxCode: DxCodeModel;
	private _providerId: string;
	private _startDate: string;
	private _updateDate: string;

	public constructor()
	{

	}

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get status(): DxRecordStatus
	{
		return this._status;
	}

	set status(value: DxRecordStatus)
	{
		this._status = value;
	}

	get dxCode(): DxCodeModel
	{
		return this._dxCode;
	}

	set dxCode(value: DxCodeModel)
	{
		this._dxCode = value;
	}

	get providerId(): string
	{
		return this._providerId;
	}

	set providerId(value: string)
	{
		this._providerId = value;
	}

	get startDate(): string
	{
		return this._startDate;
	}

	set startDate(value: string)
	{
		this._startDate = value;
	}

	get updateDate(): string
	{
		return this._updateDate;
	}

	set updateDate(value: string)
	{
		this._updateDate = value;
	}
}