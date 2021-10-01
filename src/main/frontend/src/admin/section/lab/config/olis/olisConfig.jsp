<div class="lab-config-olis">
	<panel no-header="true">
		<panel-body>
			<div class="flex-row justify-content-between align-items-center m-b-8">
				<div>
					<juno-check-box ng-model="$ctrl.pollingEnabled"
					                change="$ctrl.setPollingEnabled(value)"
					                label="Automatic Downloads Enabled">
					</juno-check-box>
				</div>
				<div class="button-wrapper">
					<juno-button click="$ctrl.labSearch()">
						OLIS Lab Search
					</juno-button>
				</div>

			</div>
			<div class="flex-column settings-table m-b-8">
				<div class="d-grid grid-column-3 p-8 m-b-4 header">
					<span class="header-text">Provider</span>
					<span class="header-text">Polling Status</span>
					<span class="header-text">Next Poll From</span>
				</div>
				<div ng-repeat="providerSettings in $ctrl.providerSettingsList"
				     class="d-grid grid-column-3 p-4">
					<span>{{providerSettings.provider.lastName}}, {{providerSettings.provider.firstName}}</span>
					<span>{{$ctrl.configurationStatusDisplay(providerSettings.isConfigured)}}</span>
					<span>{{$ctrl.startDateDisplay(providerSettings.startDateTime)}}</span>
				</div>
			</div>
			<div class="flex-row justify-content-end align-items-center">
				<div class="button-wrapper">
					<juno-button click="$ctrl.manualLabPull()"
					             disabled="$ctrl.loadingQueue.isLoading">
						Fetch Labs Now!
					</juno-button>
				</div>
			</div>
		</panel-body>
	</panel>
	<panel>
		<panel-header>
			<h6>OLIS Polling Settings</h6>
		</panel-header>
		<panel-body>
			<div class="flex-column input-form">
				<div class="m-b-8 flex-row">
					<juno-date-select ng-model="$ctrl.systemSettings.startDateTime"
					                  label="Default Polling Start Date/Time">
					</juno-date-select>
					<juno-time-select ng-model="$ctrl.systemSettings.startDateTime">
					</juno-time-select>
				</div>
				<div class="m-b-8 w-max-512">
					<juno-select ng-model="$ctrl.systemSettings.frequency"
					             options="$ctrl.frequencySelectOptions"
					             label="Polling Frequency">
					</juno-select>
				</div>
				<div class="m-b-8">
					<juno-check-box ng-model="$ctrl.systemSettings.filterPatients"
					                label="Filter patients not in system">
					</juno-check-box>
				</div>
				<div class="m-b-8 flex-row justify-content-end">
					<div class="button-wrapper">
						<juno-button click="$ctrl.saveSystemSettings()"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.DEFAULT"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
							Save
						</juno-button>
					</div>
				</div>
			</div>
		</panel-body>
	</panel>
</div>