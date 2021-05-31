<juno-simple-modal component-style="$ctrl.resolve.style"
                   modal-instance="$ctrl.modalInstance"
                   fill-color="JUNO_SIMPLE_MODAL_FILL_COLOR.GREY"
                   modal-width="1024"
                   modal-height="512">
	<div class="message-compose flex-col w-100 h-100 p-16 p-t-8">
		<div class="flex-row m-b-16">
			<!-- Sender -->
			<juno-select ng-if="!$ctrl.isReply"
			             class="flex-item-grow m-r-2"
			             ng-model="$ctrl.sourceId"
			             options="$ctrl.messageSourceOptions"
			             label-position="LABEL_POSITION.TOP"
			             label="Sender"
			             on-change="$ctrl.onSourceChange(value)"
			             component-style="$ctrl.resolve.style">
			</juno-select>

			<!-- Recipient -->
			<messageable-search ng-if="!$ctrl.isReply"
			                    label="Recipient"
			                    class="flex-item-grow m-l-2"
			                    ng-model="$ctrl.recipient"
			                    messaging-service="$ctrl.messagingService"
			                    source-id="$ctrl.sourceId"
			                    disabled="!$ctrl.validations.sourceSelected()"
			                    component-style="$ctrl.resolve.style">
			</messageable-search>
			<!-- Participants -->
			<juno-input ng-if="$ctrl.isReply"
			            class="flex-item-grow"
			            ng-model="$ctrl.participantNames"
			            label-position="LABEL_POSITION.TOP"
			            label="Participants"
			            readonly="true"
			            component-style="$ctrl.resolve.style">
			</juno-input>
		</div>

		<!-- Subject -->
		<juno-input class="m-b-16"
		            ng-model="$ctrl.subject"
		            label-position="LABEL_POSITION.TOP"
		            label="Subject"
		            readonly="$ctrl.isReply"
		            component-style="$ctrl.resolve.style">
		</juno-input>

		<!-- Message -->
		<label>Message</label>
		<div class="message-area flex-item-grow flex-col">
			<div class="flex-col h-100 overflow-y-auto p-16">
				<!-- message textarea -->
				<div class="message-body" ng-ref="$ctrl.messageTextarea" ng-on-input="$ctrl.onMessageChange()" contenteditable="true"></div>

				<!-- message attachments -->
				<attachment-list ng-if="$ctrl.attachments.length > 0"
				                 attachments="$ctrl.attachments"
				                 show-remove-button="true"
				                 component-style="$ctrl.resolve.style">
				</attachment-list>

				<!-- previous messages (readonly) -->
				<div ng-if="$ctrl.isReply">
					<div ng-repeat="message in $ctrl.conversation.messages">
						<juno-divider component-style="$ctrl.resolve.style"></juno-divider>
						<message message="message" messaging-service="$ctrl.messagingService"></message>
					</div>
				</div>

			</div>
		</div>

		<!-- Bottom Buttons -->
		<div class="flex-row m-t-16">
			<div class="flex-item-grow flex-row">
				<!-- Add Attachment -->
				<juno-button class="flex-item-no-grow" click="$ctrl.uploadAttachment()" component-style="$ctrl.resolve.style">
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
			             disabled="!$ctrl.canSend()"
			             click="$ctrl.sendMessage()"
			             title="{{$ctrl.sendButtonTooltip()}}"
			             component-style="$ctrl.resolve.style">
				Send
			</juno-button>

		</div>
	</div>
</juno-simple-modal>