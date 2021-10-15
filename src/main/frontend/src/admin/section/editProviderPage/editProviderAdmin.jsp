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
	<div class="edit-provider-fields" ng-class="{'fields-disabled': $ctrl.mode === $ctrl.modes.VIEW}">
		<div class="col-sm-6">
			<!-- User Information -->
			<panel id="edit-provider-user-information">
				<panel-header>
					<h6>User Information</h6>
				</panel-header>
				<panel-body>
					<!-- Last Name -->
					<div ng-class="{'field-error': !$ctrl.providerValidations.lastName() && $ctrl.hasSubmitted}">
						<ca-field-text
										ca-name="lastName"
										ca-title="Last Name"
										ca-model="$ctrl.provider.lastName"
										ca-rows="1"
										ca-text-placeholder="Last Name"
										ca-required-field="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>
					<!-- First Name -->
					<div ng-class="{'field-error': !$ctrl.providerValidations.firstName() && $ctrl.hasSubmitted}">
						<ca-field-text
										ca-name="firstName"
										ca-title="First Name"
										ca-model="$ctrl.provider.firstName"
										ca-rows="1"
										ca-text-placeholder="First Name"
										ca-required-field="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>
					<!-- Type -->
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="type"
									ca-title="Type"
									ca-model="$ctrl.provider.type"
									ca-options="$ctrl.providerTypes"
									ca-required-field="true"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-select>
					<!-- Speciality -->
					<ca-field-text
									ca-name="speciality"
									ca-title="Speciality"
									ca-model="$ctrl.provider.speciality"
									ca-rows="1"
									ca-text-placeholder="Speciality"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Team -->
					<ca-field-text
									ca-name="team"
									ca-title="Team"
									ca-model="$ctrl.provider.team"
									ca-rows="1"
									ca-text-placeholder="Team"
									ca-disabled="$ctrl.fieldsDisabled"
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
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-select>
					<!-- DOB -->
					<ca-field-date
									ca-title="Date of Birth"
									ca-date-picker-id="dob"
									ca-name="dob"
									ca-model="$ctrl.provider.dateOfBirth"
									ca-orientation="auto"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-date>

					<!-- Provider Status -->
					<ca-field-select
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="providerStatus"
									ca-title="Status"
									ca-model="$ctrl.provider.status"
									ca-options="$ctrl.providerStatusOptions"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-select>
				</panel-body>
			</panel>

			<!-- Site Assignment -->
			<panel id="edit-provider-site-assignment">
				<panel-header>
					<h6>Site Assignment</h6>
				</panel-header>
				<panel-body>
					<div class="flex-row flex-wrap align-items-baseline">
						<juno-typeahead
										name="siteSelection"
										class="flex-grow lg-margin-right"
										model="$ctrl.currentSiteSelection"
										options="$ctrl.siteOptions"
										placeholder="Search..."
										on-enter-key="$ctrl.addSiteAssignment($ctrl.currentSiteSelection.value)"
										disabled="$ctrl.fieldsDisabled"
										raw-output="true"
						>
						</juno-typeahead>
						<button class="btn btn-primary lg-padding-left lg-padding-right add-role-button" title="Add role" ng-click="$ctrl.addSiteAssignment($ctrl.currentSiteSelection.value)">Add</button>
					</div>
					<div class="user-role-list col-sm-12 md-margin-top">
						<label class="body-smallest md-padding-bottom">Sites</label>
						<ul class="no-padding">
							<li ng-repeat="siteId in $ctrl.provider.siteAssignments" class="group-list-item">
								<div class="flex-row align-items-center body-small-bold">
									{{$ctrl.getSiteName(siteId)}}
									<div class="flex-grow text-right">
										<i class="icon icon-delete hand-hover" title="Remove role" ng-click="$ctrl.removeSiteAssignment(siteId)"></i>
									</div>
								</div>
								<hr>
							</li>
						</ul>
					</div>
				</panel-body>
			</panel>

			<!-- Access Roles -->
			<panel id="edit-provider-access-roles">
				<panel-header>
					<h6>Access Roles</h6>
					<div class="error-message" ng-if="!$ctrl.providerValidations.userRoles()">
						* Provider must be assigned at least one role.
					</div>
				</panel-header>
				<panel-body>
					<div class="flex-row flex-wrap align-items-baseline">
						<juno-typeahead
										ng-class="{'field-error': !$ctrl.providerValidations.userRoles()}"
										name="access_roles"
										class="flex-grow lg-margin-right"
										model="$ctrl.currentRoleSelection"
										options="$ctrl.roleOptions"
										placeholder="Search..."
										on-enter-key="$ctrl.addUserRole($ctrl.currentRoleSelection.value)"
										disabled="$ctrl.fieldsDisabled"
										raw-output="true"
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
									ca-text-placeholder="123 Health St"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Home phone -->
					<ca-field-text
									ca-name="homePhone"
									ca-title="Home Phone"
									ca-model="$ctrl.provider.homePhone"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Work phone -->
					<ca-field-text
									ca-name="workPhone"
									ca-title="Work Phone"
									ca-model="$ctrl.provider.workPhone"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Cell phone -->
					<ca-field-text
									ca-name="cellPhone"
									ca-title="Cell Phone"
									ca-model="$ctrl.provider.cellPhone"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Other phone -->
					<ca-field-text
									ca-name="otherPhone"
									ca-title="Other Phone"
									ca-model="$ctrl.provider.otherPhone"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Booking Phones -->
					<div class="body-smallest error-message" ng-if="!$ctrl.providerValidations.bookingNotificationNumbers()">
						Phone number must only contain digits, dashes, parentheses, spaces and commas.
					</div>
					<div ng-class="{'field-error': !$ctrl.providerValidations.bookingNotificationNumbers()}">
						<ca-field-text
										ca-name="BookingNotificationPhones"
										ca-title="Booking Notification Phone(s)"
										ca-text-placeholder="(xxx) xxx-xxxx, (xxx) xxx-xxxx, ..."
										ca-model="$ctrl.provider.bookingNotificationNumbers"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>
					<!-- Fax -->
					<ca-field-text
									ca-name="fax"
									ca-title="Fax"
									ca-model="$ctrl.provider.fax"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- Email -->
					<ca-field-text
									ca-name="contactEmail"
									ca-title="Contact Email"
									ca-model="$ctrl.provider.contactEmail"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>
					<!-- pager -->
					<ca-field-text
									ca-name="pager"
									ca-title="Pager"
									ca-model="$ctrl.provider.pagerNumber"
									ca-rows="1"
									ca-disabled="$ctrl.fieldsDisabled"
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
					<ca-field-select
									ng-if="$ctrl.mode === $ctrl.modes.EDIT || $ctrl.mode === $ctrl.modes.VIEW"
									class="juno-modal no-padding"
									ca-template="label"
									ca-name="securityRecordSelect"
									ca-title="Select Security Record"
									ca-model="$ctrl.provider.currentSecurityRecord"
									ca-options="$ctrl.securityRecordOptions"
									ca-text-placeholder="Select Security Record to edit"
									ca-empty-option="true"
					>
					</ca-field-select>
					<div ng-repeat="securityRecord in $ctrl.provider.securityRecords">
						<div ng-if="securityRecord.securityNo === $ctrl.provider.currentSecurityRecord">
							<div class="edit-provider-or-group" ng-class="{'field-error': !securityRecord.validations.emailOrUserName() && $ctrl.hasSubmitted}">
								<!-- Email -->
								<ca-field-text
												ca-name="email"
												ca-title="Email"
												ca-model="securityRecord.email"
												ca-rows="1"
												ca-text-placeholder="Email"
												ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-text>
								<div class="error-message" ng-if="!securityRecord.validations.emailOrUserName() && $ctrl.hasSubmitted">
									Either a User Name or Email is required.
								</div>
								<!-- User Name -->
								<ca-field-text
												ca-name="username"
												ca-title="User Name"
												ca-model="securityRecord.userName"
												ca-rows="1"
												ca-text-placeholder="User Name"
												ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-text>
							</div>
							<!-- Password -->
							<div ng-class="{'field-error': (!securityRecord.validations.password() || !securityRecord.validations.passwordMatch()) && $ctrl.hasSubmitted}">
								<ca-field-password
												ca-name="password"
												ca-title="Password"
												ca-model="securityRecord.password"
												ca-rows="1"
												ca-text-placeholder="Password"
												ca-required-field="true"
												ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-password>
								<div class="body-smallest error-message" ng-if="!securityRecord.validations.password() && $ctrl.hasSubmitted">
									Password must be a minimum of 8 characters long and include at least one special character.
								</div>
							</div>
							<!-- Confirm Password -->
							<div ng-class="{'field-error': (!securityRecord.validations.passwordVerify() || !securityRecord.validations.passwordMatch()) && $ctrl.hasSubmitted}">
								<ca-field-password
												ca-name="confirm_password"
												ca-title="Confirm Password"
												ca-model="securityRecord.passwordVerify"
												ca-rows="1"
												ca-text-placeholder="Retype Password"
												ca-required-field="true"
												ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-password>
								<div class="body-smallest error-message" ng-if="!securityRecord.validations.passwordMatch() && $ctrl.hasSubmitted">
									Passwords do not match.
								</div>
							</div>
							<!-- Second Level Passcode -->
							<div ng-class="{'field-error': (!securityRecord.validations.secondLevelPasscode() || !securityRecord.validations.secondLevelPasscodeMatch()) && $ctrl.hasSubmitted}">
								<ca-field-password
												ca-name="passcode"
												ca-title="Second Level Passcode"
												ca-model="securityRecord.pin"
												ca-rows="1"
												ca-text-placeholder="Passcode"
												ca-required-field="true"
												ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-password>
							</div>
							<div class="body-smallest error-message" ng-if="!securityRecord.validations.secondLevelPasscode() && $ctrl.hasSubmitted">
								Second Level passcode must be a number.
							</div>
							<!-- Confirm Second Level Passcode -->
							<div ng-class="{'field-error': (!securityRecord.validations.secondLevelPasscodeVerify() || !securityRecord.validations.secondLevelPasscodeMatch()) && $ctrl.hasSubmitted}">
								<ca-field-password
												ca-name="confirm_passcode"
												ca-title="Retype Second Level Passcode"
												ca-model="securityRecord.pinVerify"
												ca-rows="1"
												ca-text-placeholder="Retype Passcode"
												ca-required-field="true"
												ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-password>
								<div class="body-smallest error-message" ng-if="!securityRecord.validations.secondLevelPasscodeMatch() && $ctrl.hasSubmitted">
									Passcodes do not match.
								</div>
							</div>
							<div class="grid-column-2 justify-content-start align-items-center" ng-class="{'field-error': (!securityRecord.validations.loginExpiry() && $ctrl.hasSubmitted) }">
								<div class="m-r-16">
									<juno-check-box
											ng-model="securityRecord.expirySet"
											label="Expires"
											label-position="LABEL_POSITION.TOP"
											disabled="$ctrl.fieldsDisabled"
									>
									</juno-check-box>
								</div>
								<ca-field-date
										ca-title="Expires On"
										ca-date-picker-id="login-expiry-date"
										ca-name="login-expiry-date"
										ca-model="securityRecord.expiryDate"
										ca-orientation="auto"
										ca-disabled="$ctrl.fieldsDisabled"
								>
								</ca-field-date>
							</div>
							<div class="grid-column-4 justify-content-start align-items-center">
								<juno-check-box
										ng-model="securityRecord.pinLockLocal"
										label="Local Pin Lock"
										label-position="LABEL_POSITION.TOP"
										disabled="$ctrl.fieldsDisabled"
								>
								</juno-check-box>
								<juno-check-box
										ng-model="securityRecord.pinLockRemote"
										label="Remote Pin Lock"
										label-position="LABEL_POSITION.TOP"
										disabled="$ctrl.fieldsDisabled"
								>
								</juno-check-box>
								<juno-check-box
										ng-model="securityRecord.forcePasswordReset"
										label="Force password reset"
										label-position="LABEL_POSITION.TOP"
										title="force the user to reset their password on next login"
										disabled="$ctrl.fieldsDisabled"
								>
								</juno-check-box>
							</div>

						</div>
					</div>
