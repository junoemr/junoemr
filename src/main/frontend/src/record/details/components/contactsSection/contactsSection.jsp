<div class="divider"></div>

<div class="demographic-details-section contacts-section">

	<h4 class="title">
		Contacts
	</h4>

	<div class="fields">
		<div class="column">

			<juno-button
						button-color="JUNO_BUTTON_COLOR.PRIMARY"
						ng-click="$ctrl.manageContacts()">
				Manage Contacts
			</juno-button>

			<juno-button ng-repeat="dc in $ctrl.ngModel"
			             ng-model="dc.page.demoContacts"
			             ng-click="$ctrl.openContacts(dc.contactId)"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_LIGHT"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT">
				{{dc.role}}: {{dc.lastName}}, {{dc.firstName}} Phone: {{dc.phone}}
			</juno-button>
		</div>

		<div class="divider"></div>

		<div class="column">
			<h4 class="title">
		Professional Contacts
			</h4>
			<juno-button ng-repeat="dc in $ctrl.ngModel"
			             ng-model="dc.page.demoContacts"
			             ng-click="$ctrl.openContacts(dc.contactId)"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_LIGHT"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.TRANSPARENT">
				{{dc.role}}: {{dc.lastName}}, {{dc.firstName}} Phone: {{dc.phone}}
			</juno-button>
		</div>
		</div>
</div>


