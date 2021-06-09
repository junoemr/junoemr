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
import MessageFactory from "../../../../lib/messaging/factory/MessageFactory";
import {MessageableMappingConfidence} from "../../../../lib/messaging/model/MessageableMappingConfidence";

angular.module("Messaging.Modals").component('messageCompose', {
	templateUrl: 'src/messaging/inbox/modals/messageCompose/messageCompose.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		"$scope",
		"$uibModal",
		function (
			$scope,
			$uibModal)
		{
			const ctrl = this;

			$scope.JUNO_SIMPLE_MODAL_FILL_COLOR = JUNO_SIMPLE_MODAL_FILL_COLOR;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			$scope.LABEL_POSITION = LABEL_POSITION;

			ctrl.recipient = null;
			ctrl.subject = "";
			ctrl.sending = false;
			ctrl.messageSourceOptions = [];
			ctrl.attachments = [];

			ctrl.$onInit = async () =>
			{
				ctrl.messagingService = ctrl.resolve.messagingService;
				ctrl.sourceId = ctrl.resolve.sourceId;
				ctrl.isReply = ctrl.resolve.isReply || false;
				ctrl.subject = ctrl.resolve.subject || "";
				ctrl.conversation = ctrl.resolve.conversation || null;
				ctrl.participantNames = this.getParticipantNames();
				ctrl.messageSourceOptions = await ctrl.loadMessageSourceOptions();

				ctrl.setupValidations();

				$scope.$apply();
			}

			ctrl.setupValidations = () =>
			{
				ctrl.validations = {
					messageText: Juno.Validations.validationFieldRequired(ctrl, "subject",
						Juno.Validations.validationCustom(() => ctrl.messageTextarea.text().length > 0)),

					sourceSelected: Juno.Validations.validationFieldOr(
						Juno.Validations.validationCustom(() => ctrl.messageSourceOptions.find((sourceOpt) => sourceOpt.value === ctrl.sourceId)),
						Juno.Validations.validationFieldTrue(ctrl, "isReply")),

					recipientSelected: Juno.Validations.validationFieldOr(
						Juno.Validations.validationFieldRequired(ctrl, "recipient"),
						Juno.Validations.validationFieldTrue(ctrl, "isReply")),
				};
			}

			ctrl.loadMessageSourceOptions = async () =>
			{
				const sources = (await ctrl.messagingService.getMessageSources()).filter((source) => !source.isVirtual);

				return sources.map((source) => {
					return {
						label: source.name,
						value: source.id,
					}
				})
			}

			ctrl.uploadAttachment = async () =>
			{
				const attachments = await $uibModal.open({
					component: "attachmentSelect",
					backdrop: 'static',
					windowClass: "juno-simple-modal-window",
					resolve: {
						style: () => ctrl.resolve.style,
						messageable: () => ctrl.recipient,
					}
				});

				// const files = await FileUtil.uploadFile(AllowedAttachmentTypes);
				// for (const file of files)
				// {
				// 	ctrl.attachments.push(AttachmentFactory.build(file.name, file.type, await FileUtil.getFileDataBase64(file)));
				// }

				$scope.$apply();
			}

			/**
			 * send the newly composed message
			 */
			ctrl.sendMessage = async () =>
			{
				try
				{
					ctrl.sending = true;
					let message = MessageFactory.build(
						ctrl.subject,
						ctrl.messageTextarea.text(),
						ctrl.recipient ? [ctrl.recipient] : [],
						ctrl.attachments,
						ctrl.isReply ? ctrl.conversation : null);

					await ctrl.messagingService.sendMessage(await ctrl.messagingService.getMessageSourceById(ctrl.sourceId), message);
					ctrl.modalInstance.close();
				}
				catch(error)
				{
					Juno.Common.Util.errorAlert($uibModal, "Failed to send message", "Some thing went wrong while sending your message. Please contact support if the problem persists");
					throw error;
				}
				finally
				{
					ctrl.sending = false;
				}
			}

			ctrl.onCancel = () =>
			{
				ctrl.modalInstance.close();
			};

			ctrl.onSourceChange = (value) =>
			{
				ctrl.recipient = null;
			}

			ctrl.canSend = () =>
			{
				return Juno.Validations.allValidationsValid(ctrl.validations) && !ctrl.sending;
			}

			ctrl.sendButtonTooltip = () =>
			{
				if (ctrl.canSend())
				{
					return "";
				}
				else if (!ctrl.validations.sourceSelected())
				{
					return "Please select a sender";
				}
				else if (!ctrl.validations.recipientSelected())
				{
					return "Please select a recipient";
				}
				else if (!ctrl.validations.messageText())
				{
					return "Please fill out the subject and message";
				}
				else
				{
					return "Unable to send message. If the problem persists please contact support";
				}
			}

			ctrl.getParticipantNames = () =>
			{
				if (ctrl.conversation)
				{
					return ctrl.conversation.participants.map((participant) => participant.name).join(", ");
				}
				return "";
			}
		}],
});