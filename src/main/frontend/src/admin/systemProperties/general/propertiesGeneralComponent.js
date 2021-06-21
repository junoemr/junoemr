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
        controller: [
            '$scope',
            '$http',
            '$httpParamSerializer',
            function ($scope, $http, $httpParamSerializer) {
                let propertyTypes =
                {
                    text: "string",
                }

                let ctrl = this;
                let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');

                ctrl.phonePrefixValue = "";

                ctrl.propertiesList =
                [
                    {
                        name: "Phone Prefix",
                        description: "Change the default phone number prefix",
                        propertyName: "phone_prefix",
                        type: propertyTypes.text,
                        value: ""
                    }
                ];

               ctrl.$onInit = () =>
                {
                    for (let property of ctrl.propertiesList)
                    {
                        switch (property.type)
                        {
                            case propertyTypes.text:
                            {
                                ctrl.loadTextType(property);
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
                        ctrl.phonePrefixValue = response.data.body;
                    })
                };

                ctrl.validations =
                {
                    phonePrefixValid: Juno.Validations.validationCustom(() =>
                    {
                        const prefix = ctrl.phonePrefixValue;
                        const reg = new RegExp(/^[0-9]{3}-?$/);
                        const MIN_PREFIX_LENGTH = 3;
                        const MAX_PREFIX_LENGTH = 4;

                        if (prefix == null || prefix == "" ||
                                (prefix.match(reg) != null &&
                                (prefix.length >= MIN_PREFIX_LENGTH && prefix.length <= MAX_PREFIX_LENGTH)))
                        {
                            return true;
                        }
                        return false;
                    })
                };

                /**
                 * Persist new property value
                 * @param property property to update
                 * @param value set the value in the database
                 */
                ctrl.updateProperty = (property, value) =>
                {
                    systemPreferenceApi.putPreferenceValue(property.propertyName, value)
                };
            }
        ]
    });