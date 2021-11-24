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

import {HRMCategoryTransferInbound, HRMCategoryTransferOutbound} from "../../../../../generated";

export default class HrmCategory
{
  private _id: number;
  private _name: string;

  public constructor()
  {
    this._id = null;
    this._name = null;
  }

  public static fromTransferArray(transferList: HRMCategoryTransferOutbound[]): HrmCategory[]
  {
    if (!transferList)
    {
      return null;
    }

    return transferList.map(HrmCategory.fromTransfer);
  }

  public static fromTransfer(transfer: HRMCategoryTransferOutbound): HrmCategory
  {
    if (!transfer)
    {
      return null;
    }

    const category = new HrmCategory();
    category._id = transfer.id;
    category._name = transfer.name;

    return category;
  }

  public static toTransfer(category: HrmCategory): HRMCategoryTransferInbound
  {
    return {
      name: category.name,
    }
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
}