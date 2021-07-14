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

<div class="flowsheet-manager">
	<div class="flex-row justify-content-between align-items-center">
		<h1>Manage Health Tracker</h1>

		<div class="button-wrapper">
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
			             disabled="$ctrl.isLoading || !$ctrl.userCanCreate()"
			             click="$ctrl.onFlowsheetNew()">
				New Flowsheet
			</juno-button>
		</div>
	</div>

	<juno-security-check show-placeholder="true" permissions="$ctrl.SecurityPermissions.FLOWSHEET_READ">
		<div ng-repeat="flowsheetTable in $ctrl.tablesConfig"
		     ng-if="flowsheetTable.visible"
		     class="flowsheets-list">
			<span>{{flowsheetTable.name}}</span>
			<table ng-table="$ctrl.tableParams" class="table table-striped table-bordered">
				<tbody>
				<tr ng-repeat="flowsheet in flowsheetTable.items">
					<td data-title="'Flowsheet'">
						{{flowsheet.name}}
					</td>
					<td data-title="'Description'">
						{{flowsheet.description}}
					</td>
					<td class="action-buttons">
						<div class="row">
							<div class="col-md-4">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
								             disabled="$ctrl.isLoading || !$ctrl.userCanEdit()"
								             click="$ctrl.onToggleFlowsheetEnabled(flowsheet)">
									{{$ctrl.toggleFlowsheetEnabledLabel(flowsheet)}}
								</juno-button>
							</div>
							<div class="col-md-4">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
								             disabled="$ctrl.isLoading || !$ctrl.userCanEdit()"
								             click="$ctrl.onFlowsheetEdit(flowsheet)">
									Edit
								</juno-button>
							</div>
							<div class="col-md-4">
								<juno-button component-style="$ctrl.componentStyle"
								             button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
								             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
								             disabled="$ctrl.isLoading || !$ctrl.userCanDelete()"
								             click="$ctrl.onFlowsheetDelete(flowsheet)">
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