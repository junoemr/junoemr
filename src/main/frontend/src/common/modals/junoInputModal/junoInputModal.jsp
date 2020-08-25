<div class="juno-input-modal height-100" ng-keydown="$ctrl.onKeyDown($event)" ng-class="$ctrl.getComponentClasses()">
	<h3 ng-class="$ctrl.resolve.style">{{$ctrl.resolve.title}}</h3>
	<juno-input id="modal-input"
					ng-model="$ctrl.value"
					component-style="$ctrl.resolve.style"
					label="{{$ctrl.resolve.message}}"
					label-position="LABEL_POSITION.TOP">
	</juno-input>
	<div class="buttons">
		<juno-button ng-click="$ctrl.onCancel()" component-style="$ctrl.resolve.style" button-color="JUNO_BUTTON_COLOR.BASE">
			Cancel
		</juno-button>
		<juno-button ng-click="$ctrl.onOk()" component-style="$ctrl.resolve.style">
			Ok
		</juno-button>
	</div>
</div>