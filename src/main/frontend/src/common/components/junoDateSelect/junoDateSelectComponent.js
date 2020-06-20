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
		showAge: "<?"
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

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.showAge = ctrl.showAge || false;

			ctrl.updateDateFields();
		};

		ctrl.$doCheck = () =>
		{
			ctrl.updateDateFields();
		}

		ctrl.updateDateFields = () =>
		{
			if (ctrl.ngModel && ctrl.ngModel.isValid())
			{
				ctrl.year = ctrl.ngModel.year();
				ctrl.month = Juno.Common.Util.padDateWithZero(ctrl.ngModel.month() + 1);
				ctrl.day = Juno.Common.Util.padDateWithZero(ctrl.ngModel.date());
			}
		}

		ctrl.onYearChange = (field) =>
		{
			field = ctrl.onDateChange(field, true);
			ctrl.yearValid = ctrl.ngModel.isValid()
			return field;
		}

		ctrl.onMonthChange = (field) =>
		{
			field = ctrl.onDateChange(field);
			ctrl.monthValid = ctrl.ngModel.isValid()
			return field;
		}

		ctrl.onDayChange = (field) =>
		{
			field = ctrl.onDateChange(field);
			ctrl.dayValid = ctrl.ngModel.isValid()
			return field;
		}

		ctrl.onDateChange = (field, isYear) =>
		{
			ctrl.assignDefaults();

			let date = Juno.Common.Util.getDateMomentFromComponents(ctrl.year, ctrl.month, ctrl.day);

			if (date.isValid())
			{
				ctrl.ngModel = date;
				if (!isYear)
				{
					field = Juno.Common.Util.padDateWithZero(field);
				}
				return field;
			}
			else
			{
				field = field.substring(0, field.length - 1);
				if (field === "")
				{
					ctrl.ngModel = date;
					field = "0";
				}
				return field;
			}
		}

		ctrl.assignDefaults = () =>
		{
			ctrl.year = ctrl.year || "0000";
			ctrl.month = ctrl.month || "01";
			ctrl.day = ctrl.day || "01";
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