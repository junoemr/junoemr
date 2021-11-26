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
	LABEL_POSITION,
} from "../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../common/security/securityConstants";
import HrmCategory from "../../../lib/integration/hrm/model/HRMCategory";
import HrmService from "../../../lib/integration/hrm/service/HrmService";

angular.module('Admin.Section').component('hrmCategoryDetailsModal',
	{

		templateUrl: 'src/admin/section/hrm/categoryDetailsModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'$uibModal',
			'securityRolesService',
			'NgTableParams',
			function (
				$uibModal,
				securityRolesService,
				NgTableParams)
			{
				const ctrl = this;
				const hrmService = new HrmService();

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.category = null;
				ctrl.title = "";
				ctrl.isCreate = false;
				ctrl.isLoading = true;

				ctrl.$onInit = async () =>
				{
					ctrl.tableParams = new NgTableParams({
						page: 1,
						count: -1,
						sorting: {
							facilityNumber: 'asc',
						}
					});


					ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
					ctrl.category = ctrl.resolve.category;

					if(!Juno.Common.Util.exists(ctrl.category))
					{
						ctrl.isCreate = true;
						ctrl.title = "Create a New HRM Category";
						ctrl.category = new HrmCategory();
					}
					else
					{
						ctrl.title = `Edit an Existing HRM Category (${ctrl.category.name})`;
					}

					ctrl.isLoading = false;
				}

				ctrl.canCreate = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmCreate);
				}

				ctrl.canUpdate = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmUpdate);
				}

				ctrl.canDelete = () =>
				{
					return !ctrl.isLoading && securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmDelete);
				}

				ctrl.canSave = () =>
				{
					return !ctrl.isLoading &&
						ctrl.canEdit() &&
						Juno.Common.Util.exists(ctrl.category) &&
						!Juno.Common.Util.isBlank(ctrl.category.name);
				}

				ctrl.onCancel = () =>
				{
					ctrl.modalInstance.dismiss("cancelled");
				}

				ctrl.errorFunction = (error, message) =>
				{
					console.error(error);
					Juno.Common.Util.errorAlert($uibModal, "Error", message);
				}

				ctrl.onUpdate = async () =>
				{
					ctrl.isLoading = true;
					try
					{
						await hrmService.updateCategory(ctrl.category);
						ctrl.modalInstance.close();
					}
					catch (err)
					{
						if (err.status === 400)
						{
							ctrl.errorFunction(err, "A category with that name already exists");
						}
						else
						{
							ctrl.errorFunction(err, "Could not update category at this time");
						}
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.onCreate = async () =>
				{
					ctrl.isLoading = true;
					try
					{
						await hrmService.createCategory(ctrl.category);
						ctrl.modalInstance.close();
					}
					catch (err)
					{
						if (err.status === 400)
						{
							ctrl.errorFunction(err, "A category with that name already exists");
						}
						else
						{
							ctrl.errorFunction(err, "Could not create category at this time");
						}
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.onDelete = async () =>
				{
					ctrl.isLoading = true;
					try
					{
						await hrmService.deactivateCategory(ctrl.category);
						ctrl.modalInstance.close();
					}
					catch (err)
					{
						ctrl.errorFunction(err, "Could not delete category at this time");
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}
			}]
	});