<page-wrapper class="messaging-inbox">
	<page-header>
		<h6>Backend: {{$ctrl.backend}} Source: {{$ctrl.selectedSourceId}} Group: {{$ctrl.selectedGroupId}}</h6>
	</page-header>
	<page-body class="flex-item-grow flex-col messaging-inbox">
		<inbox-header-bar ng-if="$ctrl.backend"
		                  component-style="$ctrl.componentStyle"
		                  selected-message-id="$ctrl.selectedMessageId"
		                  message-stream="$ctrl.messageStream"
		                  messaging-backend-id="$ctrl.backend"
		                  messageable-filter="$ctrl.messageableFilter"
		                  source-id="$ctrl.selectedSourceId"
		                  group-id="$ctrl.selectedGroupId">
		</inbox-header-bar>

		<div class="flex-item-grow flex-row overflow-hidden overflow-x-auto">
			<inbox-select class="inbox-select-pane w-256 w-min-256 h-min-100"
			              component-style="$ctrl.componentStyle"
			              groups="$ctrl.groups"
			              sources="$ctrl.messageSources"
			              selected-source-id="$ctrl.selectedSourceId"
			              selected-group-id="$ctrl.selectedGroupId"
			              on-select="$ctrl.onSourceGroupChange(sourceId, groupId)"
			>
			</inbox-select>
			<message-list class="message-list-pane h-min-100 overflow-y-auto"
			              selected-message-id="$ctrl.selectedMessageId"
			              messaging-backend="$ctrl.backend"
			              source-id="$ctrl.selectedSourceId"
			              group-id="$ctrl.selectedGroupId"
			              messageable-filter="$ctrl.messageableFilter"
			              message-stream-change="$ctrl.onMessageStreamChange(stream)"
			              component-style="$ctrl.componentStyle">
			</message-list>

			<ui-view class="flex-item-grow h-min-100"></ui-view>
		</div>
	</page-body>
</page-wrapper>