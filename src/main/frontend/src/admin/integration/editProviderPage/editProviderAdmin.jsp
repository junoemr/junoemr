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
									ca-options="$ctrl.providerTypes"
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
			<!-- Access Roles -->
			<panel id="edit-provider-access-roles">
				<panel-header>
					<h6>Access Roles</h6>
				</panel-header>
				<panel-body>
					<div class="flex-row flex-wrap">
						<juno-typeahead
										name="access_roles"
										class="flex-grow lg-margin-right"
										model="$ctrl.currentRoleSelection"
										options="$ctrl.roleOptions"
										placeholder="Add Role Here"
										on-enter-key="$ctrl.addUserRole($ctrl.currentRoleSelection.value)"
						>
						</juno-typeahead>
						<button class="btn btn-primary lg-padding-left lg-padding-right add-role-button" title="Add role" ng-click="$ctrl.addUserRole($ctrl.currentRoleSelection.value)">Add</button>
					</div>
					<div class="user-role-list col-sm-12 md-margin-top">
						<label class="body-smallest md-padding-bottom">Roles</label>
						<ul class="no-padding">
							<li ng-repeat="userRoleId in $ctrl.provider.userRoles" class="group-list-item">
								<div class="flex-row align-items-center body-small-bold">
									{{$ctrl.getUserRoleName(userRoleId)}}
									<div class="flex-grow text-right">
										<i class="icon icon-delete hand-hover" title="Remove role" ng-click="$ctrl.removeUserRole(userRoleId)"></i>
									</div>
								</div>
								<hr>
							</li>
						</ul>
					</div>
				</panel-body>
			</panel>
			<!-- Billing -->
			<panel id="edit-provider-billing-information">
				<panel-header>
					<h6>Billing Information</h6>
				</panel-header>
				<panel-body>
					<!-- Billing Region Override -->
					<juno-typeahead
									name="billing_region_override"
									title="Billing Region"
									class="flex-grow lg-margin-right"
									model="$ctrl.billingRegion"
									options="$ctrl.billingRegionOptions"
									enabled="$ctrl.billingRegionSelectEnabled">
					</juno-typeahead>

					<!-- BC Billing options -->
					<div ng-if="$ctrl.billingRegion.value === 'BC'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoBC"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- Payee number -->
						<ca-field-text
										ca-name="payeeNumber"
										ca-title="Payee Number"
										ca-model="$ctrl.provider.billingNo"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- rural Retention code -->
						<ca-field-text
										ca-name="ruralRetentionCode"
										ca-title="Rural Retention Code"
										ca-model="$ctrl.provider.ruralRetentionCode"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- service location -->
						<ca-field-text
										ca-name="serviceLocation"
										ca-title="Service Location"
										ca-model="$ctrl.provider.serviceLocation"
										ca-rows="1"
						>
						</ca-field-text>
					</div>

					<!-- ON billing -->
					<div ng-if="$ctrl.billingRegion.value === 'ON'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoON"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- Group Number -->
						<ca-field-text
										ca-name="groupNumber"
										ca-title="Group Number"
										ca-model="$ctrl.provider.groupNumber"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- Speciality Code -->
						<ca-field-text
										ca-name="specialityCode"
										ca-title="Speciality Code"
										ca-model="$ctrl.provider.specialityCode"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- visit Location -->
						<ca-field-text
										ca-name="visitLocation"
										ca-title="Visit Location"
										ca-model="$ctrl.provider.visitLocation"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- service location indicator -->
						<ca-field-text
										ca-name="serviceLocationIndicator"
										ca-title="Service Location Indicator"
										ca-model="$ctrl.provider.serviceLocationIndicator"
										ca-rows="1"
						>
						</ca-field-text>
					</div>

					<!-- AB billing -->
					<div ng-if="$ctrl.billingRegion.value === 'AB'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoAB"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- Source Code -->
						<ca-field-text
										class="col-sm-2 no-padding no-float"
										ca-name="sourceCode"
										ca-title="Source Code"
										ca-model="$ctrl.provider.sourceCode"
										ca-rows="1"
										ca-text-length="2"
						>
						</ca-field-text>

						<!-- Skill Code -->
						<juno-typeahead
										title="Skill Code"
										name="albertaSkillCode"
										model="$ctrl.provider.skillCode"
										options="$ctrl.skillCodeOptions"
										placeholder="Provider Skill Code"
						>
						</juno-typeahead>

						<!-- Location Code -->
						<juno-typeahead
										title="Location Code"
										name="locationCode"
										model="$ctrl.provider.locationCode"
										options="$ctrl.locationCodeOptions"
										placeholder="Location Code"
						>
						</juno-typeahead>

						<!-- BA Number -->
						<ca-field-text
										class="col-sm-2 no-padding no-float"
										ca-name="BANumber"
										ca-title="BA Number"
										ca-model="$ctrl.provider.BANumber"
										ca-rows="1"
						>
						</ca-field-text>

						<!-- Facility Number -->
						<juno-typeahead
										title="Facility Number"
										name="albertaFacilityNumber"
										model="$ctrl.provider.facilityNumber"
										options="$ctrl.albertaFacilityOptions"
										placeholder="Search..."
										typeahead-min-length="3"
						>
						</juno-typeahead>

						<!-- Functional Centers -->
						<juno-typeahead
										title="Functional"
										name="albertaFunctionalCenter"
										model="$ctrl.provider.functionalCenter"
										options="$ctrl.albertaFunctionalCenterOptions"
										placeholder="Search..."
						>
						</juno-typeahead>

						<!-- Default Time / Role  Modifier -->
						<juno-typeahead
										title="Default Time / Role Modifier"
										name="albertaRoleModifier"
										model="$ctrl.provider.roleModifier"
										options="$ctrl.albertaDefaultTimeRoleOptions"
										placeholder="Search..."
										typeahead-min-length="0"
						>
						</juno-typeahead>

					</div>

					<!-- common billing options -->
					<!-- 3rd Party Billing No -->
					<ca-field-text
									ca-name="thirdPartyBillingNo"
									ca-title="3'rd Party Billing #"
									ca-model="$ctrl.provider.thirdPartyBillingNo"
									ca-rows="1"
									ca-text-placeholder=""
					>
					</ca-field-text>

					<!-- Alternate Billing No -->
					<ca-field-text
									ca-name="alternateBillingNo"
									ca-title="Alternate Billing #"
									ca-model="$ctrl.provider.alternateBillingNo"
									ca-rows="1"
									ca-text-placeholder=""
					>
					</ca-field-text>


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
									ca-text-placeholder="Retype Passcode"
									ca-hide-input="true"
									ca-required-field="true"
					>
					</ca-field-text>


				</panel-body>
			</panel>
			<!-- Contact Information -->
			<panel id="edit-provider-contact-information">
				<panel-header>
					<h6>Contact Information</h6>
				</panel-header>
				<panel-body>
					<!-- Address -->
					<ca-field-text
									ca-name="address"
									ca-title="Address"
									ca-model="$ctrl.provider.address"
									ca-rows="1"
									ca-text-placeholder="31 Bastion Square"
					>
					</ca-field-text>
					<!-- Home phone -->
					<ca-field-text
									ca-name="homePhone"
									ca-title="Home Phone"
									ca-model="$ctrl.provider.homePhone"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- Work phone -->
					<ca-field-text
									ca-name="workPhone"
									ca-title="Work Phone"
									ca-model="$ctrl.provider.workPhone"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- Cell phone -->
					<ca-field-text
									ca-name="cellPhone"
									ca-title="Cell Phone"
									ca-model="$ctrl.provider.cellPhone"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- Other phone -->
					<ca-field-text
									ca-name="otherPhone"
									ca-title="Other Phone"
									ca-model="$ctrl.provider.otherPhone"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- Fax -->
					<ca-field-text
									ca-name="fax"
									ca-title="Fax"
									ca-model="$ctrl.provider.fax"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- Email -->
					<ca-field-text
									ca-name="contactEmail"
									ca-title="Contact Email"
									ca-model="$ctrl.provider.contactEmail"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- pager -->
					<ca-field-text
									ca-name="pager"
									ca-title="Pager"
									ca-model="$ctrl.provider.pager"
									ca-rows="1"
					>
					</ca-field-text>
				</panel-body>
			</panel>
		</div>
	</div>
</div>