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
        <panel class="summary-container"
               component-style="$ctrl.COMPONENT_STYLE">
            <panel-header>
                <h6>System Status</h6>
            </panel-header>
            <panel-body>
                <div class="flex-col">
                    <div class="flex-col align-items-start">
                        <div class="summary-step m-t-8 m-b-8 flex-row align-items-center">
                            <div class="status p-4 m-r-24" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getLoginSummary())">Connection</div>
                            <div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getLoginSummary()) }}</div>
                        </div>
                        <div class="summary-step m-t-8 m-b-8 flex-row align-items-center">
                            <div class="status p-4 m-r-24" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getDownloadSummary())">Downloading</div>
                            <div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getDownloadSummary()) }}</div>
                        </div>
                        <div class="summary-step m-t-8 m-b-8 flex-row align-items-center">
                            <div class="status p-4 m-r-24" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getProcessingSummary())">Processing</div>
                            <div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getProcessingSummary()) }}</div>
                        </div>
                    </div>
                    <div class="summary-footer m-t-4 p-0 flex-col align-items-end">
                        <p>{{ $ctrl.lastCheckedMessage() }}</p>
                        <p ng-show="$ctrl.latestResults">{{ $ctrl.latestResults.reportsDownloaded }} reports downloaded, {{ $ctrl.latestResults.reportsProcessed }} reports processed</p>
                    </div>
                </div>
            </panel-body>
        </panel>
    <div class="d-flex flex-col justify-content-center align-items-center m-t-24">
		<juno-button ng-if="!$ctrl.working"
					 class="download-button flex-grow-0 w-256"
					 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
					 click="$ctrl.fetchHRMDocs()">
			Check For Reports
		</juno-button>
        <div ng-if="$ctrl.working">
            <juno-loading-indicator indicator-type="dot-pulse"></juno-loading-indicator>
        </div>
    </div>
</div>