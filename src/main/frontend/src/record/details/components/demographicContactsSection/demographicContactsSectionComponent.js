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

import {JUNO_STYLE, LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN,} from "../../../../common/components/junoComponentConstants";
import {SystemPreferenceApi} from "../../../../../generated";

angular.module('Record.Details').component('demographicContactsSection', {
    templateUrl: 'src/record/details/components/demographicContactsSection/demographicContactsSection.jsp',
    bindings: {
        ngModel: "=",
        validations: "=",
        componentStyle: "<?"
    },
    controller: ["staticDataService", "$scope", "$uibModal", "$http", "$httpParamSerializer", "$state", "$stateParams", "demographicService", "uxService", function (staticDataService, $scope, $uibModal, $http, $httpParamSerializer, $state, $stateParams, demographicService, uxService)
    {
        let ctrl = this;
        $scope.LABEL_POSITION = LABEL_POSITION;
        $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
        $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
        ctrl.tab = [];
        ctrl.thisDemo = null;

        ctrl.demoContacts = [];
        ctrl.demoContactPros = [];


        ctrl.$onInit = () =>
        {
            ctrl.thisDemo = $stateParams.demographicNo;
            demographicService.getDemographicContacts(ctrl.thisDemo, "personal").then(
                function success(data)
                {
                    ctrl.demoContacts = (data);
                }
            );
            demographicService.getDemographicContacts(ctrl.thisDemo, "professional").then(
                function success(data)
                {
                    ctrl.demoContactPros = (data);
                }
            );
        }

        ctrl.openContacts = function (demoContactId)
        {
            $uibModal.open(
                {
                    component: 'demographicContactsModal',
                    backdrop: 'static',
                    windowClass: "juno-modal",
                    resolve: {
                        demoContact: demoContactId,
                    }
                });
        };

        ctrl.manageContacts = function manageContacts()
        {
                var url = "../demographic/Contact.do?method=manage&demographic_no=" + ctrl.thisDemo;
                window.open(url, "ManageContacts", "width=960, height=700");
        };
    }]
});