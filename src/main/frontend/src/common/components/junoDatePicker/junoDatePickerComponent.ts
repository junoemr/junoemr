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
import moment, {Moment} from "moment";
declare const $:JQueryStatic;

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
	controller: [
		"$scope",
		function ($scope)
	{
		const ctrl = this;

		ctrl.internalModel = null;

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.disabled = ctrl.disabled || false;
			ctrl.dateFormat = ctrl.dateFormat || "YYYY-MM-DD";
			ctrl.placeholderText = ctrl.dateFormat.toUpperCase();

			ctrl.updateInternalModel(ctrl.ngModel);
		};

		ctrl.createDatepicker = (): void =>
		{
			ctrl.datepickerInputRef.datepicker({
				autoclose: true,
				todayHighlight: true,
				todayBtn: 'linked',
				clearBtn: true,
				orientation: $scope.orientation,
				format: 'yyyy-mm-dd',
				showOnFocus: true,
				keyboardNavigation: true,

			}).on("show", (e: Event) =>
			{
				// custom button layout
				const buttonsHtml = "<tfoot><tr>" +
					"<th style='display: table-cell;' colspan='4' class='today'>Today</th>" +
					"<th style='display: table-cell;' colspan='3' class='clear'>Clear</th></tr>";

				const dropdown = $(".datepicker.datepicker-dropdown");
				const footer = dropdown.find('tfoot');
				footer.replaceWith(buttonsHtml);

				// replace next/prev buttons with custom font icons
				dropdown.find(".next").html("").addClass("icon-arrow-right");
				dropdown.find(".prev").html("").addClass("icon-arrow-left");
			});
		}

		$scope.$watch("$ctrl.ngModel", (newVal, oldVal) =>
		{
			if(newVal !== oldVal)
			{
				ctrl.updateInternalModel(ctrl.ngModel);
			}
		});

		ctrl.hasDatePicker = (): boolean =>
		{
			return ctrl.datepickerInputRef.data('datepicker');
		}
		ctrl.datePickerVisible = (): boolean =>
		{
			return $(".datepicker.datepicker-dropdown").is(":visible");
		}
		ctrl.showDatePicker = (): void =>
		{
			if(!ctrl.hasDatePicker())
			{
				ctrl.createDatepicker();
			}
			ctrl.datepickerInputRef.datepicker("show");
		}
		ctrl.hideDatePicker = (): void =>
		{
			if(!ctrl.hasDatePicker())
			{
				ctrl.datepickerInputRef.datepicker("hide");
			}
		}

		ctrl.toggleDatepickerState = (): void =>
		{
			if(!ctrl.disabled)
			{
				if(ctrl.datePickerVisible())
				{
					ctrl.hideDatePicker();
				}
				else
				{
					ctrl.showDatePicker();
				}
			}
		}

		ctrl.updateInternalModel = (date: Moment): void =>
		{
			if(date && date.isValid())
			{
				ctrl.internalModel = Juno.Common.Util.formatMomentDate(date, ctrl.dateFormat);
			}
			else
			{
				ctrl.internalModel = null;
			}
		}

		ctrl.updateExternalModel = (): void =>
		{
			let asMoment = moment.utc(ctrl.internalModel, ctrl.dateFormat, true);
			if(asMoment && asMoment.isValid())
			{
				ctrl.ngModel = asMoment;
			}
			else
			{
				ctrl.ngModel = null;
			}

			if(ctrl.onChange)
			{
				ctrl.onChange({value: ctrl.ngModel});
			}
		}

		ctrl.getInvalidClass = (isInvalid: boolean): string[] =>
		{
			if (isInvalid)
			{
				return ["field-invalid"];
			}
			return [];
		}

		ctrl.labelClasses = (): string[] =>
		{
			return [ctrl.labelPosition];
		};

		ctrl.componentClasses = (): string[] =>
		{
			return [ctrl.componentStyle];
		}
	}]
});