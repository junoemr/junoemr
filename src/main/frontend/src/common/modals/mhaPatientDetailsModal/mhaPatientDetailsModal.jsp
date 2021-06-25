<juno-simple-modal class="mha-patient-details-modal"
                   modal-height="556"
                   modal-width="1052"
                   modal-instance="$ctrl.modalInstance"
                   fill-color="JUNO_SIMPLE_MODAL_FILL_COLOR.GREY"
                   component-style="$ctrl.resolve.style">


	<div class="flex-col h-100 w-100">
		<h5 class="m-t-0 m-b-16">Patient Verification & Connection Status</h5>

		<juno-tab ng-model="$ctrl.currentIntegration"
		          tabs="$ctrl.integrationOptions"
		          type="JUNO_TAB_TYPE.FOLDER_TAB"
		          component-style="$ctrl.resolve.style">

		</juno-tab>

		<div class="patient-details-container flex-gap-8">
			<div class="h-100">
				<mha-patient-profile-details class="d-inline-block h-100 w-100"
				                             profile="$ctrl.currentProfile">
				</mha-patient-profile-details>
			</div>
			<div class="h-100">
				<mha-patient-connection-details class="d-inline-block h-100 w-100"
				                                profile="$ctrl.currentProfile"
				                                integration="$ctrl.currentIntegration"
				                                demographic-no="$ctrl.demographic.demographicNo"
				                                disabled="$ctrl.isLoadingProfile"
				                                on-connection-updated="$ctrl.onConnectionStatusUpdated()">
				</mha-patient-connection-details>
			</div>
		</div>

		<div class="patient-details-button-row flex-row justify-content-space-between">
			<div class="flex-row justify-content-start align-items-center">
				<juno-button ng-if="!$ctrl.currentProfile.isRejected"
				             class="flex-item-no-grow w-128 m-t-8"
				             button-color="JUNO_BUTTON_COLOR.DANGER"
				             click="$ctrl.rejectConnection()"
				             disabled="!$ctrl.currentProfile"
				             component-style="$ctrl.resolve.style">
					Reject Connection
				</juno-button>
				<juno-button ng-if="$ctrl.currentProfile.isRejected"
				             class="cancel-reject-button flex-item-no-grow m-t-8"
				             click="$ctrl.cancelRejectConnection()"
				             disabled="!$ctrl.currentProfile"
				             component-style="$ctrl.resolve.style">
					Cancel Reject Connection
				</juno-button>
			</div>
			<div class="flex-row justify-content-end align-items-center">
				<juno-button class="flex-item-no-grow w-128 m-t-8"
				             click="$ctrl.openInviteConfirmModal()"
				             disabled="$ctrl.currentProfile || $ctrl.isLoadingProfile"
				             component-style="$ctrl.resolve.style">
					{{$ctrl.getInviteButtonText()}}
				</juno-button>
			</div>
		</div>
	</div>

</juno-simple-modal>