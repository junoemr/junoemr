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
<div id="ice-fall-admin-settings">
	<div class="d-flex flex-row align-items-center">
		<label class="switch-label" for="enable-icefall-integration">
			Enable
		</label>
		<label class="switch">
			<input id="enable-icefall-integration" type="checkbox"
						 ng-model="$ctrl.iceFallSettings.visible"
						 ng-change="$ctrl.setIceFallVisible($ctrl.iceFallSettings.visible)"/>
			<span class="slider"></span>
		</label>
		<i class="integration-check icon" ng-class="{'icon-check': $ctrl.iceFallSettings.visible}"></i>
	</div>

	<h3>Account Settings</h3>

	<form class="ice-fall-setting-left-col col-sm-3" ng-submit="$ctrl.saveIceFallSettings()">
		<fieldset ng-disabled="!$ctrl.iceFallSettings.visible">

			<ca-field-text
							class="md-margin-top"
							ca-name="userName"
							ca-title="User Name"
							ca-model="$ctrl.iceFallSettings.clinicUserName"
							ca-rows="1"
			>
			</ca-field-text>

			<ca-field-text
							class="md-margin-top"
							ca-name="email"
							ca-title="Email"
							ca-model="$ctrl.iceFallSettings.clinicEmail"
							ca-rows="1"
			>
			</ca-field-text>

			<ca-field-password
							class="md-margin-top"
							ca-name="password"
							ca-title="Password"
							ca-text-placeholder ="*********"
							ca-model="$ctrl.iceFallSettings.clinicPassword"
							ca-rows="1"
			>
			</ca-field-password>
		</fieldset>

		<div class="md-margin-top d-flex flex-row justify-content-right">
			<input class="btn btn-primary" type="submit" name="Save" value="Save">
		</div>

	</form>

</div>