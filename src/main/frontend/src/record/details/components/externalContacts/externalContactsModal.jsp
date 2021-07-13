<div class="demographic-details-section ">
	<h4 class="title">
		Rostering/Enrollment
	</h4>

	<div class="fields">
		<div class="column">
			<!-- Family Doctor -->
			<juno-typeahead model="$ctrl.ngModel.scrFamilyDoc"
			                options="$ctrl.familyDoctors"
			                filter-options="false"
			                name="FamilyDoctor"
			                title="Family/Enrolled Doctor"
			                placeholder="Family Doctor"
			                label-position="LABEL_POSITION.LEFT"
			                on-change="$ctrl.updateFamilyDoctors(value)"
			                on-selected="$ctrl.updateFamilyDocNo(value)"
			                component-style="$ctrl.componentStyle">
			</juno-typeahead>
			<!-- Roster Status -->
			<juno-select ng-model="$ctrl.ngModel.rosterStatus"
			             options="$ctrl.rosterStatusList"
			             label="Roster Status"
			             component-style="$ctrl.componentStyle">
			</juno-select>
			<!-- Termination Reason -->
			<juno-select ng-if="$ctrl.ngModel.rosterStatus === 'TE'"
			             ng-model="$ctrl.ngModel.rosterTerminationReason"
			             options="$ctrl.rosterTermReasons"
			             invalid="!$ctrl.validations.rosterTerminationReason()"
			             label="Termination Reason"
			             component-style="$ctrl.componentStyle">
			</juno-select>
		</div>

		<div class="divider">
		</div>

		<div class="column">
			<!-- Family Doctor Number -->
			<juno-input
					ng-model="$ctrl.ngModel.scrFamilyDocNo"
					label="Family Doctor #"
					placeholder="Family Doctor #"
					valid-regex="$ctrl.numberRegex"
					component-style="$ctrl.componentStyle">
			</juno-input>
			<!-- Date Rostered -->
			<juno-date-select ng-model="$ctrl.ngModel.rosterDate"
			                  label="Roster Date"
			                  on-validity-change="$ctrl.rosterDateValid = valid"
			                  component-style="$ctrl.componentStyle">
			</juno-date-select>
			<!-- Termination Date -->
			<juno-date-select ng-if="$ctrl.ngModel.rosterStatus === 'TE'"
			                  ng-model="$ctrl.ngModel.rosterTerminationDate"
			                  label="Termination Date"
			                  on-validity-change="$ctrl.terminationDateValid = valid"
			                  component-style="$ctrl.componentStyle">
			</juno-date-select>
		</div>

	</div>
	<div class="fields">
		<div class="">
			<juno-button class="rostered-history-button"
			             ng-click="$ctrl.openRosteredHistoryModal()"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
				View Enrollment History
			</juno-button>
		</div>
	</div>
</div>
