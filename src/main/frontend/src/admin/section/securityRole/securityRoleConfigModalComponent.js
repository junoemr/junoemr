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
import {SecurityObjectsTransfer, SecurityRolesApi, UserSecurityRolesTransfer} from "../../../../generated";
import {JUNO_STYLE} from "../../../common/components/junoComponentConstants";

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

            ctrl.permissionLevelOptions = Object.freeze([
                {
                    label: "None",
                    value: null,
                },
                {
                    label: "Read",
                    value: UserSecurityRolesTransfer.PrivilegesEnum.READ,
                },
                {
                    label: "Read/Update",
                    value: UserSecurityRolesTransfer.PrivilegesEnum.UPDATE,
                },
                {
                    label: "Read/Update/Create",
                    value: UserSecurityRolesTransfer.PrivilegesEnum.WRITE,
                },
                {
                    label: "Read/Update/Create/Delete",
                    value: "ALL",
                },
            ]);

            ctrl.role = null;
            ctrl.accessList = [];


            ctrl.$onInit = async () =>
            {
                ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

                ctrl.role = ctrl.resolve.role;
                ctrl.allSecurityObjects = (await ctrl.securityRolesApi.getAccessObjects()).data.body.accessObjects;
                ctrl.computeAccessList();
            }

            ctrl.computeAccessList = () =>
            {
                ctrl.accessList = [];
                for (let i = 0; i < ctrl.allSecurityObjects.length; i++)
                {
                    const element = ctrl.allSecurityObjects[i];
                    const access = {
                        id: i,
                        name: element,
                        description: null,
                        permissionLevel: ctrl.getPermissionLevel(element, ctrl.role),
                    };
                    ctrl.accessList.push(access);
                }
            }

            ctrl.getPermissionLevel = (access, role) =>
            {
                if(role.privileges[access])
                {
                    if (role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.DELETE)
                        && role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.WRITE)
                        && role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.UPDATE)
                        && role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.DELETE))
                    {
                        return "ALL";
                    }
                    else if (role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.WRITE))
                    {
                        return UserSecurityRolesTransfer.PrivilegesEnum.WRITE;
                    }
                    else if (role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.UPDATE))
                    {
                        return UserSecurityRolesTransfer.PrivilegesEnum.UPDATE;
                    }
                    else if (role.privileges[access].includes(UserSecurityRolesTransfer.PrivilegesEnum.READ))
                    {
                        return UserSecurityRolesTransfer.PrivilegesEnum.READ;
                    }
                }
                return null;
            }

            ctrl.canEdit = () =>
            {
                return securityRolesStore.hasSecurityPrivileges(
                    SecurityObjectsTransfer.AccessObjectsEnum.ADMINSECURITY,
                    UserSecurityRolesTransfer.PrivilegesEnum.WRITE);
            }

            ctrl.onCancel = () =>
            {
                ctrl.modalInstance.dismiss("modal cancelled");
            }
        }]
});