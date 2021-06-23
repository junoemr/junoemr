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
import PartialDateModel from "../../models/partialDateModel";

angular.module('Common.Components').component('junoPartialDateSelect', {
    templateUrl: 'src/common/components/junoPartialDateSelect/junoPartialDateSelect.jsp',
    bindings: {
        ngModel: "=",
        label: "@?",
        labelPosition: "<?",
        componentStyle: "<?",
        fieldValid: "<?"
    },
    controller: ["$scope", function ($scope)
    {
        let ctrl = this;

        ctrl.year = "";
        ctrl.month = "";
        ctrl.day = "";

        ctrl.yearValid = true;
        ctrl.monthValid = true;
        ctrl.dayValid = true;

        ctrl.fieldsBlank = false;

        $scope.$watch("$ctrl.ngModel", (newNgModal) =>
        {
            ctrl.updateDateFields();
        })

        ctrl.$onInit = () =>
        {
            ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
            ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
            ctrl.updateDateFields();
        }

        ctrl.$doCheck = () =>
        {
            ctrl.onInvalidStateChange();
        }

        ctrl.updateDateFields = () =>
        {
            if (ctrl.ngModel)
            {
                ctrl.year = ctrl.ngModel._year;
                ctrl.month = ctrl.ngModel._month;
                ctrl.day = ctrl.ngModel._day;
            }
            else
            {
                ctrl.ngModel = new PartialDateModel(null, null, null);
                ctrl.ngModel.setYear("");
                ctrl.ngModel.setMonth("")
                ctrl.ngModel.setDay("");
            }

            ctrl.fieldsBlank = ctrl.allFieldsBlank();
        }

        ctrl.onYearBlur = (field) =>
        {
            ctrl.year = ctrl.onDateBlur(field, true);
        }

        ctrl.onMonthBlur = (field) =>
        {
            ctrl.month = ctrl.onDateBlur(field);
        }

        ctrl.onDayBlur = (field) =>
        {
            ctrl.day = ctrl.onDateBlur(field);
        }

        ctrl.onDateBlur = (field, isYear = false) =>
        {
            if (!isYear && field >= 1 && field <= 9)
            {
                field = Juno.Common.Util.padDateWithZero(field);
            }

            if (field === "0" || field === "00")
            {
                field = "";
            }

            ctrl.fieldsBlank = ctrl.allFieldsBlank();

            if (!ctrl.fieldsBlank)
            {
                if (ctrl.ngModel && ctrl.validatePartialDate())
                {
                    ctrl.ngModel._year = ctrl.year;
                    ctrl.ngModel._month = ctrl.month;
                    ctrl.ngModel._day = ctrl.day;
                }
            }
            return field;
        }

        ctrl.onInvalidStateChange = () =>
        {
        }

        ctrl.allFieldsBlank = () =>
        {
            return (ctrl.day === "" && ctrl.month === "" && ctrl.year === "");
        }

        ctrl.getInvalidClass = (isInvalid) =>
        {
            if (isInvalid)
            {
                return ["field-invalid"];
            }
            return [];
        }

        ctrl.validatePartialDate = () =>
        {
            if (ctrl.year)
            {
                ctrl.yearValid = (ctrl.year < 9999 && ctrl.year > 999);
            }

            if (ctrl.year && ctrl.month)
            {
                ctrl.monthValid = (ctrl.month >= 1 && ctrl.month <= 12);
            }

            if (ctrl.year && ctrl.month && ctrl.day)
            {
                ctrl.dayValid = (ctrl.day >= 1 && ctrl.day <= 31);
            }

            return ctrl.yearValid && ctrl.monthValid && ctrl.dayValid;
        }
    }]
});