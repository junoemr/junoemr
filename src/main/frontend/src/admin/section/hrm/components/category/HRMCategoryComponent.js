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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE} from "../../../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../../../common/security/securityConstants";
import HrmService from "../../../../../lib/integration/hrm/service/HrmService";

angular.module('Admin.Section').component('hrmCategory',
	{
		templateUrl: 'src/admin/section/hrm/components/category/HRMCategory.jsp',
		bindings: {},
		controller: [
			'$scope',
			'$http',
			'$httpParamSerializer',
			'$state',
			'$uibModal',
			'NgTableParams',
			'securityRolesService',
			function (
				$scope,
				$http,
				$httpParamSerializer,
				$state,
				$uibModal,
				NgTableParams,
				securityRolesService)
			{
				let ctrl = this;
				const hrmService = new HrmService();

				ctrl.COMPONENT_STYLE = JUNO_STYLE.DEFAULT;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.categories = []
				
				ctrl.$onInit = async () => {
					ctrl.tableParams = new NgTableParams({
							page: 1,
							count: -1,
							sorting: {
								name: 'asc',
							}
						});

					try
					{
						const categories = await hrmService.getActiveCategories();
						if (categories)
						{
							ctrl.categories = categories;
						}
					}
					finally
					{
						$scope.$apply();
					}
				};

				ctrl.canCreate = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmCreate)
				}

				ctrl.canUpdate = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmUpdate)
				}

				ctrl.onCreateCategory = () =>
				{
					ctrl.openDetailsModal(null);
				};

				ctrl.onUpdateCategory = (category) =>
				{
					ctrl.openDetailsModal(category);
				}

				ctrl.openDetailsModal = (category) =>
				{
					$uibModal
					.open({
						component: 'hrmCategoryDetailsModal',
						backdrop: 'static',
						windowClass: "juno-modal lg",
						resolve: {
							category: angular.copy(category)
						}
					})
					.result
					.then(() =>
					{
						return hrmService.getActiveCategories();
					})
					.then((categories) =>
					{
						if (categories)
						{
							ctrl.categories = categories;
							console.log(ctrl.categories);
						}
					})
					.catch(() =>
					{
						// this is the cancel workflow, do nothing.
					});
				}
			}]
	});