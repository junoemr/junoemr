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

<div class="care-tracker-manager">
	<div class="flex-row justify-content-between align-items-center">
		<h1>Manage Health Tracker</h1>

		<div class="button-wrapper">
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
			             disabled="$ctrl.isLoading || !$ctrl.userCanCreate()"
			             click="$ctrl.onCareTrackerNew()">
				New Care Tracker
			</juno-button>
		</div>
	</div>

	<juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.CARE_TRACKER_READ">
		<div ng-repeat="table in $ctrl.tablesConfig"
		     ng-if="table.visible"
		     class="care-trackers-list">
			<span>{{table.name}}</span>
			<table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
				<tbody>
				<tr ng-repeat="item in table.items">
					<td data-title="'Care Tracker'">
						{{item.name}}
					</td>
					<td data-title="'Description'">
						{{item.description}}
					</td>
					<td class="action-buttons">
						<div class="flex-row justify-content-space-evenly">
							<div class="row-spacing-l action-button-wrapper">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
								             disabled="$ctrl.isLoading || !table.enableEdit"
								             click="$ctrl.onCareTrackerEdit(item)">
									Edit
								</juno-button>
							</div>
							<div class="row-spacing action-button-wrapper">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
								             disabled="$ctrl.isLoading || !table.enableClone"
								             click="$ctrl.onCloneCareTracker(item)">
									Copy
								</juno-button>
							</div>
							<div class="row-spacing action-button-wrapper">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
								             disabled="$ctrl.isLoading || !table.enableEdit"
								             click="$ctrl.onToggleCareTrackerEnabled(item)">
									{{$ctrl.toggleCareTrackerEnabledLabel(item)}}
								</juno-button>
							</div>
							<div class="row-spacing-r action-button-wrapper">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
								             disabled="$ctrl.isLoading || !table.enableDelete"
								             click="$ctrl.onCareTrackerDelete(item)">
									Delete
								</juno-button>
							</div>
						</div>
					</td>
				</tr>
				</tbody>
			</table>
		</div>
	</juno-security-check>
</div>