<juno-simple-modal component-style="$ctrl.resolve.style"
                   class="attachment-select"
                   modal-instance="$ctrl.modalInstance"
                   fill-color="JUNO_SIMPLE_MODAL_FILL_COLOR.TRANSPARENT"
                   modal-width="816"
                   modal-height="480">
	<div class="layout-grid h-100 w-100 m-t-4">
		<!-- Attachment source list -->
		<div class="source-select grid-item justify-content-center p-t-32 p-b-16">
			<file-source-select class="d-inline-block h-100 w-100"
			                    hide-chart-sources="!$ctrl.canReadChart"
			                    on-source-selected="$ctrl.switchFileSource(value)">
			</file-source-select>
		</div>

		<!-- file list header -->
		<div class="file-list-header grid-item flex-col">
			<div class="flex-row h-100">

				<div class="header-item flex-item-grow m-l-24">
					<span class="m-l-8"> Name </span>
				</div>

				<div class="header-item w-128 text-center">
					<span class="text-center w-100"> Date Modified </span>
				</div>

				<div class="header-item w-128">
					<span class="text-center w-100"> Type </span>
				</div>
			</div>
		</div>

		<!-- file select -->
		<div class="file-list grid-item">
			<file-select-list file-options="$ctrl.currentFileList"
			                  on-file-selected="$ctrl.addAttachment(value)">
			</file-select-list>
		</div>

		<!-- currently selected attachments display -->
		<div class="selected-attachments grid-item">
			<div class="overflow-y-auto h-100">
				<attachment-list attachments="$ctrl.selectedAttachments"
				                 show-remove-button="true"
				                 hide-header="true"
				                 component-style="$ctrl.resolve.style">
				</attachment-list>
			</div>
		</div>

		<!-- Buttons -->
		<div class="buttons grid-item flex-row justify-content-end align-items-center">

			<juno-button class="flex-item-no-grow w-128 m-r-4"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             component-style="$ctrl.resolve.style">
				Cancel
			</juno-button>

			<juno-button class="flex-item-no-grow w-128"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
			             component-style="$ctrl.resolve.style">
				Add to Message
			</juno-button>

		</div>
	</div>
</juno-simple-modal>