<div id="hrm-admin" class="flex-col align-items-center justify-content-top h-100">
    <div><h4>Health Report Manager (HRM) Admin</h4></div>
        <panel class="summary-container">
            <panel-header>
                <h6>System Status</h6>
            </panel-header>
            <panel-body>
                <div class="flex-col">
                    <div class="flex-col align-items-start">
                        <div class="summary-step flex-row align-items-center">
                            <div class="status" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getLoginSummary())">Connection</div>
                            <div>{{ $ctrl.getSummaryText($ctrl.latestResults.getLoginSummary()) }}</div>
                        </div>
                        <div class="summary-step flex-row align-items-center">
                            <div class="status" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getDownloadSummary())">Downloading</div>
                            <div>{{ $ctrl.getSummaryText($ctrl.latestResults.getLoginSummary()) }}</div>
                        </div>
                        <div class="summary-step flex-row align-items-center">
                            <div class="status" ng-class="$ctrl.getSummaryClass($ctrl.latestResults.getProcessingSummary())">Processing</div>
                            <div>{{ $ctrl.getSummaryText($ctrl.latestResults.getLoginSummary()) }}</div>
                        </div>
                    </div>
                    <div class="summary-metrics flex-col align-items-end">
                        <p>Last checked for reports {{ $ctrl.lastCheckedAsMinutesAgo() }} minutes ago</p>
                        <p>{{ $ctrl.latestResults.reportsDownloadedCount }} reports downloaded, {{ $ctrl.latestResults.reportsProcessedCount }} reports processed</p>
                    </div>
                </div>
            </panel-body>
        </panel>
    <div class="d-flex flex-col justify-content-center align-items-center m-t-24">
        <div ng-if="!$ctrl.working" class="fetch-container">
            <juno-button class="download-button"
                         button-color="JUNO_BUTTON_COLOR.PRIMARY"
                         button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
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
                    <div><span class="row-title">Port:</span>{{ $ctrl.port }}</>
                </div>
            </div>
        </div>
        <div class="width-100 section">
            <div class="flex-col justify-content-center header">
                <h5>Downloading</h5>
            </div>
            <p>Juno EMR is automatically checking for new HRM reports every {{ $ctrl.interval }} minutes.
                To check now, use the "Check For Reports" button above.</p>
        </div>
        <div class="width-100 section">
            <div class="flex-col justify-content-center header">
                <h5>Processing</h5>
            </div>
            <p>Juno EMR will decrypt your HRM reports and try to associate demographics and providers using information contained in the message.
                You can always change these assignments at any time using the HRM report viewer.</p>
        </div>
    </div>
</div>
