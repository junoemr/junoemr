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
import {JUNO_BUTTON_COLOR, JUNO_STYLE, JUNO_BUTTON_COLOR_PATTERN} from "../../../common/components/junoComponentConstants";

angular.module('Admin').component('systemPropertiesBilling',
    {
        templateUrl: 'src/admin/systemProperties/billing/propertiesBilling.jsp',
        bindings: {},
        controller: [
            '$scope',
            '$http',
            '$httpParamSerializer',
            '$state',
            '$uibModal',
            'billingService',
            function ($scope, $http, $httpParamSerializer, $state, $uibModal, billingService) {

                const PROPERTY_NAME = 'service_location_code';
                const TITLE = "Save Service Code";
                const DEFAULT_VALUE = "";
                const DEFAULT_LABEL = "None";
                const ICON = "icon-logout fa-lg";

                let ctrl = this;
                let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');

                ctrl.selectedValue = null;
                ctrl.codes = [{label:DEFAULT_LABEL, value:DEFAULT_VALUE,}];
                ctrl.title = TITLE;
                ctrl.buttonColor = JUNO_BUTTON_COLOR.PRIMARY;
                ctrl.componentStyle = JUNO_STYLE.DEFAULT;
                ctrl.buttonColorPattern = JUNO_BUTTON_COLOR_PATTERN.DEFAULT;
                ctrl.icon = ICON;

                ctrl.propertiesList = [{
                        name: "Service Location Code",
                        description: "Change the default service location code",
                        propertyName: "service_location_code",
                        value: ctrl.selectedValue,
                    }];

                ctrl.$onInit = () =>
                {
                    ctrl.getServiceLocationCodes().then( () =>
                    {
                        ctrl.load(PROPERTY_NAME);
                    });
                };

                ctrl.getServiceLocationCodes = async () =>
                {
                    const codes = (await billingService.getBCBillingVisitCodes()).data.body;

                    codes.forEach((code) =>
                    {
                    	ctrl.codes.push(
                    		{
			                    label: code.visitType + " | " + code.visitDescription,
			                    value: code.visitType + "|"+ code.visitDescription,
                            })
				    });
                }

                ctrl.load = (property) =>
                {
                    systemPreferenceApi.getPreferenceValue(property)
                        .then((response) =>
                        {
                            ctrl.selectedValue = response.data.body || DEFAULT_VALUE;
                        })
                };

                /**
                 * Persist new property value
                 * @param property property to update
                 * @param value set the value in the database
                 */
                ctrl.updateProperty = () =>
                {
                    if (systemPreferenceApi.putPreferenceValue(PROPERTY_NAME, ctrl.selectedValue))
                    {
                        Juno.Common.Util.successAlert($uibModal, "Success", "Service Location Code updated");
                    }
                    else
                    {
                        Juno.Common.Util.errorAlert($uibModal, "Error", "Service Location Code was not successfully updated.");
                    }
                };
            }
        ]
    });
