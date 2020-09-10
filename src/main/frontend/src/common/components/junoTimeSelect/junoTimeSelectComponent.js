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

angular.module('Common.Components').component('junoTimeSelect', {
	templateUrl: 'src/common/components/junoTimeSelect/junoTimeSelect.jsp',
	bindings: {
		ngModel: "=",
		label: "@?",
		labelPosition: "<?",
		componentStyle: "<?",
		disabled: "<?",
		change: "&?",
	},
	controller: ["$scope", function ($scope)
		{
		let ctrl = this;

		ctrl.hour = null;
		ctrl.minute = null;
		ctrl.amPm = "AM";

		ctrl.hourOptions = [];
		ctrl.minuteOptions = [];
		ctrl.amPmOptions = [];

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			for(let i=0; i<12; i++)
			{
				ctrl.hourOptions.push({value: (i + 1) % 12, label: i+1});
			}
			for(let i=0; i<60; i++)
			{
				ctrl.minuteOptions.push({value: i, label: Juno.Common.Util.pad0(i)});
			}
			ctrl.amPmOptions = [
				{
					value: "AM",
					label: "AM",
				},
				{
					value: "PM",
					label: "PM",
				},
			];

			ctrl.updateDateFields();

			$scope.$watchGroup( ["$ctrl.hour", "$ctrl.minute", "$ctrl.amPm"], (newValue, oldValue) =>
				{
					if (newValue !== oldValue)
					{
						ctrl.onTimeChange();
					}
				}
			);
		};

		ctrl.updateDateFields = () =>
		{
			if (ctrl.ngModel && ctrl.ngModel.isValid())
			{
				const hour24 = ctrl.ngModel.hour();
				ctrl.hour = ((hour24) % 12);
				ctrl.minute = ctrl.ngModel.minute();
				ctrl.amPm = (hour24 >= 12) ? "PM" : "AM";
			}
		}

		ctrl.onTimeChange = () =>
		{
			// clone current moment model
			let date = moment(ctrl.ngModel);
			date.hour((ctrl.amPm === "AM") ? ctrl.hour : ctrl.hour + 12);
			date.minute(ctrl.minute);
			date.second(0);

			if (date.isValid())
			{
				ctrl.ngModel = date;

				console.info("updated date", Juno.Common.Util.formatMomentDateTimeNoTimezone(date));

				if(ctrl.change)
				{
					ctrl.change({moment: date});
				}
			}
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