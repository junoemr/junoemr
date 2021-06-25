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
        ngChange: "&?",
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
        ctrl.allFieldsValid = true;

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

        ctrl.updateDateFields = () =>
        {
            if (ctrl.ngModel)
            {
                if (ctrl.ngModel._year)
                {
                    ctrl.year = ctrl.ngModel.getYear();
                }
                if (ctrl.ngModel._month)
                {
                    ctrl.month = ctrl.ngModel.getMonth();
                }
                if (ctrl.ngModel._day)
                {
                    ctrl.day = ctrl.ngModel.getDay();
                }

                if (ctrl.month && ctrl.month >= 1 && ctrl.month <= 9)
                {
                    Juno.Common.Util.padDateWithZero(ctrl.month);
                }

                if (ctrl.day && ctrl.day >= 1 && ctrl.day <= 9)
                {
                    Juno.Common.Util.padDateWithZero(ctrl.day);
                }
            }
            else
            {
                ctrl.ngModel = new PartialDateModel(null, null, null);
            }

            ctrl.fieldsBlank = ctrl.allFieldsBlank();
        }

        ctrl.onYearBlur = (field) =>
        {
            ctrl.onDateBlur(field, true);
        }

        ctrl.onMonthBlur = (field) =>
        {
            ctrl.onDateBlur(field, false, true);
        }

        ctrl.onDayBlur = (field) =>
        {
            ctrl.onDateBlur(field, false, false, true);
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

            ctrl.fieldsBlank = ctrl.allFieldsBlank();

            if (ctrl.ngModel)
            {
                if (ctrl.fieldsBlank && ctrl.ngModel)
                {
                    ctrl.ngModel.setYear(null);
                    ctrl.ngModel.setMonth(null);
                    ctrl.ngModel.setDay(null);

                    return;
                }

                if (ctrl.checkField(field, isYear, isMonth, isDay))
                {
                    if (ctrl.validatePartialDate())
                    {
                        if (ctrl.year !== null && isYear)
                        {
                            ctrl.ngModel.setYear(ctrl.year);
                        }
                        if (ctrl.month !== null && isMonth)
                        {
                            ctrl.ngModel.setMonth(ctrl.month);
                        }
                        if (ctrl.day !== null && isDay)
                        {
                            ctrl.ngModel.setDay(ctrl.day);
                        }
                    }
                }

                if (ctrl.ngBlur)
                {
                    console.log("ngBlur");
                    ctrl.ngBlur({
                        date: ctrl.ngModel
                    })
                }
            }
        }

        ctrl.allFieldsBlank = () =>
        {
            return ((ctrl.day === null || ctrl.day === "") &&
                (ctrl.month === null || ctrl.month === "") &&
                (ctrl.year === null || ctrl.year === ""));
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
            if (ctrl.allFieldsBlank())
            {
                return true;
            }

            // YYYY-DD
            if (ctrl.year && !ctrl.month && ctrl.day)
            {
                ctrl.monthValid = false;
                return false;
            }
            // MM-DD/MM/DD
            else if (!ctrl.year && (ctrl.month || ctrl.day))
            {
                if (!ctrl.month)
                {
                    ctrl.monthValid = false;
                }
                ctrl.yearValid = false;
            }

            return ctrl.yearValid && ctrl.monthValid && ctrl.dayValid;
        }

        ctrl.checkField = (field, isYear, isMonth, isDay) =>
        {
            let regexp;
            if (isYear)
            {
                regexp = new RegExp('[1-2][0-9][0-9][0-9]');
            }
            else
            {
                regexp = new RegExp('[0-3][0-9]');
            }

            if (field === null || field === "")
            {
                if (isYear)
                {
                    ctrl.year = "";
                    ctrl.yearValid = true;
                }
                else if (isMonth)
                {
                    ctrl.month = "";
                    ctrl.monthValid = true;
                }
                else if (isDay)
                {
                    ctrl.day = "";
                    ctrl.dayValid = true;
                }
                return true;
            }

            if (field && field.toString().match(regexp))
            {
                if (isYear && field >= 999 && ctrl.year <= 9999)
                {
                    ctrl.year = field;
                    ctrl.yearValid = true;
                    return true;
                }
                else if (isMonth && field >= 1 && field <= 12)
                {
                    ctrl.month = field;
                    ctrl.monthValid = true;
                    return true;
                }
                else if (isDay && field >= 1 && field <= 31)
                {
                    ctrl.day = field;
                    ctrl.dayValid = true;
                    return true;
                }
            }
            // Failed validation, set the field as invalid
            if (isYear)
            {
                ctrl.yearValid = false;
            }
            else if (isMonth)
            {
                ctrl.monthValid = false;
            }
            else if (isDay)
            {
                ctrl.dayValid = false;
            }
            return false;
        }
    }]
});