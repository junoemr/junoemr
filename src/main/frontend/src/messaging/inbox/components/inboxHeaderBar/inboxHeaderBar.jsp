<div class="inbox-header-bar flex-row align-items-center">
	<div class="inbox-title w-256">
		<h6 class="m-l-32 m-r-8">Inbox</h6>
	</div>

	<!-- Inbox search keyword -->
	<juno-input class="search-input"
	            ng-model="$ctrl.searchTerm"
	            icon="icon-search"
	            placeholder="Search"
	            component-style="JUNO_STYLE.DEFAULT">
	</juno-input>

	<div class="left-buttons flex-row m-l-32">
		<!-- Mark as Unread -->
		<juno-button class="header-button"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<span>Mark as Unread</span>
				<i class="icon icon-dot"></i>
			</div>
		</juno-button>

		<!-- Delete -->
		<juno-button class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<span>Delete</span>
				<i class="icon icon-delete"></i>
			</div>
		</juno-button>

		<!-- Reply -->
		<juno-button class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<span>Reply</span>
				<i class="icon icon-reply"></i>
			</div>
		</juno-button>
	</div>

	<div class="right-buttons flex-item-grow flex-row justify-content-end">
		<!-- Compose -->
		<juno-button class="header-button m-l-24"
		             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
		             component-style="$ctrl.componentStyle">
			<div class="flex-row align-items-center">
				<span>Create New Message</span>
				<i class="icon icon-mail"></i>
			</div>
		</juno-button>
	</div>
</div>