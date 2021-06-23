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
				                                integration="$ctrl.currentIntegration">
				</mha-patient-connection-details>
			</div>
		</div>

		<div class="patient-details-button-row flex-row justify-content-end align-items-center">
			<juno-button class="flex-item-no-grow w-128 m-t-8"
			             ng-click="$ctrl.onCancel()"
			             component-style="$ctrl.resolve.style">
				Close Window
			</juno-button>
		</div>
	</div>

</juno-simple-modal>