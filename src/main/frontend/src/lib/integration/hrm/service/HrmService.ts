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
	HrmAccountApi,
	HrmCategoriesApi,
	HrmCategoryApi,
	HrmScheduleApi,
	HrmSubClassApi
} from "../../../../../generated";
import HrmFetchResults from "../model/HrmFetchResults";
import HrmCategory from "../model/HRMCategory"
import HrmCategoryToTransferConverter from "../converter/HrmCategoryToTransferConverter";
import HrmSubClassFromTransferConverter from "../converter/HrmSubClassFromTransferConverter";
import HrmCategoryFromTransferConverter from "../converter/HrmCategoryFromTransferConverter";
import HrmFetchResultsFromTransferConverter from "../converter/HrmFetchResultsFromTransferConverter";
import HrmSubClass from "../model/HrmSubClass";

export default class HrmService
{
	protected _hrmSchedulerApi: HrmScheduleApi;
	protected _hrmCategoryApi: HrmCategoryApi;
	protected _hrmCategoriesApi: HrmCategoriesApi;
	protected _hrmSubClassApi: HrmSubClassApi;
	protected _hrmAccountApi: HrmAccountApi;

	protected _hrmCategoryFromTransfer: HrmCategoryFromTransferConverter = new HrmCategoryFromTransferConverter();
	protected _hrmCategoryToTransfer: HrmCategoryToTransferConverter = new HrmCategoryToTransferConverter();
	protected _hrmSubClassFromTransfer: HrmSubClassFromTransferConverter = new HrmSubClassFromTransferConverter();
	protected _hrmFetchResultsFromTransfer: HrmFetchResultsFromTransferConverter = new HrmFetchResultsFromTransferConverter();

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this._hrmSchedulerApi = new HrmScheduleApi($http, $httpParamSerializer, API_BASE_PATH);
		this._hrmCategoryApi = new HrmCategoryApi($http, $httpParamSerializer, API_BASE_PATH);
		this._hrmCategoriesApi = new HrmCategoriesApi($http, $httpParamSerializer, API_BASE_PATH);
		this._hrmSubClassApi = new HrmSubClassApi($http, $httpParamSerializer, API_BASE_PATH);
		this._hrmAccountApi = new HrmAccountApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	// ==========================================================================
	// Public Methods
	// ==========================================================================
	public async fetchNewHRMDocuments(): Promise<HrmFetchResults>
	{
		const rawResponse = await this._hrmSchedulerApi.fetchNewDocuments();
		return this._hrmFetchResultsFromTransfer.convert(rawResponse.data.body);
	}

	public async getLastResults(): Promise<HrmFetchResults>
	{
		const rawResponse = await this._hrmSchedulerApi.getLastFetchStatus();
		return this._hrmFetchResultsFromTransfer.convert(rawResponse.data.body);
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

	public async findSubClassByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName): Promise<HrmSubClass>
	{
		const rawResponse = await this._hrmSubClassApi.findActiveByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName);
		return this._hrmSubClassFromTransfer.convert(rawResponse.data.body);
	}

	public async saveDecryptionKey(key: string): Promise<boolean>
	{
		const rawResponse = await this._hrmAccountApi.saveDecryptionKey(key);
		return rawResponse.data.body;
	}

	public async hasDecryptionKey(): Promise<boolean>
	{
		const rawResponse = await this._hrmAccountApi.hasDecryptionKey();
		return rawResponse.data.body;
	}
}