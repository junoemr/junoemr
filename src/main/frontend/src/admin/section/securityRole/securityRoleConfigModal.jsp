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
<juno-modal class="security-role-config-modal" component-style="$ctrl.resolve.style">

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title>
		<h3>Manage {{$ctrl.role.name}} role</h3>
	</modal-title>

	<modal-body>
		<div class="overflow-auto height-100">
			<div class="role-details">
				<h3>Role Details</h3>

				<juno-input ng-model="$ctrl.role.name"
				            label="Role Name"
				            disabled="!$ctrl.canEdit()">
				</juno-input>
				<juno-input ng-model="$ctrl.role.description"
				            label="Role Description"
				            disabled="!$ctrl.canEdit()">
				</juno-input>
			</div>
			<div class="role-access">
				<h3>Role Permissions</h3>

				<ul class="list-group">
					<li ng-repeat="access in $ctrl.accessList" class="list-group-item">
						<div class="flex-row">
							<juno-select
									label="{{access.name}}"
									ng-model="access.permissionLevel"
									options="$ctrl.permissionLevelOptions"
									class="access-selection"
									disabled="!$ctrl.canEdit()">
							</juno-select>
							<div class="access-description flex-column flex-grow justify-content-center">
								<span>{{access.description}}</span>
							</div>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</modal-body>
	<modal-footer>
		<div class="row footer-wrapper">
			<div class="col-md-6">
				<div class="button-group-wrapper pull-left">
					<div class="button-wrapper">
						<juno-button component-style="$ctrl.resolve.style"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						             click="$ctrl.onDelete()"
						             disabled="$ctrl.newRole || !$ctrl.canDelete()">
							Delete
						</juno-button>
					</div>
				</div>
			</div>
			<div class="col-md-6">
				<div class="button-group-wrapper">
					<div class="button-wrapper">
						<juno-button component-style="$ctrl.resolve.style"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
						             click="$ctrl.onCancel()">
							Cancel
						</juno-button>
					</div>
					<div class="button-wrapper">
						<juno-button ng-if="$ctrl.newRole"
						             component-style="$ctrl.resolve.style"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						             click="$ctrl.onCreate()"
						             disabled="!$ctrl.canSave()">
							Add Role
						</juno-button>
						<juno-button ng-if="!$ctrl.newRole"
						             component-style="$ctrl.resolve.style"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						             click="$ctrl.onUpdate()"
						             disabled="!$ctrl.canSave()">
							Save Role
						</juno-button>
					</div>
				</div>
			</div>
		</div>
	</modal-footer>
</juno-modal>
