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

import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../common/security/securityConstants";

angular.module('Admin.Section').component('securityRoleConfig',
	{
		templateUrl: 'src/admin/section/securityRole/securityRoleConfig.jsp',
		bindings: {},
		controller: [
			'$uibModal',
			'NgTableParams',
			'securityApiService',
			'securityRolesService',
			function ($uibModal, NgTableParams, securityApiService, securityRolesService)
			{
				const ctrl = this;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.operationEnum = Object.freeze({
					ADD: "add",
					UPDATE: "update",
					DELETE: "delete",
				});

				// ctrl.sortMode = "name";
				ctrl.tableParams = new NgTableParams(
					{
						page: 1, // show first page
						count: -1, // unlimited
						sorting:
						{
							name: 'asc',
						}
					});

				ctrl.rolesList = [];

				ctrl.$onInit = async () =>
				{
					ctrl.componentStyle = JUNO_STYLE.DEFAULT;
					ctrl.rolesList = await securityApiService.getRoles();
				}

				ctrl.onRoleDetails = (role) =>
				{
					ctrl.openDetailsModal(role, false);
				}

				ctrl.onAddRole = () =>
				{
					ctrl.openDetailsModal(null, true);
				}

				ctrl.onExtendRole = (role) =>
				{
					ctrl.openDetailsModal(role, true);
				}

				ctrl.canAddRole = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConfigureSecurityRolesCreate);
				}

				ctrl.openDetailsModal = (role, newRole) =>
				{
					$uibModal.open(
						{
							component: 'securityRoleConfigModal',
							backdrop: 'static',
							windowClass: "juno-modal lg",
							resolve: {
								newRole: newRole,
								roleId: (role) ? role.id : null,
								operationEnum: ctrl.operationEnum,
							}
						}
					).result.then((response) =>
					{
						const operation = response.operation;
						const updatedRole = response.data;

						if(operation === ctrl.operationEnum.ADD)
						{
							ctrl.rolesList.push(updatedRole);
						}
						else
						{
							const index = ctrl.rolesList.indexOf(role);
							if (operation === ctrl.operationEnum.UPDATE)
							{
								ctrl.rolesList[index] = updatedRole;
							}
							else if (operation === ctrl.operationEnum.DELETE)
							{
								ctrl.rolesList.splice(index, 1);
							}
						}
						// force the cached access/permissions values to reload
						securityRolesService.loadUserRoles();
					}).catch((reason) =>
					{
						// do nothing on cancel
					});
				}

				ctrl.openSecuritySetsModal = () =>
				{
					$uibModal.open(
						{
							component: 'securityRoleSetModal',
							backdrop: 'static',
							windowClass: "juno-modal",
							resolve: {
							}
						}
					).result.then((response) =>
					{

					}).catch((reason) =>
					{
						// do nothing on cancel
					});
				}
			}]
	});