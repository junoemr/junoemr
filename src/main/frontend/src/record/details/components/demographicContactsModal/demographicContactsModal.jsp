<juno-modal class="juno-modal med-sml demographic-contacts-modal">

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

	<modal-body class="modal-body">

		<div class="height-100 overflow-auto ">
			<juno-input
					ng-model="$ctrl.contact.firstName"
					label="First Name"
					disabled="true"
					placeholder="First Name"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.lastName"
					label="Last Name"
					disabled="true"
					placeholder="Last Name"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.phone"
					label="Phone #"
					disabled="true"
					placeholder="Phone number"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.role"
					disabled="true"
					label="Contact Role"
					placeholder="Mother/Father/Guardian/Uncle"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contactType"
					label="Contact Type"
					disabled="true"
					placeholder="Contact Type"
					component-style="$ctrl.componentStyle">
			</juno-input>
		</div>
	</modal-body>


	<modal-footer class="modal-footer">
		<div class="buttons">
			<juno-button
					click="$ctrl.onCancel()">
				Close
			</juno-button>

			<juno-button
					click="$ctrl.edit()"
					disabled="!($ctrl.contact.type === 1)"
					title="{{$ctrl.title}}"
					button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
				Edit
			</juno-button>
		</div>
	</modal-footer>

</juno-modal>