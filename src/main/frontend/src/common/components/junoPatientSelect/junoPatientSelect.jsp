<div class="juno-patient-select" ng-class="$ctrl.componentClasses()">
	<div>
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
		>
		</juno-typeahead>
	</div>
	<div>
		<label>Demographic</label>
		<demographic-card ng-if="$ctrl.showPatientCard" demographic-model="$ctrl.ngModel">
	</div>
	</demographic-card>
</div>