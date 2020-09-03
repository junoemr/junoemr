<div class="juno-check-box" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<button class="btn"
					ng-class="$ctrl.buttonClasses()"
					ng-click="$ctrl.onClick()"
					title="{{$ctrl.title}}">
		<i class="icon icon-check" ng-style="$ctrl.hideIcon()"></i>
	</button>
</div>