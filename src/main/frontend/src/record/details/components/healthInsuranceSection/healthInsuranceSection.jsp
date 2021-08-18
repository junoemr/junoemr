<div class="demographic-details-section health-insurance-section">

	<h4 class="title">
		Health Insurance
	</h4>

	<div class="body">
		<div class="hin-fields">
			<!-- HIN -->
			<juno-input ng-model="$ctrl.ngModel.hin"
							label="HIN"
							invalid="!$ctrl.validations.hin()"
							show-invalid-focus="false"
							component-style="$ctrl.componentStyle"
							class="hin">
			</juno-input>
			<!-- HIN Version -->
			<juno-input ng-model="$ctrl.ngModel.ver"
							placeholder="Ver"
							component-style="$ctrl.componentStyle"
							class="version">
			</juno-input>
		</div>

		<!-- Health Care Type -->
		<juno-select ng-model="$ctrl.ngModel.hcType"
						options="$ctrl.provinces"
						label="Health Card Type"
						component-style="$ctrl.componentStyle">
		</juno-select>

		<!-- Effective Date -->
		<juno-date-select ng-model="$ctrl.ngModel.effDate"
						label="Effective date"
						on-validity-change="$ctrl.effectiveDateValid = valid"
						component-style="$ctrl.componentStyle">
		</juno-date-select>


		<!-- HIN Renew Date -->
		<juno-date-select ng-model="$ctrl.ngModel.hcRenewDate"
						label="Renew Date"
						component-style="$ctrl.componentStyle">
		</juno-date-select>
	</div>
</div>