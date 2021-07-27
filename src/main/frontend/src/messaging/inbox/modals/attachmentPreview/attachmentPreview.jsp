<juno-simple-modal class="attachment-preview-modal" modal-instance="$ctrl.modalInstance" modal-height="800" modal-width="1024">
	<div class="flex-col h-100">
		<div class="attachment-container flex-item-grow flex-col justify-content-center align-items-center m-8" ng-ref="$ctrl.attachmentContainer">
			<!-- embed here -->

			<div ng-if="!$ctrl.canPreviewFile()" class="flex-col justify-content-center w-100 h-100">
				<h3 class="text-center">Preview unavailable for this file type.</h3>
			</div>

			<pre ng-if="$ctrl.isFileTextType() && !$ctrl.isLoading" class="w-100 h-100" charset="utf-8">{{$ctrl.fileDataAsText()}}</pre>

			<!-- Loading indicator -->
			<div ng-if="$ctrl.isLoading" class="flex-item-grow flex-col justify-content-center">
				<juno-loading-indicator message-alignment="vertical"
				                        indicator-type="dot-pulse">
				</juno-loading-indicator>
			</div>
		</div>

		<div class="bottom-buttons flex-row justify-content-end">

			<!-- Download -->
			<juno-button click="$ctrl.download()"
			             class="flex-item-no-grow w-128 m-l-8 m-r-8"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
			             disabled="$ctrl.isLoading"
			             component-style="$ctrl.componentStyle">
				Download
			</juno-button>

			<!-- Close -->
			<juno-button click="$ctrl.close()"
			             class="flex-item-no-grow w-128 m-l-8 m-r-8"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
			             component-style="$ctrl.componentStyle">
				Close
			</juno-button>
		</div>
	</div>
</juno-simple-modal>