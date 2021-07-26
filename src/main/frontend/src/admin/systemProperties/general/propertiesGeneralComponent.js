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
import {SYSTEM_PROPERTIES} from "../../../common/services/systemPreferenceServiceConstants";

angular.module('Admin').component('systemPropertiesGeneral',
    {
        templateUrl: 'src/admin/systemProperties/general/propertiesGeneral.jsp',
        bindings: {},
        controller: [
            '$scope',
            '$http',
            '$httpParamSerializer',
	        '$uibModal',
            function ($scope, $http, $httpParamSerializer, $uibModal) {
                let propertyTypes =
                {
                    text: "string",
	                toggle: "boolean",
                }
	
	            $scope.PROPERTY_TYPES = propertyTypes;
                
                let ctrl = this;
                let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
                
	            ctrl.validations =
		            {
			            phonePrefixValid: (prefix) =>
			            {
				            const reg = new RegExp(/^[0-9]{3}-?$/);
				            const MIN_LENGTH = 3;
				            const MAX_LENGTH = 4;

				            return prefix === "" || !prefix ||
					            (prefix.match(reg) != null && (prefix.length >= MIN_LENGTH && prefix.length <= MAX_LENGTH));
			            }
		            };
	            
                ctrl.properties = [
                    {
                        name: "Phone Prefix",
                        description: "Change the default phone number prefix (XXX or XXX-)",
                        propertyName: SYSTEM_PROPERTIES.DEFAULT_PHONE_PREFIX,
	                    type: propertyTypes.text,
	                    value: "",
	                    validation: ctrl.validations.phonePrefixValid,
                    },
	                {
		                name: "Family Doctor & Demographic Rostering",
		                description: "Enable additional family doctor field in classic UI and demographic rostering section in Juno UI",
		                propertyName: SYSTEM_PROPERTIES.ROSTERING_MODULE,
		                type: propertyTypes.toggle,
		                value: false,
		                validation: false,
	                },
					{
	                	name: "Additional Demographic Address",
		                description: "Enable an additional demographic address",
		                propertyName: SYSTEM_PROPERTIES.EXTRA_ADDRESS_FIELD,
		                type: propertyTypes.toggle,
		                value: false,
		                validation: false,
	                }
                ];

               ctrl.$onInit = () =>
                {
                    for (let property of ctrl.properties)
                    {
                    	ctrl.loadProperty(property);
                    }
                };
               
                ctrl.loadProperty = (property) =>
                {
                	if (property.type === propertyTypes.toggle)
	                {
		                systemPreferenceApi.getPreferenceEnabled(property.propertyName)
			                .then((response) =>
			                {
				                property.value = response.data.body;
			                })
	                }
                	else
	                {
	                	systemPreferenceApi.getPreferenceValue(property.propertyName)
			                .then((response) =>
		                    {
			                    property.value = response.data.body;
		                    })
	                }

                };
                

                /**
                 * Persist new property value
                 * @param property property to update
                 * @param value set the value in the database
                 */
                ctrl.updateProperty = (property, value) =>
                {
                	if (property.validation && !property.validation(value))
                	{
		                return;
	                }
                	
                	systemPreferenceApi.putPreferenceValue(property.propertyName, value)
		                .then((response) =>
		                {
		                	if (property.type === propertyTypes.text)
			                {
				                Juno.Common.Util.successAlert($uibModal, "Success", property.name + " was updated");
			                }
		                })
		                .catch((error)=>
		                {
			                if (property.type === propertyTypes.text)
			                {
				                Juno.Common.Util.errorAlert($uibModal, "Error", property.name + " could not be updated");
			                }
		                })
                };
            }
        ]
    });