<div class="attachment-list flex-col">

	<div class="text-grey">Attachments</div>

	<div class="flex-row flex-wrap" ng-class="$ctrl.attachmentListClasses()">
		<div ng-repeat="attachment in $ctrl.attachments"
		     class="attachment flex-row align-items-center p-4 m-r-8">
			<span class="flex-row align-items-center" ng-click="$ctrl.downloadAttachment(attachment)">
				<i class="download-icon icon icon-cloud-download m-r-8"></i>
				<span> {{attachment.name}} </span>
			</span>
			<juno-button ng-if="$ctrl.showRemoveButton"
			             click="$ctrl.removeAttachment(attachment)"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
			             component-style="$ctrl.componentStyle">
				<i class="icon icon-close"></i>
			</juno-button>
		</div>
	</div>
</div>