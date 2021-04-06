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

            ctrl.securityRolesApi = new SecurityRolesApi($http, $httpParamSerializer, '../ws/rs');

            ctrl.rolesList = [];

            ctrl.$onInit = async () =>
            {
                ctrl.rolesList = (await ctrl.securityRolesApi.getRoles()).data.body;
            }

            ctrl.onRoleDetails = (role) =>
            {
                $uibModal.open(
                    {
                        component: 'securityRoleConfigModal',
                        backdrop: 'static',
                        windowClass: "juno-modal lg",
                        resolve: {
                            role: role,
                        }
                    }
                ).result.then((data) =>
                {
                    // force the cached access/permissions values to reload
                    securityRolesStore.loadUserRoles();
                }).catch((reason) =>
                {
                    // do nothing on cancel
                });

            }
        }]
});