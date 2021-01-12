<div class="juno-file-chooser" ng-class="$ctrl.componentClasses()">
	<label ng-if="$ctrl.label" ng-class="$ctrl.labelClasses()">
		{{$ctrl.label}}
	</label>
	<juno-button component-style="$ctrl.componentStyle"
	             ng-click="$ctrl.onButtonClick()"
	             button-color="$ctrl.buttonColor"
	             button-color-pattern="$ctrl.buttonColorPattern"
	             button-color-override="$ctrl.buttonColorOverride"
	             disabled="$ctrl.disabled"
	>
		{{$ctrl.buttonLabel}}
	</juno-button>
	<input type="file" class="hidden-input" id="file-select"
	       accept="{{$ctrl.accept}}"
	       multiple="{{$ctrl.multiple}}"
	       onchange="angular.element(this).scope().onInputChange(this.files)">
</div>