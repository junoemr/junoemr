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

import {JUNO_SIMPLE_MODAL_FILL_COLOR} from "../../../../common/modals/junoSimpleModal/junoSimpleModalConstants";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../../common/components/junoComponentConstants";

angular.module("Messaging.Modals").component('messageCompose', {
	templateUrl: 'src/messaging/inbox/modals/messageCompose/messageCompose.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		"$scope",
		function ($scope)
		{
			const ctrl = this;

			$scope.JUNO_SIMPLE_MODAL_FILL_COLOR = JUNO_SIMPLE_MODAL_FILL_COLOR;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			$scope.LABEL_POSITION = LABEL_POSITION;

			ctrl.recipient = null;
			ctrl.subject = "";

			ctrl.$onInit = () =>
			{
				ctrl.messagingService = ctrl.resolve.messagingService;
				ctrl.sourceId = ctrl.resolve.sourceId;
				ctrl.isReply = ctrl.resolve.isReply || false;
			}

			ctrl.onCancel = () =>
			{
				ctrl.modalInstance.close();
			};
		}],
});