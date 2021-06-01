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
<div id="imdhealth-admin" ng-if="$ctrl.isBusy">
    <juno-loading-indicator
            class="loading-indicator-container"
            message = "Synchronizing"
            message-alignment="vertical"
            indicator-type="dot-pulse"
    ></juno-loading-indicator>
</div>
<div id="imdhealth-admin" ng-if="!$ctrl.isBusy">
    <h1>Patient Education by iMD Health</h1>
    <div class="marketing-container">
        <p>iMD Health is designed for healthcare professionals at every level of care to better engage, inform and educate patients about their conditions and treatment plans. </p>
        <p><a href="https://www.imdhealth.com" target="_blank">Visit iMD Health</a></p>
    </div>
    <div class="credentials-container">
        <juno-input
            ng-model="$ctrl.credentials.clientId"
            label="Client ID"
            label-position="$ctrl.LABEL_POSITION.TOP"
            component-style="$ctrl.componentStyle">
        </juno-input>
        <juno-input
            ng-model="$ctrl.credentials.clientSecret"
            label="Client Secret"
            label-position="$ctrl.LABEL_POSITION.TOP"
            component-style="$ctrl.componentStyle">
        </juno-input>
        <juno-button
                ng-click="$ctrl.onSave()"
                button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
                button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                component-style="$ctrl.componentStyle">
            Update Credentials
        </juno-button>
    </div>
    <div class="integrations-container">
        <panel ng-if="$ctrl.imdIntegrations.length > 0" component-style="$ctrl.componentStyle">
            <panel-header>
                <div>
                    <h6>Active Integrations</h6>
                </div>
            </panel-header>
            <panel-body>
                <div class="imd-integration" ng-repeat="integration in $ctrl.imdIntegrations">
                    <div class="imd-integration-name">
                        <label>
                            Client ID:
                            <span> {{ integration.clientId }} </span>
                        </label>
                    </div>
                    <div class="imd-integration-controls">
                        <juno-button
                            ng-click="$ctrl.testSSO(integration.integrationId)"
                            button-color="$ctrl.JUNO_BUTTON_COLOR.INFO"
                            button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"">
                            Test
                        </juno-button>
                        <juno-button
                                ng-click="$ctrl.syncIntegrations(integration.integrationId)"
                                button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
                                button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                                component-style="$ctrl.componentStyle">
                            Sync
                        </juno-button>
                        <juno-button
                            ng-click="$ctrl.removeIntegration(integration.integrationId)"
                            button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
                            button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED">
                            Delete
                        </juno-button>
                    </div>
                </div>
            </panel-body>
        </panel>
    </div>
</div>
