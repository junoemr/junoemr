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

import {Moment} from "moment";
import moment from "moment";

export enum HRMStatus
{
    ERROR = -1,
    HAS_ERRORS,
    SUCCESS
}

export default class HrmFetchResults
{
    private _reportsDownloaded: number;
    private _reportsProcessed: number;
    private _startTime: Moment;
    private _endTime: Moment;
    private _loginSuccess: boolean;
    private _downloadSuccess: boolean;
    private _processingSuccess: boolean;

    public constructor()
    {
    }

    public durationMS(): number
    {
        return this._endTime.diff(this._startTime);
    }

    public getLoginSummary(): HRMStatus
    {
        return this._loginSuccess ? HRMStatus.SUCCESS : HRMStatus.ERROR;
    }

    public getDownloadSummary(): HRMStatus
    {
        if (this._downloadSuccess)
        {
            return HRMStatus.SUCCESS;
        }
        else
        {
            if (this._reportsDownloaded > 0)
            {
                return HRMStatus.HAS_ERRORS;
            }
            return HRMStatus.ERROR;
        }
    }

    public getProcessingSummary(): HRMStatus
    {
        if (this._processingSuccess)
        {
            return HRMStatus.SUCCESS;
        }
        else
        {
            if (this._reportsProcessed > 0)
            {
                return HRMStatus.HAS_ERRORS;
            }
            return HRMStatus.ERROR;
        }
    }

	get reportsDownloaded(): number {
		return this._reportsDownloaded;
	}

	set reportsDownloaded(value: number) {
		this._reportsDownloaded = value;
	}

	get reportsProcessed(): number {
		return this._reportsProcessed;
	}

	set reportsProcessed(value: number) {
		this._reportsProcessed = value;
	}

	get startTime(): moment.Moment {
		return this._startTime;
	}

	set startTime(value: moment.Moment) {
		this._startTime = value;
	}

	get endTime(): moment.Moment {
		return this._endTime;
	}

	set endTime(value: moment.Moment) {
		this._endTime = value;
	}

	get loginSuccess(): boolean {
		return this._loginSuccess;
	}

	set loginSuccess(value: boolean) {
		this._loginSuccess = value;
	}

	get downloadSuccess(): boolean {
		return this._downloadSuccess;
	}

	set downloadSuccess(value: boolean) {
		this._downloadSuccess = value;
	}

	get processingSuccess(): boolean {
		return this._processingSuccess;
	}

	set processingSuccess(value: boolean) {
		this._processingSuccess = value;
	}
}
