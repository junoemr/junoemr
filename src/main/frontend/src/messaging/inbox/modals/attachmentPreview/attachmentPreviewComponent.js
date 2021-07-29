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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../../common/components/junoComponentConstants";
import FileUtil from "../../../../lib/util/FileUtil";

angular.module("Messaging.Modals").component('attachmentPreview', {
	templateUrl: 'src/messaging/inbox/modals/attachmentPreview/attachmentPreview.jsp',
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

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.isLoading = false;

			ctrl.$onInit = async () =>
			{
				ctrl.file = ctrl.resolve.file;
				ctrl.componentStyle = ctrl.resolve.style;

				try
				{
					ctrl.isLoading = true;
					ctrl.fileData = await ctrl.file.getBase64Data();
					ctrl.injectAttachment(ctrl.fileData);
				}
				finally
				{
					ctrl.isLoading = false;
					$scope.$apply();
				}
			}

			// inject attachment data in to preview
			ctrl.injectAttachment = (data) =>
			{
				if (ctrl.canPreviewFile() && !ctrl.isFileTextType())
				{
					let classes = "";
					if (ctrl.isFileDocumentType())
					{
						classes = "stretch"
					}
					else if (ctrl.isImageFileType())
					{
						classes = "clamp-width"
					}

					ctrl.attachmentContainer.get(0).innerHTML = `<embed class="embedded-content ${classes}" src="data:${ctrl.file.type};utf-8;base64,${data}" alt="Attachment preview" type="${ctrl.file.type}"\>`;
				}
			}

			ctrl.isFileDocumentType = () =>
			{
				return ctrl.file.type.match(`^application/.*`);
			}

			ctrl.isFileTextType = () =>
			{
				return ctrl.file.type.match("^text/.*");
			}

			ctrl.isImageFileType = () =>
			{
				return ctrl.file.type.match("^image/*");
			}

			ctrl.canPreviewFile = () =>
			{
				const type = ctrl.file.type;
				return type.match(`^image/.*`) ||
					type.match(`^text/.*`) ||
					type === "application/pdf";
			}

			ctrl.fileDataAsText = () =>
			{
				return FileUtil.base64ToUtf8(ctrl.fileData);
			}

			ctrl.download = async () =>
			{
				await FileUtil.saveFile(ctrl.file.name, ctrl.file.type, ctrl.fileData);
			}

			ctrl.close = () =>
			{
				ctrl.modalInstance.close();
			}
		}],
});
