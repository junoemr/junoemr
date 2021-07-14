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
import FileUtil from "../../../../lib/util/FileUtil";
import {MessageableLocalType} from "../../../../lib/messaging/model/MessageableLocalType";
import {MessageableMappingConfidence} from "../../../../lib/messaging/model/MessageableMappingConfidence";
import {JunoDocumentFactory} from "../../../../lib/documents/factory/JunoDocumentFactory";
import DemographicDocumentService from "../../../../lib/documents/service/DemographicDocumentService";

angular.module("Messaging.Components").component('attachmentList', {
	templateUrl: 'src/messaging/inbox/components/attachmentList/attachmentList.jsp',
	bindings: {
		attachments: "=",
		message: "<?",
		showRemoveButton: "<?",
		singleColumn: "<?",
		showAttachToChart: "<?",
		hideHeader: "<?",
		componentStyle: "<",
	},
	controller: [
		"$scope",
		"$uibModal",
		function (
			$scope,
			$uibModal)
		{
			const ctrl = this;
			const documentService = new DemographicDocumentService();

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.isLoading = false;

			ctrl.$onInit = async () =>
			{
				ctrl.showRemoveButton = ctrl.showRemoveButton || false;
				ctrl.singleColumn = ctrl.singleColumn || false;
				ctrl.showAttachToChart = ctrl.showAttachToChart || false;
				ctrl.hideHeader = ctrl.hideHeader || false;
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

				if (ctrl.showAttachToChart)
				{
					ctrl.canAttachToChart = (await ctrl.messageablesWhoCanAttachToChart()).length > 0;
					$scope.$apply();
				}
			}

			ctrl.attachmentListClasses = () =>
			{
				return {
					"single-column": ctrl.singleColumn,
				};
			}

			ctrl.attachToChart = async (attachment) =>
			{
				try
				{
					ctrl.isLoading = true;
					const targets = await ctrl.messageablesWhoCanAttachToChart();

					if (targets.length > 1)
					{
						console.error("Attaching document to chart when there is more than one option is not implemented");
						ctrl.showAttachmentErrorAlert();
					}
					else if (targets.length === 1)
					{
						const demographicNo = await targets[0].localId();

						try
						{
							await $uibModal.open(
								{
									component: 'attachToChart',
									backdrop: 'static',
									windowClass: "juno-simple-modal-window",
									resolve: {
										style: () => JUNO_STYLE.GREY,
										attachment: () => attachment,
										demographicNo: () => demographicNo,
									}
								}
							).result;
						}
						catch (error)
						{
							// ESC key
						}

						// const ok = await Juno.Common.Util.confirmationDialog($uibModal,
						// 	`Attach to chart?`,
						// 	`Are you sure you want to attach ${attachment.name} to ${targets[0].name}'s chart?`);
						//
						// if (ok)
						// {
						// 	const junoDoc = JunoDocumentFactory.build(attachment.name, attachment.name, attachment.type, await attachment.getBase64Data());
						// 	await documentService.uploadDocumentToDemographicChart(junoDoc, await targets[0].localId());
						// }
					}
					else
					{
						console.error("no messageables to attach the document to");
						ctrl.showAttachmentErrorAlert();
					}
				}
				catch(error)
				{
					console.error(error);
					ctrl.showAttachmentErrorAlert();
				}
				finally
				{
					ctrl.isLoading = false;
					$scope.$apply();
				}
			}

			ctrl.openAttachmentPreview = async (attachment) =>
			{
				await $uibModal.open(
					{
						component: 'attachmentPreview',
						backdrop: 'static',
						windowClass: "juno-simple-modal-window",
						resolve: {
							style: () => JUNO_STYLE.GREY,
							file: () => attachment,
						}
					}
				).result;
			}

			ctrl.showAttachmentErrorAlert = () =>
			{
				Juno.Common.Util.errorAlert(
					$uibModal,
					"Count not attach to chart",
					"Something went wrong. Please contact support if the problem persists");
			}

			ctrl.removeAttachment = (attachment) =>
			{
				ctrl.attachments.splice(ctrl.attachments.indexOf(attachment), 1);
			}

			/**
			 * get all messageables attached to this message with local demographic mapping.
			 */
			ctrl.messageablesWithDemographicMapping = async () =>
			{
				if (!ctrl.message)
				{
					return [];
				}

				const possibleTargets = ctrl.message.recipients.concat(ctrl.message.sender);

				possibleTargets.map(async (target) =>{
					if (await target.hasLocalMapping() && (await target.localType()) === MessageableLocalType.DEMOGRAPHIC)
					{
						return target;
					}
					return null;
				});

				return (await Promise.all(possibleTargets)).filter((target) => !!target);
			};

			/**
			 * get a list of messageables who can have documents attached to their charts.
			 */
			ctrl.messageablesWhoCanAttachToChart = async () =>
			{
				let messageables = await ctrl.messageablesWithDemographicMapping();
				messageables = await Promise.all(messageables.map( async (messageable) =>
				{
					if ((await messageable.localMappingConfidenceLevel()) === MessageableMappingConfidence.HIGH)
					{
						return messageable;
					}
					return null;
				}));

				return messageables.filter((messageable) => !!messageable);
			}
		}],
});