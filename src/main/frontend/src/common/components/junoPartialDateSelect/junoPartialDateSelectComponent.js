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

import {JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";
import PartialDateModel from "../../../lib/common/partialDate/model/partialDateModel";

angular.module('Common.Components').component('junoPartialDateSelect', {
    templateUrl: 'src/common/components/junoPartialDateSelect/junoPartialDateSelect.jsp',
    bindings: {
        ngModel: "=",
        ngChange: "&?",
        label: "@?",
        labelPosition: "<?",
        componentStyle: "<?",
    },
    controller: ["$scope", function ($scope)
    {
        let ctrl = this;

        ctrl.year = "";
        ctrl.month = "";
        ctrl.day = "";

        $scope.$watch("$ctrl.ngModel", (newNgModal) =>
        {
            ctrl.updateDateFields();
            ctrl.checkUnsetValidationFields();
        })

        ctrl.$onInit = () =>
        {
            ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
            ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
            ctrl.updateDateFields();
            ctrl.setupDateValidations();
            ctrl.checkUnsetValidationFields();
        }

        ctrl.updateDateFields = () =>
        {
            if (ctrl.ngModel)
            {
                if (ctrl.ngModel.year)
                {
                    ctrl.year = ctrl.ngModel.year;
                }
                if (ctrl.ngModel.month)
                {
                    ctrl.month = ctrl.ngModel.month;
                }
                if (ctrl.ngModel.day)
                {
                    ctrl.day = ctrl.ngModel.day;
                }

                if (ctrl.month && ctrl.month >= 1 && ctrl.month <= 9)
                {
                    ctrl.month = Juno.Common.Util.padDateWithZero(ctrl.month);
                    ctrl.ngModel.month = parseInt(ctrl.month);
                }

                if (ctrl.day && ctrl.day >= 1 && ctrl.day <= 9)
                {
                    ctrl.day = Juno.Common.Util.padDateWithZero(ctrl.day);
                    ctrl.ngModel.day = parseInt(ctrl.day);
                }
            }
            else
            {
                ctrl.ngModel = new PartialDateModel(null, null, null);
            }
        }

        ctrl.onYearBlur = (field) =>
        {
            ctrl.year = ctrl.onDateBlur(field, true);
        }

        ctrl.onMonthBlur = (field) =>
        {
            ctrl.month = ctrl.onDateBlur(field, false, true);
        }

        ctrl.onDayBlur = (field) =>
        {
            ctrl.day = ctrl.onDateBlur(field, false, false, true);
        }

        ctrl.onDateBlur = (field, isYear = false, isMonth = false, isDay = false) =>
        {
            if (typeof field === "undefined")
            {
                return;
            }

            if (!isYear && field >= 1 && field <= 9)
            {
                field = Juno.Common.Util.padDateWithZero(field);
            }

            if (ctrl.ngModel)
            {
                if (isYear)
                {
                    ctrl.ngModel.year = parseInt(field) || field;
                }
                else if (isMonth)
                {
                    ctrl.ngModel.month = parseInt(field) || field;
                }
                else if (isDay)
                {
                    ctrl.ngModel.day = parseInt(field) || field;
                }

                ctrl.ngModel.yearValid = ctrl.dateValidations.yearValid();
                ctrl.ngModel.monthValid = ctrl.dateValidations.monthValid();
                ctrl.ngModel.dayValid = ctrl.dateValidations.dayValid();

                return field || null;
            }
        }

        ctrl.getInvalidClass = (isInvalid) =>
        {
            if (isInvalid)
            {
                return ["field-invalid"];
            }
            return [];
        }

        ctrl.setupDateValidations = () =>
        {
            ctrl.dateValidations =
            {
                allFieldsBlank: Juno.Validations.validationFieldBlank(ctrl, "ngModel.year",
                    Juno.Validations.validationFieldBlank(ctrl, "ngModel.month"),
                    Juno.Validations.validationFieldBlank(ctrl, "ngModel.day"))
            };

            ctrl.dateValidations.yearValid = Juno.Validations.validationFieldOr(
                Juno.Validations.PartialDate.validationYear(ctrl, "ngModel.year"),
                ctrl.dateValidations.allFieldsBlank);

            ctrl.dateValidations.monthValid = Juno.Validations.validationFieldOr(
                    Juno.Validations.PartialDate.validationMonth(ctrl, "ngModel.month"),
                    Juno.Validations.validationFieldBlank(ctrl, "ngModel.month",
                        Juno.Validations.validationFieldBlank(ctrl, "ngModel.day"))
                );

            ctrl.dateValidations.dayValid = Juno.Validations.validationFieldOr(
                Juno.Validations.PartialDate.validationDay(ctrl, "ngModel.year", "ngModel.month", "ngModel.day"),
                Juno.Validations.validationFieldBlank(ctrl, "ngModel.day"));
        }

        ctrl.checkUnsetValidationFields = () =>
        {
            if (ctrl.ngModel.yearValid === null || typeof(ctrl.ngModel.yearValid) === "undefined")
            {
                ctrl.ngModel.yearValid = true;
            }
            if (ctrl.ngModel.monthValid === null || typeof(ctrl.ngModel.monthValid) === "undefined")
            {
                ctrl.ngModel.monthValid = true;
            }
            if (ctrl.ngModel.dayValid === null || typeof(ctrl.ngModel.dayValid) === "undefined")
            {
                ctrl.ngModel.dayValid = true;
            }
        }
    }]
});