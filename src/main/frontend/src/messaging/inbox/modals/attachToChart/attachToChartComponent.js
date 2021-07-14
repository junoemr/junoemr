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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";
import DocumentService from "../../../../lib/documents/service/DocumentService";
import {JunoDocumentFactory} from "../../../../lib/documents/factory/JunoDocumentFactory";
import DemographicDocumentService from "../../../../lib/documents/service/DemographicDocumentService";

angular.module("Messaging.Modals").component('attachToChart', {
	templateUrl: 'src/messaging/inbox/modals/attachToChart/attachToChart.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: [
		"$scope",
		function (
			$scope)
		{
			const ctrl = this;
			const documentService = new DocumentService();
			const demographicDocumentService = new DemographicDocumentService();

			$scope.LABEL_POSITION = LABEL_POSITION;
			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.isAttachingToChart = false;
			ctrl.description = null;
			ctrl.documentType = null;
			ctrl.documentTypes = []; //Type JunoDocumentType[]
			ctrl.documentTypesOptions = [];

			ctrl.$onInit = async () =>
			{
				ctrl.attachment = ctrl.resolve.attachment;
				ctrl.description = ctrl.attachment.name;
				ctrl.demographicNo = ctrl.resolve.demographicNo;
				await ctrl.loadDocumentTypes();
			}

			ctrl.loadDocumentTypes = async () =>
			{
				ctrl.documentTypes = await documentService.getDemographicDocumentTypes();
				ctrl.documentTypesOptions = ctrl.documentTypes.map((type) => {
					return {label: type.type, value: type};
				});
			}

			ctrl.canAttach = () =>
			{
				return ctrl.description && ctrl.documentType;
			}

			ctrl.attachToChart = async () =>
			{
				try
				{
					ctrl.isAttachingToChart = true;
					const junoDoc = JunoDocumentFactory.build(ctrl.attachment.name, ctrl.description, ctrl.documentType, ctrl.attachment.type, await ctrl.attachment.getBase64Data());
					await demographicDocumentService.uploadDocumentToDemographicChart(junoDoc, ctrl.demographicNo);
					this.close();
				}
				finally
				{
					ctrl.isAttachingToChart = false;
					$scope.$apply();
				}
			}

			ctrl.close = () =>
			{
				ctrl.modalInstance.close();
			}
		}],
});
