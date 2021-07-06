<page-wrapper class="messaging-inbox" no-header="$ctrl.recordPageEmbedded" dont-set-height="$ctrl.recordPageEmbedded">
	<page-header class="flex-row align-items-center h-100">
		<h6>{{stringResources.getString("inbox.pageTitle")}}</h6>
	</page-header>
	<page-body class="flex-item-grow flex-col messaging-inbox" ng-class="{'record-page-embedded': $ctrl.recordPageEmbedded}">
		<inbox-header-bar ng-if="$ctrl.backend"
		                  component-style="$ctrl.componentStyle"
		                  selected-message-id="$ctrl.selectedMessageId"
		                  mass-edit-list="$ctrl.massEditList"
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
			              mass-edit-list="$ctrl.massEditList"
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