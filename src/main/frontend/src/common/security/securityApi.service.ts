'use strict';

/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

 */
import {ProviderApi, ProvidersServiceApi, SecurityRolesApi} from "../../../generated";

angular.module("Common.Security").service("securityApiService", [
    '$http',
    '$httpParamSerializer',
    function(
        $http,
        $httpParamSerializer)
    {
        const service = this;
        service.providerApi = new ProviderApi($http, $httpParamSerializer, '../ws/rs');
        service.providersApi = new ProvidersServiceApi($http, $httpParamSerializer, '../ws/rs');
        service.securityRoleApi = new SecurityRolesApi($http, $httpParamSerializer, '../ws/rs');

        service.getCurrentUserSecurityRoles = async (): Promise<any> =>
        {
            return (await service.providerApi.getCurrentUserSecurityRoles()).data.body;
        }

        service.getRoles = async (): Promise<any> =>
        {
            return (await service.securityRoleApi.getRoles()).data.body;
        }

        service.getRole = async (roleId): Promise<any> =>
        {
            return (await service.securityRoleApi.getRole(roleId)).data.body;
        }

        service.addRole = async (transfer): Promise<any> =>
        {
            return (await service.securityRoleApi.addRole(transfer)).data.body;
        }

        service.updateRole = async (roleId, transfer): Promise<any> =>
        {
            return (await service.securityRoleApi.updateRole(roleId, transfer)).data.body;
        }

        service.deleteRole = async (roleId): Promise<any> =>
        {
            return (await service.securityRoleApi.deleteRole(roleId)).data.body;
        }

        service.getAllPermissions = async (): Promise<any> =>
        {
            return (await service.securityRoleApi.getAllPermissions()).data.body;
        }

        service.getProviderSecurityDemographicSetsBlacklist = async (providerId: string): Promise<any> =>
        {
            return (await service.providerApi.getProviderSecurityDemographicSetsBlacklist(providerId)).data.body;
        }

        service.setProviderSecurityDemographicSetsBlacklist = async (providerId: string, data: any): Promise<any> =>
        {
            return (await service.providerApi.setProviderSecurityDemographicSetsBlacklist(providerId, data)).data.body;
        }

        service.canCurrentUserAccessDemographic = async (demographicId: number): Promise<any> =>
        {
            return (await service.providerApi.canCurrentUserAccessDemographic(demographicId)).data.body;
        }

        service.getAllProviders = async (): Promise<any> =>
        {
            return (await service.providersApi.getAll()).data.body;
        }
    }
]);