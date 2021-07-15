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
	<div class="flex-column">
		<h3>{{$ctrl.flowsheet.name}}</h3>
		<span>{{$ctrl.flowsheet.description}}</span>
	</div>

	<juno-security-check show-placeholder="true" permissions="[$ctrl.SecurityPermissions.FLOWSHEET_READ, $ctrl.SecurityPermissions.MEASUREMENT_READ]">
		<filter-panel initial-state-expanded="false">
			<div class="filter-wrapper">
				<h6>Filter Items</h6>
				<div class="row">
					<div class="col-md-2">
						<juno-check-box label="Show Hidden Items"
						                label-position="$ctrl.LABEL_POSITION.TOP"
						                ng-model="$ctrl.filter.item.showHidden">
						</juno-check-box>
					</div>
					<div class="col-md-10">
						<juno-input label="Search Items"
						            label-position="$ctrl.LABEL_POSITION.TOP"
						            ng-model="$ctrl.filter.item.textFilter">
						</juno-input>
					</div>
				</div>
				<h6>Filter Data</h6>
				<div class="row">
					<div class="col-md-2">
						<juno-select label="Most recent entries"
						             label-position="$ctrl.LABEL_POSITION.TOP"
						             ng-model="$ctrl.filter.data.maxEntries"
						             options="$ctrl.filterOptions.dataMaxOptions">
						</juno-select>
					</div>
					<div class="col-md-4">
						<juno-date-select label="After Date"
						                  label-position="$ctrl.LABEL_POSITION.TOP"
						                  ng-model="$ctrl.filter.data.afterDate">
						</juno-date-select>
					</div>
					<div class="col-md-4">
						<juno-date-select label="Before Date"
						                  label-position="$ctrl.LABEL_POSITION.TOP"
						                  ng-model="$ctrl.filter.data.beforeDate">
						</juno-date-select>
					</div>
					<div class="col-md-2">
						<juno-button component-style="$ctrl.componentStyle"
						             label="&nbsp"
						             label-position="$ctrl.LABEL_POSITION.TOP"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
						             click="$ctrl.clearFilters()">
							Clear Filters
						</juno-button>
					</div>
				</div>
			</div>
		</filter-panel>

		<flowsheet-item-group ng-repeat="itemGroup in $ctrl.flowsheet.flowsheetItemGroups | filter:$ctrl.showFlowsheetGroup"
		                      model="itemGroup">
			<div ng-repeat="item in itemGroup.flowsheetItems | filter:$ctrl.showFlowsheetItem">
				<div class="item-divider" ng-if="!$first"></div>
				<flowsheet-item
						flowsheet-id="$ctrl.flowsheet.id"
						demographic-id="$ctrl.demographicId"
						model="item"
						filter-date-after="$ctrl.filter.data.afterDate"
						filter-date-before="$ctrl.filter.data.beforeDate"
						filter-max-entries="$ctrl.filter.data.maxEntries">
				</flowsheet-item>
			</div>
		</flowsheet-item-group>
	</juno-security-check>
</div>