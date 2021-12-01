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
import HrmCategoryConverter from "../converter/HrmCategoryConverter";
import HrmSubClassConverter from "../converter/HrmSubClassConverter";

export default class HrmService
{
    protected _HRMSchedulerAPI: any;

    protected _HRMCategoryAPI: any;

    protected _HRMCategoriesAPI: any;

    protected _HRMSubClassAPI: any;

    // ==========================================================================
    // Public Methods
    // ==========================================================================

    public constructor() {
        this._HRMSchedulerAPI = new HrmScheduleApi(
            getAngular$http(),
            getAngular$httpParamSerializer(),
            API_BASE_PATH);

        this._HRMCategoryAPI = new HrmCategoryApi(
            getAngular$http(),
            getAngular$httpParamSerializer(),
            API_BASE_PATH);

        this._HRMCategoriesAPI = new HrmCategoriesApi(
            getAngular$http(),
            getAngular$httpParamSerializer(),
            API_BASE_PATH);

        this._HRMSubClassAPI = new HrmSubClassApi(
            getAngular$http(),
            getAngular$httpParamSerializer(),
            API_BASE_PATH);
    }

    public async fetchNewHRMDocuments(): Promise<any>
    {
        const rawResponse = await this._HRMSchedulerAPI.fetchNewDocuments();
        return new HrmFetchResults(rawResponse.data.body);
    }

    public async getLastResults(): Promise<any>
    {
        const rawResponse = await this._HRMSchedulerAPI.getLastFetchStatus();
        return new HrmFetchResults(rawResponse.data.body);
    }

    public async getActiveCategories(): Promise<any>
    {
        const rawResponse = await this._HRMCategoriesAPI.getActiveCategories()
        return HrmCategoryConverter.fromTransfers(rawResponse.data.body);
    }

    public async createCategory(category: HrmCategory): Promise<any>
    {
      const rawResponse = await this._HRMCategoryAPI.createCategory(HrmCategoryConverter.toTransfer(category));
      return HrmCategoryConverter.fromTransfer(rawResponse.data.body)
    }

    public async getActiveCategory(id: number): Promise<any>
    {
      const rawResponse = await this._HRMCategoryAPI.getActiveCategory(id);
      return HrmCategoryConverter.fromTransfer(rawResponse.data.body);
    }

    public async updateCategory(category: HrmCategory): Promise<any>
    {
      const rawResponse = await this._HRMCategoryAPI.updateCategory(category.id, HrmCategoryConverter.toTransfer(category));
      return HrmCategoryConverter.fromTransfer(rawResponse.data.body);
    }

    public async deactivateCategory(category: HrmCategory): Promise<any>
    {
      const rawResponse = await this._HRMCategoryAPI.deactivateCategory(category.id);
      return HrmCategoryConverter.fromTransfer(rawResponse.data.body);
    }

    public async findSubClassByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName): Promise<any>
    {
      const rawResponse = await this._HRMSubClassAPI.findActiveByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName);
      return HrmSubClassConverter.fromTransfer(rawResponse.data.body);
    }
}