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
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../common/security/securityConstants";

angular.module('Admin.Section').component('securityRoleSetModal',
	{
		templateUrl: 'src/admin/section/securityRole/securityRoleSetModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'$uibModal',
			'securityApiService',
			'securityRolesService',
			'reportingService',
			function ($uibModal,
			          securityApiService,
			          securityRolesService,
			          reportingService)
			{
				const ctrl = this;
				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.providerOptions = [];
				ctrl.selectedProvider = null;

				ctrl.availableSetsList = [];
				ctrl.selectedSetsList = [];

				ctrl.$onInit = async () =>
				{
					ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

					securityApiService.getAllProviders().then((results) =>
					{
						ctrl.providerOptions = results.map((provider) =>
						{
							return {
								label: provider.name,
								value: provider.providerNo,
							}
						});
					});
					reportingService.getDemographicSetList().then((results) =>
					{
						ctrl.availableSetsList = results.content;
					});
				}

				ctrl.onProviderSelected = (value) =>
				{
					ctrl.reloadSelectedSetsList(value);
				}
				ctrl.reloadSelectedSetsList = async (value) =>
				{
					const assignedSets = await securityApiService.getProviderSecurityDemographicSetsBlacklist(value);
					ctrl.selectedSetsList = ctrl.availableSetsList.map((set) =>
					{
						return {
							label: set,
							selected: assignedSets.includes(set),
							data: set,
						};
					});
				}

				ctrl.onBlacklistChange = async (item, model) =>
				{
					const assignedSets = model.filter((modelItem) => modelItem.selected).map((modelItem) => modelItem.data);
					await securityApiService.setProviderSecurityDemographicSetsBlacklist(ctrl.selectedProvider, assignedSets);
				}

				ctrl.onCancel = () =>
				{
					ctrl.modalInstance.dismiss("cancelled");
				}

				ctrl.canSave = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConfigureSecurityRolesUpdate);
				}

				ctrl.onSave = () =>
				{
				}

			}]
	});