<div class="lab-config-olis">
	<panel>
		<panel-header>
			<h6>OLIS Polling Settings</h6>
		</panel-header>
		<panel-body>
			<div class="flex-column input-form">
				<div class="m-b-8 flex-row">
					<juno-date-select ng-model="$ctrl.systemSettings.startDateTime"
					                  label="Default Polling Start Date/Time"
					                  title="Providers without a set polling date will fetch labs starting at this date">
					</juno-date-select>
					<juno-time-select ng-model="$ctrl.systemSettings.startDateTime">
					</juno-time-select>
				</div>
				<div class="m-b-8 w-max-512">
					<juno-select ng-model="$ctrl.systemSettings.frequency"
					             options="$ctrl.frequencySelectOptions"
					             label="Polling Frequency"
					             title="How often the system will check OLIS for new labs">
					</juno-select>
				</div>
				<div class="m-b-8">
					<juno-check-box ng-model="$ctrl.systemSettings.filterPatients"
					                label="Filter patients not in system"
					                title="If checked, the system will ignore labs that do not match with an existing patient">
					</juno-check-box>
				</div>
				<div class="flex-row justify-content-end">
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

	<panel no-header="true">
		<panel-body>
			<div class="grid-column-3 grid-gap-8">
				<div class="flex-column">
					<div class="flex-row align-items-center justify-content-between m-b-8">
						<div>
							<juno-check-box ng-model="$ctrl.pollingEnabled"
							                change="$ctrl.setPollingEnabled(value)"
							                label="Automatic Downloads Enabled"
											title="If checked, the system will periodically check for new labs using provider specific credentials">
							</juno-check-box>
						</div>
						<span>Last Run: {{$ctrl.lastRunDateDisplay()}}</span>
					</div>
					<div class="flex-row align-items-center justify-content-between m-b-8">
						<div class="button-wrapper">
							<juno-button click="$ctrl.manualLabPull()"
							             disabled="$ctrl.loadingQueue.isLoading"
							             title="Download new labs to the inbox immediately (same action as the automatic polling)">
								Fetch Labs Now!
							</juno-button>
						</div>
					</div>
					<div class="flex-row align-items-center justify-content-between">
						<div class="button-wrapper">
							<juno-button click="$ctrl.labSearch()"
							             title="open the OLIS manual search window">
								OLIS Lab Search
							</juno-button>
						</div>
					</div>
				</div>
				<div class="flex-column settings-table grid-item-column-span-2 m-b-8 ">
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
			</div>
		</panel-body>
	</panel>

</div>