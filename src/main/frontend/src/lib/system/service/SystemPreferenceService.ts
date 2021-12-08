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

import {IHttpParamSerializer, IHttpService} from "angular";
import {SystemPreferenceApi} from "../../../../generated";
import NoSuchPropertyError from "../../error/system/NoSuchPropertyError";

export default class SystemPreferenceService
{
	protected systemPreferenceApi: SystemPreferenceApi = null;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor($http: IHttpService, $httpParamSerializer: IHttpParamSerializer)
	{
		this.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
	}

	/**
	 * Get the value of system property setting, from the server.
	 * @param propertyName - the name of the property to fetch
	 * @throws NoSuchPropertyError - if the property cannot be found by the backend server.
	 * @return the value from the server
	 */
	public async getProperty(propertyName: string): Promise<any>
	{
		const value: any = (await this.systemPreferenceApi.getPropertyValue(propertyName, null)).data.body;

		if (value != null)
		{
			return value;
		}
		else
		{
			throw new NoSuchPropertyError(`Property [${propertyName}] is not defined on this Juno server!`);
		}
	}

	/**
	 * Get the value of all system properties specified
	 * @param propertyNames - an array of property names to fetch
	 * @return object consisting of {propertyName: propertyValue}
	 */
	public async getProperties(...propertyNames: string[]): Promise<any>
	{
		let propertyValues = await Promise.all(propertyNames.map(propertyName => this.getProperty(propertyName)));

		return propertyValues.reduce((propertyMap, propertyValue, index) => {
			propertyMap[propertyNames[index]] = propertyValue;
			return propertyMap;
		}, {});
	}


	/**
	 * Get the value of system property setting, from the server. Returning default if the server does not have a value
	 * for that property.  A property is defined in the instance's .properties file.
	 * @param propertyName - the name of the property to fetch
	 * @param defaultValue - a default value to use if the property is not set on the server.
	 * @return the value from the server or the default value provided
	 */
	public async getPropertyWithDefault(propertyName: string, defaultValue: any): Promise<any>
	{
		return (await this.systemPreferenceApi.getPropertyValue(propertyName, defaultValue)).data.body;
	}

  /**
   * Get the value of system preference setting from the server.  Unlike a property, a preference is stored in the database.
   * If the server does not have the value defined, return the default value instead.
   *
   * @param preferenceName - the name of the preference to fetch
   * @param defaultValue - a default value to use if the preference is not set on the server.
   * @return the value from the server or the default value provided
   */
	public async getPreferenceWithDefault(preferenceName: string, defaultValue: any): Promise<any>
  {
    return (await this.systemPreferenceApi.getPreferenceValue(preferenceName, defaultValue)).data.body;
  }

  /**
   * Determine if a system perference (stored in the database) is enabled.
   * An enabled value is any case-insensitive match on any of {"true", "on", "yes"}
   *
   * @param preferenceName - the name of the preference to fetch
   * @param defaultValue - a default value to use if the preference is not set on the server.
   * @return boolean true if enabled, false otherwise
   */
  public async isPreferenceEnabled(preferenceName: string): Promise<any>
  {
    return (await this.systemPreferenceApi.getPreferenceEnabled(preferenceName, false)).data.body
  }
}