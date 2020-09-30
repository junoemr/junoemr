<juno-modal class="book-queued-appointment-modal" component-style="$ctrl.resolve.style">
	<modal-title>
		<h3 class="title-text"> Queue Appointment </h3>
	</modal-title>

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-body>
		<div class="content">
			<div class="modal-column">
				<!-- Demographic Select -->
				<juno-patient-select
								ng-model="$ctrl.bookingDto.demographic"
								show-patient-card="true"
								component-style="$ctrl.resolve.style">
				</juno-patient-select>
			</div>
			<div class="modal-column">
				<div class="flex-row">
					<!-- Appointment type -->
					<juno-select ng-model="$ctrl.bookingDto.appointmentType"
									placeholder="Appointment Type"
									options="$ctrl.appointmentTypeOptions"
									label="Appointment Type"
									label-position="LABEL_POSITION.TOP"
									on-change="$ctrl.onAppointmentTypeSelected(value)"
									component-style="$ctrl.resolve.style">
					</juno-select>

					<div class="flex-column align-items-flex-end justify-content-end appointment-check-box-container">
						<!-- Appointment Critical -->
						<juno-check-box ng-model="$ctrl.ngModel.critical"
										label="Critical"
										title="Critical"
										component-style="$ctrl.resolve.style">
						</juno-check-box>

						<!-- Appointment Telehealth -->
						<juno-check-box ng-model="$ctrl.ngModel.virtual"
										label="Telehealth"
										title="Telehealth"
										component-style="$ctrl.resolve.style">
						</juno-check-box>
					</div>
				</div>

				<!-- Duration -->
				<juno-input class="appointment-duration"
								label="Duration"
								only-numeric="true"
								label-position="LABEL_POSITION.TOP"
								ng-model="$ctrl.bookingDto.duration"
								component-style="$ctrl.resolve.style">
				</juno-input>

				<div class="flex-row">
					<!-- Reason type -->
					<juno-select ng-model="$ctrl.bookingDto.reasonType"
									placeholder="Reason Type"
									options="$ctrl.reasonTypeOptions"
									label="Reason Type"
									label-position="LABEL_POSITION.TOP"
									component-style="$ctrl.resolve.style">
					</juno-select>

					<!-- Site -->
					<juno-select ng-model="$ctrl.bookingDto.siteId"
									placeholder="Site"
									options="$ctrl.siteOptions"
									label="Site"
									label-position="LABEL_POSITION.TOP"
									component-style="$ctrl.resolve.style"
									disabled="!$ctrl.isMultisiteEnabled">
					</juno-select>
				</div>
			</div>
			<div class="flex-row flex-fill-row justify-content-between bottom-row">
				<!-- Notes -->
				<juno-input label="Notes"
								ng-model="$ctrl.bookingDto.notes"
								character-limit="255"
								label-position="LABEL_POSITION.TOP"
								component-style="$ctrl.resolve.style">
				</juno-input>

				<!-- Reason -->
				<juno-input label="Reason"
								ng-model="$ctrl.bookingDto.reason"
								character-limit="80"
								label-position="LABEL_POSITION.TOP"
								component-style="$ctrl.resolve.style">
				</juno-input>
			</div>
		</div>
	</modal-body>

	<modal-footer>
		<div class="flex-row justify-content-end button-footer-row">
			<juno-button button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
							button-color="JUNO_BUTTON_COLOR.PRIMARY"
							component-style="$ctrl.resolve.style"
							disabled="!$ctrl.canSubmit()">
				Create
			</juno-button>
		</div>
	</modal-footer>
</juno-simple-modal>