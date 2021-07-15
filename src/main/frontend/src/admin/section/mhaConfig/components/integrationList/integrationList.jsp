<div class="integration-list flex-col">

	<div class="header">
		<h3 class="m-t-16 m-b-16 m-l-16">MHA Integrations</h3>
	</div>

	<div class="list flex-col">
		<div ng-repeat="integration in $ctrl.integrations" class="integration-item">
			<div ng-click="$ctrl.selectedIntegration === integration ? $ctrl.selectedIntegration = null : $ctrl.selectedIntegration = integration"
			     class="integration-row p-16">
				<!-- Id -->
				<div>
					{{integration.id}}
				</div>

				<!-- site name -->
				<div class="text-ellipsis" title="{{integration.siteName}}">
					{{integration.siteName}}
				</div>

				<!-- remote Id -->
				<div class="text-ellipsis" title="{{integration.remoteId}}">
					{{integration.remoteId}}
				</div>

				<!-- type -->
				<div class="text-ellipsis">
					{{integration.type.toString()}}
				</div>

				<!-- buttons -->
				<div class="flex-row flex-gap-8">

					<juno-button title="Configure Integration."
					             component-style="$ctrl.componentStyle">
						<div class="flex-col justify-content-center">
							<i class="icon icon-gear"></i>
						</div>
					</juno-button>

					<juno-button click="$event.stopPropagation(); $ctrl.deleteIntegration(integration)"
					             title="Delete Integration."
					             button-color="JUNO_BUTTON_COLOR.DANGER"
					             component-style="$ctrl.componentStyle">
						<div class="flex-col justify-content-center">
							<i class="icon icon-delete"></i>
						</div>
					</juno-button>
				</div>
			</div>

			<!-- Integration settings -->
			<div ng-if="$ctrl.selectedIntegration === integration"
			     class="settings p-16 p-l-32 p-r-32">
				<mha-integration-settings integration="integration"
				                          component-style="$ctrl.componentStyle">
				</mha-integration-settings>
			</div>
		</div>
	</div>

	<div class="footer">
	</div>
</div>