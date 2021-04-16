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
			<juno-button button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
			             component-style="$ctrl.componentStyle"
			             click="$ctrl.onClick()">
				<i class="icon-logout fa-lg"></i>
			</juno-button>
		</div>
	</div>
</div>