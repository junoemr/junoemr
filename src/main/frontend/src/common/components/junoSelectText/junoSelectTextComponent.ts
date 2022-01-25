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

angular.module('Common.Components').component('junoSelectText', {
	templateUrl: 'src/common/components/junoSelectText/junoSelectText.jsp',
	bindings: {
		selectModel: "=",
		selectOptions: "<",
		selectPlaceholder: "@?",

		textModel: "=",
		textPlaceholder: "@?",

		label: "@?",
		labelPosition: "<?",
		change: "&?",
		disabled: "<?",

		componentStyle: "<?",
		allowAutocomplete: "<?",
	},
	controller: [ "$scope", function ($scope) {
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;
		ctrl.oldNgModel = null;
		ctrl.isFocused = false;

		ctrl.fullOptionsList = [];

		ctrl.$onInit = () =>
		{
			ctrl.selectPlaceholder = ctrl.selectPlaceholder || { label: "Select", shortLabel: "Select" }

			ctrl.fullOptionsList = [{ label: ctrl.selectPlaceholder.label,
				disabled: true,
				value:"",
				shortLabel: ctrl.selectPlaceholder.shortLabel
			}, ...ctrl.selectOptions]

			ctrl.allowAutocomplete = ctrl.allowAutocomplete || false;
			ctrl.textPlaceholder = ctrl.textPlaceholder || null;

			if (ctrl.showInvalidFocus === undefined)
			{
				ctrl.showInvalidFocus = false;
			}

			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			ctrl.deviceInfo = new DeviceInfo();
		};

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

		ctrl.resolveSelectLabel = (option) =>
		{
			if (ctrl.selectModel === option.value && option.shortLabel)
			{
					return option.shortLabel;
			}

			return option.label
		}

		// Reset the select model on click, otherwise the dropdown menu will show the abbreviated label
		ctrl.onSelectClick = () =>
		{
			ctrl.selectModel = "";
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