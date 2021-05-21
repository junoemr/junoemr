<div class="message">

	<!-- Conversation Header -->
	<div class="flex-col">
		<!-- From, and message date -->
		<div class="flex-row justify-content-space-between">
			<div>From: {{$ctrl.message.sender.name}} </div>
			<div class="text-grey">{{$ctrl.formattedMessageDate()}}</div>
		</div>

		<!-- Recipients -->
		<div class="m-t-32">
			<div class="text-grey m-b-8">Recipient</div>
			<div>{{$ctrl.recipientNames()}}</div>
		</div>

		<!-- Subject -->
		<div class="m-t-24">
			<div class="text-grey m-b-8">Subject</div>
			<div>{{$ctrl.message.subject}}</div>
		</div>

		<!-- Message Body -->
		<div class="m-t-24">
			<div class="text-grey m-b-8">Message</div>
			<p>{{$ctrl.message.message}}</p>
		</div>

		<!-- Attachments -->
		<div ng-if="$ctrl.message.hasAttachments" class="flex-col m-t-32">
			<div class="text-grey">Attachments</div>

			<div class="flex-row">
				<div class="flex-col">
					<div ng-repeat="attachment in $ctrl.message.attachments"
					     class="attachment flex-row align-items-center m-t-4 p-8"
					     ng-click="$ctrl.downloadAttachment(attachment)">
						<i class="download-icon icon icon-cloud-download m-r-8"></i>
						<span>{{attachment.name}}</span>
					</div>
				</div>
			</div>
		</div>

	</div>
</div>