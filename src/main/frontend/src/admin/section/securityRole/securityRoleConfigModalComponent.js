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

angular.module('Admin.Section').component('securityRoleConfigModal',
	{
		templateUrl: 'src/admin/section/securityRole/securityRoleConfigModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'$uibModal',
			'securityApiService',
			'securityRolesService',
			function ($uibModal, securityApiService, securityRolesService)
			{
				const ctrl = this;

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.role = null;
				ctrl.parentRole = null;
				ctrl.newRole = true;
				ctrl.isLoading = true;

				ctrl.allSecurityPermissions = [];
				ctrl.permissionsList = [];

				ctrl.$onInit = async () =>
				{
					ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
					ctrl.newRole = ctrl.resolve.newRole;

					if(ctrl.newRole)
					{
						let securityPermissions = [];
						let parentRoleId = null;
						if(ctrl.resolve.roleId) // new inherited role
						{
							ctrl.parentRole = await securityApiService.getRole(ctrl.resolve.roleId);
							securityPermissions = ctrl.parentRole.securityPermissions;
							parentRoleId = ctrl.resolve.roleId;
						}

						ctrl.role = {
							id: null,
							name: "",
							description: "",
							securityPermissions: securityPermissions,
							parentRoleId: parentRoleId,
						};
					}
					else
					{
						ctrl.role = await securityApiService.getRole(ctrl.resolve.roleId);
						if(ctrl.role.parentRoleId)
						{
							ctrl.parentRole = await securityApiService.getRole(ctrl.role.parentRoleId);
						}
					}

					ctrl.allSecurityPermissions = await securityApiService.getAllPermissions();
					await ctrl.loadPermissionsList();
					ctrl.isLoading = false;
				}

				ctrl.loadPermissionsList = async () =>
				{
					let currentPermissions = ctrl.role.securityPermissions;
					let permissionNames = currentPermissions.map((transfer) => transfer.permission);

					ctrl.permissionsList = ctrl.allSecurityPermissions.map((transfer) =>
					{
						return {
							label: transfer.permission,
							selected: permissionNames.includes(transfer.permission),
							tooltip: transfer.description,
							data: transfer,
						};
					});
				}

				ctrl.canEdit = () =>
				{
					if(ctrl.newRole)
					{
						return securityRolesService.hasSecurityPrivileges(
							SecurityPermissions.CONFIGURE_SECURITY_ROLES_CREATE);
					}
					else
					{
						return !ctrl.isSystemManaged() && securityRolesService.hasSecurityPrivileges(
							SecurityPermissions.CONFIGURE_SECURITY_ROLES_UPDATE);
					}
				}

				ctrl.canSave = () =>
				{
					return !ctrl.isLoading && ctrl.canEdit() && ctrl.role.name.length > 2
				}

				ctrl.canDelete = () =>
				{
					return !ctrl.isLoading && !ctrl.isSystemManaged() && securityRolesService.hasSecurityPrivileges(
						SecurityPermissions.CONFIGURE_SECURITY_ROLES_DELETE);
				}

				ctrl.isSystemManaged = () =>
				{
					return ctrl.role && ctrl.role.systemManaged;
				}

				ctrl.isInheritedRole = () =>
				{
					return Boolean(ctrl.parentRole);
				}

				ctrl.onCancel = () =>
				{
					ctrl.modalInstance.dismiss("cancelled");
				}

				ctrl.errorFunction = (error) =>
				{
					console.error(error);
					Juno.Common.Util.errorAlert($uibModal, "Error", "Failed to modify role");
				}

				ctrl.applyChangesToRole = () =>
				{
					ctrl.role.securityPermissions = ctrl.permissionsList.filter((permission) => permission.selected).map((permission) => permission.data);
				}

				ctrl.onUpdate = async () =>
				{
					const userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Warning",
						"You are about to modify a user role. " +
						"This may change what content you and other system users have access to, including this page.\n" +
						"Are you sure you want to modify this user role?");

					if(userOk)
					{
						ctrl.isLoading = true;
						ctrl.applyChangesToRole();
						securityApiService.updateRole(ctrl.role.id, ctrl.role).then((response) =>
						{
							ctrl.modalInstance.close({
								operation: ctrl.resolve.operationEnum.UPDATE,
								data: response,
							});
						}).catch(ctrl.errorFunction
						).finally(() =>
						{
							ctrl.isLoading = false;
						});
					}
				}

				ctrl.onCreate = () =>
				{
					ctrl.isLoading = true;
					ctrl.applyChangesToRole();
					securityApiService.addRole(ctrl.role).then((response) =>
					{
						ctrl.modalInstance.close({
							operation: ctrl.resolve.operationEnum.ADD,
							data: response,
						});
					}).catch(ctrl.errorFunction
					).finally(() =>
					{
						ctrl.isLoading = false;
					});
				}

				ctrl.onDelete = async () =>
				{
					const childCount = (ctrl.role.childRoleIds) ? ctrl.role.childRoleIds.length : 0;
					if(childCount > 0)
					{
						Juno.Common.Util.errorAlert($uibModal, "Action Prevented",
							"This role cannot be deleted, as " + childCount +
							" roles inherit from it. Please delete all inheriting roles first.");
						return;
					}

					const userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Warning",
						"You are about to delete a user role. " +
						"This may change what content you and other system users have access to, including this page.\n" +
						"Are you sure you want to delete this user role?");

					if(userOk)
					{
						ctrl.isLoading = true;
						securityApiService.deleteRole(ctrl.role.id).then((response) =>
						{
							ctrl.modalInstance.close({
								operation: ctrl.resolve.operationEnum.DELETE,
								data: null
							});
						}).catch(ctrl.errorFunction
						).finally(() =>
						{
							ctrl.isLoading = false;
						});
					}
				}
			}]
	});