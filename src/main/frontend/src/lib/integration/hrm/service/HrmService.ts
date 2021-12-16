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

import {API_BASE_PATH} from "../../../constants/ApiConstants";
import {
  HrmScheduleApi,
  HrmCategoriesApi,
  HrmCategoryApi,
  HrmSubClassApi
} from "../../../../../generated";
import HrmFetchResults from "../model/HrmFetchResults";
import HrmCategory from "../model/HRMCategory"

import {getAngular$http, getAngular$httpParamSerializer} from "../../../util/AngularUtil";
import HrmCategoryToTransferConverter from "../converter/HrmCategoryToTransferConverter";
import HrmSubClassFromTransferConverter from "../converter/HrmSubClassFromTransferConverter";
import HrmCategoryFromTransferConverter from "../converter/HrmCategoryFromTransferConverter";

export default class HrmService
{
    protected _hrmSchedulerApi: HrmScheduleApi = new HrmScheduleApi(getAngular$http(), getAngular$httpParamSerializer(), API_BASE_PATH);
    protected _hrmCategoryApi: HrmCategoryApi = new HrmCategoryApi(getAngular$http(), getAngular$httpParamSerializer(), API_BASE_PATH);
    protected _hrmCategoriesApi: HrmCategoriesApi = new HrmCategoriesApi(getAngular$http(), getAngular$httpParamSerializer(), API_BASE_PATH);
    protected _hrmSubClassApi: HrmSubClassApi = new HrmSubClassApi(getAngular$http(), getAngular$httpParamSerializer(), API_BASE_PATH);

    protected _hrmCategoryFromTransfer: HrmCategoryFromTransferConverter = new HrmCategoryFromTransferConverter();
    protected _hrmCategoryToTransfer: HrmCategoryToTransferConverter = new HrmCategoryToTransferConverter();
    protected _hrmSubClassFromTransfer: HrmSubClassFromTransferConverter = new HrmSubClassFromTransferConverter();

    // ==========================================================================
    // Public Methods
    // ==========================================================================

    public async fetchNewHRMDocuments(): Promise<HrmFetchResults>
    {
        const rawResponse = await this._hrmSchedulerApi.fetchNewDocuments();
        return new HrmFetchResults(rawResponse.data.body);
    }

    public async getLastResults(): Promise<HrmFetchResults>
    {
        const rawResponse = await this._hrmSchedulerApi.getLastFetchStatus();
        return new HrmFetchResults(rawResponse.data.body);
    }

    public async getActiveCategories(): Promise<HrmCategory[]>
    {
        const rawResponse = await this._hrmCategoriesApi.getActiveCategories()
        return this._hrmCategoryFromTransfer.convertList(rawResponse.data.body);
    }

    public async createCategory(category: HrmCategory): Promise<HrmCategory>
    {
      try
      {
        const rawResponse = await this._hrmCategoryApi.createCategory(this._hrmCategoryToTransfer.convert(category));
        return this._hrmCategoryFromTransfer.convert(rawResponse.data.body);
      }
      catch (err)
      {
        throw (err.data.error);
      }
    }

    public async getActiveCategory(id: number): Promise<HrmCategory>
    {
      const rawResponse = await this._hrmCategoryApi.getActiveCategory(id);
      return this._hrmCategoryFromTransfer.convert(rawResponse.data.body);
    }

    public async updateCategory(category: HrmCategory): Promise<HrmCategory>
    {
      try
      {
        const rawResponse = await this._hrmCategoryApi.updateCategory(category.id, this._hrmCategoryToTransfer.convert(category));
        return this._hrmCategoryFromTransfer.convert(rawResponse.data.body);
      }
      catch (err)
      {
        throw (err.data.error);
      }
    }

    public async deactivateCategory(category: HrmCategory): Promise<HrmCategory>
    {
      const rawResponse = await this._hrmCategoryApi.deactivateCategory(category.id);
      return this._hrmCategoryFromTransfer.convert(rawResponse.data.body);
    }

    public async findSubClassByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName): Promise<any>
    {
      const rawResponse = await this._hrmSubClassApi.findActiveByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName);
      return this._hrmSubClassFromTransfer.convert(rawResponse.data.body);
    }
}