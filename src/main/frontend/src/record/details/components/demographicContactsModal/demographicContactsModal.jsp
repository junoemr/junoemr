<juno-modal class="juno-modal demographic-contacts-modal">

	<modal-title>
		<h3>{{$ctrl.contactType}}</h3>
	</modal-title>

	<modal-ctl-buttons>
		<juno-button button-color-pattern=$ctrl.JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT>
			<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
		</juno-button>
	</modal-ctl-buttons>

	<modal-body class="modal-body">

		<div class="body height-100 overflow-auto" >
			<juno-input
					ng-model="$ctrl.contact.firstName"
					label="First Name"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="First Name"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.lastName"
					label="Last Name"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="Last Name"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.middleName"
					label="Middle Name"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="Middle Name"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.role"
					disabled="!$ctrl.editable"
					label="Contact Role"
					label-position="$ctrl.LABEL_POSITION.TOP"
					placeholder="Mother/Father/Guardian/Uncle"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.address"
					label="Address"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="Address"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.city"
					label="City"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="City"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<div class="postal-prov">
				<juno-input
					ng-model="$ctrl.contact.postal"
					label="Postal Code"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="Postal Code"
					component-style="$ctrl.componentStyle">
				</juno-input>

				<juno-input
					ng-model="$ctrl.contact.province"
					label="Province"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="Province"
					component-style="$ctrl.componentStyle">
				</juno-input>
			</div>

			<div class="phone-with-ext">
				<juno-input
					ng-model="$ctrl.contact.homePhone"
					label="Home Phone"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="000-000-0000"
					component-style="$ctrl.componentStyle">
				</juno-input>
				<juno-input
					class="phone-ext"
					ng-model="$ctrl.contact.hphoneExt"
					label="Ext"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="00000"
					component-style="$ctrl.componentStyle">
				</juno-input>
			</div>

			<div class="phone-with-ext">
				<juno-input
						ng-model="$ctrl.contact.cellPhone"
						label="Cell Phone"
						label-position="$ctrl.LABEL_POSITION.TOP"
						disabled="!$ctrl.editable"
						placeholder="000-000-0000"
						component-style="$ctrl.componentStyle">
				</juno-input>
				<juno-input
						class="phone-ext"
						ng-model="$ctrl.contact.cphoneExt"
						label="Ext"
						label-position="$ctrl.LABEL_POSITION.TOP"
						disabled="!$ctrl.editable"
						placeholder="00000"
						component-style="$ctrl.componentStyle">
				</juno-input>
			</div>

			<div class="phone-with-ext">
				<juno-input
						ng-model="$ctrl.contact.workPhone"
						label="Work Phone"
						label-position="$ctrl.LABEL_POSITION.TOP"
						disabled="!$ctrl.editable"
						placeholder="000-000-0000"
						component-style="$ctrl.componentStyle">
				</juno-input>
				<juno-input
						class="phone-ext"
						ng-model="$ctrl.contact.wphoneExt"
						label="Ext"
						label-position="$ctrl.LABEL_POSITION.TOP"
						disabled="!$ctrl.editable"
						placeholder="00000"
						component-style="$ctrl.componentStyle">
				</juno-input>
			</div>

			<juno-input
					ng-model="$ctrl.contact.fax"
					label="Fax"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="000-000-0000"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<juno-input
					ng-model="$ctrl.contact.email"
					label="Email Address"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					placeholder="Email"
					component-style="$ctrl.componentStyle">
			</juno-input>

			<div class="notes">
				<juno-input
					ng-model="$ctrl.contact.note"
					label="Notes"
					label-position="$ctrl.LABEL_POSITION.TOP"
					disabled="!$ctrl.editable"
					component-style="$ctrl.componentStyle">
				</juno-input>
			</div>
		</div>
	</modal-body>

	<modal-footer class="modal-footer">
		<div class="buttons">

			<juno-button
					click="$ctrl.edit()"
					title="{{$ctrl.title}}"
					button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
				Edit
			</juno-button>
			<juno-button
					click="$ctrl.save()"
					ng-if="$ctrl.editable"
					disabled="!$ctrl.dataChanged"
					button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL">
				Save
			</juno-button>

			<juno-button
					click="$ctrl.onCancel()">
				Close
			</juno-button>
		</div>
	</modal-footer>

</juno-modal>