<div class="integration-settings flex-col p-b-8">
	<h6 class="m-b-24">Settings</h6>

	<div class="flex-row align-items-center m-b-8">
		<juno-button click="$ctrl.testConnection()"
		             class="w-128 flex-item-no-grow"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
		             component-style="$ctrl.componentStyle">
			Test Connection
		</juno-button>
		<div ng-if="$ctrl.connectionOk != null"
		     ng-class="$ctrl.connectionCheckStringClasses()"
		     class="connection-status flex-row align-items-center m-l-16">
			{{$ctrl.connectionOk ? "Connection success" : "Connection failed"}}
			<i ng-if="$ctrl.connectionOk" class="icon icon-check m-l-8"></i>
			<i ng-if="!$ctrl.connectionOk" class="icon icon-delete m-l-8"></i>
		</div>
	</div>

	<juno-button click="$ctrl.toClinicAdmin()"
	             class="w-128 flex-item-no-grow"
	             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
	             component-style="$ctrl.componentStyle">
		<div class="flex-row align-items-center justify-content-center">
			<i class="icon icon-login m-r-4"></i>
			<span>Login</span>
		</div>
	</juno-button>
</div>
