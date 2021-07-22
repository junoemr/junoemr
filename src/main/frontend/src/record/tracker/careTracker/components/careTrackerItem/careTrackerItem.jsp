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
<div class="care-tracker-item">
	<div class="flex-row align-items-center">
		<h6>{{$ctrl.model.name}} ({{$ctrl.model.typeCode}})</h6>
		<div class="item-description">{{$ctrl.model.description}}</div>
	</div>
	<div class="graphing-button-container no-print">
		<juno-button component-style="$ctrl.componentStyle"
		             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
		             disabled="!$ctrl.isGraphable()"
		             click="$ctrl.onShowDataGraph()">
			Graph
		</juno-button>
	</div>
	<div class="item-guideline">Guideline: {{$ctrl.model.guideline}}</div>

	<div ng-repeat="alert in $ctrl.validationAlerts">
		<div class="alert alert-danger" role="alert">
			{{alert.message}}
		</div>
	</div>

	<div ng-repeat="alert in $ctrl.model.careTrackerItemAlerts">
		<div class="alert" ng-class="$ctrl.getAlertClass(alert.severityLevel)" role="alert">
			{{alert.message}}
		</div>
	</div>

	<div class="flex-row flex-wrap flex-gap-4 justify-content-end new-data-input">
		<div class="flex-row flex-grow align-items-center">
			<juno-check-box ng-if="$ctrl.showValueBooleanInput()"
			                label="{{$ctrl.getInputLabel()}}"
			                change="$ctrl.onBooleanValueChange(value)"
			                ng-model="$ctrl.checkboxValue">
			</juno-check-box>
			<span ng-if="$ctrl.showValueBooleanInput()" class="boolean-value-indicator">{{$ctrl.newEntry.value}}</span>
			<juno-input ng-if="$ctrl.showValueTextInput()"
			            label="{{$ctrl.getInputLabel()}}"
			            ng-model="$ctrl.newEntry.value"
			            only-numeric="$ctrl.model.valueTypeIsNumeric()">
			</juno-input>
			<juno-date-select
					ng-if="$ctrl.showValueDateInput()"
					label="{{$ctrl.getInputLabel()}}"
					ng-model="$ctrl.dateValue"
					change="$ctrl.onDateChangeValue(value)">
			</juno-date-select>
			{{$ctrl.dateValue}}
		</div>
		<div class="flex-row flex-grow align-items-center">
			<juno-check-box label="Add to Note"
			                ng-model="$ctrl.addToNoteOnSave">
			</juno-check-box>
		</div>
		<div>
			<juno-date-select
					label="Observation Date"
					ng-model="$ctrl.newEntry.observationDateTime">
			</juno-date-select>
		</div>
		<div class="action-button-container">
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
			             disabled="!$ctrl.canSubmitItem()"
			             click="$ctrl.saveAndAddToNote()">
				Add
			</juno-button>
		</div>
	</div>
	<div class="flex-row row-margin flex-wrap data-container">
		<care-tracker-item-data ng-repeat="data in $ctrl.model.data | filter:$ctrl.showData"
		                     class="column-margin"
		                     model="data">
		</care-tracker-item-data>
	</div>
</div>