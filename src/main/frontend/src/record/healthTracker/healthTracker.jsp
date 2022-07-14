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
    <div ng-if="!$ctrl.embeddedView" class="flex-row justify-content-end">
        <div class="options-button-wrapper">
            <juno-button component-style="$ctrl.componentStyle"
                         button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                         button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
                         disabled="$ctrl.isLoading"
                         click="$ctrl.onManageCareTrackers()">
                <div class="flex-row align-items-center">
                    <i class="icon icon-gears"></i>
                    <span>Manage Health Tracker</span>
                </div>
            </juno-button>
        </div>
    </div>

    <juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.CareTrackerRead">
        <div class="flex-row">
            <div class="flex-column no-print tracker-nav">
                <accordion-list item-list="$ctrl.accordianListItems" item-clicked="$ctrl.onCareTrackerSelect(item)">
                </accordion-list>
                <juno-button component-style="$ctrl.componentStyle"
                             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
                             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
                             disabled="$ctrl.isLoading"
                             click="$ctrl.onViewAllPatientMeasurements()">
                    View All Measurements
                </juno-button>
            </div>
            <div class="ui-view-wrapper flex-grow">
                <ui-view></ui-view>
            </div>
        </div>
    </juno-security-check>
</div>