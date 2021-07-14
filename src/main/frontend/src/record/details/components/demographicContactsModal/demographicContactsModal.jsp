<juno-modal class="demographic-details-section demographic-contacts-modal"
            component-style="$ctrl.resolve.style">

	<modal-title>
		<h3>{{$ctrl.contactType}}</h3>
	</modal-title>

	<modal-ctl-buttons>
		<button type="button"
		        class="btn btn-icon"
		        aria-label="Close"
		        ng-click="$ctrl.onCancel()"
		        title="Close">
			<i class="icon icon-modal-ctl icon-close"></i>
		</button>
	</modal-ctl-buttons>

	<modal-body>
		<div class="fields">
			<div class="column">
				<div class="height-100 overflow-auto ">
					<juno-input
							ng-model="$ctrl.contact.role"
							label="Contact Role"
							readonly="true"
							placeholder="Mother/Father/Guardian/Uncle"
							component-style="$ctrl.componentStyle">
					</juno-input>

					<juno-input
							ng-model="$ctrl.contactType"
							label="Contact Type"
							readonly="true"
							placeholder="Contact Type"
							component-style="$ctrl.componentStyle">
					</juno-input>

				<juno-input
						ng-model="$ctrl.contact.firstName"
						label="First Name"
						readonly="true"
						placeholder="First Name"
						component-style="$ctrl.componentStyle">
				</juno-input>

				<juno-input
						ng-model="$ctrl.contact.lastName"
						label="Last Name"
						readonly="true"
						placeholder="Last Name"
						component-style="$ctrl.componentStyle">
				</juno-input>

				<juno-input
						ng-model="$ctrl.contact.phone"
						label="Phone"
						readonly="true"
						placeholder="Phone number"
						component-style="$ctrl.componentStyle">
				</juno-input>

			</div>
			</div>
	</modal-body>

	<modal-footer>

		<button class="btn" type="button" ng-click="$ctrl.onCancel()">Close</button>
		<button class="btn btn-success" type="button" ng-if="$ctrl.contact.type === 1" ng-click="$ctrl.edit()">Edit</button>

	</modal-footer>

</juno-modal>