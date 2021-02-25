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
<div id="admin-demographic-export">
	<panel id="export-panel"
	       component-style="$ctrl.componentStyle">
		<panel-header>
			<h6 class="juno-text">Demographic Export</h6>
		</panel-header>
		<panel-body>
			<div class="row">
				<div class="col-md-12">
					<%--	export type --%>
					<juno-select ng-model="$ctrl.selectedExportType"
					             options="$ctrl.exportTypeOptions"
					             label="Export Type"
					             label-position="LABEL_POSITION.TOP"
					             component-style="$ctrl.componentStyle"
					></juno-select>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<juno-select ng-model="$ctrl.selectedSet"
					             options="$ctrl.demographicSetOptions"
					             label="Patient Set"
					             label-position="LABEL_POSITION.TOP"
					             component-style="$ctrl.componentStyle"
					></juno-select>
				</div>
			</div>
			<div class="row" ng-if="$ctrl.demographicSetOptions.length === 0">
				<div class="col-md-12">
					<p class="notice">A demographic set is required to export patients</p>
				</div>
			</div>
			<div class="row">
				<div class="col-md-2 pull-right">
					<juno-button ng-click="$ctrl.onSelectAll()"
					             disabled="!$ctrl.canRunExport()"
					             button-color="JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern=JUNO_BUTTON_COLOR_PATTERN.DEFAULT;
					>Select All</juno-button>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exPersonalHistory"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Personal History"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exFamilyHistory"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Family History"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exPastHealth"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Past Health"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exProblemList"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Family History"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exRiskFactors"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Risk Factors"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exAllergiesAndAdverseReactions"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Allergies"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exMedicationsAndTreatments"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Medications"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
				</div>
				<div class="col-md-6">
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exImmunizations"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Preventions/Immunizations"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exLaboratoryResults"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Lab Results"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exAppointments"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Appointments"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exClinicalNotes"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Chart Notes"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exReportsReceived"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Reports"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exAlertsAndSpecialNeeds"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Alerts and Special Needs"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
					<juno-check-box ng-model="$ctrl.exportToggleOptions.exCareElements"
					                disabled="!$ctrl.canRunExport()"
					                label="Export Care Elements"
					                label-position="LABEL_POSITION.LEFT"
					                component-style="$ctrl.componentStyle"
					                class="export-label"
					></juno-check-box>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<juno-button ng-click="$ctrl.onExport()"
					             disabled="!$ctrl.canRunExport()"
					             button-color="JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern = JUNO_BUTTON_COLOR_PATTERN.FILL;>
						Run Export
					</juno-button>
				</div>
			</div>
		</panel-body>
	</panel>
</div>