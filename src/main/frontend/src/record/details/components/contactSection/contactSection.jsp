<div class="demographic-details-section contact-section">
	<h4 class="title">
		Contact Information
	</h4>

	<div class="details-fields">
		<juno-input ng-model="$ctrl.ngModel.address.address"
		            label="Address"
		            placeholder="Enter Street Address"
		            component-style="$ctrl.componentStyle">
		</juno-input>

		<juno-input ng-model="$ctrl.ngModel.address.city"
		            label="City"
		            placeholder="Enter City Here"
		            component-style="$ctrl.componentStyle">
		</juno-input>


		<juno-select ng-model="$ctrl.ngModel.address.province"
		             placeholder="Select province"
		             options="$ctrl.provinces"
		             label="Province"
		             component-style="$ctrl.componentStyle">
		</juno-select>

		<juno-input ng-model="$ctrl.ngModel.address.postal"
		            label="Postal code"
		            placeholder="Enter Postal Code (e.g. K1M 1M4)"
		            component-style="$ctrl.componentStyle">
		</juno-input>

		<juno-input ng-model="$ctrl.ngModel.email"
		            label="Email Address"
		            invalid="!$ctrl.validations.email()"
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

		<!-- Work Phone -->
		<div class="phone-with-ext">
			<juno-input ng-model="$ctrl.ngModel.scrWorkPhone"
			            label="Work Phone"
			            placeholder="000-000-0000"
			            valid-regex="$ctrl.phoneNumberRegex"
			            component-style="$ctrl.componentStyle"
						>
			</juno-input>

			<div class="phone-ext">
				<juno-input ng-model="$ctrl.ngModel.scrWPhoneExt"
				            label="Ext"
				            valid-regex="$ctrl.phoneNumberRegex"
				            component-style="$ctrl.componentStyle"
				            >
				</juno-input>
			</div>

			<juno-check-box ng-model="$ctrl.ngModel.scrPreferredPhone"
			                title="Check to set preferred contact number"
			                true-value="'W'"
			                component-style="$ctrl.componentStyle">
			</juno-check-box>
		</div>

		<juno-input ng-model="$ctrl.ngModel.scrPhoneComment"
		            label="Phone Comment"
		            component-style="$ctrl.componentStyle">
		</juno-input>

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

<div class="demographic-details-section contact-section" ng-if="$ctrl.showAdditionalAddress">
	<juno-divider component-style="pageStyle"></juno-divider>
	<h4 class="title">
		Additional Contact Information
	</h4>

		<div class="details-fields">
			<juno-input ng-model="$ctrl.ngModel.address2.address"
			            label="Address"
			            placeholder="Enter Street Address"
			            component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input ng-model="$ctrl.ngModel.address2.city"
			            label="City"
			            placeholder="Enter City Here"
			            component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-select ng-model="$ctrl.ngModel.address2.province"
			             placeholder="Select province"
			             options="$ctrl.provinces"
			             label="Province"
			             component-style="$ctrl.componentStyle">
			</juno-select>


			<juno-input ng-model="$ctrl.ngModel.address2.postal"
			            label="Postal code"
			            placeholder="Enter Postal Code (e.g. K1M 1M4)"
			            component-style="$ctrl.componentStyle">
			</juno-input>
		</div>
</div>

