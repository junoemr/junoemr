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
		messageableFilter: "=?",
		selectedMessageId: "=",
		onlyUnread: "=?",
		searchKeyword: "=?",
		massEditList: "=",
		messageStream: "<",
		messagingBackendId: "<",
		sourceId: "<",
		groupId: "<",
		disableSearch: "<?",
		onReloadMessages: "&?"
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

			$scope.JUNO_STYLE = JUNO_STYLE;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			$scope.MessageGroup = MessageGroup;

			ctrl.massSelectActive = false;
			ctrl.isLoading = false;

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
				ctrl.messagingService = MessagingServiceFactory.build(ctrl.messagingBackendId);
				ctrl.onlyUnread = ctrl.onlyUnread || false;
				ctrl.searchKeyword = ctrl.searchKeyword || "";
				ctrl.disableSearch = ctrl.disableSearch || false;
			};

			/**
			 * mark all selected messages as read / un-read
			 * @param read - true / false. read / unread
			 */
			ctrl.updateSelectedMessageReadFlag = async (read) =>
			{
				const messages = await ctrl.getSelectedMessages();

				// mark messages as un read.
				const promiseList = [];
				for (let message of messages)
				{
					message.read = read;
					promiseList.push(ctrl.messagingService.updateMessage(message));
				}

				// wait for completion
				try
				{
					ctrl.isLoading = true;
					$scope.$apply();

					await Promise.all(promiseList);
				}
				finally
				{
					ctrl.isLoading = false;
				}

				$scope.$apply();
			};

			/**
			 * archive or unarchive message
			 * @param archive true / false. archive / unarchive
			 * @returns {Promise<void>}
			 */
			ctrl.archiveSelectedMessages = async (archive) =>
			{
				const messages = await ctrl.getSelectedMessages();

				const promiseList = [];
				for (let message of messages)
				{
					promiseList.push(new Promise(async (resolve) => {
						if (archive)
						{
							message.archive();
						}
						else
						{
							message.unarchive();
						}
						await ctrl.messagingService.updateMessage(message);

						if (ctrl.messageStream)
						{
							ctrl.selectNextMessage(message);

							// delete message from message stream
							ctrl.messageStream.remove(message);
						}

						resolve();
					}));
				}

				// wait for completion
				try
				{
					ctrl.isLoading = true;
					$scope.$apply();

					await Promise.all(promiseList);
					await ctrl.messageStream.load(messages.length);
				}
				finally
				{
					ctrl.isLoading = false;
				}

				// clear mass edit list
				ctrl.massEditList = [];

				$scope.$apply();
			}

			ctrl.selectUnselectAll = () =>
			{
				if (ctrl.massEditList.length === 0)
				{
					ctrl.massEditList = ctrl.massEditList.concat(ctrl.messageStream);
				}
				else
				{
					ctrl.massEditList = [];
				}
			}

			ctrl.reloadMessageList = () =>
			{
				if (ctrl.onReloadMessages)
				{
					ctrl.onReloadMessages({});
				}
			}

			ctrl.openComposeModal = async (reply = false) =>
			{
				let selectedMessage = null;
				let selectedConversation = null;
				if (reply)
				{
					selectedMessage = await ctrl.getSelectedMessage();
					selectedConversation = await ctrl.messagingService.getConversation(selectedMessage.source, selectedMessage.conversationId);
				}

				await $uibModal.open(
					{
						component: 'messageCompose',
						backdrop: 'static',
						windowClass: "juno-simple-modal-window",
						resolve: {
							style: () => JUNO_STYLE.DEFAULT,
							messagingService: () => ctrl.messagingService,
							sourceId: () => reply ? selectedMessage.source.id : ctrl.sourceId,
							isReply: () => reply,
							recipient: () => ctrl.messageableFilter,
							subject: () => reply ? selectedMessage.subject : "",
							conversation: () => reply ? selectedConversation : null,
						}
					}
				).result;
			}

			/**
			 * select the message after the given message
			 * @param message - the message to select the message after
			 */
			ctrl.selectNextMessage = (message) =>
			{
				if (ctrl.messageStream)
				{
					// select message below the message we just deleted
					const messageIndex = ctrl.messageStream.indexOf(message);
					if (messageIndex !== -1 && messageIndex + 1 < ctrl.messageStream.length)
					{
						ctrl.selectedMessageId = ctrl.messageStream[messageIndex + 1].id;
					}
				}
			}

			/**
			 * get the currently selected message or messages depending on if the user has done a group select or not.
			 * @returns Message[]
			 */
			ctrl.getSelectedMessages = async () =>
			{
				let messages = [];
				if (ctrl.massEditList.length > 0)
				{
					messages = messages.concat(ctrl.massEditList);
				}
				else
				{
					messages.push(await ctrl.getSelectedMessage());
				}

				return messages;
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
					return await ctrl.messagingService.getMessage(await ctrl.messagingService.getMessageSourceById(ctrl.sourceId), ctrl.selectedMessageId);
				}
			}

			ctrl.onUnreadFilterChange = (checked) =>
			{
				ctrl.onlyUnread = checked;
				// update url
				$state.go(".", {onlyUnread: checked}, {location: "replace"});
			}

			ctrl.updateKeywordFilter = (keyword) =>
			{
				ctrl.searchKeyword = keyword;

				// update url
				$state.go(".", {keyword}, {location: "replace"});
			}


			$scope.$watch("$ctrl.massEditList.length", (newList) => ctrl.massSelectActive = ctrl.massEditList.length > 0)

		}],
});