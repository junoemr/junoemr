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

	<modal-body>
		<div class="add-demographic-content d-flex justify-content-center">
		<div class="flex flex-row justify-content-between width-80">
			<div class="flex-col column-container">
				<!-- LAST NAME -->
				<juno-input ng-model="$ctrl.newDemographicData.lastName"
							label="Last Name"
							label-position="$ctrl.LABEL_POSITION"
							component-style="$ctrl.COMPONENT_STYLE"
							invalid="!$ctrl.newDemographicData.lastName"
							show-invalid-focus="true"
							show-invalid-pristine="false"
							auto-focus="true"
							required-indicator="true"
				>
				</juno-input>
				<!-- FIRST NAME -->
				<juno-input ng-model="$ctrl.newDemographicData.firstName"
							label="First Name"
							label-position="$ctrl.LABEL_POSITION"
							component-style="$ctrl.COMPONENT_STYLE"
							invalid="!$ctrl.newDemographicData.firstName"
							show-invalid-focus="true"
							show-invalid-pristine="false"
							required-indicator="true"
				>
				</juno-input>
				<!-- GENDER -->
				<juno-select label="Gender"
							 label-position="$ctrl.LABEL_POSITION"
							 component-style="$ctrl.COMPONENT_STYLE"
							 ng-model="$ctrl.newDemographicData.sex"
							 invalid="!$ctrl.newDemographicData.sex"
							 show-invalid-pristine="false"
							 options="$ctrl.genders"
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
				<div class="flex-row flex-grow-1 align-items-center">
					<!-- HIN NUM-->
					<juno-select-text select-model="$ctrl.newDemographicData.hcType"
									  select-placeholder="{{ {label: 'Select a Province', shortLabel: '--'} }}"
									  select-options="$ctrl.test"
									  text-model="$ctrl.newDemographicData.hin"
									  label="Health Insurance Number"
									  label-position="$ctrl.LABEL_POSITION"
									  component-style="$ctrl.COMPONENT_STYLE"
									  class="m-r-8"
					>
					</juno-select-text>
					<!-- HIN VER -->
					<juno-input label="Ver"
								ng-model="$ctrl.newDemographicData.ver"
								label-position="$ctrl.LABEL_POSITION"
								component-style="$ctrl.COMPONENT_STYLE"
								placeholder="Ver">
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
				<juno-input ng-model="$ctrl.newDemographicData.phone"
							label="Phone"
							label-position="$ctrl.LABEL_POSITION"
							component-style="$ctrl.COMPONENT_STYLE"
				>
				</juno-input>
			</div>
		</div>
		</div>
	</modal-body>


	<modal-footer>
		<div class="d-flex justify-content-center">
		<div class="d-flex flex-row justify-content-end align-items-center width-80">
			<div class="flex-row align-items-center m-r-24">
				<juno-check-box id="create-another"
								ng-model="$ctrl.isCreateAnotherEnabled">
				</juno-check-box>
				<span class="d-inline-block m-l-8">Create another demographic</span>
			</div>
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
		</div>
	</modal-footer>
</juno-modal>