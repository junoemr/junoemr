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

import {LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoInputSave',
    {
    templateUrl: 'src/common/components/junoInputSave/junoInputSave.jsp',
    bindings: {
        ngModel: "=",
        click: "&?",
        invalid: "<?"
    },

    controller: [ '$scope', function ($scope)
    {
        let ctrl = this;

        $scope.LABEL_POSITION = LABEL_POSITION;
        ctrl.oldNgModel = null;
        ctrl.isFocused = false;
        ctrl.value = "";

        ctrl.$onInit = () =>
        {
            ctrl.invalid = ctrl.invalid || false;
        }

        $scope.$watch("$ctrl.ngModel", () =>
        {
            ctrl.oldNgModel = ctrl.ngModel;
        });

        ctrl.onClick = () =>
        {
            console.log("component handler");
            if (ctrl.click)
            {

                console.log("invoking callback");
                console.log(ctrl.click.toString());

                if (!ctrl.invalid)
                {
                    ctrl.click({
                        value: ctrl.ngModel
                    })
                }
            }
        };
    }]
});