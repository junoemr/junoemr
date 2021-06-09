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
		function (
			$scope)
		{
			const ctrl = this;

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

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

			ctrl.addFile = (junoFile) =>
			{
				ctrl.selectedFiles.push(junoFile);
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
				return mimeString.replace(/^[^/]*\//, '');
			}

			ctrl.dateCompare = (t1, t2) =>
			{
				return t2.value - t1.value;
			}

			$scope.$watchCollection("$ctrl.fileOptions", ctrl.updateFileSelectState);
			$scope.$watchCollection("$ctrl.selectedFiles", ctrl.updateFileSelectState);
		}]
});