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
			<!-- User Information -->
			<panel id="edit-provider-user-information">
				<panel-header>
					<h6>User Information</h6>
				</panel-header>
				<panel-body>
					<!-- Last Name -->
					<ca-field-text
									ca-name="lastName"
									ca-title="Last Name"
									ca-model="$ctrl.provider.lastName"
									ca-rows="1"
									ca-text-placeholder="Last Name"
									ca-required-field="true"
					>
					</ca-field-text>
					<!-- First Name -->
					<ca-field-text
									ca-name="firstName"
									ca-title="First Name"
									ca-model="$ctrl.provider.firstName"
									ca-rows="1"
									ca-text-placeholder="First Name"
									ca-required-field="true"
					>
					</ca-field-text>
					<!-- Type -->
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="type"
									ca-title="Type"
									ca-model="$ctrl.provider.type"
									ca-options="TODO"
									ca-text-placeholder="Provider Type"
									ca-empty-option="true"
									ca-required-field="true"
					>
					</ca-field-select>
					<!-- Speciality -->
					<ca-field-text
									ca-name="speciality"
									ca-title="Speciality"
									ca-model="$ctrl.provider.speciality"
									ca-rows="1"
									ca-text-placeholder="Speciality"
					>
					</ca-field-text>
					<!-- Team -->
					<ca-field-text
									ca-name="team"
									ca-title="Team"
									ca-model="$ctrl.provider.team"
									ca-rows="1"
									ca-text-placeholder="Team"
					>
					</ca-field-text>
					<!-- Sex -->
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="sex"
									ca-title="Sex"
									ca-model="$ctrl.provider.sex"
									ca-options="$ctrl.sexes"
									ca-text-placeholder="Sex"
									ca-empty-option="true"
					>
					</ca-field-select>
					<!-- DOB -->
					<ca-field-date
									ca-title="Date of Birth"
									ca-date-picker-id="dob"
									ca-name="dob"
									ca-model="$ctrl.provider.dateOfBirth"
									ca-orientation="auto"
					>
					</ca-field-date>
				</panel-body>
			</panel>
			<panel id="edit-provider-access-roles">
				<panel-header>
					<h6>Access Roles</h6>
				</panel-header>
				<panel-body>
					<div class="flex-row flex-wrap">
						<juno-typeahead
										class="flex-grow lg-margin-right"
										model="$ctrl.currentRoleSelection"
										options="$ctrl.roleOptions"
										placeholder="'Search Roles...'"
						>
						</juno-typeahead>
						<button class="btn btn-success" ng-click="$ctrl.addUserRole($ctrl.currentRoleSelection)">Add</button>
					</div>
					<div class="col-sm-12 md-margin-top">
						<ul class="no-padding">
							<li ng-repeat="userRole in $ctrl.provider.userRoles" class="group-list-item">
								{{userRole}}
							</li>
						</ul>
					</div>
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
			<!-- Login Information -->
			<panel id="edit-provider-login-information">
				<panel-header>
					<h6>Login Information</h6>
				</panel-header>
				<panel-body>
					<div class="edit-provider-or-group">
						<!-- Email -->
						<ca-field-text
										ca-name="email"
										ca-title="Email"
										ca-model="$ctrl.provider.email"
										ca-rows="1"
										ca-text-placeholder="Email"
						>
						</ca-field-text>
						<!-- User Name -->
						<ca-field-text
										ca-name="username"
										ca-title="User Name"
										ca-model="$ctrl.provider.userName"
										ca-rows="1"
										ca-text-placeholder="User Name"
						>
						</ca-field-text>
					</div>
					<!-- Password -->
					<ca-field-text
									ca-name="password"
									ca-title="Password"
									ca-model="$ctrl.provider.password"
									ca-rows="1"
									ca-text-placeholder="Password"
									ca-hide-input="true"
									ca-required-field="true"
					>
					</ca-field-text>
					<!-- Confirm Password -->
					<ca-field-text
									ca-name="confirm_password"
									ca-title="Confirm Password"
									ca-model="$ctrl.provider.passwordVerify"
									ca-rows="1"
									ca-text-placeholder="Retype Password"
									ca-hide-input="true"
									ca-required-field="true"
					>
					</ca-field-text>
					<!-- Second Level Passcode -->
					<ca-field-text
									ca-name="passcode"
									ca-title="Second Level Passcode"
									ca-model="$ctrl.provider.secondLevelPasscode"
									ca-rows="1"
									ca-text-placeholder="Passcode"
									ca-hide-input="true"
									ca-required-field="true"
					>
					</ca-field-text>
					<!-- Confirm Second Level Passcode -->
					<ca-field-text
									ca-name="confirm_passcode"
									ca-title="Retype Second Level Passcode"
									ca-model="$ctrl.provider.secondLevelPasscodeVerify"
									ca-rows="1"
									ca-text-placeholder="Passcode"
									ca-hide-input="true"
									ca-required-field="true"
					>
					</ca-field-text>


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