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

import {JUNO_BUTTON_COLOR, JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoCheckBox', {
	templateUrl: 'src/common/components/junoCheckBox/junoCheckBox.jsp',
	bindings: {
		ngModel: "=?",
		title: "@?",
		label: "@?",
		labelPosition: "<?",
		componentStyle: "<?",
		buttonColor: "<?",
		// alternate value returned when checkbox is true
		trueValue: "<?",
		disabled: "<?",
		// a dummy checkbox will never update ngModel.
		dummy: "<?",
		change: "&?",
	},
	controller: [ function () {
		let ctrl = this;

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.buttonColor = ctrl.buttonColor || JUNO_BUTTON_COLOR.PRIMARY;
			ctrl.trueValue = ctrl.trueValue || true;
			ctrl.dummy = ctrl.dummy || false;
		};

		ctrl.onClick = () =>
		{
			let newValue = null;
			if (ctrl.ngModel === ctrl.trueValue)
			{
				newValue = false;
			}
			else
			{
				newValue = ctrl.trueValue;
			}

			if (!ctrl.dummy)
			{
				ctrl.ngModel = newValue
			}

			if(ctrl.change)
			{
				ctrl.change({value: newValue});
			}
		}

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition];
		};

		ctrl.buttonClasses = () =>
		{
			return [
					ctrl.buttonColor,
					ctrl.ngModel === ctrl.trueValue ? "checked" : "un-checked",
			];
		}

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.hideIcon = () =>
		{
			return {visibility: ctrl.ngModel === ctrl.trueValue ? "visible" : "hidden"};
		}
	}]
});