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
<div id="integration-modules-page">
    <div class="integration-modules" id="integration-modules">
        <h1 class="page-header text-center">Manage Integrations</h1>
        <div class="integration-list flex flex-row justify-content-center">
            <!-- integrations -->
            <div class="integration-item flex-row align-items-first-baseline " ng-repeat="integration in $ctrl.integrationList">
                <label class="switch-label" for="{{ integration.name }}">
                    <a ng-if="integration.configUrl && integration.enabled" ng-href="{{integration.configUrl}}">{{ integration.name }}</a>
                    <span ng-if="!integration.configUrl || !integration.enabled">{{ integration.name }}</span>
                </label>
                <label class="switch">
                    <input id="{{integration.name}}" type="checkbox"
                           ng-model="integration.enabled"
                           ng-change="$ctrl.enableProperty(integration.propertyName, integration.enabled)"/>
                    <span class="slider"></span>
                </label>
                <i class="integration-check icon" ng-class="integration.enabled ? 'icon-check' : ''"></i>
            </div>
        </div>
    </div>
</div>
