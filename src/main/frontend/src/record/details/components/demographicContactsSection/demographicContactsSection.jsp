<div class="demographic-details-section demographic-contacts-section">

	<div class="fields">
		<div class="column">
			<h4 class="title">
				Contacts
			</h4>
			<juno-button ng-repeat="dc in $ctrl.demoContacts"
			             ng-model="dc"
			             ng-click="$ctrl.openContacts(dc)"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_LIGHT"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.DEFAULT">
				{{dc.role}}: {{dc.lastName}}, {{dc.firstName}}
			</juno-button>
		</div>

		<div class="divider"></div>

		<div class="column">
			<h4 class="title">
				Professional Contacts
			</h4>
			<juno-button ng-repeat="dc in $ctrl.demoContactPros"
			             ng-model="dc.demoContactPros"
			             ng-click="$ctrl.openContacts(dc)"
			             button-color="JUNO_BUTTON_COLOR.GREYSCALE_LIGHT"
			             button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.DEFAULT">
				{{dc.role}}: {{dc.lastName}}, {{dc.firstName}}
			</juno-button>
		</div>

		<div class="manage">
			<juno-button
					button-color="JUNO_BUTTON_COLOR.PRIMARY"
					button-color-pattern="JUNO_BUTTON_COLOR_PATTERN.FILL"
					ng-click="$ctrl.manageContacts()">
				Manage Contacts
			</juno-button>
		</div>
	</div>
</div>


