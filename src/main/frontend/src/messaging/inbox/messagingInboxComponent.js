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
import StreamingList from "../../lib/util/StreamingList";
import {MessageGroup} from "../../lib/messaging/model/MessageGroup";
import MessagingStringResourceSetFactory from "../../lib/messaging/factory/MessagingStringResourceSetFactory";
import {BroadcastEvent} from "./components/messageList/messageListComponentConstants";
import ToastService from "../../lib/alerts/service/ToastService";
import {toastStore} from "../../lib/alerts/store/ToastStore";

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

		$scope.stringResources = MessagingStringResourceSetFactory.build($stateParams.backend);

		ctrl.backend = $stateParams.backend;
		ctrl.selectedSourceId = $stateParams.source;
		ctrl.selectedGroupId = $stateParams.group;
		ctrl.recordPageEmbedded = $stateParams.recordPageEmbedded ? Juno.Common.Util.parseBoolean($stateParams.recordPageEmbedded) : false;
		ctrl.componentStyle = JUNO_STYLE.GREY;
		ctrl.messageSources = [];
		ctrl.groups = [];
		ctrl.messageStream = null;
		ctrl.selectedMessageId = $stateParams.messageId;
		ctrl.onlyUnread = $stateParams.onlyUnread ? Juno.Common.Util.parseBoolean($stateParams.onlyUnread) : false; // only show unread messages
		ctrl.searchKeyword = $stateParams.keyword || null;

		// a list of all messages currently selected for "group editing"
		ctrl.massEditList = []; // Type Message[]

		ctrl.$onInit = async () =>
		{
			const messagingService = MessagingServiceFactory.build($stateParams.backend);

			ctrl.messageSources = await messagingService.getMessageSources();
			ctrl.groups = await messagingService.getMessageGroups();

			if ($stateParams.messageableId)
			{
				// load messageable filter based on query param
				ctrl.messageableFilter = await messagingService.getMessageable(
					await messagingService.getMessageSourceById(ctrl.selectedSourceId),
					$stateParams.messageableId);
			}
		}

		/**
		 * called when the message list is to be manually reloaded.
		 */
		ctrl.onReloadMessages = () =>
		{
			// tell message list to reload.
			$scope.$broadcast(BroadcastEvent.RefreshMessageList);
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
			$state.go(".", {backend: ctrl.backend, source: sourceId, group: groupId}, {location: "replace"});
		};

		ctrl.onMessageableFilterChange = (messageable, oldMessageable) =>
		{
			if (messageable !== oldMessageable)
			{
				$state.go(".",
					{
						backend: ctrl.backend,
						source: ctrl.selectedSourceId,
						group: ctrl.selectedGroupId,
						messageableId: messageable ? messageable.id : null,
					});
			}
		}

		ctrl.onMessageStreamChange = (stream) =>
		{
			ctrl.messageStream = stream;
		};

		ctrl.onMessageSelected = (id) =>
		{
			ctrl.selectedMessageId = id;
		}

		$scope.$watch("$ctrl.messageableFilter", ctrl.onMessageableFilterChange);
	}],
});