<div class="juno-select-save" ng-class="$ctrl.componentClasses()">

	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<div class="container">
		<div class="left">
			<juno-select ng-model="$ctrl.ngModel"
			             component-style="$ctrl.componentStyle"
			             options="$ctrl.options">
			</juno-select>
		</div>
		<div class="right" title="{{$ctrl.title}}">
			<juno-button button-color="$ctrl.buttonColor"
			             button-color-pattern="$ctrl.buttonColorPattern";
			             component-style="$ctrl.componentStyle"
			             click="$ctrl.onClick()">
				<i ng-class="$ctrl.icon"></i>
			</juno-button>
		</div>
	</div>
</div>