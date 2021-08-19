<div class="demographic-details-section demographic-section">
	<h4 class="title">
		Demographic
	</h4>

	<div class="fields">
		<juno-input ng-model="$ctrl.ngModel.lastName"
		            class = "extra-indent"
						label="Last Name"
						uppercase="true"
						component-style="$ctrl.componentStyle">
		</juno-input>

		<juno-input ng-model="$ctrl.ngModel.firstName"
						label="First Name"
		                class="justify-content-space-between"
						uppercase="true"
						component-style="$ctrl.componentStyle">
		</juno-input>

		<juno-date-select ng-model="$ctrl.ngModel.dateOfBirth"
						label="Date of Birth"
						show-age="true"
		                  class = "extra-indent"
						on-validity-change="$ctrl.dobValid = valid;"
						component-style="$ctrl.componentStyle"
		>
		</juno-date-select>

		<juno-select
				ng-model="$ctrl.ngModel.sex"
				class = "extra-indent"
				options="$ctrl.genderOptions;"
				label="Sex"
				component-style="$ctrl.componentStyle">
		</juno-select>

		<juno-select ng-model="$ctrl.ngModel.title"
						options="$ctrl.titles"
						label="Title"
		                class="justify-content-space-between"
						placeholder="Select patient title"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<juno-typeahead
				name="spokenLanguage"
				title="Spoken Language"
				model="$ctrl.ngModel.spokenLanguage"
				options="$ctrl.spokenLanguages"
				placeholder="Spoken language"
				class="justify-content-space-between"
				label-position="LABEL_POSITION.LEFT"
				component-style="$ctrl.componentStyle">
		</juno-typeahead>

		<juno-select
						ng-model="$ctrl.ngModel.officialLanguage"
						options="$ctrl.languages"
						class = "extra-indent"
						label="Language"
						placeholder="Select patient language"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<juno-typeahead
				name="countryOfOrigin"
				title="Country of Origin"
				class = "extra-indent"
				model="$ctrl.ngModel.countryOfOrigin"
				options="$ctrl.countries"
				placeholder="Country of Origin"
				label-position="LABEL_POSITION.LEFT"
				component-style="$ctrl.componentStyle">
		</juno-typeahead>

		<juno-select
						ng-model="$ctrl.ngModel.scrAboriginal"
						options="$ctrl.aboriginalStatuses"
						label="Aboriginal Status"
						class="justify-content-space-between"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<juno-input ng-model="$ctrl.ngModel.alias"
						label="Alias Names"
						placeholder="Enter Alias"
		                class="justify-content-space-between"
						component-style="$ctrl.componentStyle">
		</juno-input>

		<mha-patient-connection demographic="$ctrl.ngModel"
		                        component-style="$ctrl.componentStyle"
		                        on-site-list-change="$ctrl.onMHASiteListChange(sites)">
		</mha-patient-connection>

		<div class="connected-sites justify-content-space-between">

			<juno-input ng-model="$ctrl.mhaSites"
							label="Connected Site"
							placeholder="No Connected Sites"
							readonly="true"
							component-style="$ctrl.componentStyle">
			</juno-input>
			<juno-button disabled="!$ctrl.canOpenPatientModal()"
			             ng-click="$ctrl.openPatientModal()"
			             button-color="!$ctrl.canOpenPatientModal() ? JUNO_BUTTON_COLOR.TRANSPARENT : JUNO_BUTTON_COLOR.PRIMARY"
			             component-style="$ctrl.componentStyle">
				<div class="flex-row justify-content-center">
					<i class="icon icon-view"></i>
				</div>
			</juno-button>
		</div>
	</div>
</div>