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
<div id="admin-panel-management">
	<div class="col-sm-12 form-box">
		<panel class="no-padding">
			<panel-header>
				<h4>Panel Management</h4>
			</panel-header>
			<panel-body>
				<div class="col-sm-12 flex-row flex-wrap">
					<div class="report-select" ng-class="{'required': $ctrl.missingRequiredFeildProvider}">
						<ca-field-select
										class="juno-modal no-padding md-margin-right"
										ca-name="provider"
										ca-title="Provider"
										ca-template="label"
										ca-model="$ctrl.selectedProvider"
										ca-options="$ctrl.providers"
										ca-text-placeholder="Select a Provider"
										ca-empty-option="true"
										ca-required-field="true"
						>
						</ca-field-select>
					</div>
					<div class="report-select" ng-class="{'required': $ctrl.missingRequiredFeildPanel}">
						<ca-field-select
										class="juno-modal no-padding"
										ca-name="panel"
										ca-title="Panel"
										ca-template="label"
										ca-model="$ctrl.selectedPanel"
										ca-options="$ctrl.panels"
										ca-text-placeholder="Select a Panel"
										ca-empty-option="true"
										ca-required-field="true"
						>
						</ca-field-select>
					</div>
					<button class="btn btn-primary xl-margin-left" ng-click="$ctrl.runReport()">Run Report</button>
				</div>
			</panel-body>
		</panel>
	</div>
	<iframe ng-if="$ctrl.showIframe()"
	        id="dashboard-embedded-page"
	        ng-src="{{$ctrl.dashboardUrl}}"
	        width="97%"
	        height="4000px">
	</iframe>
</div>