<%--					<div class="edit-provider-or-group" ng-class="{'field-error': !$ctrl.providerValidations.emailOrUserName() && $ctrl.hasSubmitted}">--%>
<%--						<!-- Email -->--%>
<%--						<ca-field-text--%>
<%--										ca-name="email"--%>
<%--										ca-title="Email"--%>
<%--										ca-model="$ctrl.provider.email"--%>
<%--										ca-rows="1"--%>
<%--										ca-text-placeholder="Email"--%>
<%--										ca-disabled="$ctrl.fieldsDisabled"--%>
<%--						>--%>
<%--						</ca-field-text>--%>
<%--						<div class="error-message" ng-if="!$ctrl.providerValidations.emailOrUserName() && $ctrl.hasSubmitted">--%>
<%--							Either a User Name or Email is required.--%>
<%--						</div>--%>
<%--						<!-- User Name -->--%>
<%--						<ca-field-text--%>
<%--										ca-name="username"--%>
<%--										ca-title="User Name"--%>
<%--										ca-model="$ctrl.provider.userName"--%>
<%--										ca-rows="1"--%>
<%--										ca-text-placeholder="User Name"--%>
<%--										ca-disabled="$ctrl.fieldsDisabled"--%>
<%--						>--%>
<%--						</ca-field-text>--%>
<%--					</div>--%>
<%--					<!-- Password -->--%>
<%--					<div ng-class="{'field-error': (!$ctrl.providerValidations.password() || !$ctrl.providerValidations.passwordMatch()) && $ctrl.hasSubmitted}">--%>
<%--						<ca-field-text--%>
<%--										ca-name="password"--%>
<%--										ca-title="Password"--%>
<%--										ca-model="$ctrl.provider.password"--%>
<%--										ca-rows="1"--%>
<%--										ca-text-placeholder="Password"--%>
<%--										ca-hide-input="true"--%>
<%--										ca-required-field="true"--%>
<%--										ca-disabled="$ctrl.fieldsDisabled"--%>
<%--						>--%>
<%--						</ca-field-text>--%>
<%--						<div class="body-smallest error-message" ng-if="!$ctrl.providerValidations.password() && $ctrl.hasSubmitted">--%>
<%--							Password must be atleast 8 characters long and include atleast one special character.--%>
<%--						</div>--%>
<%--					</div>--%>
<%--					<!-- Confirm Password -->--%>
<%--					<div ng-class="{'field-error': (!$ctrl.providerValidations.passwordVerify() || !$ctrl.providerValidations.passwordMatch()) && $ctrl.hasSubmitted}">--%>
<%--						<ca-field-text--%>
<%--										ca-name="confirm_password"--%>
<%--										ca-title="Confirm Password"--%>
<%--										ca-model="$ctrl.provider.passwordVerify"--%>
<%--										ca-rows="1"--%>
<%--										ca-text-placeholder="Retype Password"--%>
<%--										ca-hide-input="true"--%>
<%--										ca-required-field="true"--%>
<%--										ca-disabled="$ctrl.fieldsDisabled"--%>
<%--						>--%>
<%--						</ca-field-text>--%>
<%--						<div class="body-smallest error-message" ng-if="!$ctrl.providerValidations.passwordMatch() && $ctrl.hasSubmitted">--%>
<%--							Passwords do not match.--%>
<%--						</div>--%>
<%--					</div>--%>
<%--					<!-- Second Level Passcode -->--%>
<%--					<div ng-class="{'field-error': (!$ctrl.providerValidations.secondLevelPasscode() || !$ctrl.providerValidations.secondLevelPasscodeMatch()) && $ctrl.hasSubmitted}">--%>
<%--						<ca-field-text--%>
<%--										ca-name="passcode"--%>
<%--										ca-title="Second Level Passcode"--%>
<%--										ca-model="$ctrl.provider.secondLevelPasscode"--%>
<%--										ca-rows="1"--%>
<%--										ca-text-placeholder="Passcode"--%>
<%--										ca-hide-input="true"--%>
<%--										ca-required-field="true"--%>
<%--										ca-disabled="$ctrl.fieldsDisabled"--%>
<%--						>--%>
<%--						</ca-field-text>--%>
<%--					</div>--%>
<%--					<!-- Confirm Second Level Passcode -->--%>
<%--					<div ng-class="{'field-error': (!$ctrl.providerValidations.secondLevelPasscodeVerify() || !$ctrl.providerValidations.secondLevelPasscodeMatch()) && $ctrl.hasSubmitted}">--%>
<%--						<ca-field-text--%>
<%--										ca-name="confirm_passcode"--%>
<%--										ca-title="Retype Second Level Passcode"--%>
<%--										ca-model="$ctrl.provider.secondLevelPasscodeVerify"--%>
<%--										ca-rows="1"--%>
<%--										ca-text-placeholder="Retype Passcode"--%>
<%--										ca-hide-input="true"--%>
<%--										ca-required-field="true"--%>
<%--										ca-disabled="$ctrl.fieldsDisabled"--%>
<%--						>--%>
<%--						</ca-field-text>--%>
<%--						<div class="body-smallest error-message" ng-if="!$ctrl.providerValidations.secondLevelPasscodeMatch() && $ctrl.hasSubmitted">--%>
<%--							Passcodes do not match.--%>
<%--						</div>--%>
<%--					</div>--%>


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
									placeholder="Search..."
									disabled="$ctrl.fieldsDisabled || !$ctrl.billingRegionSelectEnabled"
					>
					</juno-typeahead>

					<!-- BC Billing options -->
					<div ng-if="$ctrl.billingRegion === 'BC'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoBC"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Payee number -->
						<ca-field-text
										ca-name="payeeNumber"
										ca-title="Payee Number"
										ca-model="$ctrl.provider.bcBillingNo"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- rural Retention code -->
						<juno-typeahead
										title="Rural Retention Code"
										name="ruralRetentionCode"
										model="$ctrl.provider.bcRuralRetentionCode"
										options="$ctrl.bcBillingLocationOptions"
										placeholder="Search..."
										disabled="$ctrl.fieldsDisabled"
										raw-output="true"
						>
						</juno-typeahead>

						<!-- service location -->
						<juno-select
							ng-model="$ctrl.provider.bcServiceLocation"
							options="$ctrl.bcServiceLocationOptions"
							label="Service Location Code"
							label-position="$ctrl.LABEL_POSITION.TOP"
							disabled="$ctrl.fieldsDisabled"
						>
						</juno-select>
					</div>

					<!-- ON billing -->
					<div ng-if="$ctrl.billingRegion === 'ON'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoON"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Group Number -->
						<ca-field-text
										ca-name="groupNumber"
										ca-title="Group Number"
										ca-model="$ctrl.provider.onGroupNumber"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Speciality Code -->
						<ca-field-text
										ca-name="specialityCode"
										ca-title="Speciality Code"
										ca-model="$ctrl.provider.onSpecialityCode"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- visit Location -->
						<juno-typeahead
										title="Visit Location"
										name="visitLocation"
										model="$ctrl.provider.onVisitLocation"
										options="$ctrl.onVisitLocationOptions"
										placeholder="Search..."
										typeahead-min-length="3"
										disabled="$ctrl.fieldsDisabled"
						>
						</juno-typeahead>

						<!-- service location indicator -->
						<ca-field-select
										class="juno-modal no-padding"
										ca-template="label"
										ca-name="serviceLocationIndicator"
										ca-title="Service Location Indicator"
										ca-model="$ctrl.provider.onServiceLocationIndicator"
										ca-options="$ctrl.onServiceLocationIndicatorOptions"
										ca-text-placeholder="Select Service Location Indicator"
										ca-empty-option="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-select>
					</div>

					<!-- AB billing -->
					<div ng-if="$ctrl.billingRegion === 'AB'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoAB"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Source Code -->
						<ca-field-text
										class="col-sm-2 no-padding no-float"
										ca-name="sourceCode"
										ca-title="Source Code"
										ca-model="$ctrl.provider.abSourceCode"
										ca-rows="1"
										ca-text-length="2"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Skill Code -->
						<juno-typeahead
										title="Skill Code"
										name="albertaSkillCode"
										model="$ctrl.provider.abSkillCode"
										options="$ctrl.skillCodeOptions"
										placeholder="Search..."
										disabled="$ctrl.fieldsDisabled"
						>
						</juno-typeahead>

						<!-- Location Code -->
						<juno-typeahead
										title="Location Code"
										name="locationCode"
										model="$ctrl.provider.abLocationCode"
										options="$ctrl.locationCodeOptions"
										placeholder="Search..."
										disabled="$ctrl.fieldsDisabled"
						>
						</juno-typeahead>

						<!-- BA Number -->
						<ca-field-text
										class="col-sm-2 no-padding no-float"
										ca-name="BANumber"
										ca-title="BA Number"
										ca-model="$ctrl.provider.abBANumber"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Facility Number -->
						<juno-typeahead
										title="Facility Number"
										name="albertaFacilityNumber"
										model="$ctrl.provider.abFacilityNumber"
										options="$ctrl.albertaFacilityOptions"
										placeholder="Search..."
										typeahead-min-length="4"
										disabled="$ctrl.fieldsDisabled"
						>
						</juno-typeahead>

						<!-- Functional Centers -->
						<juno-typeahead
										title="Functional"
										name="albertaFunctionalCenter"
										model="$ctrl.provider.abFunctionalCenter"
										options="$ctrl.albertaFunctionalCenterOptions"
										placeholder="Search..."
										disabled="$ctrl.fieldsDisabled"
						>
						</juno-typeahead>

						<!-- Default Time / Role  Modifier -->
						<juno-typeahead
										title="Default Time / Role Modifier"
										name="albertaRoleModifier"
										model="$ctrl.provider.abRoleModifier"
										options="$ctrl.albertaDefaultTimeRoleOptions"
										placeholder="Search..."
										typeahead-min-length="0"
										disabled="$ctrl.fieldsDisabled"
						>
						</juno-typeahead>

					</div>

					<div ng-if="$ctrl.billingRegion === 'SK'">
						<!-- OHIP number -->
						<ca-field-text
										ca-name="billingNoSK"
										ca-title="Billing Number"
										ca-model="$ctrl.provider.ohipNo"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>

						<!-- Billing Mode -->
						<ca-field-select
										class="juno-modal no-padding"
										ca-template="label"
										ca-name="SKBillingMode"
										ca-title="Mode"
										ca-model="$ctrl.provider.skMode"
										ca-options="$ctrl.saskatchewanBillingModeOptions"
										ca-text-placeholder="Select Billing Mode"
										ca-empty-option="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-select>

						<!-- Location Code -->
						<ca-field-select
										class="juno-modal no-padding"
										ca-template="label"
										ca-name="SKLocationCode"
										ca-title="Location"
										ca-model="$ctrl.provider.skLocationCode"
										ca-options="$ctrl.saskatchewanLocationCodeOptions"
										ca-text-placeholder="Select Location Code"
										ca-empty-option="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-select>

						<!-- Submission Type -->
						<ca-field-select
										class="juno-modal no-padding"
										ca-template="label"
										ca-name="SKSubmissionType"
										ca-title="Submission Type"
										ca-model="$ctrl.provider.skSubmissionType"
										ca-options="$ctrl.saskatchewanSubmissionTypeOptions"
										ca-text-placeholder="Select Submission Type"
										ca-empty-option="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-select>

						<!-- Corporation Indicators -->
						<ca-field-select
										class="juno-modal no-padding"
										ca-template="label"
										ca-name="SKCorporationIndicator"
										ca-title="Corporation Indicator"
										ca-model="$ctrl.provider.skCorporationIndicator"
										ca-options="$ctrl.saskatchewanCorporationIndicatorOptions"
										ca-text-placeholder="Select Corporation Indicator"
										ca-empty-option="true"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-select>

					</div>

					<!-- common billing options -->
					<!-- 3rd Party Billing No -->
					<ca-field-text
									ca-name="thirdPartyBillingNo"
									ca-title="3rd Party Billing #"
									ca-model="$ctrl.provider.thirdPartyBillingNo"
									ca-rows="1"
									ca-text-placeholder=""
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>

					<!-- Alternate Billing No -->
					<ca-field-text
									ca-name="alternateBillingNo"
									ca-title="Alternate Billing #"
									ca-model="$ctrl.provider.alternateBillingNo"
									ca-rows="1"
									ca-text-placeholder=""
									ca-disabled="$ctrl.fieldsDisabled"
					>
					</ca-field-text>


				</panel-body>
			</panel>

			<panel ng-if="$ctrl.billingRegion === 'BC'" id="edit-provider-bcp-sites">
				<panel-header>
					<h6>BCP Sites</h6>
				</panel-header>
				<panel-body>
					<div class="flex-row flex-wrap align-items-baseline">
						<juno-typeahead
										name="bcp-site-selection"
										class="flex-grow lg-margin-right"
										model="$ctrl.currentBcpSiteSelection"
										options="$ctrl.bcpSiteOptions"
										placeholder="Search..."
										on-enter-key="$ctrl.addBCPSiteAssignment($ctrl.currentBcpSiteSelection.value)"
										disabled="$ctrl.fieldsDisabled"
										raw-output="true"
						>
						</juno-typeahead>
						<button class="btn btn-primary lg-padding-left lg-padding-right add-role-button" title="Add role"
										ng-click="$ctrl.addBCPSiteAssignment($ctrl.currentBcpSiteSelection.value)">Add</button>
					</div>
					<div class="user-role-list col-sm-12 md-margin-top">
						<label class="body-smallest md-padding-bottom">Sites</label>
						<ul class="no-padding">
							<li ng-repeat="siteId in $ctrl.provider.bcpSites" class="group-list-item">
								<div class="flex-row align-items-center body-small-bold">
									{{$ctrl.getSiteName(siteId)}}
									<div class="flex-grow text-right">
										<i class="icon icon-delete hand-hover" title="Remove role" ng-click="$ctrl.removeBCPSiteAssignment(siteId)"></i>
									</div>
								</div>
								<hr>
							</li>
						</ul>
					</div>
				</panel-body>
			</panel>

			<panel id="edit-provider-3rd-party-identifiers">
				<panel-header>
					3rd Party Identifiers
				</panel-header>
				<panel-body>
					<!-- 3rd Party Identifiers -->
					<div ng-if="$ctrl.billingRegion === 'BC'">
						<ca-field-text
										ca-name="cpsid"
										ca-title="CPSID"
										ca-model="$ctrl.provider.cpsid"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
						<ca-field-text
										ca-name="ihaProviderMnemonic"
										ca-title="IHA Provider Mnemonic"
										ca-model="$ctrl.provider.ihaProviderMnemonic"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>
					<div ng-if="$ctrl.billingRegion === 'ON'">
						<ca-field-text ng-if="$ctrl.provider.type !== 'nurse'"
									   ca-name="cpsid"
									   ca-title="CPSID"
									   ca-model="$ctrl.provider.cpsid"
									   ca-rows="1"
									   ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
						<ca-field-text ng-if="$ctrl.provider.type === 'nurse'"
									   ca-name="cno"
									   ca-title="CNO Number"
									   ca-model="$ctrl.provider.onCnoNumber"
									   ca-rows="1"
									   ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
						<ca-field-text
										ca-name="lifeLabsClientId"
										ca-title="Life Labs Client Id"
										ca-model="$ctrl.provider.lifeLabsClientId"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>

					<div ng-if="$ctrl.billingRegion === 'AB'">
						<ca-field-text
										ca-name="cpsid"
										ca-title="CPSID"
										ca-model="$ctrl.provider.cpsid"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
						<ca-field-text
										ca-name="eDeliveryIds"
										ca-title="E-Delivery Ids"
										ca-model="$ctrl.provider.eDeliveryIds"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
						<ca-field-text
										ca-name="TAKNumber"
										ca-title="TAK #"
										ca-model="$ctrl.provider.takNumber"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
						<ca-field-text
										ca-name="connectCareId"
										ca-title="Connect Care Provider Id"
										ca-model="$ctrl.provider.connectCareProviderId"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>
					<div ng-if="$ctrl.billingRegion === 'SK'">
						<ca-field-text
										ca-name="cpsid"
										ca-title="CPSID"
										ca-model="$ctrl.provider.cpsid"
										ca-rows="1"
										ca-disabled="$ctrl.fieldsDisabled"
						>
						</ca-field-text>
					</div>

				</panel-body>
			</panel>

		</div>
	</div>
	<div class="bottom-options-bar">
		<div class="col-sm-12 flex-row justify-content-center md-margin-top">
			<button class="btn btn-primary" ng-if="$ctrl.mode === $ctrl.modes.ADD" ng-click="$ctrl.submit()">Add User</button>
			<button class="btn btn-primary" ng-if="$ctrl.mode === $ctrl.modes.EDIT" ng-click="$ctrl.submit()">Update User</button>
			<button class="btn btn-primary" ng-if="$ctrl.mode === $ctrl.modes.VIEW" ng-click="$ctrl.goToEdit()">Edit User</button>
		</div>
	</div>
</div>
