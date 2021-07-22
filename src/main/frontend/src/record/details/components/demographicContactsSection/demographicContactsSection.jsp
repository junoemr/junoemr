<div class="demographic-details-section demographic-contacts-section">

	<h3 class="form-heading">
		<div class="title">
			<div>Personal Contacts</div>
			<div>
				<juno-button
						button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						class="button-width"
						button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
						click="$ctrl.manageContacts()">
					Manage
				</juno-button>
			</div>
		</div>
	</h3>

	<div class="contacts" ng-repeat="dc in $ctrl.demoContacts"
	     ng-model="dc">
		<div class="bold">{{dc.role}}</div>
		<div class="linkable">
			<div><a class="color" ng-click="$ctrl.openContacts(dc)">{{dc.lastName}}, {{dc.firstName}}</a></div>
			<div><a class="color" ng-if="dc.phone" ng-click="$ctrl.openContacts(dc)">Phone: {{dc.phone}}</a></div>
		</div>
	</div>

</div>


