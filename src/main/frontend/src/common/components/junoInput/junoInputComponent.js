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

angular.module('Common.Components').component('junoInput', {
	templateUrl: 'src/common/components/junoInput/junoInput.jsp',
	bindings: {
		ngModel: "=",
		ngChange: "&?",
		disabled: "<?",
		type: "@?",
		label: "@?",
		labelPosition: "<?",
		placeholder: "@?",
		uppercase: "<?",
		readonly: "<?",
		characterLimit: "<?",
		hideCharacterLimit: "<?",
		// if try only numbers can be entered in to this input
		onlyNumeric: "<?",
		// block characters from being entered that do not match this regex
		validRegex: "<?",
		invalid: "<?",
		// if true show invalid state even while focused
		showInvalidFocus: "<?",
		// if true do not draw the input "box" just draw its contents
		noBox: "<?",
		componentStyle: "<?",
		icon: "@?",
		allowAutocomplete: "<?",
	},
	controller: [ "$scope", function ($scope) {
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;
		ctrl.oldNgModel = null;
		ctrl.isFocused = false;

		ctrl.$onInit = () =>
		{
			ctrl.type = ctrl.type || 'text';
			ctrl.uppercase = ctrl.uppercase || false;
			ctrl.readonly = ctrl.readonly || false;
			ctrl.invalid = ctrl.invalid || false;
			ctrl.noBox = ctrl.noBox || false;
			ctrl.onlyNumeric = ctrl.onlyNumeric || false;
			ctrl.icon = ctrl.icon || null;
			ctrl.hideCharacterLimit = ctrl.hideCharacterLimit || true;
			ctrl.allowAutocomplete = ctrl.allowAutocomplete || false;

			if (ctrl.showInvalidFocus === undefined)
			{
				ctrl.showInvalidFocus = false;
			}
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.oldNgModel = ctrl.ngModel;

			ctrl.inChrome = (navigator.userAgent.search("Chrome") >= 0);
		};

		$scope.$watch("$ctrl.ngModel", () =>
		{
			ctrl.oldNgModel = ctrl.ngModel;
		});

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.inputClasses = () =>
		{
			return {
				uppercase: ctrl.uppercase,
				"field-invalid": ctrl.invalid && (ctrl.showInvalidFocus || !ctrl.isFocused),
				"field-no-border": ctrl.noBox,
				"field-disabled": ctrl.disabled,
				"shift-right-for-icon": ctrl.icon,
			};
		}

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition, "label-style"];
		};

		ctrl.onChange = () =>
		{
			if (ctrl.ngModel)
			{
				if (!ctrl.isNgModelValid())
				{
					// reset to old value
					ctrl.ngModel = ctrl.oldNgModel;
				}
			}

			// update the old value
			ctrl.oldNgModel = ctrl.ngModel;

			if (ctrl.ngChange)
			{
				ctrl.ngChange({});
			}
		}

		ctrl.isNgModelValid = () =>
		{
			if (ctrl.validRegex && !ctrl.validRegex.test(ctrl.ngModel))
			{
				return false;
			}
			else if (ctrl.onlyNumeric && !((/^\d+$/).test(ctrl.ngModel)))
			{
				return false;
			}

			return true;
		}

		ctrl.onFocus = () =>
		{
			ctrl.isFocused = true;
		}

		ctrl.onBlur = () =>
		{
			ctrl.isFocused = false;
		}

		ctrl.autocompleteValue = () =>
		{
			if(!ctrl.allowAutocomplete)
			{
				return ctrl.inChrome ? "chrome-off" : "off";
			}
			return null;
		}
	}]
});