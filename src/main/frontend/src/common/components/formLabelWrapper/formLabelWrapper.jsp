<div class="form-label-wrapper" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="form-input-wrapper">
		<ng-transclude></ng-transclude>
	</div>
</div>