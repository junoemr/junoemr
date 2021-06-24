/**
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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_INPUT_MODAL_TYPE, JUNO_STYLE} from "../../components/junoComponentConstants";

angular.module('Common.Components').component('junoInputModal',
{
	templateUrl: 'src/common/modals/junoInputModal/junoInputModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: ['$scope', function ($scope)
	{
		let ctrl = this;

		ctrl.JUNO_INPUT_MODAL_TYPE = JUNO_INPUT_MODAL_TYPE;
		ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		$scope.allValidationsValid = Juno.Validations.allValidationsValid;

		ctrl.inputModalType = null;
		ctrl.value = "";
		ctrl.hasSubmitted = false;
		ctrl.validations = {
			value: Juno.Validations.validationFieldRequired(ctrl, "value"),
		};

		ctrl.typeaheadOptions = [];

		ctrl.$onInit = () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
			ctrl.inputModalType = ctrl.resolve.type || JUNO_INPUT_MODAL_TYPE.TEXT;

			if (ctrl.inputModalType === JUNO_INPUT_MODAL_TYPE.SELECT)
			{
				ctrl.value = null;
				ctrl.placeholder = ctrl.resolve.placeholder || "Please Select an Option";
			}
			if (ctrl.inputModalType === JUNO_INPUT_MODAL_TYPE.TYPEAHEAD)
			{
				ctrl.value = null;
				ctrl.placeholder = ctrl.resolve.placeholder || "Search";
			}
			ctrl.placeholder = ctrl.resolve.placeholder || "Enter text here";
		}

		ctrl.typeaheadSearch = async (value) =>
		{
			ctrl.typeaheadOptions = [];
			if(ctrl.resolve.options && ctrl.resolve.options instanceof Function)
			{
				ctrl.typeaheadOptions = await ctrl.resolve.options(value);
			}
			else
			{
				ctrl.typeaheadOptions = ctrl.resolve.options;
			}
		}

		ctrl.setTypeahedValue = (value) =>
		{
			this.typeaheadValue = value;
		}

		ctrl.onKeyDown = (event) =>
		{
			if (event.key === "Enter")
			{
				ctrl.onOk();
				event.preventDefault();
			}
		}

		ctrl.onCancel = () =>
		{
			// TODO should be a dismiss, but all uses of it need to change to handle promise rejection
			ctrl.modalInstance.close();
		};

		ctrl.onOk = () =>
		{
			if (Juno.Validations.allValidationsValid(ctrl.validations))
			{
				if (ctrl.inputModalType === JUNO_INPUT_MODAL_TYPE.TYPEAHEAD)
				{
					ctrl.modalInstance.close(ctrl.typeaheadValue);
				}
				else
				{
					ctrl.modalInstance.close(ctrl.value);
				}
			}
			ctrl.hasSubmitted = true;
		}

		ctrl.getComponentClasses = () =>
		{
			return [ctrl.resolve.style + "-background"]
		}
	}]
});