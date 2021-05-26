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

const {JUNO_STYLE} = require("../../../../../../common/components/junoComponentConstants");

angular.module("Messaging.Modals").component('messageableSearch', {
	templateUrl: 'src/messaging/inbox/modals/messageCompose/components/messageableSearch/messageableSearch.jsp',
	bindings: {
		ngModel: "=",
		messagingService: "<",
		sourceId: "<",
		componentStyle: "<?"
	},
	controller: [
		"$scope",
		function ($scope)
		{
			const ctrl = this;
			ctrl.selectedMessageableId = null;
			ctrl.options = [];

			ctrl.$onInit = async () =>
			{
				ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			}

			ctrl.checkMessageSelection = (selection) =>
			{
				if (ctrl.ngModel && ctrl.ngModel.id !== selection)
				{
					// clear selection if id no longer matches
					ctrl.ngModel = null;
				}
			}

			ctrl.onMessageableSelected = (selection) =>
			{
				ctrl.ngModel = selection.data;
			}

			ctrl.loadSearchOptions = async (keyword) =>
			{
				if (keyword)
				{
					// strip ',' character
					keyword = keyword.replace(/,/g, '');
				}

				if (keyword && keyword.length > 2)
				{
					const messageables = await ctrl.messagingService.searchMessageables(await ctrl.messagingService.getMessageSourceById(ctrl.sourceId), keyword);

					ctrl.options = messageables.map((messageable) =>
					{
						return {
							label: messageable.name,
							value: messageable.id,
							data: messageable,
						}
					});
				}

				return ctrl.options;
			};
		}],
});