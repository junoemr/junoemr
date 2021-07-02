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

import {JUNO_STYLE} from "../../../../common/components/junoComponentConstants";
import MessagingServiceFactory from "../../../../lib/messaging/factory/MessagingServiceFactory";
import ActionAlreadyInProgressError from "../../../../lib/error/ActionAlreadyInProgressError";
import {MessageGroup} from "../../../../lib/messaging/model/MessageGroup";

angular.module("Messaging.Components").component('messageList', {
	templateUrl: 'src/messaging/inbox/components/messageList/messageList.jsp',
	bindings: {
		componentStyle: "<?",
		selectedMessageId: "=",
		messagingBackend: "<",
		messageableFilter: "<?",
		sourceId: "<",
		groupId: "<",
		messageStreamChange: "&?", // called when the message stream changes. var "stream" is the new message stream
	},
	controller: [
		"$scope",
		"$state",
		function (
			$scope,
			$state
		)
		{
			const ctrl = this;

			const NEW_MESSAGE_CHECK_INTERVAL_MS = 60000; // 1 minute;

			$scope.MessageGroup = MessageGroup;

			ctrl.messageStream = null;
			ctrl.debounceTimeout = null;
			ctrl.DEBOUNCE_TIME_MS = 500;
			ctrl.MESSAGE_FETCH_COUNT = 10;

			ctrl.newMessagesCheckInterval = null;
			ctrl.currentMessageCount = null;

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
				ctrl.setupNewMessageCheck();
			};

			ctrl.$onDestroy = () =>
			{
				if (ctrl.newMessagesCheckInterval)
				{
					window.clearInterval(ctrl.newMessagesCheckInterval);
				}
			}

			/**
			 * setup periodic new message check
			 */
			ctrl.setupNewMessageCheck = () =>
			{
				ctrl.newMessagesCheckInterval = window.setInterval(async () =>
				{
					if (ctrl.shouldCheckForNewMessages())
					{
						const messagingService = MessagingServiceFactory.build(ctrl.messagingBackend);
						const count = await messagingService.countMessages(await messagingService.getMessageSourceById(ctrl.sourceId), ctrl.groupId);

						if (count > ctrl.currentMessageCount)
						{
							const diff = count - ctrl.currentMessageCount;
							const newMessages = await messagingService.searchMessages(await messagingService.getMessageSourceById(ctrl.sourceId),
								ctrl.getMessageSearchParams(diff, 0));
							ctrl.messageStream.unshift(...newMessages);

							$scope.$apply();
						}
						ctrl.currentMessageCount = count;
					}
				}, NEW_MESSAGE_CHECK_INTERVAL_MS);
			}

			/**
			 * if true the system should check for new messages periodically
			 * @return true / false
			 */
			ctrl.shouldCheckForNewMessages = () =>
			{
				return ctrl.messageStream && ctrl.currentMessageCount && !ctrl.messageableFilter;
			}

			/**
			 * called when user selects a message from the list
			 * @param message - the selected message
			 */
			ctrl.onSelectMessage = (message) =>
			{
				ctrl.selectedMessageId = message.id;
			}

			ctrl.updateMessageView = () =>
			{
				// display the selected message
				$state.go("messaging.view.message",
					{
						backend: ctrl.messagingBackend,
						source: ctrl.sourceId,
						group: ctrl.groupId,
						messageId: ctrl.selectedMessageId,
					}, {location: "replace"});
			}

			ctrl.onMessageSelectionChange = async (messageId) =>
			{
				if (ctrl.messageStream)
				{
					const messagingService = MessagingServiceFactory.build(ctrl.messagingBackend);
					const message = ctrl.messageStream.find((msg) => msg.id === messageId );

					if (message)
					{
						// mark message as read
						if (!message.read)
						{
							message.read = true;
							await messagingService.updateMessage(message);
						}
					}
				}

				ctrl.updateMessageView();
			}

			$scope.$watch("$ctrl.selectedMessageId", ctrl.onMessageSelectionChange);

			/**
			 * fetch more messages for the message list as the user scrolls
			 */
			ctrl.fetchMoreMessages = async () =>
			{
				if (ctrl.messageStream)
				{
					try
					{
						await ctrl.messageStream.load(ctrl.MESSAGE_FETCH_COUNT);
						$scope.$apply();
					}
					catch (error)
					{
						if (error instanceof ActionAlreadyInProgressError)
						{
							// A load is already in progress. Skip load.
						}
						else
						{
							throw error;
						}
					}
				}
			}

			/**
			 * re load messages for the specified source, group & backend.
			 */
			ctrl.reloadMessages = async () =>
			{
				if (ctrl.sourceId && ctrl.groupId && ctrl.messagingBackend)
				{
					const messagingService = MessagingServiceFactory.build(ctrl.messagingBackend);

					ctrl.messageStream = await messagingService.searchMessagesAsStream(
						await messagingService.getMessageSourceById(ctrl.sourceId), ctrl.getMessageSearchParams());
					$scope.$apply();

					if (ctrl.messageStream)
					{
						await ctrl.messageStream.load(ctrl.MESSAGE_FETCH_COUNT);
						$scope.$apply();
					}

					// notify parent of stream change.
					if (ctrl.messageStreamChange)
					{
						ctrl.messageStreamChange({stream: ctrl.messageStream});
					}

					// get initial message count
					ctrl.currentMessageCount = await messagingService.countMessages(
						await messagingService.getMessageSourceById(ctrl.sourceId),
						ctrl.groupId);
				}
			}

			ctrl.getMessageSearchParams = (limit = null, offset = null) =>
			{
				switch (ctrl.groupId)
				{
					case MessageGroup.Sent:
						return {group: ctrl.groupId, recipient: ctrl.messageableFilter, limit, offset}
					default:
						return {group: ctrl.groupId, sender: ctrl.messageableFilter, limit, offset}
				}
			}

			/**
			 * start / reset reload debounce timer.
			 */
			ctrl.startReloadDebounce = () =>
			{
				if (ctrl.debounceTimeout)
				{
					window.clearTimeout(ctrl.debounceTimeout);
				}

				ctrl.messageStream = null;
				ctrl.debounceTimeout = window.setTimeout(ctrl.reloadMessages, ctrl.DEBOUNCE_TIME_MS);
			}

			// if bindings change reload message list.
			$scope.$watch("$ctrl.sourceId", ctrl.startReloadDebounce);
			$scope.$watch("$ctrl.groupId", ctrl.startReloadDebounce);
			$scope.$watch("$ctrl.messagingBackend", ctrl.startReloadDebounce);
			$scope.$watch("$ctrl.messageableFilter", ctrl.startReloadDebounce);
		}],
});