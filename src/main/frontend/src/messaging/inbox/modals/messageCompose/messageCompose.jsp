<juno-simple-modal component-style="$ctrl.resolve.style"
                   modal-instance="$ctrl.modalInstance"
                   fill-color="JUNO_SIMPLE_MODAL_FILL_COLOR.GREY"
                   modal-width="1024"
                   modal-height="512">
	<div class="message-compose flex-col w-100 h-100 p-16 p-t-8">
		<!-- Recipient -->
		<messageable-search id="recipient-search-input" class="m-b-16"
		                    ng-model="$ctrl.recipient"
		                    messaging-service="$ctrl.messagingService"
		                    source-id="$ctrl.sourceId"
		                    component-style="$ctrl.resolve.style">
		</messageable-search>

		<!-- Subject -->
		<juno-input class="m-b-16"
		            ng-model="$ctrl.subject"
		            label-position="LABEL_POSITION.TOP"
		            label="Subject"
		            component-style="$ctrl.resolve.style">
		</juno-input>

		<!-- Message -->
		<label>Message</label>
		<div class="message-area flex-item-grow flex-col">
			<div class="flex-col h-100 overflow-y-auto p-4">
				<div class="message-body flex-item-grow" contenteditable="true"></div>
			</div>
		</div>

		<!-- Bottom Buttons -->
		<div class="flex-row m-t-16">
			<div class="flex-item-grow flex-row">
				<!-- Add Attachment -->
				<juno-button class="flex-item-no-grow" component-style="$ctrl.resolve.style">
					<div class="flex-row align-items-center p-l-8 p-r-8 h-100">
						Add Attachment
						<i class="icon-cloud-upload m-l-8 body-normal"></i>
					</div>
				</juno-button>
			</div>

			<!-- Cancel -->
			<juno-button class="flex-item-no-grow w-128" component-style="$ctrl.resolve.style" click="$ctrl.onCancel()">
				Cancel
			</juno-button>

			<!-- Send -->
			<juno-button class="flex-item-no-grow w-128 m-l-4"
			             button-color="JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
			             disabled="!$ctrl.recipient"
			             component-style="$ctrl.resolve.style">
				Send
			</juno-button>

		</div>
	</div>
</juno-simple-modal>