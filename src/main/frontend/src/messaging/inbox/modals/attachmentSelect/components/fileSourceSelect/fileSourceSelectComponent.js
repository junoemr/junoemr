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

import {FileSource} from "./FileSource";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../../../../common/components/junoComponentConstants";

angular.module("Messaging.Modals.AttachmentSelect.Components").component('fileSourceSelect', {
	templateUrl: 'src/messaging/inbox/modals/attachmentSelect/components/fileSourceSelect/fileSourceSelect.jsp',
	bindings: {
		selectedSource: "=?",
		onSourceSelected: "&?",
		hideChartSources: "<?",
	},
	controller: [
		"$scope",
		function (
			$scope)
		{
			const ctrl = this;

			$scope.FileSource = FileSource;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.$onInit = () =>
			{
				ctrl.selectedSource = ctrl.selectedSource || FileSource.DOCUMENTS;
				ctrl.hideChartSources = ctrl.hideChartSources || false;
			}

			ctrl.selectSource = (source) =>
			{
				if (source !== FileSource.COMPUTER)
				{
					ctrl.selectedSource = source;
				}

				if (ctrl.onSourceSelected)
				{
					ctrl.onSourceSelected({value: source});
				}
			}

			ctrl.sourceClasses = (source) =>
			{
				return {
					"selected": source === ctrl.selectedSource,
				};
			}
		}]
});