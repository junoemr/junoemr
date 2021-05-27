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

import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE
} from "../../../../common/components/junoComponentConstants";
import MessagingServiceFactory from "../../../../lib/messaging/factory/MessagingServiceFactory";
import {MessageGroup} from "../../../../lib/messaging/model/MessageGroup";

angular.module("Messaging.Components").component('inboxHeaderBar', {
	templateUrl: 'src/messaging/inbox/components/inboxHeaderBar/inboxHeaderBar.jsp',
	bindings: {
		componentStyle: "<?",
		selectedMessageId: "<",
		messageStream: "<",
		messagingBackendId: "<",
		sourceId: "<",
		groupId: "<",
	},
	controller: [
		"$scope",
		"$uibModal",
		"$state",
		function (
			$scope,
			$uibModal,
			$state
		)
		{
			const ctrl = this;
			ctrl.searchTerm = "";
			$scope.JUNO_STYLE = JUNO_STYLE;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			$scope.MessageGroup = MessageGroup;

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			};

			ctrl.markSelectedMessageAsUnread = async () =>
			{
				const messagingService = MessagingServiceFactory.build(ctrl.messagingBackendId);
				const message = await ctrl.getSelectedMessage();

				message.read = false;
				await messagingService.updateMessage(message);

				$scope.$apply();
			};

			ctrl.archiveSelectedMessage = async () =>
			{
				const messagingService = MessagingServiceFactory.build(ctrl.messagingBackendId);
				const message = await ctrl.getSelectedMessage();

				message.archive();
				await messagingService.updateMessage(message);

				// delete message from message stream
				if (ctrl.messageStream)
				{
					ctrl.messageStream.remove(message);
				}

				$scope.$apply();
			}

			ctrl.unarchiveSelectedMessage = async () =>
			{
				const messagingService = MessagingServiceFactory.build(ctrl.messagingBackendId);
				const message = await ctrl.getSelectedMessage();

				message.unarchive();
				await messagingService.updateMessage(message);

				// delete message from message stream
				if (ctrl.messageStream)
				{
					ctrl.messageStream.remove(message);
				}

				$scope.$apply();
			}

			ctrl.openComposeModal = async (reply = false) =>
			{
				let selectedMessage = null;
				let selectedConversation = null;
				if (reply)
				{
					const messagingService = MessagingServiceFactory.build(ctrl.messagingBackendId);

					selectedMessage = await ctrl.getSelectedMessage();
					selectedConversation = await messagingService.getConversation(selectedMessage.source, selectedMessage.conversationId);
				}

				await $uibModal.open(
					{
						component: 'messageCompose',
						backdrop: 'static',
						windowClass: "juno-simple-modal-window",
						resolve: {
							style: () => JUNO_STYLE.DEFAULT,
							messagingService: () => MessagingServiceFactory.build(ctrl.messagingBackendId),
							sourceId: () => ctrl.sourceId,
							isReply: () => reply,
							subject: () => reply ? selectedMessage.subject : "",
							conversation: () => reply ? selectedConversation : null,
						}
					}
				).result;
			}

			/**
			 * get the currently selected message object
			 * @returns promise that resolves to the selected message
			 */
			ctrl.getSelectedMessage = async () =>
			{
				let message = null;
				// our version of webpack can't parse js files with optional chaining for some reason. Can be one liner with '?'.
				if (ctrl.messageStream)
				{
					message = ctrl.messageStream.find((msg) => msg.id === ctrl.selectedMessageId);
				}

				if (message)
				{
					return message;
				}
				else
				{
					// selected message may not be in the message stream. This can happen if the user reloads the page
					const messagingService = MessagingServiceFactory.build(ctrl.messagingBackendId);
					return message = await messagingService.getMessage(await messagingService.getMessageSourceById(ctrl.sourceId), ctrl.selectedMessageId);
				}
			}

		}],
});