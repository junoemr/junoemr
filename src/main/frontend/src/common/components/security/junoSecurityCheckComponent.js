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

angular.module('Common.Components').component('junoSecurityCheck',
{
    templateUrl: 'src/common/components/security/junoSecurityCheck.jsp',
    bindings: {
        permissions: '<',
        showPlaceholder: '<?'
    },
    transclude: true,
    controller: [
        '$scope',
        'securityRolesService',
        function ($scope, securityRolesService)
    {
        let ctrl = this;
        ctrl.hasAccess = false;

        ctrl.$onInit = () =>
        {
            ctrl.access = ctrl.access || null;
            ctrl.permissions = ctrl.permissions || [];

            // allow single element or array as parameter
            if(!Array.isArray(ctrl.permissions))
            {
                ctrl.permissions = [ctrl.permissions];
            }

            ctrl.hasAccess = securityRolesService.hasSecurityPrivileges(...ctrl.permissions);
        }

        ctrl.hasPermissions = () =>
        {
            return ctrl.hasAccess;
        }

    }]
});