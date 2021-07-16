<div class="inbox-header-bar flex-row align-items-center">
	<div class="inbox-title">
		<h6 class="m-l-32">Inbox</h6>
	</div>

	<juno-divider class="item-divider m-l-16 m-r-8" horizontal="true" slim="true"></juno-divider>

	<!-- check / un check all -->
	<juno-check-box ng-model="$ctrl.massSelectActive"
	                class="checkbox-no-drop-shadow m-r-8"
	                title="{{$ctrl.massSelectActive ? 'Clear selection' : 'Select all'}}"
	                change="$ctrl.selectUnselectAll()"
	                disabled="!$ctrl.messageStream"
	                dummy="true">
	</juno-check-box>

	<!-- Search -->
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

	<juno-divider class="item-divider m-l-8 m-r-8" horizontal="true" slim="true"></juno-divider>

	<div class="left-buttons flex-row align-items-center">

		<!-- Archive -->
		<juno-button ng-if="$ctrl.groupId !== MessageGroup.Archived"
		             class="header-button m-l-16"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             title="Archive selected message(s)"
		             click="$ctrl.archiveSelectedMessages(true)">
			<div class="flex-row align-items-center">
				<i class="icon icon-archive"></i>
			</div>
		</juno-button>

		<!-- UnArchive -->
		<juno-button ng-if="$ctrl.groupId === MessageGroup.Archived"
		             class="header-button m-l-16"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             title="Unarchive selected message(s)"
		             click="$ctrl.archiveSelectedMessages(false)">
			<div class="flex-row align-items-center">
				<i class="icon icon-archive"></i>
			</div>
		</juno-button>

		<!-- Only Unread -->
		<juno-button ng-if="$ctrl.groupId !== MessageGroup.Archived"
		             class="header-button m-l-8"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             title="{{$ctrl.onlyUnread ? 'Show all messages' : 'Show only unread messages'}}"
		             click="$ctrl.onUnreadFilterChange(!$ctrl.onlyUnread)">
			<div class="flex-row align-items-center">
				<i class="icon icon-nbox" ng-class="{'icon-blue': $ctrl.onlyUnread}"></i>
			</div>
		</juno-button>

		<!-- Mark as Read -->
		<juno-button class="header-button m-l-8"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             title="Mark as read"
		             click="$ctrl.updateSelectedMessageReadFlag(true)">
			<div class="flex-row align-items-center">
				<i class="icon icon-view"></i>
			</div>
		</juno-button>

		<!-- Mark as Unread -->
		<juno-button class="header-button m-l-8"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             component-style="$ctrl.componentStyle"
		             title="Mark as unread"
		             click="$ctrl.updateSelectedMessageReadFlag(false)">
			<div class="flex-row align-items-center">
				<i class="icon icon-view-off"></i>
			</div>
		</juno-button>

		<juno-divider class="item-divider" horizontal="true" slim="true"></juno-divider>


		<!-- Refresh -->
		<juno-button class="header-button"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             disabled="$ctrl.isLoading"
		             click="$ctrl.reloadMessageList()"
		             title="Refresh mailbox"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<i class="icon icon-refresh"></i>
			</div>
		</juno-button>

		<!-- Reply -->
		<juno-button class="header-button"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARK"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             click="$ctrl.openComposeModal(true)"
		             disabled="!$ctrl.selectedMessageId || $ctrl.isLoading"
		             title="Reply to message"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<i class="icon icon-reply"></i>
			</div>
		</juno-button>

		<!-- Action in progress indicator -->
		<div ng-if="$ctrl.isLoading" class="loading-indicator m-t-4">
			<juno-loading-indicator indicator-type="spinner" title="Processing...">
			</juno-loading-indicator>
		</div>
	</div>

	<div class="right-buttons flex-item-grow flex-row justify-content-end p-r-16 p-l-16">
		<!-- Compose -->
		<juno-button class="header-button compose-message-button w-128"
		             button-color="JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
		             click="$ctrl.openComposeModal()"
		             component-style="$ctrl.componentStyle">
			Compose
		</juno-button>
	</div>
</div>