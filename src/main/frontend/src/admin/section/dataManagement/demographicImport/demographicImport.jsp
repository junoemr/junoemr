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
<div id="admin-demographic-import">
	<panel id="import-panel"
	       component-style="$ctrl.componentStyle">
		<panel-header>
			<h6 class="juno-text">Demographic Import</h6>
		</panel-header>
		<panel-body>
			<div class="row">
				<div class="col-md-12">
					<%--	import type --%>
					<juno-select ng-model="$ctrl.selectedImportType"
					             options="$ctrl.importTypeOptions"
					             label="Import Type"
					             label-position="LABEL_POSITION.TOP"
					             component-style="$ctrl.componentStyle"
					></juno-select>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<juno-file-chooser label="Choose Import Files"
					                   label-position="LABEL_POSITION.TOP"
					                   component-style="$ctrl.componentStyle"
					                   placeholder="Choose Import Files"
					                   disabled="$ctrl.importRunning"
					                   change="$ctrl.onFileSelected(files)"
					                   max-size="$ctrl.maxFileSize"
					></juno-file-chooser>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div ng-repeat="file in $ctrl.selectedFiles">
						<span>{{file.name}}</span>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<%--	import source --%>
					<juno-select ng-model="$ctrl.selectedImportSource"
					             options="$ctrl.importSourceOptions"
					             label="Select Import Source"
					             label-position="LABEL_POSITION.TOP"
					             component-style="$ctrl.componentStyle"
					></juno-select>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<p class="juno-text">* Some import sources provide additional data or specific format changes that require customized handling.
						Selecting a specific source will improve the import results.
					</p>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<%--	merge strategy --%>
					<juno-select ng-model="$ctrl.selectedMergeStrategy"
					             options="$ctrl.mergeOptions"
					             label="Select Merge Strategy"
					             label-position="LABEL_POSITION.TOP"
					             component-style="$ctrl.componentStyle"
					></juno-select>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<p class="juno-text">* The merge strategy determines how the importer will handle demographic conflicts.
						A merge conflict will occur if an import record has a matching Health Number and matching date of birth.
						In this case, the merge strategy will determine how the data is imported.
					</p>
					<p class="juno-text notice">
						{{ $ctrl.getSelectedMergeDescription() }}
					</p>
				</div>
			</div>

			<div class="row" ng-if="$ctrl.sitesEnabled">
				<div class="col-md-12">
					<%--	multisite selection --%>
					<juno-select ng-model="$ctrl.selectedSite"
					             options="$ctrl.siteOptions"
					             label="Select Site"
					             label-position="LABEL_POSITION.TOP"
					             component-style="$ctrl.componentStyle"
					></juno-select>
				</div>
			</div>
			<div class="row" ng-if="$ctrl.sitesEnabled">
				<div class="col-md-12">
					<p class="juno-text">* Import data does not record site information,
						so all imported data will be assigned to this site when site assignment is needed.
					</p>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<juno-button ng-click="$ctrl.onRunImport()"
					             disabled="!$ctrl.canRunImport()"
					             button-color="JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern = JUNO_BUTTON_COLOR_PATTERN.FILL;>
						Run Import
					</juno-button>
				</div>
			</div>

			<div class="row" ng-if="$ctrl.results">
				<div class="col-md-12">
					<h6>Summary:</h6>
					<div>Completed: {{$ctrl.results.successCount}}</div>
					<div>Duplicates: {{$ctrl.results.duplicateCount}}</div>
					<div>Errors: {{$ctrl.results.failureCount}}</div>
				</div>
			</div>
			<div class="row" ng-if="$ctrl.results">
				<div class="col-md-12">
					<h6>Messages:</h6>
					<div ng-repeat="message in $ctrl.results.messages track by $index">
						<span>{{message}}</span>
					</div>
				</div>
			</div>
			<div class="row" ng-if="$ctrl.results">
				<div class="col-md-12">
					<juno-button ng-click="$ctrl.onDownloadLogFiles()"
					             disabled="!$ctrl.canDownloadLogs()"
					             button-color="JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern = JUNO_BUTTON_COLOR_PATTERN.DEFAULT;>
						Download Import Logs
					</juno-button>
				</div>
			</div>
		</panel-body>
	</panel>
</div>