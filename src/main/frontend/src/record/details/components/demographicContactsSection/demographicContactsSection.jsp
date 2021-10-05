<div class="demographic-details-section demographic-contacts-section">

	<h3 class="form-heading">
		<div class="title">
			<div>Personal Contacts</div>
			<div class="manage-button-wrapper">
				<juno-button
						button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
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
		</div>
		<div class="phone" ng-if="dc.homePhone">Home Phone: {{dc.homePhone}}</div>
		<div class="phone" ng-if="dc.cellPhone">Cell Phone: {{dc.cellPhone}}</div>
		<div class="phone" ng-if="dc.workPhone">Work Phone: {{dc.workPhone}}</div>

	</div>

</div>


