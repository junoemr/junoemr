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

import {
    JUNO_BUTTON_COLOR,
    JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE,
    LABEL_POSITION
} from "../../../../common/components/junoComponentConstants";

angular.module('Common.Components').component('junoSelectSave',
    {
        templateUrl: 'src/admin/systemProperties/components/junoSelectSave/junoSelectSave.jsp',
        bindings: {
            ngModel: "=",
            options: "<",
            click: "&?",
            title: "@?",
            buttonColor: "<?",
            componentStyle: "<?",
            buttonColorPattern: "<?",
            icon: "<?",
        },
        controller: [
            '$scope',
            '$uibModal',
            function ($scope) {
                let ctrl = this;

                $scope.LABEL_POSITION = LABEL_POSITION;

                ctrl.$onInit = () =>
                {
                    ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
                    ctrl.buttonColor = ctrl.buttonColor || JUNO_BUTTON_COLOR.PRIMARY;
                    ctrl.buttonColorPattern = ctrl.buttonColorPattern || JUNO_BUTTON_COLOR_PATTERN.DEFAULT
                    ctrl.icon = ctrl.icon || "icon-logout fa-lg";
                }
                ctrl.onClick = () =>
                {
                    if (ctrl.click)
                    {
                        ctrl.click({
                            value: ctrl.ngModel
                        })
                    }
                };
            }]
    });