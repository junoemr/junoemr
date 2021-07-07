<div class="inbox-header-bar flex-row align-items-center">
	<div class="inbox-title">
		<h6 class="m-l-32 m-r-8">Inbox</h6>
	</div>

	<!-- Only unread -->
	<juno-check-box ng-model="$ctrl.onlyUnread"
	                class="checkbox-no-drop-shadow unread-checkbox m-l-16 m-r-16"
	                title="only show unread messages"
	                label="Unread"
	                change="$ctrl.onUnreadFilterChange(value)"
	>
	</juno-check-box>

	<!-- check / un check all -->
	<juno-check-box ng-model="$ctrl.massSelectActive"
	                class="checkbox-no-drop-shadow m-l-16 m-r-16"
	                title="{{$ctrl.massSelectActive ? 'clear selection' : 'select all'}}"
	                change="$ctrl.selectUnselectAll()"
	                dummy="true">
	</juno-check-box>

	<!-- Inbox search -->
	<messageable-search class="search-input"
	                    ng-model="$ctrl.messageableFilter"
	                    placeholder="Search"
	                    messaging-service="$ctrl.messagingService"
	                    source-id="$ctrl.sourceId"
	                    on-text-change="$ctrl.updateKeywordFilter(value)"
	                    initial-text="$ctrl.searchKeyword"
	                    icon="icon-search"
	                    disabled="$ctrl.disableSearch"
	                    component-style="JUNO_STYLE.DEFAULT">
	</messageable-search>

	<div class="left-buttons flex-row m-l-32">
		<!-- Mark as Unread -->
		<juno-button class="header-button"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             click="$ctrl.updateSelectedMessageReadFlag(false)">
			<div class="flex-row align-items-center">
				<span>Mark as Unread</span>
				<i class="icon icon-dot"></i>
			</div>
		</juno-button>

		<!-- Mark as Read -->
		<juno-button class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             click="$ctrl.updateSelectedMessageReadFlag(true)">
			<div class="flex-row align-items-center">
				<span>Mark as Read</span>
				<i class="icon icon-dot"></i>
			</div>
		</juno-button>

		<!-- Archive -->
		<juno-button ng-if="$ctrl.groupId !== MessageGroup.Archived"
		             class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             click="$ctrl.archiveSelectedMessages(true)">
			<div class="flex-row align-items-center">
				<span>Archive</span>
				<i class="icon icon-delete"></i>
			</div>
		</juno-button>

		<!-- Unarchive -->
		<juno-button ng-if="$ctrl.groupId === MessageGroup.Archived"
		             class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             click="$ctrl.archiveSelectedMessages(false)">
			<div class="flex-row align-items-center">
				<span>Unarchive</span>
				<i class="icon icon-delete"></i>
			</div>
		</juno-button>

		<!-- Reply -->
		<juno-button class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             click="$ctrl.openComposeModal(true)"
		             disabled="!$ctrl.selectedMessageId || $ctrl.isLoading"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<span>Reply</span>
				<i class="icon icon-reply"></i>
			</div>
		</juno-button>

		<!-- Action in progress indicator -->
		<div ng-if="$ctrl.isLoading" class="loading-indicator m-t-4">
			<juno-loading-indicator indicator-type="spinner" title="Processing...">
			</juno-loading-indicator>
		</div>
	</div>

	<div class="right-buttons flex-item-grow flex-row justify-content-end">
		<!-- Compose -->
		<juno-button class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             click="$ctrl.openComposeModal()"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<span>Compose Message</span>
				<i class="icon icon-mail"></i>
			</div>
		</juno-button>
	</div>
</div>