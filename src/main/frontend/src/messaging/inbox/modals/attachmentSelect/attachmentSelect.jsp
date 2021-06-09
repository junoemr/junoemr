<juno-simple-modal component-style="$ctrl.resolve.style"
                   class="attachment-select"
                   modal-instance="$ctrl.modalInstance"
                   fill-color="JUNO_SIMPLE_MODAL_FILL_COLOR.TRANSPARENT"
                   modal-width="816"
                   modal-height="480">
	<div class="layout-grid h-100 w-100 m-t-4">
		<!-- Attachment source list -->
		<div class="source-select grid-item justify-content-center p-t-48 p-b-16">
			<file-source-select class="d-inline-block h-100 w-100"
			                    hide-chart-sources="!$ctrl.canReadChart"
			                    on-source-selected="$ctrl.switchFileSource(value)">
			</file-source-select>
		</div>

		<!-- Attach from computer -->
		<div class="attach-from-computer grid-item">
			<juno-button ng-click="$ctrl.attachFilesFromComputer()"
			             class="flex-item-no-grow">
				<div class="flex-row align-items-center justify-content-center">
					<span> From Computer </span>
				</div>
			</juno-button>
		</div>

		<!-- file list header -->
		<div class="file-list-header grid-item flex-col">
			<div class="flex-row h-100">

				<div class="header-item flex-item-grow m-l-24">
					<span class="m-l-8"> Name </span>
				</div>

				<div class="date-header header-item text-center">
					<span class="text-center w-100"> Date Modified </span>
				</div>

				<div class="type-header header-item">
					<span class="text-center w-100"> Type </span>
				</div>

				<div class="blank-header header-item">
				</div>
			</div>
		</div>

		<!-- file select -->
		<div class="file-list grid-item">
			<file-select-list file-options="$ctrl.currentFileList"
			                  selected-files="$ctrl.selectedAttachments">
			</file-select-list>
		</div>

		<!-- selected attachments header -->
		<div class="selected-attachments-header grid-item flex-row align-items-center">
			<span class="body-extra-small-bold">Attachments</span>
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

			<juno-button ng-click="$ctrl.closeModal(false)"
			             class="flex-item-no-grow w-128 m-r-4"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             component-style="$ctrl.resolve.style">
				Cancel
			</juno-button>

			<juno-button ng-click="$ctrl.closeModal(true)"
			             class="flex-item-no-grow w-128"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
			             component-style="$ctrl.resolve.style">
				Add to Message
			</juno-button>

		</div>
	</div>
</juno-simple-modal>