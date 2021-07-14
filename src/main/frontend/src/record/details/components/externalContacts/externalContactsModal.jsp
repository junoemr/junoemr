<juno-modal class="demographic-details-section external-contacts-modal"
            component-style="$ctrl.resolve.style"
            >
	<modal-title>
		<h3>{{$ctrl.demographic}} Contact</h3>
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
					ng-model="$ctrl.contactType"
					label="Internal/External"
					placeholder="Contact Type"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.role"
					label="Role"
		            placeholder="Role"
		            component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.firstName"
					label="First Name"
					placeholder="First Name"
					component-style="$ctrl.componentStyle">
			</juno-input>
		</div>
			<div class="divider"></div>

			<juno-input
					ng-model="$ctrl.contact.lastName"
					label="Last Name"
					placeholder="Last Name"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.phone"
					label="Phone"
					placeholder="Phone number"
					component-style="$ctrl.componentStyle">
			</juno-input>


		</div>
		</div>

	<div class="modal-footer">
		<button class="btn" type="button" ng-click="" >Cancel</button>
		<button class="btn btn-success" type="button" ng-click="" >Edit</button>

	</div>
	</modal-body>
</juno-modal>