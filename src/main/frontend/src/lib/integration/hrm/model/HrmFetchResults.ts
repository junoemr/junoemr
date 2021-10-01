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
import {HRMFetchResults} from "../../../../../generated";

export enum HRMStatus
{
    ERROR = -1,
    HAS_ERRORS,
    SUCCESS
}

export default class HrmFetchResults
{
    private readonly _reportsDownloaded: number;
    private readonly _reportsProcessed: number;
    private readonly _startTime: Moment;
    private readonly _endTime: Moment;
    private readonly _loginSuccess: boolean;
    private readonly _downloadSuccess: boolean;
    private readonly _processingSuccess: boolean;

    public constructor(transfer: HRMFetchResults)
    {
        this._reportsProcessed = transfer.reportsProcessed;
        this._reportsDownloaded = transfer.reportsDownloaded;

        this._loginSuccess = transfer.loginSuccess;
        this._downloadSuccess = transfer.downloadSuccess;
        this._processingSuccess = transfer.processingSuccess;

        this._startTime = moment(transfer.startTime);
        this._endTime = moment(transfer.endTime);
    }

    get reportsDownloadedCount(): number
    {
        return this._reportsDownloaded;
    }

    get reportsProcessedCount(): number
    {
        return this._reportsProcessed;
    }

    get startTime(): Moment
    {
        return this._startTime;
    }

    get endTime(): Moment
    {
        return this._endTime;
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
}
