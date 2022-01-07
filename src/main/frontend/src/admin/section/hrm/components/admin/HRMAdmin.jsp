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
<div id="hrm-admin" class="flex-col align-items-center justify-content-top h-100">
	<panel class="stats-panel w-100"
		   component-style="$ctrl.COMPONENT_STYLE">
		<panel-header>
			<h6>System Status</h6>
		</panel-header>
		<panel-body>
			<div class="flex-row flex-grow-1 justify-content-space-between">
				<div class="flex-col align-items-start m-l-24">
					<div class="summary-step m-t-8 m-b-8 flex-row align-items-center">
						<div class="status p-4 m-r-24" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getLoginSummary())">Remote Connection</div>
						<div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getLoginSummary()) }}</div>
					</div>
					<div class="summary-step m-t-8 m-b-8 flex-row align-items-center">
						<div class="status p-4 m-r-24" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getDownloadSummary())">Report Downloading</div>
						<div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getDownloadSummary()) }}</div>
					</div>
					<div class="summary-step m-t-8 m-b-8 flex-row align-items-center">
						<div class="status p-4 m-r-24" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getProcessingSummary())">Report Processing</div>
						<div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getProcessingSummary()) }}</div>
					</div>
				</div>
				<div class="flex-col align-items-end m-r-24">
					<p class="m-b-4 xs-text">{{ $ctrl.lastCheckedMessage() }}</p>
					<p class="m-b-0 xs-text" ng-show="$ctrl.latestResults">{{ $ctrl.latestResults.reportsDownloaded }} reports downloaded, {{ $ctrl.latestResults.reportsProcessed }} reports processed</p>
				</div>
			</div>
		</panel-body>
	</panel>
	<panel class="polling-panel w-100">
		<panel-header class="flex-row justify-content-between align-items-center">
			<h6 class="d-inline-block">HRM Report Polling</h6>
			<div class="d-flex">
				<juno-button ng-if="!$ctrl.working"
							 class="flex-grow-0 w-256"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 disabled="!$ctrl.canRead()"
							 click="$ctrl.fetchHRMDocs()">
					Check For Reports Now
				</juno-button>
				<div ng-if="$ctrl.working" class="loading-container">
					<juno-loading-indicator indicator-type="dot-pulse"></juno-loading-indicator>
				</div>
			</div>
		</panel-header>
		<panel-body class="flex-col">
			<div class="flex-row align-items-center">
				<juno-input label="Automatic Polling Frequency (minutes)"
							label-position="$ctrl.LABEL_POSITION"
							ng-model="$ctrl.pollingInterval"
							disabled="true"
				>
				</juno-input>
			</div>
		</panel-body>
	</panel>

</div>