<div class="juno-button" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<button ng-class="$ctrl.buttonClasses()"
	        ng-style="$ctrl.buttonStyle();"
	        ng-disabled="$ctrl.disabled"
			ng-click="$ctrl.clickHandler($event)">
		<ng-transclude>
		</ng-transclude>
	</button>
</div>
