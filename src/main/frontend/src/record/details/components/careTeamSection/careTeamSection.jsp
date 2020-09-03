<div class="demographic-details-section care-team-section">
	<h4 class="title">
		Care Team
	</h4>

	<div class="fields">
		<div class="column">
			<!-- MRP -->
			<juno-select ng-model="$ctrl.ngModel.providerNo"
							options="$ctrl.doctors"
							label="MRP"
							component-style="$ctrl.componentStyle">
			</juno-select>

			<!-- Midwife -->
			<juno-select ng-model="$ctrl.ngModel.midwife"
							options="$ctrl.midwives"
							label="Midwife"
							component-style="$ctrl.componentStyle">
			</juno-select>

			<!-- Referral Doctor -->
			<juno-typeahead model="$ctrl.ngModel.scrReferralDoc"
							options="$ctrl.referralDoctors"
							filter-options="false"
							name="ReferralDoctor"
							title="Referral Doctor"
							placeholder="Referral Doctor"
							label-position="LABEL_POSITION.LEFT"
							on-change="$ctrl.updateReferralDoctors(value)"
							on-selected="$ctrl.updateReferralNo(value)"
							component-style="$ctrl.componentStyle">
			</juno-typeahead>

			<!-- Roster Status -->
			<div class="select-with-button">
				<juno-select ng-model="$ctrl.ngModel.rosterStatus"
								options="$ctrl.rosterStatusList"
								label="Roster Status"
								component-style="$ctrl.componentStyle">
				</juno-select>

				<juno-button ng-click="$ctrl.openAddRosterStatusModal()"
				             button-color="JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
					Add
				</juno-button>

			</div>

			<!-- Termination Date -->
			<juno-date-select ng-if="$ctrl.ngModel.rosterStatus === 'TE'"
							ng-model="$ctrl.ngModel.rosterTerminationDate"
							label="Termination Date"
							on-validity-change="$ctrl.terminationDateValid = valid"
							component-style="$ctrl.componentStyle">
			</juno-date-select>

			<!--- Patient Status -->
			<div class="select-with-button">
				<juno-select ng-model="$ctrl.ngModel.patientStatus"
								options="$ctrl.patientStatusList"
								label="Patient Status"
								component-style="$ctrl.componentStyle">
				</juno-select>

				<juno-button ng-click="$ctrl.openAddPatientStatusModal()"
				             button-color="JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
					Add
				</juno-button>
			</div>

			<!-- Date Joined -->
			<juno-date-select ng-model="$ctrl.ngModel.dateJoined"
							label="Date Joined"
							on-validity-change="$ctrl.dateJoinedValid = valid"
							component-style="$ctrl.componentStyle">
			</juno-date-select>

			<!-- Chart Number-->
			<juno-input ng-model="$ctrl.ngModel.chartNo"
							label="Chart Number"
							placeholder="Chart Number"
							component-style="$ctrl.componentStyle">
			</juno-input>

		</div>

		<div class="divider">
		</div>

		<div class="column">
			<!-- Nurse -->
			<juno-select ng-model="$ctrl.ngModel.nurse"
							options="$ctrl.nurses"
							label="Nurse"
							component-style="$ctrl.componentStyle">
			</juno-select>

			<!-- Resident -->
			<juno-select ng-model="$ctrl.ngModel.resident"
							options="$ctrl.doctors"
							label="Resident"
							component-style="$ctrl.componentStyle">
			</juno-select>

			<!-- Referral Doctor Number -->
			<juno-input ng-model="$ctrl.ngModel.scrReferralDocNo"
							label="Referral Doctor #"
							placeholder="Referral Doctor #"
							valid-regex="$ctrl.numberRegex"
							component-style="$ctrl.componentStyle">
			</juno-input>

			<!-- Date Rostered -->
			<juno-date-select ng-model="$ctrl.ngModel.rosterDate"
							label="Roster Date"
							on-validity-change="$ctrl.rosterDateValid = valid"
							component-style="$ctrl.componentStyle">
			</juno-date-select>

			<!-- Termination Reason -->
			<juno-select ng-if="$ctrl.ngModel.rosterStatus === 'TE'"
							ng-model="$ctrl.ngModel.rosterTerminationReason"
							options="$ctrl.rosterTermReasons"
							label="Termination Reason"
							component-style="$ctrl.componentStyle">
			</juno-select>

			<!-- Patient Status Date -->
			<juno-date-select ng-model="$ctrl.ngModel.patientStatusDate"
							label="Patient Status Date"
							on-validity-change="$ctrl.patientStatusDateValid = valid"
							component-style="$ctrl.componentStyle">
			</juno-date-select>

			<!-- End Date -->
			<juno-date-select ng-model="$ctrl.ngModel.endDate"
							label="End Date"
							on-validity-change="$ctrl.endDateValid = valid"
							component-style="$ctrl.componentStyle">
			</juno-date-select>

			<!-- Cytology Number-->
			<juno-input ng-model="$ctrl.ngModel.scrCytolNum"
							label="Cytology #"
							placeholder="Cytology #"
							valid-regex="$ctrl.numberRegex"
							component-style="$ctrl.componentStyle">
			</juno-input>

		</div>

	</div>
</div>