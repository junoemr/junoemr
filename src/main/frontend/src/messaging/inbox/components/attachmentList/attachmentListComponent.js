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

angular.module("Messaging.Components").component('attachmentList', {
	templateUrl: 'src/messaging/inbox/components/attachmentList/attachmentList.jsp',
	bindings: {
		attachments: "=",
		showRemoveButton: "<?",
		singleColumn: "<?",
		componentStyle: "<",
	},
	controller: [
		"$scope",
		function ($scope)
		{
			const ctrl = this;

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.$onInit = () =>
			{
				ctrl.showRemoveButton = ctrl.showRemoveButton || false;
				ctrl.singleColumn = ctrl.singleColumn || false;
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			}

			ctrl.attachmentListClasses = () =>
			{
				return {
					"single-column": ctrl.singleColumn,
				};
			}

			ctrl.removeAttachment = (attachment) =>
			{
				ctrl.attachments.splice(ctrl.attachments.indexOf(attachment), 1);
			}

			ctrl.downloadAttachment = async (attachment) =>
			{
				await FileUtil.saveFile(attachment.name, attachment.type, await attachment.getBase64Data());
			}
		}],
});