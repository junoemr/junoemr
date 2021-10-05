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
import {JunoSelectOption} from "../../../lib/common/junoSelectOption";
import {JUNO_RADIO_STYLE} from "./junoRadioSelectConstants";

angular.module('Common.Components').component('junoRadioSelect', {
	templateUrl: 'src/common/components/junoRadioSelect/junoRadioSelect.jsp',
	bindings: {
		ngModel: "=",
		options: "<",
		name: "@",
		label: "@?",
		labelPosition: "<?",
		componentStyle: "<?",
		radioStyle: "<?",
		invalid: "<?",
		disabled: "<?",
		change: "&?"
	},
	controller: [function ()
	{
		let ctrl = this;

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.radioStyle = ctrl.radioStyle || JUNO_RADIO_STYLE.CIRCLE;
			ctrl.invalid = ctrl.invalid || false;
		};

		ctrl.labelClasses = (): string[] =>
		{
			return [ctrl.labelPosition, "label-style"];
		};

		ctrl.componentClasses = (): string[] =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.radioClasses = (): string[] =>
		{
			return [ctrl.radioStyle];
		};

		ctrl.iconClass = (): string =>
		{
			switch (ctrl.radioStyle)
			{
				case JUNO_RADIO_STYLE.CHECK: return "icon-check";
				default: return "icon-dot";
			}
		};

		ctrl.inputClasses = () =>
		{
			return {
				"field-invalid": ctrl.invalid,
			};
		}

		ctrl.onChange = (option: JunoSelectOption) =>
		{
			if (ctrl.change)
			{
				ctrl.change({value: option.value, option: option});
			}
		}
	}]
});