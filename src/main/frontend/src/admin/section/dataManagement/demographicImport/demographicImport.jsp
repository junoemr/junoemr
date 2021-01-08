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
	<panel id="cds-import-panel"
	       component-style="$ctrl.componentStyle">
		<panel-header>
			<h6>CDS Import</h6>
		</panel-header>
		<panel-body>
			<div class="row">
				<div class="col-md-12">
					<juno-file-chooser label="Choose Import File"
					                   label-position="LABEL_POSITION.TOP"
					                   component-style="$ctrl.componentStyle"
					                   placeholder="Choose Import File"
					                   change="$ctrl.onFileSelected(files)"
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
					<juno-button ng-click="$ctrl.onRunImport()"
					             button-color="JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern = JUNO_BUTTON_COLOR_PATTERN.FILL;>
						Run Import
					</juno-button>
				</div>
			</div>
		</panel-body>
	</panel>
</div>