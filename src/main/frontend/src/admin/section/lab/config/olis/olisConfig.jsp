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
				<div class="search-button-wrapper">
					<juno-button click="$ctrl.labSearch()">
						OLIS Lab Search
					</juno-button>
				</div>

			</div>
			<div class="flex-row justify-content-between align-items-center m-b-8">
				<div>
					<span></span>
				</div>
				<div class="search-button-wrapper">
					<juno-button click="$ctrl.manualLabPull()"
					             disabled="$ctrl.loadingQueue.isLoading">
						Fetch Labs Now!
					</juno-button>
				</div>
			</div>
			<div class="flex-column settings-table">
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
			<div class="p-t-16">
				<iframe width="100%"
				        height="200px"
				        title="Olis system preferences"
				        style="border:none;"
				        src="../olis/Preferences.jsp">
				</iframe>
			</div>
		</panel-body>
	</panel>
</div>