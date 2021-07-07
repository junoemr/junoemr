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

<div class="health-tracker-component">
    <h1>Patient Health Tracker</h1>

    <juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.FLOWSHEET_READ">
        <div class="flex-row">
            <div class="flex-column">
                <div class="list-group">
                    <button ng-repeat="flowsheet in $ctrl.triggerdFlowsheets"
                            type="button"
                            class="list-group-item list-group-item-action"
                            ng-class="{'active': flowsheet === $ctrl.selectedFlowsheet}"
                            ng-click="$ctrl.onFlowsheetSelect(flowsheet)">
                        {{flowsheet.name}}
                    </button>
                    <span ng-if="$ctrl.triggerdFlowsheets.length === 0" class="list-group-item">
                        No Active Flowsheets
                    </span>
                </div>
                <accordion-list item-list="$ctrl.accordianListItems" item-clicked="$ctrl.onFlowsheetSelect(item)">
                </accordion-list>
            </div>
            <div class="ui-view-wrapper flex-grow">
                <ui-view></ui-view>
            </div>
        </div>
    </juno-security-check>
</div>