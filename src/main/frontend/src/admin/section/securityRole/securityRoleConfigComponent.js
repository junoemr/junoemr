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

import {SecurityRolesApi} from "../../../../generated";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";

const {UserSecurityRolesTransfer} = require("../../../../generated");
const {SecurityObjectsTransfer} = require("../../../../generated");

angular.module('Admin.Section').component('securityRoleConfig',
	{
		templateUrl: 'src/admin/section/securityRole/securityRoleConfig.jsp',
		bindings: {},
		controller: [
			'$scope',
			'$http',
			'$httpParamSerializer',
			'$uibModal',
			'securityRolesStore',
			function ($scope, $http, $httpParamSerializer, $uibModal, securityRolesStore)
			{
				let ctrl = this;
				ctrl.access = SecurityObjectsTransfer.AccessObjectsEnum.ADMINSECURITY;
				ctrl.permissions = UserSecurityRolesTransfer.PrivilegesEnum.READ;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.securityRolesApi = new SecurityRolesApi($http, $httpParamSerializer, '../ws/rs');

				ctrl.rolesList = [];

				ctrl.$onInit = async () =>
				{
					ctrl.componentStyle = JUNO_STYLE.DEFAULT;
					ctrl.rolesList = (await ctrl.securityRolesApi.getRoles()).data.body;
				}

				ctrl.onRoleDetails = (role) =>
				{
					ctrl.openDetailsModal(role, false);
				}

				ctrl.onAddRole = () =>
				{
					ctrl.openDetailsModal(null, true);
				}

				ctrl.canAddRole = () =>
				{
					return securityRolesStore.hasSecurityPrivileges(
						SecurityObjectsTransfer.AccessObjectsEnum.ADMINSECURITY,
						UserSecurityRolesTransfer.PrivilegesEnum.WRITE);
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
								role: role,
							}
						}
					).result.then((updatedRole) =>
					{
						if (newRole)
						{
							ctrl.rolesList.push(updatedRole);
						}
						else // update existing
						{
							const index = ctrl.rolesList.indexOf(role);
							ctrl.rolesList[index] = updatedRole;
						}
						// force the cached access/permissions values to reload
						securityRolesStore.loadUserRoles();
					}).catch((reason) =>
					{
						// do nothing on cancel
					});
				}
			}]
	});