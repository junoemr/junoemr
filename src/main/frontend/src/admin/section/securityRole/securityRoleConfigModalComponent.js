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
import {SecurityObjectTransfer, SecurityRolesApi} from "../../../../generated";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";

angular.module('Admin.Section').component('securityRoleConfigModal',
	{
		templateUrl: 'src/admin/section/securityRole/securityRoleConfigModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'$scope',
			'$http',
			'$httpParamSerializer',
			'$uibModal',
			'securityRolesStore',
			function ($scope, $http, $httpParamSerializer, $uibModal, securityRolesStore)
			{
				let ctrl = this;
				ctrl.securityRolesApi = new SecurityRolesApi($http, $httpParamSerializer, '../ws/rs');

				ctrl.AccessObjectsEnum = SecurityObjectTransfer.NameEnum;
				ctrl.PrivilegesEnum = SecurityObjectTransfer.PrivilegesEnum;

				ctrl.permissionLevelValues = Object.freeze({
					read: "r",
					readUpdate: "ru",
					readUpdateWrite: "ruw",
					readUpdateWriteDelete: "ruwd",
				});
				ctrl.permissionLevelOptions = Object.freeze([
					{
						label: "None",
						value: null,
					},
					{
						label: "Read",
						value: ctrl.permissionLevelValues.read,
					},
					{
						label: "Read/Update",
						value: ctrl.permissionLevelValues.readUpdate,
					},
					{
						label: "Read/Update/Create",
						value: ctrl.permissionLevelValues.readUpdateWrite,
					},
					{
						label: "Read/Update/Create/Delete",
						value: ctrl.permissionLevelValues.readUpdateWriteDelete,
					},
				]);

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.role = null;
				ctrl.newRole = true;
				ctrl.accessList = [];
				ctrl.isLoading = true;


				ctrl.$onInit = async () =>
				{
					ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

					ctrl.newRole = ctrl.resolve.newRole;
					if(ctrl.newRole)
					{
						ctrl.role = {
							id: null,
							name: "",
							description: "",
							accessObjects: {},
						};
					}
					else
					{
						ctrl.role = (await ctrl.securityRolesApi.getRole(ctrl.resolve.roleId)).data.body;
					}

					ctrl.allSecurityObjects = (await ctrl.securityRolesApi.getAccessObjects()).data.body;
					ctrl.computeAccessList();
					ctrl.isLoading = false;
				}

				// rebuild the access list
				ctrl.computeAccessList = () =>
				{
					ctrl.accessList = [];
					for (let i = 0; i < ctrl.allSecurityObjects.length; i++)
					{
						const secObject = ctrl.allSecurityObjects[i];
						const access = {
							id: i,
							name: secObject.name,
							description: secObject.description,
							permissionLevel: ctrl.getPermissionLevelForOptions(ctrl.role.accessObjects[secObject.name]),
						};
						ctrl.accessList.push(access);
					}
				}

				// translate model to frontend selection permissionLevelOptions
				ctrl.getPermissionLevelForOptions = (accessObject) =>
				{
					// this will change once the backend system removes the 'levels' of permission
					if(accessObject && accessObject.privileges)
					{
						if (accessObject.privileges.includes(ctrl.PrivilegesEnum.READ)
							&& accessObject.privileges.includes(ctrl.PrivilegesEnum.WRITE)
							&& accessObject.privileges.includes(ctrl.PrivilegesEnum.UPDATE)
							&& accessObject.privileges.includes(ctrl.PrivilegesEnum.DELETE))
						{
							return ctrl.permissionLevelValues.readUpdateWriteDelete;
						}
						else if (accessObject.privileges.includes(ctrl.PrivilegesEnum.WRITE))
						{
							return ctrl.permissionLevelValues.readUpdateWrite;
						}
						else if (accessObject.privileges.includes(ctrl.PrivilegesEnum.UPDATE))
						{
							return ctrl.permissionLevelValues.readUpdate;
						}
						else if (accessObject.privileges.includes(ctrl.PrivilegesEnum.READ))
						{
							return ctrl.permissionLevelValues.read;
						}
					}
					return null;
				}

				// translate ui object back to the model
				ctrl.translateAccessListToModel = () =>
				{
					for (let i = 0; i < ctrl.accessList.length; i++)
					{
						const accessObj = ctrl.accessList[i];
						ctrl.role.accessObjects[accessObj.name] = {
							name: accessObj.name,
							description: accessObj.description,
							privileges: ctrl.getPermissionsForModel(accessObj.permissionLevel),
						}
					}
				}

				// change permissions selection to model permissions
				ctrl.getPermissionsForModel = (permissionLevel) =>
				{
					let permissions = [];
					if(permissionLevel === ctrl.permissionLevelValues.read)
					{
						permissions.push(ctrl.PrivilegesEnum.READ);
					}
					else if(permissionLevel === ctrl.permissionLevelValues.readUpdate)
					{
						permissions.push(ctrl.PrivilegesEnum.READ);
						permissions.push(ctrl.PrivilegesEnum.UPDATE);
					}
					else if(permissionLevel === ctrl.permissionLevelValues.readUpdateWrite)
					{
						permissions.push(ctrl.PrivilegesEnum.READ);
						permissions.push(ctrl.PrivilegesEnum.UPDATE);
						permissions.push(ctrl.PrivilegesEnum.WRITE);
					}
					else if(permissionLevel === ctrl.permissionLevelValues.readUpdateWriteDelete)
					{
						permissions.push(ctrl.PrivilegesEnum.READ);
						permissions.push(ctrl.PrivilegesEnum.UPDATE);
						permissions.push(ctrl.PrivilegesEnum.WRITE);
						permissions.push(ctrl.PrivilegesEnum.DELETE);
					}
					return permissions;
				}

				ctrl.canEdit = () =>
				{
					return securityRolesStore.hasSecurityPrivileges(
						ctrl.AccessObjectsEnum.ADMINSECURITY,
						ctrl.PrivilegesEnum.UPDATE);
				}

				ctrl.canSave = () =>
				{
					return !ctrl.isLoading && ctrl.canEdit() && ctrl.role.name.length > 2
				}

				ctrl.canDelete = () =>
				{
					return !ctrl.isLoading && securityRolesStore.hasSecurityPrivileges(
						ctrl.AccessObjectsEnum.ADMINSECURITY,
						ctrl.PrivilegesEnum.DELETE);
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

				ctrl.onUpdate = async () =>
				{
					const userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Warning",
						"You are about to modify a user role. " +
						"This may change what content you and other system users have access to, including this page.\n" +
						"Are you sure you want to modify this user role?");

					if(userOk)
					{
						ctrl.isLoading = true;
						ctrl.translateAccessListToModel();
						ctrl.securityRolesApi.updateRole(ctrl.role.id, ctrl.role).then((response) =>
						{
							ctrl.modalInstance.close({
								operation: ctrl.resolve.operationEnum.UPDATE,
								data: response.data.body
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
					ctrl.translateAccessListToModel();
					ctrl.securityRolesApi.addRole(ctrl.role).then((response) =>
					{
						ctrl.modalInstance.close({
							operation: ctrl.resolve.operationEnum.ADD,
							data: response.data.body
						});
					}).catch(ctrl.errorFunction
					).finally(() =>
					{
						ctrl.isLoading = false;
					});
				}

				ctrl.onDelete = async () =>
				{
					const userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Warning",
						"You are about to delete a user role. " +
						"This may change what content you and other system users have access to, including this page.\n" +
						"Are you sure you want to delete this user role?");

					if(userOk)
					{
						ctrl.isLoading = true;
						ctrl.securityRolesApi.deleteRole(ctrl.role.id).then((response) =>
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