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
        invalid: "<?",
        characterLimit: "<",
        hideCharacterLimit: "<"
    },
    controller: [
        '$scope',
        '$uibModal',
        function ($scope,$uibModal) {
            let ctrl = this;

            $scope.LABEL_POSITION = LABEL_POSITION;
            ctrl.oldNgModel = null;
            ctrl.value = "";

            ctrl.$onInit = () =>
            {
                ctrl.invalid = ctrl.invalid || false;
            }

            ctrl.onClick = () =>
            {
                if (ctrl.click)
                {
                    if (!ctrl.invalid)
                    {
                        ctrl.click({
                            value: ctrl.ngModel
                        })

                        Juno.Common.Util.successAlert($uibModal, "Success", "Phone Prefix saved");
                    }
                    else
                    {
                        Juno.Common.Util.errorAlert($uibModal, "Error", "Phone Prefix must be exactly three digits");
                    }
                }
            };
        }]
});