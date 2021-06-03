<div class="message">

	<!-- Conversation Header -->
	<div class="flex-col">
		<!-- From, and message date -->
		<div class="flex-row justify-content-space-between">
			<div>From: {{$ctrl.message.sender.name ? $ctrl.message.sender.name : "Account Deleted"}} </div>
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
		<div class="m-t-24 flex-row">
			<attachment-list ng-if="$ctrl.message.hasAttachments"
			                 single-column="true"
			                 attachments="$ctrl.message.attachments">
			</attachment-list>
		</div>

	</div>
</div>