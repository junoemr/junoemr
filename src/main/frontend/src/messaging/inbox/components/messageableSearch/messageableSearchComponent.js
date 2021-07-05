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

import {MessageableMappingConfidence} from "../../../../lib/messaging/model/MessageableMappingConfidence";

const {JUNO_STYLE} = require("../../../../common/components/junoComponentConstants");

angular.module("Messaging.Components").component('messageableSearch', {
	templateUrl: 'src/messaging/inbox/components/messageableSearch/messageableSearch.jsp',
	bindings: {
		ngModel: "=",
		label: "@?",
		placeholder: "@?",
		messagingService: "<",
		sourceId: "<",
		disabled: "<?",
		icon: "@?",
		onSelected: "&?",
		componentStyle: "<?"
	},
	controller: [
		"$scope",
		function ($scope)
		{
			const ctrl = this;
			ctrl.selectedMessageableId = null;
			ctrl.options = [];
			ctrl.showHighConfidenceCheckmark = false;
			ctrl.patientConfidenceMessage = "";

			ctrl.$onInit = async () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
				ctrl.placeholder = ctrl.placeholder || "Search";
				ctrl.disabled = ctrl.disabled || false;
				ctrl.label = ctrl.label || "";
			}

			$scope.$watch("$ctrl.ngModel", async (newVal) =>
			{
				if (newVal)
				{
					await this.loadSearchOptions(newVal.name);
					ctrl.selectedMessageableId = newVal.id
				}
				else
				{
					ctrl.selectedMessageableId = null;
				}
				ctrl.updateCheckmarkVisibility();
			});

			ctrl.checkMessageableSelection = (selection) =>
			{
				if (ctrl.ngModel && ctrl.ngModel.id !== selection)
				{
					// clear selection if id no longer matches
					ctrl.ngModel = null;
					ctrl.updateCheckmarkVisibility();
				}
			}

			ctrl.onMessageableSelected = (selection) =>
			{
				ctrl.ngModel = selection.data;

				if (ctrl.onSelected)
				{
					ctrl.onSelected({value: ctrl.ngModel});
				}

				ctrl.updateCheckmarkVisibility();
			}

			ctrl.loadSearchOptions = async (keyword) =>
			{
				if (keyword)
				{
					// strip "formatting" characters
					keyword = keyword.replace(/[,()]/g, '');
				}

				if (keyword && keyword.length > 2)
				{
					const messageables = await ctrl.messagingService.searchMessageables(await ctrl.messagingService.getMessageSourceById(ctrl.sourceId), keyword);

					ctrl.options = messageables.map((messageable) =>
					{
						return {
							label: messageable.identificationName,
							value: messageable.id,
							data: messageable,
						}
					});
				}

				return ctrl.options;
			};

			ctrl.updateCheckmarkVisibility = async () =>
			{
				if (ctrl.ngModel)
				{
					ctrl.showHighConfidenceCheckmark = (await ctrl.ngModel.localMappingConfidenceLevel()) === MessageableMappingConfidence.HIGH;
					ctrl.patientConfidenceMessage = await ctrl.ngModel.localMappingConfidenceExplanationString();
					$scope.$apply();
				}
				else
				{
					ctrl.showHighConfidenceCheckmark = false;
				}
			}
		}],
});