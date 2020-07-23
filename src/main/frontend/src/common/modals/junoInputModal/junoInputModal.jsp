<juno-simple-modal class="juno-input-modal"
                   component-style="$ctrl.resolve.style"
                   modal-instance="$ctrl.modalInstance"
                   ng-keydown="$ctrl.onKeyDown($event)">
	
	<h5 class="title juno-text-primary">{{$ctrl.resolve.title}}</h5>
	
	<p class="juno-text">
		{{$ctrl.resolve.message}}
	</p>
	
	<juno-input id="modal-input"
					ng-model="$ctrl.value"
          invalid="!$ctrl.validations.value() && $ctrl.hasSubmitted"
          placeholder="Please enter reason here"
					component-style="$ctrl.resolve.style">
	</juno-input>
	
	<juno-divider component-style="$ctrl.resolve.style"
	              slim="true">
	</juno-divider>
	
	<div class="buttons">
		<juno-button ng-click="$ctrl.onCancel()"
		             component-style="$ctrl.resolve.style"
		             button-color="JUNO_BUTTON_COLOR.BASE">
			Cancel
		</juno-button>
		<juno-button ng-click="$ctrl.onOk()"
		             disabled="!allValidationsValid($ctrl.validations)"
		             component-style="$ctrl.resolve.style">
			{{$ctrl.resolve.okText ? $ctrl.resolve.okText : "Ok"}}
		</juno-button>
	</div>
	
</juno-simple-modal>