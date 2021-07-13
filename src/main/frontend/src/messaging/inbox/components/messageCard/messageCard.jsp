<div class="message-card flex-col"
     ng-class="{'selected': $ctrl.selected, 'group-selected': $ctrl.inMassEditList, 'read': $ctrl.message.isRead}"
     ng-click="$ctrl.onClick()">
	<!-- Header row -->
	<div class="flex-row align-items-center m-t-8 m-b-4 m-r-16">
		<juno-check-box class="round-checkbox m-r-16 m-l-16"
		                ng-model="$ctrl.inMassEditList"
		                title="Select message"
		                change="$ctrl.onCheckedChange(value)"
		                dummy="true">
		</juno-check-box>

		<!-- Sender / To -->
		<div ng-if="!$ctrl.sentView"
		     class="text-ellipsis"
		     ng-class="{'bold': !$ctrl.message.isRead}"
		     title="{{$ctrl.message.sender.name}}">
			From: {{$ctrl.message.sender.name ? $ctrl.message.sender.name : "Account Deleted"}}
		</div>
		<div ng-if="$ctrl.sentView"
		     class="text-ellipsis"
		     ng-class="{'bold': !$ctrl.message.isRead}"
		     title="{{$ctrl.recipientNames()}}">
			To: {{$ctrl.recipientNames()}}
		</div>

		<!-- Message timestamp -->
		<div class="timestamp-text flex-item-grow text-right text-ellipsis"
		     title="{{$ctrl.formatMessageDate($ctrl.message.createdAtDateTime)}}">
			{{$ctrl.formatMessageDate($ctrl.message.createdAtDateTime)}}
		</div>
	</div>

	<!-- Subject row -->
	<div class="message-subject body-small m-l-48 m-r-16 m-b-4"
	     ng-class="{'bold': !$ctrl.message.isRead}"
	     title="{{$ctrl.message.subject}}">
		{{$ctrl.message.subject}}
	</div>

	<!-- Message preview row -->
	<div class="message-preview body-smallest m-l-48 m-r-16"
	     title="{{$ctrl.message.message}}">
		{{$ctrl.message.message}}
	</div>
</div>