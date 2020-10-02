<div class="juno-patient-select" ng-class="$ctrl.componentClasses()">
	<div class="flex-row align-items-end header-row">
		<juno-typeahead
						model="$ctrl.demographicNo"
						options="$ctrl.patientOptions"
						title="Patient"
						label-position="LABEL_POSITION.TOP"
						typeahead-min-length="3"
						name="patient"
						on-selected="$ctrl.onDemographicSelected(value)"
						on-change="$ctrl.loadPatientOptions(value)"
						filter-options="false"
						get-options-callback="$ctrl.getPatientOptions(value)"
		>
		</juno-typeahead>

		<juno-button ng-if="$ctrl.showDemographicAdd"
						class="add-demo-button"
						button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
						button-color="JUNO_BUTTON_COLOR.PRIMARY"
						title="New Demographic"
						ng-click="$ctrl.openNewDemographicModal()"
						component-style="$ctrl.resolve.style">
			<i class="icon icon-plus"></i>
		</juno-button>
	</div>
	<div>
		<label>Demographic</label>
		<demographic-card ng-if="$ctrl.showPatientCard" demographic-model="$ctrl.ngModel">
	</div>
	</demographic-card>
</div>