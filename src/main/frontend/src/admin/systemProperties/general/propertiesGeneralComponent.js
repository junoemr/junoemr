/**
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

import {SystemPreferenceApi} from "../../../../generated/api/SystemPreferenceApi";

angular.module('Admin').component('systemPropertiesGeneral',
    {
        templateUrl: 'src/admin/systemProperties/general/propertiesGeneral.jsp',
        bindings: {},
        controller: ['$scope', '$http', '$httpParamSerializer', '$state', function ($scope, $http, $httpParamSerializer, $state)
        {
            let property_types =
                {
                    text: "string",
                }

            let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
            let ctrl = this;

            ctrl.propertiesList = [
                {
                    name: "Phone Prefix",
                    description: "Change the default phone number prefix",
                    propertyName: "phoneprefix",
                    type: property_types.text,
                    value: "250"
                }
            ];

           ctrl.$onInit = () =>
            {
                for (let property of ctrl.propertiesList)
                {
                    switch (property.type)
                    {
                        case property_types.text:
                        {
                            ctrl.loadTextType(property)
                            break;
                        }
                    }
                }
            };

            ctrl.loadTextType = (property) =>
            {
                systemPreferenceApi.getPreferenceValue(property.propertyName)
                .then((response) =>
                {
                    console.log(response);
                    property.value = response.data.body;
                    console.log(response.data.body);
                    console.log(property.value);
                })
            };

            /**
             * Persist new property value
             * @param property property to update
             * @param value set the phone prefix
             */
            ctrl.updateProperty = (property, value) =>
            {
                systemPreferenceApi.putPreferenceValue(property.propertyName, value)
            };
        }]
    });