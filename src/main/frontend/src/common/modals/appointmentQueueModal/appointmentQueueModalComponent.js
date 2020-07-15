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

import {LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_STYLE} from "../../components/junoComponentConstants";

angular.module('Common.Components').component('appointmentQueueModal',
	{
		templateUrl: 'src/common/modals/appointmentQueueModal/appointmentQueueModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: ['$scope',
			'$http',
			'$httpParamSerializer',
			'$uibModal',
			function ($scope,
			          $http,
			          $httpParamSerializer,
			          $uibModal)
			{
				let ctrl = this;

				$scope.LABEL_POSITION = LABEL_POSITION;
				$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.editMode = false;
				ctrl.queueModel = {};

				ctrl.$onInit = () =>
				{
					ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
					ctrl.editMode = ctrl.resolve.editMode;

					if(ctrl.editMode)
					{
						ctrl.queueModel = ctrl.resolve.queue;
					}
					else
					{
						ctrl.queueModel = {
							name: "",
							limit: 10,
						}
					}
				}

				ctrl.saveDisabled = () =>
				{
					return ctrl.queueModel.name == null || ctrl.queueModel.name.length < 1;
				}

				ctrl.onSave = () =>
				{
					// TODO hit endpoint
					console.info("save queue", ctrl.queueModel);
					// TODO handle name conflicts
					ctrl.modalInstance.close();
				}

				ctrl.onCancel = () =>
				{
					ctrl.modalInstance.dismiss();
				}
			}]
	});