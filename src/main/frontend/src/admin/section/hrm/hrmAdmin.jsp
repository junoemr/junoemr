<div id="hrm-admin" class="flex-col align-items-center justify-content-top h-100">
    <div class="d-flex justify-content-start"><h1>Hospital Report Manager (HRM) Admin</h1></div>
    <div class="">
        <div class="connection-settings section" class="flex-col">
            <div class="d-flex justify-content-center header">
                <h4>Connection Settings</h4>
            </div>
            <p><span class="header">User Name: </span>{{$ctrl.user}}</p>
            <p><span class="header">Address: </span>{{$ctrl.address}}</p>
            <p><span class="header">Remote Path: </span>{{$ctrl.remotePath}}</p>
            <div><span class="header">Port: </span>{{$ctrl.port}}</div>
        </div>
        <div class="schedule-settings flex-col section">
            <div class="d-flex justify-content-center header">
                <h4>Schedule Settings</h4>
            </div>
            <p>JUNO EMR is automatically checking for reports every {{$ctrl.interval}} minutes</p>
            <p>New reports will be available on both patient eCharts and provider inboxes</p>
        </div>
        <div class="d-flex flex-col justify-content-center align-items-center section">
            <div class="d-flex justify-content-center header">
                <h4>Check For New Reports</h4>
            </div>
            <div ng-if="!$ctrl.working" class="fetch-container">
                <juno-button class="download-button"
                             button-color="JUNO_BUTTON_COLOR.PRIMARY"
                             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
                             click="$ctrl.fetchHRMDocs()">
                    Fetch Now
                </juno-button>
            </div>
            <div ng-if="$ctrl.working">
                <juno-loading-indicator
                        indicator-type="dot-pulse"
                >
                </juno-loading-indicator>
            </div>
        </div>
    </div>
</div>
