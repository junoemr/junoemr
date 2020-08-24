<juno-simple-modal class="add-queued-appointment-modal"
									 component-style="$ctrl.resolve.style"
									 modal-instance="$ctrl.modalInstance">

	<h5 class="title juno-text-primary">Move Queued Appointment to Schedule </h5>

	<p class="juno-text">
		Select which schedule you want to move the queued appointment to.
	</p>

	<div class="center-options flex-row align-items-flex-end">
		<juno-button ng-click="$ctrl.assignToMe()"
								 component-style="$ctrl.resolve.style"
								 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
			Assign To Me
		</juno-button>
		<juno-typeahead
						name="Provider"
						title="Provider Selection"
						model="$ctrl.bookProviderNo"
						options="$ctrl.providerOptions"
						placeholder="Or assign to provider"
						label-position="LABEL_POSITION.TOP"
						component-style="$ctrl.resolve.style">
		</juno-typeahead>
	</div>

	<juno-divider component-style="$ctrl.resolve.style"
								slim="true">
	</juno-divider>

	<div class="buttons">
		<juno-button ng-click="$ctrl.onCancel()"
								 component-style="$ctrl.resolve.style"
								 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT">
			Cancel
		</juno-button>
		<juno-button ng-click="$ctrl.bookQueuedAppointment()"
								 component-style="$ctrl.resolve.style"
								 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
								 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
								 disabled="!$ctrl.bookProviderNo">
			Assign To Selected
		</juno-button>
	</div>

</juno-simple-modal>