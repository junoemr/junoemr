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
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../../common/components/junoComponentConstants";
import {MessageableMappingConfidence} from "../../../../lib/messaging/model/MessageableMappingConfidence";
import DemographicDocumentService from "../../../../lib/documents/service/DemographicDocumentService";
import JunoFileToAttachmentConverter from "../../../../lib/messaging/converter/JunoFileToAttachmentConverter";
import {FileSource} from "./components/fileSourceSelect/FileSource";
import {AllowedAttachmentTypes} from "../../../../lib/messaging/constants/AllowedAttachmentTypes";
import FileUtil from "../../../../lib/util/FileUtil";
import AttachmentFactory from "../../../../lib/messaging/factory/AttachmentFactory";
import Attachment from "../../../../lib/messaging/model/Attachment";

angular.module("Messaging.Modals").component('attachmentSelect', {
	templateUrl: 'src/messaging/inbox/modals/attachmentSelect/attachmentSelect.jsp',
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
			const demographicDocumentService = new DemographicDocumentService();

			$scope.JUNO_SIMPLE_MODAL_FILL_COLOR = JUNO_SIMPLE_MODAL_FILL_COLOR;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.selectedAttachments = []; // Type JunoFile[]
			ctrl.currentFileList = []; // Type JunoFile[]
			ctrl.documentFileList = []; // Type JunoFile[]
			ctrl.canReadChart = false;

			ctrl.$onInit = async () =>
			{
				ctrl.messageable = ctrl.resolve.messageable;
				ctrl.canReadChart = await ctrl.checkCanReadChart();
				ctrl.messagingBackendType = ctrl.resolve.messagingBackendType;

				await ctrl.loadDocumentFiles();

				// set default to patient documents
				if (ctrl.canReadChart)
				{
					ctrl.currentFileList = ctrl.documentFileList;
					$scope.$apply();
				}
			}

			ctrl.closeModal = (returnAttachments = false) =>
			{
				ctrl.modalInstance.close(returnAttachments ? ctrl.selectedAttachments : []);
			}

			ctrl.switchFileSource = async (source) =>
			{
				switch (source)
				{
					case FileSource.COMPUTER:
						await ctrl.attachFilesFromComputer();
						break;
					case FileSource.DOCUMENTS:
						ctrl.currentFileList = ctrl.documentFileList;
						break;
				}
			}

			ctrl.attachFilesFromComputer = async () =>
			{
				const files = await FileUtil.uploadFile(AllowedAttachmentTypes);

				for (const file of files)
				{
					const base64Data = await FileUtil.getFileDataBase64(file);
					const binaryData = atob(base64Data);

					if (binaryData.length <= Attachment.MAX_ATTACHMENT_SIZE_BYTES)
					{
						ctrl.selectedAttachments.push(AttachmentFactory.build(file.name, file.type, base64Data))
					}
					else
					{
						Juno.Common.Util.errorAlert($uibModal,
							"File is to big",
							`File ${file.name} is ${Math.trunc(binaryData.length / 1024 / 1024)} MB which 
							exceeds the limit of ${Attachment.MAX_ATTACHMENT_SIZE_BYTES / 1024 / 1024} MB.`);
					}
				}

				$scope.$apply();
			}

			/**
			 * load the list of documents that could be attached to the message
			 */
			ctrl.loadDocumentFiles = async () =>
			{
				ctrl.documentFileList = [];

				if (ctrl.canReadChart)
				{
					// JunoDocument conforms to JunoFile.
					ctrl.documentFileList = await demographicDocumentService.getDemographicDocuments(await ctrl.messageable.localId());
				}
			}

			/**
			 * checks if the patients chart can be read.
			 * @return promise that resolves to true / false
			 */
			ctrl.checkCanReadChart = async () =>
			{
				return ctrl.messageable &&
					await ctrl.messageable.hasLocalMapping() &&
					(await ctrl.messageable.localMappingConfidenceLevel()) === MessageableMappingConfidence.HIGH;
			}
		}]
});