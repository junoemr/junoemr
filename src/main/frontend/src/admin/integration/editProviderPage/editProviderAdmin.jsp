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
<div id="edit-provider-admin">
	<h1 ng-if="$ctrl.mode === $ctrl.modes.EDIT">Edit User</h1>
	<h1 ng-if="$ctrl.mode === $ctrl.modes.ADD">Add User</h1>

	<div class="edit-provider-fields">
		<div class="col-sm-6">
			<panel id="edit-provider-user-information">
				<panel-header>
					<h6>User Information</h6>
				</panel-header>
				<panel-body>
					<ca-field-text
									ca-name="lastName"
									ca-title="Last Name"
									ca-model="$ctrl.provider.last_name"
									ca-rows="1"
									ca-text-placeholder="Last Name"
									ca-required-field="true"
					>
					</ca-field-text>
					<ca-field-text
									ca-name="firstName"
									ca-title="First Name"
									ca-model="$ctrl.provider.first_name"
									ca-rows="1"
									ca-text-placeholder="First Name"
									ca-required-field="true"
					>
					</ca-field-text>
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="type"
									ca-title="Type"
									ca-model="$ctrl.provider.type"
									ca-options="TODO"
									ca-text-placeholder="Provider Type"
									ca-empty-option="true"
					>
					</ca-field-select>
				</panel-body>
			</panel>
			<panel id="edit-provider-access-roles">
				<panel-header>
					<h6>Access Roles</h6>
				</panel-header>
				<panel-body>

				</panel-body>
			</panel>
			<panel id="edit-provider-billing-information">
				<panel-header>
					<h6>Billing Information</h6>
				</panel-header>
				<panel-body>

				</panel-body>
			</panel>
		</div>
		<div class="col-sm-6">
			<panel id="edit-provider-login-information">
				<panel-header>
					<h6>Login Information</h6>
				</panel-header>
				<panel-body>

				</panel-body>
			</panel>
			<panel id="edit-provider-contact-information">
				<panel-header>
					<h6>Contact Information</h6>
				</panel-header>
				<panel-body>

				</panel-body>
			</panel>
		</div>
	</div>
</div>