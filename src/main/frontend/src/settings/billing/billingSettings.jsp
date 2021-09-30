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
	<div class="col-sm-6 md-margin-top">
		<div class="form-group col-sm-6">
			<label>Default Billing Form:</label>
			<select class="form-control" ng-model="$ctrl.pref.defaultServiceType"
			        ng-options="item.type as item.name for item in $ctrl.billingServiceTypesMod">
			</select>
		</div>
		<div class="form-group col-sm-6">
			<label>Default Diagnostic Code:</label>
			<div class="input-group">
				<input ng-model="$ctrl.pref.defaultDxCode" placeholder="" class="form-control" type="text">
				<span class="input-group-btn">
					<button class="btn btn-default btn-search" ng-disabled="true">Search</button>
				</span>
			</div>
		</div>

		<div class="form-group col-sm-6">
			<label>Do Not Delete Previous Billing:</label>
			<div class="controls">
				<label class="checkbox-inline" for="radiosx-0">
					<input ng-model="$ctrl.pref.defaultDoNotDeleteBilling" name="radiosx" id="radiosx-0" ng-value="true"  type="radio">
					Enable
				</label>
				<label class="checkbox-inline" for="radiosx-1">
					<input ng-model="$ctrl.pref.defaultDoNotDeleteBilling" name="radiosx" id="radiosx-1" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>
	</div>
</div>