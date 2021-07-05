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

        ctrl.year = null;
        ctrl.month = null;
        ctrl.day = null;

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
            ctrl.year = field;
            ctrl.onDateBlur(field, true);
        }

        ctrl.onMonthBlur = (field) =>
        {
            ctrl.month = field;
            ctrl.onDateBlur(field, false, true);
        }

        ctrl.onDayBlur = (field) =>
        {
            ctrl.day = field;
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

            if (ctrl.ngModel)
            {
           /*     let validDates = ctrl.checkField(field, isYear, isMonth, isDay)
                ctrl.validYear = validDates[0];
                ctrl.validMonth = validDates[1];
                ctrl.validDay = validDates[2];*/

                ctrl.allFieldsValid = ctrl.checkAllFields();

                if (isYear)
                {
                    ctrl.ngModel.setYear(field);
                    ctrl.yearValid = Juno.Validations.validationYear(ctrl.ngModel, field);
                    console.log(ctrl.yearValid);
                }
                else if (isMonth)
                {
                    ctrl.ngModel.setMonth(field)
                    ctrl.monthValid = Juno.Validations.validationMonth(ctrl.ngModel, field);
                }
                else if (isDay)
                {
                    ctrl.ngModel.setDay(field);
                    ctrl.dayValid = Juno.Validations.validationDay(ctrl.ngModel, field);
                }
                ctrl.checkAllFields();
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

/*        ctrl.checkField = (field, isYear, isMonth, isDay) =>
        {
            let partialDate = new PartialDateModel(null, null, null);
            partialDate.setYear(ctrl.year);
            partialDate.setMonth(ctrl.month);
            partialDate.setDay(ctrl.day);

            if (isYear)
            {
                ctrl.year = field;
                partialDate.setYear(field);
            }
            else if (isMonth)
            {
                ctrl.month = field;
                partialDate.setMonth(field);
            }
            else if (isDay)
            {
                ctrl.day = field;
                partialDate.setDay(field);
            }


            ctrl.yearValid = partialDate.isValidYear();
            ctrl.monthValid = partialDate.isValidMonth();
            ctrl.dayValid = partialDate.isValidDay();

            return ctrl.yearValid, ctrl.monthValid, ctrl.dayValid;
        }*/

        ctrl.checkAllFields = () =>
        {
            if (ctrl.allFieldsBlank())
            {
                ctrl.yearValid = true;
                ctrl.monthValid = true;
                ctrl.dayValid = true;

                return true;
            }
            return ctrl.yearValid && ctrl.monthValid && ctrl.dayValid;
        }

        ctrl.allFieldsBlank = () =>
        {
            return (!ctrl.year && !ctrl.month && !ctrl.day);
        }

        ctrl.setupDateValidations = () =>
        {
            ctrl.dateValidations =
            {
                yearValid: Juno.Validations.validationYear(ctrl.ngModel, "_year"),
                monthValid: Juno.Validations.validationMonth(ctrl.ngModel, ctrl.month),
                dayValid: Juno.Validations.validationDay(ctrl.ngModel, ctrl.day),
            };
        }
    }]
});