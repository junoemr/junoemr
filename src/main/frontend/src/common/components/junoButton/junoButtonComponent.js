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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoButton', {
	templateUrl: 'src/common/components/junoButton/junoButton.jsp',
	bindings: {
		ngModel: "=",
		label: "@?",
		labelPosition: "<?",
		componentStyle: "<?",
		buttonColor: "<?",
		buttonColorPattern: "<?",
		buttonColorOverride: "<?",
		disabled: "<?",
		click: "&?"
	},
	transclude: true,
	controller: [ function () {
		let ctrl = this;

		ctrl.$onInit = () =>
		{
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.buttonColor = ctrl.buttonColor || JUNO_BUTTON_COLOR.PRIMARY;
			ctrl.buttonColorPattern = ctrl.buttonColorPattern || JUNO_BUTTON_COLOR_PATTERN.DEFAULT;
		};

		ctrl.clickHandler = ($event) =>
		{
			if (!ctrl.disabled && ctrl.click)
			{
				ctrl.click({$event});
			}
		};

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition];
		};

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.buttonClasses = () =>
		{
			return [ctrl.buttonColor, ctrl.buttonColorPattern];
		}

		ctrl.buttonStyle = () =>
		{
			if (ctrl.buttonColorOverride)
			{
				let color = ctrl.buttonColorOverride;
				if (!color.startsWith("#"))
				{
					color = "#" + color;
				}

				return {
					"background-color": color,
					"border-color": color,
				};
			}
			return {};
		}
	}]
});