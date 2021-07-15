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
		<div class="col-sm-6 no-padding md-margin-top">
			<div class="form-group col-sm-12">
				<label>RX3:</label>
				<div class="controls">
					<label class="radio-inline" for="radios-rx-0">
						<input name="radios-rx-0" id="radios-rx-0" ng-model="$ctrl.pref.useRx3" ng-value="true" type="radio">
						Enable
					</label>
					<label class="radio-inline" for="radios-rx-1">
						<input name="radios-rx-1" id="radios-rx-1" ng-model="$ctrl.pref.useRx3" ng-value="false" type="radio">
						Disable
					</label>
				</div>
			</div>

			<div class="form-group col-sm-12">
				<label>Show Patient's DOB:</label>
				<div class="controls">
					<label class="radio-inline" for="radios-rx-2">
						<input name="radios-rx-2" id="radios-rx-2" ng-model="$ctrl.pref.showPatientDob" ng-value="true" type="radio">
						Enable
					</label>
					<label class="radio-inline" for="radios-rx-3">
						<input name="radios-rx-3" id="radios-rx-3" ng-model="$ctrl.pref.showPatientDob" ng-value="false" type="radio">
						Disable
					</label>
				</div>
			</div>
			<div class="form-group col-sm-12">
				<label>Signature:</label>
				<input ng-model="$ctrl.pref.signature" placeholder="Signature" class="form-control" type="text">
			</div>

			<div class="form-group col-sm-12">
				<label>Default Quantity:</label>
				<input ng-model="$ctrl.pref.rxDefaultQuantity" placeholder="Default Qty" class="form-control" type="text">
			</div>
			<div class="form-group col-sm-12">
				<label>Page Size:</label>
				<select ng-model="$ctrl.pref.rxPageSize"
				        class="form-control"
				        ng-options="p.value as p.label for p in $ctrl.pageSizes">
				</select>
			</div>
			<div class="form-group col-sm-12">
				<label>Rx Interaction Warning Level:</label>
				<select ng-model="$ctrl.pref.rxInteractionWarningLevel"
				        class="form-control"
				        ng-options="p.value as p.label for p in $ctrl.rxInteractionWarningLevels">
				</select>
			</div>

			<div class="form-group col-sm-12">
				<label>Print QR Codes:</label>
				<div class="controls">
					<label class="radio-inline" for="radios-rx-4">
						<input name="radios-rx-4" id="radios-rx-4" ng-model="$ctrl.pref.printQrCodeOnPrescription" ng-value="true" type="radio">
						Enable
					</label>
					<label class="radio-inline" for="radios-rx-5">
						<input name="radios-rx-5" id="radios-rx-5" ng-model="$ctrl.pref.printQrCodeOnPrescription" ng-value="false" type="radio">
						Disable
					</label>
				</div>
			</div>
		</div>
		<div class="col-sm-6 no-padding">
			<div class="col-sm-12">
				<h3>External Prescriber</h3>
				<hr>
			</div>
			<div class="form-group col-sm-12">
				<label></label>
				<div class="controls">
					<label class="radio-inline" for="radios-rx-6">
						<input name="radios-rx-6" id="radios-rx-6" ng-model="$ctrl.pref.eRxEnabled" ng-value="true" type="radio">
						Enable
					</label>
					<label class="radio-inline" for="radios-rx-7">
						<input name="radios-rx-7" id="radios-rx-7" ng-model="$ctrl.pref.eRxEnabled" ng-value="false" type="radio">
						Disable
					</label>
				</div>
			</div>
			<div class="form-group col-sm-12">
				<label>Training Mode:</label>
				<div class="controls">
					<label class="radio-inline" for="radios-rx-8">
						<input name="radios-rx-8" id="radios-rx-8" ng-model="$ctrl.pref.eRxTrainingMode" ng-value="true" type="radio">
						Enable
					</label>
					<label class="radio-inline" for="radios-rx-9">
						<input name="radios-rx-9" id="radios-rx-9" ng-model="$ctrl.pref.eRxTrainingMode" ng-value="false" type="radio">
						Disable
					</label>
				</div>
			</div>
			<div class="form-group col-sm-12">
				<label>Username:</label>
				<input ng-model="$ctrl.pref.eRxUsername" placeholder="Username" class="form-control" type="text">
			</div>
			<div class="form-group col-sm-12">
				<label>Password:</label>
				<input ng-model="$ctrl.pref.eRxPassword" placeholder="Password" class="form-control" type="text">
			</div>
			<div class="form-group col-sm-12">
				<label>Clinic #:</label>
				<input ng-model="$ctrl.pref.eRxFacility" placeholder="Clinic #" class="form-control" type="text">
			</div>
			<div class="form-group col-sm-12">
				<label>URL:</label>
				<input ng-model="$ctrl.pref.eRxURL" placeholder="URL" class="form-control" type="text">
			</div>
		</div>
	</div>
</div>
