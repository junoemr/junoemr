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

	<modal-body ng-keydown="$ctrl.submitOnCtrlEnter($event)" tabIndex="-1">
		<div class="add-demographic-content d-flex justify-content-center">
			<div class="flex flex-row justify-content-between width-80">
				<div class="flex-col column-container" ng-ref="$ctrl.firstColumnRef">
					<!-- LAST NAME -->
					<juno-input ng-model="$ctrl.newDemographicData.lastName"
								label="Last Name"
								ng-change="$ctrl.onLastNameChange()"
								label-position="$ctrl.LABEL_POSITION"
								component-style="$ctrl.COMPONENT_STYLE"
								invalid="!$ctrl.lastNameValid()"
								show-invalid-focus="true"
								auto-focus="true"
								required-indicator="true"
					>
					</juno-input>
					<!-- FIRST NAME -->
					<juno-input ng-model="$ctrl.newDemographicData.firstName"
								label="First Name"
								label-position="$ctrl.LABEL_POSITION"
								ng-change="$ctrl.onFirstNameChange()"
								component-style="$ctrl.COMPONENT_STYLE"
								invalid="!$ctrl.firstNameValid()"
								show-invalid-focus="true"
								required-indicator="true"
					>
					</juno-input>
					<!-- GENDER -->
					<juno-select label="Gender"
								 label-position="$ctrl.LABEL_POSITION"
								 component-style="$ctrl.COMPONENT_STYLE"
								 ng-model="$ctrl.newDemographicData.sex"
								 invalid="!$ctrl.genderValid()"
								 options="$ctrl.genders"
								 on-change="$ctrl.onGenderChange(value)"
								 required-indicator="true">
					</juno-select>
					<!-- DOB -->
					</juno-date-select>
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
					<div class="flex-row flex-grow-1 align-items-center hin-fields">
						<!-- HIN NUM-->
						<juno-select-text select-model="$ctrl.newDemographicData.hcType"
										  select-options="$ctrl.hcTypeProvs"
										  select-change="$ctrl.onHcTypeChange(value)"
										  text-model="$ctrl.newDemographicData.hin"
										  label="Health Insurance Number"
										  label-position="$ctrl.LABEL_POSITION"
										  component-style="$ctrl.COMPONENT_STYLE"
										  class="m-r-4 hin"
						>
						</juno-select-text>
						<!-- HIN VER -->
						<juno-input label="Ver"
									ng-model="$ctrl.newDemographicData.ver"
									label-position="$ctrl.LABEL_POSITION"
									component-style="$ctrl.COMPONENT_STYLE"
									class="ver">
						</juno-input>
					</div>
					<!-- MRP -->
					<juno-select label="MRP"
								 label-position="$ctrl.LABEL_POSITION"
								 component-style="$ctrl.COMPONENT_STYLE"
								 ng-model="$ctrl.newDemographicData.mrp"
								 options="$ctrl.mrpOptions"
								 on-change="$ctrl.onMRPChange(value)">

					</juno-select>
				</div>
				<div class="flex-col column-container">
					<!-- ADDRESS -->
					<juno-input ng-model="$ctrl.newDemographicData.address.address"
								label="Address"
								label-position="$ctrl.LABEL_POSITION"
								component-style="$ctrl.COMPONENT_STYLE"
					>
					</juno-input>
					<!-- CITY -->
					<juno-input ng-model="$ctrl.newDemographicData.address.city"
								label="City"
								label-position="$ctrl.LABEL_POSITION"
								component-style="$ctrl.COMPONENT_STYLE"
					>
					</juno-input>
					<!-- PROVINCE -->
					<juno-select label="Province"
								 label-position="$ctrl.LABEL_POSITION"
								 component-style="$ctrl.COMPONENT_STYLE"
								 options="$ctrl.provincesCA"
								 ng-model="$ctrl.newDemographicData.address.province"
					></juno-select>
					<!-- POSTAL CODE -->
					<juno-input ng-model="$ctrl.newDemographicData.address.postal"
								label="Postal Code"
								label-position="$ctrl.LABEL_POSITION"
								component-style="$ctrl.COMPONENT_STYLE"
					>
					</juno-input>
					<!-- EMAIL -->
					<juno-input ng-model="$ctrl.newDemographicData.email"
								label="Email"
								label-position="$ctrl.LABEL_POSITION"
								component-style="$ctrl.COMPONENT_STYLE"
					>
					</juno-input>
					<!-- PHONE -->
					<juno-select-text select-model="$ctrl.preferredPhoneType"
									  select-options="$ctrl.preferredPhoneOptions"
									  select-change="$ctrl.onPreferredPhoneTypeChange(value)"
									  text-model="$ctrl.preferredPhoneNumber"
									  label="Preferred Phone"
									  label-position="$ctrl.LABEL_POSITION"
									  component-style="$ctrl.COMPONENT_STYLE"
									  id="preferred-phone">
					</juno-select-text>
				</div>
			</div>
		</div>
	</modal-body>


	<modal-footer>
		<div class="d-flex justify-content-center">
			<div class="d-flex justify-content-between width-80">
				<juno-button
						id="swipe-healthcard-button"
						class="w-128 flex-grow-0"
						button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.COLORED"
						click="$ctrl.openSwipecardModal()"
						disabled="$ctrl.buttonClicked">
					Swipe Health Card
				</juno-button>
				<div class="d-flex flex-row justify-content-end align-items-center">
					<div class="flex-row align-items-center m-r-24">
						<juno-check-box id="create-another"
										ng-model="$ctrl.isCreateAnotherEnabled">
						</juno-check-box>
						<span class="d-inline-block m-l-8">Create another demographic</span>
					</div>
					<juno-button
							class="add-demographic-button w-128 flex-grow-0"
							title="Ctrl-Enter"
							button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							click="$ctrl.onAdd()"
							disabled="$ctrl.buttonClicked">
						Add
					</juno-button>
				</div>
			</div>
		</div>
	</modal-footer>
</juno-modal>