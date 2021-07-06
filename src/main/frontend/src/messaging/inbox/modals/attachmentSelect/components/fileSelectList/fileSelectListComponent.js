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

import {JUNO_BUTTON_COLOR} from "../../../../../../common/components/junoComponentConstants";
import {AllowedAttachmentMimeTypes} from "../../../../../../lib/messaging/constants/AllowedAttachmentTypes";
import Attachment from "../../../../../../lib/messaging/model/Attachment";

angular.module("Messaging.Modals.AttachmentSelect.Components").component('fileSelectList', {
	templateUrl: 'src/messaging/inbox/modals/attachmentSelect/components/fileSelectList/fileSelectList.jsp',
	bindings: {
		fileOptions: "<",
		selectedFiles: "=",
		onFileSelected: "&",
		onFileRemoved: "&"
	},
	controller: [
		"$scope",
		"$uibModal",
		function (
			$scope,
			$uibModal)
		{
			const ctrl = this;

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

			ctrl.fileCurrentlyBeingAdded = null;

			ctrl.formatFileDate = (date) =>
			{
				return date.format(Juno.Common.Util.settings.date_format);
			}

			ctrl.onFileSelect = (file) =>
			{
				if (file.selected)
				{
					ctrl.removeFile(file);
					if (ctrl.onFileRemoved)
					{
						ctrl.onFileRemoved({value: file});
					}
				}
				else
				{
					ctrl.addFile(file);
					if (ctrl.onFileSelected)
					{
						ctrl.onFileSelected({value: file});
					}
				}
			}

			ctrl.addFile = async (junoFile) =>
			{
				if (AllowedAttachmentMimeTypes.includes(junoFile.type))
				{
					try
					{
						ctrl.fileCurrentlyBeingAdded = junoFile;

						const fileBinary = atob(await junoFile.getBase64Data());

						if (fileBinary.length <= Attachment.MAX_ATTACHMENT_SIZE_BYTES)
						{
							ctrl.selectedFiles.push(junoFile);
						}
						else
						{
							Juno.Common.Util.errorAlert($uibModal,
								"File is to big",
								`File ${junoFile.name} is ${Math.trunc(fileBinary.length / 1024 / 1024)} MB which 
							exceeds the limit of ${Attachment.MAX_ATTACHMENT_SIZE_BYTES / 1024 / 1024} MB.`);
						}
					}
					finally
					{
						ctrl.fileCurrentlyBeingAdded = null;
						$scope.$apply();
					}
				}
				else
				{
					Juno.Common.Util.errorAlert($uibModal,
						"File type not allowed",
						`File ${junoFile.name} of type ${ctrl.fileTypeHuman(junoFile.type)} cannot be attached to patient messages.`);
				}
			}

			ctrl.removeFile = (junoFile) =>
			{
				const index = ctrl.selectedFiles.indexOf(junoFile);
				if (index !== -1)
				{
					ctrl.selectedFiles.splice(index, 1);
				}
			}

			ctrl.updateFileSelectState = () =>
			{
				ctrl.fileOptions.forEach((fileOpt) =>
				{
					fileOpt.selected = ctrl.selectedFiles.includes(fileOpt);
				});
			}

			ctrl.fileTypeHuman = (mimeString) =>
			{
				if (mimeString)
				{
					return mimeString.replace(/^[^/]*\//, '');
				}
				return "Unknown";
			}

			ctrl.dateCompare = (t1, t2) =>
			{
				return t2.value - t1.value;
			}

			$scope.$watchCollection("$ctrl.fileOptions", ctrl.updateFileSelectState);
			$scope.$watchCollection("$ctrl.selectedFiles", ctrl.updateFileSelectState);
		}]
});