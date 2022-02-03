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
		selectChange: "&?",

		textModel: "=",
		textChange: "&?",
		textPlaceholder: "@?",

		label: "@?",
		labelPosition: "<?",

		componentStyle: "<?",
		allowAutocomplete: "<?",
	},

	controller: [ "$scope", function ($scope) {
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;

		ctrl.$onInit = () =>
		{

			ctrl.allowAutocomplete = ctrl.allowAutocomplete || false;
			ctrl.textPlaceholder = ctrl.textPlaceholder || null;

			if (ctrl.showInvalidFocus === undefined)
			{
				ctrl.showInvalidFocus = false;
			}

			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			ctrl.deviceInfo = new DeviceInfo();

			ctrl.hasSelectFocus = false;
			ctrl.hasInputFocus = false;
		};

		ctrl.onSelectFocus = (focused: boolean) :void =>
		{
			ctrl.hasSelectFocus = focused;
		}

		ctrl.onInputFocus = (focused: boolean) :void =>
		{
			ctrl.hasInputFocus = focused;
		}

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.fieldClasses = () =>
		{
			return {
				"field-focused": ctrl.hasSelectFocus || ctrl.hasInputFocus
			};
		}

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition, "label-style"];
		};


		ctrl.onSelectChange = (value) =>
		{
			if (ctrl.selectChange)
			{
				const option = (ctrl.selectOptions) ? ctrl.selectOptions.find((option) => option.value === value) : null;
				ctrl.selectChange({value: value, option: option});
			}
		}

		ctrl.onTextChange = () =>
		{
			if (ctrl.textChange)
			{
				ctrl.textChange({});
			}
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