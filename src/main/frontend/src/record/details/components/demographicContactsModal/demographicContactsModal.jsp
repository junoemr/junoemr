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
							component-style="$ctrl.JUNO_STYLE.GREY">
					</juno-input>

					<juno-input
							ng-model="$ctrl.contactType"
							label="Contact Type"
							readonly="true"
							placeholder="Contact Type"
							component-style="$ctrl.JUNO_STYLE.GREY">
					</juno-input>

					<juno-input
							ng-model="$ctrl.contact.firstName"
							label="First Name"
							readonly="true"
							placeholder="First Name"
							component-style="$ctrl.JUNO_STYLE.GREY">
					</juno-input>

					<juno-input
							ng-model="$ctrl.contact.lastName"
							label="Last Name"
							readonly="true"
							placeholder="Last Name"
							component-style="$ctrl.JUNO_STYLE.GREY">
					</juno-input>

					<juno-input
							ng-model="$ctrl.contact.phone"
							label="Phone"
							readonly="true"
							placeholder="Phone number"
							component-style="$ctrl.JUNO_STYLE.GREY">
					</juno-input>

				</div>
			</div>
	</modal-body>

	<modal-footer>

		<juno-button
		             ng-click="$ctrl.onCancel()">
			Close
		</juno-button>

		<juno-button
		             ng-click="$ctrl.edit()"
		             disabled="!($ctrl.contact.type === 1)"
		             title="{{$ctrl.title}}"
		             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
			Edit
		</juno-button>

	</modal-footer>
</juno-modal>