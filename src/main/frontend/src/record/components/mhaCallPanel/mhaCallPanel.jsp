<div class="mha-call-panel" ng-class="$ctrl.componentClasses()">
	<juno-button class="close-button"
	             click="$ctrl.close()"
	             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
	             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT">
		<i class="icon icon-delete"></i>
	</juno-button>

	<!-- MHA audio call iframe -->
	<iframe ng-if="$ctrl.inSession"
	        src="{{$ctrl.iframeUrl}}"
	        allow="camera;microphone"
	        width="280"
	        height="60"
	        class="m-l-16 m-r-16"
	        frameborder="0">
	</iframe>

	<div ng-if="!$ctrl.inSession && $ctrl.integrationList.length > 0" class="integration-select flex-col p-16 m-t-24">
		<div>
			<p>Which clinic would you like to call the patient from?</p>
		</div>

		<juno-select ng-model="$ctrl.selectedIntegration"
		             options="$ctrl.integrationOptions"
		             label-position="LABEL_POSITION.TOP"
		             label="Select clinic">
		</juno-select>

		<juno-button class="m-t-24"
		             click="$ctrl.startCall()"
		             disabled="!$ctrl.selectedIntegration || $ctrl.calling"
		             button-color="JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL">
			Start Call
		</juno-button>
	</div>
</div>