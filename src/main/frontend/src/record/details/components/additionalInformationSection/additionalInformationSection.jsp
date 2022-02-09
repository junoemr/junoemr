<div class="demographic-details-section additional-information-section">
	<h4 class="title">
		Additional Information
	</h4>

	<div class="details-fields">

		<!-- Waiting List -->
		<juno-select ng-model="$ctrl.ngModel.waitingListID"
						options="$ctrl.waitingListNames"
						label="Waiting List"
						placeholder="Waiting List"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<!-- Waiting list date -->
		<juno-date-select ng-model="$ctrl.ngModel.onWaitingListSinceDate"
		                  label="Date of request"
		                  on-validity-change="$ctrl.dateOfRequestValid = valid"
		                  component-style="$ctrl.componentStyle">
		</juno-date-select>

		<!-- Archived Paper Chart -->
		<juno-select ng-model="$ctrl.ngModel.paperChartArchived"
						options="$ctrl.archivedChartOptions"
						label="Archived Paper Chart"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<!-- Waiting List Note -->
		<juno-input ng-model="$ctrl.ngModel.waitingListNote"
		            label="Waiting List Note"
		            placeholder="Waiting List Note"
		            component-style="$ctrl.componentStyle">
		</juno-input>

		<!-- Privacy Consent-->
		<juno-input ng-model="$ctrl.ngModel.privacyConsent"
						label="Privacy Consent"
						placeholder="Privacy Consent"
						component-style="$ctrl.componentStyle">
		</juno-input>

		<!-- Informed Consent-->
		<juno-input ng-model="$ctrl.ngModel.informedConsent"
		            label="Informed Consent"
		            placeholder="Informed Consent"
		            component-style="$ctrl.componentStyle">
		</juno-input>

		<!-- US Consent-->
		<juno-input ng-model="$ctrl.ngModel.usSigned"
						label="US Resident Consent"
						placeholder="US Resident Consent"
						component-style="$ctrl.componentStyle">
		</juno-input>

		<!-- Rx Interaction Level -->
		<juno-select ng-model="$ctrl.ngModel.rxInteractionWarningLevel"
		             options="$ctrl.rxInteractionLevels"
		             label="Rx Interaction Level"
		             component-style="$ctrl.componentStyle">
		</juno-select>

		<!-- Security Question -->
		<juno-select ng-model="$ctrl.ngModel.securityQuestion1"
						options="$ctrl.securityQuestions"
						label="Security Question"
						placeholder="Select Security Question"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<!-- Security Answer -->
		<juno-input ng-model="$ctrl.ngModel.securityAnswer1"
						label="Security Answer"
						placeholder="Answer"
						component-style="$ctrl.componentStyle">
		</juno-input>
	</div>
</div>