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
	<div class="col-lg-6 col-sm-10 md-margin-top">
		<div class="form-group col-sm-6">
			<label>OLIS Default Reporting Laboratory:</label>
			<select class="form-control" ng-model="$ctrl.pref.olisDefaultReportingLab"
			        ng-options="item.value as item.label for item in $ctrl.olisLabs">
			</select>
		</div>
		<div class="form-group col-sm-6">
			<label>OLIS Default Exclude Reporting Laboratory:</label>
			<select class="form-control" ng-model="$ctrl.pref.olisDefaultExcludeReportingLab"
			        ng-options="item.value as item.label for item in $ctrl.olisLabs">
			</select>
		</div>

		<div class="form-group col-sm-6">
			<label>MyDrugRef ID:</label>
			<input ng-model="$ctrl.pref.myDrugRefId" placeholder="MyDrugRef ID" class="form-control" type="text">
		</div>
		<div class="form-group col-sm-6">
			<label>Use MyMeds:</label>
			<div class="controls">
				<label class="radio-inline" for="enable-use-my-meds">
					<input name="enableUseMyMeds" id="enable-use-my-meds" ng-model="$ctrl.pref.useMyMeds" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="disable-use-my-meds">
					<input name="disableUseMyMeds" id="disable-use-my-meds" ng-model="$ctrl.pref.useMyMeds" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>

		<div class="form-group col-sm-6">
			<label>BORN prompts in RBR/NDDS:</label>
			<div class="controls">
				<label class="radio-inline" for="enable-born-prompt">
					<input name="enableBornPrompt" id="enable-born-prompt" ng-model="$ctrl.pref.disableBornPrompts" ng-value="false" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="disable-born-prompt">
					<input name="disableBornPrompt" id="disable-born-prompt" ng-model="$ctrl.pref.disableBornPrompts" ng-value="true" type="radio">
					Disable
				</label>
			</div>
		</div>

		<div class="form-group col-sm-6">
			<label>
				Apps:<a ng-click="$ctrl.refreshAppList()">Refresh</a>
			</label>
			<table class="table table-striped table-bordered">
				<tr>
					<th>App Name</th>
					<th>Status</th>
				</tr>
				<tr ng-repeat="app in $ctrl.loadedApps">
					<td>{{app.name}}</td>
					<td ng-show="app.authenticated">{{app.authenticated}}</td>
					<td ng-hide="app.authenticated"><a ng-click="$ctrl.authenticate(app)">Authenticate</a></td>
				</tr>
			</table>
		</div>

		<div class="form-group col-sm-12">
			<div class="controls">
				<button class="btn btn-default" ng-click="$ctrl.openManageAPIClientPopup()">Manage API Clients</button>
			</div>
		</div>

		<div class="form-group col-sm-12">
			<div class="controls">
				<button class="btn btn-default" ng-click="$ctrl.openMyOscarUsernamePopup()">Set PHR Username</button>
			</div>
		</div>

		<div class="form-group col-sm-12">
			<juno-input ng-model="$ctrl.pref.netcareUserId"
			            label="Netcare User Id"
			            label-position="$ctrl.LABEL_POSITION.LEFT">
			</juno-input>
		</div>
	</div>
</div>

