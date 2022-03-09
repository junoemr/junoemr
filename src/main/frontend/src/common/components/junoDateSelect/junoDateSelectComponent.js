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

angular.module('Common.Components').component('junoDateSelect', {
	templateUrl: 'src/common/components/junoDateSelect/junoDateSelect.jsp',
	bindings: {
		ngModel: "=",
		label: "@?",
		labelPosition: "<?",
		componentStyle: "<?",
		showAge: "<?",
		onValidityChange: "&?",
		change: "&?",
		readonly: "<?",
	},
	controller: ["$scope", function ($scope)
	{
		let ctrl = this;

		ctrl.year = "";
		ctrl.month = "";
		ctrl.day = "";

		ctrl.yearValid = true;
		ctrl.monthValid = false;
		ctrl.dayValid = false;

		ctrl.fieldsBlank = false;

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.showAge = ctrl.showAge || false;
			ctrl.readonly = ctrl.readonly || null;

			ctrl.updateDateFields();
		};

		$scope.$watch("$ctrl.ngModel", () =>
		{
			ctrl.updateDateFields();
			ctrl.onInvalidStateChange();
		});

		ctrl.updateDateFields = () =>
		{
			if (ctrl.ngModel && ctrl.ngModel.isValid() && ctrl.ngModel.year() !== 0)
			{
				ctrl.year = ctrl.ngModel.year();
				ctrl.month = Juno.Common.Util.padDateWithZero(ctrl.ngModel.month() + 1);
				ctrl.day = Juno.Common.Util.padDateWithZero(ctrl.ngModel.date());

				ctrl.monthValid = true;
				ctrl.dayValid = true;
			}
			else if (!ctrl.ngModel) // reset
			{
				ctrl.year = "";
				ctrl.month = "";
				ctrl.day = "";

				ctrl.yearValid = true;
				ctrl.monthValid = false;
				ctrl.dayValid = false;
			}

			ctrl.fieldsBlank = ctrl.allFieldsBlank()
		}

		ctrl.onYearChange = (field) =>
		{
			ctrl.year = ctrl.onDateChange(field, true);
			ctrl.yearValid = ctrl.year < 9999;
		}

		ctrl.onMonthChange = (field) =>
		{
			ctrl.month = ctrl.onDateChange(field);
			ctrl.monthValid = ctrl.ngModel.isValid();
		}

		ctrl.onDayChange = (field) =>
		{
			ctrl.day = ctrl.onDateChange(field);
			ctrl.dayValid = ctrl.ngModel.isValid();
		}

		ctrl.onDateChange = (field, isYear) =>
		{
			ctrl.ngModel = Juno.Common.Util.getDateMomentFromComponents(ctrl.year, ctrl.month, ctrl.day);
			if (!isYear)
			{
				field = Juno.Common.Util.padDateWithZero(field);
			}

			if (field === "0" || field === "00")
			{
				field = "";
			}

			ctrl.fieldsBlank = ctrl.allFieldsBlank();

			if(ctrl.change)
			{
				ctrl.change({value: ctrl.ngModel});
			}

			return field;
		}

		ctrl.onInvalidStateChange = () =>
		{
			if ((!ctrl.monthValid || !ctrl.yearValid || !ctrl.dayValid) && !ctrl.allFieldsBlank() && ctrl.onValidityChange)
			{
				ctrl.onValidityChange({valid: false})
			}
			else if (ctrl.onValidityChange)
			{
				ctrl.onValidityChange({valid: true})
			}
		}

		ctrl.allFieldsBlank = () =>
		{
			return (ctrl.day === "" && ctrl.month === "" && ctrl.year === "")
		}

		ctrl.getInvalidClass = (isInvalid) =>
		{
			if (isInvalid)
			{
				return ["field-invalid"];
			}
			return [];
		}

		ctrl.getAge = () =>
		{
			let now = new moment();
			let age = moment.duration(now.diff(ctrl.ngModel)).years();
			if (isNaN(age))
			{
				return "NA";
			}
			return age;
		}

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition];
		};

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}
	}]
});