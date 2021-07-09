<div class="mha-config">
	<div class="content-area d-flex flex-col align-items-center">
		<div class="flex-col">

			<h4>MyHealthAccess Configuration</h4>
			<div class="body-small">
				<p class="m-b-0">
					<span>MyHealthAccess is</span>
					<span ng-if="$ctrl.mhaEnabled === null">...</span>
					<span ng-if="$ctrl.mhaEnabled === true" class="mha-enabled-label">active</span>
					<span ng-if="$ctrl.mhaEnabled === false" class="mha-disabled-label">inactive</span>
					<span> on this Juno server</span>
				</p>
			</div>

			<!-- Integrations -->
			<integration-list class="m-t-32 m-b-32"
			                  component-style="$ctrl.pageStyle">
			</integration-list>

			<div class="footer">
				<p class="text-center">
					Be careful when changing these settings as they can disrupt patients ability to book with this Juno server.
					If you remove an integration by accident and want to recreate it simply go to the MHA super admin
					and "regenerate the API key" for this server.
				</p>
			</div>
		</div>
	</div>
</div>
