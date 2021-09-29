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
<div class="row">
	<div class="col-sm-6">
		<div class="col-sm-12">
			<h3>Override Clinic</h3>
			<hr>
		</div>
		<div class="form-group col-sm-6">
			<label class="control-label">Address:</label>
			<input ng-model="$ctrl.pref.rxAddress" placeholder="Address" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label class="control-label">City:</label>
			<input ng-model="$ctrl.pref.rxCity" placeholder="City" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Province:</label>
			<input ng-model="$ctrl.pref.rxProvince" placeholder="Province" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Postal Code:</label>
			<input ng-model="$ctrl.pref.rxPostal" placeholder="Postal Code" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Phone:</label>
			<input ng-model="$ctrl.pref.rxPhone" placeholder="Phone" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Fax:</label>
			<input ng-model="$ctrl.pref.faxNumber" placeholder="Fax" class="form-control" type="text">
		</div>
		<div class="col-sm-12">
			<h3>Other Settings</h3>
			<hr>
		</div>
		<div class="form-group col-sm-6">
			<label>Tickler Window Provider:</label>
			<select ng-model="$ctrl.pref.ticklerWarningProvider" class="form-control" ng-options="p.providerNo as p.name for p in $ctrl.providerList">
			</select>
		</div>

		<div class="form-group col-sm-6">
			<label>Workload Management:</label>
			<select ng-model="$ctrl.pref.workloadManagement" class="form-control" ng-options="item.name as item.type for item in $ctrl.billingServiceTypesMod">
			</select>
		</div>

		<div class="form-group col-sm-6">
			<label></label>
			<div class="controls">
				<button class="btn btn-default" ng-click="$ctrl.openChangePasswordModal()">Change Password</button>
			</div>
		</div>

		<div class="form-group col-sm-6">
			<label>Enable Tickler Window:</label>
			<div class="controls">
				<label class="checkbox-inline" for="radios-0">
					<input ng-model="$ctrl.pref.newTicklerWarningWindow" name="radios" id="radios-0" value="enabled" type="radio">
					Enable
				</label>
				<label class="checkbox-inline" for="radios-1">
					<input ng-model="$ctrl.pref.newTicklerWarningWindow" name="radios" id="radios-1" value="disabled" type="radio">
					Disable
				</label>
			</div>
		</div>

		<div class="form-group col-sm-6">
			<label>Default Tickler View</label>
			<div>
				<label class="checkbox-inline" for="all-ticklers-radio">
					<input ng-model="$ctrl.pref.ticklerViewOnlyMine" name="tickler-view-radios" id="all-ticklers-radio" ng-value="false" type="radio">
					All ticklers
				</label>
				<label class="checkbox-inline" for="onlymine-ticklers-radio">
					<input ng-model="$ctrl.pref.ticklerViewOnlyMine" name="tickler-view-radios" id="onlymine-ticklers-radio" ng-value="true" type="radio">
					View mine only
				</label>
			</div>
		</div>
		<!-- Extra column intentionally left empty to make things line up -->
		<div class="form-group col-sm-6"></div>
		<div class="form-group col-sm-6">
			<label>CareConnect PPN Check</label>
			<div>
				<label class="checkbox-inline" for="careconnect-ppn-enable-radio">
					<input ng-model="$ctrl.pref.enableCareConnectPPNCheck"
					       name="careconnect-view-disable"
					       id="careconnect-ppn-enable-radio"
					       ng-value="true"
					       type="radio">
					Check for PPN
				</label>
				<label class="checkbox-inline" for="careconnect-ppn-disable-radio">
					<input ng-model="$ctrl.pref.enableCareConnectPPNCheck"
					       name="careconnect-view-disable"
					       id="careconnect-ppn-disable-radio"
					       ng-value="false"
					       type="radio">
					Disable PPN Check
				</label>
			</div>
		</div>
	</div>
</div>
