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

import {LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_STYLE} from "../junoComponentConstants";

angular.module('Common.Components').component('junoInputModal',
{
	templateUrl: 'src/common/components/junoInputModal/junoInputModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: ['$scope', function ($scope)
	{
		let ctrl = this;

		ctrl.value = "";
		$scope.LABEL_POSITION = LABEL_POSITION;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		ctrl.$onInit = () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

			if (ctrl.resolve.style === JUNO_STYLE.DRACULA)
			{
				// we are inside an bootstrap transclude component, restyle it.
				angular.element(document.querySelector(".modal-content")).addClass("juno-style-dracula-background");
			}
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
			ctrl.modalInstance.close();
		};

		ctrl.onOk = () =>
		{
			ctrl.modalInstance.close(ctrl.value);
		}
	}]
});