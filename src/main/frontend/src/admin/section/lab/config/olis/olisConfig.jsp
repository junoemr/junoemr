<div class="lab-config-olis">
	<panel no-header="true">
		<panel-body>
			<div class="flex-row justify-content-between align-items-center">
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