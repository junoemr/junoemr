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
				            
				            if (prefix)
				            {
				            	const valid2 = prefix.match(reg) != null && (prefix.length >= MIN_LENGTH && prefix.length <= MAX_LENGTH)
					            console.log(valid2);
				            }
				            
				            
				            return prefix === "" || !prefix ||
					            (prefix.match(reg) != null && (prefix.length >= MIN_LENGTH && prefix.length <= MAX_LENGTH));
			            }
		            };
	            
                ctrl.properties = [
                    {
                        name: "Phone Prefix",
                        description: "Change the default phone number prefix (XXX or XXX-)",
                        propertyName: "phone_prefix",
	                    type: propertyTypes.text,
	                    value: "",
	                    validation: ctrl.validations.phonePrefixValid,
                    },
	                {
	                	name: "Ontario CNO Number",
		                description: "Enable CNO field for nurse providers",
		                propertyName: "enable_ontario_cno_field",
		                type: propertyTypes.toggle,
		                value: false,
		                validation: false,
	                },
					{
	                	name: "Additional Demographic Address",
		                description: "Enable an additional demographic address",
		                propertyName: "enable_additional_address",
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
						systemPreferenceApi.getPreferenceValue(property.propertyName)
							.then((response) =>
							{
								if (response.data.body === "true")
								{
									property.value = true;
								}
								else if (response.data.body === "false")
								{
									property.value = false;
								}
								else
								{
									property.value = response.data.body;
								}
							})

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