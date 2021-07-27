<div class="message" ng-class="$ctrl.getComponentClasses()">

	<!-- Message -->
	<div class="flex-col">
		<!-- From, and message date -->
		<div class="from-container flex-row justify-content-space-between">
			<div>
				From:
				<a ng-if="$ctrl.demographicMapping.has($ctrl.message.sender.id)"
				   class="chart-link"
				   title="To chart"
				   ng-click="$ctrl.toDemographicSummary($ctrl.demographicMapping.get($ctrl.message.sender.id))">
					{{$ctrl.message.sender.name ? $ctrl.message.sender.name : "Account Deleted"}}
				</a>
				<span ng-if="!$ctrl.demographicMapping.has($ctrl.message.sender.id)" title="Cannot be matched to chart">
					{{$ctrl.message.sender.name ? $ctrl.message.sender.name : "Account Deleted"}}
				</span>
			</div>
			<div class="text-grey">{{$ctrl.formattedMessageDate()}}</div>
		</div>

		<!-- Recipients -->
		<div class="recipients-container m-t-32">
			<div class="text-grey m-b-8">Recipient:</div>
			<div>
				<span ng-repeat="recipient in $ctrl.message.recipients">
					<a ng-if="$ctrl.demographicMapping.has(recipient.id)"
					   class="chart-link"
					   title="To chart"
					   ng-click="$ctrl.toDemographicSummary($ctrl.demographicMapping.get(recipient.id))">
						{{recipient.name}}
					</a>
					<span ng-if="!$ctrl.demographicMapping.has(recipient.id)" title="Cannot be matched to chart">
						{{recipient.name}}
					</span>
				</span>
			</div>
		</div>

		<!-- Subject -->
		<div class="subject-container m-t-24">
			<div class="text-grey m-b-8">Subject:</div>
			<div>{{$ctrl.message.subject}}</div>
		</div>

		<!-- Message Body -->
		<div class="message-container m-t-24">
			<div class="text-grey m-b-8">Message</div>
			<p>{{$ctrl.message.message}}</p>
		</div>


		<!-- Loading indicator (Don't display attachments until loading complete) -->
		<div ng-if="$ctrl.loading && $ctrl.message.hasAttachments" class="flex-row flex-col justify-content-center">
			<juno-loading-indicator message-alignment="vertical"
			                        indicator-type="dot-pulse">
			</juno-loading-indicator>
		</div>
		<!-- Attachments -->
		<div ng-if="!$ctrl.loading && $ctrl.message.hasAttachments" class="m-t-24 flex-row">
			<attachment-list single-column="true"
			                 show-attach-to-chart="true"
			                 message="$ctrl.message"
			                 attachments="$ctrl.message.attachments">
			</attachment-list>
		</div>

	</div>
</div>