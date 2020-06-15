<div class="juno-button" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<button ng-class="$ctrl.buttonClasses()" class="btn">
		<ng-transclude>
		</ng-transclude>
	</button>
</div>
