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
import HrmSubClass, {HrmReportClass} from "../../../lib/integration/hrm/model/HrmSubClass";

angular.module('Admin.Section').component('hrmCategoryDetailsModal',
	{

		templateUrl: 'src/admin/section/hrm/categoryDetailsModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'$scope',
			'$uibModal',
			'securityRolesService',
			'NgTableParams',
			function (
				$scope,
				$uibModal,
				securityRolesService,
				NgTableParams)
			{
				const ctrl = this;
				const hrmService = new HrmService();

				ctrl.COMPONENT_STYLE = JUNO_STYLE.DEFAULT;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				ctrl.LABEL_POSITION = LABEL_POSITION;

				ctrl.category = null;

				ctrl.newSubClass = null;
				ctrl.hrmReportClassOptions = Object.keys(HrmReportClass).map(reportClass =>
				{
					return {
						label: HrmReportClass[reportClass],
						value: HrmReportClass[reportClass]
					}
				})

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

					if(!ctrl.category)
					{
						ctrl.isCreate = true;
						ctrl.title = "Create a New HRM Category";
						ctrl.category = new HrmCategory();
					}
					else
					{
						ctrl.title = `Edit an Existing HRM Category (${ctrl.category.name})`;
					}

					ctrl.newSubClass = new HrmSubClass();
					ctrl.isLoading = false;
				}

				ctrl.canCreate = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmCreate) && ctrl.category.name;
				}

				ctrl.canUpdate = () =>
				{
					return securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmUpdate) && ctrl.category.name;
				}

				ctrl.canDelete = () =>
				{
					return !ctrl.isLoading && securityRolesService.hasSecurityPrivileges(SecurityPermissions.HrmDelete);
				}

				ctrl.canSave = () =>
				{
					return !ctrl.isLoading && ctrl.canEdit();
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

				ctrl.onUpdateCategory = async () =>
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

				ctrl.onCreateCategory = async () =>
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

				ctrl.onDeleteCategory = async () =>
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

				ctrl.isEligibleForSubClass = () =>
				{
					return this.newSubClass.reportClass === HrmReportClass.MEDICAL_RECORDS;
				}

				ctrl.isEligibleForAccompanyingSubClass = () =>
				{
					return (this.newSubClass.reportClass === HrmReportClass.CARDIO_RESPIRATORY) ||
						(this.newSubClass.reportClass === HrmReportClass.DIAGNOSTIC_IMAGING);
				}

				ctrl.isSubClassComplete = () =>
				{
					let subClass = ctrl.newSubClass;
					return subClass.isCompleteMapping()
				}

				ctrl.onReportClassChange = (newValue) =>
				{
					// Model won't be updated yet, need to use the param returned by the component
					if (newValue === HrmReportClass.MEDICAL_RECORDS)
					{
						ctrl.newSubClass.accompanyingSubClassName = null;
					}
					else
					{
						ctrl.newSubClass.subClassName = null;
					}
				}

				ctrl.onCreateSubClass = async () =>
				{
					const newSubClass = ctrl.newSubClass;

					const alreadyOnThisCategory = ctrl.category.subClasses
						.filter(existing => existing.equals(newSubClass))
						.length > 0

					if (alreadyOnThisCategory)
					{
						Juno.Common.Util.errorAlert($uibModal, "In Use", "That subclass is already assigned to this category");
						return;
					}

					const alreadyOnAnotherCategory = await hrmService.findSubClassByAttributes(
						newSubClass.facilityNumber,
						newSubClass.reportClass,
						newSubClass.subClassName,
						newSubClass.accompanyingSubClassName);

					if (alreadyOnAnotherCategory != null)
					{
						const hrmCategory = await hrmService.getActiveCategory(alreadyOnAnotherCategory.hrmCategoryId);
						Juno.Common.Util.errorAlert($uibModal, "In Use", "That subclass is already assigned to " + hrmCategory.name);
						return;
					}

					// Subclasses are reconciled by the backend after the save button is pressed.
					ctrl.category.subClasses.push(ctrl.newSubClass);
					ctrl.newSubClass = new HrmSubClass();
					$scope.$apply();
				}

				ctrl.onDeleteSubClass = async (subClass) =>
				{
					// Subclasses are reconciled by the backend after the save button is pressed.
					const index = ctrl.category.subClasses.indexOf(subClass);
					if (index >= 0)
					{
						ctrl.category.subClasses.splice(index, 1);
					}
				}
			}]
	});