<div class="message-card flex-col" ng-class="$ctrl.selected ? 'selected' : ''" ng-click="$ctrl.onClick()">
	<!-- Header row -->
	<div class="flex-row align-items-center m-t-8 m-b-4 m-r-16">
		<div ng-hide="$ctrl.message.isRead" class="not-read-circle"></div>
		<div class="text-ellipsis"
		     title="{{$ctrl.message.sender.name}}">
			From: {{$ctrl.message.sender.name}}
		</div>
		<div class="timestamp-text flex-item-grow text-right text-ellipsis"
		     title="{{$ctrl.formatMessageDate($ctrl.message.createdAtDateTime)}}">
			{{$ctrl.formatMessageDate($ctrl.message.createdAtDateTime)}}
		</div>
	</div>

	<!-- Subject row -->
	<div class="message-subject body-small m-l-48 m-r-16 m-b-4"
	     title="{{$ctrl.message.subject}}">
		{{$ctrl.message.subject}}
	</div>

	<!-- Message preview row -->
	<div class="message-preview body-smallest m-l-48 m-r-16"
	     title="{{$ctrl.message.message}}">
		{{$ctrl.message.message}}
	</div>
</div>