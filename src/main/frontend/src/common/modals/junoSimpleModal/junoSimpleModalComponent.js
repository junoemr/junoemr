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

import {
	JUNO_STYLE,
	JUNO_BACKGROUND_STYLE, JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN
} from "../../components/junoComponentConstants";
import {JUNO_SIMPLE_MODAL_FILL_COLOR} from "./junoSimpleModalConstants";

angular.module('Common.Components').component('junoSimpleModal',
		{
			templateUrl: 'src/common/modals/junoSimpleModal/junoSimpleModal.jsp',
			bindings: {
				modalInstance: "<",
				componentStyle: "<?",
				modalWidth: "<?",
				modalHeight: "<?",
				fillColor: "<?"
			},
			transclude: true,
			controller: ['$scope', function ($scope)
			{
				let ctrl = this;

				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.$onInit = () =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
					ctrl.fillColor = ctrl.fillColor || JUNO_SIMPLE_MODAL_FILL_COLOR.TRANSPARENT;
				}

				ctrl.$doCheck = () =>
				{
					let window = document.querySelector(".juno-simple-modal-window");
					if (ctrl.modalWidth)
					{
						window.style.setProperty("--juno-simple-modal-width", ctrl.modalWidth + "px");
					}
					if (ctrl.modalHeight)
					{
						window.style.setProperty("--juno-simple-modal-height", ctrl.modalHeight + "px");
					}
				}

				ctrl.onCancel = () =>
				{
					ctrl.modalInstance.close();
				};

				ctrl.getComponentClasses = () =>
				{
					return [ctrl.componentStyle, ctrl.componentStyle + JUNO_BACKGROUND_STYLE.PRIMARY]
				}

				ctrl.getInnerContainerClasses = () =>
				{
					return [ctrl.fillColor];
				}
			}]
		});