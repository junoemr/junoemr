<div class="mha-patient-connection">
	<juno-button ng-click="$ctrl.onClick()"
	             title="{{$ctrl.getToolTip()}}"
	             label="Connection Status"
	             disabled="$ctrl.buttonDisabled()"
	             button-color="$ctrl.getButtonColor()"
	             button-color-pattern="$ctrl.getButtonColorPattern()"
	             component-style="$ctrl.componentStyle">
		<div class="button-content">
			<i ng-if="$ctrl.isConfirmed || $ctrl.hasEmail()" class="icon icon-mha text-left" ng-class="$ctrl.iconClasses()"></i>
			<div>{{ $ctrl.getButtonText() }}</div>
			<i ng-if="$ctrl.isConfirmed" class="icon icon-check text-right" ng-class="$ctrl.iconClasses()"></i>
			<i ng-if="!$ctrl.isConfirmed && $ctrl.hasEmail()" class="icon icon-send text-right" ng-class="$ctrl.iconClasses()"></i>
		</div>
	</juno-button>
</div>