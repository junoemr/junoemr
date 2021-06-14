<juno-modal class="mha-patient-details-modal" component-style="$ctrl.resolve.style">

	<modal-ctl-buttons>
			<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title class="patient-title">
		<div class="header-start">
			<h3>MHA Profile</h3>
			<h5> for </h5>
			<h4>{{$ctrl.getLocalPatientName()}}</h4>
		</div>
		<h5>{{$ctrl.getLocalPatientHinAndProv()}}</h5>
	</modal-title>

	<modal-body class="patient-modal-body">
		<juno-tab ng-model="$ctrl.currentIntegration"
		          tabs="$ctrl.integrationTabs"
		          component-style="$ctrl.resolve.style">
		</juno-tab>
		<div class="content-area">
			<div class="left-content">
				<div class="top-content">
					<div class="flex-row">
						<juno-input ng-model="$ctrl.currentProfile.last_name"
						            label="Last Name"
						            label-position="LABEL_POSITION.TOP"
						            readonly="true"
						            no-box="true"
						            component-style="$ctrl.resolve.style">
						</juno-input>
						<juno-input ng-model="$ctrl.currentProfile.first_name"
						            label="First Name"
						            label-position="LABEL_POSITION.TOP"
						            readonly="true"
						            no-box="true"
						            component-style="$ctrl.resolve.style">
						</juno-input>
					</div>
					<!-- Email -->
					<juno-input ng-model="$ctrl.currentProfile.email"
									label="Email Address"
									label-position="LABEL_POSITION.TOP"
									readonly="true"
									no-box="true"
									component-style="$ctrl.resolve.style">
					</juno-input>
				</div>

				<panel no-header="true" component-style="$ctrl.resolve.style">
					<panel-body>
						<div class="panel-content">
							<div class="panel-left">
								<!-- Phone -->
								<juno-input ng-model="$ctrl.currentProfile.cell_phone"
								            label="Phone Number"
								            label-position="LABEL_POSITION.TOP"
								            readonly="true"
								            no-box="true"
								            component-style="$ctrl.resolve.style">
								</juno-input>

								<!-- Address -->
								<juno-input ng-model="$ctrl.currentProfile.address_1"
								            label="Street Address"
								            label-position="LABEL_POSITION.TOP"
								            readonly="true"
								            no-box="true"
								            component-style="$ctrl.resolve.style"
								            class="no-margin">
								</juno-input>
							</div>

							<div class="panel-right">
								<!-- Birth Date -->
								<juno-input ng-model="$ctrl.currentProfile.birth_date"
												label="Date of Birth"
												label-position="LABEL_POSITION.TOP"
												readonly="true"
												no-box="true"
												component-style="$ctrl.resolve.style">
								</juno-input>

								<!-- Gender  -->
								<juno-input ng-model="$ctrl.currentProfile.sex"
												label="Gender"
												label-position="LABEL_POSITION.TOP"
												readonly="true"
												no-box="true"
												component-style="$ctrl.resolve.style"
												class="small">
								</juno-input>

								<!-- City Province -->
								<juno-input ng-model="$ctrl.currentProfile.city_province"
												label="City Province"
												label-position="LABEL_POSITION.TOP"
												readonly="true"
												no-box="true"
												component-style="$ctrl.resolve.style"
												class="no-margin">
								</juno-input>

								<!-- Postal Code -->
								<juno-input ng-model="$ctrl.currentProfile.postal_code"
												label="Postal Code"
												label-position="LABEL_POSITION.TOP"
												readonly="true"
												no-box="true"
												component-style="$ctrl.resolve.style"
												class="no-margin small">
								</juno-input>
							</div>
						</div>
					</panel-body>
				</panel>
			</div>

			<juno-divider component-style="$ctrl.resolve.style" horizontal="true"></juno-divider>

			<div class="right-content">
				<!-- Connection Status -->
				<div class="connection-status">
					<juno-input ng-model="$ctrl.connectionStatus"
					            label="Connection"
					            label-position="LABEL_POSITION.TOP"
					            readonly="true"
					            no-box="true"
					            component-style="$ctrl.resolve.style"
					            class="no-margin small">
					</juno-input>
					<i ng-if="$ctrl.hasActiveConnection()" class="icon icon-check"></i>
				</div>
			</div>
		</div>
	</modal-body>

	<modal-footer>
		<panel no-header="true" component-style="$ctrl.resolve.style">
			<panel-body>
				<div class="footer-button-wrapper">
					<juno-button component-style="$ctrl.resolve.style"
				             button-color="JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
				             disabled="!$ctrl.hasActiveConnection()"ng-click="$ctrl.cancelConnection()"
				             class="cancel-connection-btn">
					<div class="cancel-btn-text">
						Cancel Connection
					</div>
				</juno-button><juno-button disabled="$ctrl.currentProfile"
					             component-style="$ctrl.resolve.style"
					             button-color="JUNO_BUTTON_COLOR.PRIMARY"
					             ng-click="$ctrl.openInviteConfirmModal()"
					             class="cancel-connection-btn">
						{{ $ctrl.getInviteButtonText() }}
					</juno-button>
				</div>
			</panel-body>
		</panel>
	</modal-footer>
</juno-modal>