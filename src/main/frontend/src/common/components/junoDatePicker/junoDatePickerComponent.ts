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
import moment from "moment";

angular.module('Common.Components').component('junoDatePicker', {
	templateUrl: 'src/common/components/junoDatePicker/junoDatePicker.jsp',
	bindings: {
		ngModel: "=", // moment
		label: "@?",
		labelPosition: "<?",
		labelClassList: "<?",
		componentStyle: "<?",
		onValidityChange: "&?",
		onChange: "&?",
		disabled: "<?",
		dateFormat: "@?",
	},
	controller: ["$scope", function ($scope)
	{
		const ctrl = this;

		ctrl.internalModel = null;


		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.disabled = ctrl.disabled || false;
			ctrl.dateFormat = ctrl.dateFormat || "yyyy-MM-dd";
			ctrl.placeholderText = ctrl.dateFormat.toUpperCase();

			ctrl.updateDateFields();
		};

		$scope.$watch("$ctrl.ngModel", () =>
		{
		});

		ctrl.updateInternalModel = () =>
		{

		}

		ctrl.getInvalidClass = (isInvalid) =>
		{
			if (isInvalid)
			{
				return ["field-invalid"];
			}
			return [];
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