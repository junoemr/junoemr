<div class="demographic-details-section contact-section">
	<h4 class="title">
		Contact Information
	</h4>

	<div class="fields">
		<div class="column">
			<juno-input ng-model="$ctrl.ngModel.address.address"
							label="Address"
							placeholder="Enter patient street address"
							component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-select ng-model="$ctrl.ngModel.address.province"
							placeholder="Select province"
							options="$ctrl.provinces"
							label="Province"
							component-style="$ctrl.componentStyle">
			</juno-select>

			<juno-input ng-model="$ctrl.ngModel.email"
							label="Email Address"
							placeholder="Email"
							invalid="!$ctrl.validations.email()"
							component-style="$ctrl.componentStyle">
			</juno-input>

			<!-- Home Phone -->
			<div class="phone-with-ext">
				<juno-input ng-model="$ctrl.ngModel.scrHomePhone"
								label="Home Phone"
								placeholder="000-000-0000"
								valid-regex="$ctrl.phoneNumberRegex"
								component-style="$ctrl.componentStyle">
				</juno-input>

				<juno-input ng-model="$ctrl.ngModel.scrHPhoneExt"
								label="Ext"
								placeholder="00000"
								valid-regex="$ctrl.phoneNumberRegex"
								component-style="$ctrl.componentStyle"
								class="phone-ext">
				</juno-input>

				<juno-check-box ng-model="$ctrl.ngModel.scrPreferredPhone"
								title="Check to set preferred contact number"
								true-value="'H'"
								component-style="$ctrl.componentStyle">
				</juno-check-box>
			</div>

			<juno-input ng-model="$ctrl.ngModel.scrPhoneComment"
							label="Phone Comment"
							placeholder="Enter phone comment here"
							component-style="$ctrl.componentStyle">
			</juno-input>

		</div>

		<div class="divider"></div>

		<div class="column">
			<juno-input ng-model="$ctrl.ngModel.address.city"
							label="City"
							placeholder="Enter city here"
							component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input ng-model="$ctrl.ngModel.address.postal"
							label="Postal code"
							placeholder="Enter postal code"
							component-style="$ctrl.componentStyle">
			</juno-input>

			<!-- Cell Phone -->
			<div class="mobile-phone">
				<juno-input ng-model="$ctrl.ngModel.scrCellPhone"
								label="Mobile phone"
								placeholder="000-000-0000"
								valid-regex="$ctrl.phoneNumberRegex"
								component-style="$ctrl.componentStyle">
				</juno-input>
				<juno-check-box ng-model="$ctrl.ngModel.scrPreferredPhone"
								title="Check to set preferred contact number"
								true-value="'C'"
								component-style="$ctrl.componentStyle">
				</juno-check-box>
			</div>

			<!-- Work Phone -->
			<div class="phone-with-ext">
				<juno-input ng-model="$ctrl.ngModel.scrWorkPhone"
								label="Work Phone"
								placeholder="000-000-0000"
								valid-regex="$ctrl.phoneNumberRegex"
								component-style="$ctrl.componentStyle">
				</juno-input>

				<juno-input ng-model="$ctrl.ngModel.scrWPhoneExt"
								label="Ext"
								placeholder="00000"
								valid-regex="$ctrl.phoneNumberRegex"
								component-style="$ctrl.componentStyle"
								class="phone-ext">
				</juno-input>

				<juno-check-box ng-model="$ctrl.ngModel.scrPreferredPhone"
								title="Check to set preferred contact number"
								true-value="'W'"
								component-style="$ctrl.componentStyle">
				</juno-check-box>
			</div>

			<!-- Electronic Messaginc Consent -->
			<div class="e-messaging-consent">
				<juno-select ng-model="$ctrl.ngModel.electronicMessagingConsentStatus"
								options="$ctrl.electronicMessagingConsentOptions"
								label="E-Messaging Consent"
								component-style="$ctrl.componentStyle"
								on-change="$ctrl.onConsentStatusChange(value)">
				</juno-select>

				<span class="consent-date">
					{{$ctrl.getElectronicMessagingConsentStatusText()}}
				</span>
			</div>
		</div>
	</div>
</div>