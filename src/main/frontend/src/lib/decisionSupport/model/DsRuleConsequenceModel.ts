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

import {ConsequenceType} from "./DsConsequenceType";
import {ConsequenceSeverity} from "./DsConsequenceSeverity";

export default class DsRuleConsequenceModel
{
    private _id: number;
    private _message: string;
    private _severityLevel: ConsequenceSeverity;
    private _type: ConsequenceType;

    public constructor()
    {
        this.type = ConsequenceType.ALERT; // default type
        this.severityLevel = ConsequenceSeverity.RECOMMENDATION;
    }

    get id(): number
    {
        return this._id;
    }

    set id(value: number)
    {
        this._id = value;
    }

    get message(): string
    {
        return this._message;
    }

    set message(value: string)
    {
        this._message = value;
    }

    get severityLevel(): ConsequenceSeverity
    {
        return this._severityLevel;
    }

    set severityLevel(value: ConsequenceSeverity)
    {
        this._severityLevel = value;
    }

    get type(): ConsequenceType
    {
        return this._type;
    }

    set type(value: ConsequenceType)
    {
        this._type = value;
    }
}

