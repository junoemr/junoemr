<page-wrapper class="messaging-inbox">
	<page-header>
		<h6>Backend: {{$ctrl.backend}} Source: {{$ctrl.selectedSourceId}} Group: {{$ctrl.selectedGroupId}}</h6>
	</page-header>
	<page-body class="flex-item-grow flex-col messaging-inbox">
		<inbox-header-bar component-style="$ctrl.componentStyle"></inbox-header-bar>

		<div class="flex-item-grow flex-row overflow-hidden">
			<inbox-select class="w-256 w-min-256 h-min-100"
			              component-style="$ctrl.componentStyle"
			              groups="$ctrl.groups"
			              sources="$ctrl.messageSources"
			              selected-source-id="$ctrl.selectedSourceId"
			              selected-group-id="$ctrl.selectedGroupId"
			              on-select="$ctrl.onSourceGroupChange(sourceId, groupId)"
			>
			</inbox-select>
			<message-list class="message-list-pane h-min-100 overflow-y-auto"
			              component-style="$ctrl.componentStyle"
			              messaging-backend="$ctrl.backend"
			              source-id="$ctrl.selectedSourceId"
			              group-id="$ctrl.selectedGroupId"
			>
			</message-list>

			<ui-view class="flex-item-grow h-min-100"></ui-view>
		</div>
	</page-body>
</page-wrapper>