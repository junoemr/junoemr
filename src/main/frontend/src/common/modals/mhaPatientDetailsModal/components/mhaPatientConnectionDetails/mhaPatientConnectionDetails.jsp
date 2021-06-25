<div class="mha-patient-connection-details w-100 h-100">
	<div class="grid-row-2 w-100 h-100">
		<!-- Confirmation -->
		<div>
			<div class="connection-status-grid-layout grid-row-gap-24">
				<!-- Confirmation Status -->
				<div class="text-grey text-right">Confirmation Status:</div>
				<div class="p-l-24">{{$ctrl.profile.isConfirmed ? "Patient Has Been Confirmed" : "Patient Has Not Been Confirmed"}}</div>
				<div class="m-r-8">
					<juno-button ng-if="!$ctrl.profile.isConfirmed"
					             click="$ctrl.confirm()"
					             disabled="$ctrl.disabled || !$ctrl.profile"
					             title="{{!$ctrl.profile ? 'No MHA patient connected to this demographic.' : ''}}"
					             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
						Confirm Patient
					</juno-button>
					<juno-button ng-if="$ctrl.profile.isConfirmed"
					             disabled="$ctrl.disabled"
					             click="$ctrl.cancelConfirmation()">
						Cancel Confirmation
					</juno-button>
				</div>

				<!-- Confirmed On -->
				<div class="text-grey text-right">Confirmed On:</div>
				<div class="p-l-24">{{$ctrl.profile.isConfirmed ? $ctrl.formatStatusDate($ctrl.patientAccess.confirmedAt) : ''}}</div>
				<div></div>

				<!-- Confirmed By -->
				<div class="text-grey text-right">Confirmed By:</div>
				<div class="p-l-24">{{$ctrl.profile.isConfirmed ? $ctrl.patientAccess.confirmingUserName : ''}}</div>
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
					<juno-button ng-if="!$ctrl.profile.isVerified && !$ctrl.verifying"
					             click="$ctrl.startVerification()"
					             disabled="$ctrl.disabled"
					             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
						Verify Patient
					</juno-button>
					<juno-button ng-if="$ctrl.profile.isVerified && !$ctrl.verifying"
					             disabled="$ctrl.disabled"
					             click="$ctrl.cancelVerification()">
						Cancel Verification
					</juno-button>

					<juno-button ng-if="$ctrl.verifying && !$ctrl.verificationProfile"
					             disabled="$ctrl.disabled"
					             click="$ctrl.stopVerifying()">
						Cancel
					</juno-button>
					<juno-button ng-if="$ctrl.verifying && $ctrl.verificationProfile"
					             disabled="$ctrl.disabled"
					             button-color-pattern="$ctrl.verificationProfile ? JUNO_BUTTON_COLOR_PATTERN.FILL : JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
					             click="$ctrl.confirmVerification()">
						Confirm
					</juno-button>
				</div>

				<!-- Verified On -->
				<div ng-if="!$ctrl.verifying" class="text-grey text-right">Verified On:</div>
				<div ng-if="!$ctrl.verifying" class="p-l-24">{{$ctrl.profile.isVerified ? $ctrl.formatStatusDate($ctrl.patientAccess.verifiedAt) : ''}}</div>
				<div ng-if="!$ctrl.verifying"></div>

				<!-- Verified By -->
				<div ng-if="!$ctrl.verifying" class="text-grey text-right">Verified By:</div>
				<div ng-if="!$ctrl.verifying" class="p-l-24">{{$ctrl.profile.isVerified ? $ctrl.patientAccess.verifierUserName : ''}}</div>
				<div ng-if="!$ctrl.verifying"></div>

				<!-- Verification Code -->
				<div ng-if="$ctrl.verifying" class="verification-code-area">
					<div class="text-grey text-center m-b-8">Enter the 6 digit verification code provided to you by the patient below</div>
					<div class="flex-row justify-content-center align-items-center">
						<div class="spacing-div"></div>
						<juno-code-input ng-model="$ctrl.verificationCode"
						                 code-length="VERIFICATION_CODE_LENGTH"
						                 on-change="$ctrl.onCodeChange(value)">
						</juno-code-input>

						<!-- code status icons -->
						<div ng-if="$ctrl.verificationCode.length !== VERIFICATION_CODE_LENGTH || $ctrl.loadingVerificationProfile" class="spacing-div"></div>
						<i ng-if="$ctrl.verificationProfile && !$ctrl.loadingVerificationProfile && $ctrl.verificationCode.length === VERIFICATION_CODE_LENGTH"
						   title="Code valid"
						   class="icon help primary icon-check"></i>
						<i ng-if="!$ctrl.verificationProfile && !$ctrl.loadingVerificationProfile && $ctrl.verificationCode.length === VERIFICATION_CODE_LENGTH"
						   title="Code invalid or expired"
						   class="icon help danger icon-delete"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>