<div class="attachment-list flex-col">

	<div ng-if="!$ctrl.hideHeader" class="text-grey">Attachments</div>

	<div class="flex-row flex-wrap m-t-4" ng-class="$ctrl.attachmentListClasses()">
		<div ng-repeat="attachment in $ctrl.attachments"
		     class="flex-row align-items-center m-r-8">
			<!-- Attachment name -->
			<div class="attachment flex-row align-items-center p-4" title="Download">
				<span class="flex-row align-items-center" ng-click="$ctrl.downloadAttachment(attachment)">
					<i class="download-icon icon icon-cloud-download m-r-8"></i>
					<span> {{attachment.name}} </span>
				</span>
			</div>

			<!-- remove button -->
			<juno-button ng-if="$ctrl.showRemoveButton"
			             click="$ctrl.removeAttachment(attachment)"
			             class="remove-button flex-item-no-grow"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
			             title="Remove attachment"
			             component-style="$ctrl.componentStyle">
				<i class="icon icon-close"></i>
			</juno-button>

			<!-- Attach to chart button -->
			<juno-button ng-if="$ctrl.showAttachToChart"
			             click="$ctrl.attachToChart(attachment)"
			             class="attach-button flex-item-no-grow"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_DARKEST"
			             disabled="!$ctrl.canAttachToChart || $ctrl.isLoading"
			             title="{{$ctrl.canAttachToChart ? 'Attach to chart' : 'No suitable patient to attach the document to'}}"
			             component-style="$ctrl.componentStyle">
				<i class="icon icon-share"></i>
			</juno-button>
		</div>
	</div>
</div>