<%--
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
--%>
<juno-modal id="hrm-category-details-modal" component-style="$ctrl.resolve.style">
	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title>
		<h3>{{$ctrl.title}}</h3>
	</modal-title>

	<modal-body>
		<div class="overflow-auto height-100 flex-column p-16">
			<div class="role-details">
				<juno-input ng-model="$ctrl.category.name"
							label="Name">
				</juno-input>
			</div>
			<div class="category-subclasses overflow-auto flex-column m-t-16">
				<table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
					<tbody>
					<tr ng-repeat="subclass in $ctrl.category.subClasses">
						<td data-title="'Facility Number'">
							{{ subclass.facilityNumber }}
						</td>
						<td data-title="'Report Type'">
							{{ subclass.reportClass }}
						</td>
						<td data-title="'Subclass'">
							{{ subclass.subClassName }}
						</td>
						<td data-title="'Accompanying Subclass'">
							{{ subclass.accompanyingSubClassName }}
						</td>
						<td>
							<juno-button component-style="$ctrl.componentStyle"
										 button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
										 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
										 click="$ctrl.onDeleteSubClass(subclass)"
										 disabled="!$ctrl.canDelete()">
								X
							</juno-button>
						</td>
					</tr>
					<tr>
						<td>
							<juno-input
									placeholder="Facility Number"
									ng-model = "$ctrl.newSubClass.facilityNumber"
									invalid = "!$ctrl.newSubClass.facilityNumber"
							>
							</juno-input>
						</td>
						<td>
							<juno-select
									options = "$ctrl.hrmReportClassOptions"
									ng-model = "$ctrl.newSubClass.reportClass"
									placeholder = "Report Type"
									invalid = "!$ctrl.newSubClass.reportClass"
									on-change = "$ctrl.onReportClassChange(value)"
							>
							</juno-select>
						</td>
						<td>
							<juno-input
									placeholder="Subclass"
									ng-model = "$ctrl.newSubClass.subClassName"
									disabled = "!$ctrl.isEligibleForSubClass()"
									invalid ="$ctrl.isEligibleForSubClass() && !$ctrl.newSubClass.subClassName"
							>
							</juno-input>
						</td>
						<td>
							<juno-input
									placeholder="Accompanying Subclass"
									ng-model = "$ctrl.newSubClass.accompanyingSubClassName"
									disabled = "!$ctrl.isEligibleForAccompanyingSubClass()"
									invalid = "$ctrl.isEligibleForAccompanyingSubClass() && !$ctrl.newSubClass.accompanyingSubClassName"
							>
							</juno-input>
						</td>
						<td>
							<juno-button component-style="$ctrl.componentStyle"
										 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
										 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
										 click="$ctrl.onCreateSubClass()"
										 disabled="!$ctrl.canCreate() || !$ctrl.isSubClassComplete()">
								+
							</juno-button>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</div>
	</modal-body>
	<modal-footer>
		<div class="d-flex justify-content-between align-items-center h-100">
			<juno-button class="w-128 m-l-16 flex-grow-0"
						 ng-if="!$ctrl.isCreate"
						 component-style="$ctrl.resolve.style"
						 button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
						 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						 disabled="!$ctrl.canDelete()"
						 click="$ctrl.onDeleteCategory()">
				Delete
			</juno-button>
			<div class="button-group d-flex m-r-16">
				<juno-button class="w-128 m-r-8 flex-grow-0"
							 component-style="$ctrl.resolve.style"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
							 click="$ctrl.onCancel()">
					Cancel
				</juno-button>
				<juno-button ng-if="$ctrl.isCreate"
							 class="w-128 flex-grow-0"
							 component-style="$ctrl.resolve.style"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onCreateCategory()"
							 disabled="!$ctrl.canCreate()">
					Add Category
				</juno-button>
				<juno-button ng-if="!$ctrl.isCreate"
							 class="w-128 flex-grow-0"
							 component-style="$ctrl.resolve.style"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onUpdateCategory()"
							 disabled="!$ctrl.canUpdate()">
					Save Category
				</juno-button>
			</div>
		</div>
	</modal-footer>
</juno-modal>