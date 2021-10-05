<juno-modal id="add-demographic-modal">
	<modal-ctl-buttons>
		<button type="button" class="btn btn-icon" aria-label="Close"
						ng-click="$ctrl.onCancel()"
						title="Cancel">
			<i class="icon icon-modal-ctl icon-close"></i>
		</button>
	</modal-ctl-buttons>

	<modal-title>
		<h3>Add Demographic</h3>
	</modal-title>

	<modal-body class="add-demographic-content">
		<div class="col-xs-6 flex flex-row justify-content-center">
			<div class="left-column-container">
				<!-- LAST NAME -->
				<div ng-class="{'invalid-field': $ctrl.invalidLastName}">
					<ca-field-text
						ca-name="lastName"
						ca-title="Last Name"
						ca-model="$ctrl.newDemographicData.lastName"
						ca-rows="1"
						ca-required-field="true"
						ca-focus-field="$ctrl.focusField"
					>
					</ca-field-text>
				</div>
				<!-- FIRST NAME -->
				<div ng-class="{'invalid-field': $ctrl.invalidFirstName}">
					<ca-field-text
									ca-name="firstName"
									ca-title="First Name"
									ca-model="$ctrl.newDemographicData.firstName"
									ca-rows="1"
									ca-required-field="true"
					>
					</ca-field-text>
				</div>
				<!-- GENDER -->
				<div ng-class="{'invalid-field': $ctrl.invalidSex}">
					<ca-field-select
									ca-template="label"
									ca-name="gender"
									ca-title="Gender"
									ca-model="$ctrl.newDemographicData.sex"
									ca-options="$ctrl.genders"
									ca-empty-option="false"
									ca-required-field="true"
					>
					</ca-field-select>
				</div>
				<!-- DOB -->
				<div ng-class="{'invalid-field': $ctrl.invalidDob}">
					<ca-field-date
									ca-title="Date of Birth"
									ca-date-picker-id="dob"
									ca-name="dob"
									ca-model="$ctrl.newDemographicData.dateOfBirth"
									ca-orientation="auto"
									ca-required-field="true"
					>
					</ca-field-date>
				</div>
				<!-- HIN -->
				<div class="hin-fields">
					<!-- HIN NUM-->
					<ca-field-text
									class="hin"
									ca-name="hin"
									ca-title="Health Insurance Number"
									ca-model="$ctrl.newDemographicData.hin"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- HIN VER -->
					<ca-field-text
									class="ver"
									ca-name="ver"
									ca-title="&nbsp;"
									ca-text-placeholder="Ver."
									ca-model="$ctrl.newDemographicData.ver"
									ca-rows="1"
					>
					</ca-field-text>
					<!-- HIN TYPE -->
					<ca-field-select
									ca-template="label"
									ca-name="hcType"
									ca-title="HIN Type"
									ca-model="$ctrl.newDemographicData.hcType"
									ca-options="$ctrl.provinces"
									ca-empty-option="false"
					>
					</ca-field-select>
				</div>
			</div>
		</div>
		<div class="col-xs-6 flex flex-row justify-content-center">
			<div class="right-column-container">
				<!-- ADDRESS -->
				<ca-field-text
								ca-name="address"
								ca-title="Address"
								ca-model="$ctrl.newDemographicData.address.address"
								ca-rows="1"
				>
				</ca-field-text>
				<!-- CITY -->
				<ca-field-text
								ca-name="city"
								ca-title="City"
								ca-model="$ctrl.newDemographicData.address.city"
								ca-rows="1"
				>
				</ca-field-text>
				<!-- PROVINCE -->
				<ca-field-select
								ca-template="label"
								ca-name="province"
								ca-title="Province"
								ca-model="$ctrl.newDemographicData.address.province"
								ca-options="$ctrl.provincesCA"
								ca-empty-option="false"
				>
				</ca-field-select>
				<!-- POSTAL CODE -->
				<ca-field-text
								ca-name="postal-code"
								ca-title="Postal Code"
								ca-model="$ctrl.newDemographicData.address.postal"
								ca-rows="1"
				>
				</ca-field-text>
				<!-- EMAIL -->
				<ca-field-text
								ca-name="email"
								ca-title="Email"
								ca-model="$ctrl.newDemographicData.email"
								ca-rows="1"
				>
				</ca-field-text>
				<!-- PHONE -->
				<ca-field-text
								ca-name="phone"
								ca-title="Phone"
								ca-model="$ctrl.newDemographicData.phone"
								ca-rows="1"
				>
				</ca-field-text>
			</div>
		</div>
	</modal-body>


	<modal-footer>
		<div class="flex flex-row justify-content-center">
			<juno-button
				 class="add-demographic-button"
				 title="Add"
				 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
				 click="$ctrl.onAdd()"
				 disabled="$ctrl.buttonClicked">
				Add
			</juno-button>
		</div>
	</modal-footer>
</juno-modal>