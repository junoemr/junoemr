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
import DeviceInfo from "../../../lib/util/DeviceInfo";

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
		// if true show invalid on pristine state
		showInvalidPristine: "<?",
		// if true do not draw the input "box" just draw its contents
		noBox: "<?",
		componentStyle: "<?",
		// Display an icon inside the text input
		icon: "@?",
		allowAutocomplete: "<?",
		// This component should have focus set on it when rendered
		autoFocus: "<",
		// Display a required icon to the right of this component's label
		requiredIndicator: "<?"
	},
	controller: [ "$scope", "$timeout", function ($scope, $timeout) {
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;
		ctrl.oldNgModel = null;
		ctrl.isFocused = false;
		ctrl.pristine = true;

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

			ctrl.showInvalidFocus = ctrl.showInvalidFocus  || false;

			if (ctrl.showInvalidPristine === undefined)
			{
				ctrl.showInvalidPristine = true;
			}

			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.oldNgModel = ctrl.ngModel;
			ctrl.deviceInfo = new DeviceInfo();
			ctrl.autoFocus = ctrl.autoFocus || false;
			ctrl.requiredIndicator = ctrl.requiredIndicator || false;
		};

		ctrl.$postLink = () =>
		{
			if (ctrl.autoFocus && !ctrl.disabled)
			{
				$timeout(() =>
				{
					ctrl.inputRef.focus();
				});
			}
		}

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
				"field-invalid": ctrl.invalid && ctrl.calcShowInvalid(),
				"field-no-border": ctrl.noBox,
				"field-disabled": ctrl.disabled,
				"shift-right-for-icon": ctrl.icon,
			};
		}

		// Function assumes that ctrl.invalid is true, is determining edge cases
		ctrl.calcShowInvalid = () =>
		{
			if (!ctrl.showInvalidFocus && ctrl.isFocused)
			{
				return false;
			}

			if (!ctrl.showInvalidPristine && ctrl.pristine)
			{
				return false;
			}

			return true;
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

			ctrl.pristine = false;
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
				return ctrl.deviceInfo.autocompleteOffValue;
			}
			return null;
		}
	}]
});