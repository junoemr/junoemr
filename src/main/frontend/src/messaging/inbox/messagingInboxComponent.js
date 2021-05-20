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

import MessagingServiceFactory from "../../lib/messaging/factory/MessagingServiceFactory";
import {JUNO_STYLE} from "../../common/components/junoComponentConstants";

angular.module("Messaging").component('messagingInbox', {
	templateUrl: 'src/messaging/inbox/messagingInbox.jsp',
	bindings: {
	},
	controller: [
		"$scope",
		"$stateParams",
		"$state",
		function (
			$scope,
			$stateParams,
			$state
		)
	{
		const ctrl = this;
		ctrl.backend = $stateParams.backend;
		ctrl.selectedSourceId = $stateParams.source;
		ctrl.selectedGroupId = $stateParams.group;
		ctrl.messagingService = MessagingServiceFactory.build($stateParams.backend);
		ctrl.componentStyle = JUNO_STYLE.GREY;
		ctrl.messageSources = [];
		ctrl.groups = [];

		ctrl.$onInit = async () =>
		{
			ctrl.messageSources = await ctrl.messagingService.getMessageSources();
			ctrl.groups = await ctrl.messagingService.getMessageGroups();
		}

		/**
		 * called when the user's selection of source / message group changes
		 * @param sourceId - the new source id
		 * @param groupId - the new message group id
		 */
		ctrl.onSourceGroupChange = (sourceId, groupId) =>
		{
			ctrl.selectedSourceId = sourceId;
			ctrl.selectedGroupId = groupId;
			$state.go(".", {backend: ctrl.backend, source: sourceId, group: groupId});
		}
	}],
});