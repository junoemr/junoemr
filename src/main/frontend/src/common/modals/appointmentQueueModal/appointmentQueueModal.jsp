<juno-modal class="appointment-queue-modal" component-style="$ctrl.resolve.style">

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title>
		<h3>Appointment Queue Settings</h3>
	</modal-title>

	<modal-body>
		<div class="queue-options-container">
			<juno-input
					label="Queue Name"
					ng-model="$ctrl.queueModel.name"
					component-style="$ctrl.resolve.style">
			</juno-input>

			<juno-input
					label="Queue Limit"
					ng-model="$ctrl.queueModel.limit"
					component-style="$ctrl.resolve.style">
			</juno-input>
		</div>
	</modal-body>

	<modal-footer>
		<div class="row">
			<div class="col-md-6">
				<juno-button component-style="$ctrl.resolve.style"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.SECONDARY"
				             ng-click="$ctrl.onCancel()"
				             class="cancel-connection-btn">
					<div class="cancel-btn-text">
						Cancel
					</div>
				</juno-button>
			</div>
			<div class="col-md-6">
				<juno-button component-style="$ctrl.resolve.style"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             ng-click="$ctrl.onSave()"
				             disabled="$ctrl.saveDisabled()"
				             class="cancel-connection-btn">
					Save
				</juno-button>
			</div>
		</div>
	</modal-footer>

</juno-modal>