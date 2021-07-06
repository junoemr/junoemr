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
import DsRuleConsequenceModel from "./DsRuleConsequenceModel";
import DsRuleConditionModel from "./DsRuleConditionModel";

export default class DsRuleModel
{
    private _id: number;
    private _name: string;
    private _description: string;
    private _systemManaged: boolean;
    private _conditions: DsRuleConditionModel[];
    private _consequences: DsRuleConsequenceModel[];

    public constructor()
    {
        this.systemManaged = false;
        this.conditions = [];
        this.consequences = [];
    }

    private _isValid = (): boolean =>
    {
        return ((this.conditions && this.conditions.length > 0)
            && (this.consequences && this.consequences.length > 0)
            && (!Juno.Common.Util.isBlank(this.name))
        );
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

    get systemManaged(): boolean
    {
        return this._systemManaged;
    }

    set systemManaged(value: boolean)
    {
        this._systemManaged = value;
    }

    get conditions(): DsRuleConditionModel[]
    {
        return this._conditions;
    }

    set conditions(value: DsRuleConditionModel[])
    {
        this._conditions = value;
    }

    get consequences(): DsRuleConsequenceModel[]
    {
        return this._consequences;
    }

    set consequences(value: DsRuleConsequenceModel[])
    {
        this._consequences = value;
    }

    get isValid(): () => boolean
    {
        return this._isValid;
    }

    set isValid(value: () => boolean)
    {
        this._isValid = value;
    }
}

