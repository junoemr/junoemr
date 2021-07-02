<div class="message-view h-100 w-100 flex-col">

	<!-- Loading indicator -->
	<div ng-if="$ctrl.isLoading" class="flex-item-grow flex-col justify-content-center">
		<juno-loading-indicator message-alignment="vertical"
		                        indicator-type="dot-pulse">
		</juno-loading-indicator>
	</div>

	<!-- Message Feed -->
	<div ng-if="!$ctrl.isLoading" class="flex-item-grow flex-col p-16 overflow-y-auto">
		<div ng-repeat="message in $ctrl.conversation.messages | orderBy:'-createdAtDateTime'" class="flex-col">
			<message class="m-l-16 m-r-16 m-t-16" message="message" messaging-service="$ctrl.messagingService"></message>
			<juno-divider component-style="$ctrl.componentStyle"></juno-divider>
		</div>
	</div>

	<!-- Footer -->
	<div class="footer-strip">
		<!-- nothing -->
	</div>
</div>