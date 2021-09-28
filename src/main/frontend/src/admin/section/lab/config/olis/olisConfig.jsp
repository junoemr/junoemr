<div class="lab-config-olis">
	<div class="p-16">
		<juno-check-box ng-model="$ctrl.pollingEnabled"
		                change="$ctrl.setPollingEnabled(value)"
		                label="Automatic Downloads Enabled">
		</juno-check-box>
		<juno-button click="$ctrl.labSearch()">
			OLIS Lab Search
		</juno-button>
	</div>
</div>