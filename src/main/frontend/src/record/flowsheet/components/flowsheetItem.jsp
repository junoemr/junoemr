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
<div class="flowsheet-item">
	<h6>{{$ctrl.model.name}} ({{$ctrl.model.typeCode}})</h6>
	<div>{{$ctrl.model.description}}</div>

	<div ng-repeat="alert in $ctrl.validationAlerts">
		<div class="alert alert-danger" role="alert">
			{{alert.validationFailMessage}}
		</div>
	</div>

	<div ng-repeat="alert in $ctrl.model.flowsheetItemAlerts">
		<div class="alert" ng-class="$ctrl.getAlertClass(alert.strength)" role="alert">
			{{alert.message}}
		</div>
	</div>

	<div class="flex-row">
		<div class="flex-grow">
			<juno-check-box ng-if="$ctrl.valueIsBoolean()"
			                label="{{$ctrl.getInputLabel()}}"
			                ng-model="$ctrl.newEntry.value">
			</juno-check-box>
			<juno-input ng-if="!$ctrl.valueIsBoolean()"
			            label="{{$ctrl.getInputLabel()}}"
			            ng-model="$ctrl.newEntry.value"
			            only-numeric="$ctrl.valueIsNumeric()">
			</juno-input>
		</div>
		<div class="action-button-container">
			<juno-button component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
			             click="$ctrl.validateAndSubmit()">
				Submit
			</juno-button>
		</div>
	</div>
</div>