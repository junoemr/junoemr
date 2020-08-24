<juno-simple-modal class="add-queued-appointment-modal"
									 component-style="$ctrl.resolve.style"
									 modal-instance="$ctrl.modalInstance">

	<div class="content">
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
							on-selected="$ctrl.onProviderSelectChange(value.value)"
							label-position="LABEL_POSITION.TOP"
							component-style="$ctrl.resolve.style">
			</juno-typeahead>
		</div>

		<div ng-if="$ctrl.isMultisiteEnabled" class="site-select">
			<juno-select ng-model="$ctrl.siteSelection"
									 label="Select Site"
									 placeholder="Select Site"
									 options="$ctrl.siteOptions"
									 component-style="$ctrl.resolve.style">
			</juno-select>
		</div>
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
								 disabled="!$ctrl.bookProviderNo || (!$ctrl.siteSelection && $ctrl.isMultisiteEnabled) || $ctrl.isLoading">
			Assign To Selected
		</juno-button>
	</div>

</juno-simple-modal>