<juno-modal class="book-queued-appointment-modal" component-style="$ctrl.resolve.style">
	<modal-title class="title-text">
		<i class="icon icon-day-sheet"></i>
		<h3 class="title-text">
			{{$ctrl.resolve.title}}
		</h3>
	</modal-title>

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-body>
		<div class="content">
			<div class="modal-column">
				<!-- Demographic Select -->
				<juno-patient-select
								ng-model="$ctrl.resolve.bookingData.demographic"
								show-patient-card="true"
								component-style="$ctrl.resolve.style">
				</juno-patient-select>
			</div>
			<div class="modal-column">
				<div class="flex-row appointment-type-row">
					<!-- Appointment type -->
					<juno-select ng-model="$ctrl.resolve.bookingData.appointmentType"
									placeholder="Appointment Type"
									options="$ctrl.appointmentTypeOptions"
									label="Appointment Type"
									label-position="LABEL_POSITION.TOP"
									on-change="$ctrl.onAppointmentTypeSelected(value)"
									component-style="$ctrl.resolve.style">
					</juno-select>

					<div class="flex-column align-items-flex-end justify-content-end appointment-check-box-container">
						<!-- Appointment Critical -->
						<juno-check-box ng-model="$ctrl.resolve.bookingData.critical"
										label="Critical"
										title="Critical"
										component-style="$ctrl.resolve.style">
						</juno-check-box>
					</div>
				</div>

				<div class="flex-row">

					<!-- Date FAKE  -->
					<juno-input label="Date"
									label-position="LABEL_POSITION.TOP"
									disabled="true"
									component-style="$ctrl.resolve.style">
					</juno-input>

					<!-- Time FAKE -->
					<juno-input label="Time"
									label-position="LABEL_POSITION.TOP"
									disabled="true"
									component-style="$ctrl.resolve.style">
					</juno-input>

					<!-- Duration -->
					<juno-input class="appointment-duration"
									label="Duration"
									only-numeric="true"
									label-position="LABEL_POSITION.TOP"
									ng-model="$ctrl.resolve.bookingData.duration"
									component-style="$ctrl.resolve.style">
					</juno-input>
				</div>

				<div class="flex-row">
					<!-- Reason type -->
					<juno-select ng-model="$ctrl.resolve.bookingData.reasonType"
									placeholder="Reason Type"
									options="$ctrl.reasonTypeOptions"
									label="Reason Type"
									label-position="LABEL_POSITION.TOP"
									component-style="$ctrl.resolve.style">
					</juno-select>

					<!-- Site -->
					<juno-select ng-model="$ctrl.resolve.bookingData.siteId"
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
				<juno-input class="booking-notes"
								label="Notes"
								ng-model="$ctrl.resolve.bookingData.notes"
								character-limit="255"
								label-position="LABEL_POSITION.TOP"
								component-style="$ctrl.resolve.style">
				</juno-input>

				<!-- Reason -->
				<juno-input class="booking-reason"
								label="Reason"
								ng-model="$ctrl.resolve.bookingData.reason"
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
							ng-click="$ctrl.onCreate()"
							disabled="!$ctrl.canSubmit()">
				Create
			</juno-button>
		</div>
	</modal-footer>
</juno-simple-modal>