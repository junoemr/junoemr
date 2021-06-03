<div class="message-list h-100 w-100">
	<!-- Loading indicator -->
	<div ng-if="!$ctrl.messageStream">
		<juno-loading-indicator message-alignment="vertical"
		                        indicator-type="dot-pulse">
		</juno-loading-indicator>
	</div>

	<!-- Message list -->
	<div ng-if="$ctrl.messageStream && $ctrl.messageStream.length > 0"
	     class="flex-col"
	     infinite-scroll="$ctrl.fetchMoreMessages()"
	     infinite-scroll-distance="0.5"
	     infinite-scroll-parent="true">

		<div ng-repeat="message in $ctrl.messageStream" class="message-container">
			<message-card message="message"
			              selected="message.id === $ctrl.selectedMessageId"
			              component-style="$ctrl.componentStyle"
			              sent-view="$ctrl.groupId === MessageGroup.Sent"
			              click="$ctrl.onSelectMessage(message)">
			</message-card>
		</div>
	</div>

	<!-- Zero state -->
	<div ng-if="$ctrl.messageStream && $ctrl.messageStream.length === 0 && !$ctrl.messageStream.isLoading">
		<div class="zero-sate-text text-center body-normal m-t-32">This mailbox is empty</div>
	</div>

</div>