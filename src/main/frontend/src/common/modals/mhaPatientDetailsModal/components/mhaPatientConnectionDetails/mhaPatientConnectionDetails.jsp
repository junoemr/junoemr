<div class="mha-patient-connection-details grid-row-2 w-100 h-100">
	<!-- Confirmation -->
	<div>
		<div class="connection-status-grid-layout grid-row-gap-24">
			<!-- Confirmation Status -->
			<div class="text-grey text-right">Confirmation Status:</div>
			<div class="p-l-24">{{$ctrl.profile.isConfirmed ? "Patient Has Been Confirmed" : "Patient Has Not Been Confirmed"}}</div>
			<div class="m-r-8">
				<juno-button ng-if="!$ctrl.profile.isConfirmed" button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
					Confirm Patient
				</juno-button>
				<juno-button ng-if="$ctrl.profile.isConfirmed">
					Cancel Confirmation
				</juno-button>
			</div>

			<!-- Confirmed On -->
			<div class="text-grey text-right">Confirmed On:</div>
			<div class="p-l-24">{{$ctrl.formatStatusDate($ctrl.patientAccess.confirmedAt)}}</div>
			<div></div>

			<!-- Confirmed By -->
			<div class="text-grey text-right">Confirmed By:</div>
			<div class="p-l-24">{{$ctrl.patientAccess.confirmingUserName}}</div>
			<div></div>
		</div>
	</div>

	<!-- Verification -->
	<div>
		<div class="connection-status-grid-layout grid-row-gap-24">
			<!-- Verification Status -->
			<div class="text-grey text-right">Verification Status:</div>
			<div class="p-l-24">{{$ctrl.profile.isVerified ? "Patient Has Been Verified" : "Patient Has Not Been Verified"}}</div>
			<div class="m-r-8">
				<juno-button ng-if="!$ctrl.profile.isVerified"
				             click="$ctrl.startVerification()"
				             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
					Verify Patient
				</juno-button>
				<juno-button ng-if="$ctrl.profile.isVerified">
					Cancel Verification
				</juno-button>
			</div>

			<!-- Verified On -->
			<div ng-if="!$ctrl.verifying" class="text-grey text-right">Verified On:</div>
			<div ng-if="!$ctrl.verifying" class="p-l-24">{{$ctrl.formatStatusDate($ctrl.patientAccess.verifiedAt)}}</div>
			<div ng-if="!$ctrl.verifying"></div>

			<!-- Verified By -->
			<div ng-if="!$ctrl.verifying" class="text-grey text-right">Verified By:</div>
			<div ng-if="!$ctrl.verifying" class="p-l-24">{{$ctrl.patientAccess.verifierUserName}}</div>
			<div ng-if="!$ctrl.verifying"></div>

			<!-- Verification Code -->
			<div ng-if="$ctrl.verifying" class="verification-code-area">
				<div class="flex-row justify-content-center align-items-center">
					<juno-code-input code-length="6"></juno-code-input>
				</div>
			</div>
		</div>
	</div>

</div>