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
	<div class="flex-row justify-content-between flex-wrap">
		<div class="flex-row align-items-center">
			<h6 class="item-header">{{$ctrl.model.name}} ({{$ctrl.model.typeCode}})</h6>
			<div class="item-description">{{$ctrl.model.description}}</div>
		</div>
		<div ng-if="$ctrl.isGraphable()" class="no-print m-t-2">
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
			             disabled="!$ctrl.model.hasAttachedData()"
			             click="$ctrl.onShowDataGraph()">
				Graph
			</juno-button>
		</div>
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
			<juno-check-box ng-if="$ctrl.model.itemTypeIsPrevention()"
			                label="{{$ctrl.getInputLabel()}}"
			                ng-model="$ctrl.preventionGivenCheck">
			</juno-check-box>

			<div class="flex-row align-items-center flex-gap-16 boolean-checks" ng-if="$ctrl.showValueBooleanInput()">
				<label class="m-b-0">{{$ctrl.getInputLabel()}}</label>
				<div class="flex-row flex-gap-8">
					<juno-check-box label="{{$ctrl.dataTrueValue}}"
					                ng-model="$ctrl.booleanCheckYes"
					                change="$ctrl.onBooleanValueYes(value)">
					</juno-check-box>
					<juno-check-box label="{{$ctrl.dataFalseValue}}"
					                ng-model="$ctrl.booleanCheckNo"
					                change="$ctrl.onBooleanValueNo(value)">
					</juno-check-box>
				</div>
			</div>

			<juno-input ng-if="$ctrl.showValueTextInput()"
			            label="{{$ctrl.getInputLabel()}}"
			            ng-model="$ctrl.newEntry.value"
			            valid-regex="$ctrl.inputRegexRestriction">
			</juno-input>
			<juno-date-picker ng-if="$ctrl.showValueDateInput()"
			                  label="{{$ctrl.getInputLabel()}}"
			                  ng-model="$ctrl.dateValue"
			                  on-change="$ctrl.onDateChangeValue(value)">
			</juno-date-picker>
		</div>
		<div class="flex-row flex-grow align-items-center">
			<juno-check-box label="Add to Note"
			                ng-model="$ctrl.addToNoteOnSave">
			</juno-check-box>
		</div>
		<div>
			<juno-date-picker
					label="Observation Date"
					ng-model="$ctrl.newEntry.observationDateTime">
			</juno-date-picker>
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
	<div class="flex-row flex-wrap flex-gap-4 justify-content-end new-data-input m-t-8">
		<div class="flex-grow">
			<juno-input label="Comment"
			            ng-model="$ctrl.newEntry.comment">
			</juno-input>
		</div>
	</div>
	<div class="flex-row row-margin flex-wrap data-container">
		<care-tracker-item-data ng-repeat="data in $ctrl.model.data | filter:$ctrl.showData"
		                     class="column-margin"
		                     model="data">
		</care-tracker-item-data>
	</div>
</div>