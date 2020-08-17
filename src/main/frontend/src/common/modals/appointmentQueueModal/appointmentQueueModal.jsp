<juno-modal class="appointment-queue-modal" component-style="$ctrl.resolve.style">

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title>
		<h3>Appointment Queue Settings</h3>
	</modal-title>

	<modal-body>
		<div class="queue-options-container">
			<div class="options-common">
				<juno-input
						label="Queue Name"
						ng-model="$ctrl.queueModel.queueName"
						component-style="$ctrl.resolve.style">
				</juno-input>

				<juno-input
						label="Queue Limit"
						ng-model="$ctrl.queueModel.queueLimit"
						valid-regex="$ctrl.numberRegex"
						component-style="$ctrl.resolve.style">
				</juno-input>
			</div>
			<div class="options-availability">
				<availability-settings settings-model="$ctrl.queueModel.availabilitySettings"
				                       component-style="$ctrl.componentStyle">
				</availability-settings>
			</div>
		</div>
	</modal-body>

	<modal-footer>
		<div class="row footer-wrapper">
			<div class="col-md-6">
				<juno-button component-style="$ctrl.resolve.style"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
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
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
				             ng-click="$ctrl.onSave()"
				             disabled="$ctrl.saveDisabled()"
				             class="cancel-connection-btn">
					Save
				</juno-button>
			</div>
		</div>
	</modal-footer>

</juno-modal>