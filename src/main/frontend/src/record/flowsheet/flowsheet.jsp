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

<div class="flowsheet-container">
	<h3>{{$ctrl.flowsheet.name}} flowsheet</h3>

	<juno-security-check show-placeholder="true" permissions="[$ctrl.SecurityPermissions.FLOWSHEET_READ, $ctrl.SecurityPermissions.MEASUREMENT_READ]">
		<div>
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
			             click="$ctrl.toHealthTracker()">
				<< Health Tracker
			</juno-button>
		</div>

		<div ng-repeat="itemGroup in $ctrl.flowsheet.flowsheetItemGroups">
			<panel no-header="!itemGroup.name">
				<panel-header>
					<h6>{{itemGroup.name}}</h6>
					<span>{{itemGroup.description}}</span>
				</panel-header>
				<panel-body>
					<div ng-repeat="item in itemGroup.flowsheetItems">
						<flowsheet-item
								flowsheet-id="$ctrl.flowsheet.id"
								demographic-id="$ctrl.demographicId"
								model="item">
						</flowsheet-item>
					</div>
				</panel-body>
			</panel>
		</div>

	</juno-security-check>
</div>