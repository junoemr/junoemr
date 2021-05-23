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

angular.module("Messaging.Components").component('messageList', {
	templateUrl: 'src/messaging/inbox/components/messageList/messageList.jsp',
	bindings: {
		componentStyle: "<?",
		selectedMessageId: "=",
		messagingBackend: "<",
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
			ctrl.messageStream = null;
			ctrl.debounceTimeout = null;
			ctrl.DEBOUNC_TIME_MS = 500;
			ctrl.MESSAGE_FETCH_COUNT = 10;

			ctrl.$onInit = () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			};

			/**
			 * called when user selects a message from the list
			 * @param message - the selected message
			 */
			ctrl.onSelectMessage = (message) =>
			{
				const messagingService = MessagingServiceFactory.build(ctrl.messagingBackend);

				// mark message as read
				message.read = true;
				messagingService.updateMessage(message);

				// display the selected message
				ctrl.selectedMessageId = message.id;
				$state.go("messaging.view.message",
				{
					backend: ctrl.messagingBackend,
					source: ctrl.sourceId,
					group: ctrl.groupId,
					messageId: message.id,
				});
			}

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
						await messagingService.getMessageSourceById(ctrl.sourceId),
						{group: ctrl.groupId});
					$scope.$apply();
					await ctrl.messageStream.load(ctrl.MESSAGE_FETCH_COUNT);
					$scope.$apply();

					// notify parent of stream change.
					if (ctrl.messageStreamChange)
					{
						ctrl.messageStreamChange({stream: ctrl.messageStream});
					}
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
				ctrl.debounceTimeout = window.setTimeout(ctrl.reloadMessages, ctrl.DEBOUNC_TIME_MS);
			}

			// if bindings change reload message list.
			$scope.$watch("$ctrl.sourceId", ctrl.startReloadDebounce);
			$scope.$watch("$ctrl.groupId", ctrl.startReloadDebounce);
			$scope.$watch("$ctrl.messagingBackend", ctrl.startReloadDebounce);
		}],
});