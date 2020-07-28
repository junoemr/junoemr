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

import {LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_STYLE, JUNO_BUTTON_COLOR_PATTERN} from "../../components/junoComponentConstants";
import {AqsQueuesApi} from "../../../../generated";

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

				// load appointment queue api
				let aqsQueuesApi = new AqsQueuesApi($http, $httpParamSerializer, '../ws/rs');

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				ctrl.editMode = false;
				ctrl.queueModel = {};
				ctrl.isoading = true;

				ctrl.$onInit = () =>
				{
					ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
					ctrl.editMode = ctrl.resolve.editMode;

					if(ctrl.editMode)
					{
						ctrl.queueModel = angular.copy(ctrl.resolve.queue);
					}
					else
					{
						ctrl.queueModel = {
							id: null,
							queueName: "",
							queueLimit: 10,
							queueColor: "#ffffff",
							organizationId: null,
							createdAt: null,
							updatedAt: null,
							createdBy: null,
							createdByType: null,
							updatedBy: null,
							updatedByType: null,
						}
					}
					ctrl.isoading = false;
				}

				ctrl.saveDisabled = () =>
				{
					return ctrl.isoading || ctrl.queueModel.queueName == null || ctrl.queueModel.queueName.length < 1;
				}

				ctrl.onSave = () =>
				{
					ctrl.isoading = true;
					const onSaveSuccess = (response) =>
					{
						ctrl.modalInstance.close(response.data.body);
						ctrl.isoading = false;
					}
					const onSaveError = (error) =>
					{
						// TODO handle name conflicts/errors
						console.error(error);
						alert("Failed to save appointment queue");
						ctrl.isoading = false;
					}

					if (ctrl.editMode)
					{
						aqsQueuesApi.updateAppointmentQueue(ctrl.queueModel.id, ctrl.queueModel).then(onSaveSuccess).catch(onSaveError);
					}
					else
					{
						aqsQueuesApi.createAppointmentQueue(ctrl.queueModel).then(onSaveSuccess).catch(onSaveError);
					}
				}

				ctrl.onCancel = () =>
				{
					ctrl.modalInstance.dismiss("modal cancelled");
				}
			}]
	});