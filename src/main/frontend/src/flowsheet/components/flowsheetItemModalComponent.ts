/**
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

import {SecurityPermissions} from "../../common/security/securityConstants";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../common/components/junoComponentConstants";
import {ItemType} from "../../lib/flowsheet/FlowsheetConstants";

angular.module('Flowsheet').component('flowsheetItemModal',
	{
		templateUrl: 'src/flowsheet/components/flowsheetItemModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'flowsheetApiService',
			function (
				flowsheetApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.isLoading = true;
				ctrl.type = null;
				ctrl.model = null;

				ctrl.itemList = [];

				ctrl.$onInit = async (): Promise<void> =>
				{
					ctrl.componentStyle = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
					ctrl.type = ctrl.resolve.itemType;

					let results = [];
					if(ctrl.type === ItemType.PREVENTION)
					{
						results = await flowsheetApiService.getPreventionTypes();
					}
					ctrl.itemList = results.map((entry) =>
					{
						return {label: entry, value: entry};
					});

					//todo replace with model object
					ctrl.model = {
						name: null,
					}

					ctrl.isLoading = false;
				}

				ctrl.cancel = (): void =>
				{
					ctrl.modalInstance.dismiss();
				}

				ctrl.onComplete = (): void =>
				{
					ctrl.modalInstance.close(ctrl.model);
				}

				ctrl.getModalTitle = (): string =>
				{
					if(ctrl.type === ItemType.MEASUREMENT)
					{
						return "Add Flowsheet Measurement";
					}
					else if(ctrl.type === ItemType.PREVENTION)
					{
						return "Add Flowsheet Prevention";
					}
					return "Flowsheet Item";
				}
			}]
	});
