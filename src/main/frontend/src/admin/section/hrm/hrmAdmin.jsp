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
    <div><h4>Health Report Manager (HRM) Admin</h4></div>
        <panel class="summary-container"
               component-style="$ctrl.COMPONENT_STYLE">
            <panel-header>
                <h6>System Status</h6>
            </panel-header>
            <panel-body>
                <div class="flex-col">
                    <div class="flex-col align-items-start">
                        <div class="summary-step flex-row align-items-center">
                            <div class="status" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getLoginSummary())">Connection</div>
                            <div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getLoginSummary()) }}</div>
                        </div>
                        <div class="summary-step flex-row align-items-center">
                            <div class="status" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getDownloadSummary())">Downloading</div>
                            <div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getDownloadSummary()) }}</div>
                        </div>
                        <div class="summary-step flex-row align-items-center">
                            <div class="status" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getProcessingSummary())">Processing</div>
                            <div class="status-text">{{ $ctrl.getSummaryText($ctrl.latestResults.getProcessingSummary()) }}</div>
                        </div>
                    </div>
                    <div class="summary-footer flex-col align-items-end">
                        <p>Last checked for reports {{ $ctrl.lastCheckedAsMinutesAgo() }} minutes ago</p>
                        <p>{{ $ctrl.latestResults.reportsDownloadedCount }} reports downloaded, {{ $ctrl.latestResults.reportsProcessedCount }} reports processed</p>
                    </div>
                </div>
            </panel-body>
        </panel>
    <div class="d-flex flex-col justify-content-center align-items-center m-t-24">
        <div ng-if="!$ctrl.working" class="fetch-container">
            <juno-button class="download-button"
                         button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                         button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
                         click="$ctrl.fetchHRMDocs()">
                Check For Reports
            </juno-button>
        </div>
        <div ng-if="$ctrl.working">
            <juno-loading-indicator indicator-type="dot-pulse"></juno-loading-indicator>
        </div>
    </div>
    <div class="details-container flex-col align-items-start">
        <div class="width-100 section">
            <div class="flex-col justify-content-center header">
                <h5>Connection</h5>
            </div>
            <div>
                <p>Juno EMR is using the following connection settings:</p>
                <div class="flex-col width-100 align-items-start">
                    <div><span class="row-title">Address:</span> {{ $ctrl.address }}</div>
                    <div><span class="row-title">User Name:</span> {{ $ctrl.user }}</div>
                    <div><span class="row-title">Remote Path:</span> {{ $ctrl.remotePath }}</div>
                    <div><span class="row-title">Port:</span>{{ $ctrl.port }}</div>
                </div>
            </div>
        </div>
        <div class="width-100 section">
            <div class="flex-col justify-content-center header">
                <h5>Downloading</h5>
            </div>
            <p>Juno EMR is automatically checking for new HRM reports every {{ $ctrl.interval }} minutes.</p>
            </p>To check now, use the "Check For Reports" button above.</p>
        </div>
        <div class="width-100 section">
            <div class="flex-col justify-content-center header">
                <h5>Processing</h5>
            </div>
            <p>Juno EMR is decrypting your HRM reports.</p>
            <p>Demographics and providers will be automatically linked to each message.
               You can always change these assignments at any time using the HRM report viewer.</p>
        </div>
    </div>
</div>
