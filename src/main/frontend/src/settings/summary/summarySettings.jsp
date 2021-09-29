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
<div class="row" id="summary">
	<div class="col-md-7">
		<div class="col-sm-12">
			<h3>Notes</h3>
			<hr>
		</div>
		<div class="form-group col-sm-12">
			<label>CPP Single Line</label>
			<div class="controls">
				<label class="radio-inline" for="radios-enc-0">
					<input name="radios-enc-0" id="radios-enc-0" ng-model="$ctrl.pref.cppSingleLine" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="radios-enc-1">
					<input name="radios-enc-1" id="radios-enc-1" ng-model="$ctrl.pref.cppSingleLine" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>
		<div class="form-group col-sm-12">
			<label>Use Single View</label>
			<div class="controls">
				<label class="radio-inline" for="radios-enc-2">
					<input name="radios-enc-2" id="radios-enc-2" ng-model="$ctrl.pref.cmeNoteFormat" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="radios-enc-3">
					<input name="radios-enc-3" id="radios-enc-3" ng-model="$ctrl.pref.cmeNoteFormat" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>

		<div class="form-group col-sm-12">
			<label>Stale Date</label>
			<small><em>Please set how many months in the past before a Case Management Note is fully visible e.g. Set to 6 will display fully all notes within the last 6 months</em></small>
			<select ng-model="$ctrl.pref.cmeNoteDate" class="form-control" ng-options="p.value as p.label for p in $ctrl.staleDates">
			</select>
		</div>

		<div class="form-group col-sm-12">
			<label>Default Quick Chart Size</label>
			<small><em>Enter the number of notes for quick chart size.</em></small>
			<input ng-model="$ctrl.pref.quickChartSize" class="form-control" type="text">
		</div>
		<div class="col-sm-12">
			<h3>Patient Summary Viewable Items</h3>
			<hr>
		</div>

		<div class="form-group col-sm-12" id="summary-items">
			<label>Enable Custom Summary</label>
			<small><em>Enabling this feature will allow you to to hide or display CPP and Summary Items.</em></small>
			<div class="controls">
				<label class="radio-inline" for="radios-enc-4">
					<input name="radios-enc-4" id="radios-enc-4" ng-model="$ctrl.pref.summaryItemCustomDisplay" ng-value="true" type="radio">
					Enable
				</label>
				<label class="radio-inline" for="radios-enc-5">
					<input name="radios-enc-5" id="radios-enc-5" ng-model="$ctrl.pref.summaryItemCustomDisplay" ng-value="false" type="radio">
					Disable
				</label>
			</div>
		</div>
	</div>
	<patient-summary-items
			ng-if="$ctrl.pref.summaryItemCustomDisplay"
			pref="$ctrl.pref"
	></patient-summary-items>
	<div class="col-md-3 pull-right col-xs-12">
		<h4>Classic Encounter Preferences:</h4>
		<div class="well">
			<a href="javascript:void(0)" ng-click="$ctrl.showDefaultEncounterWindowSizePopup()">Set Default Encounter Window Size</a><br>
			<a href="javascript:void(0)" ng-click="$ctrl.showProviderColourPopup()">Set Provider Colour</a><br>
			<a href="javascript:void(0)" ng-click="$ctrl.openConfigureEChartCppPopup()">Configure EChart CPP</a><br>
		</div>
	</div>
	<!-- container -->
</div>


