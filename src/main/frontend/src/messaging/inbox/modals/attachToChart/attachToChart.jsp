<juno-simple-modal class="attach-to-chart-modal" modal-instance="$ctrl.modalInstance">
	<div class="flex-col h-100 w-100">

		<!-- Header -->
		<h6 class="m-t-0 text-ellipsis" title="Attach {{$ctrl.attachment.name}} to chart.">Attach {{$ctrl.attachment.name}} to chart.</h6>

		<div class="flex-item-grow flex-col">
			<!-- Document Description -->
			<juno-input ng-model="$ctrl.description"
			            label="Document Description"
			            label-position="LABEL_POSITION.TOP"
			            component-style="$ctrl.resolve.style">
			</juno-input>

			<!-- Document Type -->
			<juno-input ng-model="$ctrl.type"
			            class="m-t-8"
			            label="Document Type"
			            label-position="LABEL_POSITION.TOP"
			            component-style="$ctrl.resolve.style">
			</juno-input>
		</div>

		<div class="bottom-buttons flex-row justify-content-end flex-gap">

			<!-- Cancel -->
			<juno-button click="$ctrl.cancel()"
			             class="flex-item-no-grow w-128 m-r-8"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
			             component-style="$ctrl.componentStyle">
				Cancel
			</juno-button>

			<!-- Attach -->
			<juno-button click="$ctrl.cancel()"
			             class="flex-item-no-grow w-128"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
			             disabled="!$ctrl.canAttach()"
			             component-style="$ctrl.componentStyle">
				Attach
			</juno-button>
		</div>
	</div>
</juno-simple-modal>