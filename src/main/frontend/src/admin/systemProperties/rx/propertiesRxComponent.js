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

angular.module('Admin').component('systemPropertiesRx',
    {
        templateUrl: 'src/admin/systemProperties/rx/propertiesRx.jsp',
        bindings: {},
        controller: ['$scope', '$http', '$httpParamSerializer', '$state', function ($scope, $http, $httpParamSerializer, $state)
        {
            let property_types = {
                boolean: "boolean",
                text: "string",
            }

            let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
            let ctrl = this;

            ctrl.propertiesList = [
                {
                    name: "Signature stamping",
                    description: "Enable automatic signing of prescriptions when faxing.  Valid provider signatures must be uploaded",
                    propertyName: "rx_preset_signatures",
                    type: property_types.boolean,
                    value: false
                }
            ];

            ctrl.$onInit = () =>
            {
                for (let property of ctrl.propertiesList)
                {
                    switch (property.type)
                    {
                        case property_types.boolean:
                        {
                            ctrl.loadBooleanType(property)
                            break;
                        }
                        case property_types.string:
                        {
                            /** May not actually be needed, still figuring out what to do for string properties in the future **/
                            console.error("Not yet implemented");
                        }
                    }
                }
            };

            ctrl.loadBooleanType = (property) =>
            {
                systemPreferenceApi.getPreferenceEnabled(property.propertyName)
                    .then((response) =>
                    {
                        property.value = !!response.data.body;
                    })
            };

            /**
             * Persist new property value
             * @param property property to update
             * @param checked toggle switch state
             */
            ctrl.updateProperty = (property, checked) =>
            {
                systemPreferenceApi.putPreferenceValue(property.propertyName, checked)
            };

        }]
    });