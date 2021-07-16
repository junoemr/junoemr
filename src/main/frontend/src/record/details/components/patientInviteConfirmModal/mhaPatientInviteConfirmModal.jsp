<juno-modal class="mha-patient-details-modal" component-style="$ctrl.resolve.style">

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title class="patient-title">
		<div class="header-start">
			<h3>Confirm Patient Invite</h3>
		</div>
	</modal-title>

	<modal-body ng-class="$ctrl.getComponentClasses()">
		<div class="confirm-content">
			<h3 class="confirm-message juno-text">
				Are you sure you want to send an invite to this patient?
			</h3>
			<span>{{$ctrl.demographicEmail}}</span>
			<juno-select ng-if="$ctrl.isMultisiteEnabled && !$ctrl.hideIntegrationSelect"
			             component-style="$ctrl.resolve.style"
			             label="Site"
			             label-position="$ctrl.LABEL_POSITION.TOP"
			             options="$ctrl.integrationsList"
			             ng-model="$ctrl.selectedIntegrationId">
			</juno-select>
		</div>
	</modal-body>

	<modal-footer>
		<div class="row">
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
				             ng-click="$ctrl.sendPatientInvite()"
				             disabled="$ctrl.sendDisabled()"
				             class="cancel-connection-btn">
					Send Invite
				</juno-button>
			</div>
		</div>
	</modal-footer>
</juno-modal>